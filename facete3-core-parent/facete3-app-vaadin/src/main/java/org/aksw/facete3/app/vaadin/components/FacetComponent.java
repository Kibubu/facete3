package org.aksw.facete3.app.vaadin.components;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;

import org.aksw.facete.v3.api.FacetCount;
import org.aksw.facete.v3.api.FacetedQuery;
import org.aksw.facete3.app.vaadin.MainView;
import org.aksw.facete3.app.vaadin.QueryConf;
import org.apache.jena.graph.Node;

public class FacetComponent extends VerticalLayout {

    public FacetComponent(MainView mainView, QueryConf queryConf) {

        ConfigurableFilterDataProvider<FacetCount, Void, String> wrapper =
                mainView.facetProvider.withConfigurableFilter();

        add(new Label("Facets"));

        TextField searchField = new TextField();
        searchField.addValueChangeListener(event -> {
            String filter = event.getValue();
            if (filter.trim().isEmpty()) {
                filter = null;
            }
            wrapper.setFilter(filter);
        });
        add(searchField);

        FacetPathComponent facetPath =new FacetPathComponent(mainView, queryConf); 
        add(facetPath);

        Grid<FacetCount> grid = new Grid<>(FacetCount.class);
        grid.setDataProvider(wrapper);
        grid.getColumns().forEach(grid::removeColumn);
        grid.addColumn(FacetCount::getPredicate).setSortProperty("");
        grid.addColumn("distinctValueCount.count").setSortProperty("facetCount");
        grid.asSingleSelect().addValueChangeListener(event -> {
            queryConf.setSelectedFacet(event.getValue().getPredicate());
            mainView.facetValueProvider.refreshAll();
        });
        grid.addItemDoubleClickListener(event -> {
            org.aksw.facete.v3.api.Direction dir = queryConf.getFacetDirNode().dir();
            Node node = event.getItem().getPredicate();
            FacetedQuery facetedQuery = queryConf.getFacetedQuery();
            facetedQuery.focus().step(node, dir).one().chFocus();
            queryConf.setFacetDirNode(facetedQuery.focus().step(dir));
            wrapper.refreshAll();
            facetPath.refresh();
        });
        add(grid);
    }
}
