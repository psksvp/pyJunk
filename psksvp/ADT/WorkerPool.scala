package psksvp.ADT


/**
  * Created by psksvp on 3/7/17.
  */

trait Worker
{
  def run():Boolean
  def shutdown():Unit
}

class WorkerPool[W <: Worker](pool:Seq[W])
{
  private val busy = Array.fill[Boolean](pool.length)(false)
  pool.map(_.run)

  def shutdown():Unit = pool.map(_.shutdown)


  def get(timeOut:Long = 1000):W =
  {
    var found = false
    var index = -1

    while (!found)
    {
      this.synchronized
      {
        val i = busy.indexOf(false)
        if (i >= 0)
        {
          found = true
          busy(i) = true
          index = i
        }
      }
    }
    pool(index)
  }

  def release(worker:W):Unit = this.synchronized
  {
    val i = pool.indexOf(worker)
    if(i >= 0)
      busy(i) = false
  }
}

object WorkerPool
{
  def apply[W <: Worker](pool:Seq[W]):WorkerPool[W] = new WorkerPool[W](pool)
}


