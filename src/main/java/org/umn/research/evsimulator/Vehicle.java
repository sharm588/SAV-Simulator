package org.umn.research.evsimulator;

import lombok.Data;


import java.util.List;

import static javafx.application.Platform.exit;

@Data
public class Vehicle {
    private int batteryPercent;
    private Location loc;
    public Network net;
    private List<Link> path;
    private int counter = 0; // counts how far in path vehicle is (by counting nodes)
    private float currentTravelTime = 0; //travel time to target node
    private boolean requested = false; // checks if vehicle is assigned to passenger
    public Passenger passenger; // vehicle's assigned passenger
    public boolean notMoving = true; // check if vehicle is is motion or not
    public boolean justPickedUp = false; // checks if vehicle has just picked up assigned passenger
    public boolean pickedUp = false; // checks if passenger is in vehicle
    public boolean droppedOff = false; // checks if vehicle has dropped off assigned passenger
    public boolean alreadyAtTarget = false; // checks if vehicle is already at passenger pickup location
    public boolean noMoreRides = false; // checks if vehicle can get assigned to more passengers or not
    public boolean alreadyPrintedDropOff = false; // checks if drop off message is already printed in simulation log
    public boolean alreadyBeginningRouteToDestination = false; // is true when vehicle is already at passenger origin
    public boolean beginningRouteToPassenger = false; // is true when vehicle begins route to passenger
    public boolean idle = false; // checks if vehicle is not doing anything
    public boolean sentToNode = false; // checks if empty vehicle is sent to a different node
    public Node node; // node empty vehicle is sent to
    public boolean enRouteToNode = false; // check if vehicle is en route to empty node
    public boolean arrivedAtNode = false; // check if vehicle has arrived at empty node
    public boolean assignedSameNode = false; // checks if vehicle is assigned to the same empty node as before
    public boolean alreadyAtNode = false; // checks if vehicle is already at empty node
    public double totalDistanceTravelled = 0.0;
    public double totalTravelTime = 0.0;
    public double inVehicleTravelTime = 0.0; // travel time with passenger in vehicle
    public int numberDroppedOff = 0;
    public boolean used = false; //checks if vehicle has been used

    private int id;

    public Vehicle (Network net, int batteryPercent, Location loc) {

        this.batteryPercent = batteryPercent;
        this.loc = loc;

    }

    public void resetVariables () {

        notMoving = true;
        justPickedUp = false;
        pickedUp = false;
        droppedOff = false;
        alreadyAtTarget = false;
        noMoreRides = false;
        alreadyPrintedDropOff = false;
        alreadyBeginningRouteToDestination = false;
        beginningRouteToPassenger = false;
        idle = false;
        requested = false;
        sentToNode = false;
        node = null;
        enRouteToNode = false;
        arrivedAtNode = false;
        counter = 0;

    }

    public List<Passenger> step(List<Passenger> waitingList, Passenger P) {


        if (!this.pickedUp) {
            if (this.getCurrentTravelTime() > 30) { //move forward 30 seconds
                this.setCurrentTravelTime(this.getCurrentTravelTime() - 30);
                this.setTotalTravelTime(this.getTotalTravelTime() + 30);
            }
            else if (this.getPath().get(this.getCounter()).getDestination() == P.getOrigin()){ //if next node is destination and within 30 seconds, passenger is picked up
                this.setTotalTravelTime(this.getTotalTravelTime() + this.getCurrentTravelTime());
                this.totalDistanceTravelled += this.getPath().get(this.getCounter()).getDistance();
                this.setJustPickedUp(true);
                this.pickedUp = true;
            }
            else { //otherwise, move to next node
                Zone nextNode = net.matchLocationWithCorrespondingZone(this.getPath().get(this.getCounter()).getDestination());
                this.loc = nextNode;
                this.totalDistanceTravelled += this.getPath().get(this.getCounter()).getDistance();
                this.setTotalTravelTime(this.getTotalTravelTime() + this.getCurrentTravelTime());
                this.counter++; //counter keeps track of node index in path array list
                this.createTravelTime();
            }

        } else { // if passenger picked up, step towards destination

            if (this.getCurrentTravelTime() > 30) {
                this.setCurrentTravelTime(this.getCurrentTravelTime() - 30);
                this.setTotalTravelTime(this.getTotalTravelTime() + 30);
                this.setInVehicleTravelTime(this.getInVehicleTravelTime() + 30);
            }
            else if (this.getPath().get(this.getCounter()).getDestination() == P.getDestination()){ //set droppedOff to true for next potential passenger
                this.setDroppedOff(true);
                this.setTotalTravelTime(this.getTotalTravelTime() + this.getCurrentTravelTime());
                this.setInVehicleTravelTime(this.getInVehicleTravelTime() + this.getCurrentTravelTime());
            }
            else {
                Zone nextNode = net.matchLocationWithCorrespondingZone(this.getPath().get(this.getCounter()).getDestination());
                /*for (int i = 0; i < net.getZoneList().size(); i++) {
                    if (this.getPath().get(this.getCounter()).getDestination().getId() == net.getZoneList().get(i).getId()) {
                        nextNode = net.getZoneList().get(i);
                    }
                }*/
                this.loc = nextNode;
                this.totalDistanceTravelled += this.getPath().get(this.getCounter()).getDistance();
                this.setTotalTravelTime(this.getTotalTravelTime() + this.getCurrentTravelTime());
                this.setInVehicleTravelTime(this.getInVehicleTravelTime() + this.getCurrentTravelTime());
                this.counter++;
                this.createTravelTime();
            }
        }
        return waitingList;
    }

    public void stepTowardsEmptyNode () {

        if (this.getCounter() >= this.getPath().size() && this.getPath().size() == 1 && this.getCurrentTravelTime() < 30) { //arrived at empty node if path is only one node and travel time < 30 and if the counter is going past bounds
            this.setArrivedAtNode(true);
            this.setEnRouteToNode(false);
            this.setSentToNode(false);
            return;
        }

        if (this.getCurrentTravelTime() > 30) { //move forward 30 seconds
            this.setCurrentTravelTime(this.getCurrentTravelTime() - 30);
            this.setTotalTravelTime(this.getTotalTravelTime() + 30);
        }
        else if (this.getPath().get(this.getCounter()).getDestination() == this.getNode()){ //if next node is destination and within 30 seconds, vehicle arrived at empty node
            this.totalDistanceTravelled += this.getPath().get(this.getCounter()).getDistance();
            this.setTotalTravelTime(this.getTotalTravelTime() + this.getCurrentTravelTime());
            this.setArrivedAtNode(true);
            this.setEnRouteToNode(false);
            this.setSentToNode(false);
        }
        else { //otherwise, move to next node
            Zone nextNode = net.matchLocationWithCorrespondingZone(this.getPath().get(this.getCounter()).getDestination());
            this.loc = nextNode;
            this.totalDistanceTravelled += this.getPath().get(this.getCounter()).getDistance();
            this.setTotalTravelTime(this.getTotalTravelTime() + this.getCurrentTravelTime());
            this.counter++; //counter keeps track of node index in path array list
            this.createTravelTime();
        }
    }


    public void createPath (Location loc, Node dest) {
        Zone location = (Zone) loc;
        Node node = null;

        if (location.getId() == dest.getId() && this.isRequested()) {                                                    //check if vehicle is already at passenger location
            this.setAlreadyAtTarget(true);
        }
        /*else if (location.getId() == dest.getId() && this.isSentToNode()) {                                                    //check if vehicle is already at empty node
            this.setAlreadyAtTarget(true);
        }*/

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
