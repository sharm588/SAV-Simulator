package org.umn.research.evsimulator;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class NetworkTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void loadNetwork() {
        Network network = Network.createNetwork();

        assertTrue(network.getNodesList().size() > 0);
        assertTrue(network.getNodeMap().size() > 0);
        assertTrue(network.getLinksList().size() > 0);
        assertTrue(network.getDemand().size() > 0);
        assertTrue(network.getDepartureIdMap().size() > 0);
        assertTrue(network.getPassengers().size() > 0);
    }

    @Test
    public void shortestPath() {
        Network network = Network.createNetwork();
        List<Node> networkNodes = network.getNodesList();
        List<Link> links = network.shortestPath(networkNodes.get(0), networkNodes.get(networkNodes.size()-1));
        assertTrue(links.size() > 0);
    }

    @Test
    public void makeVehicle() {
        Network network = Network.createNetwork();
        Vehicle vehicle = network.makeVehicle();
        assertNotNull(vehicle);
    }

    @Test
    public void simulate() {
        Network network = Network.createNetwork();
        Vehicle vehicle = network.makeVehicle();

        assertNotNull(vehicle);

        List<Passenger> waitingList = network.simulate();
        //assertTrue(waitingList.size() > 0);
    }
}