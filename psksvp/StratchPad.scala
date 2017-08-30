package psksvp

// this file is a scratch pad

import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
import au.edu.mq.comp.smtlib.parser.{SMTLIB2Parser, SMTLIB2PrettyPrinter}
import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax._
import au.edu.mq.comp.smtlib.typedterms.QuantifiedTerm
import logics._
import org.bitbucket.inkytonik.kiama.util.StringSource

import scala.util.{Success, Failure}


/**
  * Created by psksvp on 9/5/17.
  */
object StratchPad
{
  def main(args: Array[String]): Unit =
  {
    //test1()
    //test61()
    //test19()
    //testWithReport()
  }



  def testEspresso():Unit=
  {
    val minterms = List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341, 342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352, 353, 354, 355, 356, 357, 358, 359, 360, 361, 362, 363, 364, 365, 366, 367, 368, 369, 370, 371, 372, 373, 374, 375, 376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, 388, 389, 390, 391, 392, 393, 394, 395, 396, 397, 398, 399, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 419, 420, 421, 422, 423, 424, 425, 426, 427, 428, 429, 430, 431, 432, 433, 434, 435, 436, 437, 438, 439, 440, 441, 442, 443, 444, 445, 446, 447, 448, 449, 450, 451, 452, 453, 454, 455, 456, 457, 458, 459, 460, 461, 462, 463, 464, 465, 466, 467, 468, 469, 470, 471, 472, 473, 474, 475, 476, 477, 478, 479, 480, 481, 482, 483, 484, 485, 486, 487, 488, 489, 490, 491, 492, 493, 494, 495, 496, 497, 498, 499, 500, 501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511)
    val r = BooleanMinimize(toCNF).espresso(minterms, 9)
    println(r)
  }

  def testBooleanMinimizeX():Unit =
  {
    val i = Ints("i")
    val j = Ints("j")

    val r = BooleanMinimize(toCNF).minimize(List(0, 1, 3, 7, 8, 9, 11, 15),
                                             List(i > 0, i < 0, j > 0, j < 0))
    println(termAsInfix(r))

    val m = BooleanMinimize(toDNF).minimize(List(0, 1, 3, 7, 8, 9, 11, 15),
                                             List(i > 0, i < 0, j > 0, j < 0))
    println(termAsInfix(m))
  }


  def testWithReport():Unit=
  {
    import psksvp.SkinkExecutor.Code
    val baseDir = "/home/psksvp/workspace/sv-bench"
    val loopAcc = List(Code(baseDir + "/c/loop-acceleration/const_true-unreach-call1.c", false, "clang-3.7", 20),
                     Code(baseDir + "/c/loop-acceleration/diamond_true-unreach-call1.c", false, "clang-3.7", 20),
                    //Code(baseDir + "/c/loop-acceleration/diamond_true-unreach-call2.c", false, "clang-3.7", 10),
                    Code(baseDir + "/c/loop-acceleration/functions_true-unreach-call1.c", false, "clang-3.7", 20),
                    Code(baseDir + "/c/loop-acceleration/nested_true-unreach-call1.c", false, "clang-3.7", 20),
                    Code(baseDir + "/c/loop-acceleration/overflow_true-unreach-call1.c", false, "clang-3.7", 20),
                    Code(baseDir + "/c/loop-acceleration/multivar_true-unreach-call1.c", false, "clang-3.7", 20),
                    Code(baseDir + "/c/loop-acceleration/phases_true-unreach-call1.c", false, "clang-3.7", 20),
                    Code(baseDir + "/c/loop-acceleration/phases_true-unreach-call2.c", false, "clang-3.7", 20),
                    Code(baseDir + "/c/loop-acceleration/simple_true-unreach-call1.c", false, "clang-3.7", 20),
                    Code(baseDir + "/c/loop-acceleration/simple_true-unreach-call2.c", false, "clang-3.7", 20),
                    Code(baseDir + "/c/loop-acceleration/simple_true-unreach-call3.c", false, "clang-3.7", 20),
                    Code(baseDir + "/c/loop-acceleration/simple_true-unreach-call4.c", false, "clang-3.7", 20),
                    Code(baseDir + "/c/loop-acceleration/underapprox_true-unreach-call1.c", false, "clang-3.7", 20),
                    Code(baseDir + "/c/loop-acceleration/underapprox_true-unreach-call2.c", false, "clang-3.7", 20))

    val loopLit = List(Code(baseDir + "/c/loop-lit/afnp2014_true-unreach-call.c", false, "clang-3.7", 10),
                    Code(baseDir + "/c/loop-lit/bhmr2007_true-unreach-call.c", false, "clang-3.7", 10),
                    Code(baseDir + "/c/loop-lit/cggmp2005_true-unreach-call.c", false, "clang-3.7", 10),
                    Code(baseDir + "/c/loop-lit/cggmp2005b_true-unreach-call.c", false, "clang-3.7", 10),
                    Code(baseDir + "/c/loop-lit/css2003_true-unreach-call.c", false, "clang-3.7", 10),
                    Code(baseDir + "/c/loop-lit/ddlm2013_true-unreach-call.c", false, "clang-3.7", 10),
                    Code(baseDir + "/c/loop-lit/gj2007_true-unreach-call.c", false, "clang-3.7", 10),
                    Code(baseDir + "/c/loop-lit/gj2007b_true-unreach-call.c", false, "clang-3.7", 10),
                    Code(baseDir + "/c/loop-lit/gr2006_true-unreach-call.c", false, "clang-3.7", 10),
                    Code(baseDir + "/c/loop-lit/gsv2008_true-unreach-call.c", false, "clang-3.7", 10),
                    Code(baseDir + "/c/loop-lit/ghhk2008_true-unreach-call.c", false, "clang-3.7", 10)
                   )

    val loopLitO2 = List(Code(baseDir + "/c/loop-lit/afnp2014_true-unreach-call.c", true, "clang-4.0", 10),
                        Code(baseDir + "/c/loop-lit/bhmr2007_true-unreach-call.c", true, "clang-4.0", 10),
                        Code(baseDir + "/c/loop-lit/cggmp2005_true-unreach-call.c", true, "clang-4.0", 10),
                        Code(baseDir + "/c/loop-lit/cggmp2005b_true-unreach-call.c", true, "clang-4.0", 10),
                        Code(baseDir + "/c/loop-lit/css2003_true-unreach-call.c", true, "clang-4.0", 10),
                        Code(baseDir + "/c/loop-lit/ddlm2013_true-unreach-call.c", true, "clang-4.0", 10),
                        Code(baseDir + "/c/loop-lit/gj2007_true-unreach-call.c", true, "clang-4.0", 10),
                        Code(baseDir + "/c/loop-lit/gj2007b_true-unreach-call.c", true, "clang-4.0", 10),
                        Code(baseDir + "/c/loop-lit/gr2006_true-unreach-call.c", true, "clang-4.0", 10),
                        Code(baseDir + "/c/loop-lit/gsv2008_true-unreach-call.c", true, "clang-4.0", 10),
                        Code(baseDir + "/c/loop-lit/hhk2008_true-unreach-call.c", false, "clang-3.7", 10)
                      )

    import scala.concurrent.duration._
    SkinkExecutor.runBenchAndOutputReport(loopLitO2, 120.minutes, "/home/psksvp/workspace/output")
  }

  def testBunchEasy():Unit =
  {
    val files = List(//"/home/psksvp/workspace/svcomp/c/loop-acceleration/diamond_false-unreach-call1.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/diamond_true-unreach-call1.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/diamond_true-unreach-call2.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/functions_true-unreach-call1.c",
                     //"/home/psksvp/workspace/svcomp/c/loop-acceleration/multivar_false-unreach-call1.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/multivar_true-unreach-call1.c",
                     //"/home/psksvp/workspace/svcomp/c/loop-acceleration/nested_false-unreach-call1.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/nested_true-unreach-call1.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/overflow_true-unreach-call1.c",
                     //"/home/psksvp/workspace/svcomp/c/loop-acceleration/phases_false-unreach-call1.c",
                     //"/home/psksvp/workspace/svcomp/c/loop-acceleration/phases_false-unreach-call2.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/phases_true-unreach-call1.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/phases_true-unreach-call2.c",
                     //"/home/psksvp/workspace/svcomp/c/loop-acceleration/simple_false-unreach-call1.c",
                     //"/home/psksvp/workspace/svcomp/c/loop-acceleration/simple_false-unreach-call2.c",
                     //"/home/psksvp/workspace/svcomp/c/loop-acceleration/simple_false-unreach-call3.c",
                     //"/home/psksvp/workspace/svcomp/c/loop-acceleration/simple_false-unreach-call4.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/simple_true-unreach-call1.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/simple_true-unreach-call2.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/simple_true-unreach-call3.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/simple_true-unreach-call4.c",
                     //"/home/psksvp/workspace/svcomp/c/loop-acceleration/underapprox_false-unreach-call1.c",
                     //"/home/psksvp/workspace/svcomp/c/loop-acceleration/underapprox_false-unreach-call2.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/underapprox_true-unreach-call1.c",
                     "/home/psksvp/workspace/svcomp/c/loop-acceleration/underapprox_true-unreach-call2.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/down_true-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/fragtest_simple_true-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/half_2_true-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/id_build_true-unreach-call.c",
                     //"/home/psksvp/workspace/svcomp/c/loop-invgen/id_trans_false-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/large_const_true-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/MADWiFi-encode_ie_ok_true-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/nest-if3_true-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/nested6_true-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/nested9_true-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/NetBSD_loop_true-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/sendmail-close-angle_true-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/SpamAssassin-loop_true-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loop-invgen/string_concat-noarr_true-unreach-call.c",
                     "/home/psksvp/workspace/svcomp/c/loops/sum01_bug02_false-unreach-call_true-termination.c",
                     "/home/psksvp/workspace/svcomp/c/loops/sum01_bug02_sum01_bug02_base.c",
                     //"/home/psksvp/workspace/svcomp/c/loops/sum01_false-unreach-call_true-termination.c",
                     "/home/psksvp/workspace/svcomp/c/loops/sum01_true-unreach-call_true-termination.c",
                     //"/home/psksvp/workspace/svcomp/c/loops/sum03_false-unreach-call_true-termination.c",
                     //"/home/psksvp/workspace/svcomp/c/loops/sum03_true-unreach-call_false-termination.c",
                     //"/home/psksvp/workspace/svcomp/c/loops/sum04_false-unreach-call_true-termination.c",
                     "/home/psksvp/workspace/svcomp/c/loops/sum04_true-unreach-call_true-termination.c",
                     "/home/psksvp/workspace/svcomp/c/loops/trex01_true-unreach-call.c")
                     //"/home/psksvp/workspace/svcomp/c/loops/trex02_false-unreach-call_true-termination.c")
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

    SkinkExecutor.consoleRun(toFile(code),
               Nil, //List(i >= 1, i <= 1000, !(i === 1001)),
               useO2 = false,
               usePredicateAbstraction = true,
               useClang = "clang-3.7")
  }

  def test1Dot1(): Unit =
  {
    val i = Ints("%i")
    val a = Ints("%a")

    // predicate abs ok
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |unsigned int __VERIFIER_nondet_uint();
                  |
                  |int main(int argc, char** arg)
                  |{
                  |  unsigned int n = __VERIFIER_nondet_uint();
                  |  int i;
                  |  for(i = 1; i <= n; i++);
                  |
                  |  if(i != n + 1) __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    SkinkExecutor.consoleRun(toFile(code),
              Nil, //List(i >= 1, i <= 1000, !(i === 1001)),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-4.0")
  }

  def test2(): Unit =
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
                  |  int a = 0;
                  |  for(i = 1; i <= 1000; i++);
                  |
                  |  if(i != 1001) __VERIFIER_error();
                  |  if(a != 0) __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    SkinkExecutor.consoleRun(toFile(code),
              Nil, //List(i >= 1, i <= 1000, !(i === 1001), a === 0),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-3.7")
  }

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

    SkinkExecutor.consoleRun(toFile(code),
               List(/*i >= 0,*/ i < 1000, i === 1000, /*a >= 0, a < 1000,*/ a === 1000, a === i),
               useO2 = false,
               usePredicateAbstraction = true,
               useClang = "clang-3.7")
  }

  def test31(useO2:Boolean = false, useClang:String = "clang-3.7"): Unit =
  {
    val i = Ints("%i")
    val a = Ints("%a")

    // pred abs works
    // interpol require many iteration
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |extern int __VERIFIER_nondet_int();
                  |extern void __VERIFIER_assume(int);
                  |int main(int argc, char** arg)
                  |{
                  |  int n = __VERIFIER_nondet_int();
                  |  __VERIFIER_assume( n > 0);
                  |  int a = 0;
                  |  int i = 0;
                  |  while(i < n)
                  |  {
                  |    if(i != a) __VERIFIER_error();
                  |    i = i + 1;
                  |    a = i;  //a = a + 1;
                  |  }
                  |  if(i != n)  __VERIFIER_error();
                  |  if(a != n)  __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    SkinkExecutor.consoleRun(toFile(code),
              Nil, //List(/*i >= 0,*/ i < 1000, i === 1000, /*a >= 0, a < 1000,*/ a === 1000, a === i),
              useO2,
              usePredicateAbstraction = true,
              useClang)
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

    SkinkExecutor.consoleRun(toFile(code),
               Nil, //List(x >= 0, y >= 0, x === 2000, x < 2000),
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

    SkinkExecutor.consoleRun(toFile(code),
               Nil, //List(y === x, x === -1, y === -1, x < 2000),
               useO2 = false,
               usePredicateAbstraction = true,
               useClang = "clang-3.7")
  }

  def test51(): Unit =
  {
    val x = Ints("%x")
    val y = Ints("%y")
    val n = Ints("%n")

    //
    // both ok.
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |int __VERIFIER_nondet_int();
                  |extern void __VERIFIER_assume(int);
                  |
                  |int main(int argc, char** arg)
                  |{
                  |  int n = __VERIFIER_nondet_int();
                  |  int x = 0;
                  |  int y = 0;
                  |  __VERIFIER_assume(n > 0);
                  |  while(x < n)
                  |  {
                  |      x++;
                  |      y++;
                  |  }
                  |  if(x == -1) __VERIFIER_error();
                  |  if(y == -1)   __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    SkinkExecutor.consoleRun(toFile(code),
              Nil, //List(n > 0, y === x, x === -1, y === -1, x < n),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-3.7")
  }

  def test52(): Unit =
  {
    val x = Ints("%x")
    val y = Ints("%y")
    val n = Ints("%n")

    // Pred ok
    // auto pred no.
    // Interpol max iter.
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |int __VERIFIER_nondet_int();
                  |extern void __VERIFIER_assume(int);
                  |
                  |int main(int argc, char** arg)
                  |{
                  |  int n = __VERIFIER_nondet_int();
                  |  int x = 0;
                  |  int y = 0;
                  |  __VERIFIER_assume(n > 0);
                  |  while(x < n)
                  |  {
                  |      x++;
                  |      y++;
                  |  }
                  |
                  |  // if I take the below verify out, the predicate inferrer
                  |  // would not be able to extract the x and y relation which is
                  |  // x == y
                  |  if(x != y) __VERIFIER_error();
                  |
                  |  // just more verifies
                  |  if(x != n) __VERIFIER_error();
                  |  if(y != n) __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    SkinkExecutor.consoleRun(toFile(code),
              Nil, //List(n > 0, y === x, x === n, y === n, x < n),
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

    SkinkExecutor.consoleRun(toFile(code),
              Nil, //List( x === 0, x > 0, y === x, y < x),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-3.7")
  }

  def test61(): Unit =
  {
    val x = Ints("%x")
    val y = Ints("%y")

    // pred abs works
    // interpol require many iteration
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |unsigned int __VERIFIER_nondet_uint();
                  |extern void __VERIFIER_assume(int);
                  |
                  |int main(int argc, char** arg)
                  |{
                  |  unsigned int x = __VERIFIER_nondet_uint();
                  |  __VERIFIER_assume(x > 0);
                  |  while(x > 0)
                  |  {
                  |      unsigned int y = 0;
                  |      while(y < x) y++;
                  |      if(y != x) __VERIFIER_error();
                  |      x--;
                  |  }
                  |  if(x != 0) __VERIFIER_error();
                  |
                  |  return 0;
                  |}
                """.stripMargin

    SkinkExecutor.consoleRun(toFile(code),
              Nil, //List( x === 0, x > 0, y === x, y < x),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-3.7")
  }

  def test62(): Unit =
  {
    val x = Ints("%x")
    val y = Ints("%y")

    // pred abs works
    // interpol require many iteration
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |extern int __VERIFIER_nondet_int();
                  |extern void __VERIFIER_assume(int);
                  |
                  |int main(int argc, char** arg)
                  |{
                  |  int x = __VERIFIER_nondet_int();
                  |  __VERIFIER_assume(x > 0);
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

    SkinkExecutor.consoleRun(toFile(code),
              Nil, //List( x === 0, x > 0, y === x, y < x),
              useO2 = true,
              usePredicateAbstraction = true,
              useClang = "clang-4.0")
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
                  |int main()
                  |{
                  |  int x = 1;
                  |  int a = 0;
                  |  while(x <= 1000)
                  |  {
                  |    a = a + 1;
                  |    if(a != x) __VERIFIER_error();
                  |    x = x + 1;
                  |  }
                  |  if(a != x - 1) __VERIFIER_error();
                  |  if(a != 1000) __VERIFIER_error();
                  |  if(x != 1001) __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    SkinkExecutor.consoleRun(toFile(code),
              List( x >= 0, a === 0, x <= 1000, a === 1000, a === x, a === x - 1, x === 1001),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-3.7")
  }
  def test71(): Unit =
  {
    val x = Ints("%x")
    val a = Ints("%a")
    val n = Ints("%n")

    // pred abs works
    // intepol works with too many iterations
    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |unsigned int __VERIFIER_nondet_uint();
                  |
                  |int main()
                  |{
                  |  unsigned int n = __VERIFIER_nondet_uint();
                  |  int x = 1;
                  |  int a = 0;
                  |  while(x <= n)
                  |  {
                  |    a = a + 1;
                  |    if(a != x) __VERIFIER_error();
                  |    x = x + 1;
                  |  }
                  |  if(a != x - 1) __VERIFIER_error();
                  |  if(a != n) __VERIFIER_error();
                  |  if(x != n + 1) __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    SkinkExecutor.consoleRun(toFile(code),
              Nil, //List( x >= 0, x <= n, a === n, a === x, a === x - 1, x === n + 1),
              useO2 = true,
              usePredicateAbstraction = true,
              useClang = "clang-4.0")
  }

  def test8(): Unit =
  {
    val x = Ints("%x")
    val a = Ints("%a")

    // predABS works 53 sec
    // interpol does not work max iter reach
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

    SkinkExecutor.consoleRun(toFile(code),
              //List( x >= 1, x <= 10, a === 0, a === 55, a === ((x * x) / 2) - (x / 2), x === 11),
              List((a  === 55 ) ,
                    (a  === (((x  * x )  / 2 )  - (x  / 2 ) ) ) ,
                      (x  >= 1 ) ,
                      (x  <= 10 ) ,
                      (a  === 0 ) ),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-3.7")
  }

  def test81(): Unit =
  {
    val x = Ints("%x")
    val a = Ints("%a")
    val n = Ints("%n")
    val p = Ints("%p")

    val code =  """
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |unsigned int __VERIFIER_nondet_uint();
                  |
                  |int main(int argc, char** arg)
                  |{
                  |  unsigned int p = __VERIFIER_nondet_uint();
                  |  int n = p + 1;
                  |  int x = 1;
                  |  int a = 0;
                  |  while(x <= n)
                  |  {
                  |    a = a + x;
                  |    if(a < x) __VERIFIER_error();
                  |    x = x + 1;
                  |  }
                  |  if(x != n + 1) __VERIFIER_error();
                  |  if(0 == a) __VERIFIER_error();
                  |  return 0;
                  |}
                """.stripMargin

    SkinkExecutor.consoleRun(toFile(code),
              List( p >= 0, n >= 1, x >= 1, x <= n, a >= 0, a >= x,  x === n + 1, a === 0),
              useO2 = false,
              usePredicateAbstraction = true,
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

    SkinkExecutor.consoleRun(toFile(code),
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


    SkinkExecutor.consoleRun(toFile(code),
              List( i === 1, j === 10 , j === 6, j >= i, i === Ints(21) - (j * 2) ),
              useO2 = false,
              usePredicateAbstraction = true,
              useClang = "clang-3.7")
  }

  def test11(): Unit =
  {
    val x = Ints("%x")
    val y = Ints("%y")

    // interpolant: max iter reach
    // predicate abstraction works 399 sec, now down to 196
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

    SkinkExecutor.consoleRun(toFile(code),
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
    // predicate abstraction     no
    // gj2007b_true-unreach-call.c
    val code =  """
                  |// Source: Sumit Gulwani, Nebosja Jojic: "Program Verification as
                  |// Probabilistic Inference", POPL 2007.
                  |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
                  |int __VERIFIER_nondet_int();
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
                  |    if(!(m >= 0 || n <= 0) ) __VERIFIER_error(); //__VERIFIER_assert((m >= 0 || n <= 0));
                  |    if(!(m < n || n <= 0)  ) __VERIFIER_error(); //__VERIFIER_assert((m < n || n <= 0));
                  |    return 0;
                  |}
                """.stripMargin

    def implies(a:PredicateTerm, b:PredicateTerm):PredicateTerm = !a | b

    SkinkExecutor.consoleRun(toFile(code),
              Nil,//List(m === x, x < n, n <= 0, m >= 0, m < n),
              useO2 = true,
              usePredicateAbstraction = true,
              maxIteration = 30,
              useClang = "clang-4.0")
  }

  def test13(): Unit =
  {
      val r = Ints("%r")

      //val i = Ints("%2")
      //val j = Ints("%3")


      /// this is false
      val code =

        """
            |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
            |
            |
            |int __VERIFIER_nondet_int();
            |
            |int main ()
            |{
            |  int r = __VERIFIER_nondet_int();
            |  while (r > 0)
            |  {
            |      if(!(r > 0)) __VERIFIER_error();
            |      r = r - 1;
            |  }
            |  if(!(r > 0)) __VERIFIER_error();
            |}
          """.stripMargin

      SkinkExecutor.consoleRun(toFile(code),
                List( r > 0 ),
                useO2 = false,
                usePredicateAbstraction = true,
                useClang = "clang-3.7")
  }

  def test14():Unit =
  {
    val code2 =
      """
        |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
        |
        |void __VERIFIER_assert(int cond) {
        |  if (!(cond)) {
        |    ERROR: __VERIFIER_error();
        |  }
        |  return;
        |}
        |
        |int main(void) {
        |  unsigned int x = 0;
        |  unsigned int y = 1;
        |
        |  while (x < 6) {
        |    x++;
        |    y *= 2;
        |  }
        |
        |  __VERIFIER_assert(x != 6);
        |}
      """.stripMargin
    val code =
      """
        |extern void __VERIFIER_error(void);
        |extern void __VERIFIER_assume(int);
        |void __VERIFIER_assert(int cond) {
        |  if (!(cond)) {
        |    ERROR: __VERIFIER_error();
        |  }
        |  return;
        |}
        |int __VERIFIER_nondet_int();
        |#define LARGE_INT 1000000
        |int main()
        |{
        |    int scheme;
        |    int urilen,tokenlen;
        |    int cp,c;
        |    urilen = __VERIFIER_nondet_int();
        |    tokenlen = __VERIFIER_nondet_int();
        |    scheme = __VERIFIER_nondet_int();
        |    __VERIFIER_assume(urilen <= LARGE_INT && urilen >= -LARGE_INT);
        |    __VERIFIER_assume(tokenlen <= LARGE_INT && tokenlen >= -LARGE_INT);
        |    __VERIFIER_assume(scheme <= LARGE_INT && scheme >= -LARGE_INT);
        |
        |    if(urilen>0); else goto END;
        |    if(tokenlen>0); else goto END;
        |    if(scheme >= 0 );else goto END;
        |    if (scheme == 0 || (urilen-1 < scheme)) {
        |        goto END;
        |    }
        |
        |    cp = scheme;
        |
        |    __VERIFIER_assert(cp-1 < urilen);
        |    __VERIFIER_assert(0 <= cp-1);
        |
        |    if (__VERIFIER_nondet_int()) {
        |        __VERIFIER_assert(cp < urilen);
        |        __VERIFIER_assert(0 <= cp);
        |        while ( cp != urilen-1) {
        |            if(__VERIFIER_nondet_int()) break;
        |            __VERIFIER_assert(cp < urilen);
        |            __VERIFIER_assert(0 <= cp);
        |            ++cp;
        |        }
        |        __VERIFIER_assert(cp < urilen);
        |        __VERIFIER_assert( 0 <= cp );
        |        if (cp == urilen-1) goto END;
        |        __VERIFIER_assert(cp+1 < urilen);
        |        __VERIFIER_assert( 0 <= cp+1 );
        |        if (cp+1 == urilen-1) goto END;
        |        ++cp;
        |
        |        scheme = cp;
        |
        |        if (__VERIFIER_nondet_int()) {
        |            c = 0;
        |            __VERIFIER_assert(cp < urilen);
        |            __VERIFIER_assert(0<=cp);
        |            while ( cp != urilen-1
        |                    && c < tokenlen - 1) {
        |                __VERIFIER_assert(cp < urilen);
        |                __VERIFIER_assert(0<=cp);
        |                if (__VERIFIER_nondet_int()) {
        |                    ++c;
        |                    __VERIFIER_assert(c < tokenlen);
        |                    __VERIFIER_assert(0<=c);
        |                    __VERIFIER_assert(cp < urilen);
        |                    __VERIFIER_assert(0<=cp);
        |                }
        |                ++cp;
        |            }
        |            goto END;
        |        }
        |    }
        |
        |END:
        |    return 0;
        |}
        |
        |
      """.stripMargin

    SkinkExecutor.consoleRun(toFile(code2),
              Nil,
              useO2 = true,
              usePredicateAbstraction = true,
              useClang = "clang-4.0")
  }


  def test15():Unit=
  {

    val codeNoO2 =
      """
        |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
        |
        |
        |int main(void) {
        |  unsigned int x = 0;
        |
        |  while (x < 0x0fffffff) {
        |    if (x < 0xfff0) {
        |      x++;
        |    } else {
        |      x += 2;
        |    }
        |  }
        |
        |  if(x % 2)  __VERIFIER_error();
        |  //__VERIFIER_assert(!(x % 2));
        |}
      """.stripMargin

    SkinkExecutor.consoleRun(toFile(codeNoO2),
              Nil,
              useO2 = true,
              usePredicateAbstraction = true,
              useClang = "clang-4.0")
  }

  def test16():Unit=
  {
    val code =
      """
       |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
       |extern void __VERIFIER_assume(int);
       |extern unsigned int __VERIFIER_nondet_uint(void);
       |
       |int main(void)
       |{
       |  unsigned int x = 1;
       |  unsigned int y = __VERIFIER_nondet_uint();
       |
       |  __VERIFIER_assume(y > 0);
       |
       |  while (x < y)
       |  {
       |    if (x < y / x)
       |    {
       |      x *= x;
       |    }
       |    else
       |    {
       |      x++;
       |    }
       |  }
       |
       |  if(x != y) __VERIFIER_error();
       |}
      """.stripMargin

    val x = Ints("%x")
    val y = Ints("%y")

    SkinkExecutor.consoleRun(toFile(code),
              Nil, //List(x === 1, y > 0, x < y, x === y, (y / x) > x),
              useO2 = true,
              usePredicateAbstraction = true,
              useClang = "clang-4.0")
  }

  def test17():Unit=
  {
    val code =
      """
        |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
        |extern void __VERIFIER_assume(int);
        |extern  int __VERIFIER_nondet_int(void);
        |
        |int main()
        |{
        |    int n, sum, i;
        |    n = __VERIFIER_nondet_int();
        |    __VERIFIER_assume(1 <= n && n <= 1000);
        |    sum = 0;
        |    for(i = 1; i <= n; i++)
        |    {
        |        sum = sum + i;
        |    }
        |    if(!(2*sum == n*(n+1))) __VERIFIER_error();
        |    return 0;
        |}
      """.stripMargin

    val i = Ints("%i")
    val n = Ints("%n")
    val sum = Ints("%sum")

    SkinkExecutor.consoleRun(toFile(code),
              Nil, //List(n >= 1 & n <= 1000, i === 1 & i < n, i === n, sum === 0, sum === ((i * i) / 2) - (i / 2)), //, sum === ((i * i) / 2) - (i / 2)),
              useO2 = true,
              usePredicateAbstraction = true,
              useClang = "clang-4.0")
  }

  def test18():Unit =
  {
    // pred with auto gen works, 3 iters with only one
    // interpol works
    val code =
      """
        |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
        |
        |extern void __VERIFIER_assume(int);
        |void __VERIFIER_assert(int cond) {
        |  if (!(cond)) {
        |    ERROR: __VERIFIER_error();
        |  }
        |  return;
        |}
        |int __VERIFIER_nondet_int();
        |_Bool __VERIFIER_nondet_bool();
        |
        |int main()
        |{
        |    int x=__VERIFIER_nondet_int();
        |    int y=__VERIFIER_nondet_int();
        |    int z=__VERIFIER_nondet_int();
        |    __VERIFIER_assume(x<100);
        |    __VERIFIER_assume(x>-100);
        |    __VERIFIER_assume(z<100);
        |    __VERIFIER_assume(z>-100);
        |    while(x<100 && 100<z)
        |    {
        |        _Bool tmp=__VERIFIER_nondet_bool();
        |        if (tmp) {
        |            x++;
        |        } else {
        |            x--;
        |            z--;
        |        }
        |    }
        |
        |    __VERIFIER_assert(x>=100 || z<=100);
        |
        |    return 0;
        |}
      """.stripMargin

    SkinkExecutor.consoleRun(toFile(code),
              Nil,
              useO2 = true,
              usePredicateAbstraction = true,
              useClang = "clang-4.0")
  }

  def test19():Unit=
  {
    val code =
      """
        |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
        |
        |void __VERIFIER_assert(int cond) {
        |  if (!(cond)) {
        |    ERROR: __VERIFIER_error();
        |  }
        |  return;
        |}
        |unsigned int __VERIFIER_nondet_uint();
        |_Bool __VERIFIER_nondet_bool();
        |
        |int main()
        |{
        |  unsigned int x1=__VERIFIER_nondet_uint(), x2=__VERIFIER_nondet_uint(), x3=__VERIFIER_nondet_uint();
        |  unsigned int d1=1, d2=1, d3=1;
        |  _Bool cb1=__VERIFIER_nondet_bool(), cb2=__VERIFIER_nondet_bool();
        |
        |  while(x1>0 && x2>0 && x3>0)
        |  {
        |    if (cb1) x1=x1-d1;
        |    else if (cb2) x2=x2-d2;
        |    else x3=x3-d3;
        |    cb1=__VERIFIER_nondet_bool();
        |    cb2=__VERIFIER_nondet_bool();
        |  }
        |
        |  //__VERIFIER_assert(x1==0 || x2==0 || x3==0);
        |  if(!(x1==0 || x2==0 || x3==0)) __VERIFIER_error();
        |  return 0;
        |}
      """.stripMargin

    val cb1 = Ints("%cb1")
    val cb2 = Ints("%cb2")
    val x1 = Ints("%x1")
    val x2 = Ints("%x2")
    val x3 = Ints("%x3")


    SkinkExecutor.consoleRun(toFile(code),
              Nil, //List(x1 > 0, x2 > 0, x3 > 0, x1 >= 0, x2 >= 0, x3 >= 0),
              useO2 = true,
              usePredicateAbstraction = true,
              useClang = "clang-4.0")
  }

  def test20(): Unit =
  {
    val code =
      """
        |extern void __VERIFIER_error() __attribute__ ((__noreturn__));
        |extern unsigned int __VERIFIER_nondet_uint();
        |
        |int main() {
        |  int n;
        |  int k = 0;
        |  int i = 0;
        |  n = __VERIFIER_nondet_int();
        |  while( i < n ) {
        |      i++;
        |      k++;
        |  }
        |  int j = n;
        |  while( j > 0 ) {
        |      //__VERIFIER_assert(k > 0);
        |      if(!(k > 0)) __VERIFIER_error();
        |      j--;
        |      k--;
        |  }
        |  return 0;
        |}
        |
      """.stripMargin

    val n = Ints("%n")
    val k = Ints("%k")
    val i = Ints("%i")
    val j = Ints("%j")

    SkinkExecutor.consoleRun(toFile(code),
                              Nil, //List(n >= 0, i === 0, k === 0, i < n, i > 0, k <= 0, j === n, j < n, k === i, i === n),
                              useO2 = true,
                              usePredicateAbstraction = true,
                              useClang = "clang-4.0")
  }
}
