/**
 * Pisa University
 * Peer to Peer Systems and Blockchains
 * Year 2019/2020
 * Paoletti Riccardo, paoletti.riccardo0@gmail.com
 * Final Project
 */
package main;

// THIS CLASS MODELS AN EDGE BETWEEN TWO PEERS
public class Edge {
    Peer source;
    Peer dest;
    int value;
    
    Edge(Peer source, Peer dest, int value){
        this.source = source;
        this.dest = dest;
        this.value = value;
    }
    
    @Override
    public String toString(){
        return source.ID + " " + dest.ID + " " + value;
    }
}
