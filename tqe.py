from z3 import *
i, zero, one, a, add, two, inc, sub = Ints('i zero one a add two inc sub')
cmp = Bool('cmp')

                              
j = Goal()
j.add(Exists([one, two, sub, cmp],   And(one == a,
                                     two == i,
                                     sub == (two - 1),
                                     cmp == (sub != one))))                              
                              
t = Tactic('qe')

print(t(j))