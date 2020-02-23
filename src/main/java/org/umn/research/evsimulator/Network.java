package org.umn.research.evsimulator;

import ilog.concert.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import ilog.cplex.IloCplex;

@Data
@EqualsAndHashCode
public class Network {

    public ArrayList<Integer> oneVehicleToPassenger = new ArrayList<>();
    public ArrayList<Integer> onePassengerToVehicle = new ArrayList<>();
    private List<Node> nodesList = new ArrayList<>();
    private List<Double> demand = new ArrayList<>();
    private HashMap<Integer, int[]> departureIdMap = new HashMap<>();
    private HashMap<Integer, Node> nodeMap = new HashMap<>();
    private List<Passenger> passengers = new ArrayList<>();
    private List<Link> linksList = new ArrayList<>();
    private List<Vehicle> vehicleList = new ArrayList<>();
    private List<Zone> zoneList = new ArrayList<>();
    private List<Passenger> waitingList = new ArrayList<>();
    private List<Vehicle>availableVehiclesList = new ArrayList<>();
    private List<Node>sourcesList = new ArrayList<>();
    private List<Node>destinationsList = new ArrayList<>();
    private int endTime = 0;

    public static Network createNetwork() {
        Network network = new Network();
        network.scanNodes(getFilePath("nodes.txt"));
        network.createNodeMap();
        network.readLinks(getFilePath("links.txt"));
        network.scanDemand(getFilePath("dynamic_od.txt"));
        network.readDepartureTimes(getFilePath("demand_profile.txt"));
        network.createPassengers(getFilePath("dynamic_od.txt"));


        return network;
    }

    public List<Passenger> simulate(float time) {

        for (int i = 0; i < passengers.size(); i++) {
            oneVehicleToPassenger.add(0);
        }
        for (int i = 0; i < vehicleList.size(); i++) {
            onePassengerToVehicle.add(0);
        }

        for (Passenger p : passengers) { //fill waiting list, change to '< t' when finished
            if (p.getDeparturetime() < 1000)
            {
                waitingList.add(p);
            }
        }

        System.out.println("Waiting List size: " + waitingList.size());
        System.out.println();

        for (Vehicle vehicle : vehicleList) { //assign a passenger to each vehicle of fleet

            assignPassengerToVehicle(vehicle);

        }
        System.out.println();
        for (int i = 0; i < time; i += 30) { //simulate SAEV in 30 second intervals
            System.out.println("Time: " + i + " seconds");

            for (Vehicle vehicle : vehicleList) {


                if (vehicle.isNotMoving()) { //check if vehicle is at zone, just picked up passenger, or just dropped off passenger

                    if (vehicle.isPickedUp()) {

                        beginRouteToDestination(vehicle);

                    } else if (!vehicle.isPickedUp() && !vehicle.isDroppedOff()) {

                        beginRouteToPassenger(vehicle);

                    } else if (vehicle.isDroppedOff()) {
                        vehicle.setDroppedOff(false);
                        vehicle.setPickedUp(false);
                        vehicle.setRequested(false);
                        vehicle.setCounter(0); //reset counter (keeps track of node index in path array list)

                        int indexVehicle = vehicleList.indexOf(vehicle);
                        int indexPassenger = passengers.indexOf(vehicle.getPassenger());
                        onePassengerToVehicle.set(indexVehicle, onePassengerToVehicle.get(indexVehicle)-1);
                        oneVehicleToPassenger.set(indexPassenger, oneVehicleToPassenger.get(indexPassenger)-1);

                        if (!vehicle.isNoMoreRides()) {
                            assignPassengerToVehicle(vehicle);
                            beginRouteToPassenger(vehicle);
                        } else {
                            vehicle.setDroppedOff(true);
                            //System.out.println("Vehicle #" + vehicle.getId() + " is idle");
                            vehicle.setAlreadyPrintedDropOff(true);
                        }

                    }
                    vehicle.setNotMoving(false);
                }
                vehicle.step(waitingList, nodesList, vehicle.passenger);

                if (vehicle.isJustPickedUp()) {
                    System.out.println("(!) Vehicle #" + vehicle.getId() + " picked up passenger " + "[" + vehicle.getPassenger() + "]");

                    Location location = matchLocationWithCorrespondingZone(vehicle.getPassenger().getOrigin()); // treat passenger origin (also vehicle's current location) as a starting zone
                    vehicle.setLoc(location); //set vehicle location to passenger origin (as a zone)

                    vehicle.setJustPickedUp(false);
                    vehicle.setNotMoving(true);
                }

                if (vehicle.isDroppedOff()) {
                    if (!vehicle.isAlreadyPrintedDropOff()) {
                        System.out.println("(!) Vehicle #" + vehicle.getId() + " dropped off passenger " + "[" + vehicle.getPassenger() + "]");
                        availableVehiclesList.add(vehicle);
                        if (vehicle.isNoMoreRides()) {
                            vehicle.setAlreadyPrintedDropOff(true);
                        }
                    }

                    vehicle.setPickedUp(false);
                    vehicle.setNotMoving(true);

                    Location location = matchLocationWithCorrespondingZone(vehicle.getPassenger().getDestination());
                    vehicle.setLoc(location); //set vehicle location as passenger destination (as a zone)
                    vehicle.setCounter(0);

                }
                else if (!vehicle.isPickedUp() && !vehicle.isAlreadyBeginningRouteToPassenger()) {
                    System.out.println("Vehicle #" + vehicle.getId() + " is heading towards passenger" );
                }
                else if (vehicle.isPickedUp() && !vehicle.isAlreadyBeginningRouteToDestination()) {
                    System.out.println("Vehicle #" + vehicle.getId() + " is driving passenger to destination");
                }
                vehicle.setAlreadyBeginningRouteToDestination(false);
                vehicle.setAlreadyBeginningRouteToPassenger(false);
            }

            if (waitingList.isEmpty()) {
                System.out.println("(!) Waiting list is empty");
                boolean done = true;
                for (Vehicle vehicle : vehicleList) {
                    if (!vehicle.isNoMoreRides() || !vehicle.isDroppedOff()) {
                        done = false;
                    }
                }
                if (done) {
                    break;
                }
            }

            endTime = i;
            System.out.println();
        }

        System.out.println();
        System.out.println("EV ridesharing simulated in " + (endTime + 30) + " seconds");
        System.out.println();
        return waitingList;
    }

