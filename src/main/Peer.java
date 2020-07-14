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
public class Peer {
    
    Parameters.Infection_State INFECTION_STATE;
    Parameters.Moving_State MOVING_STATE;
    Position POSITION;
    Position DIRECTION;
    
    Peer(Position position){
        INFECTION_STATE = Parameters.Infection_State.SUSCEPTIBLE;
        MOVING_STATE = Parameters.Moving_State.HALTING;
        POSITION = position;
    }
    
    Peer(int x, int y){
        INFECTION_STATE = Parameters.Infection_State.SUSCEPTIBLE;
        MOVING_STATE = Parameters.Moving_State.HALTING;
        POSITION = new Position(x, y);
    }
    
    @Override
    public String toString(){
        String s = "X : " + POSITION.X + ", Y : " + POSITION.Y;
        return s;
    }
    
    class Position {
        int X;
        int Y;
        
        Position(int x, int y){
            X = x;
            Y = y;
        }
    }
    
    void move(){
        /*TODO
        1. Choose new state according to probabilities
        2. Choose new direction
        3. Move to this direction
        */
        
        
    }
}
