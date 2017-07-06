package psksvp.ADT

import psksvp.BooleanTerm

/**
  * Created by psksvp on 21/6/17.
  */
trait AbstractInterpreter[CONCRETE, ABSTRACT]
{
  def alpha(c:CONCRETE):Seq[ABSTRACT]
  def gamma(a:Seq[ABSTRACT]):CONCRETE
}

//class PredicatesAbstraction(predicates:Seq[BooleanTerm]) extends AbstractInterpreter[BooleanTerm, Int]
//{
//  override def alpha(c: BooleanTerm): Seq[Int] =
//  {
//    0
//  }
//
//  override def gamma(a: Seq[Int]): BooleanTerm =
//  {
//
//  }
//}
