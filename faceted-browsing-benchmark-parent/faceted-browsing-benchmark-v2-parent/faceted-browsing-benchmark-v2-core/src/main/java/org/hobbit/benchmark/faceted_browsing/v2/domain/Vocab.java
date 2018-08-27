package org.hobbit.benchmark.faceted_browsing.v2.domain;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class Vocab {
	public static final Property root = property("root");
	public static final Property parent = property("parent");
	public static final Property alias = property("alias");

	public static final Property expr = property("alias");
	public static final Property constraint = property("constraint");
	public static final Property enabled = property("enabled");

	public static final Property value = property("value");
	public static final Property facetCount = property("facetCount");
	public static final Property facetValueCount = property("facetValueCount");

	
	public static final Property totalValueCount = property("totalValueCount");
	public static final Property distinctValueCount = property("distinctValueCount");
	public static final Property min = property("min");
	public static final Property minInclusive = property("minInclusive");
	public static final Property max = property("max");
	public static final Property maxInclusive = property("maxInclusive");
	public static final Property groupKey = property("groupKey");

	// TODO Add minInclusive / maxInclusive
	
	public static final Property key = property("key");
	public static final Property entry = property("entry");
//	public static final Property value = property("value");
	public static final Property weights = property("weights");

	
	public static final Resource ScenarioConfig = resource("ScenarioConfig");
	public static final Property randomSeed = property("randomSeed");

	public static final Property last = property("last");
	public static final Property prior = property("prior");


	public static final Property query = property("query");
//	public static final Property constraint = property("constraint");

	
	
	public static Property property(String localName) {
		return ResourceFactory.createProperty("http://aksw.org/adhoc/ontology/" + localName);
	}

	public static Resource resource(String localName) {
		return ResourceFactory.createResource("http://aksw.org/adhoc/ontology/" + localName);
	}

}
