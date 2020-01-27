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
        List<Passenger> waitingList = network.simulate(1000000);

        System.out.println("Waiting List after simulation");
        System.out.println("-----------------------------");
        System.out.println("           [Empty]           ");

        if (waitingList.size() == 0) {

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
