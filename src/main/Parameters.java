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
    // RANDOM MEASURES SELECTED BY ME
    public static final int BOARD_HEIGHT = 100;
    public static final int BOARD_WIDTH = 100;
    public static final int RADIUS = 5;
    
    // TAKEN BY MARKET SHARE
    public static final double ANDROID_PERCENTAGE = 0.85;
    public static final double IOS_PERCENTAGE = 0.1328;
    public static final double OTHERS_PERCENTAGE = (1.0 - (ANDROID_PERCENTAGE + IOS_PERCENTAGE));
    
    // RANDOM MEASURE SELECTED BY ME
    public static final int NUMBER_OF_PEERS = 200;
    
    // POOSSIBLE STATES
    public static enum Infection_State{SUSCEPTIBLE, INFECTIOUS, RECOVERED}
    public static enum Moving_State{HALTING, EXPLORING, TRAVELLING}
    
    // RANDOM MEASURES SELECTED BY ME
    public static final int NUMBER_OF_HOTSPOTS = 5;
    public static final double HOTSPOT_DISTANCE_PERCENTAGE = 0.4;
    public static final double HOTSPOT_PROPORTION = 0.3;
    
    // RANDOM MEASURES SELECTED BY ME
    public static final double H_TO_H = 1/12;
    public static final double H_TO_T = 4/12;
    public static final double H_TO_E = 7/12;
    public static final double T_TO_T = 3/10;
    public static final double T_TO_E = 5/10;
    public static final double T_TO_H = 2/10;
    public static final double E_TO_E1 = 9/33;
    public static final double E_TO_E2 = 10/33;
    public static final double E_TO_T = 8/33;
    public static final double E_TO_H = 6/33;
}
