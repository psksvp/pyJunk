package psksvp

object PredicatesFilter
{
  /**
    *
    * @param predicates
    * @return
    */
  def breakOrTerm(predicates:Set[PredicateTerm]):Set[PredicateTerm] =
  {
    import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax.OrTerm
    val r = for(t <- predicates) yield
      {
        t.termDef match
        {
          case r:OrTerm => psksvp.SMTLIB.breakOrTerm(t.typeDefs, r).toSet
          case _        => Set(t)
        }
      }
    r.reduceLeft(_ union _)
  }

  /**
    *
    * @param predicates
    * @return
    */
  def reduceToEqualTerms(predicates:Set[PredicateTerm]):Set[PredicateTerm] =
  {
    import au.edu.mq.comp.smtlib.parser.Analysis
    import au.edu.mq.comp.smtlib.parser.SMTLIB2Syntax._
    import au.edu.mq.comp.smtlib.theories.BoolTerm
    import au.edu.mq.comp.smtlib.typedterms.TypedTerm
    var rm:Set[PredicateTerm] = Set()
    val pairs = for(i <- predicates; j <- predicates if i != j) yield (i, j)
    val rt = for((t1, t2) <- pairs) yield
      {
        val a = Analysis(t1.termDef).ids
        val defs = for(SortedQId(x, s) <- t1.typeDefs if a.contains(SimpleQId(x))) yield SortedQId(x,s)
        (t1.termDef, t2.termDef) match
        {
          case (GreaterThanEqualTerm(a1, a2), LessThanEqualTerm(b1, b2))
            if(a1 == b1 && a2 == b2) => rm = rm ++ List(t1, t2)
                                        Set(TypedTerm[BoolTerm, Term](defs, EqualTerm(a1, a2)))

          case (LessThanEqualTerm(b1, b2), GreaterThanEqualTerm(a1, a2))
            if(a1 == b1 && a2 == b2) => rm = rm ++ List(t1, t2)
                                        Set(TypedTerm[BoolTerm, Term](defs, EqualTerm(a1, a2)))

          case  _                    => Set(t1, t2)
        }
      }
    rt.reduceLeft(_ union _) -- rm
  }

  /**
    *
    * @param predicates
    * @return
    */
  def reduceToSuperSetTerms(predicates:Set[PredicateTerm]):Set[PredicateTerm] =
  {
    import au.edu.mq.comp.smtlib.interpreters.SMTLIBInterpreter
    import psksvp.ADT.FixedPoint
    implicit val solver = new SMTLIBInterpreter(solverFromName("Z3"))

    def subsetOf(predicate:PredicateTerm, fromSet:Set[PredicateTerm])
                (implicit solver:SMTLIBInterpreter):Set[PredicateTerm] =
    {
      for(p <- fromSet if !(p eq predicate) && subsetCheck(p, withSuperSet = predicate)) yield p
    }
    def test(a:Set[PredicateTerm], b:Set[PredicateTerm]) = a == b
    def step(a:Set[PredicateTerm]):Set[PredicateTerm] =
    {
      if(a.isEmpty)
        Set[PredicateTerm]()
      else
      {
        //freaking state change for now.
        var i = 0
        var subset = Set[PredicateTerm]()
        val totest = a.toIndexedSeq
        while(i < a.size && subset.isEmpty)
        {
          subset = subsetOf(totest(i), fromSet = a)
          i = i + 1
        }
        a -- subset
      }
    }

    val r = FixedPoint(test, step).run(predicates)
    solver.destroy()
    r
  }
}
