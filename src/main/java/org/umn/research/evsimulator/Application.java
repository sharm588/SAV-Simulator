package org.umn.research.evsimulator;

import java.util.ArrayList;
import java.util.List;

public class Application {

    public static void main(String [] args)
    {
        Network network = Network.createNetwork();
        int fleetSize = 20;
        createFleet(fleetSize, network);

        System.out.println("Fleet size: " + fleetSize);
        List<Passenger> waitingList = network.simulate(3600);

        System.out.println("Waiting List after simulation");
        System.out.println("-----------------------------");

        if (waitingList.size() == 0) {
            System.out.println("           [Empty]           ");
        } else {
            System.out.println("Size: " + waitingList.size());
        }

        for (Passenger passenger : waitingList) {
            System.out.println(passenger);
        }
    }

    private static void createFleet (int size, Network network) {
        ArrayList<Vehicle> fleet = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            fleet.add(network.makeVehicle(network));
        }

    }
}
