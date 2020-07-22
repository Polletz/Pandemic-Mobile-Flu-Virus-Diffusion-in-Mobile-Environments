/**
 * Pisa University
 * Peer to Peer Systems and Blockchains
 * Year 2019/2020
 * Paoletti Riccardo, paoletti.riccardo0@gmail.com
 * Final Project
 */
package main;

// THIS CLASS CONTAINS ALL THE SIMULATION PARAMETERS
public interface Parameters {
    public static final int ITERATIONS = 200;
    
    // MEASURES SELECTED BY ME
    public static final int BOARD_HEIGHT = 100;
    public static final int BOARD_WIDTH = 100;
    public static final int INFECTION_RADIUS = 2;
    public static final int STEP = 1;
    public static final int NUMBER_OF_HOTSPOTS = 100;
    public static final double HOTSPOT_AWAY_PERCENTAGE = 0.9;
    public static final double HOTSPOT_PROPORTION = 0.7;
    public static final int HOTSPOT_RADIUS = 6;
    
    // SELECTED ACCORDING TO THE ILLNESS TO SIMULATE
    public static final int NUMBER_OF_PEERS = 60000;
    public static final double INFECTION_RATE = 0.3;
    public static final double PATCH_RATE = 0.05;
    
    // SELECTED ACCORDING TO THE CITY TO SIMULATE
    public static final double ANDROID_PERCENTAGE = 0.8548;
    public static final double IOS_PERCENTAGE = 0.1422;
    
    // POOSSIBLE STATES AND OPERATING SYSTEMS
    public static enum Infection_State{SUSCEPTIBLE, INFECTIOUS, RECOVERED}
    public static enum Moving_State{HALTING, EXPLORING, TRAVELLING}
    public static enum OS{ANDROID, IOS, OTHERS, HOTSPOT}
    
    // MOVING PROBABILITIES, TAKEN FROM THE PAPER PROVIDED BY THE TEACHER
    // CAN BE MODIFIED AS WANTED
    public static final double H_TO_H = 0.83;
    public static final double H_TO_T = 0.33;
    public static final double H_TO_E = 0.583;
    public static final double T_TO_T = 0.3;
    public static final double T_TO_E = 0.5;
    public static final double T_TO_H = 0.2;
    public static final double E_TO_E1 = 0.27;
    public static final double E_TO_E2 = 0.303;
    public static final double E_TO_T = 0.242;
    public static final double E_TO_H = 0.182;
    
    // SIZE CHOSEN ACCORDING TO THE NUMBER OF CORES OF MY PC
    public static final int THREADPOOL_SIZE = 7;
}
