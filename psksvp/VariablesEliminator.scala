package psksvp

import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.ExistsTerm
import au.edu.mq.comp.smtlib.theories.BoolTerm
import au.edu.mq.comp.smtlib.typedterms.TypedTerm

trait VariablesEliminator
{
  def eliminate(existsTerm:TypedTerm[BoolTerm, ExistsTerm]): Seq[BooleanTerm]
}

class Z3PyQE extends  VariablesEliminator
{
  import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
  import au.edu.mq.comp.smtlib.typedterms.QuantifiedTerm
  import logics._

  override def eliminate(existsTerm:TypedTerm[BoolTerm, ExistsTerm]): Seq[BooleanTerm] =
  {
    Nil
  }
}