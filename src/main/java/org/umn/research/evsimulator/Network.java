package org.umn.research.evsimulator;

import ilog.concert.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.lang.*;

import ilog.cplex.IloCplex;

@Data
@EqualsAndHashCode
public class Network {

    private int timeIncrement = 30;
    private List<Node> nodesList = new ArrayList<>();
    private List<Double> demand = new ArrayList<>();
    private HashMap<Integer, int[]> departureIdMap = new HashMap<>();
    private HashMap<Integer, Node> nodeMap = new HashMap<>();
    private List<Passenger> passengers = new ArrayList<>();
    private List<Link> linksList = new ArrayList<>();
    private List<Vehicle> vehicleList = new ArrayList<>();
    private List<Zone> zoneList = new ArrayList<>();
    private List<Node> relocatableNodesList = new ArrayList<>();
    public static List<Node> mainNodesList = new ArrayList<>();
    private List<Zone> fakeZoneList = new ArrayList<>();
    private List<Passenger> waitingList = new ArrayList<>();
    private List<Vehicle>availableVehiclesList = new ArrayList<>();
    private List<Node>sourcesList = new ArrayList<>();
    private List<Node>destinationsList = new ArrayList<>();
    private int endTime = 0;
    private int officialTime = 0;
    private int totalNumberOfPassengers = 0;
    private double sumOfWaitTimes = 0; //waiting time from when passenger is assigned to vehicle to when passenger is picked up
    private int originalWaitingListSize = 0;
    private double[] beta;
    private double[] alpha;
    public double avgWaitTime;
    public FileWriter simulationWriter;
    private int initialThreshold = 300;
    private List<Double> zValuesList = new ArrayList<>();
    private int incrementer = 0;
    private ArrayList<Passenger> droppedPassengersList = new ArrayList<>();
    public int numberOfUsedVehicles = 0;
    public int sumOfTravelTimes = 0;
    private ArrayList<Vehicle> justAssignedVehicles = new ArrayList<>();
    private static int validSim = 0;


    public static Network createNetwork() throws IOException{
        Network network = new Network();
        network.scanNodes(getFilePath("nodes.txt"));
        network.createNodeMap();
        network.readLinks(getFilePath("links.txt"));
        network.scanDemand(getFilePath("dynamic_od.txt"));
        network.readDepartureTimes(getFilePath("demand_profile.txt"));
        network.createPassengers(getFilePath("dynamic_od.txt"), 1.0);
       // network.simulationWriter = new FileWriter(getFilePath("simulation_log.txt"), false);
        return network;
    }

    public static Network createNetwork(double scale) throws IOException{
        Network network = new Network();
        network.scanNodes(getFilePath("nodes.txt"));
        network.createNodeMap();
        network.readLinks(getFilePath("links.txt"));
        network.scanDemand(getFilePath("dynamic_od.txt"));
        network.readDepartureTimes(getFilePath("demand_profile.txt"));
        network.createPassengers(getFilePath("dynamic_od.txt"), scale);
        // network.simulationWriter = new FileWriter(getFilePath("simulation_log.txt"), false);

        return network;
    }

