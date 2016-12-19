/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.fv;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.ImgTemplate;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;





/**
 *
 * @author k.skowronski
 */
public class PdfExport {
    
    private String fontDirectory = null;
private final String baseFont = "Arial";

private PdfWriter writer;
private Document document;

private Font captionFont;
private Font normalFont;

private String svgStr;

/**
 * Writes a PDF file with some static example content plus embeds the chart
 * SVG.
 * 
 * @param pdffilename
 *            PDF's filename
 * @param svg
 *            SVG as a String
 * @return PDF File
 */
public File writePdf(String pdffilename, String svg) {
    svgStr = svg;

    document = new Document();
    document.addTitle("PDF Sample");
    document.addCreator("Vaadin");
    initFonts();

    File file = null;
    try {
        file = writeToFile(pdffilename, document);

        document.open();

        writePdfContent();

        document.close();
    } catch (DocumentException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return file;
}

/**
 * Get Font directory that will be checked for custom fonts.
 * 
 * @return Path to fonts
 */
public String getFontDirectory() {
    return fontDirectory;
}

/**
 * Set Font directory that will be checked for custom fonts.
 * 
 * @param fontDirectory
 *            Path to fonts
 */
public void setFontDirectory(String fontDirectory) {
    this.fontDirectory = fontDirectory;
}

private void initFonts() {
    if (fontDirectory != null) {
        FontFactory.registerDirectory(fontDirectory);
    }

    captionFont = FontFactory.getFont(baseFont, 10, Font.BOLD);
    normalFont = FontFactory.getFont(baseFont, 10, Font.NORMAL);
}

private File writeToFile(String filename, Document document)
        throws DocumentException, FileNotFoundException {
    File file = null;
    try {
        file = File.createTempFile(filename, ".pdf");
        file.deleteOnExit();
        FileOutputStream fileOut = new FileOutputStream(file);
        writer = PdfWriter.getInstance(document, fileOut);
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return file;
}

private void writePdfContent() throws DocumentException, IOException {
    Paragraph caption = new Paragraph();
    caption.add(new Chunk("Vaadin Charts Export Demo PDF", captionFont));
    document.add(caption);

    Paragraph br = new Paragraph(Chunk.NEWLINE);
    document.add(br);

    Paragraph paragraph = new Paragraph();
    paragraph.add(new Chunk("This PDF is rendered with iText 2.1.7.",
            normalFont));
    document.add(paragraph);

    paragraph = new Paragraph();
    paragraph
            .add(new Chunk(
                    "Chart below is originally an SVG image created with Vaadin Charts and rendered with help of Batik SVG Toolkit.",
                    normalFont));
    document.add(paragraph);

    document.add(createSvgImage(writer.getDirectContent(), 400, 400));

    document.add(createExampleTable());
}

private PdfPTable createExampleTable() throws BadElementException {
    PdfPTable table = new PdfPTable(2);
    table.setHeaderRows(1);
    table.setWidthPercentage(100);
    table.setTotalWidth(100);

    // Add headers
    table.addCell(createHeaderCell("Browser"));
    table.addCell(createHeaderCell("Percentage"));

    // Add rows
    table.addCell(createCell("Firefox"));
    table.addCell(createCell("45.0"));
    table.addCell(createCell("IE"));
    table.addCell(createCell("26.8"));
    table.addCell(createCell("Chrome"));
    table.addCell(createCell("12.8"));
    table.addCell(createCell("Safari"));
    table.addCell(createCell("8.5"));
    table.addCell(createCell("Opera"));
    table.addCell(createCell("6.2"));
    table.addCell(createCell("Others"));
    table.addCell(createCell("0.7"));

    return table;
}

private PdfPCell createHeaderCell(String caption)
        throws BadElementException {
    Chunk chunk = new Chunk(caption, captionFont);
    Paragraph p = new Paragraph(chunk);
    p.add(Chunk.NEWLINE);
    p.add(Chunk.NEWLINE);

    PdfPCell cell = new PdfPCell(p);
    cell.setBorder(0);
    cell.setBorderWidthBottom(1);
    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
    return cell;
}

private PdfPCell createCell(String value) throws BadElementException {
    PdfPCell cell = new PdfPCell(new Phrase(new Chunk(value, normalFont)));
    cell.setBorder(0);
    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
    return cell;
}

private Image drawUnscaledSvg(PdfContentByte contentByte)
        throws IOException, BadElementException {

    PdfTemplate template = contentByte.createTemplate(600, 700);

        return new ImgTemplate(template);

}


private Image createSvgImage(PdfContentByte contentByte,
        float maxPointWidth, float maxPointHeight) throws IOException, BadElementException {
    Image image = drawUnscaledSvg(contentByte);
    image.scaleToFit(maxPointWidth, maxPointHeight);
    return image;
}
    
}
