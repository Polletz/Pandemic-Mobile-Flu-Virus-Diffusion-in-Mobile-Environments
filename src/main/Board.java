/**
 * Pisa University
 * Peer to Peer Systems and Blockchains
 * Year 2019/2020
 * Paoletti Riccardo, paoletti.riccardo0@gmail.com
 * Final Project
 */
package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// THIS CLASS MODEL THE BOARD AND PERFORM THE SIMULATION
// CONTAINS ALL THE PEERS AND THE EDGES
public class Board {
    // ADIACENCY LIST OF THE PEERS
    public Map<Peer, List<Edge>> adjPeers;
    // LIST OF HOTSPOTS IN THE BOARD
    private List<Peer> hotspots;
    // STATISTICS TO UPGRADE
    Stats statistics;
    
    Board(){
        adjPeers = new HashMap<Peer, List<Edge>>();
        hotspots = new ArrayList<Peer>();
        statistics = new Stats();
    }
    
    void addPeer(Peer p){
        adjPeers.putIfAbsent(p, new ArrayList<Edge>());
        // THE INFECTED PEER IS LABELED AS INFECTED BY HIMSELF
        // -1 POINTS OUT THAT THE PEER IS INFECTED FROM THE BEGINNING, BEFORE CYCLE 0
        if(p.INFECTION_STATE==Parameters.Infection_State.INFECTIOUS)
            adjPeers.get(p).add(new Edge(p, p, -1));
    }
    
    void addEdge(Peer p1, Peer p2, int value){
        adjPeers.get(p1).add(new Edge(p1,p2, value));
    }
    
    public void nextInfectionState(int cycle){     
        ArrayList<Peer> tmp = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(Parameters.THREADPOOL_SIZE);
        
        // tmp IS AN ARRAY CONTAINING A COPY OF ALL THE PEERS IN THE NETWORK
        // IT'S USED IN READ-ONLY MODE BY THE THREADS TO CHECK IF A PEER IS NEAR OR NOT
        adjPeers.keySet().forEach((x) -> tmp.add(x.getCopy()));
        
        adjPeers.entrySet().stream()
                .filter((entry)->entry.getKey().INFECTION_STATE!=Parameters.Infection_State.RECOVERED)
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
        // DECOMMENT THIS LINE ONLY FOR AIDS SIMULATION
        //if(p.INFECTION_STATE==Parameters.Infection_State.INFECTIOUS) return;
        // IF SOMEONE IS RECOVERED IT CANNOT CHANGE STATE ANYMORE
        if(p.INFECTION_STATE==Parameters.Infection_State.RECOVERED) return;
        Random r = new Random();
        double prob = r.nextDouble();
        if(prob < Parameters.PATCH_RATE){
            if(p.INFECTION_STATE==Parameters.Infection_State.SUSCEPTIBLE){
                // IF A SUSCEPTIBLE PEER INSTALL A PATCH, IT BECOMES IMMUNED
                statistics.increaseImmunedPeers();
                statistics.increaseImmunedPeersPerCycle(cycle);
            }else{
                // IF AN INFECTED PEER INSTALL A PATCH, IT BECOMES RECOVERED
                statistics.increadRecoveredPeers();
                statistics.increaseRecoveredPeersPerCycle(cycle);
                if (!adjPeers.get(p).isEmpty()){
                    adjPeers.get(p).get(0).value = cycle;
                }
            }
            p.INFECTION_STATE = Parameters.Infection_State.RECOVERED;
            return;
        }
        // AN INFECTED PEER THAT NOT INSTALL THE PATCH CAN EXIT HERE
        if(p.INFECTION_STATE == Parameters.Infection_State.INFECTIOUS)
            return;
        
        // INFECTED PEERS THAT ARE IN THE RADIUS OF THE PEER
        // WITH THE SAME OPERATING SYSTEM
        List<Peer> infectious_peers = 
                tmp.stream()
                .filter((x) -> x.OPERATING_SYSTEM == p.OPERATING_SYSTEM)
                .filter((x) -> p.hasInRadius(x, Parameters.INFECTION_RADIUS))
                .filter((x) -> x.INFECTION_STATE == Parameters.Infection_State.INFECTIOUS)
                .collect(Collectors.toList());

        // THE PEER IS INFECTED WITH A CERTAIN PROBABILITY FOR
        // EACH INFECTED PEER IN HIS RADIUS
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
        // HOTSPOTS ARE GENERATED RANDOMLY UNIFORMLY
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
        // FIRST ALL THE SUSCEPTIBLE PEERS ARE GENERATED
        for(int i=0;i<Parameters.NUMBER_OF_PEERS-1;i++){
            Peer p = generatePeer(i);
            statistics.UpdatePeersOs(p.OPERATING_SYSTEM);
            addPeer(p);
        }
        // THEN THE INFECTED PEER IS GENERATED
        Peer p = generatePeer(Parameters.NUMBER_OF_PEERS-1);
        p.INFECTION_STATE = Parameters.Infection_State.INFECTIOUS;
        statistics.INFECTED_OS = p.OPERATING_SYSTEM;
        statistics.UpdatePeersOs(p.OPERATING_SYSTEM);
        addPeer(p);
    }
    
