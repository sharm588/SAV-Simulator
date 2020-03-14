package org.umn.research.evsimulator;

import lombok.Data;


import java.util.ArrayList;
import java.util.List;

import static javafx.application.Platform.exit;

@Data
public class Vehicle {
    private int batteryPercent;
    private Location loc;
    public Network net;
    private List<Link> path;
    private int counter = 0;
    private float currentTravelTime = 0;
    private boolean requested = false;
    public int assignedPassenger = 0;
    public Passenger passenger;
    public boolean notMoving = true;
    public boolean justPickedUp = false;
    public boolean pickedUp = false;
    public boolean droppedOff = false;
    public boolean alreadyAtTarget = false;
    public boolean noMoreRides = false;
    public boolean alreadyPrintedDropOff = false;
    public boolean alreadyBeginningRouteToDestination = false;
    public boolean alreadyBeginningRouteToPassenger = false;
    public boolean idle = false;
    public double totalDistanceTraveled = 0.0;

    private int id;

    public Vehicle (Network net, int batteryPercent, Location loc) {

        this.batteryPercent = batteryPercent;
        this.loc = loc;

    }

    public List<Passenger> step (List<Passenger> waitingList, List<Node> nodesList, Passenger P) {                                   //assumes there is only one passenger on waiting list

        if (!this.pickedUp) {
            if (this.getCurrentTravelTime() > 30) { //move forward 30 seconds
                this.setCurrentTravelTime(this.getCurrentTravelTime() - 30);
            }
            else if (this.getPath().get(this.getCounter()).getDestination() == P.getOrigin()){ //if next node is destination and within 30 seconds, passenger is picked up
                this.setJustPickedUp(true);
                this.pickedUp = true;
            }
            else { //otherwise, move to next node
                Zone nextNode = net.matchLocationWithCorrespondingZone(this.getPath().get(this.getCounter()).getDestination());
                this.loc = nextNode;
                this.totalDistanceTraveled += this.getPath().get(this.getCounter()).getDistance();
                this.counter++; //counter keeps track of node index in path array list
                this.createTravelTime();
            }

        } else { // if passenger picked up, step towards destination

            if (this.getCurrentTravelTime() > 30) {
                this.setCurrentTravelTime(this.getCurrentTravelTime() - 30);
            }
            else if (this.getPath().get(this.getCounter()).getDestination() == P.getDestination()){ //set droppedOff to true for next potential passenger
                this.setDroppedOff(true);
            }
            else {
                Zone nextNode = net.matchLocationWithCorrespondingZone(this.getPath().get(this.getCounter()).getDestination());
                /*for (int i = 0; i < net.getZoneList().size(); i++) {
                    if (this.getPath().get(this.getCounter()).getDestination().getId() == net.getZoneList().get(i).getId()) {
                        nextNode = net.getZoneList().get(i);
                    }
                }*/
                this.loc = nextNode;
                this.totalDistanceTraveled += this.getPath().get(this.getCounter()).getDistance();
                this.counter++;
                this.createTravelTime();
            }
        }


        return waitingList;

    }


    public void createPath (Location loc, Node dest) {
        Zone location = (Zone) loc;
        Node node = null;

        if (location.getId() == dest.getId() && this.isRequested()) {                                                                                //check if vehicle is already at passenger location
            this.setAlreadyAtTarget(true);
        }

        for (int i = 0; i < net.getNodesList().size(); i++) {
                                                                                                                        //match vehicle location with node to get outgoing nodes
            if (location.getId() == net.getNodesList().get(i).getId()) {
                node = net.getNodesList().get(i);
            }
        }
        this.setPath(net.shortestPath(node, dest));
    }

    public void createTravelTime () {
        try {
            this.setCurrentTravelTime(this.getPath().get(this.getCounter()).getTraveltime());
        }
        catch (IndexOutOfBoundsException e) {

            exit();
        }
    }
}
//org.umn.research.evsimulator.Location class that could be either node or link
//instanceofto check if location is node or link
//interface location
//create linklocation class with link, with link and tt as a tuple

//when to pick up passenger

//Pass waiting list to step fucntion

//function to create vehicles based off paramters with random location

//Step function (go forward on it's parth in terms of time), Stop,

//If it is less than 30 seconds away from the final destination then the vehicle stops on the current step,
//one passneger per vehicle

//The time increment in simulate is how many seconds the vehicle steps in terms of the travel time of the link


//create a random location of the vehicle
//Drop off and pick up in seperate v steps
//pass waiting list as a parameter (zone waiting list)
//Have a way of a passenger to assign vehicles simplified
//Pick up
//Have a field of assign for all nodes doing it


//Missing Cases


//fix the step method
//fix the simulate method
//fix the make vehicle method
//create a list of zones
