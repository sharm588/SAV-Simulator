package org.umn.research.evsimulator;

import lombok.Data;

import java.util.Objects;

@Data
public class Link implements Location {
    private Node source;
    private Node destination;
    private int id;
    private float traveltime;

    public Link(int id, Node source, Node destination, float traveltime) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.traveltime = traveltime;

        source.addOutgoing(this);
    }

    public String toString() {
        return Integer.toString(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return id == link.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
//in network class create link array and call readlink function to add link to array
//Travel time length/ ffspeed
//create hashmap in network class and then call that function to read in the correct nodes for links class
