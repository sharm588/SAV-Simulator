package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Random;

import static org.umn.research.evsimulator.Application.runSimulation;

public class Organism implements Comparable<Organism> {

    double high = 10.0;
    Random r = new Random();
    double alphaValues[] = new double[24]; // 24 relocatable nodes [9001 - 9024]
    double betaValues[] = new double[576]; // 576 possible origin-destination combinations
    double randomBeta;
    double randomAlpha;
    double waitTime;

    //constructor for creating initial population
    public Organism () throws IloException, IOException {
        do {
            for (int i = 0; i < alphaValues.length; i++) {
                alphaValues[i] = high * r.nextDouble();
            }
            for (int i = 0; i < betaValues.length; i++) {
                betaValues[i] = high * r.nextDouble();
            }
            //randomBeta = high * r.nextDouble();
            //randomAlpha = high * r.nextDouble();
            waitTime = runSimulation(betaValues, alphaValues, false);
        } while (waitTime == -1);
    }

    //constructor for creating children
    public Organism (double beta[], double alpha[]) throws IloException, IOException {

        for (int i = 0; i < alphaValues.length; i++) {
            alphaValues[i] = alpha[i];
        }
        for (int i = 0; i < betaValues.length; i++) {
            betaValues[i] = beta[i];
        }

        do {
            waitTime = runSimulation(betaValues, alphaValues, true);
            if (waitTime == -1) {
                for (int i = 0; i < alphaValues.length; i++) {
                    alphaValues[i] = high * r.nextDouble();
                }
                for (int i = 0; i < betaValues.length; i++) {
                    betaValues[i] = high * r.nextDouble();
                }
            }
        } while (waitTime == -1);
    }

    public int compareTo(Organism other) {
        return Double.compare(this.waitTime, other.waitTime);
    }

}
