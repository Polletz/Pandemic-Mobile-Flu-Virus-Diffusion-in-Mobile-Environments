/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ricca
 */
public class Stats {
    
    Parameters.OS INFECTED_OS;
    
    int android_peers=0;
    int ios_peers=0;
    int others_peers=0;
    
    int peers_spawned_near_hotspots=0;
    int peers_spawned_random=0;
    
    int infected_peers=1;
    int immuned_peers=0;
    int recovered_peers=0;
    int dead_peers=0;
    
    ArrayList<Integer> infected_peers_per_cycle;
    ArrayList<Integer> recovered_peers_per_cycle;
    ArrayList<Integer> immuned_peers_per_cycle;
    
    HashMap<Integer, ArrayList<Integer>> population_in_hotspots_per_cycle;
    
    Stats(){
        immuned_peers_per_cycle = new ArrayList<>(Parameters.ITERATIONS);
        for(int i=0;i<Parameters.ITERATIONS;i++)
            immuned_peers_per_cycle.add(0);
        infected_peers_per_cycle = new ArrayList<>(Parameters.ITERATIONS);
        for(int i=0;i<Parameters.ITERATIONS;i++)
            infected_peers_per_cycle.add(0);
        population_in_hotspots_per_cycle = new HashMap<>(Parameters.ITERATIONS);
        for(int i=0;i<Parameters.ITERATIONS;i++){
            population_in_hotspots_per_cycle.put(i, new ArrayList<Integer>(Parameters.NUMBER_OF_HOTSPOTS));
            for(int j=0; j<Parameters.NUMBER_OF_HOTSPOTS;j++)
                population_in_hotspots_per_cycle.get(i).add(0);
        }
        recovered_peers_per_cycle = new ArrayList<>();
        for(int i=0;i<Parameters.ITERATIONS;i++)
            recovered_peers_per_cycle.add(0);
    }
    
    void UpdatePeersOs(Parameters.OS OS){
        switch(OS){
            case ANDROID:
                android_peers++;
                break;
            case IOS:
                ios_peers++;
                break;
            case OTHERS:
                others_peers++;
                break;
        }
    }

    synchronized void increaseInfectedPeersPerCycle(int cycle){
        int tmp = infected_peers_per_cycle.get(cycle);
        infected_peers_per_cycle.set(cycle, tmp+1);
    }
    
    synchronized void increaseRecoveredPeersPerCycle(int cycle){
        int tmp = recovered_peers_per_cycle.get(cycle);
        recovered_peers_per_cycle.set(cycle, tmp+1);
    }
    
    synchronized void increaseImmunedPeersPerCycle(int cycle){
        int tmp = immuned_peers_per_cycle.get(cycle);
        immuned_peers_per_cycle.set(cycle, tmp+1);
    }
    
    synchronized void increasePopulationInHotspotPerCycle(int cycle, int hotspot){
        int tmp = population_in_hotspots_per_cycle.get(cycle).get(hotspot);
        population_in_hotspots_per_cycle.get(cycle).set(hotspot, tmp+1);
    }

    synchronized void increaseImmunedPeers(){
        immuned_peers++;
    }
    
    synchronized void increadRecoveredPeers(){
        infected_peers--;
        recovered_peers++;
    }
    
    synchronized void increaseInfectedPeers(){
        infected_peers++;
    }
    
    synchronized void printStatistics(PrintStream ps){
        ps.println("Stats");
        ps.println();
        ps.println("Infected OS : " + INFECTED_OS);
        ps.println();
        ps.println("Population with Android : " + android_peers);
        ps.println("Population with IOS : " + ios_peers);
        ps.println("Population with OTHERS : " + others_peers);
        ps.println();
        ps.println("Peers spawned near hotspots : " + peers_spawned_near_hotspots);
        ps.println("Peers spawned randomly : " + peers_spawned_random);
        ps.println();
        ps.println("Infected Peers : " + infected_peers);
        ps.println("Recovered Peers : " + recovered_peers);
        ps.println("Immuned Peers : " + immuned_peers);
        ps.println();
        ps.println("Infected Peers per cycle : ");
        for(int i=0;i<Parameters.ITERATIONS;i++)
            ps.println("Cycle -> " + i + ", Infected -> " + infected_peers_per_cycle.get(i));
        ps.println();
        ps.println("Recovered Peers per cycle : ");
        for(int i=0;i<Parameters.ITERATIONS;i++)
            ps.println("Cycle -> " + i + ", Recovered -> " + recovered_peers_per_cycle.get(i));
        ps.println();
        ps.println("Immuned Peers per cycle : ");
        for(int i=0;i<Parameters.ITERATIONS;i++)
            ps.println("Cycle -> " + i + ", Immuned -> " + immuned_peers_per_cycle.get(i));
        ps.println();
//        ps.println("Population in Hotspot per cycle : ");
//        for(int i=0;i<Parameters.ITERATIONS;i++){
//            ps.println("Cycle -> " + i);
//            for(int j=0;j<Parameters.NUMBER_OF_HOTSPOTS;j++)
//                ps.println("\tHotspot : " + j + ", Peers : " + population_in_hotspots_per_cycle.get(i).get(j));
//        }
    }
}
