package psksvp

/**
  * Created by psksvp@gmail.com on 22/4/17.
  */
//http://sce2.umkc.edu/csee/hieberm/281_new/lectures/quine-McCluskey.html
//https://en.wikipedia.org/wiki/Quine–McCluskey_algorithm
object booleanMinimize2
{
  type Group = Map[Int, Set[Implicant]]
  type Table = Map[(Implicant, Int), Set[Boolean]]
  /**
    * https://en.wikipedia.org/wiki/Implicant
    * @param terms
    * @param binary
    */
  case class Implicant(terms:Seq[Int], binary:Seq[Char])
  {
    def size:Int = terms.length
    def literalCount:Int = binary.count(_ != '_')
  }

  def vector(length:Int):Vector[Int] = Vector.range(0, length)

  /**
    *
    * @param outputTerms either max terms or min terms
    * @param dontCareTerms
    * @param numberOfVariables
    */
  def apply(outputTerms:Seq[Int],
            dontCareTerms:Seq[Int],
            numberOfVariables:Int):Set[Implicant]=
  {
    val combinedTerms = combineTerms(outputTerms ++ dontCareTerms, numberOfVariables)
    val primeImplicants = primeImplicantTerms(combinedTerms)
    //primeImplicants
    essentialPrimeImplicantTerms(primeImplicants, outputTerms)
  }

  /**
    * // might need this one https://en.wikipedia.org/wiki/Petrick%27s_method
    * @param primeImplicants
    * @param terms
    * @return
    */
  def essentialPrimeImplicantTerms(primeImplicants:Set[Implicant], terms:Seq[Int]):Set[Implicant]=
  {
    /**
      *
      * @param table
      * @return
      */
    def reduceDominances(table: Table): Table =
    {
      /**
        *
        * @param table
        * @return
        */
      def reducedRows(table: Table): Table =
      {
        val (pi, terms) = table.keys.unzip

        /**
          *
          * @param a
          * @param b
          * @return
          */
        def dominating(a:Int, b:Int):Int =
        {
          def computeCoverOfTerm(j:Int, byTerm:Int):(Int, Int) =
          {
            val p = for(t <- pi.toSeq) yield (table((t, j)), table((t, byTerm)))
            val countJ = p.count(Set(true) == _._1)
            val countK = p.count{ case (x, y) => x == Set(true) && x == y }
            (countJ, countK)
          }

          val (ac, bc) = computeCoverOfTerm(a, byTerm = b)
          if(ac == bc)
            b
          else
          {
            val (bc, ac) = computeCoverOfTerm(b, byTerm = a)
            if(ac == bc)
              a
            else
              -1
          }
        }

        val combinations = terms.toSeq.combinations(2)
        val dominators = (for(v <- combinations) yield dominating(v(0), v(1))).filter( _ != -1).toSet
        val rows = for(t <- pi; c <- dominators) yield (t, c)

        table -- rows
      }


      /**
        *
        * @param table
        * @return
        */
      def reduceColumes(table: Table): Table =
      {
        val (pi, terms) = table.keys.unzip

        def dominating(a:Implicant, b:Implicant): Implicant =
        {
          def computeCoverOfImplicant(j:Implicant, byImplicant:Implicant):(Int, Int) =
          {
            val p = for (m <- terms.toSeq) yield (table((j, m)), table((byImplicant, m)))
            val countA = p.count(Set(true) == _._1)
            val countC = p.count{case (x, y) => x == Set(true) && x == y}
            (countA, countC)
          }

          val (ac1, bc1) = computeCoverOfImplicant(a, byImplicant = b)
          val (bc2, ac2) = computeCoverOfImplicant(b, byImplicant = a)

          if(ac1 == bc1 && bc2 == ac2) // co-covering
          {
            if(a.literalCount > b.literalCount)
              b
            else
              a
          }
          else if(ac1 == bc1)
            b
          else if(ac2 == bc2)
            a
          else
            Implicant(Seq(), Seq())
        }

        val combinations = pi.toSeq.combinations(2)
        val dominators = (for(v <- combinations) yield dominating(v(0), v(1))).filter( _.size > 0).toSet
        val cols = for(m <- terms; t <- dominators) yield (t, m)

        table -- cols
      }
      reduceColumes(reducedRows(table))
    }

    /**
      *
      * @param table
      * @return
      */
    def reduceEssentialImplicants(table: Table): (Set[Implicant], Table) =
    {
      lazy val (pi, terms) = table.keySet.unzip

      lazy val essentialTerms: Set[Implicant] =
      {
        def implicantsOfMinTerm(min: Int): Set[Implicant] =
        {
          for (t <- pi if Set(true) == table((t, min))) yield t
        }

        def run(ls: List[Int]): Set[Implicant] = ls match
        {
          case Nil       => Set()
          case m :: rest => val t = implicantsOfMinTerm(m)
                            if (1 == t.size)  //exactly 1
                              t ++ run(rest)
                            else
                              run(rest)
        }

        val et = run(terms.toList)
        if(et.isEmpty) // NOTE: Pick one randomly for now if empty or pick the one on the list
          Set(pi.toIndexedSeq(scala.util.Random.nextInt(pi.size)))
        else
          et
      }

      lazy val reducedTable: Table =
      {
        val rowMarks = for (m <- terms; t <- essentialTerms if Set(true) == table((t, m))) yield m
        val row = for (m <- rowMarks; i <- pi) yield (i, m)
        val col = for (t <- essentialTerms; m <- terms) yield (t, m)
        table -- col -- row
      }

      (essentialTerms, reducedTable)
    }

    def reduceImplicant(table:Table):(Set[Implicant], Table) =
    {
      val (e, t) = reduceEssentialImplicants(table)
      (e, reduceDominances(t))
    }

    def fixedPointRun(table:Table):Set[Implicant]=
    {
      if(table.isEmpty)
        Set()
      else
      {
        val (e, t) = reduceImplicant(table)
        if(t == table)
          e
        else
          e ++ fixedPointRun(t)
      }

    }

    ///////////////////////////
    val initialTable:Table =
    {
      val pairs = for (t <- primeImplicants; m <- terms) yield
                  {
                    if (t.terms.contains(m))
                      (t, m) -> true
                    else
                      (t, m) -> false
                  }
      pairs.groupBy(_._1).map{ case (k, v) => (k, v.map(_._2)) }
    }

    ///////////////////////////
    fixedPointRun(initialTable)
  }

