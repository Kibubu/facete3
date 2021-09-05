package org.aksw.jena_sparql_api.schema.traversal.sparql;

import org.aksw.jena_sparql_api.entity.graph.metamodel.path.Path;
import org.aksw.jena_sparql_api.entity.graph.metamodel.path.node.PathOpsNode;
import org.aksw.jena_sparql_api.schema.traversal.sparql.TravTripleViews.TravAlias;
import org.aksw.jena_sparql_api.schema.traversal.sparql.TravTripleViews.TravDirection;
import org.aksw.jena_sparql_api.schema.traversal.sparql.TravTripleViews.TravProperty;
import org.aksw.jena_sparql_api.schema.traversal.sparql.TravTripleViews.TravValues;
import org.apache.jena.graph.Node;

public class TravProviderTripleImpl<S>
    implements TravProviderTriple<S>
{
//    protected Path<Node> rootPath;

//    public TravProviderTripleImpl(Path<Node> rootPath) {
//        super();
//        this.rootPath = rootPath;
//    }

    public TravProviderTripleImpl() {
        super();
    }

    public static TravProviderTriple<Void> create() {
        return new TravProviderTripleImpl<>();
    }

    @Override
    public TravValues<S> root() {
        Path<Node> rootPath = PathOpsNode.get().newRoot();
        S rootState = rooteState();
        return new TravValues<>(this, rootPath, null, rootState);
    }

    @Override
    public TravDirection<S> toDirection(TravValues<S> from, Node value) {
        Path<Node> next = from.path().resolve(value);

        S nextState = computeNextState(from, value);

        return new TravDirection<>(this, next, from, nextState);
    }

    @Override
    public TravProperty<S> toProperty(TravDirection<S> from, boolean isFwd) {
        Node segment = isFwd ? TravDirection.FWD : TravDirection.BWD;
        Path<Node> next = from.path().resolve(segment);

        S nextState = computeNextState(from, isFwd);

        return new TravProperty<>(this, next, from, nextState);
    }

    @Override
    public TravAlias<S> toAlias(TravProperty<S> from, Node property) {
        Path<Node> next = from.path().resolve(property);

        S nextState = computeNextState(from, property);

        return new TravAlias<>(this, next, from, nextState);
    }

    @Override
    public TravValues<S> toValues(TravAlias<S> from, Node alias) {
        Path<Node> next = from.path().resolve(alias);

        S nextState = computeNextState(from, alias);

        return new TravValues<>(this, next, from, nextState);
    }



    public S rooteState() {
        return null;
    }

    public S computeNextState(TravValues<S> from, Node value) {
        return null;
    }

    public S computeNextState(TravDirection<S> from, boolean isFwd) {
        return null;
    }

    public S computeNextState(TravProperty<S> from, Node property) {
        return null;
    }

    public S computeNextState(TravAlias<S> from, Node alias) {
        return null;
    }



}