    private Peer generatePeer(int id){
        Random r = new Random();
        Peer p;
        
        // OS IS CHOSEN
        Parameters.OS os;
        double prob_os = r.nextDouble();
        if(prob_os < Parameters.ANDROID_PERCENTAGE)
            os = Parameters.OS.ANDROID;
        else if(prob_os < Parameters.ANDROID_PERCENTAGE + Parameters.IOS_PERCENTAGE)
            os = Parameters.OS.IOS;
        else
            os = Parameters.OS.OTHERS;

        // rate IS THE PROPORTION OF PEERS IN HOTSPOTS
        double rate = (adjPeers.isEmpty()) ? 1 : ((double) statistics.peers_in_hotspots/adjPeers.size());
        // IF THE PROPORTION IS STILL ACCEPTABLE
        if(rate <= Parameters.HOTSPOT_PROPORTION)
        {
            // THIS PEER IS GOING TO BE PLACED NEAR AN HOTSPOT
            int hotspot = r.nextInt(Parameters.NUMBER_OF_HOTSPOTS);
            // POSITION GENERATED WITH ZIPF-LAW LIKE ALGORITHM
            Position pos = ZipfLawDistanceGenerator(hotspot);
            
            p = new Peer(pos.X, pos.Y, os, id);
            statistics.peers_in_hotspots++;
        }else{
            // THIS PEER IS GOING TO BE PLACED UNIFORMLY IN THE MAP
            int x = r.nextInt(Parameters.BOARD_WIDTH) + 1;
            int y = r.nextInt(Parameters.BOARD_HEIGHT) + 1;
            p = new Peer(x, y, os, id);
        }
        int x = r.nextInt(Parameters.BOARD_WIDTH) + 1;
        int y = r.nextInt(Parameters.BOARD_HEIGHT) + 1;
        
        // INITIAL DIRECTION COINCIDES WITH THE POSITION
        // AT FIRST ITERATION WHEN THE PEER CHANGE MOVING STATE IT WILL BE CHANGED
        p.DIRECTION = new Position(x, y);
        return p;
    }
    
    public void movePeers(){
        // HERE EACH PEER IS MOVED ACCORDING TO ITS MOVING STATE
        statistics.peers_in_hotspots=0;
        ExecutorService pool = Executors.newFixedThreadPool(Parameters.THREADPOOL_SIZE);
        adjPeers.entrySet().forEach((entry) ->
        {
            pool.execute(() -> {
                // ONLY THE PEERS THAT ARE NOT HALTING ARE MOVED
                if(entry.getKey().MOVING_STATE!=Parameters.Moving_State.HALTING) 
                    movePeer(entry.getKey());
            });
        });
        
        pool.shutdown();
        try {
            pool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            System.exit(-1);
        }
    }
    
    private void movePeer(Peer p){
        double x=p.POSITION.X, y=p.POSITION.Y;
        
        // IF I HAVE TO MOVE IN ONE OF THE CARTESIAN DIRECTIONS
        // I SIMPLY SUM THE STEP TO MY POSITION
        if(x == p.DIRECTION.X){
            y += Parameters.STEP;
        }else if(y == p.DIRECTION.Y){
            x += Parameters.STEP;
        }else{
            // OTHERWISE THE TWO POINT LINE EQUATION IS USED TO PERFORM
            // A STEP TOWARD MY DIRECTION
            x = (p.POSITION.X <= p.DIRECTION.X) ? p.POSITION.X + Parameters.STEP : p.POSITION.X - Parameters.STEP;
            y = ((x - p.POSITION.X) / (double) (p.DIRECTION.X - p.POSITION.X))*(p.DIRECTION.Y - p.POSITION.Y) + p.POSITION.Y;
        }

        // IF ENCOUNTERS ONE OF THE BORDERS
        // THE BEHAVIOUR IS TO BOING IN THE OPPOSITE DIRECTION
        if(x <= 0) x = -1*x + 2;
        if(y <= 0) y = -1*y + 2;

        if(x > Parameters.BOARD_WIDTH) x = 2*Parameters.BOARD_WIDTH - x;
        if(y > Parameters.BOARD_HEIGHT) y = 2*Parameters.BOARD_HEIGHT - y;

        
        // NEW POSITION IS SET
        x = (int) x;
        y = (int) y;
        
        p.POSITION.X = (int) x;
        p.POSITION.Y = (int) y;
        
        for(Peer hotspot : hotspots){
            if(hotspot.hasInRadius(p, Parameters.HOTSPOT_RADIUS)){
                statistics.increaseHotspotPeers();
                return;
            }    
        }
    }
    
