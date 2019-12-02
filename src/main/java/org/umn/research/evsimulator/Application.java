package org.umn.research.evsimulator;

public class Application {

    public static void main(String [] args)
    {
        Network network = Network.createNetwork();

        System.out.println(network.getPassengers());

        Vehicle vehicle = network.makeVehicle();

        System.out.println(vehicle.getBatteryPercent());
        System.out.println(network.simulate());
    }
}
