package org.aksw.facete3.app.vaadin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.aksw.facete.v3.api.FacetCount;
import org.aksw.facete.v3.api.FacetDirNode;
import org.aksw.facete.v3.api.FacetedQuery;
import org.aksw.facete.v3.bgp.api.XFacetedQuery;
import org.aksw.facete.v3.impl.FacetedQueryImpl;
import org.aksw.facete.v3.plugin.JenaPluginFacete3;
import org.aksw.jena_sparql_api.algebra.expr.transform.ExprTransformVirtualBnodeUris;
import org.aksw.jena_sparql_api.core.RDFConnectionFactoryEx;
import org.aksw.jena_sparql_api.rx.RDFDataMgrEx;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.WebContent;
import org.apache.jena.sys.JenaSystem;
import org.apache.jena.vocabulary.RDF;

public class QueryConf {

    private FacetDirNode facetDirNode;
    private FacetedQuery facetedQuery;
    private Node selectedFacet;

    public FacetDirNode getFacetDirNode() {
        return facetDirNode;
    }

    public void setFacetDirNode(FacetDirNode facetDirNode) {
        this.facetDirNode = facetDirNode;
    }

    public FacetedQuery getFacetedQuery() {
        return facetedQuery;
    }

    public void setFacetedQuery(FacetedQuery facetedQuery) {
        this.facetedQuery = facetedQuery;
    }

    public Node getSelectedFacet() {
        return selectedFacet;
    }

    public void setSelectedFacet(Node facet) {
        selectedFacet = facet;
    }

    public QueryConf() {
        JenaSystem.init();
        JenaPluginFacete3.init();
        RDFConnection conn = loadFile();
        Model dataModel = ModelFactory.createDefaultModel();
        XFacetedQuery xFacetedQuery = dataModel.createResource()
                .as(XFacetedQuery.class);
        FacetedQueryImpl.initResource(xFacetedQuery);
        setFacetedQuery(FacetedQueryImpl.create(xFacetedQuery, conn));
        setFacetDirNode(facetedQuery.focus()
                .fwd());
        setSelectedFacet(RDF.type.asNode());
    }

    private RDFConnection connectUrl() {
        RDFConnection conn = RDFConnectionRemote.create()
                .destination("https://databus.dbpedia.org/repo/sparql")
                .acceptHeaderQuery(WebContent.contentTypeResultsXML)
                .build();
        return conn;
    }

    private RDFConnection loadFile() {
        // https://www.orkg.org/orkg/sparql/
        // https://www.orkg.org/orkg/triplestore/
        // http://cord19.aksw.org/sparql
        // https://databus.dbpedia.org/repo/sparql
        Dataset dataset = DatasetFactory.create();
        RDFDataMgr.read(dataset,
                "/home/beavis/cloud/repositories/link-discovery-and-data-fusion/fusion/fused.nt");
        RDFConnection conn = RDFConnectionFactory.connect(dataset);
        conn = wrapWithVirtualBnodeUris(conn, "jena");
        return conn;
    }

    public static RDFConnection wrapWithVirtualBnodeUris(RDFConnection conn, String profile) {
        Model model = RDFDataMgr.loadModel("bnode-rewrites.ttl");
        RDFDataMgrEx.execSparql(model, "udf-inferences.sparql");

        Set<String> activeProfiles =
                new HashSet<>(Arrays.asList("http://ns.aksw.org/profile/" + profile));
        ExprTransformVirtualBnodeUris xform =
                ExprTransformVirtualBnodeUris.createTransformFromUdfModel(model, activeProfiles);
        RDFConnection result = RDFConnectionFactoryEx.wrapWithQueryTransform(conn, xform::rewrite);
        return result;
    }
}
