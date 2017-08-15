package psksvp

object SkinkExecutor
{
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
                 predicates:Seq[BooleanTerm],
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
          predicates:Seq[BooleanTerm],
          useO2:Boolean,
          usePredicateAbstraction:Boolean,
          useClang:String = "clang-4.0",
          maxIteration:Int = 20):String=
  {
    import java.io.{PrintStream, File}
    val outputFile = new File(filename + ".output.txt")
    Console.withOut(new PrintStream(outputFile))
    {
      consoleRun(filename, predicates, useO2, usePredicateAbstraction, useClang)
    }
    import sys.process._
    val result = Seq("/usr/bin/tail", "-n", "2", filename + ".output.txt").!!
    result.trim
  }

  /**
    *
    * @param filePaths
    * @param useO2
    * @param useClang
    * @param usePredicateAbstraction
    */
  def runBunch(filePaths:Seq[String],
               useO2:Boolean,
               useClang:String,
               usePredicateAbstraction:Boolean):Unit =
  {
    for(path <- filePaths)
    {
      print(s"running:$path")
      val result = run(path, Nil, useO2, usePredicateAbstraction, useClang)
      println(s"-----> $result")
    }
  }


  /**
    *
    * @param filePath
    * @param useO2
    * @param useClang
    * @param maxIteration
    */
  case class VerifyCode(filePath:String, useO2:Boolean, useClang:String, maxIteration:Int)

  /**
    *
    * @param runDataList
    * @param timeout
    * @param outputDir
    * @return
    */
  def runBench(runDataList:Seq[VerifyCode], timeout:Long, outputDir:String):Seq[Seq[String]] =
  {
    for(d <- runDataList) yield
    {
      val result = runWithTimeout(timeout, "timeout")
      {
        run(d.filePath, Nil, d.useO2, true, d.useClang, d.maxIteration)
      }
      copyFile(d.filePath, outputDir)
      copyFile(d.filePath.replace("*.c", ".ll"), outputDir)
      copyFile(s"${d.filePath}.output.txt", outputDir)
      Seq(d.filePath.split("/").last,
          d.filePath.replace("*.c", ".ll").split("/").last,
          s"${d.filePath}.output.txt".split("/").last,
          result)
    }
  }

  /**
    *
    * @param runDataList
    * @param timeout
    * @param outputDir
    */
  def runBenchOutputReport(runDataList:Seq[VerifyCode], timeout:Long, outputDir:String):Unit =
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
      require(4 == r.length)
      s"""
         |<tr>
         |  <th> <a href="$r(0)"> $r(0) </a> </th>
         |  <th> <a href="$r(1)"> $r(1) </a> </th>
         |  <th> <a href="$r(2)"> ${r.last} <a> <th>
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
       |<table style="width:100%">
       |${makeTable(output)}
       |</table>
     """.stripMargin
  }
}
