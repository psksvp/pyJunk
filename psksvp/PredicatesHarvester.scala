package psksvp

import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
import au.edu.mq.comp.smtlib.parser.Analysis
import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax._
import au.edu.mq.comp.smtlib.theories.BoolTerm
import au.edu.mq.comp.smtlib.typedterms.{Commands, QuantifiedTerm, TypedTerm}


/**
  * Created by psksvp on 19/5/17.
  */

trait PredicatesFilter
{
  def apply(predicates:Set[BooleanTerm]):Set[BooleanTerm]
}


trait PredicatesHarvester
{
  def inferredPredicates:Set[BooleanTerm]

  def inferredWithFilters(filters:Seq[PredicatesFilter]):Set[BooleanTerm] =
  {
    var p = inferredPredicates
    for(f <- filters)
    {
      p = f(p)
    }
    p
  }
}




/// infer using existential quantifiers elimination
class EQEPredicatesHarvester(traceAnalyzer:TraceAnalyzer,
                             functionInformation:FunctionInformation,
                             solver:SMTLIBInterpreter) extends PredicatesHarvester
                                                               with Commands
                                                               with QuantifiedTerm
{
  import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.{ISymbol, SymbolId, Term}
  import au.edu.mq.comp.smtlib.theories.BoolTerm
  import au.edu.mq.comp.smtlib.typedterms.TypedTerm

  import scala.util.Success

  ///
  def inferPredicates(blockNo:Int):Seq[TypedTerm[BoolTerm, Term]] =
  {
    val (blockEffect, lastIndex) = traceAnalyzer.function.traceBlockEffect(traceAnalyzer.trace,
                                                                           blockNo,
                                                                           traceAnalyzer.choices(blockNo))

    ////////////////////////////////////////////////////////////////////////
    def blockLocalVariable(blockNo:Int):Set[ISymbol] =
    {
      def commonVar(s:ISymbol):Boolean =
      {
        if(lastIndex.isDefinedAt(s.simpleVarname))
          functionInformation.commonVariables.contains(s.simpleVarname) && lastIndex(s.simpleVarname) == s.digits
        else
          functionInformation.commonVariables.contains(s.simpleVarname)
      }

      val local = for(v <- blockEffect.typeDefs) yield v.id match
                                                       {
                                                         case SymbolId(s@ISymbol(n, i))
                                                              if !commonVar(s) => s
                                                         case _                => ISymbol("0", -1) /// ARGGGGGGG.......
                                                       }
      local.filter(_.digits >= 0)
    }

    ////////////////////////////////////////////////////////////////////////
    val s = blockLocalVariable(blockNo).toSeq
    if(s.nonEmpty)
    {
      //val indexedPre = pre.indexedBy {case _ => 0}
      val e = SMTLIB.Exists(s.head, s.tail:_*)(blockEffect) //( indexedPre & blockEffect)
      println(s"exists term:${psksvp.termAsInfix(e)}")

      psksvp.SMTLIB.Z3QE(e)(solver) match
      {
        case Success(ls) => val r = ls.map { t => t.unIndexed }
                            println(termAsInfix(r))
                            r
        case _           => sys.error(s"psksvp.SMTLIB.Z3QE($e) fail")
      }
    }
    else
    {
      println("list of variables to quantified over is empty")
      Nil
    }
  }
  override def inferredPredicates: Set[BooleanTerm] =
  {
    val r = for(block <- 0 until traceAnalyzer.length - 1) yield inferPredicates(block).toSet
    r.reduce(_ union _)
  }
}



object BreakOrTerms extends PredicatesFilter
{
  override def apply(predicates:Set[BooleanTerm]):Set[BooleanTerm] =
  {
    val r = for(t <- predicates) yield
            {
              t.termDef match
              {
                case r:OrTerm => psksvp.SMTLIB.breakOrTerm(t.typeDefs, r).toSet
                case _        => Set(t)
              }
            }
    r.reduceLeft(_ union _)
  }
}

object ReduceToEqualTerms extends PredicatesFilter
{
  override def apply(predicates:Set[BooleanTerm]):Set[BooleanTerm] =
  {
    var rm:Set[BooleanTerm] = Set()
    val pairs = for(i <- predicates; j <- predicates if i != j) yield (i, j)
    val rt = for((t1, t2) <- pairs) yield
    {
      val a = Analysis(t1.termDef).ids
      val defs = for(SortedQId(x, s) <- t1.typeDefs if a.contains(SimpleQId(x))) yield SortedQId(x,s)
      (t1.termDef, t2.termDef) match
      {
        case (GreaterThanEqualTerm(a1, a2), LessThanEqualTerm(b1, b2))
              if(a1 == b1 && a2 == b2) => rm = rm ++ List(t1, t2)
                                          Set(TypedTerm[BoolTerm, Term](defs, EqualTerm(a1, a2)))

        case (LessThanEqualTerm(b1, b2), GreaterThanEqualTerm(a1, a2))
              if(a1 == b1 && a2 == b2) => rm = rm ++ List(t1, t2)
                                          Set(TypedTerm[BoolTerm, Term](defs, EqualTerm(a1, a2)))

        case  _                        => Set(t1, t2)
      }
    }
    rt.reduceLeft(_ union _) -- rm
  }
}
