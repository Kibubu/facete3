package org.aksw.facete.v3.impl;

import java.util.Set;

import org.aksw.facete.v3.api.ConstraintFacade;
import org.aksw.facete.v3.api.DataQuery;
import org.aksw.facete.v3.api.FacetDirNode;
import org.aksw.facete.v3.api.FacetNode;
import org.aksw.jena_sparql_api.concepts.BinaryRelation;
import org.aksw.jena_sparql_api.concepts.BinaryRelationImpl;
import org.aksw.jena_sparql_api.utils.ElementUtils;
import org.aksw.jena_sparql_api.utils.Vars;
import org.aksw.jena_sparql_api.utils.model.ResourceUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.hobbit.benchmark.faceted_browsing.v2.domain.Vocab;


public class FacetNodeImpl
	implements FacetNodeResource
{
	protected FacetNodeResource parent;
	protected Resource state;
	
	protected FacetNodeImpl(FacetNodeResource parent, Resource state) {
		this.parent = parent;
		this.state = state; 
	}
	
	@Override
	public Resource state() {
		return state;
	}
	
	@Override
	public FacetDirNode fwd() {
		return new FacetDirNodeImpl(this, true);
	}

	@Override
	public FacetDirNode bwd() {
		return new FacetDirNodeImpl(this, false);
	}

	@Override
	public BinaryRelation getReachingRelation() {
		BinaryRelation result;

		if(parent == null) {
			result = null;
		} else {
			
			boolean isReverse = false;
			Set<Statement> set = ResourceUtils.listProperties(parent().state(), null).filterKeep(stmt -> stmt.getObject().equals(state)).toSet();
			
			if(set.isEmpty()) {
				isReverse = true;
				set = ResourceUtils.listReverseProperties(parent().state(), null).filterKeep(stmt -> stmt.getSubject().equals(state)).toSet();
			}
			
			// TODO Should never fail - but ensure that
			Property p = set.iterator().next().getPredicate();
			
			result = create(p.asNode(), isReverse);
		}

		return result;
	}
	
	public static BinaryRelation create(Node node, boolean isReverse) {
		//ElementUtils.createElement(triple)
		Triple t = isReverse
				? new Triple(Vars.o, node, Vars.s)
				: new Triple(Vars.s, node, Vars.o);

		BinaryRelation result = new BinaryRelationImpl(ElementUtils.createElement(t), Vars.s, Vars.o);
		return result;
	}

	

	@Override
	public DataQuery availableValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public FacetNode as(String varName) {
		ResourceUtils.setLiteralProperty(state, Vocab.alias, varName);		
		return this;
	}
	
	@Override
	public FacetNode as(Var var) {
		return as(var.getName());
	}
	
	@Override
	public Var alias() {
		return ResourceUtils.getLiteralPropertyValue(state, Vocab.alias, String.class)
			.map(Var::alloc).orElse(null);
	}

	@Override
	public FacetNodeResource parent() {
		return parent;
	}

	@Override
	public FacetNode root() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConstraintFacade<FacetNode> constraints() {
		// TODO Auto-generated method stub
		return null;
	}
}
