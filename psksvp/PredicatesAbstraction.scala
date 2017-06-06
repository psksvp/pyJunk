package psksvp

import au.edu.mq.comp.skink.ir.IRFunction
import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.{SSymbol, Term}
import au.edu.mq.comp.smtlib.theories.BoolTerm
import au.edu.mq.comp.smtlib.typedterms.TypedTerm
import au.edu.mq.comp.automat.auto.NFA

import scala.util.Success
import logics._
import psksvp.resources.using
import au.edu.mq.comp.smtlib.typedterms.Commands
import au.edu.mq.comp.smtlib.interpreters.Resources
import psksvp.PredicatesAbstraction.timeUsedCheckComb

import scala.annotation.tailrec

/**
 * Created by psksvp on 29/07/2016.
 */
case class PredicatesAbstraction(traceAnalyzer: TraceAnalyzer,
                                 inputPredicates:Seq[TypedTerm[BoolTerm, Term]],
                                 termComposer:PredicatesAbstraction.TermComposer)
                                 (implicit solver:SMTLIBInterpreter) extends Commands with Resources
{
  lazy val combinationSize:Int = Math.pow(2, inputPredicates.length).toInt

  lazy val tracePredicates:Seq[BooleanTerm] =
  {
    @tailrec
    def equalTest(current:Seq[BooleanTerm], next:Seq[BooleanTerm]): Boolean =
    {
      require(0 != current.length && 0 != next.length, "equalTest cannot check zero length")
      require(current.length == next.length, "equalTest current.length != next.Length")

      if(1 == current.length)  // last one
        equivalence(current.head, next.head)
      else if(!equivalence(current.head, next.head))
        false
      else
        true && equalTest(current.tail, next.tail)
    }

    ////////////-----------
    import psksvp.ADT.FixedPoint
    println("I am doing the trace" + traceAnalyzer.choices)
    val result = FixedPoint(equalTest,
                            computePredicates).run(True() :: List.fill(traceAnalyzer.length - 1)(False()))

    println("\nFixed point reached with Predicates ===============" )
    result.foreach { t => println(termAsInfix(t))}
    println("------------")
    result
  }

  ///////////
  lazy val automaton:NFA[Int, Int] =
  {
    val lastLocPredicateIsFalse = equivalence(False(), tracePredicates.last)
    println("last loc eq to false? >>>" + (if(lastLocPredicateIsFalse) "yes" else "no"))

    if(lastLocPredicateIsFalse)
      traceAnalyzer.automatonWithBackEdges(tracePredicates)
    else
      traceAnalyzer.linearAutomaton
  }

  /**
    *
    * @param currentPredicates
    * @return
    */
  def computePredicates(currentPredicates:Seq[BooleanTerm]):Seq[BooleanTerm] =
  {
    val nextPredicate = Array.fill[BooleanTerm](currentPredicates.length)(True())

    for(i <- 1 until currentPredicates.length)
    {
      val newTermOfThisLoc = nextPredicateAtLocation(i, currentPredicates)
      if(equivalence(newTermOfThisLoc, currentPredicates(i)))
        nextPredicate(i) = newTermOfThisLoc
      else
        nextPredicate(i) = newTermOfThisLoc | currentPredicates(i)
    }
    nextPredicate
  }


  /**
    *
    * @param loc
    * @param currentPredicates
    * @return
    */
  def nextPredicateAtLocation(loc:Int, currentPredicates:Seq[BooleanTerm]):BooleanTerm =
  {
    ////////////////////////////////////////////
    def checkCombination(c:Int, transition:TraceAnalyzer.Transition):Int =
    {
      val pre = currentPredicates(transition.preconditionIndex)
      val indexedPre = pre indexedBy { case _ => 0}

      val post = termComposer.combinationToTerm(c, inputPredicates)
      val indexedPost = post indexedBy
                        {
                          case SSymbol(x) => transition.effect.lastIndexMap.getOrElse(x, 0)
                        }

      if(psksvp.checkPost(indexedPre, transition.effect.term, indexedPost))
        c
      else
        -1
    }

//    def predicatesFromTransition(t:TraceAnalyzer.Transition):Seq[BooleanTerm]=
//    {
//      def appear(p:BooleanTerm, inTerm:BooleanTerm):Boolean = (p.typeDefs intersect inTerm.unIndexed.typeDefs).nonEmpty
//
//      val pre = currentPredicates(t.preconditionIndex)
//      val eff = t.effect.term
//      val r = for(p <- inputPredicates if appear(p, pre & eff)) yield p
//      r
//    }

    /////////////////////////////////////////////

     val absDomains = for(t <- traceAnalyzer.transitionMap(loc)) yield
                      {
                        val combinations = List.range(0, combinationSize)
                        val start = System.currentTimeMillis()
                        val absDom = combinations.map(checkCombination(_, t))
                        timeUsedCheckComb = timeUsedCheckComb + (System.currentTimeMillis() - start)
                        absDom.filter( _ >= 0)
                      }

    absDomains.map(termComposer.gamma(_, inputPredicates, simplify = true)).reduce(_ | _)
  }
}


/**
  * ***************************************************************************
  * ***************************************************************************
  * ***************************************************************************
  */
object PredicatesAbstraction
{
  // for testing purpose
  var timeUsedWhole: Long = 0
  var timeUsedCheckComb: Long = 0

