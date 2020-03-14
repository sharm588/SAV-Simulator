package org.umn.research.evsimulator;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Passenger {
    private Node origin;
    private Node destination;
    private int departuretime;
    public boolean assigned = false;

    public Passenger(Node origin, Node destination, int departuretime) {
        this.destination = destination;
        this.origin = origin;
        this.departuretime = departuretime;
    }

    public String toString() {
        return "Origin id: " + Integer.toString(origin.getId()) + " destination id: " + Integer.toString(destination.getId()) + " Departure time: " + Integer.toString(departuretime);
    }

}
//use the hashmap from network class to store in origin and destination nodes
//ast 0 correspond to 7am
//node origin destination and departure time from demand profile and pick value between two intervals

//ast and demand values and create random value of either surrounding integer
//math.rand if greater than decimal make another passenger if not then don't
//create list of passengers in network class
//map from id to start and stop times with value of start and stop times
//change all array to arraylist
