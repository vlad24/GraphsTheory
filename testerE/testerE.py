import random
import numpy as np
print "Generating..."
java_input = open("/root/workspace/JavaProgramming/GraphsTheory/E_LazySegmentTree/middle.in", 'w')
python_output = open("/root/workspace/JavaProgramming/GraphsTheory/E_LazySegmentTree/middle2.out", 'w')

elements_amount = 30 * 1000
queries = 30 * 1000

array = np.array(np.random.rand(elements_amount) * (10 ** 9), 'int64')
#array = [1,2,3,4,5,6]
initialSum = np.sum(array)
java_input.write(str(elements_amount) + " " + str(queries) + "\n")
print "Start params:", str(elements_amount) + " " + str(queries)
java_input.write(" ".join(list(map(lambda x : str(x), array))) + "\n")
#print array
for i in range(queries):
    if (i % 1000 == 0):
        print i, " tests already generated"
    start = random.randint(1, elements_amount)
    amount = random.randint(0, elements_amount - start)
    end = start + amount
    java_input.write(str(start) + " " + str(end) + "\n")
    # now calculate array changes
    element_sum = np.sum(array[start - 1 : end])
    #print "Query:", start - 1, end - 1
    average = float(element_sum) / (end - start + 1)
    #print "Sum:", element_sum, "Av:", average
    current_sum = np.sum(array)
    if current_sum <= initialSum:
        #print "CEILING"
        average = int(np.ceil(average)) 
    else:
        #print "FLOORING"
        average = int(np.floor(average))
    #print "New av:" , average
    array[start - 1:end] = [average] * (end - start + 1)
    #print "Updated:", array 
java_input.close()
python_output.write(" ".join(list(map(lambda x : str(x), array))))
python_output.close()
print "Generating: done"
answer = str(raw_input("Has your program worked(y/n)?"))
if answer != "n":
    java_answer = open("/root/workspace/JavaProgramming/GraphsTheory/E_LazySegmentTree/middle.out", 'r')
    python_answer = open("/root/workspace/JavaProgramming/GraphsTheory/E_LazySegmentTree/middle2.out", 'r')
    js = [line for line in java_answer]
    ps = [line for line in python_answer]
    #print js, ps
    print "Test passed:", ps == js
else:
    print "=("