    public double simulate(float time, double betaVal[], double alphaVal[], boolean writerOn) throws IloException, IOException {

        if (writerOn) simulationWriter = new FileWriter(getFilePath("simulation_log.txt"), false);
        beta = betaVal; //initialize beta
        alpha = alphaVal;  //initialize alpha
        originalWaitingListSize = passengers.size();

        while (waitingList.size() == 0) {   //ensure the simulation has some passengers in the waiting list to start with

            for (Passenger p : passengers) { //fill waiting list with passengers with departure times within the initial time threshold
                if (initialThreshold >= p.getDeparturetime()) {
                    waitingList.add(p);
                    p.setDispatched(true);
                    //totalNumberOfPassengers += 1;
                }
            }

            if (waitingList.size() == 0) {
                initialThreshold += 100;    //increase time threshold to include more passengers
            }

        }
        removePassengersFromPassengerList();

        if (writerOn) {
            simulationWriter.write("Fleet size: " + vehicleList.size() + "\n");
            simulationWriter.write("Waiting List size: " + waitingList.size() + "\n");
            simulationWriter.write("\n");
        }

        /*for (Vehicle vehicle : vehicleList) {
            System.out.println("Vehicle " + vehicle.getId());
            for (Passenger p : waitingList) {
                double timeToPassenger = 0;
                vehicle.createPath(vehicle.getLoc(), p.getOrigin());
                for (int y = 0; y < vehicle.getPath().size(); y++) {
                    timeToPassenger += vehicle.getPath().get(y).getTraveltime(); //calculate total travel time from vehicle location to passenger origin
                }
                System.out.println("Time to passenger " + p.getId() + ": " + timeToPassenger);
            }

        }*/

        IloCplex c = new IloCplex();

        IloIntVar[] initialAssignment = generateAssignments(c); //initialize initial assignments

        sendVehiclesToEmptyNodes(writerOn);

        for (Vehicle vehicle : vehicleList) { //assign a passenger to each vehicle of fleet
            assignPassengerToVehicle(vehicle, initialAssignment, c, writerOn);
        }
        removeVehiclesFromList();
        removePassengersFromWaitingList();

        for (Vehicle v : justAssignedVehicles) {
            double timeCombo = 0;

            v.createPath(v.getLoc(), v.getPassenger().getOrigin());
            for (int y = 0; y < v.getPath().size(); y++) {
                timeCombo += v.getPath().get(y).getTraveltime(); //calculate total travel time from vehicle location to passenger origin
            }
            for (Vehicle v1 : availableVehiclesList) {

                double travelTimeCombo = 0;
                v1.createPath(v.getLoc(), v.getPassenger().getOrigin());
                for (int y = 0; y < v.getPath().size(); y++) {
                    travelTimeCombo += v1.getPath().get(y).getTraveltime(); //calculate total travel time from vehicle location to passenger origin
                }
                if (travelTimeCombo < timeCombo) {
                    System.out.println("Vehicle " + v1.getId() + " [" + travelTimeCombo + "] is a better choice than vehicle " + v.getId() + " [" + timeCombo + "]");
                }
            }
        }
        justAssignedVehicles.clear();

        if (writerOn) simulationWriter.write("\n");

        for (int i = 0; i < time; i += timeIncrement) { //simulate SAEV in 30 second intervals

            if (writerOn) simulationWriter.write("Time: " + i + " seconds\n");

            for (Passenger p : passengers) { //fill waiting list, change to '< t' when finished
                if (i >= p.getDeparturetime()) {
                    waitingList.add(p);
                    p.setDispatched(true);
                    //totalNumberOfPassengers += 1;
                }
            }
            removePassengersFromPassengerList();

            if (writerOn) {
                simulationWriter.write("\n");
                simulationWriter.write("Waiting List size: " + waitingList.size() + "\n");
                simulationWriter.write("\n");
            }

            for (Vehicle vehicle : availableVehiclesList) { //make sure each vehicle's location is not null
                if (vehicle.getLoc() == null) {
                    System.out.println("Time: " + i);
                    System.out.println("Vehicle #" + vehicle.getId() + " location is null");
                    System.out.println(vehicle.isIdle());
                    if (writerOn) simulationWriter.close();
                    System.exit(1);
                }
            }

            IloIntVar[] newAssignment = generateAssignments(c); //generate new assignments for vehicles

            sendVehiclesToEmptyNodes(writerOn);

            for (Vehicle vehicle : availableVehiclesList) { //assign vehicles to passengers based on assignment above
                assignPassengerToVehicle(vehicle, newAssignment, c, writerOn);
                if (!vehicle.isIdle()) {
                    vehicle.resetVariables();
                    vehicle.setRequested(true);
                }

            }
            removeVehiclesFromList();
            removePassengersFromWaitingList();

            for (Vehicle v : justAssignedVehicles) {
                double timeCombo = 0;

                v.createPath(v.getLoc(), v.getPassenger().getOrigin());
                for (int y = 0; y < v.getPath().size(); y++) {
                    timeCombo += v.getPath().get(y).getTraveltime(); //calculate total travel time from vehicle location to passenger origin
                }
                for (Vehicle v1 : availableVehiclesList) {

                    double travelTimeCombo = 0;
                    v1.createPath(v.getLoc(), v.getPassenger().getOrigin());
                    for (int y = 0; y < v.getPath().size(); y++) {
                        travelTimeCombo += v1.getPath().get(y).getTraveltime(); //calculate total travel time from vehicle location to passenger origin
                    }
                    if (travelTimeCombo < timeCombo) {
                        System.out.println("Vehicle " + v1.getId() + " [" + travelTimeCombo + "] is a better choice than vehicle " + v.getId() + " [" + timeCombo + "]");
                    }
                }
            }
            justAssignedVehicles.clear();

            for (Vehicle vehicle : vehicleList) {   // do for each vehicle

                if (!vehicle.idle) {  //check if vehicle has an assignment

                    if (vehicle.isNotMoving()) { //check if vehicle is at zone, just picked up passenger, or just dropped off passenger

                        if (vehicle.isPickedUp()) { //vehicle already picked up passenger
                            beginRouteToDestination(vehicle, writerOn); //begin route to passenger's destination

                        } else if (!vehicle.isPickedUp() && !vehicle.isDroppedOff() && vehicle.isRequested()) { //otherwise if vehicle has not picked up passenger and has not dropped off passenger and the vehicle is requested

                            beginRouteToPassenger(vehicle, writerOn); //begin route to passenger

                        }

                        vehicle.setNotMoving(false); //vehicle should be ready to move at this point, so notMoving is false
                    }

                    if (vehicle.passenger == null) {    // error check vehicle has an assigned passenger
                        System.out.println("Time: " + i);
                        System.out.println("Vehicle #" + vehicle.getId() + " passenger is null");
                        simulationWriter.close();
                        System.exit(1);
                    }

                    vehicle.step(waitingList, vehicle.passenger); //step 30 seconds in simulation


                    if (vehicle.isJustPickedUp()) { //vehicle has just picked up passenger

                        if (writerOn) simulationWriter.write("(!) Vehicle #" + vehicle.getId() + " picked up passenger " + "[" + vehicle.getPassenger() + "]\n");

                        Location location = matchLocationWithCorrespondingZone(vehicle.getPassenger().getOrigin()); // update vehicle location
                        vehicle.setLoc(location);

                        vehicle.setJustPickedUp(false);
                        vehicle.setNotMoving(true);
                    }

                    if (vehicle.isDroppedOff()) {   // vehicle has dropped off passenger

                        vehicle.setRequested(false);
                        vehicle.setDroppedOff(false);

                        Location location = matchLocationWithCorrespondingZone(vehicle.getPassenger().getDestination());    //update vehicle location
                        vehicle.setLoc(location);

                        if (!vehicle.isAlreadyPrintedDropOff()) {

                            if (writerOn) simulationWriter.write("(!) Vehicle #" + vehicle.getId() + " dropped off passenger " + "[" + vehicle.getPassenger() + "]\n");
                            if (vehicle.isNoMoreRides()) {
                                vehicle.setAlreadyPrintedDropOff(true);
                            }
                            vehicle.numberDroppedOff++;
                            vehicle.getPassenger().setDroppedOff(true);

                        }
                        availableVehiclesList.add(vehicle);
                        vehicle.resetVariables();
                        //vehicle.setPickedUp(false);
                        vehicle.setNotMoving(true);
                        vehicle.setIdle(true);
                        vehicle.passenger = null;

                    } else if (!vehicle.isPickedUp() && !vehicle.isBeginningRouteToPassenger() && vehicle.isRequested()) { // otherwise if vehicle has not picked up passenger and vehicle is requested (and vehicle hasn't already begun route to passenger

                        if (writerOn) simulationWriter.write("Vehicle #" + vehicle.getId() + " is heading towards passenger\n");

                        boolean addToSum = true;

                        for (Vehicle v: vehicleList) {
                            if (v.getPassenger() != null) {
                                if (vehicle.getPassenger().getId() == v.getPassenger().getId() && vehicle.getId() != v.getId()) {
                                    System.out.println("Same passenger: " + vehicle.getPassenger());
                                    addToSum = false;
                                }
                            }
                        }

                        if (addToSum) {
                            sumOfWaitTimes += 30;
                            incrementer += 1;
                        }

                    } else if (vehicle.isPickedUp() && !vehicle.isAlreadyBeginningRouteToDestination()) { // otherwise if vehicle has picked up passenger and not already printed info

                        if (writerOn) simulationWriter.write("Vehicle #" + vehicle.getId() + " is driving passenger to destination\n");

                    }

                    vehicle.setAlreadyBeginningRouteToDestination(false);
                    vehicle.setBeginningRouteToPassenger(false);

                } else if (vehicle.isSentToNode()) {

                    vehicle.createPath(vehicle.getLoc(), vehicle.getNode());

                    Zone loc = (Zone) vehicle.getLoc();
                    if (loc.getId() == vehicle.getNode().getId()) {
                        vehicle.setAlreadyAtNode(true);
                    }

                    if (vehicle.isAlreadyAtNode()) {
                        if (writerOn) simulationWriter.write("Vehicle #" + vehicle.getId() + " is already at empty node\n");
                        vehicle.setAlreadyAtNode(false);
                        vehicle.setSentToNode(false);
                        vehicle.setEnRouteToNode(false);
                        vehicle.setArrivedAtNode(false);
                        vehicle.setCounter(0); //reset counter (keeps track of node index in path array list)
                    } else {

                        /*if (vehicle.getCounter() >= vehicle.getPath().size()) {
                            System.out.println("vehicle loc: " + vehicle.getLoc());
                            printVehiclePath(vehicle);
                            System.out.println("counter: " + vehicle.getCounter() + " size: " + vehicle.getPath().size());
                        }*/

                        vehicle.stepTowardsEmptyNode();

                        if (vehicle.isArrivedAtNode()) {
                            if (writerOn)
                                simulationWriter.write("Vehicle #" + vehicle.getId() + " has arrived at empty node\n");
                            vehicle.setArrivedAtNode(false);
                            vehicle.setSentToNode(false);
                            vehicle.setEnRouteToNode(false);
                            vehicle.setArrivedAtNode(false);
                            vehicle.setCounter(0); //reset counter (keeps track of node index in path array list)
                        } else if (vehicle.isEnRouteToNode()) {
                            if (writerOn)
                                simulationWriter.write("Vehicle #" + vehicle.getId() + " is driving to empty node\n");
                        }
                    }
                } else {
                    vehicle.setCounter(0); //reset counter (keeps track of node index in path array list)
                    if (vehicle.isAssignedSameNode()) {
                        if (writerOn) simulationWriter.write("Vehicle #" + vehicle.getId() + " is stationary at empty node [assigned to same empty node " + vehicle.getNode() + "]\n");
                        vehicle.setAssignedSameNode(false);
                    }
                    else if (writerOn) simulationWriter.write("Vehicle #" + vehicle.getId() + " is stationary at empty node " + vehicle.getNode() + "\n");
                }
            }

            if (writerOn) {
                simulationWriter.write("\n");
                simulationWriter.write("Available vehicles: " + availableVehiclesList.size() + "\n");
                simulationWriter.write("\n");
            }

            endTime = i;
            if (writerOn) simulationWriter.write("\n");
        }

        avgWaitTime = sumOfWaitTimes / totalNumberOfPassengers;
        if (writerOn) {
            simulationWriter.write("\n");
            simulationWriter.write("EV ridesharing simulated in " + (endTime + 30) + " seconds\n");
            simulationWriter.write("\n");
            simulationWriter.write("Total number of passengers: " + totalNumberOfPassengers + "\n");
            simulationWriter.write("Average passenger wait time: " + avgWaitTime + " seconds\n");
            simulationWriter.close();
        }

        for (Vehicle v: vehicleList) {
            if (v.isUsed()) {
                numberOfUsedVehicles++;
            }
        }
       // System.out.println("Avg travel time: " + sumOfTravelTimes / totalNumberOfPassengers);
      //  System.out.println(endTime);
       /* if (totalNumberOfPassengers == 0) {
            simulationWriter.close();
            System.out.println("No passengers picked - alpha: " + alpha + " beta: " + beta);
            System.exit(0);
        }*/
        //System.out.println("# of passengers: " + totalNumberOfPassengers + " alpha: " + alpha + " beta: " + beta);
        //if (totalNumberOfPassengers >= 130) validSim++;
        //System.out.println(validSim);
        return avgWaitTime;
    }

