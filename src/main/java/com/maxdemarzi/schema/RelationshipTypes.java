package com.maxdemarzi.schema;

import org.neo4j.graphdb.RelationshipType;

public enum RelationshipTypes implements RelationshipType {
    NACL_TO,
    ATTACHED_TO,
    ROUTES_TO,
}
