package org.umn.research.evsimulator;

import java.util.List;

public class Application {

    public static void main(String [] args)
    {
        Network network = Network.createNetwork();
        Vehicle vehicle1 = network.makeVehicle();
        //Vehicle vehicle2 = network.makeVehicle();
        vehicle1.net = network;
        //vehicle2.net = network;

        List<Passenger> waitingList = network.simulate();
    }
}