    public IloIntVar[] generateAssignments(IloCplex c) throws IloException, IOException {

        c.clearModel();
        int size = availableVehiclesList.size() * waitingList.size();
        IloIntVar[] xValues = c.intVarArray(size, 0, 1);
        IloIntVar[] travelTimexValues = c.intVarArray(size, 1, 1);
        ArrayList<Node>sources = new ArrayList<>();
        ArrayList<Node>destinations = new ArrayList<>();

        for (Passenger passenger : waitingList) {   //get passenger sources / destinations
            Node passengerSource = passenger.getOrigin();
            Node passengerDestination = passenger.getDestination();

            if (!sources.contains(passengerSource)) {
                sources.add(passengerSource);
            }

            if (!destinations.contains(passengerDestination)) {
                destinations.add(passengerDestination);
            }
        }

        int zValuesSize = relocatableNodesList.size() * availableVehiclesList.size();
        IloIntVar[] zValues = c.intVarArray(zValuesSize, 0, 1);

        int i = 0;

        for (Vehicle vehicle : availableVehiclesList) { // constraint: each vehicle is assigned to <= 1 passenger [one vehicle to passenger]
            IloLinearNumExpr e = c.linearNumExpr();
           // IloLinearNumExpr e1 = c.linearNumExpr();
            for (Passenger passenger : waitingList) {
                xValues[i] = c.intVar(0, 1);
                e.addTerm(1, xValues[i]);
                travelTimexValues[i] = c.intVar(1,1);
                //e1.addTerm(1, travelTimexValues[i]);
                i++;

            }
            c.addLe(e, 1);
          //  c.addLe(e1, 1);
        }

        int next = 0;
        for (Passenger passenger : waitingList) { // constraint: each passenger is assigned to <= 1 vehicle [one passenger to vehicle]
            IloLinearNumExpr e = c.linearNumExpr();
            //IloLinearNumExpr e1 = c.linearNumExpr();
            for (int p = next; p < xValues.length; p += waitingList.size()) {
                e.addTerm(1, xValues[p]);
           //     e1.addTerm(1, travelTimexValues[p]);
            }
            next++;
            c.addLe(e, 1);
          //  c.addLe(e1, 1);
        }

        double travelTime = 0;
        i = 0;
        for (Vehicle vehicle : availableVehiclesList) { //constraint: if vehicle is more than 10 time steps away from passenger origin, set zValue to 0
            for (Node node : relocatableNodesList) {

                vehicle.createPath(vehicle.getLoc(), node);
                for (int x = 0; x < vehicle.getPath().size(); x++) {
                    travelTime += vehicle.getPath().get(x).getTraveltime(); //calculate total travel time from vehicle location to passenger origin
                }
                if (travelTime > 10 * timeIncrement) {
                    zValues[i] = c.intVar(0, 0);
                } else {
                    zValues[i] = c.intVar(0, 1);
                }
                travelTime = 0;
                i++;
            }
        }

        next = 0;
        for (Vehicle vehicle : availableVehiclesList) { //constraint: vehicle can either be assigned to one passenger or to travel empty to one node
            IloLinearNumExpr e = c.linearNumExpr();
            int vehicleNodesStart = next * relocatableNodesList.size();
            for (int z = vehicleNodesStart; z < vehicleNodesStart + relocatableNodesList.size(); z++) { //iterate through vehicle's node values
                e.addTerm(1, zValues[z]);
            }

            int vehiclePassengersStart = next * waitingList.size();
            for (int p = vehiclePassengersStart; p < vehiclePassengersStart + waitingList.size(); p++) { //iterate through vehicle's passenger assignments
                e.addTerm(1, xValues[p]);
            }
            c.addLe(e, 1); //sum of both must be <= 1
            next++;
        }

        /*next = 0;
        for (Vehicle vehicle : availableVehiclesList) { //constraint: vehicle can either be assigned to one passenger or to travel empty to one node (mimick for travelTimexValues)
            IloLinearNumExpr e = c.linearNumExpr();
            int vehicleNodesStart = next * relocatableNodesList.size();
            for (int z = vehicleNodesStart; z < vehicleNodesStart + relocatableNodesList.size(); z++) { //iterate through vehicle's node values
                e.addTerm(1, zValues[z]);
            }

            int vehiclePassengersStart = next * waitingList.size();
            for (int p = vehiclePassengersStart; p < vehiclePassengersStart + waitingList.size(); p++) { //iterate through vehicle's passenger assignments
                e.addTerm(1, travelTimexValues[p]);
            }
            c.addLe(e, 1); //sum of both must be <= 1
            next++;
        }*/




        IloLinearNumExpr summation = c.linearNumExpr();
        for (Node source : sources) { //objective: minimize the waiting list size (waiting list size - each passengersPerVehicle element for each passenger traveling from r to s)

            for (Node  destination : destinations) {
                //Node dest = destination.getDestination();

                for (Passenger passenger : waitingList) {

                    if (passenger.getOrigin() == source && passenger.getDestination() == destination) {
                        int index = waitingList.indexOf(passenger);

                        int betaIndex = matchPassengerRouteWithBetaIndex(passenger);

                        for (int p = index; p < xValues.length; p += waitingList.size()) {  //if passenger is on designated route, add its xValues value to summation
                            summation.addTerm(beta[betaIndex] * 1 * 20, xValues[p]);
                        }

                    }
                }
            }
        }

        int travelTimeCombo = 0;
        int iterator = 0;
        for (Vehicle v : availableVehiclesList) {
            for (Passenger p : waitingList) {

                v.createPath(v.getLoc(), p.getOrigin());
                for (int y = 0; y < v.getPath().size(); y++) {
                    travelTimeCombo += v.getPath().get(y).getTraveltime(); //calculate total travel time from vehicle location to passenger origin
                }
                int betaIndex = matchPassengerRouteWithBetaIndex(p);
                summation.addTerm(xValues[iterator], beta[betaIndex] * travelTimeCombo * -1 * 0.01); // -1 is used since terms are being subtracted
                iterator++;
            }
            travelTimeCombo = 0;
        }

        i = 0;
        for (Vehicle vehicle : availableVehiclesList) { //objective: minimize preemptive vehicle relocation
            for (Node node : relocatableNodesList) {
                summation.addTerm(alpha[relocatableNodesList.indexOf(node)] * 1, zValues[i]);
                i++;
            }
        }


        IloObjective objective = c.maximize(summation); // did not add waitingList size since size is just a constant offset
        c.add(objective);

        //try (FileOutputStream log = new FileOutputStream(getFilePath("cplex_log.txt"))) {
            c.setOut(null);
        //}

        c.solve();

        zValuesList.clear();
        for (int x = 0; x < zValuesSize; x++) {
            zValuesList.add(c.getValue(zValues[x]));
            //System.out.println(zValuesList.get(x));
        }

        /*int place = 0;
        for (Vehicle v: availableVehiclesList) {
            for (Passenger p: waitingList) {
                if (c.getValue(xValues[place]) != c.getValue(travelTimexValues[place])) {
                    System.out.println("Different");
                }
                place++;
            }
        }*/

        return xValues; //xValues holds each of the vehicle/passenger assignments

    }

