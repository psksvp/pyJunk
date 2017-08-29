package psksvp

import au.edu.mq.comp.smtlib.theories.{Core, IntegerArithmetics}

case class BooleanMinimize(termCombinerFunc:List[List[PredicateTerm]] => PredicateTerm) extends IntegerArithmetics with Core
{
  import psksvp.ADT.Cache
  private val cache = Cache[(Seq[Int], Seq[PredicateTerm]), PredicateTerm]
                      {
                        case (minTerms, predicates) => minimize(minTerms.toList,
                                                                predicates.toList)
                      }

  /**
    *
    * @return
    */
  def cacheStatistic:(Long, Long) = cache.statistic

  /**
    * minimize boolean combination by looking at cache
    * @param minTerms
    * @param predicates
    * @return
    */
  def apply(minTerms:List[Int],
               predicates:List[PredicateTerm]):PredicateTerm = cache((minTerms, predicates))

  /**
    * minimize boolean combination without looking at cache
    * @param minTerms
    * @param predicates
    * @return
    */
  def minimize(minTerms:List[Int],
               predicates:List[PredicateTerm]):PredicateTerm =
  {
    // to understand the body of function minimize
    // look at function espresso for the comment about
    // espresso's input and out format (pla)
    def toTerms(sl:String):Seq[PredicateTerm] =
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
    termCombinerFunc(r.toList)
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

    /*
       The pla for minterm List(0, 1, 3, 7, 8, 9, 11, 15) with 4 predicates (p0 ,p1, p2, p3) looks like below.
       .e indicates the end of input

      -----------------------------
      .i 4
      .o 1
      0000 1
      0001 1
      0010 0
      0011 1
      0100 0
      0101 0
      0110 0
      0111 1
      1000 1
      1001 1
      1010 0
      1011 1
      1100 0
      1101 0
      1110 0
      1111 1
      .e
      ------------------------------

      The output is below, which indicates [[!p1, !p2], [p2, p2]]  from -00- and --11

      ------------------------------
      .i 4
      .o 1
      .p 2
      -00- 1
      --11 1
      .e
      ------------------------------
      */

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

/**
  *
  */
object BooleanMinimizeCNF extends BooleanMinimize(toCNF)
{
  override def apply(minTerms:List[Int], predicates:List[PredicateTerm]):PredicateTerm = super.apply(minTerms, predicates)
}

/**
  *
  */
object BooleanMinimizeDNF extends BooleanMinimize(toDNF)
{
  override def apply(minTerms:List[Int], predicates:List[PredicateTerm]):PredicateTerm = super.apply(minTerms, predicates)
}
