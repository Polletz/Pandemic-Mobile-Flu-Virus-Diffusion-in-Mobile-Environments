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
            // SELECTED HOTSPOT
            int hotspot = r.nextInt(Parameters.NUMBER_OF_HOTSPOTS);
            Position pos = ZipfLawGenerator(hotspot);
            
            p = new Peer(pos.X, pos.Y);
            PEERS_IN_HOTSPOTS++;
        }else{
            int x = r.nextInt(Parameters.BOARD_WIDTH) + 1;
            int y = r.nextInt(Parameters.BOARD_HEIGHT) + 1;
            p = new Peer(x, y);
        }
        return p;
    }
    
    // COMPUTE NEXT POSITION OF THE PEERS
    public void movePeers(){
        adjPeers.entrySet().forEach((entry) -> {
            movePeer(entry.getKey());
        });
    }
    
    private void movePeer(Peer p){
        int x = (p.POSITION.X <= p.DIRECTION.X) ? p.POSITION.X + Parameters.STEP : p.POSITION.X - Parameters.STEP;
        int y = (x - p.POSITION.X / p.DIRECTION.X - p.POSITION.X)*(p.DIRECTION.Y - p.POSITION.Y) + p.POSITION.Y;
        
        if(x <= 0) x = -1*x + 2;
        if(y <= 0) y = -1*y + 2;
        
        if(x > Parameters.BOARD_WIDTH) x = 2*Parameters.BOARD_WIDTH - x;
        if(y > Parameters.BOARD_HEIGHT) y = 2*Parameters.BOARD_HEIGHT - y;
        
        p.POSITION.X = x;
        p.POSITION.Y = y;
    }
    
    // COMPUTE NEXT STAGE FOR ALL PEERS
    public void nextState(){
        adjPeers.entrySet().forEach((entry) -> {
            changeState(entry.getKey());
        });
    }
    
    // CHANGE STATE FOR PEER P
    private void changeState(Peer p){
        Random r = new Random();
        double state_changing = r.nextDouble();
        
        switch(p.MOVING_STATE){
            case HALTING:
                if(state_changing < Parameters.H_TO_E)
                    moveToExploring(p);
                else if(state_changing < Parameters.H_TO_E + Parameters.H_TO_T)
                    moveToTravelling(p);
                break;
            case EXPLORING:
                if(state_changing < Parameters.E_TO_H)
                    p.MOVING_STATE = Parameters.Moving_State.HALTING;
                else if(state_changing < Parameters.E_TO_H + Parameters.E_TO_T)
                    moveToTravelling(p);
                else if(state_changing < Parameters.E_TO_E2)
                    moveToExploring(p);
                break;
            case TRAVELLING:
                if(state_changing < Parameters.T_TO_E)
                    moveToExploring(p);
                else if(state_changing < Parameters.T_TO_E + Parameters.T_TO_H)
                    p.MOVING_STATE = Parameters.Moving_State.HALTING;
                break; 
           default: break;
        }
    }
    
    private void moveToTravelling(Peer p){
        p.MOVING_STATE = Parameters.Moving_State.TRAVELLING;
        Random r = new Random();
        p.DIRECTION = new Position(r.nextInt(Parameters.BOARD_WIDTH) + 1, r.nextInt(Parameters.BOARD_HEIGHT) + 1);
    }
    
    private void moveToExploring(Peer p){
        boolean found = false;
        Random r = new Random();
        p.MOVING_STATE = Parameters.Moving_State.EXPLORING;
        for(Peer hotspot : hotspots){
            if(hotspot.hasInRadius(p)){
                // NEW POSITION IN HOTSPOT WITH ZIPF LAW
                Position pos = ZipfLawGenerator(hotspots.indexOf(hotspot));
                p.DIRECTION = pos;
                
                found = true;
                break;
            }
        }
        
        if(!found)
            // NEW POSITION RANDOM
            p.DIRECTION = new Position(r.nextInt(Parameters.BOARD_WIDTH) + 1, r.nextInt(Parameters.BOARD_HEIGHT) + 1);
    }
    
    private Position ZipfLawGenerator(int hotspot){
        // THIS PEER IS GOING TO BE PLACED IN AN HOTSPOTS
        int x = 0;
        int y = 0;
        
        Random r = new Random();
        
        // WHILE THE COORDINATES ARE ACCEPTABLE
        while(x <= 0 || x > Parameters.BOARD_WIDTH || y <= 0 || y > Parameters.BOARD_HEIGHT)
        {
            // DIRECTION ANGLE
            int angle = r.nextInt(360) + 1;
            // DISTANCE COMPUTED WITH ZIPF'S LAW
            int distance = 1;
            double par = r.nextDouble();
            while(par < (Parameters.HOTSPOT_DISTANCE_PERCENTAGE/distance))
                distance++;

            x = (int) (distance * Math.cos(angle));
            y = (int) (distance * Math.sin(angle));
        }
        
        // CONVERSION FROM POLAR COORDINATES TO CARTESIAN COORDINATES
        x += hotspots.get(hotspot).POSITION.X;
        y += hotspots.get(hotspot).POSITION.Y;
        
        return new Position(x, y);
    }
}