  /**
    *
    * @param lsCombinedTerms
    * @return
    */
  def primeImplicantTerms(lsCombinedTerms:Seq[Group]):Set[Implicant]=
  {
    /**
      *
      * @param t1
      * @param wasCombinedIntoGroup
      * @return
      */
    def check(t1:Implicant, wasCombinedIntoGroup:Group):Boolean =
    {
      /**
        *
        * @param t1
        * @param wasCombinedWith
        * @return
        */
      def check(t1:Implicant, wasCombinedWith:Implicant):Boolean = t1.terms.toSet subsetOf wasCombinedWith.terms.toSet

      //////////////////////////////////////////
      val r = for(s <- wasCombinedIntoGroup.values; t <- s) yield check(t1, wasCombinedWith = t)
      r.reduce(_ | _)
    }

    /**
      *
      * @param group
      * @param checkWithGroup
      * @return
      */
    def primeImplicantOf(group:Group, checkWithGroup:Group):Set[Implicant] =
    {
      val r = for(s <- group.values;
                  t <- s if false == check(t, wasCombinedIntoGroup = checkWithGroup)) yield t

      r.toSet
    }

    /////////////////////////////////////////
    lsCombinedTerms match
    {
      case Nil       => Set()
      case g :: Nil  => g.values.reduce(_ ++ _)
      case g :: rest => primeImplicantOf(g, checkWithGroup = rest.head) ++  primeImplicantTerms(rest)
    }
  }

