import numpy as np
import random
from time import sleep

def calculate_black(array): 
    count = 0
    black_length = 0
    blacken = False
    for i in range(len(array)):
        if not blacken:
            if not array[i]:
                count += 1
                black_length += 1
                blacken = True
        else:
            if not array[i]:
                black_length += 1
                blacken = True
            else:
                blacken = False
    return count, black_length

print "Generating..."
java_input = open("C:\Users\Vladislav\Programming\JavaProgramming\SE Projects\GraphsTheory\F_Painter\painter.in", 'w')
python_output = open("C:\Users\Vladislav\Programming\JavaProgramming\SE Projects\GraphsTheory\F_Painter\painter2.out", 'w')

half_size = 500000
queries = 100000 + 1
verbose = True

array = np.ones(half_size + 1 + half_size, 'int64')
#array = [1,2,3,4,5,6]
colors = ['W', 'B']
java_input.write(str(queries) + "\n")
print "Start params:", str(half_size) + " " + str(queries)
#print array
for i in range(queries):
    if ((i < 5) or ((i < 100) and (i % 10 == 0)) or ((i < 1000) and (i % 100 == 0)) or (i % 1000 == 0)):
        print "~~~", i, " queries have been already generated."
    start = random.randint(-half_size, half_size - 1)
    leng = random.randint(1, half_size - start)
    color_number = random.randint(0,1)
    color = 'B'
    if color_number:
        color = 'W'
    java_input.write(str(color) + " " + str(start) + " " + str(leng) + "\n")
    array[half_size + start:half_size + start+leng] = [color_number] * leng
    count, black_length = calculate_black(array)
    #print "Note:", count, black_length
    if (i == queries - 1):
        python_output.write(str(count) + " " + str(black_length))
    else:
        python_output.write(str(count) + " " + str(black_length) + "\n")
java_input.close()
python_output.close()
print "Generating: done"
if verbose:
    answer = str(raw_input("Has your program worked(y/n)?"))
else:
    print "Ten seconds to launch your program."
    sleep(10)
    print "Checking..."
    answer = "y"
if answer != "n":
    java_answer = open("C:\Users\Vladislav\Programming\JavaProgramming\SE Projects\GraphsTheory\F_Painter\painter.out", 'r')
    python_answer = open("C:\Users\Vladislav\Programming\JavaProgramming\SE Projects\GraphsTheory\F_Painter\painter2.out", 'r')
    js = [line for line in java_answer]
    ps = [line for line in python_answer]
    #print js, ps
    print "ACCEPTED:", ps == js
else:
    print "=("