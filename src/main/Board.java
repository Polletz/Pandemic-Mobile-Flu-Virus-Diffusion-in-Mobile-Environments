/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author ricca
 */
public class Board {
    public Map<Peer, List<Peer>> adjPeers;
    private List<Peer> hotspots;
    public int PEERS_IN_HOTSPOTS = 0;
    
    Board(){
        this.adjPeers = new HashMap<Peer, List<Peer>>();
        this.hotspots = new ArrayList<Peer>();
    }
    
    void addPeer(Peer p){
        adjPeers.putIfAbsent(p, new ArrayList<Peer>());
    }
    
    void removePeer(Peer p){
        adjPeers.values().stream().forEach(e -> e.remove(p));
        adjPeers.remove(p);
    }
    
    void addEdge(Peer p1, Peer p2){
        adjPeers.get(p1).add(p2);
    }
    
    void removeEdge(Peer p1, Peer p2){
        List<Peer> eP1 = adjPeers.get(p1);
        if (eP1 != null)
            eP1.remove(p2);
    }
    
    public void generateHotspots(){
        for(int i=0;i<Parameters.NUMBER_OF_HOTSPOTS;i++){
            Peer p;
            Random r = new Random();
            int x = r.nextInt(Parameters.BOARD_WIDTH) + 1;
            int y = r.nextInt(Parameters.BOARD_HEIGHT) + 1;
            p = new Peer(x, y);
            hotspots.add(p);
            System.out.println("New Hotspot Generated -> X : " + p.POSITION.X + ", Y : " + p.POSITION.Y);
        }
    }
    
    public Peer generatePeer(){
        Random r = new Random();
        Peer p;
        // PROPORTION OF PEERS IN HOTSPOTS
        double rate = (adjPeers.isEmpty()) ? 1 : ((double) PEERS_IN_HOTSPOTS/adjPeers.size());
        // IF THE PROPORTION IS STILL ACCEPTABLE
        if(rate <= Parameters.HOTSPOT_PROPORTION)
        {
            // THIS PEER IS GOING TO BE PLACED IN AN HOTSPOTS
            int x = 0;
            int y = 0;
            // SELECTED HOTSPOT
            int hotspot = r.nextInt(Parameters.NUMBER_OF_HOTSPOTS);
            // WHILE THE COORDINATES ARE ACCEPTABLE
            while(x <= 0 || x > Parameters.BOARD_WIDTH || y <= 0 || y > Parameters.BOARD_HEIGHT)
            {
                // DIRECTION ANGLE
                int angle = r.nextInt(360) + 1;
                // DISTANCE COMPUTED WITH ZIPF'S LAW
                int distance = 1;
                while(r.nextDouble() < (Parameters.HOTSPOT_DISTANCE_PERCENTAGE/distance))
                    distance++;

                x = (int) (distance * Math.cos(angle));
                y = (int) (distance * Math.sin(angle));
            }
            // CONVERSION FROM POLAR COORDINATES TO CARTESIAN COORDINATES
            x += hotspots.get(hotspot).POSITION.X;
            y += hotspots.get(hotspot).POSITION.Y;
            p = new Peer(x, y);
            PEERS_IN_HOTSPOTS++;
        }else{
            int x = r.nextInt(Parameters.BOARD_WIDTH) + 1;
            int y = r.nextInt(Parameters.BOARD_HEIGHT) + 1;
            p = new Peer(x, y);
        }
        return p;
    }
}
