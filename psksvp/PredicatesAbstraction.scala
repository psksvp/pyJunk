package psksvp

import au.edu.mq.comp.skink.ir.IRFunction
import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.Term
import au.edu.mq.comp.smtlib.theories.BoolTerm
import au.edu.mq.comp.smtlib.typedterms.TypedTerm
import au.edu.mq.comp.automat.auto.NFA
import au.edu.mq.comp.automat.edge.Implicits._
import au.edu.mq.comp.skink.ir.Trace

import scala.util.Success
import logics._
import psksvp.resources.using
import au.edu.mq.comp.smtlib.typedterms.Commands
import au.edu.mq.comp.smtlib.interpreters.Resources
import psksvp.PredicatesAbstraction.repetitionsPairs
import au.edu.mq.comp.automat.edge.LabDiEdge

import scala.annotation.tailrec

/**
 * Created by psksvp on 29/07/2016.
 */
case class PredicatesAbstraction(function:IRFunction,
                                choices:Seq[Int],
                                predicateList:Seq[TypedTerm[BoolTerm, Term]],
                                termComposer:PredicatesAbstraction.TermComposer)
                                (implicit solver:SMTLIBInterpreter) extends Commands with Resources
{
  lazy private val trace = Trace(choices)
  lazy private val combinationSize = Math.pow(2, predicateList.length).toInt
  lazy private val transitionMap = psksvp.PredicatesAbstraction.traceToTransitionMap(function, choices)

  def automaton:NFA[Int, Int] =
  {
    val tracePredicates = generatePredicates
    val lastLocPredicateIsFalse = equivalence(False(), tracePredicates.last)
    println("last loc eq to false? >>>" + (if(lastLocPredicateIsFalse) "yes" else "no"))

    val linear = PredicatesAbstraction.automatonFromTrace(choices)
    if(lastLocPredicateIsFalse)
    {
      val newBackEdges = backEdges(tracePredicates)
      if (newBackEdges.isEmpty)
        linear
      else
        NFA(linear.getInit, linear.transitions ++ newBackEdges, linear.accepting, linear.accepting)
    }
    else
      linear
  }

  /**
    *
    * @param tracePredicates
    * @return
    */
  def backEdges(tracePredicates: Seq[BooleanTerm]):Seq[LabDiEdge[Int, Int]] =
  {
    println("------------------safeBackEdges")

    val completeItp = tracePredicates //True() +: preds :+ False()
    val candidatePairs = repetitionsPairs(function, choices)
    println(s"candidate pairs $candidatePairs")
    val newBackEdges = for((i, j) <- candidatePairs;
                           x1 = completeItp(j).unIndexed;
                           x2 = completeItp(i + 1).unIndexed
                           if checkPost( x1, j, choices(i), x2)) yield
                           {
                             println(s"new backedge found from $j to ${i + 1} with choice $i")
                             (j ~> (i + 1))(choices(i))
                           }

    println("----------------------")
    newBackEdges
  }

  /**
    *
    * @return
    */
  def generatePredicates:Seq[BooleanTerm] =
  {
//    def fixedPoint(previous:Seq[BooleanTerm], next:Seq[BooleanTerm]): Boolean =
//    {
//      require(previous.length == next.length)
//      val v = for(i <- previous.indices) yield equivalence(previous(i), next(i))
//      v.reduce(_ && _)
//    }

    @tailrec
    def fixedPoint(current:Seq[BooleanTerm], next:Seq[BooleanTerm]): Boolean =
    {
      require(current.length == next.length, "isFixedPoint current.length != next.Length")
      if(1 == current.length)  // last one
        equivalence(current.head, next.head)
      else if(!equivalence(current.head, next.head))
        false
      else
        true && fixedPoint(current.tail, next.tail)
    }

    ////////////-----------
    println("I am doing the trace" + choices)
    var locPredicates:Seq[BooleanTerm] = True() :: List.fill[BooleanTerm](choices.length - 1)(False())

    //fixed point
    var keepGoing = true
    while(keepGoing)
    {
      val locNextPredicates = generateLocationPredicates(locPredicates)

//      println("current Predicates ===============" )
//      locPredicates.foreach { t => println(termAsInfix(t))}
//      println("next Predicates =================" )
//      locNextPredicates.foreach {t => println(termAsInfix(t))}
//      println("-----------------------------")

      if(fixedPoint(locPredicates, locNextPredicates))
        keepGoing = false
      else
        locPredicates = locNextPredicates
    }

    println("\nFixed point reached with Predicates ===============" )
    locPredicates.foreach { t => println(termAsInfix(t))}
    println("------------")
    locPredicates
  }

  /**
    *
    * @param currentPredicatesOfLoc
    * @return
    */
  def generateLocationPredicates(currentPredicatesOfLoc:Seq[BooleanTerm]):Seq[BooleanTerm] =
  {
    val nextLocPredicate = Array.fill[BooleanTerm](currentPredicatesOfLoc.length)(True())

    for(i <- 1 until currentPredicatesOfLoc.length)
    {
      val newTermOfThisLoc = nextPredicatesOfLocation(i, currentPredicatesOfLoc)
      if(equivalence(newTermOfThisLoc, currentPredicatesOfLoc(i)))
        nextLocPredicate(i) = newTermOfThisLoc
      else
        nextLocPredicate(i) = newTermOfThisLoc | currentPredicatesOfLoc(i)
    }
    nextLocPredicate
  }


  /**
    *
    * @param loc
    * @param currentPredicates
    * @return
    */
  def nextPredicatesOfLocation(loc:Int, currentPredicates:Seq[BooleanTerm]):BooleanTerm =
  {
    val absDomains = for (transition <- transitionMap(loc)) yield
                     {
                       abstractPostOfBlock(transition.source,
                                           transition.choice,
                                           withPrecondition = currentPredicates(transition.preconditionIndex))
                     }

    //val terms = absDomains.map(gamma(_, predicateList, simplify = true)).reduce(_ | _)
    val terms = absDomains.map(termComposer.gamma(_, predicateList, simplify = true)).reduce(_ | _)
    terms
  }

  /**
    *
    * @param blockNumber
    * @param withPrecondition
    * @return
    */
  def abstractPostOfBlock(blockNumber:Int,
                          exitChoice:Int,
                          withPrecondition: BooleanTerm):AbstractDomain =
  {
    val combinations = List.range(0, combinationSize)
    val absDomain = combinations.map(checkCombination(_, blockNumber, exitChoice, withPrecondition))
    absDomain.filter( _ >= 0)
  }

  /**
    *
    * @param combination
    * @param blockNumber
    * @param precondition
    * @return
    */
  def checkCombination(combination:Int,
                       blockNumber:Int,
                       exitChoice:Int,
                       precondition:BooleanTerm):Int=
  {
    val start = System.currentTimeMillis()
    import psksvp.PredicatesAbstraction._
    //val postcondition = combinationToDisjunctTerm(combination, predicateList)
    val postcondition = termComposer.combinationToTerm(combination, predicateList)
    timeUsedCheckComb = timeUsedCheckComb + (System.currentTimeMillis() - start)
    if(checkPost(precondition, blockNumber, exitChoice, postcondition))
      combination
    else
      -1
  }

  /**
    *
    * @param precondition
    * @param blockIndex
    * @param exitChoice
    * @param postcondition
    * @return
    */
  def checkPost(precondition:BooleanTerm, blockIndex:Int, exitChoice:Int, postcondition:BooleanTerm):Boolean=
  {
    push()
    val r =  function.checkPost(precondition, trace, blockIndex, exitChoice, postcondition)
    pop()
    r match
    {
      case Success(b) => b
      case _          => sys.error("at PredicateAbstraction.checkPost solver fail at PredicateABstraction.checkPost")
    }
  }
}


