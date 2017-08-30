package psksvp

import psksvp.logics._

/**
  *
  */
trait TermComposer
{
  def combinationToTerm(combination: Int, predicates: Seq[PredicateTerm]): PredicateTerm

  def gamma(absDomain: AbstractDomain,
            predicates: Seq[PredicateTerm],
            simplify: Boolean): PredicateTerm
}

/**
  *
  */
object CNFComposer extends TermComposer
{
  def combinationToTerm(combination: Int,
                        predicates: Seq[PredicateTerm]): PredicateTerm =
  {
    val bin = binaryString(combination, predicates.length)
    val exprLs = for (i <- bin.indices) yield if (bin(i) == '1') predicates(i) else !predicates(i)
    exprLs.reduce(_ | _)
  }

  def gamma(absDomain: AbstractDomain,
            predicates: Seq[PredicateTerm],
            simplify: Boolean): PredicateTerm =
  {
    if (absDomain.size == math.pow(2, predicates.length).toInt)
      False() // short cut for CNF
    else if (absDomain.isEmpty)
      True() // short cut  for CNF
    else
    {
      if (!simplify)
      {
        val exprLs =for (i <- absDomain.indices) yield combinationToTerm(absDomain(i), predicates)
        exprLs.par.reduce(_ & _)
      }
      else
      {
        BooleanMinimizeCNF(absDomain.toList, predicates.toList)
      }
    }
  }

  /**
    *
    */
  object DNFComposer extends TermComposer
  {
    def combinationToTerm(combination: Int,
                          predicates: Seq[PredicateTerm]): PredicateTerm =
    {
      val bin = binaryString(combination, predicates.length)
      val exprLs = for (i <- bin.indices) yield if (bin(i) == '1') predicates(i) else !predicates(i)
      exprLs.reduce(_ & _)
    }

    def gamma(absDomain: AbstractDomain,
              predicates: Seq[PredicateTerm],
              simplify: Boolean): PredicateTerm =
    {
      if (absDomain.size == math.pow(2, predicates.length).toInt)
        True() // short cut for DNF
      else if (absDomain.isEmpty)
        False() // short cut  for DNF
      else
      {
        if (!simplify)
        {
          val exprLs = for (i <- absDomain.indices) yield combinationToTerm(absDomain(i), predicates)
          exprLs.reduce(_ | _) // DNF
        }
        else
        {
          BooleanMinimizeDNF(absDomain.toList, predicates.toList)
        }
      }
    }
  }
}
