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
    Parameters.OS OPERATING_SYSTEM;
    
    Peer(Position position, Parameters.OS operating_system){
        INFECTION_STATE = Parameters.Infection_State.SUSCEPTIBLE;
        MOVING_STATE = Parameters.Moving_State.HALTING;
        POSITION = position;
        OPERATING_SYSTEM = operating_system;
    }
    
    Peer(int x, int y, Parameters.OS operating_system){
        INFECTION_STATE = Parameters.Infection_State.SUSCEPTIBLE;
        MOVING_STATE = Parameters.Moving_State.HALTING;
        POSITION = new Position(x, y);
        OPERATING_SYSTEM = operating_system;
    }
    
    @Override
    public String toString(){
        String s = "X : " + POSITION.X + ", Y : " + POSITION.Y + ", INFECTION STATE : " + INFECTION_STATE + ", MOVING STATE : " + MOVING_STATE + ", DIRECTION : " + DIRECTION.X + ", " + DIRECTION.Y;
        return s;
    }
    
    boolean hasInRadius(Peer p, int radius){
        boolean result = false;
        if((p.POSITION.X - this.POSITION.X) * (p.POSITION.X - this.POSITION.X) + 
            (p.POSITION.Y - this.POSITION.Y) * (p.POSITION.Y - this.POSITION.Y)
                <= radius * radius) result = true;
        return result;
    }
}

class Position {
    int X;
    int Y;

    Position(int x, int y){
        X = x;
        Y = y;
    }
}