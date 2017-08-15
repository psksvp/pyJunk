package psksvp

import au.edu.mq.comp.skink.ir.IRFunction
import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.{SSymbol, Term}
import au.edu.mq.comp.smtlib.theories.BoolTerm
import au.edu.mq.comp.smtlib.typedterms.TypedTerm
import au.edu.mq.comp.automat.auto.NFA
import au.edu.mq.comp.smtlib.configurations.SMTInit
import au.edu.mq.comp.smtlib.configurations.SMTOptions.INTERPOLANTS
import logics._
import au.edu.mq.comp.smtlib.typedterms.Commands
import au.edu.mq.comp.smtlib.interpreters.Resources
import psksvp.ADT.{AutoDispose, Disposable}
import psksvp.TraceAnalyzer.Transition

import scala.collection.parallel.immutable.ParVector


/**
 * Created by psksvp on 29/07/2016.
 */


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
  var genPredicates = false
  private var usePredicates: Seq[BooleanTerm] = Nil
  def setToUsePredicates(pl: Seq[BooleanTerm]): Unit = {usePredicates = pl}

 //val solverPool = new ADT.WorkerPool(Array.fill[Solver](10)(new Solver(solverFromName("Z3"))))
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
    import au.edu.mq.comp.skink.ir.llvm.LLVMFunction
    val traceAnalyzer = TraceAnalyzer(function, choices)
    val functionInformation = FunctionInformation(function.asInstanceOf[LLVMFunction])


    if (traceAnalyzer.repetitionsPairs.isEmpty)
    {
      println(choices)
      println("no Repetitions, rtn linear automaton")
      val la = traceAnalyzer.linearAutomaton
      println("linear auto created and about to return")
      la
    }
    else
    {
      if(Nil == usePredicates || genPredicates)
      {
        //val solver = solverPool.getWorker()
        println("generating predicates for abstraction")
        val solver = new SMTLIBInterpreter(solverFromName("Z3"))//, new SMTInit(List(INTERPOLANTS)))
        val ph = new EQEPredicatesHarvester(traceAnalyzer, functionInformation, solver)
        //val ph = new InterpolantBasedHarvester(traceAnalyzer, functionInformation, solver)
        usePredicates = ph.inferredPredicates.toIndexedSeq//ph.inferredWithFilters(/*BreakOrTerms :: ReduceToEqualTerms ::*/ Nil).toSeq
        //solverPool.releaseWorker(solver)
        solver.destroy()
        genPredicates = true
      }

      println(s"running with input predicates: ${usePredicates.length}")
      println(termAsInfix(usePredicates))
      val p = PredicatesAbstraction(traceAnalyzer, usePredicates, new CNFComposer)
      val result = p.automaton
      p.dispose()
      //solverPool.shutdown()
      result
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
        val exprLs =for (i <- absDomain.indices) yield combinationToDisjunctTerm(absDomain(i), predicates)
        exprLs.par.reduce(_ & _)
      }
      else
      {
        BooleanMinimizeCNF(absDomain.toList, predicates.toList)
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
        BooleanMinimizeDNF(absDomain.toList, predicates.toList)
      }
    }
  }

