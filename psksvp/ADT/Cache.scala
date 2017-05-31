package psksvp.ADT

/**
 * Created by psksvp on 21/06/15.
 */
class Cache[K, V](generator:K => V)
{
  private val map = scala.collection.mutable.Map[K, V]()
  private var hit = 0
  private var mis = 0

  def apply(key:K):V=
  {
    if(hasKey(key))
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

  def set(key:K, data:V):Unit = map(key) = data
  def hasKey(key:K):Boolean = map.contains(key)
  def clear():Unit =
  {
    map.clear()
    hit = 0
    mis = 0
  }
  def statistic:(Int, Int)=(hit, mis)
}

/**
 *
 */
object Cache
{
  def apply[K, D](f:K => D)= new Cache[K, D](f)
}


