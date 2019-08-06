package org.aksw.jena_sparql_api.data_query.impl;

import org.aksw.facete.v3.api.path.Path;

public class NodePathletPath
	extends NodeCustom<Path>
{	
	public NodePathletPath(Path value) {
		super(value);
	}

	public static NodePathletPath create(Path path) {
		return new NodePathletPath(path);
	}
}
