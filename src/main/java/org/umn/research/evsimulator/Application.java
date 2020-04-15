package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static boolean writeToFile = true;

    public static void main(String [] args) throws IloException, IOException
    {

        for (int i = 0; i < 3; i++) {

            GeneticAlgorithm alg = new GeneticAlgorithm();
            if (i == 0) alg.setFirstTerm(0.0125);
            if (i == 1) alg.setFirstTerm(0.025);
            if (i == 2) alg.setFirstTerm(0.05);

            if (alg.populationSize > 10) {
                writeToFile = false;
            }
            alg.createPopulation();
            alg.calculateArithmeticFactor();
            alg.survivalOfFittest();
            System.out.println();
        }


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

    private static void createFleet (int size, Network network) {
        ArrayList<Vehicle> fleet = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            fleet.add(network.makeVehicle(network));
        }

    }
}
