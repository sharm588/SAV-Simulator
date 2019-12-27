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
    private boolean pickedUp = false;
    private int id;

    public Vehicle (Network net, int batteryPercent, Location loc) {

        this.batteryPercent = batteryPercent;
        this.loc = loc;

    }

    public List<Passenger> step (List<Passenger> waitingList, List<Node> nodesList, Passenger P) {                                   //assumes there is only one passenger on waiting list
        Node node = null;                                                                                               //this is a temporary fix; need to change use of node so it is an instance variable of a Vehicle so movement of a Vehicle is tracked
        if (!this.pickedUp) {
            if (this.getCurrentTravelTime() > 30) {
                this.setCurrentTravelTime(this.getCurrentTravelTime() - 30);
            }
            else if (this.getPath().get(this.getCounter()).getDestination() == P.getOrigin()){
                this.pickedUp = true;
            }
            else {
                Zone z = null;
                for (int i = 0; i < net.getZoneList().size(); i++) {
                    if (this.getPath().get(this.getCounter()).getDestination().getId() == net.getZoneList().get(i).getId()) {
                        z = net.getZoneList().get(i);
                    }
                }
                this.loc = z;
                this.counter++;
                this.createTravelTime();
            }

        }
        else
        {

            if (this.getCurrentTravelTime() > 30) {
                this.setCurrentTravelTime(this.getCurrentTravelTime() - 30);
            }
            else if (this.getPath().get(this.getCounter()).getDestination() == P.getDestination()){
                this.pickedUp = false;
            }
            else {
                Zone z = null;
                for (int i = 0; i < net.getZoneList().size(); i++) {
                    if (this.getPath().get(this.getCounter()).getDestination().getId() == net.getZoneList().get(i).getId()) {
                        z = net.getZoneList().get(i);
                    }
                }
                this.loc = z;
                this.counter++;
                this.createTravelTime();
            }
        }


        return waitingList;

    }


    public void createPath (Location loc, Node dest) {
        Zone z = (Zone) loc;
        Node node = null;

        if (z.getId() == dest.getId()) {                                                                                //check if vehicle is already at passenger location
            this.setPickedUp(true);
        }

        for (int i = 0; i < net.getNodesList().size(); i++) {
                                                                                                                        //match vehicle location with node to get outgoing nodes
            if (z.getId() == net.getNodesList().get(i).getId()) {
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