  /**
    *
    * @param minTerms
    * @param numberOfVariables
    * @return
    */
  def combineTerms(minTerms:Seq[Int],
                   numberOfVariables:Int):Seq[Group]=
  {
    def fixedPointRun(termGroup:Group,
                      order:Int):List[Group]=
    {
      if(termGroup.isEmpty)
        Nil
      else
      {
        val newTermGroup = combineTermIn(termGroup, order)
        termGroup :: fixedPointRun(newTermGroup, order * 2)
      }
    }

    //////////////////////////////////////////
    val term = (for(m <- minTerms)yield Implicant(m :: Nil, psksvp.binaryString(m, numberOfVariables).toCharArray)).toSet //.groupBy(countOnes(_))
    val termGroup1 = term.groupBy(countOnes(_))
    fixedPointRun(termGroup1, 2).filter(_.isEmpty == false)
  }

  /**
    *
    * @param t
    * @return
    */
  def countOnes(t: Implicant):Int = t.binary.count('1' == _)


  /**
    *
    * @param a
    * @param b
    * @return
    */
  def differences(a:Seq[Char], b:Seq[Char]):(Int, Seq[Char])=
  {
    require(a.length == b.length)
    val diff = vector(a.length).map{i => if(a(i) == b(i)) a(i) else '_'}
    val count = vector(a.length).map{i => if(a(i) == b(i)) 0 else 1}
    (count.sum, diff)
  }

  /**
    * NOTE::: NEED TO CHECK again, I don't want member LIKE (1,2) and (2, 1)
    * @param lsA
    * @param lsB
    * @tparam U
    * @tparam V
    * @return
    */
  def combination[U, V](lsA:Set[U],
                        lsB:Set[V]):Seq[(U, V)] = (for(a <- lsA; b <- lsB) yield (a, b)).toSeq


  /**
    *
    * @param group
    * @return
    */
  def combineTermIn(group:Group, size:Int):Group =
  {
    /**
      *
      * @param pairs
      * @return
      */
    def combine(pairs:Seq[(Implicant, Implicant)]):Seq[Implicant] =
    {
      /**
        *
        * @param t
        * @param withImplicate
        * @return
        */
      def combine(t:Implicant, withImplicate:Implicant):Implicant =
      {
        val (hammingDist, diff) = differences(t.binary, withImplicate.binary)
        if(1 == hammingDist)
          Implicant((t.terms.toList ::: withImplicate.terms.toList).sorted, diff)
        else
          t
      }

      ///////////////////////////////////////
      vector(pairs.length).map{i => combine(pairs(i)._1, withImplicate = pairs(i)._2)}
    }

    ///////////////////////////////////////
    val implicants = group.keySet.flatMap
    {
      key => if(group.isDefinedAt(key + 1))
               combine(combination(group(key), group(key+1)))
             else
               Nil
    }

    implicants.filter(_.terms.length == size).groupBy(countOnes(_))
  }


