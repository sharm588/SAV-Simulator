package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.IOException;
import java.util.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode

public class GeneticAlgorithm {

    ArrayList<Organism> population = new ArrayList<>();
    ArrayList<Organism> sortedList = new ArrayList<>();
    int generations = 50;
    int populationSize = 100;
    int size = 0;
    double mutate = 0.00;
    double mutateValue = 0.025f;
    double bestPercent = 0.1; //take top 10% of fittest organisms
    double firstTerm = 0.009;
    double arithmeticFactor = 0;
    Random r = new Random();
    double probabilityValue = 0;
    double avgPopulationWaitTime = 0;
    int alphaLength = 24;
    int betaLength = 576;
    int parentChecker = 0;


    public void calculateArithmeticFactor() throws IloException, IOException
    {
        int divisor = populationSize - 1;
        arithmeticFactor = -1 * ((2 / populationSize) - (2 * firstTerm)) / divisor;
    }

    public void createPopulation () throws IloException, IOException { //create initial population of organisms
        for (int i = 0; i < populationSize; i++) {
            Organism organism = new Organism();
            population.add(organism);
        }
        //System.out.println("done");
    }

    public int assignParent() throws IloException, IOException
    {

        probabilityValue = r.nextDouble(); //assign probability between 0 and 1

        if (probabilityValue == 0) { //if probability is 0, set to 1
            probabilityValue++;
        }

        double arithmeticCounter = 0;
        int numberOfTerms = 0;

        while(arithmeticCounter < probabilityValue) {   //loop until arithmetic sequence sum is greater than the random probability value
            arithmeticCounter += firstTerm + (numberOfTerms * arithmeticFactor);
            numberOfTerms++;
        }
        return numberOfTerms;
    }

    public void survivalOfFittest () throws IloException, IOException {

        ArrayList<Organism> bestOrganisms = new ArrayList<>();
        ArrayList<Organism> nextGeneration = new ArrayList<>();

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

            for (int j = 0; j < populationSize; j++) { //loop through population
                avgPopulationWaitTime += population.get(j).waitTime;
                int numberofValues = assignParent();
                int parent1_index = populationSize - (numberofValues - 1) - 1;  //assign parent
                numberofValues = assignParent();
                int parent2_index = populationSize - (numberofValues - 1) - 1;  //assign parent
                if (parent2_index == parent1_index) { //make sure parents aren't the same
                    if (parent2_index == populationSize - 1) parent2_index--;
                    parent2_index++;
                }
                //Set alpha array for child
                double [] childAlpha = new double[alphaLength];
                parentChecker = r.nextInt(alphaLength);
                for(int k =0; k<alphaLength; k++)
                {
                    if(k<=parentChecker)
                    {
                        childAlpha[k] = population.get(parent1_index).alphaValues[k];
                    }
                    else
                    {
                        childAlpha[k] = population.get(parent2_index).alphaValues[k];
                    }
                }
                //set Child beta array
                double [] childBeta = new double[betaLength];
                parentChecker = r.nextInt(betaLength);
                for(int z = 0; z<betaLength; z++)
                {
                    if(z<=parentChecker)
                    {
                        childBeta[z] = population.get(parent1_index).betaValues[z];
                    }
                    else
                    {
                        childBeta[z] = population.get(parent2_index).betaValues[z];
                    }
                }

                mutate = r.nextDouble();
                if(mutate<mutateValue) {
                    int randomAlphaIndex = r.nextInt(alphaLength);
                    int randomBetaIndex = r.nextInt(betaLength);
                    double newAlphaValue = 10* r.nextDouble();
                    double newBetaValue = 10 * r.nextDouble();
                    childAlpha[randomAlphaIndex] = newAlphaValue;
                    childBeta[randomBetaIndex] = newBetaValue;
                }


                Organism child = new Organism(childBeta, childAlpha); //create organism with random alpha and beta values (mutation)
                nextGeneration.add(child);
            }

            Collections.sort(population);   //sort current population from lowest to highest waiting time
            avgPopulationWaitTime /= population.size();
            System.out.println((i+1) + ": " + population.get(0).waitTime); //print best waiting time
            System.out.println("Beta values: " + Arrays.toString(population.get(0).betaValues));
            System.out.println("Alpha values: " + Arrays.toString(population.get(0).alphaValues));
            avgPopulationWaitTime = 0;

            for (int x = 0; x < bestNumber; x++) { //add best organisms from this generation to next generation
                nextGeneration.add(bestOrganisms.get(x));
            }

            population.clear();
            for (Organism org : nextGeneration) {  //replace organisms of last generation with children
                population.add(org);
            }
            bestOrganisms.clear();
            nextGeneration.clear();

        }
        //System.out.println("done1");

    }

}