//  def alpha(pre:BooleanTerm, effect:BooleanTerm, inputPredicates:Seq[BooleanTerm]):AbstractDomain =
//  {
//    def checkCombination(c:Int):Boolean =
//    {
//      val solver = solverPool.getWorker()
//      val term = combinationToDisjunctTerm(c, inputPredicates)
//      val r = checkPost(pre, effect, term)(solver)
//      solverPool.releaseWorker(solver)
//      r
//    }
//
//    val combinationSize = Math.pow(2, usePredicates.length).toInt
//    val absDomain = for(c <- ParVector.range(0, combinationSize) if checkCombination(c)) yield c
//    absDomain.toIndexedSeq
//  }
//
//  def computeAbstractPost(pre:BooleanTerm,
//                          effect:BooleanTerm,
//                          inputPredicates:Seq[BooleanTerm]):BooleanTerm =
//  {
//    val absDom = alpha(pre, effect, inputPredicates)
//    gammaCNF(absDom, inputPredicates, simplify = true)
//  }


  /**
    *
    * @param combination
    * @param predicates
    * @return
    */
  def combinationToDisjunctTerm(combination: Int, predicates: Seq[BooleanTerm]): BooleanTerm =
  {
    val bin = binaryString(combination, predicates.length)
    val exprLs = for (i <- bin.indices) yield if (bin(i) == '1') predicates(i) else !predicates(i)
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
    val exprLs = for (i <- bin.indices) yield if (bin(i) == '1') predicates(i) else !predicates(i)
    exprLs.reduce(_ & _)
  }


  /**
    *
    */
  trait TermComposer
  {
    def combinationToTerm(combination: Int, predicates: Seq[BooleanTerm]): BooleanTerm

    def gamma(absDomain: AbstractDomain,
              predicates: Seq[BooleanTerm],
              simplify: Boolean): BooleanTerm
  }

  /**
    *
    */
  class DNFComposer extends TermComposer
  {
    def combinationToTerm(combination: Int,
                          predicates: Seq[BooleanTerm]): BooleanTerm = combinationToConjunctTerm(combination, predicates)

    def gamma(absDomain: AbstractDomain,
              predicates: Seq[BooleanTerm],
              simplify: Boolean): BooleanTerm = gammaDNF(absDomain, predicates, simplify)
  }

  /**
    *
    */
  class CNFComposer extends TermComposer
  {
    def combinationToTerm(combination: Int,
                          predicates: Seq[BooleanTerm]): BooleanTerm = combinationToDisjunctTerm(combination, predicates)

    def gamma(absDomain: AbstractDomain,
              predicates: Seq[BooleanTerm],
              simplify: Boolean): BooleanTerm = gammaCNF(absDomain, predicates, simplify)
  }
} // object PredicateAbstraction

/**
  *
  * @param traceAnalyzer
  * @param inputPredicates
  * @param termComposer
  */
