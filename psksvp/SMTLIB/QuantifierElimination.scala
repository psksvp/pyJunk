package psksvp.SMTLIB


import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
import au.edu.mq.comp.smtlib.parser.{Analysis, PredefinedParsers, SMTLIB2Parser}
import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax._
import au.edu.mq.comp.smtlib.theories.BoolTerm
import au.edu.mq.comp.smtlib.typedterms.{Commands, TypedTerm}
import au.edu.mq.comp.smtlib.eval
import org.bitbucket.inkytonik.kiama.util.StringSource

import scala.util.{Failure, Success, Try}


trait QuantifierElimination
{
  def apply(existsTerm: TypedTerm[BoolTerm, Term])
           (implicit solver : SMTLIBInterpreter): Try[Seq[TypedTerm[BoolTerm, Term]]]
}

/**
  * Created by psksvp on 9/6/17.
  */
object Z3QE extends QuantifierElimination
               with Commands
               with PredefinedParsers
{
  def apply(existsTerm: TypedTerm[BoolTerm, Term])
           (implicit solver : SMTLIBInterpreter): Try[Seq[TypedTerm[BoolTerm, Term]]] =
  {
    require(solver.name == "Z3", "Z3QE requires Z3 solver")

    def makeTerms(s:String):Try[Seq[TypedTerm[BoolTerm, Term]]]=
    {
      //TODO: too complex, refactor
      def toTypedTerm(ls:Seq[Term]):Try[Seq[TypedTerm[BoolTerm, Term]]] =
      {
        val parseDeclCmdResponse = SMTLIB2Parser[GetDeclCmdResponse]

        eval( Seq( GetDeclCmd() ) ) flatMap
          //
          { solverResponse => parseDeclCmdResponse ( StringSource(solverResponse) ) } flatMap
          //
          {
            case DeclCmdResponseSuccess( solverDeclStack ) =>
              Success ( ls map {
                i =>
                  val b = Analysis( i ).ids
                  val defs = for {
                    SortedQId( x, s ) <- solverDeclStack if b.contains( SimpleQId( x ) )
                  } yield SortedQId( x, s )

                  TypedTerm[ BoolTerm, Term ]( defs.toSet, i )
              } )

            case _ => sys.error("fail to get solverDeclStack")
          }

      }

      if(s.trim.isEmpty)
        Success(Nil)
      else
      {
        ///////////////////////////////////////////////////////////
        // catch a ride with GetInterpolantResponses parser for now.
        SMTLIB2Parser[GetInterpolantResponses].apply(StringSource(s)) match
        {
          case Success(GetInterpolantResponsesSuccess(InterpolantResponse(ls))) => toTypedTerm(ls)
          case _                                                                => sys.error("fail to parse response")
        }
      }
    }

    //KISS parser for now
    def parseForTermsAsString(s:String):String =
    {
      val idx1 = s.indexOf("(goals\n(goal")
      val idx2 = s.indexOf(":precision")

      if(idx1 >= 0 && idx2 >= 0 && idx2 > idx1)
        s.substring(idx1 + "(goals\n(goal".length, idx2 - 1)
      else
        ""   /// bad bad bad
    }

    /////////////////////////////////////
    val boundedVars = existsTerm.termDef match
    {
      case ExistsTerm(vars, _) => vars
      case _                   => sys.error("term is not a ExistsTerm")
    }

    val freeVarDeclCmd:Seq[Command] = for(v <- existsTerm.typeDefs.toSeq) yield
                                      {
                                        v.id match
                                        {
                                          case SymbolId(s) => DeclareFunCmd(FunDecl(s, List(), v.sort))
                                          case _           => sys.error(s"Z3QE, unsupported var type:${v.id}")
                                        }
                                      }

    val boundedVarDeclCmd:Seq[Command] = for(v <- boundedVars) yield DeclareFunCmd(FunDecl(v.sMTLIB2Symbol,
                                                                                            List(),
                                                                                            v.sort))
    val cmds = freeVarDeclCmd ++
               boundedVarDeclCmd :+
               AssertCmd(existsTerm.termDef) :+
               Raw("(apply (using-params qe :qe-nonlinear false))")
    push()
    val m = eval(cmds.toList) match
            {
              case Success(respond)  => makeTerms(parseForTermsAsString(respond))
              case Failure(e)        => sys.error(e.toString)
              case _                 => sys.error("eval Fail at Z3QE")
            }
    pop()
    m
  }
}