  /**
    *
    */
  def test():Unit=
  {
    import au.edu.mq.comp.smtlib.theories.{Core, IntegerArithmetics}
    import au.edu.mq.comp.smtlib.typedterms.Commands
    import au.edu.mq.comp.smtlib.interpreters.Resources
    object resources extends Resources
    object Cmds extends Commands
    object logics extends IntegerArithmetics with Core
    import logics._

    def toImplicant(term:Int, numberOfVariables:Int):Implicant =
    {
      Implicant(Seq(term), psksvp.binaryString(term, numberOfVariables).toCharArray)
    }

    def toImplicants(terms:Seq[Int],
                     numberOfVariables:Int):Seq[Implicant] = for(t <- terms) yield toImplicant(t, numberOfVariables)


    def toProduct(implicant:Implicant, variables:Seq[BooleanTerm]):BooleanTerm=
    {
      val p = vector(implicant.binary.length).map
              {
                i => if('1' == implicant.binary(i)) variables(i)
                     else if('0' == implicant.binary(i)) !variables(i)
                     else False()
              }

      p.filter(_ != False()).reduce(_ & _)
    }

    def toProductOfSum(implicants:Seq[Implicant], variables:Seq[BooleanTerm]):BooleanTerm=
    {
      val p = for(i <- implicants) yield toProduct(i, variables)
      p.reduce(_ | _)
    }

    def toProductOfSumExpression(terms:Seq[Int], variables:Seq[BooleanTerm]):BooleanTerm =
    {
      toProductOfSum(toImplicants(terms, variables.length), variables)
    }

    def check(name:String, outputTerms:Seq[Int], dontCareTerms:Seq[Int],variables:Seq[BooleanTerm]):Boolean=
    {
      val m1 = psksvp.booleanMinimize2(outputTerms, dontCareTerms, numberOfVariables = variables.length)
      val m1F = toProductOfSumExpression(outputTerms ++ dontCareTerms, variables)
      val m1R = toProductOfSum(m1.toSeq, variables)
      val em1 = true//psksvp.equivalence(m1F, m1R)
      println("---------------------------------")
      println(s"$name ok -> $em1")
      println(termAsInfix(m1F))
      println(termAsInfix(m1R))
      println(m1)
      println("---------------------------------")
      em1
    }

    val a = Bools("a")
    val b = Bools("b")
    val c = Bools("c")
    val d = Bools("d")

//    check("m1", List(4, 8, 10, 11, 12, 15), List(9, 14), List(a, b, c, d))
//    check("m2", List(0, 9, 13, 15), List(7, 12), List(a, b, c, d))
//    check("m3", List(0, 2, 5, 6, 7, 8, 10, 12, 13, 14, 15), Nil, List(a, b, c, d))
//    check("m4", List(2, 5, 6, 11, 12, 14, 15), List(0, 3, 4), List(a, b, c, d))
//    check("m5", List(0, 1, 2, 5, 6, 7), Nil, List(a, b, c))
//
//    check("m6",
//           outputTerms = List(0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13),
//           dontCareTerms = Nil,
//           variables = List(a, b, c, d))

//    check("m7",
//           outputTerms = List(2, 3, 7, 9, 11, 13),
//           dontCareTerms = List(1, 10, 15),
//           variables = List(a, b, c, d))


    //https://webdocs.cs.ualberta.ca/~amaral/courses/329/webslides/Topic5-QuineMcCluskey/sld007.htm
//    check("m8",
//           outputTerms = List(0, 1, 2, 5, 6, 7, 8, 9, 10, 14),
//           dontCareTerms = Nil,
//           variables = List(a, b, c, d))
//
//    //https://webdocs.cs.ualberta.ca/~amaral/courses/329/webslides/Topic5-QuineMcCluskey/sld096.htm
//    check("m9",
//           outputTerms = List(0, 1, 2, 5, 6, 7),
//           dontCareTerms = Nil,
//           variables = List(a, b, c))
//
//    check("m10",
//           outputTerms = List(0, 1, 4, 5),
//           dontCareTerms = Nil,
//           variables = List(a, b, c))
//
//    check("m11",
//           outputTerms = List(1, 2, 3),
//           dontCareTerms = Nil,
//           variables = List(a, b))

//    check("m12",
//          outputTerms = List(0, 2, 8, 10, 12, 13, 14, 15),
//          dontCareTerms = List(5, 7),
//          variables = List(a, b, c ,d))


//    check("m13",
//           outputTerms = List(4, 5, 6, 8, 10, 13),
//           dontCareTerms = Nil,
//           variables = List(a, b, c ,d))

    check("m14",
           outputTerms = List(0, 1, 2, 5, 6, 7, 8, 9, 10, 14),
           dontCareTerms = Nil,
           variables = List(a, b, c ,d))


//    println("abuse")
//
//
//    val terms = List.fill(600)(scala.util.Random.nextInt(8192)).distinct
//    println(terms)
//    println(terms.length)
//    val start = System.currentTimeMillis()
//    val m10 = psksvp.Math.booleanMinimize(terms, Nil, 13)
//    println(System.currentTimeMillis() - start)
//    println(m10)
  }
}
