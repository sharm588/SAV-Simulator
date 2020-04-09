package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.IOException;
import java.util.*;

public class GeneticAlgorithm {

    ArrayList<Organism> population = new ArrayList<>();
    ArrayList<Organism> sortedList = new ArrayList<>();
    int generations = 1;
    int populationSize = 1;
    int size = 0;
    float mutate = 0;
    double bestPercent = 0.1; //take top 10% of fittest organisms
    double firstTerm = 0.05;
    double arithmeticFactor = 0;
    Random r = new Random();
    double probabilityValue = 0;

    public void calculateArithmeticFactor() throws IloException, IOException
    {
        int divisor = populationSize - 1;
        arithmeticFactor = -1 * ((2 / populationSize) - (2 * firstTerm)) / divisor;
    }

    public void createPopulation () throws IloException, IOException { //create population of beta values
        for (int i = 0; i < populationSize; i++) {
            Organism organism = new Organism();
            population.add(organism);
        }
    }

    public int findParent() throws IloException, IOException
    {

        probabilityValue = r.nextDouble();
        if (probabilityValue == 0) {
            probabilityValue++;
        }
        double arithmeticCounter = 0;
        int numberOfValues = 0;
        while(arithmeticCounter < probabilityValue)
        {
            arithmeticCounter += firstTerm + (numberOfValues * arithmeticFactor);
            numberOfValues++;
        }
        return numberOfValues;
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

            //size = population.size() - bestNumber; //set size to population - bestNumber since we are not considering best few
            //System.out.println("size: " + (size + 2)); //print original population size

            for (int j = 0; j < populationSize; j++) { //loop through population (minus first two)

                mutate = r.nextFloat();

                if (mutate > 0.05f) { //mutate 5% of the time


                    int numberofValues = findParent();
                    int parent1 =  populationSize - (numberofValues - 1) - 1;  //assign parent randomly (disregarding first few organisms)
                    double parent1_beta = population.get(parent1).randomBeta; //first parent beta value

                    numberofValues = findParent();

                    int parent2 = populationSize - (numberofValues - 1) - 1;  //assign parent randomly (disregarding first few organisms)

                    if (parent2 == parent1) { //make sure parents aren't the same
                        if(parent2 == populationSize-1)
                        {
                            parent2--;
                        }
                        parent2++;
                    }
                    double parent2_beta = population.get(parent2).randomBeta;   //second parent beta value

                    Organism child = new Organism();    //create child
                    double averageBeta = (parent1_beta + parent2_beta) / 2;   //calculate average beta value between parents
                    child.randomBeta = averageBeta; //set child's beta value
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
            //System.out.println("Average Waiting Times (in seconds)");
            Collections.sort(population);
            //for (Organism org : population) {
            System.out.println(population.get(0).waitTime);
            //}

        }

    }

}