package psksvp

import au.edu.mq.comp.skink.ir.IRFunction
import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.Term
import au.edu.mq.comp.smtlib.theories.BoolTerm
import au.edu.mq.comp.smtlib.typedterms.TypedTerm
import au.edu.mq.comp.smtlib.solvers.Z3
import au.edu.mq.comp.automat.auto.NFA
import au.edu.mq.comp.automat.edge.Implicits._
import au.edu.mq.comp.skink.ir.Trace

import scala.util.{Failure, Success, Try}
import logics._
import psksvp.resources.using
//object resources extends Resources

/**
 * Created by psksvp on 29/07/2016.
 */
case class PredicateAbstraction(function:IRFunction,
                                choices:Seq[Int],
                                predicateList:Seq[TypedTerm[BoolTerm, Term]])
{
  lazy private val trace = Trace(choices)
  lazy private val combinationSize = Math.pow(2, predicateList.length).toInt
  lazy private val transitionMap = psksvp.PredicateAbstraction.traceToTransitionMap(function, choices)

  def automaton:NFA[Int, Int] =
  {
    val tracePredicates = generatePredicates
    val lastLocPredicateIsFalse = isEquivalence(False(), tracePredicates.last)
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
      val v = for(i <- previous.indices) yield isEquivalence(previous(i), next(i))
      v.reduce(_ & _)
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
    * @param initPredicatesOfLoc
    * @return
    */
  def generateLocationPredicates(initPredicatesOfLoc:Seq[BooleanTerm]):Seq[BooleanTerm] =
  {
    val nextLocPredicate = Array.fill[BooleanTerm](initPredicatesOfLoc.length)(True())

    for(i <- 1 until initPredicatesOfLoc.length)
    {
      val newTermOfThisLoc = nextPredicatesOfLocation(i, initPredicatesOfLoc)
      //println("checking for true -> " + isEquivalence(True(), newTermOfThisLoc))
      if(isEquivalence(newTermOfThisLoc, initPredicatesOfLoc(i)))
        nextLocPredicate(i) = newTermOfThisLoc
      else
        nextLocPredicate(i) = newTermOfThisLoc | initPredicatesOfLoc(i)
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
    val absDomains = for(transition <- transitionMap(loc)) yield
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
  def abstractPostOfBlock(blockNumber: Int,
                          exitChoice:Int,
                          withPrecondition: BooleanTerm):AbstractDomain =
  {
    /// hard code to test something
//    println("******* HARD CODE ***********")
//    if(0 == blockNumber)
//      List[AbstractDomain]()
    ////////

    val combinations = List.range(0, combinationSize)
    val absDomain = combinations.map(checkCombination(_, blockNumber, exitChoice, withPrecondition))
//    val selectedVec = absDomain.map(_ >= 0)
//    (absDomain.filter( _ >= 0), selectedVec)
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
    import resources.using
    import psksvp.PredicateAbstraction._

    val postcondition = combinationToExpression(combination, predicateList)
    using(new Z3)
    {
      implicit solver => function.checkPost(precondition,
                                            trace,
                                            blockNumber,
                                            exitChoice,
                                            postcondition)
    }
    match
    {
      case Success(v) => if (v) combination else -1
      case Failure(e) => sys.error(s"PredicateAbstraction.abstractBlock($blockNumber):" +
                                   s"Failure($e) from checkPost where the combination index is $combination")
    }
  }

}


/**
  * ***************************************************************************
  * ***************************************************************************
  * ***************************************************************************
  */
object PredicateAbstraction
{
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
                        choices: Seq[Int]): NFA[Int, Int] =
  {
    if(repetitionsPairs(function, choices).isEmpty)
    {
      println(choices)
      println("no Repetitions, rtn linear automaton")
      automatonFromTrace(choices)
    }
    else
    {
      import au.edu.mq.comp.smtlib.theories.{Core, IntegerArithmetics}
      object logics extends IntegerArithmetics
      import logics._

      val i = Ints("%i")
      val iPromoted = Ints("%i.promoted")
      val zero = Ints("%0")
      val one = Ints("%1")
      val add = Ints("%add")
      val cmp = Bools("%cmp")
      val cmp1 = Bools("%cmp1")
      val three = Ints("%3")
      val iToBool = Bools("%tobool.i")
      val addlcssa = Ints("%add.lcssa")
      val conv = Ints("%conv")
      val condAddrI = Ints("%cond.addr.i")

      val predList2:List[BooleanTerm] = List(add > 0, add <= 10, add === 10)


      //-v -f LLVM /Users/psksvp/Workspace/test2.ll
//      val predList:List[BooleanTerm] = List(//zero >= 0, zero < 100,
//                                             add > 0,
//                                             add < 100, //loop guard,
//                                             add === 100) // assert cond
//                                             //cmp)//, cmp1)

      PredicateAbstraction(function, choices, predList2).automaton
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

    //  Collect partition of indices according to block equivalence
    val indexPartition: Seq[Seq[Int]] = function.traceToRepetitions(Trace(choices))
    println(s"Partitions $indexPartition")
    println(s"Non-singleton partitions ${indexPartition.filter(_.size > 1)}")

    //  Compute candidate backEdges from the indexPartition
    //  for each partition with more than 2 elements, build the candidate min, max
    val candidatePairs = indexPartition.filter(_.size > 1).map(_.toList).map(generatePairs(_)).flatten.flatten

    println(s"candidate pairs $candidatePairs")

    /**
      * Check if backedges can be added to the linear automaton
      * If there is a repetition of a block at index i and j, we
      * can try to add a backedge j -- choices(i) -> i + 1
      * For each set of repeated blocks, we try to add the first and closest
      * backedge. For instance if the a set is {1,4,7} (same block at index 1,4,7)
      * we try to add a backedge from 4 to 2 with choices(1)
      */

    val newBackEdges =
      for (
        (i, j) <- candidatePairs;
        x1 = completeItp(j).unIndexed;
        x2 = completeItp(i + 1).unIndexed;
        res = using(new Z3) {
          implicit solver =>  function.checkPost(
                                                  x1,
                                                  Trace(choices),
                                                  index = j,
                                                  choice = choices(i),
                                                  x2
                                                )
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
    * combine boolean vectors (disjunct clauses) to a conjunct clause
    *
    * @param absDomain
    * @return BooleanExpression
    */
  def gamma(absDomain: AbstractDomain,
            predicates:Seq[BooleanTerm],
            simplify: Boolean): BooleanTerm =
  {
    if (absDomain.size == math.pow(2, predicates.length).toInt)
      False()  // short cut
    else if(absDomain.isEmpty)
      True()   // short cut
    else
    {
      if (!simplify)
      {
        val exprLs = for (i <- absDomain.indices) yield combinationToExpression(absDomain(i), predicates)
        exprLs.reduce(_ & _)
      }
      else
        simplifyAbstractDomain(absDomain, predicates)
    }
  }

  /**
    *
    * @param absDomain
    * @return
    */
  def simplifyAbstractDomain(absDomain: AbstractDomain,
                             predicates:Seq[BooleanTerm]): BooleanTerm =
  {
    if (absDomain.size == math.pow(2, predicates.length).toInt)
      False()
    else if(absDomain.isEmpty)
      True()   // short cut
    else
    {
      val minTerms = booleanMinimize(absDomain.toList, predicates.toList)
      CNF(minTerms)
    }
  }

  def combinationToExpression(combination:Int, predicates:Seq[BooleanTerm]):BooleanTerm=
  {
    val binStr = binaryString(combination, predicates.length)
    binaryStringToExpression(binStr, predicates)
  }


  /**
    *
    * @param bin
    * @param predicates
    * @return
    */
  def binaryStringToExpression(bin:String, predicates:Seq[BooleanTerm]): BooleanTerm =
  {
    require(bin.length == predicates.length)
    val exprLs = for (i <- bin.indices) yield if (bin(i) == '1') predicates(i) else !predicates(i)
    exprLs.reduce(_ | _)
  }


  /**
    *
    * @param n
    * @param bits
    * @return
    */
  def binaryString(n:Int, bits:Int):String=
  {
    require(n >= 0, s"psksvp.binaryString($n, $bits) n (1st args) must be >= 0")
    require(n <= Integer.parseInt("1" * bits, 2), s"psksvp.binaryString($n, $bits) $bits bits is too small for $n ")
    val format = "%" + bits + "s"
    // make sure we have all the leading zeros
    String.format(format, Integer.toBinaryString(n)).replace(" ", "0")
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
    import au.edu.mq.comp.smtlib.solvers._


    val combinationSize = Math.pow(2, predicates.length).toInt  // can have overflow problem, very unlikely, only 32 bits number of predicates
    val domain = for(i <- 0 until combinationSize) yield        // .par possible?? don't know, function.checkPost may not thread safe?
                 {
                    val post = combinationToExpression(i, predicates)
                    using(new Z3)
                    {
                      implicit solver => psksvp.checkPost(precondition,
                                                           expression,
                                                           post)
                    }
                    match
                    {
                      case Success(v) => if(v) i else -1
                      case Failure(e) => sys.error(s"PredicateAbstraction.abstractExpression" +
                                                     s"Failure($e) from checkPost where the combination index is $i")
                    }
                 }
    println(s"valid combination is $domain")
    gamma(domain.filter(_ != -1), predicates,  simplify = false)
  }


  def checkForFalseCombination(predicateList:Seq[BooleanTerm]):Unit=
  {
    val combiExps = for(i <- 0 until math.pow(2, predicateList.length).toInt) yield
                    {
                      combinationToExpression(i, predicateList)
                    }

    for(exp <- combiExps)
    {
      println(isEquivalence(True(), exp)+" ---> " + termAsInfix(exp))
    }
  }




  /**
    *
    * @param args
    */
  def main(args:Array[String]):Unit=
  {
    val x0 = Ints("x0")
    val x1 = Ints("x1")
    val x2 = Ints("x2")
    val x3 = Ints("x3")

    val p1:List[BooleanTerm] = List(x0 === 0, x1 >= 1)
    val p2:List[BooleanTerm] = List(x0 === 0, x1 >= 1, x2 >= 1)
    val p3:List[BooleanTerm] = List(x0 === 0, x1 >= 1, x2 >= 1, x1 <= 0, x3 >= 1, x1 >= 2, x2 >= 3,
                                              x1 <= 5, x3 >= 6, x1 >= 7, x2 >= 7, x1 <= 20, x3 >= 1001)

    {
      val a1 = abstractPostOf(x1 === x0 + 1, precondition = True(), p1)
      val a2 = abstractPostOf(x1 === x0 + 1, precondition = True(), p2)
      println(termAsInfix(a1))
      println(termAsInfix(a2))
      println(isEquivalence(a1, a2))
    }

    {
      println("---------------------------------------------")
      val a1 = abstractPostOf(x1 === x0 + 1, precondition = x0 === 0, p1)
      val a2 = abstractPostOf(x1 === x0 + 1, precondition = x0 === 0, p2)
      println(termAsInfix(a1))
      println(termAsInfix(a2))
      println(isEquivalence(a1, a2))
    }

    {
      println("---------------------------------------------")
      val a1 = abstractPostOf(x1 === x0 + 1, precondition = x0 === 0, p1)
      val a2 = abstractPostOf(x1 === x0 + 1, precondition = x0 === 0, p2)
      println("going to abs with 13 predicates")
      val a3 = abstractPostOf(x1 === x0 + 1, precondition = x0 === 0, p3)
      println(termAsInfix(a1))
      println(termAsInfix(a2))
      println(termAsInfix(a3))
      println(isEquivalence(a1, a2))
      println(isEquivalence(a3, a2))
    }

    {
      println("---------------------------------------------")
      val a1 = abstractPostOf(x1 === x0 + 1, precondition = True(), p1)
      val a2 = abstractPostOf(x1 === x0 + 1, precondition = True(), p2)
      val a3 = abstractPostOf(x1 === x0 + 1, precondition = True(), p3)
      println(termAsInfix(a1))
      println(termAsInfix(a2))
      println(termAsInfix(a3))
      println(isEquivalence(a1, a2))
      println(isEquivalence(a3, a2))
    }

    {
      println("---------------------------------------------")
      val a1 = abstractPostOf(True(), precondition = True(), p1)
      val a2 = abstractPostOf(True(), precondition = True(), p2)
      val a3 = abstractPostOf(True(), precondition = True(), p3)
      println(termAsInfix(a1))
      println(termAsInfix(a2))
      println(termAsInfix(a3))
      println(isEquivalence(a1, a2))
      println(isEquivalence(a3, a2))
    }




//    val p0 = Bools("p0")
//    println(isEquivalence(p0 & !p0, False()))
//
//    val b = Bools("b")
//    val c = Bools("c")
//    val a = Bools("a")
//    val d = Bools("d")
//
//    println(isEquivalence(c | !(b & c),                                   True()))
//    println(isEquivalence(!(a & b) & (!a | b) & (!b | b),                 !a))
//    println(isEquivalence((a | c) & ( (a & d) | (a & !d)) | (a & c) | c,  a | c))
//    println(isEquivalence( !a & (a | b) | (b | (a & a)) & (a | !b),       a | b))
//
//    println(isEquivalence(False() | False(),                              False()))
//    println(isEquivalence(False() | True(),                               True() & True()))

  }
}









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






