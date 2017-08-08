package psksvp

import au.edu.mq.comp.smtlib.theories.{Core, IntegerArithmetics}

object BooleanMinimize extends IntegerArithmetics with Core
{
  import psksvp.ADT.Cache
  private val cache = Cache[(Seq[Int], Seq[BooleanTerm]), List[List[BooleanTerm]]]
                      {
                        case (minTerms, predicates) => minimize(minTerms.toList,
                                                                predicates.toList)
                      }

  def cacheStatistic:(Long, Long) = cache.statistic

  def apply(minTerms:Seq[Int],
            predicates:Seq[BooleanTerm]):List[List[BooleanTerm]] = cache((minTerms, predicates))

  def minimize(minTerms:List[Int],
               predicates:List[BooleanTerm]):List[List[BooleanTerm]] =
  {

    def toTerms(sl:String):Seq[BooleanTerm] =
    {
      for(i <- predicates.indices if sl(i) != '-') yield
      {
        sl(i) match
        {
          case '1' => predicates(i)
          case '0' => !predicates(i)
          case _   => sys.error("BooleanMinimize.toTerms match error")
        }
      }
    }

    def valid(line:String):Boolean = line.indexOf(".i") < 0 &&
                                     line.indexOf(".o") < 0 &&
                                     line.indexOf(".p") < 0

    val result = espresso(minTerms, predicates.length)
    val r = for (line <- result.split("\n") if valid(line)) yield toTerms(line).toList
    r.toList
  }

  /**
    *
    * @param minTerms
    * @param numberOfBits
    * @param timeout
    * @param exePath
    * @return
    */
  def espresso(minTerms:Seq[Int],
               numberOfBits:Int,
               timeout:Int = 20,
               exePath:String = "espresso"):String=
  {
    import scala.util.{Failure, Success}
    import scala.concurrent.duration._
    import org.bitbucket.franck44.expect.Expect

    val table = for(i <- 0 to Integer.parseInt("1" * numberOfBits, 2)) yield
                {
                  val bin = psksvp.binaryString(i, numberOfBits)
                  val out = if (minTerms.indexOf(i) >= 0) "1" else "0"
                  s"$bin $out \n"
                }

    val pla = s"""
                 |.i $numberOfBits
                 |.o 1
                 |${table.reduceLeft(_ + _)}
                 |.e\n
               """.stripMargin

    val esp = Expect(exePath, Nil)
    esp.send(pla)
    val result = esp.expect(".e".r, timeout.minutes)  match
                 {
                   case Success(r) => r
                   case Failure(e) => sys.error(e.toString)
                   case _          => sys.error("espresso fail ")
                 }
    esp.destroy()
    result
  }

}
