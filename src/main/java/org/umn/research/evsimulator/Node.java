package org.umn.research.evsimulator;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class Node {
    private int id;
    private int type;
    private double cost;
    private Link pred;
    private List<Link> outgoing;

    public Node(int id, int type) {
        this.id = id;
        this.type = type;
        outgoing = new ArrayList<>();
    }

    public String toString() {
        return id + " " + type;
    }

    public void addOutgoing(Link link) {
        outgoing.add(link);
    }

    public List<Link> getOutgoing() {
        return outgoing;
    }

    public Zone identifyType(Node N) {
        if (N.type == 1000) {
            Zone z = new Zone(N.id, N.type);
            return z;
        } else {
            Zone fake = new Zone(-1, 100);
            return fake;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


//create an array list of outgoing link from node
