import os
from time import sleep

from networkx.generators.small import tutte_graph

import matplotlib.pyplot as plt
import networkx as nx


n = 100

def show(graph):
    positions = nx.spring_layout(graph)
    nx.draw(graph, pos=positions)
    nx.draw_networkx_labels(graph, positions, font_size=14)
    plt.show()
    

def fill_file(graph, filename):
    string = str(graph.edges())
    string = string.replace(" ", "")
    string = string.replace("(", "")
    string = string.replace("[", "")
    string = string.replace("]", "")
    string = string.replace(")", "")
    string_edges = string.split(",")
    filename.write(str(len(graph.nodes())) + " " + str(len(string_edges)//2) + "\n")
    for i in xrange(0, len(string_edges), 2):
        a = int(string_edges[i]); b = int(string_edges[i+1])
        filename.write(str(a) + " " + str(b) + "\n")
    print "File ready"
#graph = nx.complete_graph(n)
#graph = nx.dorogovtsev_goltsev_mendes_graph(n)

def randgraph():
    import random
    n = 15
    k = 1 + 1
    start_vert = range(1, n)
    graph = nx.DiGraph()
    for i in xrange(1,k):
        end_vert = list(start_vert)
        random.shuffle(end_vert)
        new_edges = zip(start_vert,end_vert)
        graph.add_edges_from(list(new_edges))
    print "done"
    show(graph)
    return graph
      
print "PYTHON WORKING"        
filename = open(".\\topsort.in", 'w')
graph = randgraph()
fill_file(graph, filename)
filename.close()
#java_out = open(".\\topsort.out", 'r')

