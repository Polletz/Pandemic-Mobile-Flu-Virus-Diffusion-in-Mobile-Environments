import networkx as nx
import json
import matplotlib.pyplot as plt
import networkx.drawing


def Average(lst):
    return sum(lst) / len(lst)


city_list = ("Istanbul", "MexicoCity", "NewYork", "Tokyo")
illness_list = ("COVID", "HIV-AIDS", "SPANISH FLU")

for city in city_list:
    print(city + " : ")
    for illness in illness_list:
        print("\t" + illness + " : ")
        # path = city + "/" + illness + "/nodes_4.json"

        # d = json.load(open(path))

        G = nx.Graph()

        # for id in d.keys():
        #     G.add_node(id, infection_state=d[str(id)]["infection_state"], moving_state=d[str(id)]["moving_state"], OS=d[str(id)]["OS"])

        path = city + "/" + illness + "/edges_1.txt"
        file1 = open(path, 'r')
        Lines = file1.readlines()
        for line in Lines:
            tokens = line.split(" ")
            G.add_node(tokens[0])
            G.add_node(tokens[1])
            G.add_edge(tokens[0], tokens[1], weight=int(tokens[2]))

        lst = list()
        for tuple in G.degree():
            lst.append(tuple[1])
        print("\t\t" + "Average node degree : " + str(Average(lst)))
        # print(nx.clustering(G))
        avg_clus = nx.average_clustering(G)
        print("\t\t" + "Average clustering coefficient : %.10f" % avg_clus)
        if nx.is_connected(G):
            print("\t\t" + "The graph is connected, dimeter = " + str(nx.diameter(G)))
        else:
            print("\t\t" + "The graph is not connected, diameter is infinite")
        density = nx.density(G)
        print("\t\t" + "Edge density : %.10f" % density)
