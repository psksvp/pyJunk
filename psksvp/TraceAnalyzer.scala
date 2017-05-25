package psksvp

import au.edu.mq.comp.automat.auto.NFA
import au.edu.mq.comp.automat.edge.Implicits._
import au.edu.mq.comp.automat.edge.LabDiEdge
import au.edu.mq.comp.skink.ir.{IRFunction, Trace}
import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter

import scala.util.Success
import au.edu.mq.comp.smtlib.typedterms.Commands
/**
  * Created by psksvp on 23/5/17.
  */
case class TraceAnalyzer(function:IRFunction, choices:Seq[Int]) extends Commands
{
  //////////////////////////////////////////////////
  lazy val repetitionsPairs:Seq[(Int, Int)] =
  {
    val indexPartition: Seq[Seq[Int]] = function.traceToRepetitions(Trace(choices))
    indexPartition.filter(_.size > 1).map(_.toList).map(generatePairs(_)).flatten.flatten
  }

  //////////////////////////////////////////////////
  case class Transition(source: Int, sink: Int, choice: Int)
  {
    def preconditionIndex: Int = source //precondition index of this transition
    def locationIndex: Int = sink //location index  where this transition contributes its post
  }

  //////////////////////////////////////////////////
  lazy val transitionMap:Map[Int, List[Transition]] =
  {
    // start from 1 because, l0 is always true
    val linear = for (l <- 1 until choices.length) yield
                 {
                   l -> Transition(source = l - 1,
                                    sink = l,
                                    choice = choices(l - 1)) // linear
                 }

    val backEdge = for ((i, j) <- repetitionsPairs) yield
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

  //////////////////////////////////////////////////
  lazy val linearAutomaton: NFA[Int, Int] =
  {
    val transitions = for (i <- choices.indices) yield (i ~> (i + 1)) (choices(i))
    NFA(Set(0), transitions.toSet, Set(choices.length), Set(choices.length))
  }


  def automatonWithBackEdges(tracePredicates:Seq[BooleanTerm])
                            (implicit solver:SMTLIBInterpreter):NFA[Int, Int] =
  {
    lazy val backEdges:Seq[LabDiEdge[Int, Int]] =
    {
      println("------------------safeBackEdges")
      println(s"candidate pairs $repetitionsPairs")
      val newBackEdges = for((i, j) <- repetitionsPairs;
                             x1 = tracePredicates(j).unIndexed;
                             x2 = tracePredicates(i + 1).unIndexed
                             if checkPost( x1, j, choices(i), x2)) yield
                             {
                               println(s"new backedge found from $j to ${i + 1} with choice $i")
                               (j ~> (i + 1))(choices(i))
                             }

      println("----------------------")
      newBackEdges
    }

    if (backEdges.isEmpty)
      linearAutomaton
    else
      NFA(linearAutomaton.getInit,
             linearAutomaton.transitions ++ backEdges,
             linearAutomaton.accepting,
             linearAutomaton.accepting)

  }

  //////////////////////////////////////////////////
  def checkPost(precondition:BooleanTerm,
                blockIndex:Int,
                exitChoice:Int,
                postcondition:BooleanTerm)(implicit solver:SMTLIBInterpreter):Boolean=
  {
    push()
    val r =  function.checkPost(precondition, Trace(choices), blockIndex, exitChoice, postcondition)
    pop()
    r match
    {
      case Success(b) => b
      case _          => sys.error("at PredicateAbstraction.checkPost solver fail at PredicateABstraction.checkPost")
    }
  }
}
