from z3 import *
x, t1, t2 = Reals('x t1 t2')
g = Goal()
g.add(Exists(x, And(t1 < x, x < t2)))
t = Tactic('qe')
print t(g)