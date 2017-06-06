package psksvp

// this file is a scratch pad

import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.SSymbol
import au.edu.mq.comp.smtlib.typedterms.QuantifiedTerm
import logics._


object runSkink
{
  def apply(filename:String,
            predicates:Seq[BooleanTerm],
            useO2:Boolean,
            usePredicateAbstraction:Boolean,
            useClang:String = "clang-4.0",
            maxIteration:Int = 20):Unit=
  {
    import au.edu.mq.comp.skink.Main
    PredicatesAbstraction.setToUsePredicates(predicates)
    val args = List("-v",
                    if(usePredicateAbstraction) "--use-predicate-abstraction" else "",
                    if(useO2) "" else "--no-O2",
                     "--use-clang", useClang,
                     "-m", maxIteration.toString,
                    filename)

     Main.main(args.filter(_.length > 0).toArray)

    println("---------------------------------------------------")
    println("equivalence checking satHitCounter:" + psksvp.satHitCounter)
    println("time used in checking combination:" + psksvp.PredicatesAbstraction.timeUsedCheckComb)
    println("whole time used by predAbs:" + psksvp.PredicatesAbstraction.timeUsedWhole)
  }
}


/**
  * Created by psksvp on 9/5/17.
  */
object test
{
  def main(args:Array[String]):Unit=
  {
    test3()
  }


  def testDNF():Unit=
  {
    import scala.util.{Try, Success}
    import au.edu.mq.comp.smtlib.interpreters.Resources
    object resources extends Resources
    import resources._
    val p0 = Bools("p0")
    val p1 = Bools("p1")
    val dnf = (!p0 & !p1) | (!p0 & p1) | (p0 & !p1) | (p0 & p1)
    val cnf = (!p0 | !p1) & (!p0 | p1) & (p0 | !p1) & (p0 | p1)
    val r = using[Boolean](new SMTLIBInterpreter(solverFromName("Z3")))
    {
      implicit solver => println(equivalence(True(), dnf))
                         println(equivalence(False(), cnf))

                         Success(true)
    }
  }

  def testQE():Unit=
  {
    object qt extends QuantifiedTerm
    import qt._

    val a = Ints("a")
    val cmp = Bools("cmp")
    val five = Ints("five")
    val ex = exists(five.symbol, cmp.symbol.asInstanceOf[SSymbol])
             {
               five === a & cmp === (five < 1000) & False() === cmp
             }

    val r = psksvp.SMTLIB.QuantifierElimination(ex)
    println(r)
  }

  def test1(): Unit =
  {
    val i = Ints("%i")
    val a = Ints("%a")

    // predicate abs ok
    val code =  """
      |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
      |
      |int main(int argc, char** arg)
      |{
      |  int i;
      |  for(i = 1; i <= 1000; i++);
      |
      |  if(i != 1001) __VERIFIER_error();
      |  return 0;
      |}
    """.stripMargin

    runSkink(toFile(code),
               List(i >= 1, i <= 1000, !(i === 1001)),
               useO2 = false,
               usePredicateAbstraction = true,
               useClang = "clang-3.7")
  }

  def test2(): Unit = test7()

