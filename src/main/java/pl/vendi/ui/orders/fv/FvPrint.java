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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
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
    protected Font font16b;
    
    public String numerFaktury = null;

    
    public FvPrint() throws DocumentException, IOException
    {
        
            BaseFont helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
            Font helvetica16=new Font(helvetica,16);
        
            font10  = new Font(helvetica, 10);
            font10b = new Font(helvetica, 10, Font.BOLD);  
            font12  = new Font(helvetica, 12);
            font12b = new Font(helvetica, 12, Font.BOLD); 
            font14  = new Font(helvetica, 14);
            font16b = new Font(helvetica, 16, Font.BOLD);
    }
    
    
    
    
    
    
    public StreamResource runFVStream( Long dokId ) throws DocumentException, IOException {
        
        pl.vo.documents.model.Document order = VOLookup.lookupDocumentsApi().get(dokId);
        
        // numer faktury
        numerFaktury = 
                order.getOwnNumber().replace("ZWD", "FV").substring(0, order.getOwnNumber().length() - 5) 
                + order.getDateOperation().getDate()
                + "/" + order.getOwnNumber().replace("ZWD", "FV").substring(order.getOwnNumber().length() - 5);
        
        StreamResource.StreamSource source = new StreamResource.StreamSource() {
            
            
            public InputStream getStream() {
             
            ByteArrayOutputStream baos = new ByteArrayOutputStream();    
                
            try {    
                     // step 1
                    Document document = new Document(PageSize.A4);
                
                    
            
                
                    PdfWriter.getInstance(document, baos);
                    
                    // step 3
                    document.open();

                    document.add(Chunk.NEWLINE);   //Something like in HTML :-)
                    
                    
                    
                    
                    Paragraph numInvoice = new Paragraph( numerFaktury , font16b  );
                    numInvoice.setAlignment(Element.ALIGN_CENTER);
                    document.add( numInvoice );
                    
                    document.add(Chunk.NEWLINE);
                    
                    
                    
                     // Address seller / buyer
                    PdfPTable table = new PdfPTable(2);
                    table.setWidthPercentage(100);
                    PdfPCell seller = getPartyAddress("Sprzedawca:",
                            order.getSupplier().getName(), 
                            order.getSupplier().getCity(),
                            order.getSupplier().getNip(),
                            order.getSupplier().getAddress(),
                            "",
                            "" );
                    table.addCell(seller);
                    PdfPCell buyer = getPartyAddress("Nabywca:",
                            order.getClient().getName(), 
                            order.getClient().getCity(),
                            order.getClient().getNip(),
                            order.getClient().getAddress(),
                            "",
                            "" );
                    table.addCell(buyer);
                 
                  
                    document.add(table);
                    
                    
                    document.add(Chunk.NEWLINE);
                    

                    // line items
                    table = new PdfPTable(7);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10);
                    table.setSpacingAfter(10);
                    table.setWidths(new int[]{7, 2, 2, 1, 2, 2, 2});
                    table.addCell(getCell("Pozycja", Element.ALIGN_LEFT, font12b));
                    table.addCell(getCell("Cena", Element.ALIGN_LEFT, font12b));
                    table.addCell(getCell("Ilość", Element.ALIGN_LEFT, font12b));
                    table.addCell(getCell("jm", Element.ALIGN_LEFT, font12b));
                    table.addCell(getCell("Netto", Element.ALIGN_LEFT, font12b));
                    table.addCell(getCell("VAT", Element.ALIGN_LEFT, font12b));
                    table.addCell(getCell("Brutto", Element.ALIGN_LEFT, font12b));
                    
                    
                    //Locale.setDefault( new Locale("pl","PL") );
                    DecimalFormat df = new DecimalFormat("#,##0.00");
  
                    BigDecimal sumNetto = new BigDecimal(BigInteger.ZERO);
                    BigDecimal sumTax = new BigDecimal(BigInteger.ZERO);
                    BigDecimal sumBrutto = new BigDecimal(BigInteger.ZERO);
                   
                    for ( DocumentItem di : order.getItems() ) {
                     
                        table.addCell(getCell( di.getProduct().getName(), Element.ALIGN_LEFT, font12));
                        table.addCell(getCell( df.format(di.getUnitPriceNet()).toString(), Element.ALIGN_RIGHT, font12));
                        table.addCell(getCell( df.format(di.getAmount()).toString(), Element.ALIGN_RIGHT, font12));
                        table.addCell(getCell( di.getProduct().getMeasureUnit().getAbbr(), Element.ALIGN_RIGHT, font12));
                        table.addCell(getCell( df.format(di.getValueNet()).toString(), Element.ALIGN_RIGHT, font12));
                        table.addCell(getCell( df.format(di.getValueTax()).toString(), Element.ALIGN_RIGHT, font12));
                        table.addCell(getCell( df.format(di.getValueBrut()).toString() , Element.ALIGN_RIGHT, font12));
                        
                        sumNetto = sumNetto.add(di.getValueNet());
                        sumTax = sumTax.add(di.getValueTax());
                        sumBrutto = sumBrutto.add(di.getValueBrut());
                    }
                    document.add(table);
                    
                    
                   
                // grand totals
                PdfPTable t2 = getTotalsTable( df.format(sumNetto), df.format(sumTax), df.format(sumBrutto) );
                document.add( t2 );
 
                // payment info
                //document.add(getPaymentInfo(basic.getPaymentReference(), basic.getPaymentMeansPayeeFinancialInstitutionBIC(), basic.getPaymentMeansPayeeAccountIBAN()));
                    
                    


                    document.add(Chunk.NEWLINE);   //Something like in HTML :-)		
                    
                    // Rabat 
                    if ( !order.dicountIsEmpty() )
                    {
                        Paragraph labDiscount = new Paragraph( "Rabat: " + order.getDiscount().toString() + "%" , font10  );
                        document.add(labDiscount);
                        
                        
                        BigDecimal nettoDiscount = sumNetto.subtract(sumNetto.multiply( (order.getDiscount().divide( new BigDecimal(100) ) ) ) );
                        BigDecimal taxDiscount = sumTax.subtract(sumTax.multiply( (order.getDiscount().divide( new BigDecimal(100) ) ) ) );
                        BigDecimal bruttoDiscount = sumBrutto.subtract(sumBrutto.multiply( (order.getDiscount().divide( new BigDecimal(100) ) ) ) );
                        Paragraph labBruttoDiscount = new Paragraph( 
                                "Wartość po rabacie: "  );
                        document.add(labBruttoDiscount);
                        
                        document.add(Chunk.NEWLINE);
                        
                        PdfPTable t3 = getTotalsTable( df.format( nettoDiscount )
                                , df.format( taxDiscount )
                                , df.format( bruttoDiscount) );
                        document.add( t3 );
                        
                    }
                    

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
        
      StreamResource resource = new StreamResource ( source,  numerFaktury.replace("/", "_") + ".pdf" );
        return resource;
    }
    
    
    public PdfPCell getPartyAddress(String who, String name, String line1, String line2, String countryID, String postcode, String city) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.addElement(new Paragraph(who, font12b));
        cell.addElement(new Paragraph(name, font12));
        cell.addElement(new Paragraph(line1, font12)); // check
        cell.addElement(new Paragraph("NIP: " + line2, font12));
        cell.addElement(new Paragraph(String.format("%s %s %s", countryID, postcode, city), font12));
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
   
  
    
    public PdfPTable getTotalsTable(String tBase, String tTax, String tTotal) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{ 3, 3, 3});
        table.addCell(getCell("Razem Netto:", Element.ALIGN_LEFT, font12b));
        table.addCell(getCell("Razem VAT:", Element.ALIGN_LEFT, font12b));
        table.addCell(getCell("Razem Brutto:", Element.ALIGN_LEFT, font12b));
       /* int n = type.length;
        for (int i = 0; i < n; i++) {
            table.addCell(getCell(type[i], Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(percentage[i], Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(base[i], Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(tax[i], Element.ALIGN_RIGHT, font12));
            double total = Double.parseDouble(base[i]) + Double.parseDouble(tax[i]);
            table.addCell(getCell( "ksTODO" , Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(currency[i], Element.ALIGN_LEFT, font12));
        }*/
        PdfPCell cell = getCell("", Element.ALIGN_LEFT, font12b);
        cell.setColspan(2);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(getCell(tBase, Element.ALIGN_RIGHT, font12b));
        table.addCell(getCell(tTax, Element.ALIGN_RIGHT, font12b));
        table.addCell(getCell(tTotal, Element.ALIGN_RIGHT, font12b));
        return table;
    }
    
    
    
    
}
