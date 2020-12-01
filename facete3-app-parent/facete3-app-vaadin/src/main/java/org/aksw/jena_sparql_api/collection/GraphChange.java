package org.aksw.jena_sparql_api.collection;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aksw.commons.collections.CartesianProduct;
import org.aksw.commons.collections.SetUtils;
import org.aksw.jena_sparql_api.utils.TripleUtils;
import org.apache.jena.ext.com.google.common.collect.HashMultimap;
import org.apache.jena.ext.com.google.common.collect.Multimap;
import org.apache.jena.ext.com.google.common.collect.Multimaps;
import org.apache.jena.ext.com.google.common.collect.Streams;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.GraphBase;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.WrappedIterator;

import com.google.common.collect.Sets;

/**
 * A field that when setting its value removes the referred to triple
 * and replaces it with another one
 *
 * @author raven
 *
 * @param <T>
 */
class RdfFieldFromExistingTriple<T>
    implements ObservableValue<T>
{
    protected GraphChange graph;
    protected Triple existingTriple;

    @Override
    public T get() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void set(T value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Runnable addListener(PropertyChangeListener listener) {
        // TODO Auto-generated method stub
        return null;
    }
}

public class GraphChange
//    extends GraphBase
{
    /** A set of blank that were newly created and must thus not clash with existing resources */
    // protected Set<Node> newNodes;

    protected Map<Node, Node> renamedNodes;

    /** Mapping of original triples to their edited versions */
    protected Map<Triple, Triple> tripleReplacements;

    /** Replacing a triple with null counts as a deletion */
    // protected Set<Triple> tripleDeletions;


    protected ObservableGraph baseGraph;

    public Map<Node, Node> getRenamedNodes() {
        return renamedNodes;
    }

    public Graph getBaseGraph() {
        return baseGraph;
    }

    public GraphChange() {
        this(new HashMap<>(), new HashMap<>(), GraphFactory.createDefaultGraph());
    }


    public static <T> Collection<T> nullableSingleton(T item) {
        return item == null
                ? Collections.emptySet()
                : Collections.singleton(item);
    }

    /**
     * A graph view of the final state.
     *
     * Nodes that were renamed are no longer visible.
     *
     * @return
     */
    public Graph getEffectiveGraphView() {
        return new GraphBase() {
            @Override
            protected ExtendedIterator<Triple> graphBaseFind(Triple triplePattern) {

                // If there is a request for x but x was renamed to y
                // then rephrase the express in terms of y.

                Map<Node, Node> nodeToCluster = new HashMap<>(renamedNodes);
                // For each value that is not mapped to by a key map it to itself
                for (Node v : renamedNodes.values()) {
                    Node newV = renamedNodes.get(v);

                    if (newV == null) {
                        nodeToCluster.put(v,  v);
                    }
                }
                Multimap<Node, Node> clusterToMembers = nodeToCluster.entrySet().stream()
                        .collect(Multimaps.toMultimap(Entry::getValue, Entry::getKey, HashMultimap::create));

//                Multimap<Node, Node> fwdMap = Multimaps.forMap(renamedNodes);

                // If a node was renamed it ceases to exist
                Stream<Triple> expandedLookups = expand(triplePattern, Triple.createMatch(null, null, null),
                        x -> {
                            Collection<Node> sources = clusterToMembers.get(x);
                            Collection<Node> r = !sources.isEmpty()
                                    ? sources
                                    : renamedNodes.containsKey(x)
                                        ? sources
                                        : Collections.singleton(x);
                            return r;
                        });
//                        x -> renamedNodes.get(x) != null ? Collections.emptySet() : clusterToMembers.get(x));


                List<Triple> tmpX = expandedLookups.collect(Collectors.toList());
                expandedLookups = tmpX.stream();
//                System.out.println("Expanded " + triplePattern + " to " + tmpX);

                Stream<Triple> rawTriples = expandedLookups
                        .flatMap(pattern -> Streams.stream(baseGraph.find(pattern)));

                Stream<Triple> stream = rawTriples
                    .flatMap(triple -> {

                        Stream<Triple> r;

                        boolean isRemapped = tripleReplacements.containsKey(triple);
                        if (isRemapped) {
                            Triple replacement = tripleReplacements.get(triple);
                            r = replacement == null
                                    ? Stream.empty()
                                    : Stream.of(replacement);
                        } else {
                            r = Stream.of(triple);
                        }

                        return r;
                    })
                    .flatMap(triple -> {
                        return expand(triple, triplePattern,
                                x -> nullableSingleton(renamedNodes.getOrDefault(x, x)));
                    });

                List<Triple> tmp = stream.collect(Collectors.toList());
                stream = tmp.stream();

//                System.out.println("Lookup for " + triplePattern);
//                System.out.println("Returned: " + tmp);

                ExtendedIterator<Triple> result = WrappedIterator.create(stream.iterator());
                return result;
            }
        };
    }

    /**
     * Return a graph view where all attributes of resources that are renamed
     * to the same final resource appear on all involved resources.
     *
     * This graph view differs from the effective graph view where the resources
     * that are the source of renaming do no longer exist (as they have been renamed)
     *
     * @return
     */
    public Graph getSameAsInferredGraphView() {
        return new GraphBase() {
            @Override
            protected ExtendedIterator<Triple> graphBaseFind(Triple triplePattern) {

                Map<Node, Node> nodeToCluster = new HashMap<>(renamedNodes);
                // For each value that is not mapped to by a key map it to itself
                for (Node v : renamedNodes.values()) {
                    Node newV = renamedNodes.get(v);

                    if (newV == null) {
                        nodeToCluster.put(v,  v);
                    }
                }


                Multimap<Node, Node> clusterToMembers = nodeToCluster.entrySet().stream()
                        .collect(Multimaps.toMultimap(Entry::getValue, Entry::getKey, HashMultimap::create));

//                Multimap<Node, Node> fwdMap = Multimaps.forMap(map);

                // For each value that is not mapped to by a key map it to itself
//                for (Node v : renamedNodes.values()) {
//                    Node newV = renamedNodes.get(v);
//
//                    if (newV == null) {
//                        fwdMap.put(v,  v);
//                    }
//                }

                Stream<Triple> expandedLookups = expand(triplePattern, Triple.createMatch(null, null, null),  node -> clusterToMembers.get(nodeToCluster.get(node)));

//                Stream<Triple> expandedLookups = Streams.concat(
//                    Stream.of(triplePattern),
//                    extraLookups);

                List<Triple> tmpX = expandedLookups.collect(Collectors.toList());
                expandedLookups = tmpX.stream();
//                System.out.println("Expanded " + triplePattern + " to " + tmpX);

                Stream<Triple> rawTriples = expandedLookups
                        .flatMap(pattern -> Streams.stream(baseGraph.find(pattern)));

                Stream<Triple> stream = rawTriples
                    .flatMap(triple -> {

                        Stream<Triple> r;

                        boolean isRemapped = tripleReplacements.containsKey(triple);
                        if (isRemapped) {
                            Triple replacement = tripleReplacements.get(triple);
                            r = replacement == null
                                    ? Stream.empty()
                                    : Stream.of(replacement);
                        } else {
                            r = Stream.of(triple);
                        }

                        return r;
                    })
                    .flatMap(triple -> {
                        return expand(triple, triplePattern, node -> clusterToMembers.get(nodeToCluster.get(node)));
                    });

                List<Triple> tmp = stream.collect(Collectors.toList());
                stream = tmp.stream();

//                System.out.println("Lookup for " + triplePattern);
//                System.out.println("Returned: " + tmp);

                ExtendedIterator<Triple> result = WrappedIterator.create(stream.iterator());
                return result;
            }
        };
    }

    public GraphChange(Map<Node, Node> renamedNodes, Map<Triple, Triple> tripleReplacements, Graph baseGraph) {
        super();
        this.renamedNodes = renamedNodes;
        this.tripleReplacements = tripleReplacements;
        this.baseGraph = ObservableGraph.decorate(baseGraph);
    }

    public static <T> Collection<T> defaultToSingletonIfEmpty(Collection<T> items, T defaultItem) {
        return items.isEmpty()
                ? Collections.singleton(defaultItem)
                : items;
    }

//    public static <T> Collection<T> defaultToSingletonIfEmpty(Collection<T> items, T defaultItem) {
//        return Sets.union(Collections.singleton(defaultItem), SetUtils.asSet(items));
//    }

    /**
     * Filter a collection based on a pattern with the following rules:
     * If pattern is null returned the collection unchanged.
     * Otherwise, if the pattern is contained in the collection return a collection with only that item
     * otherwise return an empty collection.
     *
     * @param <T>
     * @param collection
     * @param pattern
     * @return
     */
    public static <T> Collection<T> filterToPattern(Collection<T> collection, T pattern) {
        return pattern == null
                ? collection
                : collection.contains(pattern)
                    ? Collections.singleton(pattern)
                    : Collections.emptySet();
    }

    public static Collection<Node> filterToPattern(Function<Node, Collection<Node>> fn, Node node, Node pattern) {
        boolean isAnyNode = node == null || Node.ANY.equals(node);
        boolean isAnyPattern = pattern == null || Node.ANY.equals(pattern);

        Collection<Node> result;
        if (isAnyNode) {
            result = isAnyPattern
                    ? Collections.singleton(Node.ANY)
                    : Collections.singleton(pattern);
        } else {
            result = fn.apply(node);

            if (!isAnyPattern) {
                result = result.contains(pattern)
                    ? Collections.singleton(pattern)
                    : Collections.emptySet();
            }
        }

        return result;
    }


    public static <T> Collection<T> get(Multimap<T, T> multimap, T key, boolean reflexive) {
        Collection<T> result = multimap.get(key);

        result = reflexive
            ? result.contains(key)
                ? result
                : Sets.union(Collections.singleton(key), SetUtils.asSet(result))
            : result;

        return result;
    }

    public static <K, V> V getOrDefault(Function<? super K, ? extends V> fn, K key, V defaultValue) {
        V result = fn.apply(key);
        result = result == null ? defaultValue : result;
        return result;
    }

    /**
     * Create all possible triples by substituting each node in the triple
     * with all possible nodes w.r.t. the reverse mapping.
     * If a node is not mapped it remains itself.
     *
     * @param concrete
     * @param reverseMap
     * @return
     */
    public static Stream<Triple> expand(
            Triple concrete,
            Triple pattern,
//            Function<Node, Node> nodeToCluster,
            Function<Node, Collection<Node>> clusterToMembers
            ) {

//        Multimap<Node, Node> clusterToMembers = nodeToCluster.entrySet().stream()
//                .collect(Multimaps.toMultimap(Entry::getValue, Entry::getKey, HashMultimap::create));

        Node cs = concrete.getSubject();
        Node cp = concrete.getPredicate();
        Node co = concrete.getObject();

        CartesianProduct<Node> cart = CartesianProduct.create(
            filterToPattern(clusterToMembers, cs, pattern.getMatchSubject()),
            filterToPattern(clusterToMembers, cp, pattern.getMatchPredicate()),
            filterToPattern(clusterToMembers, co, pattern.getMatchObject())
        );


//        CartesianProduct<Node> cart = CartesianProduct.create(
//            filterToPattern(
////                defaultToSingletonIfEmpty(
//                        get(reverseMap, concrete.getSubject(), reflexive),
////                        concrete.getSubject()),
//                pattern.getMatchSubject()),
//            filterToPattern(
////                    defaultToSingletonIfEmpty(
//                            get(reverseMap, concrete.getPredicate(), reflexive),
////                            concrete.getPredicate()),
//                    pattern.getMatchPredicate()),
//            filterToPattern(
////                    defaultToSingletonIfEmpty(
//                            get(reverseMap, concrete.getObject(), reflexive),
////                            concrete.getObject())
//                    pattern.getMatchObject())
//        );

//        CartesianProduct<Node> cart = CartesianProduct.create(
//            filterToPattern(
//                defaultToSingletonIfEmpty(reverseMap.get(concrete.getSubject()), concrete.getSubject()),
//                pattern.getMatchSubject()),
//            filterToPattern(
//                defaultToSingletonIfEmpty(reverseMap.get(concrete.getPredicate()), concrete.getPredicate()),
//                pattern.getMatchPredicate()),
//            filterToPattern(
//                defaultToSingletonIfEmpty(reverseMap.get(concrete.getObject()), concrete.getObject()),
//                pattern.getMatchObject())
//        );

        Stream<Triple> result = cart.stream()
                .map(TripleUtils::listToTriple);

        List<Triple> tmp = result.collect(Collectors.toList());
        result = tmp.stream();

        return result;
    }


//    @Override
//    protected ExtendedIterator<Triple> graphBaseFind(Triple triplePattern) {
//        return null;
//    }
}
