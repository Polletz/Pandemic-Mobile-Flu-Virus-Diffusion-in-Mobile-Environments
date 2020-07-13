/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author ricca0
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DirectedGraph g = createGraph();
        System.out.println(g.printGraph());
        System.out.println("Breadth First : ");
        for(String s : g.breadthFirstTraversal("Alice")){
            System.out.print(s + " - ");
        }
        System.out.println();
        System.out.println("Depth First : ");
        for(String s : g.depthFirstTraversal("Alice")){
            System.out.print(s + " - ");
        }
        System.out.println();
    }
    
    static DirectedGraph createGraph() {
        DirectedGraph graph = new DirectedGraph();
        graph.addVertex("Bob");
        graph.addVertex("Alice");
        graph.addVertex("Mark");
        graph.addVertex("Rob");
        graph.addVertex("Maria");
        graph.addEdge("Bob", "Alice");
        graph.addEdge("Bob", "Rob");
        graph.addEdge("Alice", "Mark");
        graph.addEdge("Rob", "Mark");
        graph.addEdge("Alice", "Maria");
        graph.addEdge("Rob", "Maria");
        return graph;
    }
}