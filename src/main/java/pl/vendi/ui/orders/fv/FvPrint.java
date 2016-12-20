/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.fv;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.orders.zwd.WndOrderZwd;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.BaseFont;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import pl.vo.documents.model.DocumentItem;

/**
 *
 * @author k.skowronski
 */


public class FvPrint {
  

    protected Font font10;
    protected Font font10b;
    protected Font font12;
    protected Font font12b;
    protected Font font14;
    

    
    public FvPrint() throws DocumentException, IOException
    {    
            font10 = new Font(FontFamily.HELVETICA, 10);
            font10b = new Font(FontFamily.HELVETICA, 10, Font.BOLD);  
            font12 = new Font(FontFamily.HELVETICA, 12);
            font12b = new Font(FontFamily.HELVETICA, 12, Font.BOLD); 
            font14 = new Font(FontFamily.HELVETICA, 14);
    }
    
    
    
    
    
    
    public StreamResource runFVStream( Long dokId ) throws DocumentException, IOException {
        StreamResource.StreamSource source = new StreamResource.StreamSource() {

            pl.vo.documents.model.Document order = VOLookup.lookupDocumentsApi().get(dokId);
            
            public InputStream getStream() {
             
            ByteArrayOutputStream baos = new ByteArrayOutputStream();    
                
            try {    
                     // step 1
                    Document document = new Document(PageSize.A4);
                
                    
            
                
                    PdfWriter.getInstance(document, baos);
                    
                    // step 3
                    document.open();

                    document.add(Chunk.NEWLINE);   //Something like in HTML :-)

                    //document.add(new Paragraph("TEST" + order.getOwnNumber() ));
                    
             
                    
                    
                     // Address seller / buyer
                    PdfPTable table = new PdfPTable(2);
                    table.setWidthPercentage(100);
                    PdfPCell seller = getPartyAddress("From:",
                            order.getSupplier().getName(), 
                            order.getSupplier().getCity(),
                            order.getSupplier().getNip(),
                            order.getSupplier().getAddress(),
                            order.getSupplier().getNip(),
                            order.getSupplier().getExternalId() );
                    table.addCell(seller);
                    PdfPCell buyer = getPartyAddress("To:",
                            order.getClient().getName(), 
                            order.getClient().getCity(),
                            order.getClient().getNip(),
                            order.getClient().getAddress(),
                            order.getClient().getNip(),
                            order.getClient().getExternalId() );
                    table.addCell(buyer);
                 
                  
                    document.add(table);

                    // line items
                    table = new PdfPTable(6);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10);
                    table.setSpacingAfter(10);
                    table.setWidths(new int[]{7, 2, 1, 2, 2, 2});
                    table.addCell(getCell("Item:", Element.ALIGN_LEFT, font12b));
                    table.addCell(getCell("Price:", Element.ALIGN_LEFT, font12b));
                    table.addCell(getCell("Qty:", Element.ALIGN_LEFT, font12b));
                    table.addCell(getCell("Subtotal:", Element.ALIGN_LEFT, font12b));
                    table.addCell(getCell("VAT:", Element.ALIGN_LEFT, font12b));
                    table.addCell(getCell("Total:", Element.ALIGN_LEFT, font12b));
                   
                    for ( DocumentItem di : order.getItems() ) {
                     
                        table.addCell(getCell( di.getProduct().getName(), Element.ALIGN_LEFT, font12));
                        table.addCell(getCell( di.getValueNet().toString(), Element.ALIGN_RIGHT, font12));
                        table.addCell(getCell( di.getValueTax().toString(), Element.ALIGN_RIGHT, font12));
                        table.addCell(getCell( di.getValueBrut().toString(), Element.ALIGN_RIGHT, font12));
                        table.addCell(getCell( di.getValueBrut().toString(), Element.ALIGN_RIGHT, font12));
                        table.addCell(getCell(
                            di.getValueBrut().toString() ,
                            Element.ALIGN_RIGHT, font12));
                    }
                    document.add(table);
                    
                    
                    


                    document.add(Chunk.NEWLINE);   //Something like in HTML :-)							    

                    document.newPage();            //Opened new page

                    //document.add(list);            //In the new page we are going to add list

                    document.close();

                    //file.close();

                    System.out.println("Pdf created successfully..");
                    
                    
                    
                    
                } catch (DocumentException ex) {
                    Logger.getLogger(WndOrderZwd.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ByteArrayOutputStream stream = baos;
                InputStream input = new ByteArrayInputStream(stream.toByteArray());
                  return input;

            }
        };
      StreamResource resource = new StreamResource ( source, "test.pdf" );
        return resource;
    }
    
    
    public PdfPCell getPartyAddress(String who, String name, String line1, String line2, String countryID, String postcode, String city) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.addElement(new Paragraph(who, font12b));
        cell.addElement(new Paragraph(name, font12));
        cell.addElement(new Paragraph(line1, font12));
        cell.addElement(new Paragraph(line2, font12));
        cell.addElement(new Paragraph(String.format("%s-%s %s", countryID, postcode, city), font12));
        return cell;
    }
    
    public PdfPCell getCell(String value, int alignment, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        Paragraph p = new Paragraph(value, font);
        p.setAlignment(alignment);
        cell.addElement(p);
        return cell;
    }
   
  
    
}
