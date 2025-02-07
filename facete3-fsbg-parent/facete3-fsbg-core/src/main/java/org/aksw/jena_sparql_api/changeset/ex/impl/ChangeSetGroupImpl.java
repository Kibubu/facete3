package org.aksw.jena_sparql_api.changeset.ex.impl;

import java.util.Set;

import org.aksw.jena_sparql_api.changeset.api.ChangeSet;
import org.aksw.jena_sparql_api.changeset.ex.api.CSX;
import org.aksw.jena_sparql_api.changeset.ex.api.ChangeSetGroup;
import org.aksw.jena_sparql_api.rdf.collections.ResourceUtils;
import org.aksw.jena_sparql_api.rdf.collections.SetFromPropertyValues;
import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.vocabulary.RDFS;

public class ChangeSetGroupImpl
	extends ResourceImpl
	implements ChangeSetGroup
{
	public ChangeSetGroupImpl(Node n, EnhGraph m) {
		super(n, m);
	}

	@Override
	public ChangeSetGroup getPrecedingChangeSetGroup() {
		return ResourceUtils.getPropertyValue(this, CSX.precedingChangeSetGroup, ChangeSetGroup.class);
	}

	@Override
	public void setPrecedingChangeSetGroup(ChangeSetGroup precedingChangeSetGroup) {
		ResourceUtils.setProperty(this, CSX.precedingChangeSetGroup, precedingChangeSetGroup);
	}

	@Override
	public Set<ChangeSet> members() {
		return new SetFromPropertyValues<>(this, RDFS.member, ChangeSet.class);
	}
}
