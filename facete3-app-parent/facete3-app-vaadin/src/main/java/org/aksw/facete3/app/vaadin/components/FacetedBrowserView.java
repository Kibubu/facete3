package org.aksw.facete3.app.vaadin.components;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.aksw.commons.rx.lookup.LookupService;
import org.aksw.facete.v3.api.FacetCount;
import org.aksw.facete.v3.api.FacetNode;
import org.aksw.facete.v3.api.FacetValueCount;
import org.aksw.facete.v3.api.HLFacetConstraint;
import org.aksw.facete3.app.shared.concept.RDFNodeSpec;
import org.aksw.facete3.app.shared.viewselector.ViewTemplate;
import org.aksw.facete3.app.shared.viewselector.ViewTemplateImpl;
import org.aksw.facete3.app.vaadin.Config;
import org.aksw.facete3.app.vaadin.Facete3Wrapper;
import org.aksw.facete3.app.vaadin.ResourceHolder;
import org.aksw.facete3.app.vaadin.SearchSensitiveRDFConnectionTransform;
import org.aksw.facete3.app.vaadin.components.sparql.wizard.SparqlConnectionWizard;
import org.aksw.facete3.app.vaadin.plugin.search.SearchPlugin;
import org.aksw.facete3.app.vaadin.plugin.view.ViewFactory;
import org.aksw.facete3.app.vaadin.plugin.view.ViewManager;
import org.aksw.facete3.app.vaadin.providers.FacetCountProvider;
import org.aksw.facete3.app.vaadin.providers.FacetValueCountProvider;
import org.aksw.facete3.app.vaadin.providers.ItemProvider;
import org.aksw.jena_sparql_api.common.DefaultPrefixes;
import org.aksw.jena_sparql_api.concepts.Concept;
import org.aksw.jena_sparql_api.concepts.ConceptUtils;
import org.aksw.jena_sparql_api.conjure.dataref.rdf.api.RdfDataRefSparqlEndpoint;
import org.aksw.jena_sparql_api.conjure.dataset.algebra.Op;
import org.aksw.jena_sparql_api.conjure.dataset.algebra.OpDataRefResource;
import org.aksw.jena_sparql_api.conjure.dataset.algebra.OpUnionDefaultGraph;
import org.aksw.jena_sparql_api.rx.entity.model.EntityGraphFragment;
import org.aksw.jena_sparql_api.rx.entity.model.EntityQueryImpl;
import org.aksw.jena_sparql_api.rx.entity.model.EntityTemplateImpl;
import org.aksw.jena_sparql_api.rx.entity.model.GraphPartitionJoin;
import org.aksw.jenax.analytics.core.RootedQuery;
import org.aksw.jenax.arq.aggregation.BestLiteralConfig;
import org.aksw.jenax.arq.connection.core.QueryExecutionFactoryOverSparqlQueryConnection;
import org.aksw.jenax.arq.connection.core.RDFConnectionTransform;
import org.aksw.jenax.arq.util.syntax.ElementUtils;
import org.aksw.jenax.arq.util.var.Vars;
import org.aksw.jenax.dataaccess.LabelUtils;
import org.aksw.jenax.sparql.relation.api.UnaryRelation;
import org.aksw.jenax.vaadin.component.grid.sparql.SparqlGridComponent;
import org.aksw.jenax.vaadin.label.VaadinRdfLabelMgr;
import org.aksw.jenax.vaadin.label.VaadinRdfLabelMgrImpl;
import org.apache.jena.ext.com.google.common.collect.Streams;
import org.apache.jena.ext.com.google.common.graph.Traverser;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.data.provider.InMemoryDataProvider;
import com.vaadin.flow.data.provider.Query;

import io.reactivex.rxjava3.core.Flowable;

