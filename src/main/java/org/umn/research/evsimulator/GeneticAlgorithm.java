package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class GeneticAlgorithm {

    ArrayList<Organism> population = new ArrayList<>();
    ArrayList<Organism> sortedList = new ArrayList<>();
    int generations = 1;
    int populationSize = 10;
    int size = 0;
    float mutate = 0;
    double bestPercent = 0.1; //take top 10% of fittest organisms
    Random r = new Random();

    public void createPopulation () throws IloException, IOException { //create population of beta values
        for (int i = 0; i < populationSize; i++) {
            Organism organism = new Organism();
            population.add(organism);
        }
    }

    public void survivalOfFittest () throws IloException, IOException {

        ArrayList<Organism> bestOrganisms = new ArrayList<>();
        ArrayList<Organism>tmp = new ArrayList<>();

        for (int i = 0; i < generations; i++) { //loop through number of generations

            Collections.sort(population);   //sort values from least to greatest

            int bestNumber = (int) Math.round(bestPercent * populationSize); //calculate how many organisms to take into next generation

            if (bestNumber == 0) { // if  bestNumber rounds to 0, set bestNumber to 1
                bestNumber = 1;
            }

            for (int x = 0; x < bestNumber; x++) { //add best organisms to list
                bestOrganisms.add(population.get(x));
            }

            size = population.size() - bestNumber; //set size to population - bestNumber since we are not considering best few
            //System.out.println("size: " + (size + 2)); //print original population size

            for (int j = 0; j < size; j++) { //loop through population (minus first two)

                mutate = r.nextFloat();

                if (mutate > 0.01f) { //mutate 1% of the time

                    int parent1 = r.nextInt(size - 1) + bestNumber;  //assign parent randomly (disregarding first few organisms)
                    double parent1_beta = population.get(parent1).randombeta; //first parent beta value

                    int parent2 = r.nextInt(size - 1) + bestNumber;  //assign parent randomly (disregarding first few organisms)

                    while (parent2 == parent1) { //make sure parents aren't the same
                        parent2 = r.nextInt(size - 1) + bestNumber;
                    }
                    double parent2_beta = population.get(parent2).randombeta;   //second parent beta value

                    Organism child = new Organism();    //create child
                    double averageBeta = (parent1_beta + parent2_beta) / 2;   //calculate average beta value between parents
                    child.randombeta = averageBeta; //set child's beta value
                    tmp.add(child); //add organism to temporary array list

                } else {
                    Organism child = new Organism(); //create organism, beta value produced is random so it has mutated
                    tmp.add(child);
                }

            }

            for (int x = 0; x < bestNumber; x++) { //add best organisms to tmp
                tmp.add(bestOrganisms.get(x));
            }

            population.clear();
            for (Organism org : tmp) {  //replace organisms with children of last population
                population.add(org);
            }
            bestOrganisms.clear();
            tmp.clear();
            System.out.println("Average Waiting Times (in seconds)");
            Collections.sort(population);
            for (Organism org : population) {
                System.out.println(org.waitTime);
            }

        }

    }



}
