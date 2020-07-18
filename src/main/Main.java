/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 *
 * @author ricca
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        for(int j=0;j<5;j++){
            
            Board b = new Board();
            b.generateHotspots();
            b.generatePeers();
            
            for(int i=0;i<Parameters.ITERATIONS;i++){ 
                b.nextState(i);
                b.movePeers(i);
                b.nextInfectionState(i);
            }

            try {
                PrintStream ps = new PrintStream(new FileOutputStream("stats_" + j + ".out"));
                b.statistics.printStatistics(ps);
                b.createNodesFile(j);
                b.createEdgesFile(j);
            } catch (FileNotFoundException ex) {
                System.exit(-1);
            }
        }
    }
}