    public void beginRouteToDestination (Vehicle vehicle, boolean writerOn) throws IOException {
        float totalTravelTime = 0;

        vehicle.createPath(vehicle.getLoc(), vehicle.getPassenger().getDestination());

        for (int i = 0; i < vehicle.getPath().size(); i++) {
            totalTravelTime += vehicle.getPath().get(i).getTraveltime(); //calculate total travel time from vehicle location to passenger origin
        }

        if (writerOn) simulationWriter.write("Vehicle #" + vehicle.getId() + " is beginning route to destination | Travel time: " + totalTravelTime + " seconds\n");


        if (vehicle.getPath().size() > 0) {
            if (vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() != vehicle.getPassenger().getDestination().getId()) {
                throw new RuntimeException("Vehicle is headed to destination ID " + vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() + " but passenger destination is located at ID " + vehicle.getPassenger().getDestination().getId());
            }
        }

        vehicle.setCounter(0); //reset counter (keeps track of node index in path array list)
        vehicle.createTravelTime();
    }

    public void beginRouteToPassenger (Vehicle vehicle, boolean writerOn) throws IOException{

        float totalTravelTime = 0;

       // System.out.println(vehicle.getLoc());
       // System.out.println(vehicle.getPassenger());

        vehicle.createPath(vehicle.getLoc(), vehicle.getPassenger().getOrigin());

        for (int i = 0; i < vehicle.getPath().size(); i++) {
            totalTravelTime += vehicle.getPath().get(i).getTraveltime(); //calculate total travel time from vehicle location to passenger origin
        }

        if (vehicle.isAlreadyAtTarget()) {
            vehicle.setAlreadyAtTarget(false);
            vehicle.setPickedUp(true);

            vehicle.createPath(vehicle.getLoc(), vehicle.getPassenger().getDestination());

            totalTravelTime = 0;
            for (int i = 0; i < vehicle.getPath().size(); i++) {
                totalTravelTime += vehicle.getPath().get(i).getTraveltime(); //calculate total travel time from vehicle location to passenger origin
            }

            if (writerOn) simulationWriter.write("(!) Vehicle #" + vehicle.getId() + " already at passenger location. Beginning route to destination | Travel time: " + totalTravelTime + " seconds\n");
            vehicle.setAlreadyBeginningRouteToDestination(true);

            if (vehicle.getPath().size() > 0) {
                if (vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() != vehicle.getPassenger().getDestination().getId()) {
                    throw new RuntimeException("Vehicle is headed to destination ID " + vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() + " but passenger destination is located at ID " + vehicle.getPassenger().getDestination().getId());
                }
            }
        }
        else {
            if (writerOn) simulationWriter.write("Vehicle #" + vehicle.getId() + " is beginning route to assigned passenger | Travel time: " + totalTravelTime + " seconds\n");
            vehicle.setBeginningRouteToPassenger(true);
            sumOfTravelTimes += totalTravelTime;

            if (vehicle.getPath().size() > 0) {
                if (vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() != vehicle.getPassenger().getOrigin().getId()) { //check if path destination and actual destination match
                    throw new RuntimeException("Vehicle is headed to passenger origin ID " + vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() + " but passenger is located at ID " + vehicle.getPassenger().getOrigin().getId());
                }
            }
        }
    }

