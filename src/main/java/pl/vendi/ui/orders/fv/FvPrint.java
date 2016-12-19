/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.fv;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author k.skowronski
 */
public class FvPrint {
    
    
    
    public FvPrint()
    {
        
        
    }
    
    private void Run()
    {
        File f = new File("test.pdf");
 
        
        try {
            
            OutputStream file = new FileOutputStream(f); //
                  
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, file);

            // Filedownload.save(f, "application/pdf"); // work in zkoss
            
            //FileDownloader fileDownloader = new FileDownloader(sr);
            //fileDownloader.extend(pdfDownload);
            
            
            document.open();
            
            document.add(Chunk.NEWLINE);   //Something like in HTML :-)
 
            document.add(new Paragraph("Okres: " ));
            
            
            document.add(Chunk.NEWLINE);   //Something like in HTML :-)							    
 
            document.newPage();            //Opened new page
            
            //document.add(list);            //In the new page we are going to add list
 
	    document.close();
 
	    file.close();
 
            System.out.println("Pdf created successfully..");
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
                  
        
    }
    
    
    public StreamResource createResource() {
        return new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                
                try
                {
                   File f = new File("C:/themes/test.pdf");
                    FileInputStream fis = new FileInputStream(f);
                    return fis;
                    
                    
                } catch (FileNotFoundException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
                
                


            }
        }, "test.pdf");
    }
    
    
    
   
  
    
}
