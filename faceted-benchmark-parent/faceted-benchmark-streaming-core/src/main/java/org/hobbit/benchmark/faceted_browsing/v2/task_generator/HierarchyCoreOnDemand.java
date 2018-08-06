package org.hobbit.benchmark.faceted_browsing.v2.task_generator;

import org.aksw.jena_sparql_api.concepts.Concept;
import org.aksw.jena_sparql_api.concepts.UnaryRelation;
import org.aksw.jena_sparql_api.utils.ElementUtils;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_NotExists;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathFactory;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;

public class HierarchyCoreOnDemand
	implements HierarchyCore
{
	public static final Var root = Var.alloc("root");
	public static final Var parent = Var.alloc("parent");
	public static final Var ancestor = Var.alloc("ancestor");

	
	protected Path path;
	

	public HierarchyCoreOnDemand(Path path) {
		super();
		this.path = path;
	}

	/**
	 * Roots are all nodes having no ancestor without parents
	 * 
	 * <pre>
	 * {@code
     * SELECT DISTINCT ?root {
     *   [] <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?root
	 *   FILTER(NOT EXISTS { ?root (<http://www.w3.org/2000/01/rdf-schema#subClassOf>)+ ?ancestor . FILTER(NOT EXISTS {?ancestor <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?parent }) . })\n" + 
     * }
     * }
     * </pre>
     * 
	 * This means that every node in a cycle which does not have any parent outside of that cycle will become a root.
	 */
	@Override
	public UnaryRelation roots() {
		UnaryRelation result = createRootConcept(path);
		return result;
	}
	
	
	public static UnaryRelation createRootConcept(Path path) {
		Element e = ElementUtils.createElementGroup(
			ElementUtils.createElement(new TriplePath(NodeFactory.createBlankNode(), path, root)),
			new ElementFilter(new E_NotExists(
				ElementUtils.createElementGroup(
					ElementUtils.createElement(new TriplePath(root, PathFactory.pathOneOrMore1(path), ancestor)),
					new ElementFilter(new E_NotExists(ElementUtils.createElementGroup(ElementUtils.createElement(new TriplePath(ancestor, path, parent)))))
		))));
		
		System.out.println(e);
		
		UnaryRelation result = new Concept(e, root);
		return result;
	}

	/**
	 * If any child is part of a cycle, all members of the cycle become children
	 * 
	 * 
	 * -> so effective children are all direct children that did not already appear as an ancestor
	 */
	@Override
	public UnaryRelation children(UnaryRelation nodes) {
		return null;
	
	}

	@Override
	public UnaryRelation parents(UnaryRelation nodes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnaryRelation descendents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnaryRelation ancestors() {
		// TODO Auto-generated method stub
		return null;
	}	
}