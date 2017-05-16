package psksvp

import au.edu.mq.comp.skink.ir.IRFunction
import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.Term
import au.edu.mq.comp.smtlib.theories.BoolTerm
import au.edu.mq.comp.smtlib.typedterms.TypedTerm
import au.edu.mq.comp.automat.auto.NFA
import au.edu.mq.comp.automat.edge.Implicits._
import au.edu.mq.comp.skink.ir.Trace

import scala.util.{Failure, Success}
import logics._
import psksvp.resources.using
import au.edu.mq.comp.smtlib.typedterms.Commands
import au.edu.mq.comp.smtlib.interpreters.Resources
//object resources extends Resources

/**
 * Created by psksvp on 29/07/2016.
 */
case class PredicateAbstraction(function:IRFunction,
                                choices:Seq[Int],
                                predicateList:Seq[TypedTerm[BoolTerm, Term]])
                                (implicit solver:SMTLIBInterpreter) extends Commands with Resources
{
  lazy private val trace = Trace(choices)
  lazy private val combinationSize = Math.pow(2, predicateList.length).toInt
  lazy private val transitionMap = psksvp.PredicateAbstraction.traceToTransitionMap(function, choices)

  def automaton:NFA[Int, Int] =
  {
    val tracePredicates = generatePredicates
    val lastLocPredicateIsFalse = equivalence(False(), tracePredicates.last)
    println("last loc eq to false? >>>" + (if(lastLocPredicateIsFalse) "yes" else "no"))

    val linear = PredicateAbstraction.automatonFromTrace(choices)
    if(lastLocPredicateIsFalse)
    {
      val newBackEdges = PredicateAbstraction.computeSafeBackEdges(function, choices, tracePredicates)
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
    * @return
    */
  def generatePredicates:Seq[BooleanTerm] =
  {
    /**
      *
      * @param previous
      * @param next
      * @return
      */
    def isFixedPoint(previous:Seq[BooleanTerm], next:Seq[BooleanTerm]): Boolean =
    {
      require(previous.length == next.length)
      val v = for(i <- previous.indices) yield equivalence(previous(i), next(i))
      v.reduce(_ && _)
    }

    ////////////-----------
    println("I am doing the trace" + choices)
    var locPredicates:Seq[BooleanTerm] = True() :: List.fill[BooleanTerm](choices.length - 1)(False())

    //fixed point
    var keepGoing = true
    while(keepGoing)
    {
      val locNextPredicates = generateLocationPredicates(locPredicates)

      println("current Predicates ===============" )
      locPredicates.foreach { t => println(termAsInfix(t))}
      println("next Predicates =================" )
      locNextPredicates.foreach {t => println(termAsInfix(t))}
      println("-----------------------------")

      if(isFixedPoint(locPredicates, locNextPredicates))
        keepGoing = false
      else
        locPredicates = locNextPredicates
    }

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
      //println("checking for true -> " + isEquivalence(True(), newTermOfThisLoc))
      if(equivalence(newTermOfThisLoc, currentPredicatesOfLoc(i)))
        nextLocPredicate(i) = newTermOfThisLoc
      else
        nextLocPredicate(i) = newTermOfThisLoc | currentPredicatesOfLoc(i)
      print(".")
    }
    println()
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
    import psksvp.PredicateAbstraction.gamma

   val absDomains = for (transition <- transitionMap(loc)) yield
                    {
                      abstractPostOfBlock(transition.source,
                                           transition.choice,
                                           withPrecondition = currentPredicates(transition.preconditionIndex))
                    }

    val terms = absDomains.map(gamma(_, predicateList, simplify = true)).reduce(_ | _)
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
    import psksvp.PredicateAbstraction._
    val postcondition = combinationToDisjunctTerm(combination, predicateList)
    push()
    val result = function.checkPost(precondition,
                                    trace,
                                    blockNumber,
                                    exitChoice,
                                    postcondition) match
                  {
                    case Success(v) => if (v) combination else -1
                    case Failure(e) => sys.error(s"PredicateAbstraction.abstractBlock($blockNumber):" +
                      s"Failure($e) from checkPost where the combination index is $combination")
                  }
    pop()
    result
  }
}


/**
  * ***************************************************************************
  * ***************************************************************************
  * ***************************************************************************
  */
object PredicateAbstraction
{
  // for testing purpose
  private var usePredicates:Seq[BooleanTerm] = Nil
  def setToUsePredicates(pl:Seq[BooleanTerm]):Unit=
  {
    usePredicates = pl
  }

  /**
    *
    * @param function
    * @param choices
    * @return
    */
  def repetitionsPairs(function:IRFunction, choices:Seq[Int]):Seq[(Int, Int)]=
  {
    val indexPartition: Seq[Seq[Int]] = function.traceToRepetitions(Trace(choices))
    indexPartition.filter(_.size > 1).map(_.toList).map(generatePairs(_)).flatten.flatten
  }

  /**
    *
    * @param source Index of source block
    * @param sink Index of sink block
    * @param choice choice that transition takes from source block
    */
  case class Transition(source:Int, sink:Int, choice:Int)
  {
    def preconditionIndex:Int=source //precondition index of this transition
    def locationIndex:Int=sink       //location index  where this transition contributes its post
  }

  /**
    *
    * @param function
    * @param choices
    * @return
    */
  def traceToTransitionMap(function:IRFunction, choices:Seq[Int]):Map[Int, List[Transition]]=
  {
    // start from 1 because, l0 is always true
    val linear = for(l <- 1 until choices.length) yield
                 {
                   l -> Transition(source = l - 1,
                                   sink = l,
                                   choice = choices(l - 1))  // linear
                 }

    val backEdge = for((i, j) <- repetitionsPairs(function, choices)) yield
                   {
                     // j and i  repeat so, backedge candidate is j to i + 1
                     val exitChoice = choices(i)  // take choice from i because it is a repeat from j and i + 1
                                                  // thus exit choice from j to i + 1 is the same as
                                                  // from i to i + 1
                     val transition = Transition(source = j,
                                                 sink = i + 1,
                                                 choice = exitChoice)

                     transition.locationIndex -> transition
                   }


    val x = linear.toList ::: backEdge.toList

    //        Map(0 -> Nil) is for l0, predicate at l0 is true
    val map = Map(0 -> Nil) ++ x.groupBy(_._1).map { case (k,v) => (k,v.map(_._2))}
    map
  }


  /**
    *
    * @param choices
    * @return
    */
  def automatonFromTrace(choices: Seq[Int]): NFA[Int, Int] =
  {
    val transitions = for (i <- choices.indices) yield (i ~> (i + 1))(choices(i))
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
                        iteration:Int): NFA[Int, Int] =
  {
    if(repetitionsPairs(function, choices).isEmpty)
    {
      println(choices)
      println("no Repetitions, rtn linear automaton")
      automatonFromTrace(choices)
    }
    else
    {
      using[NFA[Int, Int]](new SMTLIBInterpreter(solverFromName("Z3")))
      {
        implicit solver => Success(PredicateAbstraction(function, choices, usePredicates).automaton)
      }
      match
      {
        case Success(a) => a
        case _          => sys.error("hdsajdaskdjh")
      }

    }
  }

  /**
    * Provide a list of new edges that preserve infeasibility.
    *
    * @param   function     The function to analyse
    * @param   choices     An infeasible path of size n in `function` given by a sequence
    *                      of choices
    * @param   preds       Am inductive interpolants of size n - 2 (initial predicate is True and
    *                      final must be False)
    *
    * @note
    */
  def computeSafeBackEdges(function: IRFunction,
                           choices: Seq[Int],
                           preds: Seq[TypedTerm[BoolTerm, Term]]) =
  {
    println("------------------safeBackEdges")
    //println(s"Annotations: ${preds.map(_.termDef).map(showTerm(_))}")

    val completeItp = preds //True() +: preds :+ False()
    val candidatePairs = repetitionsPairs(function, choices)
    println(s"candidate pairs $candidatePairs")
    val newBackEdges =
      for (
        (i, j) <- candidatePairs;
        x1 = completeItp(j).unIndexed;
        x2 = completeItp(i + 1).unIndexed;
        res = using(new SMTLIBInterpreter(solverFromName("Z3")))
        {
          implicit solver =>  function.checkPost( x1,
                                                  Trace(choices),
                                                  index = j,
                                                  choice = choices(i),
                                                  x2)
        };
        uu = {
          println(s"Result of checkPost $res")
          res match {
            case Success(_) =>
            case Failure(_) => sys.error(s"Result of checkPost $res")
          }
        }
        if (res == Success(true))
      ) yield {
        println(s"new backedge found from $j to ${i + 1} with choice $i")
        (j ~> (i + 1))(choices(i))
      }

    println("----------------------")
    newBackEdges
  }


  /**
    * combine boolean vectors (disjunct clauses) to a conjunct
    *
    * @param absDomain
    * @return BooleanExpression
    */
  def gamma(absDomain: AbstractDomain,
            predicates:Seq[BooleanTerm],
            simplify: Boolean): BooleanTerm =
  {
    if (absDomain.size == math.pow(2, predicates.length).toInt)
      False()  // short cut   // this may be incorrect for CNF
    else if(absDomain.isEmpty)
      True()   // short cut  // this may be incorrect for CNF experiment.
    else
    {
      if(!simplify)
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


  def combinationToDisjunctTerm(combination:Int, predicates:Seq[BooleanTerm]):BooleanTerm=
  {
    val bin = binaryString(combination, predicates.length)
    val exprLs = for (i <- bin.indices) yield if (bin(i) == '1') predicates(i) else !predicates(i)
    exprLs.reduce(_ | _)
  }

  def combinationToConjunctTerm(combination:Int, predicates:Seq[BooleanTerm]):BooleanTerm=
  {
    val bin = binaryString(combination, predicates.length)
    val exprLs = for (i <- bin.indices) yield if (bin(i) == '1') predicates(i) else !predicates(i)
    exprLs.reduce(_ & _)
  }



  /**
    *
    * @param precondition
    * @param expression
    * @param predicates
    * @return
    */
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
  }
}


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






