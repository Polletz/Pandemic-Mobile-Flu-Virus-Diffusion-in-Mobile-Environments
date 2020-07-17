import networkx as nx
import json
import matplotlib.pyplot as plt
import networkx.drawing

path = "nodes.json"

d = json.load(open(path))

G = nx.Graph()

for id in d.keys():
    G.add_node(id, infection_state=d[str(id)]["infection_state"], moving_state=d[str(id)]["moving_state"], OS=d[str(id)]["OS"])

path = "edges.txt"
file1 = open(path, 'r')
Lines = file1.readlines()
for line in Lines:
    tokens = line.split(" ")
    G.add_edge(tokens[0], tokens[1], weight=int(tokens[2]))

# for edge in G.edges(data=True):
#     print(edge)

# for node in G.nodes(data=True):
#     print(node[1]["infection_state"])

# print(list(G.degree()))
# print(nx.clustering(G))
print(nx.average_clustering(G))
# print(nx.diameter(G))
print(nx.is_connected(G))
# print(nx.is_strongly_connected(G))
print(nx.density(G))

nx.draw(G, with_labels=True, font_weight='bold')
plt.show()
