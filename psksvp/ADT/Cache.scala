package psksvp.ADT

/**
 * Created by psksvp on 21/06/15.
 */
class Cache[K, V](generator:K => V)
{
  private val map = scala.collection.mutable.Map[K, V]()
  private var hit = 0L
  private var mis = 0L

  def apply(key:K):V=
  {
    this.synchronized
    {
      if (map.contains(key))
      {
        hit = hit + 1
        map(key)
      }
      else
      {
        mis = mis + 1
        val d = generator(key)
        map(key) = d
        d
      }
    }
  }

  def clear():Unit =
  {
    map.clear()
    hit = 0
    mis = 0
  }
  def statistic:(Long, Long)=(hit, mis)
}

/**
 *
 */
object Cache
{
  def apply[K, V](f:K => V)= new Cache[K, V](f)
}


