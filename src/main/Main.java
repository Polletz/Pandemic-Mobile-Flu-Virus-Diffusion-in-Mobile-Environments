/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.Random;

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
        
        
        for(int i=0;i<Parameters.NUMBER_OF_PEERS;i++){
            Peer p = b.generatePeer();
//            System.out.println("New Peer Generated -> " + p.toString());
            b.addPeer(p);
        }
        
        System.out.println("Percentage of Peers spawned in hotspots : " + ((double) b.PEERS_IN_HOTSPOTS/b.adjPeers.size()));
        
        Peer p = b.generatePeer(Parameters.Infection_State.INFECTIOUS);
        b.addPeer(p);
        
        for(int i=0;i<20;i++){ 
            b.nextState();
            b.movePeers();
            b.nextInfectionState();
        }
        
        System.out.println("Android : " + b.adjPeers.entrySet().stream()
                .filter((x) -> x.getKey().OPERATING_SYSTEM == Parameters.OS.ANDROID)
                .count());   
        
        System.out.println("Infectious : " + b.adjPeers.entrySet().stream()
                .filter((x) -> x.getKey().INFECTION_STATE == Parameters.Infection_State.INFECTIOUS)
                .count());   
        
        System.out.println("Recovered : " + b.adjPeers.entrySet().stream()
                .filter((x) -> x.getKey().INFECTION_STATE == Parameters.Infection_State.RECOVERED && x.getKey().OPERATING_SYSTEM == Parameters.OS.ANDROID)
                .count());     
    }
}