package org.umn.research.evsimulator;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Data
@EqualsAndHashCode
public class Network {

    private List<Node> nodesList = new ArrayList<>();
    private List<Double> demand = new ArrayList<>();
    private HashMap<Integer, int[]> departureIdMap = new HashMap<>();
    private HashMap<Integer, Node> nodeMap = new HashMap<>();
    private List<Passenger> passengers = new ArrayList<>();
    private List<Link> linksList = new ArrayList<>();
    private List<Vehicle> vehicleList = new ArrayList<>();
    private List<Zone> zoneList = new ArrayList<>();
    private List<Passenger> waitingList = new ArrayList<>();

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

    public List<Passenger> simulate() {
        for (Passenger p : passengers) {
            if (p.getDeparturetime() < 1000)  //change to '< t' when finished
            {
                waitingList.add(p); // if passenger departure time is less than t, add passenger to waiting list
            }
        }

        System.out.println("Waiting List size: " + waitingList.size());
        System.out.println();

        for (Vehicle vehicle : vehicleList) { //currently looks for passenger closest to vehicle, should ideally be closest vehicle to passenger (remove waitlist removal in step)

            System.out.println("Vehicle #" + vehicle.getId());
            float totalTravelTime = 0;
            float fastestTime = Integer.MAX_VALUE;
            Passenger passenger = null;

            for (Passenger p : waitingList) { //find passenger that is closest to vehicle

                vehicle.createPath(vehicle.getLoc(), p.getOrigin());

                for (int i = 0; i < vehicle.getPath().size(); i++) {
                    totalTravelTime += vehicle.getPath().get(i).getTraveltime();
                }

                if (totalTravelTime < fastestTime) { //find passenger that can be reached the fastest among fleet of SAEVs
                    fastestTime = totalTravelTime;
                    passenger = p;
                }

                totalTravelTime = 0;
            }

            vehicle.createPath(vehicle.getLoc(), passenger.getOrigin()); //create path to passenger origin from current vehicle position

            Zone location = (Zone) vehicle.getLoc(); //treat vehicle location as a starting zone
            System.out.println("Vehicle Position ID: " + location.getId());
            System.out.println("Heading towards ID " + passenger.getOrigin().getId());

            if (vehicle.getPath().size() > 0) {
                printVehiclePath(vehicle);
                if (vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() != passenger.getOrigin().getId()) { //check if path destination and actual destination match
                    throw new RuntimeException("Vehicle is headed to passenger origin ID " + vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() + " but passenger is located at ID " + passenger.getOrigin().getId());
                }
            }

            for (int i = 0; i < vehicle.getPath().size(); i++) {
                totalTravelTime += vehicle.getPath().get(i).getTraveltime(); //calculate total travel time from vehicle location to passenger origin
            }

            System.out.println("Total Travel Time to Passenger: " + totalTravelTime + " seconds");
            totalTravelTime = 0; //reset travel time for next calculation

            while (!vehicle.isPickedUp()) {
                vehicle.step(waitingList, nodesList, passenger); // moves vehicle forward on its assigned path
            }

            location = matchLocationWithCorrespondingZone(passenger.getOrigin()); // treat passenger origin (also vehicle's current location) as a starting zone
            vehicle.setLoc(location); //set vehicle location to passenger origin (as a zone)

            System.out.println("Reached Passenger Origin ID: " + location.getId());

            vehicle.createPath(location, passenger.getDestination()); //create path from passenger origin to destination

            if (vehicle.getPath().size() > 0) {
                printVehiclePath(vehicle);
                if (vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() != passenger.getDestination().getId()) {
                    throw new RuntimeException("Vehicle is headed to destination ID " + vehicle.getPath().get(vehicle.getPath().size() - 1).getDestination().getId() + " but passenger destination is located at ID " + passenger.getDestination().getId());
                }
            }

            location = (Zone) vehicle.getLoc();
            System.out.println("Vehicle Position ID: " + location.getId());
            System.out.println("Heading towards ID " + passenger.getDestination().getId());

            for (int i = 0; i < vehicle.getPath().size(); i++) {
                totalTravelTime += vehicle.getPath().get(i).getTraveltime();
            }
            System.out.println("Total Travel Time to Destination: " + totalTravelTime + " seconds");

            System.out.println("Reached Passenger Destination ID: " + passenger.getDestination().getId());

            vehicle.setCounter(0); //reset counter (keeps track of node index in path array list)
            vehicle.createTravelTime();

            while (vehicle.isPickedUp()) {
                vehicle.step(waitingList, nodesList, passenger); // moves vehicle forward on its assigned path
            }
            waitingList.remove(passenger); //passenger has reached destination, so remove from waiting list

            location = matchLocationWithCorrespondingZone(passenger.getDestination());
            vehicle.setLoc(location); //set vehicle location as passenger destination (as a zone)

            if (waitingList.isEmpty()) {
                break;
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("EV ridesharing simulated");
        return waitingList;
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

    //creats hashmap for nodew with format <id of node, com.umn.research.evsimulator.Node>
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
                int sourcelink = s.nextInt();
                int destlink = s.nextInt();
                float lengthInMiles = s.nextFloat() / 5280;
                float speedInMilesPerSecond = s.nextFloat() / 3600;
                float tt = lengthInMiles / speedInMilesPerSecond;
                s.nextFloat();
                s.nextFloat();
                s.nextInt();
                Link L = new Link(idLink, nodeMap.get(sourcelink), nodeMap.get(destlink), tt);
                //nodeMap.get(sourcelink).addOutgoing(L);
                //System.out.println(nodeMap.get(sourcelink));
                //System.out.println(nodeMap.get(sourcelink).outgoing);
                linksList.add(L);
                s.nextLine();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private void scanDemand(String fileName) {
        //creats arraylist of demand values
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
            while (passengers.size() < 5000) {   //reads all passengers in file
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
                //for(int j = 0; j<ceiling; j++)
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

// use instance variable for vehicle list and add vehicles to vehicle like in make vehicle function
