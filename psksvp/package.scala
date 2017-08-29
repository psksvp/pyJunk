import au.edu.mq.comp.smtlib.configurations.SolverConfig
import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter

import scala.concurrent.duration.Duration
import scala.util.Failure

/**
  * Created by psksvp on 14/2/17.
  */
package object psksvp
{
  import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.Sat
  import au.edu.mq.comp.smtlib.theories.{Core, IntegerArithmetics}
  import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.Term
  import au.edu.mq.comp.smtlib.theories.BoolTerm
  import au.edu.mq.comp.smtlib.typedterms.TypedTerm
  import scala.util.Success
  import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.{SatResponses, UnSat}
  import au.edu.mq.comp.smtlib.typedterms.Commands
  import au.edu.mq.comp.smtlib.interpreters.Resources
  object resources extends Resources
  object Cmds extends Commands
  import Cmds._
  object logics extends IntegerArithmetics with Core
  import logics._

  //type PredicateTerm = TypedTerm[BoolTerm, Term]
  type PredicateTerm = TypedTerm[BoolTerm, Term]
  type AbstractDomain = Seq[Int]

  /**
    *
    * @param term
    * @return
    */
  def satisfiableCheck(term : PredicateTerm)
                      (implicit solver:SMTLIBInterpreter):SatResponses =
  {
    push()
    val result = isSat(term) match
                 {
                   case Success(s) => s
                   case Failure(e) => sys.error(s"psksvp.satisfiableCheck of terms ${psksvp.termAsInfix(term)} solver returns ${e.toString}")
                   case _          => sys.error(s"psksvp.satisfiableCheck of terms ${psksvp.termAsInfix(term)} solver returns fail")
                 }
    pop()
    result
  }

  /**
    *
    * @param term
    * @return
    */
  def validityCheck(term : PredicateTerm)
                   (implicit solver:SMTLIBInterpreter):Boolean = UnSat() == satisfiableCheck(!term)


  /***
    *
    * @param f1
    * @param f2
    * @return
    */
  def equivalence(f1:PredicateTerm, f2:PredicateTerm)
                 (implicit solver:SMTLIBInterpreter):Boolean = validityCheck(f1 === f2)


  /**
    * check if predicate q contains predicate p
    * @param predicate
    * @param withSuperSet
    * @param solver
    * @return
    */
  def subsetCheck(predicate:PredicateTerm, withSuperSet:PredicateTerm)
                 (implicit solver:SMTLIBInterpreter):Boolean = satisfiableCheck(predicate & !withSuperSet) match
                                                            {
                                                              case Sat()   => false
                                                              case UnSat() => true
                                                              case _       => false
                                                            }

  /**
    *
    * @param p  precondition
    * @param e  effect
    * @param q  postcondition
    * @return true if p is included in q
    */
  def checkPost(p:PredicateTerm, e:PredicateTerm, q:PredicateTerm)
               (implicit solver:SMTLIBInterpreter):Boolean = satisfiableCheck(p & e & !q) match
                                                             {
                                                               case Sat()   => false
                                                               case UnSat() => true
                                                               case _       => false
                                                             }

  def termAsInfix[A](terms:Seq[TypedTerm[A, Term]]):String =
  {
    if(terms.nonEmpty)
    {
      val ls = for (t <- terms) yield termAsInfix(t)
      ls.reduceLeft(_ + ",\n" + _)
    }
    else
      "termAsInfix arg is an empty list"
  }
  /**
    *
    */
  def termAsInfix[A](term: TypedTerm[A, Term]):String=
  {
    object InfixSMTLibTermPrettyPrinter extends au.edu.mq.comp.smtlib.parser.SMTLIB2PrettyPrinter
    {
      import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax._
      override def toDoc( astNode : ASTNode ) : Doc = astNode match
      {
        case v @ LessThanEqualTerm( v1, v2 ) =>
          char ( '(' ) <> toDoc ( v1 ) <> space <> text ( "<=" ) <> space <>  toDoc ( v2 ) <> text ( ")" ) <> space
        case v @ LessThanTerm( v1, v2 ) =>
          char ( '(' ) <> toDoc ( v1 ) <> space <> text ( "<" ) <> space <> toDoc ( v2 ) <> text ( ")" ) <> space
        case v @ GreaterThanEqualTerm( v1, v2 ) =>
          char ( '(' ) <> toDoc ( v1 ) <> space <> text ( ">=" ) <> space <> toDoc ( v2 ) <> text ( ")" ) <> space
        case v @ GreaterThanTerm( v1, v2 ) =>
          char ( '(' ) <> toDoc ( v1 ) <> space <> text ( ">" ) <> space <> toDoc ( v2 ) <> text ( ")" ) <> space
        case v @ EqualTerm( v1, v2 ) =>
          char ( '(' ) <> toDoc ( v1 ) <> space <> text ( "=" ) <> space <> toDoc ( v2 ) <> text ( ")" ) <> space

        case v @ AndTerm (v1, v2) =>
          char (' ') <> text ("And(") <> space <> toDoc (v1) <> comma  <> ssep (v2.map (toDoc), comma) <> text (")") <> space
        case v @ OrTerm (v1, v2) =>
          char (' ') <> text ("Or(") <> space <> toDoc (v1) <> comma <> ssep (v2.map (toDoc), comma) <> text (")") <> space
        case v @ XorTerm (v1, v2) =>
          char (' ') <> text ("Xor(") <> space <> toDoc (v1) <> comma <> toDoc (v2) <> text (")") <> space
        case v @ ImplyTerm (v1, v2) =>
          char (' ') <> text ("Implies") <> space <> toDoc (v1) <> comma <> ssep (v2.map (toDoc), comma) <> text (")") <> space
        //case v @ IfThenElseTerm (v1, v2, v3) =>
        //  char (' ') <> text ("ite") <> space <> toDoc (v1) <> toDoc (v2) <> toDoc (v3) <> text (")") <> space
        case v @ NotTerm (v1) =>
          char (' ') <> text ("Not(") <> space <> toDoc (v1) <> text (")") <> space

        case v @ PlusTerm (v1, v2) =>
          char ('(') <> toDoc (v1) <> space <> text ("+") <> space <> ssep (v2.map (toDoc), emptyDoc) <> text (")") <> space
        case v @ SubTerm (v1, v2) =>
          char ('(') <> toDoc (v1) <> space <> text ("-") <> space <> ssep (v2.map (toDoc), emptyDoc) <> text (")") <> space
        case v @ MultTerm (v1, v2) =>
          char ('(') <> toDoc (v1) <> space <> text ("*") <> space <> ssep (v2.map (toDoc), emptyDoc) <> text (")") <> space
        case v @ IntDivTerm (v1, v2) =>
          char ('(') <> toDoc (v1) <> space <> text ("//") <> space <> ssep (v2.map (toDoc), emptyDoc) <> text (")") <> space
        case v @ RealDivTerm (v1, v2) =>
          char ('(') <> toDoc (v1) <> space <> text ("/") <> space <> ssep (v2.map (toDoc), emptyDoc) <> text (")") <> space
        case v @ IntModTerm (v1, v2) =>
          char ('(')  <> toDoc (v1) <> text ("%") <> space <> toDoc (v2) <> text (")") <> space

        case _ => super.toDoc(astNode)
      }

    }

    InfixSMTLibTermPrettyPrinter.show(term.termDef)
  }


  /**
    *
    * @param s
    * @return
    */
  def toCNF(s:List[List[PredicateTerm]]):PredicateTerm = s match
  {
    case Nil       => sys.error("psksvp.CNF, Nil list was passed")
    case l :: Nil  => l.reduce(_ | _)
    case l :: rest => l.reduce(_ | _) & toCNF(rest)  // conjunct them
  }

  def toDNF(s:List[List[PredicateTerm]]):PredicateTerm = s match
  {
    case Nil       => sys.error("psksvp.CNF, Nil list was passed")
    case l :: Nil  => l.reduce(_ & _)
    case l :: rest => l.reduce(_ & _) | toDNF(rest)  // disjunct them
  }


  /**
    *
    * @param xl
    * @return
    */
  def generatePairs(xl: Seq[Int]): List[List[(Int, Int)]] = xl match
  {
    case l if (l.size < 2) => Nil
    case a :: xa => xa.map((a, _)) :: generatePairs(xa)
  }

  /**
    *
    * @param n
    * @param bits
    * @return
    */
  def binaryString(n:Int, bits:Int):String=
  {
    require(bits > 0, "psksvp.binaryString arg bit must be > 0")
    require(n >= 0, s"psksvp.binaryString($n, $bits) n (1st args) must be >= 0")
    require(n <= Integer.parseInt("1" * bits, 2), s"psksvp.binaryString($n, $bits) $bits bits is too small for $n ")
    val format = "%" + bits + "s"
    // make sure we have all the leading zeros
    String.format(format, Integer.toBinaryString(n)).replace(" ", "0")
  }

  def solverFromName(name : String) : SolverConfig =
  {
    au.edu.mq.comp.smtlib.configurations.AppConfig.config.find(_.name == name) match
    {
      case Some(sc) => sc
      case None     => sys.error(s"TraceRefinement: can't find solver called $name in config file")
    }
  }

  def toFile(code:String, fileExt:String = ".c"):String=
  {
    import java.io.PrintWriter
    var tmpDir = System.getProperty("java.io.tmpdir")
    if(tmpDir.last != '/') tmpDir = tmpDir + "/"
    val file = scala.util.Random.alphanumeric.take(10).mkString
    val fileName = tmpDir + file + fileExt
    new PrintWriter(fileName)
    {
      write(code)
      close()
    }
    fileName
  }

  def printrc(row:Int, col:Int, s:String):Unit =
  {
    import ADT.ANSI._
    setCursor(row, col)
    print(s)
  }

  def copyFile(path:String, toDir:String):Unit=
  {
    import sys.process._
    Seq("cp", path, s"$toDir/.").!!
  }

  def fileExists(path:String):Boolean = new java.io.File(path).exists()


  def runWithTimeout[T](timeout:Duration, defaultReturn:T)(f: => T):T =
  {
    import scala.concurrent.{Await, Future}
    import scala.concurrent.ExecutionContext.Implicits.global
    try
    {
      Await.result(Future[T](f), timeout)
    }
    catch
    {
      case _:Throwable => defaultReturn
    }
  }
}
