package com.maxdemarzi;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.test.server.HTTP;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static junit.framework.TestCase.assertEquals;

public class RoutertblTest {

    ObjectMapper mapper = new ObjectMapper();

    @Rule
    public final Neo4jRule neo4j = new Neo4jRule()
            .withFixture(FIXTURE)
            .withProcedure(Procedures.class);

    @Test
    public void testRoutertbl() throws Exception {

        HTTP.Response response = HTTP.POST(neo4j.httpURI().resolve("/db/data/transaction/commit").toString(), QUERY);
        ArrayList<HashMap> results = mapper.readValue(response.get("results").get(0).get("data").get(0).get("row").get(0), ArrayList.class);
        assertEquals(9, results.size());

    }

    private static final Map QUERY =
            singletonMap("statements", asList(singletonMap("statement",
                    "CALL com.maxdemarzi.routertbl('10.175.64.2', '10.175.122.10')")));

    private static final String FIXTURE =
        "CREATE (r1:VPC {id: 'l3-r1', ip:'10.175.64.2'})" +
        "CREATE (r2:VPC {id: 'l3-r2', ip:'10.175.66.6'})" +
        "CREATE (r3:VPC {id: 'l3-r3', ip:'10.182.32.3'})" +
        "CREATE (r4:VPC {id: 'l3-r4', ip:'10.175.32.1'})" +
        "CREATE (i1:Interface {id: 'Vlan111'})" +
        "CREATE (i2:Interface {id: 'port-channel1'})" +
        "CREATE (i3:Interface {id: 'Vlan200'})" +
        "CREATE (i4:Interface {id: 'Vlan205'})" +
        "CREATE (s1:Subnet {id: 'l2-s1'})" +
        "CREATE (s2:Subnet {id: 'l2-s2'})" +
        "CREATE (s3:Subnet {id: 'l2-s3'})" +
        "CREATE (s4:Subnet {id: 'l2-s4'})" +
        "CREATE (s5:Subnet {id: 'l2-s5'})" +
        "CREATE (ser1:EC2 {ip: '10.175.122.10'})" +
        "CREATE (ser2:EC2 {ip: '10.175.122.12'})" +
        "CREATE (ser3:EC2 {ip: '10.175.122.13'})" +

        "CREATE (s1)-[:ATTACHED_TO]->(r1)" +
        "CREATE (r1)-[:ROUTES_TO {routes:['10.175.112.0/20', '10.175.108.0/22', '10.182.14.64/26', '10.182.22.0/25']}]->(r2)" +
        "CREATE (r1)-[:ROUTES_TO {routes:['10.182.32.0/29']}]->(r3)" +
        "CREATE (r1)-[:ROUTES_TO {routes:['10.182.32.1/32']}]->(r4)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.66.9', mac:'0081.c4dc.6161' }]->(i1)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.66.5', mac:'e4c7.221c.87c1' }]->(i2)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.2', mac:'0081.c4dc.6161' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.4', mac:'1402.ec86.c6f8' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.5', mac:'1402.ec86.c664' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.6', mac:'1402.ec86.c6f8' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.7', mac:'1402.ec86.c664' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.8', mac:'1402.ec86.c63c' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.9', mac:'1402.ec86.c6f0' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.10', mac:'1402.ec86.c63c' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.11', mac:'1402.ec86.c6f0' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.12', mac:'1402.ec86.c610' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.13', mac:'1402.ec86.c634' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.14', mac:'1402.ec86.c610' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.47', mac:'1402.ec86.c674' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.48', mac:'1402.ec86.c674' }]->(i3)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.65', mac:'0000.0c9f.f0cd' }]->(i4)" +
        "CREATE (r2)-[:ATTACHED_TO {ip:'10.175.122.66', mac:'0081.c4dc.6161' }]->(i4)" +
        "CREATE (i3)-[:NACL_TO { port:'Po111', mac:'0081.c4dc.6161'}]->(s4)" +
        "CREATE (i3)-[:NACL_TO { port:'Po2', mac:'1402.ec86.c610'}]->(s3)" +
        "CREATE (i3)-[:NACL_TO { port:'Po2', mac:'1402.ec86.c634'}]->(s3)" +
        "CREATE (i3)-[:NACL_TO { port:'Po3', mac:'1402.ec86.c63c'}]->(s5)" +
        "CREATE (i3)-[:NACL_TO { port:'Po111', mac:'1402.ec86.c640'}]->(s4)" +
        "CREATE (s3)-[:NACL_TO { port:'Eth1/9', mac:'1402.ec86.c610' }]->(ser2)" +
        "CREATE (s3)-[:NACL_TO { port:'Eth1/10', mac:'1402.ec86.c634' }]->(ser3)" +
        "CREATE (s5)-[:NACL_TO { port:'Eth1/8', mac:'1402.ec86.c63c' }]->(ser1)";

}