    public void assignPassengerToVehicle (Vehicle vehicle, IloIntVar[] xValues, IloCplex c, boolean writerOn) throws IOException {

        try {

            float totalTravelTime = 0;
            float fastestTime = Integer.MAX_VALUE;

            if (!vehicle.isRequested()) {
                if (waitingList.isEmpty()) {
                    vehicle.setNoMoreRides(true);
                }
                else {

                    int iterator = 0; //iterate through CPLEX xValues indexes
                    int index_p = 0;    //iterate through waiting list indexes

                    for (Vehicle v : availableVehiclesList) {   //iterate through vehicles
                       // System.out.println("current waiting list: " + waitingList.size());
                        //System.out.println("(!) Vehicle #" + v.getId());
                        v.setIdle(true);
                        int offset = availableVehiclesList.indexOf(v)*waitingList.size();   //calculate beginning offset into xValues
                       // System.out.println("offset: " + offset);
                        iterator = offset;
                        for (int p = 0; p < waitingList.size(); p++) {
                            if (c.getValue(xValues[iterator]) == 1.0) { //check if specific vehicle is assigned to specific passenger
                                Passenger passenger = waitingList.get(index_p); //get corresponding passenger from waiting list
                                v.passenger = passenger;    //assign passenger to vehicle
                                //System.out.println("passenger: " + v.passenger);
                                passenger.setAssigned(true);
                                v.setIdle(false);
                                v.setSentToNode(false);
                                //System.out.println("(!!) Vehicle #" + v.getId() + " has been assigned to passenger " + "[" + v.getPassenger() + "]");

                            }
                                iterator++;
                                index_p++;
                        }
                        index_p = 0;
                    }

                    if (vehicle.passenger != null) {
                        vehicle.setRequested(true);
                        vehicle.used = true;
                        if (writerOn) simulationWriter.write("(!) Vehicle #" + vehicle.getId() + " has been assigned to passenger " + "[" + vehicle.getPassenger() + "]\n");
                        totalNumberOfPassengers++;
                        justAssignedVehicles.add(vehicle);
                    }
                   // availableVehiclesList.remove(vehicle);
                }
            }

        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    private void sendVehiclesToEmptyNodes(boolean writerOn) throws IOException {
        int iterator = 0;
        int index;
        int size = 0;

        for (int v = 0; v < zValuesList.size(); v += relocatableNodesList.size()) { //iterate for each vehicle
            Vehicle vehicle = availableVehiclesList.get(iterator);
            for (int n = v; n < v + relocatableNodesList.size(); n++) { //iterate each node (for each vehicle)
                if (zValuesList.get(n) == 1.0 && !vehicle.isSentToNode() && !vehicle.isRequested()) { //check if node assigned and if vehicle is not already en route to empty node
                    index = n - v;  // get nodesList index of node in zValueslist
                    if (vehicle.getNode() == relocatableNodesList.get(index)) {
                        vehicle.setAssignedSameNode(true);
                    } else {
                        vehicle.sentToNode = true;
                        vehicle.setAssignedSameNode(false);
                        //System.out.println("vehicle #" + vehicle.getId() + " assigned to " + nodesList.get(index));
                        vehicle.setNode(relocatableNodesList.get(index));
                    }
                }
            }
            iterator++;
        }

        for (Vehicle vehicle : availableVehiclesList) {
            if (vehicle.isSentToNode() && !vehicle.isEnRouteToNode()) {
                if (writerOn && !vehicle.isAssignedSameNode()) simulationWriter.write("Vehicle #" + vehicle.getId() + " sent to node " + vehicle.getNode() + "\n");
                vehicle.setEnRouteToNode(true);
            }
        }
    }

    public void removePassengersFromWaitingList () {
        waitingList.removeIf(passenger -> passenger.isAssigned());
    }

    public void removeVehiclesFromList () {
        availableVehiclesList.removeIf(vehicle -> vehicle.isRequested());
    }

    public void removePassengersFromPassengerList () {
        passengers.removeIf(passenger -> passenger.isDispatched());
    }

    public List<Link> shortestPath(Node origin, Node dest) {
        ArrayList<Link> output = new ArrayList<>();

        dijkstras(origin);

        Node curr = dest;
        while (curr != origin) {
            output.add(0, curr.getPred()); //missing parameters of link
            if (curr.getPred() != null) {
                curr = curr.getPred().getSource();
            }
            else {
                throw new RuntimeException(String.format("org.umn.research.evsimulator.Node is missing predecessor [id: %s, type: %s]",curr.getId(),curr.getType()));
            }
        }

        return output;
    }

    public Vehicle makeVehicle(Network network) {
        Random ran = new Random();
        //  int x = ran.nextInt(1)+1;
        int n;
        List<Link> Path = new ArrayList<>();
        //if(x==1)
        //{
        n = ran.nextInt(zoneList.size());
        Zone l = zoneList.get(n); //zone cast
        Location loc = l;
        Vehicle v = new Vehicle(this, 100, l);
        vehicleList.add(v);
        availableVehiclesList.add(v);
        v.setId(vehicleList.indexOf(v));
        v.net = network;
        return v;
        //}
    }

    private void scanNodes(String filename) {
        //creates nodelist to read in nodes from file
        try {
            File file = new File(filename);
            Scanner s = new Scanner(file);

            s.nextLine();
            int id;
            int type;
            while (s.hasNextLine()) {
                id = s.nextInt();
                type = s.nextInt();
                Node n = new Node(id, type);
                n.setCost(Double.MAX_VALUE);
                Zone temp = new Zone(n.getId(), n.getType());
                if (temp.getType() == 1000) {
                    zoneList.add(temp);
                    relocatableNodesList.add(n);
                    mainNodesList.add(n);
                } else {
                    fakeZoneList.add(temp);
                }
                nodesList.add(n);
                s.nextLine();
            }
            s.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    //creates hashmap for nodew with format <id of node, com.umn.research.evsimulator.Node>
    private void createNodeMap() {
        int count = 0;
        for (int i = 0; i < nodesList.size(); i++) {
            nodeMap.put(nodesList.get(i).getId(), nodesList.get(i));


        }
    }

    //reads in links to list of links
    private void readLinks(String fileName) {
        //creates list of links to store in links
        File file = new File(fileName);
        try {
            Scanner s = new Scanner(file);
            s.nextLine();
            //call method to get nodeMap
            while (s.hasNextLine()) {
                int idLink = s.nextInt();
                s.nextInt();
                int sourceLink = s.nextInt();
                //sourcesList.add(sourceLink);
                int destLink = s.nextInt();
                //destinationsList.add(destLink);
                float lengthInMiles = s.nextFloat() / 5280;
                float speedInMilesPerSecond = s.nextFloat() / 3600;
                float tt = lengthInMiles / speedInMilesPerSecond;
                s.nextFloat();
                s.nextFloat();
                s.nextInt();
                Link L = new Link(idLink, nodeMap.get(sourceLink), nodeMap.get(destLink), tt, lengthInMiles);
                //nodeMap.get(sourceLink).addOutgoing(L);
                //System.out.println(nodeMap.get(sourceLink));
                //System.out.println(nodeMap.get(sourceLink).outgoing);
                linksList.add(L);
                s.nextLine();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private void scanDemand(String fileName) {
        //creates arraylist of demand values
        File file = new File(fileName);
        try {
            Scanner s = new Scanner(file);
            s.nextLine();
            while (s.hasNextLine()) {
                s.nextInt();
                s.nextInt();
                s.nextInt();
                s.nextInt();
                s.nextInt();
                demand.add(s.nextDouble());
                s.nextLine();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

    }

    //reads values into departureIdMap hashmap
    private void readDepartureTimes(String fileName) {
        //creates a hashmap with key id and value an array of start time and duration
        File file = new File(fileName);

        int id = 0;
        try {
            Scanner s = new Scanner(file);
            s.nextLine();
            while (s.hasNextLine()) {
                int[] a = new int[2];
                id = s.nextInt();
                s.nextDouble();
                a[0] = s.nextInt();
                a[1] = s.nextInt();

                departureIdMap.put(id, a);
                s.nextLine();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    //reads in passenger data and creates passengers and put them into passenger array list
    private void createPassengers(String fileName, double scale) //call scanDemand before using this func and above func
    {
        File file = new File(fileName);
        try {
            Scanner s = new Scanner(file);
            int i = 0;
            int id = 0;
            s.nextLine();
            while (passengers.size() < 200/*s.hasNextLine()*/) {   //reads passengers in file
                //System.out.println(i);
                int departuretime;
                //while (s.hasNextLine())
                //{
                s.nextInt();
                s.nextInt();
                //Cast origin and destination as zone so that they can be used properly in the step function for shortest path
                Node origin = nodeMap.get(s.nextInt());

                Node dest = nodeMap.get(s.nextInt());


                int ast = s.nextInt();
                int start = departureIdMap.get(ast)[0];
                int end = start + (departureIdMap.get(ast)[1]);
                Random r = new Random();
                double demandval = demand.get(i);
                demandval *= scale; //scale demand by 'scale' value
                i++;
                double floor = Math.floor(demandval);
                double ceiling = Math.ceil(demandval);
                int value;
                for(int j = 0; j<ceiling; j++) {
                    value = (int) Math.round(floor + (ceiling-floor) * r.nextDouble()); // generates random value between floor and ceiling to see if passengers is made

                    if (value == ceiling) { //if value rounded to ceiling, create passenger. If it rounded to floor, do not create passenger
                        departuretime = r.nextInt(end - start) + start;
                        Passenger p = new Passenger(origin, dest, departuretime, id);
                        id++;
                        passengers.add(p);
                    }
                //  if(demandval>checker)
                //{
                }
                // }
                //s.nextLine();
                // }
                //System.out.println(s.hasNextLine());
                s.nextLine();
            }
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void dijkstras(Node source) {

        Set<Node> Q = new HashSet<Node>();

        for (Node n : nodesList) {
            n.setCost(Integer.MAX_VALUE);
            n.setPred(null);
        }

        source.setCost(0);

        Q.add(source);

        while (!Q.isEmpty()) {
            Node u = new Node(0, 0);
            double min = Integer.MAX_VALUE;

            for (Node n : Q) {
                if (n.getCost() < min) {
                    min = n.getCost();
                    u = n;
                }
            }

            Q.remove(u);

            for (Link uv : u.getOutgoing()) {
                Node v = uv.getDestination();
                double temp = u.getCost() + uv.getTraveltime();
                if (temp < v.getCost()) {
                    v.setCost(temp);
                    Q.add(v);
                    v.setPred(uv);
                }
            }
        }
    }

    public Zone matchLocationWithCorrespondingZone (Node node) { //treat node as a new starting zone for vehicle
        Zone location = null;
        for (int i = 0; i < this.getZoneList().size(); i++) {
            if (node.getId() == this.getZoneList().get(i).getId()) {
                location = this.getZoneList().get(i);
            }
        }
        if (location == null) {                                     //if location is not an official zone, check if it is a 'fake' zone
            for (int i = 0; i < this.getFakeZoneList().size(); i++) {
                if (node.getId() == this.getFakeZoneList().get(i).getId()) {
                    location = this.getFakeZoneList().get(i);
                }
            }
        }
        return location;
    }

    public int matchPassengerRouteWithBetaIndex (Passenger passenger) {

        int originIndex = passenger.getOrigin().getId() % 1000 - 1; // mod 1000 to get individual ID, subtract one to get index
        int destinationIndex = passenger.getDestination().getId() % 1000 - 1;

        int index = originIndex * 24 + destinationIndex;

        //System.out.println("Origin: " + passenger.getOrigin().getId() + " Destination: " + passenger.getDestination().getId() + " Index: " + index);

        return index;
    }

    private void printVehiclePath (Vehicle vehicle) {
            System.out.print("Path: " + vehicle.getPath().get(0).getSource().getId());
            for (Link link : vehicle.getPath()) {
                if (vehicle.getPath().indexOf(link) != 0) {
                    System.out.print(" -> ");
                }
                System.out.print(link.getDestination().getId());
            }
            System.out.println();
    }

    private static String getFilePath(String fileName) {
        return System.getenv("RESOURCES_FOLDER")+"/"+fileName;
    }
}