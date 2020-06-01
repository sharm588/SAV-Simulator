package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.IOException;
import java.util.ArrayList;

public class Application {

    private static boolean writeToFile = true;

    public static void main(String [] args) throws IloException, IOException
    {
        double ratio = 1.0/6;
        double scale = 1.0;
        int size = 20;

        Network network = Network.createNetwork();
        double waitTime = 0;
        int fleetSize = 25;
        createFleet(fleetSize, network);

        //waitTime = network.simulate(7200, 5.539512878, 5.254875176, true);
        //System.out.println("Avg wait time: " + network.avgWaitTime);

        /*while (size != 45) {
            double percent = scale * 100;
            System.out.println("Fleet Size: " + size);
            runSimulation(5.539512878, 5.254875176, scale, size);
            size += 5;
            /*runSimulation(5.539512878, 5.254875176, scale, ratio);

            if (scale != 1.0) {
                scale += 10.0;
            } else {
                scale += 9.0;
            }*/
        //}
        for (int i = 0; i < 3; i++) {

            GeneticAlgorithm alg = new GeneticAlgorithm();
            if (i == 0) {
                alg.setMutateValue(0.025);
                alg.setFirstTerm(0.007);
            }

            if (i == 1) {
                alg.setMutateValue(0.025);
                alg.setFirstTerm(0.008);
            }

            if (i == 2) {
                alg.setMutateValue(0.025);
                alg.setFirstTerm(0.009);
            }

            if (alg.populationSize > 10) {
                writeToFile = false;
            }
            alg.createPopulation();
            alg.calculateArithmeticFactor();
            alg.survivalOfFittest();

            System.out.println("Best Beta: " + alg.population.get(0).randomBeta + " Best Alpha: " + alg.population.get(0).randomAlpha);
            System.out.println();
        }


    }

    public static double runSimulation(double betaVal, double alphaVal, boolean child) throws IloException, IOException { // constructor for genetic algorithm
        Network network = Network.createNetwork();
        double waitTime = 0;
        int fleetSize = 25;
        createFleet(fleetSize, network);

        if (child || !writeToFile) waitTime = network.simulate(7200, betaVal, alphaVal, false);
        else if (writeToFile) waitTime = network.simulate(7200, betaVal, alphaVal, true);

        if (network.getTotalNumberOfPassengers() < 120) { // for a fleet size of 25, at least 120 passengers must be picked to use alpha/beta values
            waitTime = -1;
        }

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

    public static void runSimulation(double betaVal, double alphaVal, double scale, double ratio) throws IloException, IOException { // constructor for specific fleet to passenger ratio
        double[] waitTime = new double[10];
        double sumOfWaitTimes = 0;
        double sumOfInVehicleSeconds = 0;
        double sumOfTotalTravelSeconds = 0;
        double sumOfTotalDistanceTravelled = 0;
        double totalPassengers = 0;
        double standardDev = 0;
        for (int i = 0; i < waitTime.length; i++) {
            Network network = Network.createNetwork(scale);
            int time = 7200;

            int numberOfPassengers = network.getPassengers().size();
            for (Passenger passenger : network.getPassengers()) {   //number of passengers is approximately the number of people picked up + the rest on the waitinglist
                if (passenger.getDeparturetime() > time) {
                    numberOfPassengers--;
                }
            }
            int fleetSize = (int) (ratio * numberOfPassengers);
            createFleet(fleetSize, network);

            if (!writeToFile) waitTime[i] = network.simulate(time, betaVal, alphaVal, false);
            else waitTime[i] = network.simulate(7200, betaVal, alphaVal, true);

            sumOfWaitTimes += waitTime[i];
            for (Vehicle vehicle : network.getVehicleList()) {
                sumOfInVehicleSeconds += vehicle.getInVehicleTravelTime();
                sumOfTotalTravelSeconds += vehicle.getTotalTravelTime();
                sumOfTotalDistanceTravelled += vehicle.getTotalDistanceTravelled();
            }
            totalPassengers += network.getTotalNumberOfPassengers();
        }

        double avgWaitTime = sumOfWaitTimes / waitTime.length;

        for (int i = 0; i < waitTime.length; i++) {
            standardDev += Math.pow(waitTime[i] - avgWaitTime, 2);
        }
        standardDev = Math.sqrt(standardDev / waitTime.length);
        //System.out.println("Average Wait Time | Average in-vehicle travel time | Average total vehicle travel time | Total miles travelled in fleet | Standard deviation" + waitTime);
        System.out.println(avgWaitTime + " " + (sumOfInVehicleSeconds / totalPassengers) + " " + (sumOfTotalTravelSeconds / totalPassengers) + " " + sumOfTotalDistanceTravelled + " " + standardDev);
    }

    public static void runSimulation(double betaVal, double alphaVal, double scale, int size) throws IloException, IOException { // constructor for specific fleet size

        int time = 7200;
        double[] waitTime = new double[10];
        double sumOfWaitTimes = 0;
        double waitTimeSum = 0;
        int vehicleSum = 0;
        double sumOfPassengersNotPicked = 0;
        double sumOfInVehicleSeconds = 0;
        double totalNumberOfPassengers = 0;
        int fleetSize = size;

        for (int i = 0; i < waitTime.length; i++) {

            Network network = Network.createNetwork(scale);
            createFleet(fleetSize, network);

            if (!writeToFile) waitTime[i] = network.simulate(time, betaVal, alphaVal, false);
            else waitTime[i] = network.simulate(7200, betaVal, alphaVal, true);

            for (Vehicle vehicle : network.getVehicleList()) {
                sumOfInVehicleSeconds += vehicle.getInVehicleTravelTime();
            }
            totalNumberOfPassengers += network.getTotalNumberOfPassengers();
            sumOfWaitTimes += waitTime[i];
            waitTimeSum += network.getSumOfWaitTimes();
            vehicleSum += network.getNumberOfUsedVehicles();
            sumOfPassengersNotPicked += network.getWaitingList().size();
        }

        double avgWaitTime = sumOfWaitTimes / waitTime.length;
        double avgNumberPicked = totalNumberOfPassengers / waitTime.length;
        double avgNumberNotPicked = sumOfPassengersNotPicked / waitTime.length;
        System.out.println("Average Wait Time: " + avgWaitTime);
        System.out.println("Average in-vehicle travel time: " + (sumOfInVehicleSeconds / totalNumberOfPassengers));
        System.out.println("Average # of Passengers Picked: " + avgNumberPicked);
        System.out.println("Average # of Passengers Not Picked: " + avgNumberNotPicked);
        System.out.println("Average # of Used Vehicles: " + (vehicleSum / waitTime.length));
        System.out.println("Sum of Wait Times " + (waitTimeSum / waitTime.length));
    }

    private static void createFleet (int size, Network network) {
        ArrayList<Vehicle> fleet = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            fleet.add(network.makeVehicle(network));
        }

    }
}
