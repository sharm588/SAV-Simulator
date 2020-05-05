package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static boolean writeToFile = true;

    public static void main(String [] args) throws IloException, IOException
    {
        double ratio = 1.0/6;
        double scale = 0.7;
        for (int i = 0; i < 1; i++) {
            double percent = scale * 100;
            System.out.println("Demand Scale: " + percent + "%");
            runSimulation(5.966902378318867, 7.6254545181893942, scale, ratio);
            scale += 0.05;
        }
        /*for (int i = 0; i < 1; i++) {

            GeneticAlgorithm alg = new GeneticAlgorithm();

            if (alg.populationSize > 10) {
                writeToFile = false;
            }
            alg.createPopulation();
            alg.calculateArithmeticFactor();
            alg.survivalOfFittest();
            System.out.println();
        }*/


    }

    public static double runSimulation(double betaVal, double alphaVal, boolean child) throws IloException, IOException {
        Network network = Network.createNetwork();
        double waitTime = 0;
        int fleetSize = 25;
        createFleet(fleetSize, network);

        if (child || !writeToFile) waitTime = network.simulate(7200, betaVal, alphaVal, false);
        else if (writeToFile) waitTime = network.simulate(7200, betaVal, alphaVal, true);

        /*System.out.println("Waiting List after simulation (" + waitingList.size() + ")");
        System.out.println("-----------------------------");

        if (waitingList.size() == 0) {
            System.out.println("           [Empty]           ");
        }
        System.out.println();

        for (Passenger passenger : waitingList) {
            System.out.println(passenger);
        }
        System.out.println();*/
        return waitTime;
    }

    public static double runSimulation(double betaVal, double alphaVal, double scale, double ratio) throws IloException, IOException {
        Network network = Network.createNetwork(scale);
        int time = 7200;
        double waitTime = 0;
        int numberOfPassengers = network.getPassengers().size();
        for (Passenger passenger : network.getPassengers()) {
            if (passenger.getDeparturetime() > time) {
                numberOfPassengers--;
            }
        }

        int fleetSize = (int) (ratio * numberOfPassengers);
        createFleet(fleetSize, network);

        if (!writeToFile) waitTime = network.simulate(time, betaVal, alphaVal, false);
        else waitTime = network.simulate(7200, betaVal, alphaVal, true);

        for (Vehicle vehicle : network.getVehicleList()) {
            System.out.println("Vehicle " + vehicle.getId() + ": Total travel distance of " + vehicle.getTotalDistanceTraveled() + " miles; total time with passenger in-vehicle of " + vehicle.getInVehicleTravelTime() + " seconds; total travel time of " + vehicle.getTotalTravelTime() + " seconds");
        }
        System.out.println("Average Wait Time: " + waitTime);
        return waitTime;
    }

    private static void createFleet (int size, Network network) {
        ArrayList<Vehicle> fleet = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            fleet.add(network.makeVehicle(network));
        }

    }
}
