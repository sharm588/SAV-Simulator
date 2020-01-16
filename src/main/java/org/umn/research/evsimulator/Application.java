package org.umn.research.evsimulator;

import java.util.ArrayList;
import java.util.List;

public class Application {

    public static void main(String [] args)
    {
        Network network = Network.createNetwork();

        createFleet(20, network);
        List<Passenger> waitingList = network.simulate();

        System.out.println("Waiting List: " + waitingList);
    }

    private static void createFleet (int size, Network network) {
        ArrayList<Vehicle> fleet = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            fleet.add(network.makeVehicle(network));
        }

    }
}
