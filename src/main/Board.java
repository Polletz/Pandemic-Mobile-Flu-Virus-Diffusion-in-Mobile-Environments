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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author ricca
 */
public class Board {
    public Map<Peer, List<Edge>> adjPeers;
    private List<Peer> hotspots;
    Stats statistics;
    
    Board(){
        adjPeers = new HashMap<Peer, List<Edge>>();
        hotspots = new ArrayList<Peer>();
        statistics = new Stats();
    }
    
    void addPeer(Peer p){
        adjPeers.putIfAbsent(p, new ArrayList<Edge>());
    }
    
    void removePeer(Peer p){
        adjPeers.remove(p);
    }
    
    void addEdge(Peer p1, Peer p2, int value){
        adjPeers.get(p1).add(new Edge(p1,p2, value));
    }
    
    public void nextInfectionState(int cycle){     
        ArrayList<Peer> tmp = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(8);
        
        adjPeers.keySet().forEach((x) -> tmp.add(x.getCopy()));
        
        adjPeers.entrySet().stream()
                .filter((x) -> x.getKey().INFECTION_STATE != Parameters.Infection_State.RECOVERED)
                .forEach((entry) -> {
                    pool.execute(() -> {
                        changeInfectionState(entry.getKey(), tmp, cycle);
                    });
        });
        
        pool.shutdown();
        try {
            pool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            System.exit(-1);
        }
    }
    
    private void changeInfectionState(Peer p, ArrayList<Peer> tmp, int cycle){
        Random r = new Random();
        double prob = r.nextDouble();
        if(prob < Parameters.PATCH_RATE){
            
            if(p.INFECTION_STATE==Parameters.Infection_State.SUSCEPTIBLE){
                statistics.increaseImmunedPeers();
                statistics.increaseImmunedPeersPerCycle(cycle);
            }else{
                statistics.increadRecoveredPeers();
                statistics.increaseRecoveredPeersPerCycle(cycle);
            }
            
            p.INFECTION_STATE = Parameters.Infection_State.RECOVERED;
            List<Edge> edges = adjPeers.get(p);
            if(!edges.isEmpty())
                edges.get(0).value = cycle;
            return;
        }
        if(p.INFECTION_STATE == Parameters.Infection_State.INFECTIOUS)
            return;
        
        List<Peer> infectious_peers = 
                tmp.stream()
                .filter((x) -> x.OPERATING_SYSTEM == p.OPERATING_SYSTEM)
                .filter((x) -> p.hasInRadius(x, Parameters.INFECTION_RADIUS))
                .filter((x) -> x.INFECTION_STATE == Parameters.Infection_State.INFECTIOUS)
                .collect(Collectors.toList());

        for(Peer peer : infectious_peers){
            if(r.nextDouble() < Parameters.INFECTION_RATE){
                p.INFECTION_STATE = Parameters.Infection_State.INFECTIOUS;
                statistics.increaseInfectedPeers();
                statistics.increaseInfectedPeersPerCycle(cycle);
                addEdge(p, peer, cycle);
                return;
            }
        }
    }
    
    public void generateHotspots(){
        for(int i=0;i<Parameters.NUMBER_OF_HOTSPOTS;i++){
            Peer p;
            Random r = new Random();
            int x = r.nextInt(Parameters.BOARD_WIDTH) + 1;
            int y = r.nextInt(Parameters.BOARD_HEIGHT) + 1;
            Parameters.OS os = Parameters.OS.HOTSPOT;
            p = new Peer(x, y, os, i);
            hotspots.add(p);
        }
    }
    
    public void generatePeers(){
        for(int i=0;i<Parameters.NUMBER_OF_PEERS-1;i++){
            Peer p = generatePeer(i);
            statistics.UpdatePeersOs(p.OPERATING_SYSTEM);
            addPeer(p);
        }
        
        Peer p = generatePeer(Parameters.NUMBER_OF_PEERS-1);
        p.INFECTION_STATE = Parameters.Infection_State.INFECTIOUS;
        statistics.INFECTED_OS = p.OPERATING_SYSTEM;
        statistics.UpdatePeersOs(p.OPERATING_SYSTEM);
        addPeer(p);
    }
    
    private Peer generatePeer(int id){
        Random r = new Random();
        Peer p;
        
        Parameters.OS os;
        double prob_os = r.nextDouble();
        if(prob_os < Parameters.ANDROID_PERCENTAGE)
            os = Parameters.OS.ANDROID;
        else if(prob_os < Parameters.ANDROID_PERCENTAGE + Parameters.IOS_PERCENTAGE)
            os = Parameters.OS.IOS;
        else
            os = Parameters.OS.OTHERS;

        // PROPORTION OF PEERS IN HOTSPOTS
        double rate = (adjPeers.isEmpty()) ? 1 : ((double) statistics.peers_spawned_near_hotspots/adjPeers.size());
        // IF THE PROPORTION IS STILL ACCEPTABLE
        if(rate <= Parameters.HOTSPOT_PROPORTION)
        {
            // SELECTED HOTSPOT
            int hotspot = r.nextInt(Parameters.NUMBER_OF_HOTSPOTS);
            Position pos = ZipfLawDistanceGenerator(hotspot);
            
            p = new Peer(pos.X, pos.Y, os, id);
            statistics.peers_spawned_near_hotspots++;
        }else{
            int x = r.nextInt(Parameters.BOARD_WIDTH) + 1;
            int y = r.nextInt(Parameters.BOARD_HEIGHT) + 1;
            p = new Peer(x, y, os, id);
            statistics.peers_spawned_random++;
        }
        int x = r.nextInt(Parameters.BOARD_WIDTH) + 1;
        int y = r.nextInt(Parameters.BOARD_HEIGHT) + 1;

        p.DIRECTION = new Position(x, y);
        return p;
    }
    
