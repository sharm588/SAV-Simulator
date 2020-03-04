package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.util.*;

public class GeneticAlgorithm {

    ArrayList<Organism> population = new ArrayList<>();
    ArrayList<Organism> sortedList = new ArrayList<>();
    int generations = 5;
    int size = 0;
    Random r = new Random();
    public void createPopulation () throws IloException {
        for (int i = 0; i < 10; i++) {
            Organism organism = new Organism();
            population.add(organism);
        }

    }

    public void survivalOfFittest () throws IloException {
        ArrayList<Organism>tmp = new ArrayList<>();
        for (int i = 0; i < generations; i++) {
            Collections.sort(population);
            size = population.size();
            Organism bestp1 = population.get(0);
            Organism bestp2 = population.get(1);
            System.out.println("size: " + size);
            for (int j = 0; j < size -2; j++) {
                int parent1 = r.nextInt(size-3)+2;
                double p1 =population.get(parent1).randombeta;
                int parent2 = r.nextInt(size -3)+2;
                while(parent2 == parent1)
                {
                    parent2 = r.nextInt(size- 3)+2;
                }
                double p2 = population.get(parent2).randombeta;
                Organism child = new Organism();
                double averageBeta = (p1+p2)/(2);
                child.randombeta = averageBeta;
                tmp.add(child);

            }
            tmp.add(bestp1);
            tmp.add(bestp2);
            population.clear();
            for (Organism org : tmp) {
                population.add(org);
            }
            tmp.clear();
            System.out.println("check");
            Collections.sort(population);
            for (Organism org : population) {
                System.out.println(org.waitTime);
            }

        }

    }



}