public class FacetedBrowserView
    extends VerticalLayout {

    protected FacetedBrowserToolbar toolbar;

    protected ConstraintsComponent constraintsComponent;
    protected SearchComponent searchComponent;
    protected FacetCountComponent facetCountComponent;
    protected FacetPathComponent facetPathComponent;
    protected FacetValueCountComponent facetValueCountComponent;
    protected Facete3Wrapper facete3;
    // protected ItemComponent itemComponent;

    protected SparqlGridComponent sparqlGridComponent;

    protected Label connectionInfo;

//    protected ResourceComponent resourceComponent;

    /** The resource browser should eventually supersede the resourceComponent */
    protected ResourceBrowserComponent resourceBrowserComponent;

//  @Autowired
    protected RDFConnection baseDataConnection;

//    protected SearchProvider searchProvider;
    protected InMemoryDataProvider<SearchPlugin> searchPluginDataProvider;
    protected SearchPlugin activeSearchPlugin;

//    @Autowired(required = false)
//    protected SearchSensitiveRDFConnectionTransform searchSensitiveRdfConnectionTransform = null;

    @Autowired
    protected ConfigurableApplicationContext cxt;


    protected VaadinRdfLabelMgr labelMgr;

    public VaadinRdfLabelMgr getLabelMgr() {
        return labelMgr;
    }


    public FacetedBrowserView(
            RDFConnection baseDataConnection,
//            SearchPlugin searchPlugin,
            InMemoryDataProvider<SearchPlugin> searchPluginProvider,
            PrefixMapping prefixMapping,
            Facete3Wrapper facete3,
            FacetCountProvider facetCountProvider,
            FacetValueCountProvider facetValueCountProvider,
            ItemProvider itemProvider,
            Config config,
            ViewManager viewManagerFull,
            ViewManager viewManagerDetails,
            BestLiteralConfig bestLabelConfig,
            VaadinRdfLabelMgr labelMgr
            ) {

        this.labelMgr = labelMgr;

        ViewFactory dftViewFactory = new ViewFactory() {

            @Override
            public ViewTemplate getViewTemplate() {

                // FIXME The view template should be static;
                // at present each invocation creates a new one

                EntityQueryImpl attrQuery = new EntityQueryImpl();


                /*
                 * Unfortunately there is no syntax (yet) for entity-centric sparql;
                 * the following is (roughly)
                 *
                 * ENTITY ?x
                 * CONSTRUCT { }
                 * WHERE { }
                 *
                 */
                List<Var> vars = Collections.singletonList(Vars.x);
                EntityGraphFragment fragment = new EntityGraphFragment(
                        vars,
                        new EntityTemplateImpl(Collections.<Node>singletonList(Vars.x), new Template(
                                BasicPattern.wrap(Collections.emptyList()))),
                        // ElementUtils.createElementTriple(Vars.s, Vars.p, Vars.o)
                        new ElementGroup()
                        );

                attrQuery.getOptionalJoins().add(new GraphPartitionJoin(fragment));

                ElementUnion union = new ElementUnion();
                union.addElement(ElementUtils.createElementTriple(Vars.x, Vars.p, Vars.o));
                union.addElement(ElementUtils.createElementTriple(Vars.s, Vars.x, Vars.o));
                union.addElement(ElementUtils.createElementTriple(Vars.s, Vars.p, Vars.x));

                return new ViewTemplateImpl(
                        // The id of the view
                        ModelFactory.createDefaultModel()
                            .createResource("http://cord19.aksw.org/view/resource-explorer")
                            .addLiteral(RDFS.label, "Resource"),

                        // The condition for which set of resources the view is applicable
                        new Concept(union, Vars.x),

                        // The entity-centric construct query for what information to fetch when applying the view
                        attrQuery
                        );

            }

            @Override
            public Component createComponent(RDFNode data) {
                ResourceComponent result = new ResourceComponent(PrefixMapping.Extended, viewManagerFull, labelMgr);
                result.setNode(data);
                return result;
            }
        };


        this.baseDataConnection = baseDataConnection;
        this.searchPluginDataProvider = searchPluginProvider;
        this.activeSearchPlugin = searchPluginDataProvider.fetch(new Query<>()).limit(1).findFirst().orElse(null);
        this.facete3 = facete3;

        LookupService<Node, String> labelService = LabelUtils.getLabelLookupService(
                new QueryExecutionFactoryOverSparqlQueryConnection(baseDataConnection),
                RDFS.label,
                prefixMapping);

//        TransformService transformService = new TransformService(config.getPrefixFile());

        Function<RDFNode, String> labelFunction = rdfNode ->
            Objects.toString(LabelUtils.getOrDeriveLabel(rdfNode, bestLabelConfig));

        toolbar = new FacetedBrowserToolbar();
        facetCountComponent = new FacetCountComponent(this, facetCountProvider);
        facetValueCountComponent = new FacetValueCountComponent(this, facetValueCountProvider);
        facetPathComponent = new FacetPathComponent(this, facete3, labelService);

        // itemComponent = new ItemComponent(this, itemProvider, viewManagerDetails);

        // baseConcept
        sparqlGridComponent = new SparqlGridComponent(query -> baseDataConnection.query(query), ConceptUtils.createSubjectConcept(), labelMgr);


        resourceBrowserComponent = new ResourceBrowserComponent(viewManagerFull, labelFunction, dftViewFactory);
        resourceBrowserComponent.setWidthFull();
        resourceBrowserComponent.setHeightFull();

        constraintsComponent = new ConstraintsComponent(this, facete3, labelService);
        constraintsComponent.setMaxHeight("40px");
        connectionInfo = new Label();
        connectionInfo.getElement().setAttribute("theme", "badge primary pill");

        searchComponent = new SearchComponent(this, () -> activeSearchPlugin.getSearchProvider());
        toolbar.add(searchComponent);

        Select<SearchPlugin> searchPluginSelect = new Select<>();
        searchPluginSelect.setDataProvider(searchPluginProvider);
        searchPluginSelect.setValue(activeSearchPlugin);
        searchPluginSelect.setTextRenderer(item -> item.getSearchProvider().toString());
        searchPluginSelect.addValueChangeListener(event -> {
            activeSearchPlugin = event.getValue();
            System.out.println("Active search plugin: " + activeSearchPlugin);
        });

        toolbar.add(searchPluginSelect);

        Button changeConnectionBtn = new Button(connectionInfo);
        toolbar.add(changeConnectionBtn);

        connectionInfo.addClassName("no-wrap");
        toolbar.setFlexGrow(1, searchComponent);

//        HorizontalLayout navbarLayout = new HorizontalLayout();
//        navbarLayout.setWidthFull();
//        navbarLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
//
//        Button appSettingsBtn = new Button(new Icon(VaadinIcon.COG));
//        navbarLayout.add(appSettingsBtn);

        Dialog dialog = new Dialog();
        dialog.setWidth("50em");

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        SparqlEndpointForm input = new SparqlEndpointForm();
        input.setWidthFull();

        layout.add(new SparqlConnectionWizard() {
            public void onSelect() {
                String urlStr = sparqlEndpointForm.getServiceUrl().getValue().getEndpoint();

                ResourceHolder opHolder = cxt.getBean(ResourceHolder.class);

                RdfDataRefSparqlEndpoint dataRef = ModelFactory.createDefaultModel().createResource().as(RdfDataRefSparqlEndpoint.class);
                dataRef.setServiceUrl(urlStr);

                List<String> defaultGraphIris = graphGrid.getSelectedItems().stream()
                    .map(qs -> qs.get("g").asNode().getURI())
                    .collect(Collectors.toList());

                dataRef.getDefaultGraphs().addAll(defaultGraphIris);

                Op op = OpDataRefResource.from(dataRef);

//                if (input.getUnionDefaultGraphMode().isEnabled()) {
//                    op = OpUnionDefaultGraph.create(op);
//                }

                opHolder.set(op);
//                System.out.println("INVOKING REFRESH");
//                System.out.println("Given cxt:\n" + toString(cxt));
//                System.out.println("Updated dataRef " + System.identityHashCode(dataRef));
                refreshAllNew();

                dialog.close();
            }
        });

        // layout.add(input);
        Button applyBtn = new Button("Apply");
        layout.add(applyBtn);
        applyBtn.addClickListener(event -> {
            String urlStr = input.getServiceUrl().getValue().getEndpoint();


            ResourceHolder opHolder = cxt.getBean(ResourceHolder.class);

            RdfDataRefSparqlEndpoint dataRef = ModelFactory.createDefaultModel().createResource().as(RdfDataRefSparqlEndpoint.class);
            dataRef.setServiceUrl(urlStr);

            Op op = OpDataRefResource.from(dataRef);

            if (input.getUnionDefaultGraphMode().isEnabled()) {
                op = OpUnionDefaultGraph.create(op);
            }

            opHolder.set(op);
//            System.out.println("INVOKING REFRESH");
//            System.out.println("Given cxt:\n" + toString(cxt));
//            System.out.println("Updated dataRef " + System.identityHashCode(dataRef));
            refreshAllNew();

            // TODO Now all dataProviders need to refresh
            dialog.close();
        });


        dialog.add(layout);


        Button refreshBtn = new Button(new Icon(VaadinIcon.REFRESH));
        refreshBtn.getElement().setProperty("title", "Refresh all data in this view");
        refreshBtn.addClickListener(event -> {
            refreshAllNew();
        });
        toolbar.add(refreshBtn);


        Button toggleLabelsBtn = new Button(VaadinIcon.TEXT_LABEL.create(), ev -> {
            // LookupService<Node, String> ls1 = LabelUtils.getLabelLookupService(qef, labelProperty, DefaultPrefixes.get());
            //LookupService<Node, String> ls2 = keys -> Flowable.fromIterable(keys).map(k -> Map.entry(k, Objects.toString(k)));

            //VaadinRdfLabelMgrImpl labelMgr = new VaadinRdfLabelMgrImpl(ls1);
        });
        toolbar.add(toggleLabelsBtn);



        Button configBtn = new Button(new Icon(VaadinIcon.COG));
        toolbar.add(configBtn);

        changeConnectionBtn.addClickListener(event -> {
            dialog.open();
        });

        configBtn.addClickListener(event -> {
            dialog.open();
//            input.focus();
        });


        add(toolbar);

//        appContent.add(getNaturalLanguageInterfaceComponent());

        add(constraintsComponent);

        add(getFacete3Component());


        // onRefresh();
    }



    public static String toString(ApplicationContext cxt) {
        Iterable<ApplicationContext> ancestors = Traverser.<ApplicationContext>forTree(c ->
            c.getParent() == null
                ? Collections.emptyList()
                : Collections.singletonList(c.getParent()))
        .depthFirstPreOrder(cxt);

        String result = Streams
            .stream(ancestors)
            .map(Object::toString)
            .collect(Collectors.joining("\n"));

        return result;
    }

    /**
     * Handler for refresh events
     * Calling this method should be handled by the spring context / config class
     * that creates this component
     *
     */
    @PostConstruct
    public void onRefresh() {
        ResourceHolder configHolder = cxt.getBean(ResourceHolder.class);
        Resource config = configHolder.get();

        String connectionLabel;
//        if (op != null) {
//        	if (op instanceof OpDataR)
//	        String url = Optional.ofNullable(op)
//	                .map(RdfDataRefSparqlEndpoint::getServiceUrl)
//	                .orElse("unknown connection");
//        } else {
//
//        }
        String url = "unknown connection";

        connectionInfo.setText(url);
    }


    public void refreshAllNew() {

        if (true) {
            UnaryRelation baseConcept = facete3.getFacetedQuery().baseConcept();
            sparqlGridComponent.setBaseConcept(baseConcept);
            sparqlGridComponent.resetGrid();
        }

        RefreshScope refreshScope = cxt.getBean(RefreshScope.class);
        refreshScope.refreshAll();
    }

    // Auto-wiring happens after object construction
    // So in order to access auto-wired properties we need to use this post-construct init method
//    @PostConstruct
//    public void init() {
//    }

    protected Component getAppContent() {
        VerticalLayout appContent = new VerticalLayout();
        appContent.add(toolbar);

//        appContent.add(getNaturalLanguageInterfaceComponent());

        appContent.add(constraintsComponent);

        appContent.add(getFacete3Component());
        return appContent;
    }



    protected Component getFacete3Component() {
        SplitLayout component = new SplitLayout();
        component.setSizeFull();
        component.setOrientation(Orientation.HORIZONTAL);
        component.setSplitterPosition(20);


        component.addToPrimary(getFacetComponent());


        component.addToSecondary(getResultsComponent());
        return component;
    }

    protected Component getFacetComponent() {
        SplitLayout facetComponent = new SplitLayout();
        facetComponent.setSizeFull();
        facetComponent.setOrientation(Orientation.VERTICAL);
        facetComponent.addToPrimary(facetCountComponent);
        facetComponent.addToSecondary(facetValueCountComponent);
        VerticalLayout component = new VerticalLayout();
        component.add(facetPathComponent);
//        component.add(constraintsComponent);
        component.add(facetComponent);

        facetComponent.setSplitterPosition(20);
        return component;
    }

    protected Component getResultsComponent() {
        SplitLayout component = new SplitLayout();
        component.setOrientation(Orientation.HORIZONTAL);

        //component.addToPrimary(itemComponent);
        component.addToPrimary(sparqlGridComponent);


        component.addToSecondary(resourceBrowserComponent);
        return component;
    }

    public void viewNode(Node node) {
        RDFNode rdfNode = facete3.fetchIfResource(node);
        if ( rdfNode != null )
        {  resourceBrowserComponent.setNode(rdfNode); }

    }

    public void viewNode(FacetValueCount facetValueCount) {
        viewNode(facetValueCount.getValue());
    }

    public void selectFacet(Node node) {
        facete3.setSelectedFacet(node);
        facetValueCountComponent.refresh();
    }

    public void activateConstraint(FacetValueCount facetValueCount) {
        facete3.activateConstraint(facetValueCount);
        refreshAll();
    }

    public void deactivateConstraint(FacetValueCount facetValueCount) {
        facete3.deactivateConstraint(facetValueCount);
        refreshAll();
    }

    // TODO Why the long class declaration?
    public void setFacetDirection(org.aksw.facete.v3.api.Direction direction) {
        facete3.setFacetDirection(direction);
        facetCountComponent.refresh();
        facetPathComponent.refresh();
    }

    public void resetPath() {
        facete3.resetPath();
        facetCountComponent.refresh();
        facetPathComponent.refresh();
    }

    public void addFacetToPath(FacetCount facet) {
        facete3.addFacetToPath(facet);
        facetCountComponent.refresh();
        facetPathComponent.refresh();
    }

    public void changeFocus(FacetNode facet) {
        facete3.changeFocus(facet);
        facetCountComponent.refresh();
        facetPathComponent.refresh();
    }

    public void handleSearchResponse(RDFNodeSpec rdfNodeSpec) {

        if (rdfNodeSpec.isCollection()) {
            UnaryRelation baseConcept = ConceptUtils.createConceptFromRdfNodes(rdfNodeSpec.getCollection());
            facete3.setBaseConcept(baseConcept);
        } else if (rdfNodeSpec.isRootedQuery()) {

            // FIXME Not all possible cases are handled here
            // We just assume that the rooted query's root node is a variable that appears in the element

            RootedQuery rq = rdfNodeSpec.getRootedQuery();
            Var var = (Var)rq.getRootNode();
            Element element = rq.getObjectQuery().getRelation().getElement();
            UnaryRelation concept = new Concept(element, var);

            facete3.setBaseConcept(concept);

        } else {
            throw new RuntimeException("Unknown rdfNodeSpec type "  + rdfNodeSpec);
        }

        RDFConnection effectiveDataConnection = baseDataConnection;
        SearchSensitiveRDFConnectionTransform connectionTransform = activeSearchPlugin.getConnectionTransform();
        if (connectionTransform != null) {
            RDFConnectionTransform connXform = connectionTransform.create(rdfNodeSpec);
            effectiveDataConnection = connXform.apply(effectiveDataConnection);
        }

        facete3.getFacetedQuery().connection(effectiveDataConnection);

        refreshAll();
    }

    public void deactivateConstraint(HLFacetConstraint<?> constraint) {
        constraint.deactivate();
        refreshAll();
    }
//
//    protected List<String> getPaperIds(NliResponse response) {
//        List<Paper> papers = response.getResults();
//        List<String> ids = new LinkedList<String>();
//        for (Paper paper : papers) {
//            ids.addAll(paper.getId());
//        }
//        return ids;
//    }

    private Concept createConcept(List<String> ids) {
        List<Node> baseConcepts = new LinkedList<Node>();
        for (String id : ids) {
            Node baseConcept = NodeFactory.createURI(id);
            baseConcepts.add(baseConcept);
        }
        return ConceptUtils.createConcept(baseConcepts);
    }

    protected void refreshAll() {
        refreshAllNew();
//        facetCountComponent.refresh();
//        facetValueCountComponent.refresh();
//        itemComponent.refresh();
//        resourceComponent.refesh();
//        constraintsComponent.refresh();
    }

}
