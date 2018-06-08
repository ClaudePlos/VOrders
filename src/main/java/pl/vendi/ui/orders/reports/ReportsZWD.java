/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.reports;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.common.ComboBoxOrganisationUnit;
import pl.vendi.ui.common.DateFieldPl;
import pl.vendi.ui.orders.zwd.WndOrderZwd;
import pl.vo.VOConsts;
import pl.vo.common.model.ReportDTO;
import pl.vo.documents.DocumentsApi;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;


/**
 *
 * @author k.skowronski
 */
public class ReportsZWD extends Window implements Button.ClickListener {
    
    private DocumentsApi documentsApi;
    
    VerticalLayout vboxMain = new VerticalLayout();
    HorizontalLayout hboxAdd = new HorizontalLayout();

    ComboBoxOrganisationUnit cmbOrganisationUnit = new ComboBoxOrganisationUnit("Jednostka org");
    
    SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
    
    protected Font font7;
    protected Font font10;
    protected Font font10b;
    protected Font font12;
    protected Font font12b;
    protected Font font14;
    protected Font font16b;
    
    private DateFieldPl dataFrom = new DateFieldPl("2. Data od");
    private DateFieldPl dataTo = new DateFieldPl("3. Data do");
    
  
    public ReportsZWD() throws DocumentException, IOException
    {
        super("Reports");
        
        documentsApi = VOLookup.lookupDocumentsApi();
        this.setContent(vboxMain);
        
        BaseFont helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
        Font helvetica16=new Font(helvetica,16);

        font7  = new Font(helvetica, 7);
        font10  = new Font(helvetica, 10);
        font10b = new Font(helvetica, 10, Font.BOLD);  
        font12  = new Font(helvetica, 12);
        font12b = new Font(helvetica, 12, Font.BOLD); 
        font14  = new Font(helvetica, 14);
        font16b = new Font(helvetica, 16, Font.BOLD);
        
        
        vboxMain.setSpacing( true );
        vboxMain.setMargin( true );
        
        vboxMain.setSizeFull();
        
        hboxAdd.setSpacing(true); // Compact layout
        vboxMain.addComponent(hboxAdd);
        
        
        List<ReportDTO> raports = new ArrayList<>();
    
        raports.add( new ReportDTO(1, "Wykaz zamówień") );
        raports.add( new ReportDTO(2, "-") );
        raports.add( new ReportDTO(3, "-") );
        
    
        BeanItemContainer<ReportDTO> objects = new BeanItemContainer(ReportDTO.class, raports);
    
        ComboBox listReports = new ComboBox("1. Wybierz raport", objects);
        listReports.setItemCaptionPropertyId("name");
        listReports.setWidth("300px");
        
        
        listReports.addValueChangeListener(new ValueChangeListener() {
            
            public void valueChange(ValueChangeEvent event) {
                if (listReports.getValue() != null) {
                    
                ReportDTO val = (ReportDTO) listReports.getValue();
                
                if ( val.getName().equals("Wykaz zamówień") )
                {
                    
                    hboxAdd.addComponent(cmbOrganisationUnit);
                }

             
                
                }
}
            
        });
                
        hboxAdd.addComponent(listReports);

        
        
        hboxAdd.addComponent(dataFrom);

        hboxAdd.addComponent(dataTo);
        
        
        
        Button butRun = new Button("Uruchom");
        
        StreamResource myResource;
           
             
        myResource = runPdf001();
        FileDownloader fileDownloader = new FileDownloader(myResource);
        fileDownloader.extend(butRun);
        
        
      
        
        vboxMain.addComponent(butRun);
        
        
    }
    
    private StreamResource runPdf001()
    {
        
        Date dF = dataFrom.getValue() ;
        Date dT = dataTo.getValue() ;
        Long orgUnitId = cmbOrganisationUnit.getOrganisationUnitId();
        String[] docTypes = null;
        docTypes = new String[]{ VOConsts.DOC_TYPE_ZWD };
        List<Document> docs = documentsApi.findDocumentsFromTo( docTypes, orgUnitId,  dF, dT, VendiOrdersUI.getLoggedUsername());  
      
      StreamResource.StreamSource source = new StreamResource.StreamSource() { 
          
       public InputStream getStream() {   
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        
        try {    
                     // step 1
                    com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4);
                
                    
            
                
                    PdfWriter.getInstance(document, baos);
                    
                    // step 3
                    document.open();

                    document.add(Chunk.NEWLINE);   //Something like in HTML :-)
                    
                    
               
                    
                    
                     // Address seller / buyer
                    PdfPTable table = new PdfPTable(7);
                   
                    

                    // line items
                    table = new PdfPTable(7);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10);
                    table.setSpacingAfter(10);
                    table.setWidths(new int[]{3, 3, 7, 4, 2, 2, 2});
                    table.addCell(getCell("OwnNumber", Element.ALIGN_LEFT, font7));
                    table.addCell(getCell("ExternalNumber", Element.ALIGN_LEFT, font7));
                    table.addCell(getCell("Description", Element.ALIGN_LEFT, font7));
                    table.addCell(getCell("Status", Element.ALIGN_LEFT, font7));
                    table.addCell(getCell("Client", Element.ALIGN_LEFT, font7));
                    table.addCell(getCell("CompanyUnit", Element.ALIGN_LEFT, font7));
                    table.addCell(getCell("Data", Element.ALIGN_LEFT, font7));
                    
                    
                    //Locale.setDefault( new Locale("pl","PL") );
                    DecimalFormat df = new DecimalFormat("#,##0.00");

                   
                    for ( Document doc : docs ) {
                     
                        table.addCell(getCell( doc.getOwnNumber(), Element.ALIGN_LEFT, font7));
                        table.addCell(getCell( doc.getExternalNumber(), Element.ALIGN_LEFT, font7));
                        table.addCell(getCell( doc.getDescription(), Element.ALIGN_LEFT, font7));
                        
                        table.addCell(getCell( doc.getStatus().replace("SENDED_TO_SUPPLIER", "WYSŁANE DO DOSTAWCY")
                                .replace("RECEIVED_BY_SUPPLIER", "OTRZYMANE PRZEZ DOSTAWCY") 
                                .replace("OPEN", "OTWARTE") 
                                .replace("ACCEPTED", "ZAAKCEPTOWANE")
                                .replace("CONFIRMED_SUPPLIER", "POTWIERDZONE PRZEZ DOSTAWCE") 
                                , Element.ALIGN_LEFT, font7)); 
                        
                        if ( doc.getClient() != null )
                          table.addCell(getCell( doc.getClient().getName(), Element.ALIGN_LEFT, font7));
                        else 
                          table.addCell("");
                        
                        if ( doc.getClient() != null )
                          table.addCell(getCell( doc.getCompanyUnit().getName(), Element.ALIGN_LEFT, font7));
                        else 
                          table.addCell("");
                        
                        if ( doc.getDateDelivery() != null )
                          table.addCell(getCell( dt1.format(doc.getDateDelivery() ) , Element.ALIGN_LEFT, font7));
                        else 
                          table.addCell("");
                        
                    }
                    document.add(table);
                    
                    
                   
              
                    

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
                
        StreamResource resource = new StreamResource ( source,  "zamowieniaZWD.pdf" );
        return resource;
        
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
    
    
    
    
    @Override
    public void buttonClick(Button.ClickEvent event) {

    }
  
    
}
