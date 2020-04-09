package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.IOException;
import java.util.Random;

public class Organism implements Comparable<Organism> {

    double high = 10.0;
    Random r = new Random();
    double randomBeta = high * r.nextDouble();
    double randomAlpha = high * r.nextDouble();
    double waitTime;

    public Organism () throws IloException, IOException {
        waitTime = Application.runSimulation(randomBeta, randomAlpha);
    }

    public int compareTo(Organism other) {
        return Double.compare(this.waitTime, other.waitTime);
    }

}
