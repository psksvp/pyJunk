package psksvp.ADT

import scala.annotation.tailrec

/**
  * Created by psksvp on 26/5/17.
  */
class FixedPoint[T](equalp:(T, T)=>Boolean, stepf:T => T)
{
  @tailrec
  final def run(current:T):T=
  {
    val next = stepf(current)
    if (equalp(next, current))
      current
    else
      run(next)
  }
}

object FixedPoint
{
  def apply[T](equalp:(T, T)=>Boolean,
               stepf:T => T) = new FixedPoint[T](equalp, stepf)
}
