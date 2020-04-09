package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static boolean writeToFile = true;

    public static void main(String [] args) throws IloException, IOException
    {

        GeneticAlgorithm alg = new GeneticAlgorithm();

        if (alg.populationSize > 10) {
            writeToFile = false;
        }
        alg.createPopulation();
        alg.calculateArithmeticFactor();
        alg.survivalOfFittest();


    }

    public static double runSimulation(double betaVal, double alphaVal) throws IloException, IOException {
        Network network = Network.createNetwork();
        int fleetSize = 25;
        createFleet(fleetSize, network);

        //Scanner scanner = new Scanner(System.in);

        //System.out.println("Input beta value: ");
        //double betaVal = scanner.nextFloat();
        double waitTime = network.simulate(7200, betaVal, alphaVal, writeToFile);

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

    private static void createFleet (int size, Network network) {
        ArrayList<Vehicle> fleet = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            fleet.add(network.makeVehicle(network));
        }

    }
}
