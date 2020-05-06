package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.IOException;
import java.util.ArrayList;

public class Application {

    private static boolean writeToFile = true;

    public static void main(String [] args) throws IloException, IOException
    {
        double ratio = 1.0/6;
        double scale = 0.7;
        for (int i = 0; i < 13; i++) {
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
        for (Passenger passenger : network.getPassengers()) {   //number of passengers is approximately the number of people picked up + the rest on the waitinglist
            if (passenger.getDeparturetime() > time) {
                numberOfPassengers--;
            }
        }
        int fleetSize = (int) (ratio * numberOfPassengers);
        createFleet(fleetSize, network);

        if (!writeToFile) waitTime = network.simulate(time, betaVal, alphaVal, false);
        else waitTime = network.simulate(7200, betaVal, alphaVal, true);

        double sumOfInVehicleSeconds = 0;
        double sumOfTotalTravelSeconds = 0;
        double sumOfTotalDistanceTravelled = 0;
        for (Vehicle vehicle : network.getVehicleList()) {
            sumOfInVehicleSeconds += vehicle.getInVehicleTravelTime();
            sumOfTotalTravelSeconds += vehicle.getTotalTravelTime();
            sumOfTotalDistanceTravelled += vehicle.getTotalDistanceTravelled();
        }
        System.out.println("Average Wait Time: " + waitTime);
        System.out.println("Average in-vehicle travel time: " + (sumOfInVehicleSeconds / network.getVehicleList().size()));
        System.out.println("Average total vehicle travel time: " + (sumOfTotalTravelSeconds / network.getVehicleList().size()));
        System.out.println("Total miles travelled in fleet: " + sumOfTotalDistanceTravelled);
        return waitTime;
    }

    private static void createFleet (int size, Network network) {
        ArrayList<Vehicle> fleet = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            fleet.add(network.makeVehicle(network));
        }

    }
}
