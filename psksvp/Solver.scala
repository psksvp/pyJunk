package psksvp

import au.edu.mq.comp.smtlib.configurations.SolverConfig
import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
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
