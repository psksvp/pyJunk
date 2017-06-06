package psksvp.SMTLIB

import au.edu.mq.comp.smtlib.parser.SMTLIB2PrettyPrinter
import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax._
import au.edu.mq.comp.smtlib.theories.BoolTerm
import au.edu.mq.comp.smtlib.typedterms.TypedTerm
import psksvp.BooleanTerm

/**
  * Created by psksvp on 30/5/17.
  */
object QuantifierElimination
{
  def varDecl(v:SortedQId):String = v.id match
  {
    case SymbolId(symbol) => SMTLIB2PrettyPrinter.show(DeclareConstCmd(symbol, v.sort))
    case IndexedId(_, _)  => sys.error("QuantifierElimination cannot handle IndexedId yet")
  }

  def apply(existsTerm:TypedTerm[BoolTerm, ExistsTerm]): String =
  {
    import scala.sys.process._

    val declVars = for(v <- existsTerm.typeDefs) yield varDecl(v)
    val assert = AssertCmd(existsTerm.termDef)

    val code = declVars.mkString("\n") + "\n" +
               SMTLIB2PrettyPrinter.show(assert) + "\n" +
               "(apply (using-params qe :qe-nonlinear true))"

    val result = (Seq("/usr/local/bin/z3", psksvp.toFile(code, ".z3")).!!).trim
    result
  }
}
