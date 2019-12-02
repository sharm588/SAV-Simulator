package org.umn.research.evsimulator;

import lombok.Data;


import java.util.List;

@Data
public class Vehicle {
    private int batteryPercent;
    private Location loc;
    private Network net;

    public Vehicle (Network net, int batteryPercent, Location loc) {
        this.net = net;
        this.batteryPercent = batteryPercent;
        this.loc = loc;

    }

    public List<Passenger> step (List<Passenger> waitingList, List<Node> nodesList) {
        Node node = null;
        List<Link> path;
        int counter = 0;


        Passenger P = waitingList.get(0);                                                                               //shortest path call from zone to passenger origin and from passenger origin to destination

        for (int i = 0; i < nodesList.size(); i++) {                                                                    //match vehicle location with node to get outgoing nodes
            if (((Zone) this.loc).getId() == nodesList.get(i).getId()) {
                node = nodesList.get(i);
            }
        }

        path = net.shortestPath(node, P.getOrigin());                                                                   //get shortest path from vehicle location to passenger

        while (node != P.getOrigin()) {

            if (path.get(counter).getTraveltime() > 30) {
                Link link = path.get(counter);
                link.setTraveltime(link.getTraveltime() - 30);
            } else {
                node = path.get(counter).getDestination();
                counter++;
            }
        }

        waitingList.remove(0);                                                                                    //passenger picked up, so remove from waiting list
        path = net.shortestPath( node, P.getDestination());                                                             //get shortest path from passenger to destination
        counter = 0;

        while (node != P.getDestination()) {
            if (path.get(counter).getTraveltime() > 30) {
                Link link = path.get(counter);
                link.setTraveltime(link.getTraveltime() - 30);
            } else {
                node = path.get(counter).getDestination();
                counter++;
            }
        }

        return waitingList;

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