    public void beginRouteToDestination (Vehicle vehicle) {
        float totalTravelTime = 0;

        vehicle.createPath(vehicle.getLoc(), vehicle.getPassenger().getDestination());

        for (int i = 0; i < vehicle.getPath().size(); i++) {
            totalTravelTime += vehicle.getPath().get(i).getTraveltime(); //calculate total travel time from vehicle location to passenger origin
        }

        System.out.println("Vehicle #" + vehicle.getId() + " is beginning route to destination | Travel time: " + totalTravelTime + " seconds");

        if (vehicle.getPath().size() > 0) {
            printVehiclePath(vehicle);
            if (vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() != vehicle.getPassenger().getDestination().getId()) {
                throw new RuntimeException("Vehicle is headed to destination ID " + vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() + " but passenger destination is located at ID " + vehicle.getPassenger().getDestination().getId());
            }
        }

        vehicle.setCounter(0); //reset counter (keeps track of node index in path array list)
        vehicle.createTravelTime();
    }

    public void beginRouteToPassenger (Vehicle vehicle) {

        float totalTravelTime = 0;

        System.out.println(vehicle.getLoc());
        System.out.println(vehicle.getPassenger());


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

            System.out.println("(!) Vehicle #" + vehicle.getId() + " already at passenger location. Beginning route to destination | Travel time: " + totalTravelTime + " seconds");
            vehicle.setAlreadyBeginningRouteToDestination(true);

            if (vehicle.getPath().size() > 0) {
                printVehiclePath(vehicle);
                if (vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() != vehicle.getPassenger().getDestination().getId()) {
                    throw new RuntimeException("Vehicle is headed to destination ID " + vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() + " but passenger destination is located at ID " + vehicle.getPassenger().getDestination().getId());
                }
            }
        }
        else {
            System.out.println("Vehicle #" + vehicle.getId() + " is beginning route to assigned passenger | Travel time: " + totalTravelTime + " seconds");
            vehicle.setAlreadyBeginningRouteToPassenger(true);

            if (vehicle.getPath().size() > 0) {
                printVehiclePath(vehicle);
                if (vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() != vehicle.getPassenger().getOrigin().getId()) { //check if path destination and actual destination match
                    throw new RuntimeException("Vehicle is headed to passenger origin ID " + vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() + " but passenger is located at ID " + vehicle.getPassenger().getOrigin().getId());
                }
            }
        }
    }

    public void assignPassengerToVehicle (Vehicle vehicle) {

        constraints();
        float totalTravelTime = 0;
        float fastestTime = Integer.MAX_VALUE;


        if (!vehicle.isRequested()) {

            if (waitingList.isEmpty()) {
                vehicle.setNoMoreRides(true);
            }
            else {
                for (Passenger p : waitingList) { //find passenger that is closest to vehicle

                    vehicle.createPath(vehicle.getLoc(), p.getOrigin());

                    for (int i = 0; i < vehicle.getPath().size(); i++) {
                        totalTravelTime += vehicle.getPath().get(i).getTraveltime();
                    }

                    if (totalTravelTime < fastestTime) { //find passenger that can be reached the fastest among fleet of SAEVs
                        fastestTime = totalTravelTime;
                        vehicle.setPassenger(p);
                        System.out.println(vehicle.getPassenger().getOrigin());
                    }

                    totalTravelTime = 0;
                }
                waitingList.remove(vehicle.getPassenger());
                vehicle.setRequested(true);
                availableVehiclesList.remove(vehicle);
                System.out.println("(!) Vehicle #" + vehicle.getId() + " has been assigned to passenger " + "[" + vehicle.getPassenger() + "]");
            }
        }

       /* int indexVehicle = vehicleList.indexOf(vehicle);
        int indexPassenger = passengers.indexOf(vehicle.getPassenger());
        onePassengerToVehicle.set(indexVehicle, onePassengerToVehicle.get(indexVehicle)+1);
        oneVehicleToPassenger.set(indexPassenger, oneVehicleToPassenger.get(indexPassenger)+1);

        for (int x : onePassengerToVehicle) {
            if (x > 1) {
                System.out.println("break1");
            }
        }

        for (int x : oneVehicleToPassenger) {
            if (x > 1) {
                System.out.println("break2");

            }
        }*/

    }

    public void constraints () {
        try {
            int i = 0;
            int size = availableVehiclesList.size() * waitingList.size();
            IloCplex c = new IloCplex();
            IloIntVar[] xValues = c.intVarArray(size, 0, 1);



            for (Vehicle vehicle : availableVehiclesList) { // constraint: each vehicle is assigned to <= 1 passenger [one vehicle to passenger]
                IloLinearNumExpr e = c.linearNumExpr();
                for (Passenger passenger : waitingList) {
                    xValues[i] = c.intVar(0, 1);
                    e.addTerm(1, xValues[i]);
                    i++;

                }
                c.addLe(e, 1);
            }

            int next = 0;
            for (Passenger passenger : waitingList) { //constraint: each passenger is assigned to <= 1 vehicle [one passenger to vehicle]
                IloLinearNumExpr e = c.linearNumExpr();
                for (int p = next; p < xValues.length; p += waitingList.size()) {
                    e.addTerm(1, xValues[p]);
                }
                next++;
                c.addLe(e, 1);
            }


            /*for (Passenger passenger : waitingList) {   //constraint: each passenger is assigned to <= 1 vehicle [one passenger to vehicle]
                IloLinearNumExpr e1 = c.linearNumExpr();
                for (Vehicle vehicle : availableVehiclesList) {
                    if (j >= waitingList.size()) {
                        break;
                    }
                    //xValues[j] = c.intVar(0, 1);
                    //e1.addTerm(1, xValues[j]);
                    j += waitingList.size();
                }
                originalJValue++;
                j = originalJValue;


                c.addLe(e1, 1);
            }*/

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
            IloLinearNumExpr summation = c.linearNumExpr();
            for (Node source : sources) { //objective: minimize the waiting list size (waiting list size - each passengersPerVehicle element for each passenger traveling from r to s)

                for (Node  destination : destinations) {
                    //Node dest = destination.getDestination();


                        for (Passenger passenger : waitingList) {
                            //System.out.println("check1");
                            if (passenger.getOrigin() == source && passenger.getDestination() == destination) {
                                int index = waitingList.indexOf(passenger);
                               // System.out.println("check2");
                                for (int p = index; p < xValues.length; p += waitingList.size()) {  //if passenger is on designated route, add its xValues value to summation
                                    summation.addTerm(1, xValues[p]);
                                }

                            }

                        }

                    //c.addLe(c.sum(xValues), 1); //one passenger to vehicle
                    /*
                    System.out.println(c.solve());
                    System.out.println();
                    System.out.println("val: " + c.getValue(summation));
                    System.out.println();
                    c.remove(objective);*/





                }
            }

            int travelTimeCombo = 0;
            int iterator = 0;
            for (Vehicle v : availableVehiclesList) {
                for (Passenger p : waitingList) {
                    v.createPath(v.getLoc(), p.getOrigin());
                    for (int y = 0; y < v.getPath().size(); y++) {
                        travelTimeCombo += v.getPath().get(y).getTraveltime();
                        //calculate total travel time from vehicle location to passenger origin
                    }
                    summation.addTerm(xValues[iterator], travelTimeCombo);
                    iterator++;
                }
                travelTimeCombo = 0;
            }

            IloObjective objective = c.maximize(summation);
            c.add(objective);
            c.solve();
            System.out.println(c.solve());
            for (int a = 0; a < xValues.length; a++) {
                System.out.println(c.getValue(xValues[a]));
                if (a % waitingList.size() == 0) {
                    System.out.println();
                }
            }

        }
        catch (IloException e) {
            e.printStackTrace();
        }


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
                Zone temp = n.identifyType(n);
                if (temp.getId() != -1) {
                    zoneList.add(temp);
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
                Link L = new Link(idLink, nodeMap.get(sourceLink), nodeMap.get(destLink), tt);
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
    private void createPassengers(String fileName) //call scanDemand before using this func and above func
    {
        File file = new File(fileName);
        try {
            Scanner s = new Scanner(file);
            while (passengers.size() < 220) {   //reads passengers in file
                int i = 0;
                int departuretime;
                s.nextLine();
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
                i++;
                double floor = Math.floor(demandval);
                double ceiling = Math.ceil(demandval);
                Random r1 = new Random();
                double checker;
                //\]for(int j = 0; j<ceiling; j++)
                //{
                //generates random value to see if passengers is made
                //checker = floor + (ceiling-floor)*r1.nextDouble();
                //generates random departure time for each passenger
                departuretime = r.nextInt(end - start) + start;
                //  if(demandval>checker)
                //{
                Passenger p = new Passenger(origin, dest, departuretime);
                passengers.add(p);
                //}
                // }
                //s.nextLine();
                // }
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
        return location;
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


/*
public void constraints () {
        try {
            int i = 0;
            int j = 0;
            int originalJValue = 0;
            int size = availableVehiclesList.size() * waitingList.size();
            IloCplex c = new IloCplex();
            IloIntVar[] xValues = c.intVarArray(size, 0, 1);

            for (Vehicle vehicle : availableVehiclesList) { // constraint: each vehicle is assigned to <= 1 passenger [one vehicle to passenger]
                IloLinearNumExpr e = c.linearNumExpr();
                for (Passenger passenger : waitingList) {
                    xValues[i] = c.intVar(0, 1);
                    e.addTerm(1, xValues[i]);
                    i++;

                }
                c.addLe(e, 1);
            }


            for (Passenger passenger : waitingList) {   //constraint: each passenger is assigned to <= 1 vehicle [one passenger to vehicle]
                IloLinearNumExpr e = c.linearNumExpr();
                for (Vehicle vehicle : availableVehiclesList) {
                    if (j >= waitingList.size()) {
                        break;
                    }
                    xValues[j] = c.intVar(0, 1);
                    e.addTerm(1, xValues[j]);
                    j += waitingList.size();
                }
                originalJValue++;
                j = originalJValue;


                c.addLe(e, 1);
            }

            int k = 0;
            for (Node source : nodesList) { //objective: minimize the waiting list size (waiting list size - each passengersPerVehicle element for each passenger traveling from r to s)

                for (Node  dest : nodesList) {
                    //Node dest = destination.getDestination();
                    IloLinearNumExpr summation = c.linearNumExpr();

                    for (Vehicle vehicle : availableVehiclesList) {

                        for (Passenger passenger : waitingList) {
                            System.out.println("Source: " + source);
                            System.out.println("Destination: " + dest);
                            System.out.println("Passenger Origin: " + passenger.getOrigin());
                            System.out.println("Passenger Destination: " + passenger.getDestination());
                            if (passenger.getOrigin() == source && passenger.getDestination() == dest) {
                                System.out.println("checker");
                                xValues[k] = c.intVar(0, 1);
                                summation.addTerm(1, xValues[k]);
                                k++;

                            }

                        }


                    }
                    k = 0;
                    IloObjective objective = c.maximize(summation);
                    c.add(objective);
                    c.solve();
                    System.out.println();
                    System.out.println("val: " + c.getValue(summation));
                    System.out.println();
                    c.remove(objective);





                }
            }



        }
        catch (IloException e) {
            e.printStackTrace();
        }


    }

 */