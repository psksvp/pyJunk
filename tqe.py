from z3 import *
a, five = Ints('a five')
cmp = Bool('cmp')

                              
j = Goal()
j.add(Exists([five, cmp],   And(five == a,
                                cmp == (five < 1000), 
                                False == cmp)))                              
                              
t = Tactic('qe')

print(t(j))

"""from z3 import *
b,c,x = Reals('b c x')
f = Exists(x, b*x+c==0);
tac = Tactic('qe')
tac = With(tac, qe_nonlinear=True)
print tac.param_descrs() 
print tac(f).as_expr();"""