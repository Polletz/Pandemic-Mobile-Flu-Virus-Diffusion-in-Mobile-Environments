/**
 * Pisa University
 * Peer to Peer Systems and Blockchains
 * Year 2019/2020
 * Paoletti Riccardo, paoletti.riccardo0@gmail.com
 * Final Project
 */
package main;

// THIS CLASS MODEL A PEER IN THE NETWORK
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
    
    // CONTROLS IF THIS PEER HAS IN ITS RADIUS THE PEER PROVIDED IN INPUT
    // THE RADIUS TO CHECK IS ALSO A PARAMETER
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

// CLASS TO MODEL A POSITION IN THE BOARDS
class Position {
    int X;
    int Y;

    Position(int x, int y){
        X = x;
        Y = y;
    }
}