  def test3(): Unit =
  {
    val i = Ints("%i")
    val a = Ints("%a")

    // pred abs works
    // interpol require many iteration
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |
                  |int main(int argc, char** arg)
                  |{
                  |  int a = 0;
                  |  int i = 0;
                  |  while(i < 1000)
                  |  {
                  |    if(i != a) __VERIFIER_error();
                  |    i = i + 1;
                  |    a = i;  //a = a + 1;
                  |  }
                  |  if(i != 1000)  __VERIFIER_error();
                  |  if(a != 1000)  __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    runSkink(toFile(code),
               List(/*i >= 0,*/ i < 1000, i === 1000, /*a >= 0, a < 1000,*/ a === 1000, a === i),
               useO2 = false,
               usePredicateAbstraction = true,
               useClang = "clang-3.7")
  }

  def test4(): Unit =
  {
    val x = Ints("%x")
    val y = Ints("%y")

    // interpolant have problem
    // predicate abs ok
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |
                  |int main(int argc, char** arg)
                  |{
                  |  int x = 0;
                  |  int y = 0;
                  |  while(x < 2000)
                  |  {
                  |      x++;
                  |      y++;
                  |  }
                  |  if(x != 2000) __VERIFIER_error();
                  |  if(y < 0)   __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    runSkink(toFile(code),
               List(x >= 0, y >= 0, x === 2000, x < 2000),
               useO2 = false,
               usePredicateAbstraction = true,
               useClang = "clang-3.7")
  }

  def test5(): Unit =
  {
    val x = Ints("%x")
    val y = Ints("%y")

    //
    // both ok.
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |
                  |int main(int argc, char** arg)
                  |{
                  |  int x = 0;
                  |  int y = 0;
                  |  while(x < 2000)
                  |  {
                  |      x++;
                  |      y++;
                  |  }
                  |  if(x == -1) __VERIFIER_error();
                  |  if(y == -1)   __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    runSkink(toFile(code),
               List(x >= 0, y >= 0, x === 2000, x < 2000),
               useO2 = false,
               usePredicateAbstraction = true,
               useClang = "clang-3.7")
  }

  def test6(): Unit =
  {
    val x = Ints("%x")
    val y = Ints("%y")

    // pred abs works
    // interpol require many iteration
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |
                  |int main(int argc, char** arg)
                  |{
                  |  int x = 2000;
                  |  while(x > 0)
                  |  {
                  |      int y = 0;
                  |      while(y < x) y++;
                  |      if(y != x) __VERIFIER_error();
                  |      x--;
                  |  }
                  |  if(x != 0) __VERIFIER_error();
                  |
                  |  return 0;
                  |}
                """.stripMargin

    runSkink(toFile(code),
              List( x === 0, x > 0, y === x, y < x),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-3.7")
  }

  def test7(): Unit =
  {
    val x = Ints("%x")
    val a = Ints("%a")

    // pred abs works
    // intepol works with too many iterations
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |
                  |int main(int argc, char** arg)
                  |{
                  |  int x = 1;
                  |  int a = 0;
                  |  while(x <= 1000)
                  |  {
                  |    a = a + 1;
                  |    if(a != x) __VERIFIER_error();
                  |    x = x + 1;
                  |  }
                  |  if(a != 1000) __VERIFIER_error();
                  |  if(x != 1001) __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    runSkink(toFile(code),
              List( x >= 0, x <= 1000, a === 1000, a === x, a === x - 1, x === 1001),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-3.7")
  }

  def test8(): Unit =
  {
    val x = Ints("%x")
    val a = Ints("%a")

    // both does not work
    // non linear relation ship between a and x
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |
                  |int main(int argc, char** arg)
                  |{
                  |  int x = 1;
                  |  int a = 0;
                  |  while(x <= 10)
                  |  {
                  |    a = a + x;
                  |    x = x + 1;
                  |  }
                  |  if(a != 55) __VERIFIER_error();
                  |  if(x != 11) __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    runSkink(toFile(code),
              List( x >= 0, x <= 10, a === 55, a === x * 5, x === 11),
              useO2 = false,
              usePredicateAbstraction = false,
              useClang = "clang-3.7")
  }

  def test9(): Unit =
  {
    val x = Ints("%x")
    val y = Ints("%y")
    val n = Ints("%n")

    // both works
    // count_up_down_true-unreach-call_true-termination.c
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |unsigned int __VERIFIER_nondet_uint();
                  |
                  |int main()
                  |{
                  |  unsigned int n = __VERIFIER_nondet_uint();
                  |  unsigned int x=n, y=0;
                  |  while(x>0)
                  |  {
                  |    x--;
                  |    y++;
                  |  }
                  |  if(y != n) __VERIFIER_error();
                  |}
                """.stripMargin

    runSkink(toFile(code),
              List( n >= 0, x === n, x > 0, y === 0, y === n, y === n - x),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-3.7")
  }

  def test10(): Unit =
  {
    val i = Ints("%i")
    val j = Ints("%j")

    // interpolant works
    // predicate abstraction works
    // cggmp2005_true-unreach-call.c
    val code =  """
                  |// Source: A. Costan, S. Gaubert, E. Goubault, M. Martel, S. Putot: "A Policy
                  |// Iteration Algorithm for Computing Fixed Points in Static Analysis of
                  |// Programs", CAV 2005
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |unsigned int __VERIFIER_nondet_uint();
                  |
                  |int main()
                  |{
                  |    int i,j;
                  |    i = 1;
                  |    j = 10;
                  |    while (j >= i)
                  |    {
                  |        i = i + 2;
                  |        j = -1 + j;
                  |    }
                  |    if(j != 6) __VERIFIER_error();
                  |    return 0;
                  |}
                """.stripMargin

    runSkink(toFile(code),
              List( i === 1, j === 10, j === 6, j >= i, i === Ints(21) - (j * 2) ),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-3.7")
  }

  def test11(): Unit =
  {
    val x = Ints("%x")
    val y = Ints("%y")

    // interpolant: max iter reach
    // predicate abstraction works 399 sec
    val code =  """
                  |// Source: Sumit Gulwani, Nebosja Jojic: "Program Verification as
                  |// Probabilistic Inference", POPL 2007.
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |
                  |int main()
                  |{
                  |    int x = 0;
                  |    int y = 50;
                  |    while(x < 100)
                  |    {
                  |	     if (x < 50)
                  |	       x = x + 1;
                  |	     else
                  |      {
                  |	       x = x + 1;
                  |	       y = y + 1;
                  |	     }
                  |    }
                  |    if(y != 100) __VERIFIER_error();
                  |    return 0;
                  |}
                """.stripMargin

    runSkink(toFile(code),
              List( x === 0, y === 50, x < 100, x < 50, y === 100, x === y),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-3.7")
  }

  def test12(): Unit =
  {
    val m = Ints("%m")
    val n = Ints("%n")
    val x = Ints("%x")
    val call1 = Ints("%call1")

    // interpolant: max iter reach
    // predicate abstraction     --- untest
    // gj2007b_true-unreach-call.c
    val code =  """
                  |// Source: Sumit Gulwani, Nebosja Jojic: "Program Verification as
                  |// Probabilistic Inference", POPL 2007.
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |unsigned int __VERIFIER_nondet_uint();
                  |
                  |
                  |int main()
                  |{
                  |    int x = 0;
                  |    int m = 0;
                  |    int n = __VERIFIER_nondet_int();
                  |    while(x < n)
                  |    {
                  |	      if(__VERIFIER_nondet_int())
                  |       {
                  |	        m = x;
                  |	      }
                  |	      x = x + 1;
                  |    }
                  |
                  |    //if(!(m >= 0 || n <= 0) ) __VERIFIER_error(); //__VERIFIER_assert((m >= 0 || n <= 0));
                  |    //if(!(m < n || n <= 0)  ) __VERIFIER_error(); //__VERIFIER_assert((m < n || n <= 0));
                  |    if(!((m >= 0 || n <= 0) && (m < n || n <= 0)))
                  |      __VERIFIER_error();
                  |    return 0;
                  |}
                """.stripMargin

    def implies(a:BooleanTerm, b:BooleanTerm):BooleanTerm = !a | b

    runSkink(toFile(code),
              List(call1 === 0, m === x, x < n, n < 0, n > 0, n === 0, (m >= 0 | n <= 0) & (m < n | n <= 0 )),
              useO2 = false,
              usePredicateAbstraction = true,
              maxIteration = 30,
              useClang = "clang-3.7")
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
