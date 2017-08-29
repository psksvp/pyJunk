package psksvp

object SkinkExecutor
{
  import scala.concurrent.duration.Duration
  /**
    *
    * @param filename
    * @param predicates
    * @param useO2
    * @param usePredicateAbstraction
    * @param useClang
    * @param maxIteration
    */
  def consoleRun(filename:String,
                 predicates:Seq[PredicateTerm],
                 useO2:Boolean,
                 usePredicateAbstraction:Boolean,
                 useClang:String = "clang-4.0",
                 maxIteration:Int = 20):Unit=
  {
    import au.edu.mq.comp.skink.Main
    PredicatesAbstraction.setToUsePredicates(predicates)
    val args = List("-v",
                     if(usePredicateAbstraction) "--use-predicate-abstraction" else "",
                     if(useO2) "" else "--no-O2",
                     "--use-clang", useClang,
                     "-m", maxIteration.toString,
                     filename)

    Main.main(args.filter(_.length > 0).toArray)
  }


  abstract class RunResult
  case class RunTRUE() extends RunResult
  case class RunFALSE() extends RunResult
  case class RunUNKNOWN() extends RunResult
  case class RunTIMEOUT() extends RunResult

  /**
    *
    * @param filename
    * @param predicates
    * @param useO2
    * @param usePredicateAbstraction
    * @param useClang
    * @param maxIteration
    * @return
    */
  def run(filename:String,
          predicates:Seq[PredicateTerm],
          useO2:Boolean,
          usePredicateAbstraction:Boolean,
          useClang:String = "clang-4.0",
          maxIteration:Int = 20): RunResult=
  {
    import java.io.{PrintStream, File}
    val outputPath = s"$filename.output.txt"
    if(!fileExists(outputPath))
    {
      Console.withOut(new PrintStream(new File(outputPath)))
      {
        consoleRun(filename, predicates, useO2, usePredicateAbstraction, useClang)
      }
    }
    else
      println(s"$outputPath exists, grabbing the result..")

    import sys.process._
    val result = Seq("/usr/bin/tail", "-n", "1", outputPath).!!

    if(result.trim.indexOf("TRUE") >= 0)
      RunTRUE()
    else if(result.trim.indexOf("FALSE") >= 0)
      RunFALSE()
    else
      RunUNKNOWN()
  }

  /**
    *
    * @param filePath
    * @param useO2
    * @param useClang
    * @param maxIteration
    */
  case class Code(filePath:String, useO2:Boolean, useClang:String, maxIteration:Int)

  /**
    *
    * @param runDataList
    * @param timeout
    * @param outputDir
    * @return
    */
  def runBench(runDataList:Seq[Code], timeout:Duration, outputDir:String):Seq[Seq[String]] =
  {
    for(d <- runDataList) yield
    {
      println(s"running -> $d")
//      val result = runWithTimeout[RunResult](timeout, RunTIMEOUT())
//      {
//        run(d.filePath, Nil, d.useO2, true, d.useClang, d.maxIteration)
//      }

      val result = run(d.filePath, Nil, d.useO2, true, d.useClang, d.maxIteration)
      copyFile(d.filePath, outputDir)
      copyFile(s"${d.filePath}.output.txt", outputDir)
      Seq(d.filePath.split("/").last,
          s"${d.filePath}.output.txt".split("/").last,
          result.toString())
    }
  }

  /**
    *
    * @param runDataList
    * @param timeout
    * @param outputDir
    */
  def runBenchAndOutputReport(runDataList:Seq[Code], timeout:Duration, outputDir:String):Unit =
  {
    val output = runBench(runDataList, timeout, outputDir)
    val report = makeReportHTML(output)
    import java.io.PrintWriter
    new PrintWriter(s"$outputDir/report.html")
    {
      write(report)
      close()
    }
  }


  /**
    *
    * @param output
    * @return
    */
  def makeReportHTML(output:Seq[Seq[String]]):String =
  {
    def addRow(r:Seq[String]):String =
    {
      require(3 == r.length)
      s"""
         |<tr>
         |  <th> <a href="${r.head}"> ${r.head} </a> </th>
         |  <th> <a href="${r(1)}"> ${r.last} </a> </th>
         |</tr>
       """.stripMargin
    }

    def makeTable(t:Seq[Seq[String]]):String =
    {
      if(t.isEmpty)
        ""
      else
        s"${addRow(t.head)} ${makeTable(t.tail)}"
    }

    s"""
       |<html>
       |<head>
       |<style>
       |table {
       |    width:100%;
       |}
       |table, th, td {
       |    border: 1px solid black;
       |    border-collapse: collapse;
       |}
       |th, td {
       |    padding: 5px;
       |    text-align: left;
       |}
       |table#t01 tr:nth-child(even) {
       |    background-color: #eee;
       |}
       |table#t01 tr:nth-child(odd) {
       |   background-color:#fff;
       |}
       |table#t01 th {
       |    background-color: black;
       |    color: white;
       |}
       |</style>
       |</head>
       |<body><table>
       |${makeTable(output)}
       |</body></table>
       |</html>
     """.stripMargin
  }
}
