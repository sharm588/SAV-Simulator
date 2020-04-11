package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.IOException;
import java.util.Random;

import static org.umn.research.evsimulator.Application.runSimulation;

public class Organism implements Comparable<Organism> {

    double high = 10.0;
    Random r = new Random();
    double randomBeta;
    double randomAlpha;
    double waitTime;

    //constructor for creating initial population
    public Organism () throws IloException, IOException {
        randomBeta = high * r.nextDouble();
        randomAlpha = high * r.nextDouble();
        waitTime = runSimulation(randomBeta, randomAlpha, false);
    }

    //constructor for creating children
    public Organism (double beta, double alpha) throws IloException, IOException {
        randomBeta = beta;
        randomAlpha = alpha;
        waitTime = runSimulation(randomBeta, randomAlpha, true);
    }

    public int compareTo(Organism other) {
        return Double.compare(this.waitTime, other.waitTime);
    }

}
