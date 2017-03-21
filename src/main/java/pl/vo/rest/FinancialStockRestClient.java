/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.rest;

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
        target = client.target("http://localhost:40884/VOrdersEgeria")
           //.queryParam("cnt", "10")
           //.queryParam("mode", "json")
           //.queryParam("units", "metric")
                ;

        System.out.print( target.toString() );
    }

    ///rest/monitor/1/2017
    public String getFinancialStock(){
            String response = target.path("rest").
                            path("monitor").
                            path("1").
                            path("2017").
                            request().
                            accept(MediaType.APPLICATION_JSON).
                            get(String.class);
            return response;
    }
    
    
}
