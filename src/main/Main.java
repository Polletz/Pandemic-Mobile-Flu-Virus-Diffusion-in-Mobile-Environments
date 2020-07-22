/**
 * Pisa University
 * Peer to Peer Systems and Blockchains
 * Year 2019/2020
 * Paoletti Riccardo, paoletti.riccardo0@gmail.com
 * Final Project
 */
package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main {
    public static void main(String[] args){
        // 5 executions are performed and then the average results are taken
        for(int j=0;j<5;j++){
            Board b = new Board();
            b.generateHotspots();
            b.generatePeers();
            for(int i=0;i<Parameters.ITERATIONS;i++){
                b.nextState(i); // change moving state for each peer
                b.movePeers(); // move all peers that are not halting
                b.nextInfectionState(i); // compute new infection state of each peer
            }
            // save results of execution
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