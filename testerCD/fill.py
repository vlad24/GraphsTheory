import random
import numpy as np
print "Generating..."
java_input = open("/root/workspace/JavaProgramming/GraphsTheory/D_Fenwick/sum.in", 'w')
python_output = open("/root/workspace/JavaProgramming/GraphsTheory/D_Fenwick/sum2.out", 'w')
n = 100000
k = 100000
letters = ['A', 'Q']
java_input.write(str(n) + " " + str(k) + "\n")
l = np.zeros(1 + n, dtype='int64');
for i in range(k):
    if i % 100 == 0:
        print i, " tests have been generated"
    command = random.choice(letters)
    if command == 'Q':
        element = random.randint(1, n)
        offset = random.randint(0, n - element)
        string_sum = str(np.sum(l[element:element+offset+1]))
        #print "sum query:", element, element+offset, "/", string_sum
        java_input.write(str(command) + " " + str(element) + " " + str(element + offset) + " \n")
        if not (i == k - 1):
            python_output.write(string_sum + "\n")
        else:
            print "!!!!!"
            python_output.write(string_sum)
    else:
        element = random.randint(1, n)
        new_value = 10**6 - element
        l.itemset(element, new_value)
        #print "upd query:",element,new_value,"/",l
        java_input.write(str(command) + " " + str(element) + " " + str(new_value) + "\n")
java_input.close()
python_output.close()
print "Generating: done"
answer = str(raw_input("Has your program worked(y/n)?"))
if answer != "n":
    java_sort = open("/root/workspace/JavaProgramming/GraphsTheory/D_Fenwick/sum.out", 'r')
    python_sort = open("/root/workspace/JavaProgramming/GraphsTheory/D_Fenwick/sum2.out", 'r')
    js = [line for line in java_sort]
    ps = [line for line in python_sort]
    print len(js)
    print len(ps)
    print "Test passed:", ps == js
else:
    print "=("

    
