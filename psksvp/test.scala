package psksvp

import psksvp.PredicateAbstraction.abstractPostOf
import logics._


object runVerify
{
  def apply(filename:String,
            predicates:Seq[BooleanTerm],
            useO2:Boolean,
            useClang:String = "clang-4.0"):Unit=
  {
    import au.edu.mq.comp.skink.Main
    PredicateAbstraction.setToUsePredicates(predicates)
    if(useO2)
    {
      Main.main(Array("-v",
                       "--use-predicate-abstraction",
                       "--use-clang", useClang,
                       filename))
    }
    else
    {
      Main.main(Array("-v",
                       "--use-predicate-abstraction",
                       "--use-clang", useClang,
                       "--no-O2",
                       filename))
    }
  }
}


/**
  * Created by psksvp on 9/5/17.
  */
object test
{
  def main(args:Array[String]):Unit=
  {
    testForLoopSum()
  }

  def testForLoopSum(): Unit =
  {
    val i = Ints("%i")
    val a = Ints("%a")

    runVerify("/Users/psksvp/MyCode/skink/testing.psksvp/test_for_loop_sum.c",
               List(i >= 1, i <= 1000, i === 1001),//, a >= 0, a <= 1000, a === i - 1),
               useO2 = false,
               useClang = "clang-3.7")
  }

  def writeToTemp(code:String):String=
  {
    val tmpDir = System.getProperty("java.io.tmpdir")
  }
}


/*
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
      val a = Ints("%a")

      val n = Ints("%n")
      val x = Ints("%x")
      val y = Ints("%y")
      val dec = Ints("%dec")
      val j = Ints("%j")

      //val predList2:List[BooleanTerm] = List(add > 0, add <= 10, add === 10)
      //val predList2:List[BooleanTerm] = List( a === 0, i >= 0, i < 2000, i === 2000)
      //val predList2:List[BooleanTerm] = List(n === 2, y === 2, y >= 0, x <= y, x > 0)
      //val predList2:List[BooleanTerm] = List(x === 2, one === x, x === 0, one < 2, one > 0, one === 0)

      val predList2:List[BooleanTerm] = List(dec <= 2, dec > 0, dec === 0)
      //val predList2:List[BooleanTerm] = List(dec >= 1, dec === 0)
      //val predList2:List[BooleanTerm] = List(a === 0, i >= 0, i < 1000, j >= 0, j < 1000, a === 1000000)

      //val predList2:List[BooleanTerm] = List(cmp1 === True())

      //-v -f LLVM /Users/psksvp/Workspace/test2.ll
//      val predList:List[BooleanTerm] = List(//zero >= 0, zero < 100,
//                                             add > 0,
//                                             add < 100, //loop guard,
//                                             add === 100) // assert cond
//                                             //cmp)//, cmp1)

      val predList3:List[BooleanTerm] = List(i >= 1, i <= 10, a >= 0, a == i + 1)
 */


/*
def xmain(args:Array[String]):Unit=
  {
    val x0 = Ints("x0")
    val x1 = Ints("x1")
    val x2 = Ints("x2")
    val x3 = Ints("x3")

    val p1:List[BooleanTerm] = List(x1 >= 0, x2 < x1, x1 === x2)

    val a1 = abstractPostOf(x2 === x1, precondition = (x1 >= 0 & x2 === 0), p1)
    println(psksvp.termAsInfix(a1))
  }

  def notmain(args:Array[String]):Unit=
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
      println(equivalence(a1, a2))
    }

    {
      println("---------------------------------------------")
      val a1 = abstractPostOf(x1 === x0 + 1, precondition = x0 === 0, p1)
      val a2 = abstractPostOf(x1 === x0 + 1, precondition = x0 === 0, p2)
      println(termAsInfix(a1))
      println(termAsInfix(a2))
      println(equivalence(a1, a2))
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
      println(equivalence(a1, a2))
      println(equivalence(a3, a2))
    }

    {
      println("---------------------------------------------")
      val a1 = abstractPostOf(x1 === x0 + 1, precondition = True(), p1)
      val a2 = abstractPostOf(x1 === x0 + 1, precondition = True(), p2)
      val a3 = abstractPostOf(x1 === x0 + 1, precondition = True(), p3)
      println(termAsInfix(a1))
      println(termAsInfix(a2))
      println(termAsInfix(a3))
      println(equivalence(a1, a2))
      println(equivalence(a3, a2))
    }

    {
      println("---------------------------------------------")
      val a1 = abstractPostOf(True(), precondition = True(), p1)
      val a2 = abstractPostOf(True(), precondition = True(), p2)
      val a3 = abstractPostOf(True(), precondition = True(), p3)
      println(termAsInfix(a1))
      println(termAsInfix(a2))
      println(termAsInfix(a3))
      println(equivalence(a1, a2))
      println(equivalence(a3, a2))
    }




    val p0 = Bools("p0")
    println(equivalence(p0 & !p0, False()))

    val b = Bools("b")
    val c = Bools("c")
    val a = Bools("a")
    val d = Bools("d")

    println(equivalence(c | !(b & c),                                   True()))
    println(equivalence(!(a & b) & (!a | b) & (!b | b),                 !a))
    println(equivalence((a | c) & ( (a & d) | (a & !d)) | (a & c) | c,  a | c))
    println(equivalence( !a & (a | b) | (b | (a & a)) & (a | !b),       a | b))

    println(equivalence(False() | False(),                              False()))
    println(equivalence(False() | True(),                               True() & True()))

  }
 */
