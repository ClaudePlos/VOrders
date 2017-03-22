/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.rest;

import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author k.skowronski
 */


public class FinancialStockRestClient {
    
    private Client client;
    public WebTarget target;


    public FinancialStockRestClient() {
        client = ClientBuilder.newClient();
        //query params: ?q=Turku&cnt=10&mode=json&units=metric
        //i2.naprzod.pl
        //localhost:40884
        target = client.target("http://i2.naprzod.pl/VOrdersEgeria")
           //.queryParam("cnt", "10")
           //.queryParam("mode", "json")
           //.queryParam("units", "metric")
                ;

        System.out.print( target.toString() );
    }

    /// items
    public String getFinancialStock( String nip, String rok){
            String response = target.path("rest").
                            path("monitor").
                            path("items").
                            path(nip).
                            path(rok).
                            request().
                            accept(MediaType.APPLICATION_JSON).
                            get(String.class);
            return response;
    }
    
    //Doc
    public String getDocFinancialStock( BigDecimal rozId ){
            String response = target.path("rest").
                            path("monitor").
                            path("doc").
                            path(rozId.toString()).
                            request().
                            accept(MediaType.APPLICATION_JSON).
                            get(String.class);
            return response;
    }
    
    
}
