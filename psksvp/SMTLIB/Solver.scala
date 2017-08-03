package psksvp.SMTLIB

import au.edu.mq.comp.smtlib.configurations.SolverConfig
import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
import au.edu.mq.comp.smtlib.typedterms.Commands
import psksvp.ADT
import psksvp.ADT.WorkerPool

/**
  * Created by psksvp on 3/7/17.
  */
class Solver(config:SolverConfig) extends SMTLIBInterpreter(config)
                                     with ADT.Worker
{
  override def run():Boolean = true
  override def shutdown():Unit = this.destroy()
}


class SolverPool(pool:Seq[Solver]) extends WorkerPool[Solver](pool)
                                      with Commands
{
  // override to push before returning it
  override def get(timeOut: Long): Solver =
  {
    val s = super.get(timeOut)
    push()(s)
    s
  }

  //override to pop before releasing it.
  override def release(worker: Solver): Unit =
  {
    pop()(worker)
    super.release(worker)
  }
}
