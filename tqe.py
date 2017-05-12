from z3 import *
i, i0, i1, zero, one, a, add, two, inc = Ints('i i0 i1 zero one a add two inc')
cmp = Bool('cmp')

g = Goal()

g.add(Exists([cmp, zero], And(zero == i, 
                              cmp == (zero <= 10), 
                              one == a)))
                              
j = Goal()
j.add(Exists([add, two, inc],    And(add == one + 1,
                                     a == add,
                                     two == i0,
                                     inc == two + 1,
                                     i1 == inc)))                              
                              
t = Tactic('qe')
print(t(g))
print(t(j))