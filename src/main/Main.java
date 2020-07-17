/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.FileNotFoundException;

/**
 *
 * @author ricca
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Board b = new Board();
        b.generateHotspots();
        b.generatePeers();
        
        for(int i=0;i<Parameters.ITERATIONS;i++){ 
            b.nextState(i);
            b.movePeers(i);
            b.nextInfectionState(i);
        }
        
        b.statistics.printStatistics(System.out);
        try {
            b.createNodesFile();
            b.createEdgesFile();
        } catch (FileNotFoundException ex) {
            System.exit(-1);
        }
    }
}