    // COMPUTE NEXT POSITION OF THE PEERS
    public void movePeers(int cycle){
        ExecutorService pool = Executors.newFixedThreadPool(8);
        adjPeers.entrySet().forEach((entry) ->
        {
            pool.execute(() -> {
                if(entry.getKey().MOVING_STATE!=Parameters.Moving_State.HALTING) movePeer(entry.getKey(), cycle);
            });
        });
        
        pool.shutdown();
        try {
            pool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            System.exit(-1);
        }
    }
    
    private void movePeer(Peer p, int cycle){
        double x=p.POSITION.X, y=p.POSITION.Y;
        
        if(x == p.DIRECTION.X){
            y += Parameters.STEP;
        }else if(y == p.DIRECTION.Y){
            x += Parameters.STEP;
        }else{
            x = (p.POSITION.X <= p.DIRECTION.X) ? p.POSITION.X + Parameters.STEP : p.POSITION.X - Parameters.STEP;
            y = ((x - p.POSITION.X) / (double) (p.DIRECTION.X - p.POSITION.X))*(p.DIRECTION.Y - p.POSITION.Y) + p.POSITION.Y;
        }

        if(x <= 0) x = -1*x + 2;
        if(y <= 0) y = -1*y + 2;

        if(x > Parameters.BOARD_WIDTH) x = 2*Parameters.BOARD_WIDTH - x;
        if(y > Parameters.BOARD_HEIGHT) y = 2*Parameters.BOARD_HEIGHT - y;

        x = (int) x;
        y = (int) y;
        
        p.POSITION.X = (int) x;
        p.POSITION.Y = (int) y;
        
        for(int i=0;i<Parameters.NUMBER_OF_HOTSPOTS;i++){
            if(hotspots.get(i).hasInRadius(p, Parameters.HOTSPOT_RADIUS))
                statistics.increasePopulationInHotspotPerCycle(cycle, i);
        }
    }
    
    // COMPUTE NEXT STAGE FOR ALL PEERS
    public void nextState(int cycle){
        ExecutorService pool = Executors.newFixedThreadPool(8);
        
        adjPeers.entrySet().stream()
                .filter((x) -> x.getKey().INFECTION_STATE != Parameters.Infection_State.RECOVERED)
                .forEach((entry) -> {
                    pool.execute(() -> {
                        changeState(entry.getKey());
                    });
        });
        
        pool.shutdown();
        try {
            pool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            System.exit(-1);
        }
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
            if(hotspot.hasInRadius(p, Parameters.HOTSPOT_RADIUS)){
                // NEW POSITION IN HOTSPOT WITH ZIPF LAW
                Position pos = ZipfLawDistanceGenerator(hotspots.indexOf(hotspot));
                p.DIRECTION = pos;
                
                found = true;
                break;
            }
        }
        
        if(!found)
            // NEW POSITION RANDOM
            p.DIRECTION = new Position(r.nextInt(Parameters.BOARD_WIDTH) + 1, r.nextInt(Parameters.BOARD_HEIGHT) + 1);
    }
    
    private Position ZipfLawDistanceGenerator(int hotspot){
        // THIS PEER IS GOING TO BE PLACED IN AN HOTSPOTS
        int x = 0;
        int y = 0;
        
        Random r = new Random();
        
        int angle;
        int distance;
        // WHILE THE COORDINATES ARE ACCEPTABLE
        while(x <= 0 || x > Parameters.BOARD_WIDTH || y <= 0 || y > Parameters.BOARD_HEIGHT)
        {
            // DIRECTION ANGLE
            angle = r.nextInt(360) + 1;
            // DISTANCE COMPUTED WITH ZIPF'S LAW
            distance = 1;
            double par = r.nextDouble();
            while(r.nextDouble() > (Parameters.HOTSPOT_DISTANCE_PERCENTAGE/distance))
                distance++;

            x = (int) (distance * Math.cos(angle));
            y = (int) (distance * Math.sin(angle));
            
            // CONVERSION FROM POLAR COORDINATES TO CARTESIAN COORDINATES
            x += hotspots.get(hotspot).POSITION.X;
            y += hotspots.get(hotspot).POSITION.Y;
        }
        return new Position(x, y);
    }
}