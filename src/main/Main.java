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
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Board b = new Board();
        
        b.generateHotspots();
        
        for(int i=0;i<Parameters.NUMBER_OF_PEERS;i++){
            Peer p = b.generatePeer();
            System.out.println("New Peer Generated -> " + p.toString());
            b.addPeer(p);
        }
        
        System.out.println("Percentage of Peers spawned in hotspots : " + ((double) b.PEERS_IN_HOTSPOTS/b.adjPeers.size()));
    }
}