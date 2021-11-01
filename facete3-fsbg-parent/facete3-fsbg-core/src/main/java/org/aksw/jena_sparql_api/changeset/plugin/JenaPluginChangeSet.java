package org.aksw.jena_sparql_api.changeset.plugin;

import org.aksw.jena_sparql_api.changeset.api.ChangeSet;
import org.aksw.jena_sparql_api.changeset.api.RdfStatement;
import org.aksw.jena_sparql_api.changeset.ex.api.ChangeSetGroup;
import org.aksw.jena_sparql_api.changeset.ex.api.ChangeSetGroupState;
import org.aksw.jena_sparql_api.changeset.ex.api.ChangeSetState;
import org.aksw.jena_sparql_api.changeset.ex.impl.ChangeSetGroupImpl;
import org.aksw.jena_sparql_api.changeset.ex.impl.ChangeSetGroupStateImpl;
import org.aksw.jena_sparql_api.changeset.ex.impl.ChangeSetStateImpl;
import org.aksw.jena_sparql_api.changeset.impl.ChangeSetImpl;
import org.aksw.jena_sparql_api.changeset.impl.RdfStatementImpl;
import org.aksw.jenax.arq.util.implementation.SimpleImplementation;
import org.apache.jena.enhanced.BuiltinPersonalities;
import org.apache.jena.enhanced.Personality;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sys.JenaSubsystemLifecycle;

public class JenaPluginChangeSet
	implements JenaSubsystemLifecycle
{
	public void start() {
		init();
	}

	@Override
	public void stop() {
	}

	public static void init() {
		init(BuiltinPersonalities.model);
	}

	public static void init(Personality<RDFNode> p) {
		p.add(ChangeSet.class, new SimpleImplementation(ChangeSetImpl::new));
		p.add(RdfStatement.class, new SimpleImplementation(RdfStatementImpl::new));

		// Extensions
		p.add(ChangeSetGroup.class, new SimpleImplementation(ChangeSetGroupImpl::new));
		p.add(ChangeSetState.class, new SimpleImplementation(ChangeSetStateImpl::new));
		p.add(ChangeSetGroupState.class, new SimpleImplementation(ChangeSetGroupStateImpl::new));
	}
}
