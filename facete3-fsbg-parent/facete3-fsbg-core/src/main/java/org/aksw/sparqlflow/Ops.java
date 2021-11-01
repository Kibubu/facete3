package org.aksw.sparqlflow;

import org.aksw.jenax.connection.extra.RDFConnectionEx;
import org.aksw.jenax.connection.extra.RDFConnectionMetaData;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.UpdateRequest;



public class Ops {
    public void execUpdate(UpdateRequest updateRequest, RDFConnectionEx conn) {
        RDFConnectionMetaData m = conn.getMetaData();

        try {
            conn.update(updateRequest);

            Resource dataset = m.getDataset();

            // Create an entry, that a new dataset was derived from the prior one by executing an update statement


            m.getDatasets();

        } catch(Exception e) {

        }

    }
}