/**
  * ***************************************************************************
  * ***************************************************************************
  * ***************************************************************************
  */
object PredicatesAbstraction
{
  var timeUsedWhole: Long = 0
  var timeUsedCheckComb: Long = 0
  // for testing purpose
  private var usePredicates: Seq[BooleanTerm] = Nil
  def setToUsePredicates(pl: Seq[BooleanTerm]): Unit = {usePredicates = pl}
  // -----------

  /**
    *
    * @param function
    * @param choices
    * @return
    */
  def repetitionsPairs(function: IRFunction, choices: Seq[Int]): Seq[(Int, Int)] =
  {
    val indexPartition: Seq[Seq[Int]] = function.traceToRepetitions(Trace(choices))
    indexPartition.filter(_.size > 1).map(_.toList).map(generatePairs(_)).flatten.flatten
  }

  /**
    *
    * @param source Index of source block
    * @param sink   Index of sink block
    * @param choice choice that transition takes from source block
    */
  case class Transition(source: Int, sink: Int, choice: Int)
  {
    def preconditionIndex: Int = source //precondition index of this transition
    def locationIndex: Int = sink //location index  where this transition contributes its post
  }

  /**
    *
    * @param function
    * @param choices
    * @return
    */
  def traceToTransitionMap(function: IRFunction, choices: Seq[Int]): Map[Int, List[Transition]] =
  {
    // start from 1 because, l0 is always true
    val linear = for (l <- 1 until choices.length) yield
                 {
                   l -> Transition(source = l - 1,
                                   sink = l,
                                   choice = choices(l - 1)) // linear
                 }

    val backEdge = for ((i, j) <- repetitionsPairs(function, choices)) yield
                   {
                     // j and i  repeat so, backedge candidate is j to i + 1
                     val exitChoice = choices(i) // take choice from i because it is a repeat from j and i + 1
                     // thus exit choice from j to i + 1 is the same as
                     // from i to i + 1
                     val transition = Transition(source = j,
                                                 sink = i + 1,
                                                 choice = exitChoice)

                     transition.locationIndex -> transition
                   }


    val x = linear.toList ::: backEdge.toList

    //        Map(0 -> Nil) is for l0, predicate at l0 is true
    val map = Map(0 -> Nil) ++ x.groupBy(_._1).map{ case (k, v) => (k, v.map(_._2)) }
    map
  }


