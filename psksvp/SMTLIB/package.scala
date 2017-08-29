package psksvp

import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
import au.edu.mq.comp.smtlib.parser.Analysis
import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax._
import au.edu.mq.comp.smtlib.theories
import au.edu.mq.comp.smtlib.theories.BoolTerm
import au.edu.mq.comp.smtlib.typedterms.{QuantifiedTerm, TypedTerm}

import scala.util.Try
/**
  * Created by psksvp on 18/6/17.
  */
package object SMTLIB
{
  def reset(solver:SMTLIBInterpreter):Unit =
  {
    solver.eval(Raw("(reset)"))
  }

  def breakOrTerm(sortedQIds:Set[SortedQId], orTerm:OrTerm):Seq[PredicateTerm] =
  {
    val r = for(t <- orTerm.terms :+ orTerm.term) yield
            {
              val a = Analysis(t).ids
              val defs = for(SortedQId(x, s) <- sortedQIds if a.contains(SimpleQId(x))) yield SortedQId(x,s)
              TypedTerm[BoolTerm, Term](defs, t)
            }
    r
  }

  /**
    * interim to create exists typedTerm.
    * until scala-smtlib fix the parameters of QuantifiedTerm.exists from (x:SMTLIB2Symbol, xs:SSymbol*) to
    * (x:SMTLIB2Symbol, xs:SMTLIB2Symbol*)
    */
  object Exists extends QuantifiedTerm
  {
    /**
      * Split the t.typeDefs into bounded/unbounded vars
      * result is a Map m. m(true) <=> bounded, m(false) <=> free vars
      *
      *
      * @param symbolSet   The symbols that should be bounded
      * @param t           The term to analyse
      */
    private def splitBounded[ A, T <: Term ](
                                              symbolSet : Seq[ SMTLIB2Symbol ], t : TypedTerm[ A, T ]
                                            ) : Map[ Boolean, Set[ SortedQId ] ] =
    {

      t.typeDefs.groupBy
      {
        //  discriminator for grouping
        v => v match
             {
               //  bounded
               case SortedQId( SymbolId( name ), _ ) if symbolSet.contains( name )  => true

               //  not bounded
               case SortedQId( SymbolId( name ), _ ) if !symbolSet.contains( name ) => false

               //  should never happen
               case e =>
                 // logger.error( "Variable {} encountered in typeDefs for {}", e, defs )
                 throw new Exception( "Type different to SortedQid encountered in typeDefs for term" )
             }
      }

    }

    def apply[T <: Term](x:SMTLIB2Symbol, xs:SMTLIB2Symbol*)(defs : ⇒ TypedTerm[ BoolTerm, T ]) =
    {

      //  split the vars into bounded/unbounded
      val groupedVars = splitBounded( x +: xs, defs )

      /*
       * Transform the boundedVars into SortedVars for use in ForAllTerm
       *
       * SortedVar and SortedQId contain the same information but using different
       * SMTLIB2 grammar rules.
       * Note that a SortedVar is pretty printed as (name sort) whereas s SortedQId
       * shows as (as name sort)
       */
      val boundedVars = groupedVars.getOrElse( true, Set[ SortedQId ]() ) map
      {
        case SortedQId( SymbolId( name ), sort ) => SortedVar( name, sort )
        case _                                   => sys.error("match error at psksvp.SMTLIB.Exists")
      }

      /*
       * Get the free variables in the term (the ones that should be declared in
       * the solver)
       */
      val freeVars = groupedVars.getOrElse( false, Set[ SortedQId ]() )

      //  build the TypedTerm
      TypedTerm[ BoolTerm, ExistsTerm ](
                                         freeVars,
                                         ExistsTerm(
                                                     boundedVars.toList,
                                                     defs.termDef
                                                   )
                                       )
    }
  }
}