case class PredicatesAbstraction(traceAnalyzer: TraceAnalyzer,
                                  inputPredicates:Seq[TypedTerm[BoolTerm, Term]],
                                  termComposer:PredicatesAbstraction.TermComposer) extends Commands
                                                                                      with Resources
                                                                                      with Disposable
{
  import scala.collection.parallel.immutable.ParVector
  val solverArray = Array.fill[SMTLIBInterpreter](traceAnalyzer.length)(new SMTLIBInterpreter(solverFromName("Z3")))

  ///////////////////////////////////////////////
  lazy val tracePredicates:Seq[BooleanTerm] =
  {
    def equalTest(current:Seq[BooleanTerm], next:Seq[BooleanTerm]): Boolean =
    {
      require(0 != current.length && 0 != next.length, "equalTest2 cannot check zero length")
      require(current.length == next.length, "equalTest2 current.length != next.Length")

      val r = ParVector.range(0, current.length).map
              {
                i => equivalence(current(i), next(i))(solverArray(i))
              }
      r.reduceLeft(_ && _)
    }

    ///////////////////////////////
    import psksvp.ADT.FixedPoint
    println("I am doing the trace:" + traceAnalyzer.choices)
    val result = FixedPoint(equalTest,
                            computePredicates).run(True() :: List.fill(traceAnalyzer.length - 1)(False()))

    println("\nFixed point reached with Predicates ===============" )
    result.foreach { t => println(termAsInfix(t))}
    println("------------")
    val (hits, miss) = BooleanMinimizeCNF.cacheStatistic
    println(s"simplify cache hit is $hits and mis is $miss")
    result
  }

  ///////////
  lazy val automaton:NFA[Int, Int] =
  {
    val lastLocPredicateIsFalse = equivalence(False(), tracePredicates.last)(solverArray(0))
    println("last loc eq to false? >>>" + (if(lastLocPredicateIsFalse) "yes" else "no"))

    if(lastLocPredicateIsFalse)
      traceAnalyzer.automatonWithBackEdges(tracePredicates)(solverArray(0))
    else
      traceAnalyzer.linearAutomaton
  }


  def dispose():Unit=
  {
    for(solver <- solverArray)
      solver.destroy()
  }

  /**
    *
    * @param currentPredicates
    * @return Seq of BooleanTerm of assertion at each location on the trace.
    */
  def computePredicates(currentPredicates:Seq[BooleanTerm]):Seq[BooleanTerm] =
  {
    /**
      *
      * @param t
      * @return
      */
    def predicatesForTransition(t:Transition):Seq[BooleanTerm] =
    {
      /**
        *
        * @param predicate
        * @return
        */
      def inEffectOrPre(predicate:BooleanTerm):Boolean =
      {
        val indexedPredicate = predicate.indexedBy{ case SSymbol(x) => t.effect.lastIndexMap.getOrElse(x, 0)}
        val insideEffect = (indexedPredicate.typeDefs intersect t.effect.term.typeDefs).nonEmpty

        //pre has no index, so we don't need to index predicate term
        val insidePre = (predicate.typeDefs intersect currentPredicates(t.preconditionIndex).typeDefs).nonEmpty
        //if it is in effect Or pre term, we need to include the predicate for abstraction.
        insideEffect || insidePre
      }

      ///////////////////////////////////////////////////////////////
      val lsp = for(p <- inputPredicates if inEffectOrPre(p)) yield p
      if(lsp.nonEmpty) lsp else inputPredicates
    }

    /**
      * computes POSTs at location loc.
      * @param loc is location
      * @return union of posts at location loc
      */
    def nextPredicateAtLocation(loc:Int):BooleanTerm =
    {
      ////////////////////////////////////////////
      /**
        * check to see if combination c is included in the post of the effect of
        * transition transition.
        * @param c
        * @param transition
        * @param usePredicates : list of predicates to be used for computing abstraction
        * @return true if combination c is included, false otherwise.
        */
      def checkCombination(c:Int,
                           transition:TraceAnalyzer.Transition,
                           usePredicates:Seq[BooleanTerm]):Boolean =
      {
        val pre = currentPredicates(transition.preconditionIndex)
        val indexedPre = pre.indexedBy { case _ => 0}

        val post = termComposer.combinationToTerm(c, usePredicates)
        val indexedPost = post.indexedBy { case SSymbol(x) => transition.effect.lastIndexMap.getOrElse(x, 0)}
        psksvp.checkPost(indexedPre, transition.effect.term, indexedPost)(solverArray(loc))
      }

      /////////////////////////////////////////////
      /////////////////////////////////////////////
      val absPosts = for(t <- traceAnalyzer.transitionMap(loc)) yield
                     {
                       val usePredicates = predicatesForTransition(t) // use only predicates which lits are in the eff or pre
                       //println(s"usePredicates:${usePredicates.length} <-> inputPredicates:${inputPredicates.length}")

                       val combinationSize:Int = Math.pow(2, usePredicates.length).toInt
                       val absDom = for(c <- List.range(0, combinationSize)
                                        if checkCombination(c, t, usePredicates)) yield c

                       termComposer.gamma(absDom, usePredicates, simplify = true)
                     }
      // in each locations, there can be more than one Transitions that need to be abstracted.
      // one is from the direct edge from previous location.
      // another may be from incomming edges from repeat locations (back edges).
      // absPost of these transitions at the same location are combined (union) together.
      absPosts.reduce(_ | _) // union all post at this location (loc)
    }

    /////////////////////////////////////
    /// compute abstraction at each location
    /// NOTE: we start from loc 1, because loc 0 is always True.
    val rls = ParVector.range(1, currentPredicates.length).map
              {
                loc => (loc, nextPredicateAtLocation(loc))
              }

    //updating the each location with new abstracted post if changes from last run
    val newPredicates = for((loc, term) <- rls) yield
                        {
                          if (equivalence(term, currentPredicates(loc))(solverArray(loc)))
                            currentPredicates(loc)
                          else
                            term | currentPredicates(loc)
                        }
    // the first loc is always true.
    True() +: newPredicates.toIndexedSeq
  }
}