  private var usePredicates: Seq[BooleanTerm] = Nil
  def setToUsePredicates(pl: Seq[BooleanTerm]): Unit = {usePredicates = pl}
  // -----------

  /**
    *
    * @param function
    * @param choices
    * @return
    */
  def apply(function: IRFunction,
            choices: Seq[Int],
            iteration: Int): NFA[Int, Int] =
  {
    val traceAnalyzer = TraceAnalyzer(function, choices)

//    println("blockVariables")
//    println(traceAnalyzer.blockVariables)
//    println("-------------\ncommon variables\n")
//    println(traceAnalyzer.commonVariables)
//    println("-------------\ninstruction terms\n")
//    println(traceAnalyzer.blockInstructionTerms)

    if (traceAnalyzer.repetitionsPairs.isEmpty)
    {
      println(choices)
      println("no Repetitions, rtn linear automaton")
      traceAnalyzer.linearAutomaton
    }
    else
    {
      println("running with predicates: ")
      usePredicates.foreach{p => print(termAsInfix(p) + ",")}
      println()
      val start = System.currentTimeMillis()
      val result = using[NFA[Int, Int]](new SMTLIBInterpreter(solverFromName("Z3")))
      {
        implicit solver => Success(PredicatesAbstraction(traceAnalyzer,
                                                         usePredicates,
                                                         new CNFComposer).automaton)
      }
      timeUsedWhole = timeUsedWhole + (System.currentTimeMillis() - start)

      result match
      {
        case Success(a) => a
        case _          => sys.error("solver error at PredicateAbstraction.apply")
      }
    }
  }


  /**
    * combine boolean vectors (disjunct clauses) to a conjunct
    *
    * @param absDomain
    * @return BooleanExpression
    */
  def gammaCNF(absDomain: AbstractDomain,
               predicates: Seq[BooleanTerm],
               simplify: Boolean): BooleanTerm =
  {
    if (absDomain.size == math.pow(2, predicates.length).toInt)
      False() // short cut for CNF
    else if (absDomain.isEmpty)
      True() // short cut  for CNF
    else
    {
      if (!simplify)
      {
        val exprLs = for (i <- absDomain.indices) yield combinationToDisjunctTerm(absDomain(i), predicates)
        exprLs.reduce(_ & _)
      }
      else
      {
        val minTerms = booleanMinimize(absDomain.toList, predicates.toList)
        val terms = toCNF(minTerms)
        terms
      }
    }
  }

  /**
    * combine boolean vectors (conjunct clauses) to a disjunct term
    *
    * @param absDomain
    * @return BooleanExpression
    */
  def gammaDNF(absDomain: AbstractDomain,
               predicates: Seq[BooleanTerm],
               simplify: Boolean): BooleanTerm =
  {
    if (absDomain.size == math.pow(2, predicates.length).toInt)
      True() // short cut for DNF
    else if (absDomain.isEmpty)
      False() // short cut  for DNF
    else
    {
      if (!simplify)
      {
        val exprLs = for (i <- absDomain.indices) yield combinationToConjunctTerm(absDomain(i), predicates)
        exprLs.reduce(_ | _) // DNF
      }
      else
      {
        val minTerms = booleanMinimize(absDomain.toList, predicates.toList)
        val terms = toDNF(minTerms)
        terms
      }
    }
  }


  /**
    *
    * @param combination
    * @param predicates
    * @return
    */
  def combinationToDisjunctTerm(combination: Int, predicates: Seq[BooleanTerm]): BooleanTerm =
  {
    val bin = binaryString(combination, predicates.length)
    val exprLs = for (i <- bin.indices) yield if (bin(i) == '1') predicates(i)
    else !predicates(i)
    exprLs.reduce(_ | _)
  }

  /**
    *
    * @param combination
    * @param predicates
    * @return
    */
  def combinationToConjunctTerm(combination: Int, predicates: Seq[BooleanTerm]): BooleanTerm =
  {
    val bin = binaryString(combination, predicates.length)
    val exprLs = for (i <- bin.indices) yield if (bin(i) == '1') predicates(i)
    else !predicates(i)
    exprLs.reduce(_ & _)
  }


  trait TermComposer
  {
    def combinationToTerm(combination: Int, predicates: Seq[BooleanTerm]): BooleanTerm

    def gamma(absDomain: AbstractDomain,
              predicates: Seq[BooleanTerm],
              simplify: Boolean): BooleanTerm
  }

  class DNFComposer extends TermComposer
  {
    def combinationToTerm(combination: Int,
                          predicates: Seq[BooleanTerm]): BooleanTerm = combinationToConjunctTerm(combination, predicates)

    def gamma(absDomain: AbstractDomain,
              predicates: Seq[BooleanTerm],
              simplify: Boolean): BooleanTerm = gammaDNF(absDomain, predicates, simplify)
  }

  class CNFComposer extends TermComposer
  {
    def combinationToTerm(combination: Int,
                          predicates: Seq[BooleanTerm]): BooleanTerm = combinationToDisjunctTerm(combination, predicates)

    def gamma(absDomain: AbstractDomain,
              predicates: Seq[BooleanTerm],
              simplify: Boolean): BooleanTerm = gammaCNF(absDomain, predicates, simplify)
  }
} // object PredicateAbstraction





