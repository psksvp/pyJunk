package psksvp

import au.edu.mq.comp.skink.ir.llvm.LLVMFunction

/**
  * Created by psksvp on 23/6/17.
  * FunctionInformation is specific to LLVMFunction (LLVM Ir instruction)
  */
case class FunctionInformation(function: LLVMFunction)
{
  /**
    *
    */
  lazy val blockVariables:Map[String, Set[String]] =
  {
    import org.scalallvm.assembly.AssemblySyntax._

    def variablesOf(block:Block):Set[String] =
    {
      val v = for (metaInstr <- block.optMetaInstructions) yield
              {
                metaInstr.instruction match
                {
                  case Alloca(Binding(n1),_,_,_,_)                      => Set(n1)
                  case Store(_, _, Named(n1), _, Named(n2), _)          => Set(n1, n2)
                  case Store(_, _, Const(_), _, Named(n1), _)           => Set(n1)
                  case Load(Binding(n1), _, _, _, Named(n2), _)         => Set(n1, n2)
                  case Load(Binding(n1), _, _, _, Const(_), _)          => Set(n1)
                  case Call(Binding(n1), _, _, _, _, _, _, _)           => Set(n1)
                  case Compare(Binding(n1), _, _, Named(n2), Named(n3)) => Set(n1, n2, n3)
                  case Compare(Binding(n1), _, _, Const(_), Named(n3))  => Set(n1, n3)
                  case Compare(Binding(n1), _, _, Named(n3), Const(_))  => Set(n1, n3)
                  case Compare(Binding(n1), _, _, Const(_), Const(_))   => Set(n1)
                  case Binary(Binding(n1), _, _, Named(n2), Named(n3))  => Set(n1, n2, n3)
                  case Binary(Binding(n1), _, _, Const(_), Named(n3))   => Set(n1, n3)
                  case Binary(Binding(n1), _, _, Named(n3), Const(_))   => Set(n1, n3)
                  case Binary(Binding(n1), _, _, Const(_), Const(_))    => Set(n1)
                  case _                                                => Set[Name]()
                }
              }



      val u = for(metaPhiInstr <- block.optMetaPhiInstructions) yield
              {
                def varInPredecessors(pds:Seq[PhiPredecessor]):Set[Name] =
                {
                  val pv = for(ppd <- pds) yield ppd.value match
                           {
                             case Named(n) => Set(n)
                             case _        => Set[Name]()
                           }
                  pv.reduceLeft(_ union _)
                }

                metaPhiInstr.phiInstruction match
                {
                  case Phi(Binding(n1), _,  ppd:Vector[PhiPredecessor]) => varInPredecessors(ppd) + n1
                  case _                                                => Set[Name]()
                }

              }


      for (n <- (v ++ u).reduceLeft(_ union _)) yield n match
      {
        case Global(name) => s"%$name"
        case Local(name)  => s"%$name"
      }
    }

    val m = for (b <- function.funTree.root.functionBody.blocks
                 if b.optMetaInstructions.nonEmpty) yield b.optBlockLabel.toString -> variablesOf(b)
    m.toMap
  }

  /**
    *
    */
  lazy val allVariables:Set[String] = (for((_, variables) <- blockVariables) yield variables).reduceLeft(_ union _)

  /**
    *
    */
  lazy val commonVariables:Set[String] =
  {
    def appearCount(v:String):Int =
    {
      val c = for((_, variables) <- blockVariables if variables.contains(v)) yield 1
      c.sum
    }

    val comVar = for(v <- allVariables if appearCount(v) > 1) yield v
    comVar
  }
}
