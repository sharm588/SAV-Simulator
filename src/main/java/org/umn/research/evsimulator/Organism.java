package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections.*;
import java.util.Comparator;
import java.util.Random;

public class Organism implements Comparable<Organism> {

    Application app = new Application();
    double high = 10.0;
    Random r = new Random();
    double randombeta = high * r.nextDouble();
    double waitTime;

    public Organism () throws IloException, IOException {
        waitTime = app.runSimulation(randombeta);
    }

    public int compareTo(Organism other) {
        return Double.compare(this.waitTime, other.waitTime);
    }

}
