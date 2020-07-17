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
    
    int ID;
    Parameters.Infection_State INFECTION_STATE;
    Parameters.Moving_State MOVING_STATE;
    Position POSITION;
    Position DIRECTION;
    Parameters.OS OPERATING_SYSTEM;
    
    Peer(Position position, Parameters.OS operating_system, int id){
        ID = id;
        INFECTION_STATE = Parameters.Infection_State.SUSCEPTIBLE;
        MOVING_STATE = Parameters.Moving_State.HALTING;
        POSITION = position;
        OPERATING_SYSTEM = operating_system;
    }
    
    Peer(int x, int y, Parameters.OS operating_system, int id){
        ID = id;
        INFECTION_STATE = Parameters.Infection_State.SUSCEPTIBLE;
        MOVING_STATE = Parameters.Moving_State.HALTING;
        POSITION = new Position(x, y);
        OPERATING_SYSTEM = operating_system;
    }
    
    @Override
    public String toString(){
        String s = "ID :" + ID + ", INFECTION STATE : " + INFECTION_STATE + ", MOVING STATE : " + MOVING_STATE;
        return s;
    }
    
    boolean hasInRadius(Peer p, int radius){
        boolean result = false;
        if((p.POSITION.X - this.POSITION.X) * (p.POSITION.X - this.POSITION.X) + 
            (p.POSITION.Y - this.POSITION.Y) * (p.POSITION.Y - this.POSITION.Y)
                <= radius * radius) result = true;
        return result;
    }
    
    public Peer getCopy(){
        Peer peer = new Peer(this.POSITION, this.OPERATING_SYSTEM, this.ID);
        peer.DIRECTION=this.DIRECTION;
        peer.INFECTION_STATE=this.INFECTION_STATE;
        peer.MOVING_STATE=this.MOVING_STATE;
        return peer;
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