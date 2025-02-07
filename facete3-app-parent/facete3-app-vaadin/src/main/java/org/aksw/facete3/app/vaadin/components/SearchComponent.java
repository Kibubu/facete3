package org.aksw.facete3.app.vaadin.components;

import java.util.function.Supplier;

import org.aksw.facete3.app.shared.concept.RDFNodeSpec;
import org.aksw.facete3.app.vaadin.providers.SearchProvider;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class SearchComponent extends VerticalLayout {

    private static final long serialVersionUID = -331380480912293631L;
    protected FacetedBrowserView mainView;
    protected Supplier<SearchProvider> searchProvider;

    public SearchComponent(FacetedBrowserView mainView, Supplier<SearchProvider> searchProvider) {
        this.mainView = mainView;
        this.searchProvider = searchProvider;

        addSearchComponent();
    }


    private void addSearchComponent() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search...");
        searchField.addValueChangeListener(this::searchCallback);
        add(searchField);
    }

    private void searchCallback(ComponentValueChangeEvent<TextField, String> event) {
        String query = event.getValue();
        RDFNodeSpec searchResult = searchProvider.get().search(query);
//        URI uri = UriComponentsBuilder.fromUriString(nliConfig.getEnpoint())
//                .queryParam("query", query)
//                .queryParam("limit", nliConfig.getResultLimit())
//                .build()
//                .toUri();
//        NliResponse response = restTemplate.getForObject(uri, NliResponse.class);
        mainView.handleSearchResponse(searchResult);
    }
}
