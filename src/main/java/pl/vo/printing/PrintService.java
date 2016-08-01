/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.printing;

import java.text.SimpleDateFormat;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.milyn.edi.unedifact.d96a.ORDERS.Orders;
import pl.vo.documents.DocumentsApi;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;

/**
 *
 * @author Piotr
 */
@Path("/print")
@Stateless
public class PrintService {

    @EJB
    DocumentsApi documentsApi;

    @Path("/document/{id}")
    @Produces("text/html; charset=utf-8")
    @GET
    public Response printDocument(@PathParam("id") Long id) {

        Document order = documentsApi.get(id);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        // 
        String ret = "<HTML>";
        ret += "<head><meta charset=\"utf-8\"><link rel='stylesheet' href='../../../css/print.css'></link></head>";
        ret += "<body>";
        ret += "<div class='cnt'>";
        ret += "<h2>";
        ret += "Dokument " ;
        if ( order.getOwnNumber() != null ) 
            ret += " nr: " + order.getOwnNumber();
        
        ret += " z dnia " + df.format( order.getDateOperation() ) +" </h2> ";
        ret += "<table>";
        ret += "<thead><td>Nazwa towaru</td><td>Jednostka</td><td>Ilość zamawiana</td><td>Netto</td><td>Vat</td><td>Brutto</td></thead>";
        ret += "<tbody>";
        
        for ( DocumentItem di : order.getItems()){
          ret +="<tr>";
          ret +="<td> " +di.getProduct().getName() + "</td>";
          ret +="<td> "+di.getProduct().getMeasureUnit().getAbbr()+"</td>";
          ret +="<td> " + di.getAmount().toString() +"</td>";
          ret +="<td class='value'> " + di.getValueNet().toString() +" zł</td>";
          ret +="<td class='value'> " + di.getValueTax().toString() +" zł</td>";
          ret +="<td class='value'> " + di.getValueBrut().toString() +" zł</td>";
           
          ret += "</tr>";
        };

        ret += "</table></div></body></html>";

        return Response.ok().entity(ret).build();
    }

}
