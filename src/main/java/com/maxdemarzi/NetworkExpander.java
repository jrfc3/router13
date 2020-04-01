package com.maxdemarzi;

import com.maxdemarzi.schema.RelationshipTypes;
import inet.ipaddr.*;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.helpers.collection.Pair;

import java.util.*;

public class NetworkExpander implements PathExpander<String> {
    private String ip;
    private IPAddress ipAddress;

    public NetworkExpander(String ip) {
        this.ip = ip;
        this.ipAddress = new IPAddressString(ip).getAddress();
    }

    @Override
    public Iterable<Relationship> expand(Path path, BranchState branchState) {
        Node last = path.endNode();

        ArrayList<Relationship> relationships = new ArrayList<>();
        ArrayList<Pair<Integer, Relationship>> unorderedRoutes = new ArrayList<>();

        // Layer 3 Longest prefix matching
        for(Relationship routes_to : last.getRelationships(RelationshipTypes.ROUTES_TO, Direction.OUTGOING)) {
            String[] routes = (String[])routes_to.getProperty("routes", new String[]{});
            for(String route : routes) {
                IPAddressString addrString = new IPAddressString(route);
                if(addrString.getAddress().contains(ipAddress)) {
                    unorderedRoutes.add(Pair.of(addrString.getNetworkPrefixLength(), routes_to));
                }
            }
        }
        unorderedRoutes.sort(Comparator.comparingInt(p -> - p.first()));
        unorderedRoutes.forEach( p -> relationships.add(p.other()));

        // If there is no Layer 3 next hop, go to Layer 2
        if (relationships.isEmpty()) {
            // Layer 2
            for (Relationship attached_to : last.getRelationships(RelationshipTypes.ATTACHED_TO, Direction.OUTGOING)) {
                String ip = (String) attached_to.getProperty("ip");
                if (ip.equals(this.ip)) {
                    relationships.add(attached_to);
                }
            }
            String mac = "";
            Relationship lastRel = path.lastRelationship();
            if (lastRel != null) {
                mac = (String) lastRel.getProperty("mac", "");
            }

            for (Relationship nacl_to : last.getRelationships(RelationshipTypes.NACL_TO, Direction.OUTGOING)) {
                String mac_too = (String) nacl_to.getProperty("mac", "");
                if (mac.equals(mac_too)) {
                    relationships.add(nacl_to);
                }
            }
        }
        return relationships;
    }

    @Override
    public PathExpander reverse() {
        return null;
    }
}
