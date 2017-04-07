

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
  import au.edu.mq.comp.smtlib.solvers._
  object resources extends Resources
  import resources._
  object Cmds extends Commands
  import Cmds._
  object logics extends IntegerArithmetics with Core
  import logics._

  type BooleanTerm = TypedTerm[BoolTerm, Term]
  type AbstractDomain = Seq[Int]

  var debug = false

  def debugEnable(enable:Boolean):Unit=
  {
    debug = enable
  }

  def isDebugging = debug

  /**
    *
    * @param term
    * @return
    */
  def satisfiableCheck(term : BooleanTerm) : SatResponses =
  {
    val result = using(new Z3)
    {
      implicit solver => isSat(term)
    }

    result match
    {
      case Success(s) => s
      case _          => sys.error(s"psksvp.satisfiableCheck of terms $term solver returns fail")
    }
  }

  /**
    *
    * @param term
    * @return
    */
  def isValid(term : BooleanTerm) : Boolean = UnSat() == satisfiableCheck(!term)


  /***
    *
    * @param f1
    * @param f2
    * @return
    */
  def isEquivalence(f1:BooleanTerm, f2:BooleanTerm): Boolean = isValid(f1 === f2)


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
    val result = using(new Z3)
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
  def CNF(s:List[List[BooleanTerm]]):BooleanTerm = s match
  {
    case Nil       => sys.error("psksvp.CNF, Nil list was passed")
    case l :: Nil  => l.reduce(_ | _)
    case l :: rest => l.reduce(_ | _) & CNF(rest)  // conjunt them
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
}
