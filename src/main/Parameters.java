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
public interface Parameters {
    
    public static final int ITERATIONS = 200;
    
    // RANDOM MEASURES SELECTED BY ME
    public static final int BOARD_HEIGHT = 100;
    public static final int BOARD_WIDTH = 100;
    public static final int INFECTION_RADIUS = 2;
    public static final int STEP = 1;
        
    public static final double INFECTION_RATE = 0.3;
    public static final double PATCH_RATE = 0.05;
    public static final double DEATH_RATE = 0.01;
    
    // TAKEN BY MARKET SHARE
    public static final double ANDROID_PERCENTAGE = 0.85;
    public static final double IOS_PERCENTAGE = 0.1328;
    public static final double OTHERS_PERCENTAGE = (1.0 - (ANDROID_PERCENTAGE + IOS_PERCENTAGE));
    
    // RANDOM MEASURE SELECTED BY ME
    public static final int NUMBER_OF_PEERS = 60000;
    
    // POOSSIBLE STATES
    public static enum Infection_State{SUSCEPTIBLE, INFECTIOUS, RECOVERED}
    public static enum Moving_State{HALTING, EXPLORING, TRAVELLING}
    public static enum OS{ANDROID, IOS, OTHERS, HOTSPOT}
    
    // RANDOM MEASURES SELECTED BY ME
    public static final int NUMBER_OF_HOTSPOTS = 150;
    public static final double HOTSPOT_AWAY_PERCENTAGE = 0.9;
    public static final double HOTSPOT_PROPORTION = 0.7;
    public static final int HOTSPOT_RADIUS = 6;
    
    // RANDOM MEASURES SELECTED BY ME
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
}