  /**
    *
    * @param choices
    * @return
    */
  def automatonFromTrace(choices: Seq[Int]): NFA[Int, Int] =
  {
    val transitions = for (i <- choices.indices) yield (i ~> (i + 1)) (choices(i))
    NFA(Set(0), transitions.toSet, Set(choices.length), Set(choices.length))
  }


  /**
    *
    * @param function
    * @param choices
    * @return
    */
  def generateAutomaton(function: IRFunction,
                        choices: Seq[Int],
                        iteration: Int): NFA[Int, Int] =
  {
    if (repetitionsPairs(function, choices).isEmpty)
    {
      println(choices)
      println("no Repetitions, rtn linear automaton")
      automatonFromTrace(choices)
    }
    else
    {
      val start = System.currentTimeMillis()
      val result = using[NFA[Int, Int]](new SMTLIBInterpreter(solverFromName("Z3")))
      {
        implicit solver => Success(PredicatesAbstraction(function,
                                                        choices,
                                                        usePredicates,
                                                        new CNFComposer).automaton)
      }
      timeUsedWhole = timeUsedWhole + (System.currentTimeMillis() - start)

      result match
      {
        case Success(a) => a
        case _          => sys.error("solver error at PredicateAbstraction.generateAutomaton")
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
  /*
  def abstractPostOf(expression:BooleanTerm,
                     precondition:BooleanTerm,
                     predicates:List[BooleanTerm]):BooleanTerm =
  {
    val combinationSize = Math.pow(2, predicates.length).toInt  // can have overflow problem, very unlikely, only 32 bits number of predicates
    val domain = for(i <- 0 until combinationSize) yield        // .par possible?? don't know, function.checkPost may not thread safe?
    {
      val post = combinationToDisjunctTerm(i, predicates)
      psksvp.checkPost(precondition, expression, post) match
      {
        case Success(v) => if(v) i else -1
        case Failure(e) => sys.error(s"PredicateAbstraction.abstractExpression" +
                                     s"Failure($e) from checkPost where the combination index is $i")
      }
    }
    println(s"valid combination is $domain")
    gamma(domain.filter(_ != -1), predicates,  simplify = true)
  }*/



/*



  def checkForFalseCombination(predicateList:Seq[BooleanTerm]):Unit=
  {
    val combiExps = for(i <- 0 until math.pow(2, predicateList.length).toInt) yield
                    {
                      combinationToExpression(i, predicateList)
                    }

    for(exp <- combiExps)
    {
      println(equivalence(True(), exp)+" ---> " + termAsInfix(exp))
    }
  }
 */






///**
//  *
//  * @param post
//  * @param withPrecondition
//  * @param blockNumber
//  * @return
//  */
//def validatePost(post:BooleanTerm, withPrecondition:BooleanTerm, blockNumber:Int):Boolean=
//{
//  import resources.using
//  using(new Z3)
//{
//  implicit solver => function.checkPost(withPrecondition,
//  trace,
//  blockNumber,
//  choices(blockNumber),
//  post)
//}
//  match
//{
//  case Success(v) => if (v) true else false
//  case Failure(e) => sys.error("PredicateAbstraction.validatePost failure occurs");
//}
//}
//
//  /**
//    *
//    * @param tracePredicates
//    * @return
//    */
//  def validateTracePredicates(tracePredicates:Seq[BooleanTerm]):Boolean =
//{
//  require(choices.length + 1 == tracePredicates.length, "choices.length + 1 == tracePredicates.length  is false")
//  val e = for(i <- 0 until choices.length - 1) yield validatePost(post = tracePredicates(i + 1),
//  withPrecondition = tracePredicates(i),
//  blockNumber = i)
//
//  println("validateTracePredicates ")
//  println(e)
//  e.reduce(_ & _)
//}