    // COMPUTE NEXT MOVING STATE FOR ALL PEERS
    public void nextState(int cycle){
        ExecutorService pool = Executors.newFixedThreadPool(Parameters.THREADPOOL_SIZE);
        
        adjPeers.entrySet().stream()
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
    
    private void changeState(Peer p){
        Random r = new Random();
        double state_changing = r.nextDouble();
        
        // DEPENDING ON WHAT MOVING STAGE A PEER IS ON
        // ACCORDING TO MOVING PROBABILITIES A NEW STATE
        // IS CHOSEN AND THE RELATIVE FUNCTION IS INVOKED
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
        Position pos;
        
        // GENERATION OF NEW DIRECTION WILL BE MADE WITH THE
        // SAME POLITICS AS THE INITIAL GENRATION OF PEER
        double rate = (adjPeers.isEmpty()) ? 1 : ((double) statistics.peers_in_hotspots/adjPeers.size());
        if(rate <= Parameters.HOTSPOT_PROPORTION)
        {
            int hotspot = r.nextInt(Parameters.NUMBER_OF_HOTSPOTS);
            pos = ZipfLawDistanceGenerator(hotspot);
        }else{
            int x = r.nextInt(Parameters.BOARD_WIDTH) + 1;
            int y = r.nextInt(Parameters.BOARD_HEIGHT) + 1;
            pos = new Position(x, y);
        }
        p.DIRECTION = pos;
    }
    
    private void moveToExploring(Peer p){
        boolean found = false;
        Random r = new Random();
        p.MOVING_STATE = Parameters.Moving_State.EXPLORING;
        // IF THE PEER IS IN AN HOTSPOT, THE NEW DIRECTION IS CHOSEN
        // WITHIN THE SAME HOTSPOT WITH THE SAME LAW AS BEFORE
        for(Peer hotspot : hotspots){
            if(hotspot.hasInRadius(p, Parameters.HOTSPOT_RADIUS)){
                Position pos = ZipfLawDistanceGenerator(hotspots.indexOf(hotspot));
                p.DIRECTION = pos;
                
                found = true;
                break;
            }
        }
        // OTHERWISE NEW DIRECTION IS SELECTED UNIFORMLY
        if(!found)
            p.DIRECTION = new Position(r.nextInt(Parameters.BOARD_WIDTH) + 1, r.nextInt(Parameters.BOARD_HEIGHT) + 1);
    }
    
    // CUSTOM MADE ZIPF LAW-LIKE FUNCTION TO GENERATE A NEW POSITION
    private Position ZipfLawDistanceGenerator(int hotspot){
        int x = 0;
        int y = 0;
        
        Random r = new Random();
        
        // POLAR COORDINATES
        int angle;
        int distance;
        
        while(x <= 0 || x > Parameters.BOARD_WIDTH || y <= 0 || y > Parameters.BOARD_HEIGHT)
        {
            // ANGLE IS CHOSEN UNIFORMLY
            angle = r.nextInt(360) + 1;
            // DISTANCE COMPUTED WITH ZIPF'S LAW-LIKE METHOD
            distance = 1;
            double par = r.nextDouble();
            while(r.nextDouble() > (Parameters.HOTSPOT_AWAY_PERCENTAGE/distance) && distance < Parameters.HOTSPOT_RADIUS)
                distance++;
            
            // FROM POLAR COORDINATES TO CARTESIAN COORDINATES
            x = (int) (distance * Math.cos(angle));
            y = (int) (distance * Math.sin(angle));
            x += hotspots.get(hotspot).POSITION.X;
            y += hotspots.get(hotspot).POSITION.Y;
        }
        return new Position(x, y);
    }

    // PRINT NODES TO FILE
    public void createNodesFile(int proof) throws FileNotFoundException{
        try (PrintStream writetoFile = new PrintStream(new File("nodes_"+proof+".json"))) {
            writetoFile.println("{");
            adjPeers.keySet().forEach((p) -> {
                writetoFile.println("\"" + p.ID + "\"" + ":{\"infection_state\":\"" + p.INFECTION_STATE +
                        "\",\"moving_state\":\"" + p.MOVING_STATE + "\",\"OS\":\"" + p.OPERATING_SYSTEM + "\"},");
            });
            writetoFile.println("}");
        }
    }
    
    // PRINT EDGES TO FILE
    public void createEdgesFile(int proof) throws FileNotFoundException{
        try (PrintStream writetoFile = new PrintStream(new File("edges_"+proof+".txt"))) {
            adjPeers.entrySet().forEach((entry) -> {
                entry.getValue().forEach((p) -> {
                    writetoFile.println(p.toString());
                });
            });
        }
    }
}