package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Application {

    public static void main(String [] args) throws IloException, IOException
    {

        GeneticAlgorithm alg = new GeneticAlgorithm();
        alg.createPopulation();
        alg.survivalOfFittest();

    }

    public double runSimulation(double betaVal) throws IloException, IOException {
        Network network = Network.createNetwork();
        int fleetSize = 20;
        createFleet(fleetSize, network);

        //Scanner scanner = new Scanner(System.in);

        //System.out.println("Input beta value: ");
        //double betaVal = scanner.nextFloat();
        System.out.println("Fleet size: " + fleetSize);
        List<Passenger> waitingList = network.simulate(7200, betaVal);

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
        return network.avgWaitTime;
    }

    private static void createFleet (int size, Network network) {
        ArrayList<Vehicle> fleet = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            fleet.add(network.makeVehicle(network));
        }

    }
}
