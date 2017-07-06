package psksvp.ADT

/**
  * Created by psksvp on 19/6/17.
  */
class AutoDispose[T](disposable:Disposable)(code: => T)
{
  def run:T =
  {
    val result = code
    disposable.dispose()
    result
  }
}

object AutoDispose
{
  def apply[T](disposable:Disposable)
              (code: => T):AutoDispose[T] = new AutoDispose[T](disposable)(code)
}
