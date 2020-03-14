package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class GeneticAlgorithm {

    ArrayList<Organism> population = new ArrayList<>();
    ArrayList<Organism> sortedList = new ArrayList<>();
    int generations = 10;
    int size = 0;
    Random r = new Random();

    public void createPopulation () throws IloException, IOException { //create population of beta values
        for (int i = 0; i < 10; i++) {
            Organism organism = new Organism();
            population.add(organism);
        }
    }

    public void survivalOfFittest () throws IloException, IOException {
        ArrayList<Organism>tmp = new ArrayList<>();

        for (int i = 0; i < generations; i++) { //loop through number of generations

            Collections.sort(population);   //sort values from least to greatest

            Organism bestp1 = population.get(0); //get best two organisms (parents) with lowest waiting times
            Organism bestp2 = population.get(1);
            size = population.size() - 2; //set size to population - 2 since we are not considering best two
            System.out.println("size: " + (size + 2)); //print original population size

            for (int j = 0; j < size; j++) { //loop through population (minus first two)

                int parent1 = r.nextInt(size - 1) + 2;  //assign parent randomly (disregarding first two organisms)
                double parent1_beta = population.get(parent1).randombeta; //first parent beta value

                int parent2 = r.nextInt(size - 1) + 2;  //assign parent randomly (disregarding first two organisms)

                while(parent2 == parent1) { //make sure parents aren't the same
                    parent2 = r.nextInt(size - 1) + 2;
                }
                double parent2_beta = population.get(parent2).randombeta;   //second parent beta value

                Organism child = new Organism();    //create child
                double averageBeta = (parent1_beta+parent2_beta) / 2;   //calculate average beta value between parents
                child.randombeta = averageBeta; //set child's beta value
                tmp.add(child); //add organism to temporary array list

            }
            tmp.add(bestp1);    //add best two organisms to temporary list
            tmp.add(bestp2);
            population.clear();
            for (Organism org : tmp) {  //replace organisms with children of last population
                population.add(org);
            }
            tmp.clear();
            System.out.println("Average Waiting Times (in seconds)");
            Collections.sort(population);
            for (Organism org : population) {
                System.out.println(org.waitTime);
            }

        }

    }



}
