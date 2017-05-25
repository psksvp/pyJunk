import au.edu.mq.comp.smtlib.configurations.SolverConfig
import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter

/**
  * Created by psksvp on 14/2/17.
  */
package object psksvp
{
  import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.{Sat, UnKnown}
  import scala.util.Failure
  import scala.util.Try
  import au.edu.mq.comp.smtlib.theories.{Core, IntegerArithmetics}
  import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.Term
  import au.edu.mq.comp.smtlib.theories.BoolTerm
  import au.edu.mq.comp.smtlib.typedterms.TypedTerm
  import scala.util.Success
  import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.{SatResponses, UnSat}
  import au.edu.mq.comp.smtlib.typedterms.Commands
  import au.edu.mq.comp.smtlib.interpreters.Resources
  object resources extends Resources
  import resources._
  object Cmds extends Commands
  import Cmds._
  object logics extends IntegerArithmetics with Core
  import logics._

  type BooleanTerm = TypedTerm[BoolTerm, Term]
  type AbstractDomain = Seq[Int]

  var satHitCounter = 0
  /**
    *
    * @param term
    * @return
    */
  def satisfiableCheck(term : BooleanTerm)
                      (implicit solver:SMTLIBInterpreter):SatResponses =
  {
    push()
    val result = isSat(term) match
                 {
                   case Success(s) => s
                   case _          => sys.error(s"psksvp.satisfiableCheck of terms $term solver returns fail")
                 }
    pop()
    satHitCounter = satHitCounter + 1
    result
  }

  /**
    *
    * @param term
    * @return
    */
  def validityCheck(term : BooleanTerm)
                   (implicit solver:SMTLIBInterpreter):Boolean = UnSat() == satisfiableCheck(!term)


  /***
    *
    * @param f1
    * @param f2
    * @return
    */
  def equivalence(f1:BooleanTerm, f2:BooleanTerm)
                  (implicit solver:SMTLIBInterpreter):Boolean = validityCheck(f1 === f2)


  /**
    *
    * @param p  precondition
    * @param e  effect
    * @param q  postcondition
    * @return true of p is included in q
    */
  def checkPost(p:BooleanTerm,
                e:BooleanTerm,
                q:BooleanTerm):Try[Boolean]=
  {
    val result = using(new SMTLIBInterpreter(solverFromName("Z3")))
    {
      implicit solver => isSat(p & e & !q) match
      {
        //  if Sat, checkPost is false
        case Success(Sat())   => Success(false)

        //  if unSat checkPost is true
        case Success(UnSat()) => Success(true)

        case Success(UnKnown()) =>
          sys.error(s"psksvp.checkPost Solver returned UnKnown for check-sat")

        case Failure(f) =>
          sys.error(s"psksvp.checkPost Solver failed to determine sat-status in checkpost $f")
      }
    }
    result
  }
  /**
    *
    */
  def termAsInfix(term: BooleanTerm):String=
  {
    /**
      *
      */
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

        case _ => super.toDoc(astNode)
      }
    }

    InfixSMTLibTermPrettyPrinter.show(term.termDef)
  }


  /**
    *
    * @param minTerms
    * @param predicates
    * @return
    */
  def booleanMinimize(minTerms:List[Int],
                      predicates:List[BooleanTerm]):List[List[BooleanTerm]] =
  {
    import psksvp.QuineMcCluskey._

    val symbols:List[String] = (for(i <- predicates.indices) yield {s"p$i"}).toList
    val symbol2Predicate:Map[String, BooleanTerm] = (symbols zip predicates).toMap

    def groupMinTerm(implicant:Implicant):List[BooleanTerm]=
    {
      val weights = symbols.indices.map(1 << _).reverse
      val varByWeight = (weights zip symbols).toMap

      val varList = for(w <- weights) yield
                    {
                      if(!implicant.group.contains(w))
                      {
                        val p = symbol2Predicate(varByWeight(w))
                        if ((implicant.minterm & w) != 0)
                          p     //varByWeight(w)
                        else
                          !p    //varByWeight(w) + "'"
                      }
                      else
                        False()
                    }

      varList.filter(_ != False()).toList
    }

    val implicants = minTerms.sorted.sortBy(bitCount(_)).map(new Implicant(_))
    val primeImplicants = genImplicants(implicants, symbols.length).filter(_.prime)

    val piTable = PITable.solve(primeImplicants, minTerms, symbols)
    val r = piTable.results.map(groupMinTerm(_)).toList
    r
  }

  /**
    *
    * @param s
    * @return
    */
  def toCNF(s:List[List[BooleanTerm]]):BooleanTerm = s match
  {
    case Nil       => sys.error("psksvp.CNF, Nil list was passed")
    case l :: Nil  => l.reduce(_ | _)
    case l :: rest => l.reduce(_ | _) & toCNF(rest)  // conjunt them
  }

  def toDNF(s:List[List[BooleanTerm]]):BooleanTerm = s match
  {
    case Nil       => sys.error("psksvp.CNF, Nil list was passed")
    case l :: Nil  => l.reduce(_ & _)
    case l :: rest => l.reduce(_ & _) | toDNF(rest)  // conjunt them
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
}
