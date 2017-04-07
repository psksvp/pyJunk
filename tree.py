import turtle
import math

def wormHole(depth):
    if 0 >= depth:
        return
    else:
        turtle.circle(depth)
        wormHole(depth - 20)
        

def fibonacci(depth):
    turtle.home()
    turtle.clear()
    turtle.right(45)
    turtle.fd(1)
    turtle.left(90)
    turtle.fd(1)
    turtle.left(90)
    Fn1 = 1
    Fn2 = 1
    Fn = Fn1 + Fn2
    while(Fn <= depth):
        dist = math.sqrt(Fn * Fn + Fn * Fn)
        turtle.circle(dist, 90)
        #turtle.fd(dist)
        #turtle.left(90)
        Fn2 = Fn1
        Fn1 = Fn
        Fn = Fn1 + Fn2
        
def drawFromString(S, d)
    position = []
    heading = []
    for i in range(0, len(S) - 1):
        if '0' == S[i]:
            turtle.forward(d)
        elif '1' == S[i]:
            turtle.forward(d)
        elif '[' == S[i]:
            position.append(turtle.position())
            heading.append(turtle.heading())
        elif ']' == S[i]
            pos = position.pop()
            head = hading.pop()
            turtle.penup()
            turtle.setpos(pos)
            
        