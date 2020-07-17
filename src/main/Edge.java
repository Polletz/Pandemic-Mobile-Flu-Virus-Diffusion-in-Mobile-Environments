/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author ricca
 */
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
