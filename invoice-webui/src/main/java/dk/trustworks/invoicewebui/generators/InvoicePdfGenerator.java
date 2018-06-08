package dk.trustworks.invoicewebui.generators;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.renderer.IRenderer;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.model.InvoiceItem;
import dk.trustworks.invoicewebui.model.InvoiceType;
import dk.trustworks.invoicewebui.utils.StringUtils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by hans on 18/07/2017.
 */

@Component
public class InvoicePdfGenerator {

    private final String INVOICE_IMAGE = "/invoice-high.png";
    private final String CREDIT_NOTE_IMAGE = "/credit-note-high.png";

    public byte[] createInvoice(Invoice invoice) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        PageSize pageSize = new PageSize(PageSize.A4);
        Document document = new Document(pdf, pageSize);
        PdfCanvas canvas = new PdfCanvas(pdf.addNewPage());

        if(invoice.type.equals(InvoiceType.INVOICE))
            canvas.addImage(ImageDataFactory.create(getClass().getResource(INVOICE_IMAGE)), pageSize, false);
        else
            canvas.addImage(ImageDataFactory.create(getClass().getResource(CREDIT_NOTE_IMAGE)), pageSize, false);
        canvas.concatMatrix(1, 0, 0, 1, 0, PageSize.A4.getHeight());

        float[] tableColumns = {112.0f, 153.0f, 83.0f, 110.0f, 70.0f};

        double sumNoTax = 0.0;
        Table table = new Table(tableColumns, false);
        for (InvoiceItem invoiceitem : invoice.invoiceitems) {
            sumNoTax += (invoiceitem.hours * invoiceitem.rate);
            table.addCell(new Cell(1, 1)
                    .setTextAlignment(TextAlignment.LEFT)
                    .add(invoiceitem.itemname)
                    .setBorder(Border.NO_BORDER));

            table.addCell(new Cell(1, 1)
                    .setTextAlignment(TextAlignment.LEFT)
                    .add(invoiceitem.description)
                    .setBorder(Border.NO_BORDER));

            table.addCell(new Cell(1, 1)
                    .setTextAlignment(TextAlignment.LEFT)
                    .add(convertDoubleToDanish(invoiceitem.rate))
                    .setBorder(Border.NO_BORDER));

            table.addCell(new Cell(1, 1)
                    .setTextAlignment(TextAlignment.LEFT)
                    .add(convertDoubleToDanish(invoiceitem.hours) + " timer")
                    .setBorder(Border.NO_BORDER));

            table.addCell(new Cell(1, 1)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .add(convertDoubleToDanish(invoiceitem.hours * invoiceitem.rate))
                    .setBorder(Border.NO_BORDER));
        }
        /*
        for (int i = 0; i < 20; i++) {
            Cell aCell = new Cell(1, 1)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .add("Hi")
                    .setBorder(Border.NO_BORDER);
            table.addCell(aCell);
        }*/

        table.setBorder(Border.NO_BORDER);
        IRenderer tableRenderer = table.createRendererSubTree().setParent(document.getRenderer());
        LayoutResult tableLayoutResult = tableRenderer.layout(new LayoutContext(new LayoutArea(0, new Rectangle(528, 1000))));
        float tableHeightTotal = tableLayoutResult.getOccupiedArea().getBBox().getHeight();

        table.setFixedPosition(40, -380-(tableHeightTotal), 528); // table.getNumberOfRows()*22

        document.add(table);

        List<String> text = new ArrayList();
        text.add(invoice.clientname);
        text.add(invoice.clientaddresse);
        text.add(invoice.otheraddressinfo);
        text.add(invoice.zipcity);
        if(invoice.cvr!=null && invoice.cvr.trim().length() > 0) text.add("CVR: " + invoice.cvr);
        if(invoice.ean!=null && invoice.ean.trim().length() > 0)  text.add("EAN: " + invoice.ean);
        text.add("");
        text.add("ATT: " + invoice.attention);

        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD), 14)
                .setLeading(14 * 1.2f)
                .moveText(46, -115);
        for (String s : text) {
            canvas.newlineShowText(s);
            canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 14);
        }
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 14)
                .setLeading(14 * 1.2f);
        canvas.endText();

        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD), 12)
                .setLeading(12 * 1.2f)
                .moveText(380, -222);
        canvas.newlineShowText("Faktura nr: "+ StringUtils.convertInvoiceNumberToString(invoice.invoicenumber));
        canvas.newlineShowText("Dato: "+invoice.invoicedate);
        canvas.endText();

        // 129, 296
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 14)
                .setLeading(14 * 1.2f)
                .moveText(135, -278).setLineWidth(345);
        canvas.showText(invoice.contractref);
        canvas.newlineShowText(invoice.projectref);
        canvas.newlineShowText(invoice.specificdescription);
        canvas.endText();

        float[] tableColumns2 = {70.0f};

        Table table2 = new Table(tableColumns2, false);
        table2.setPaddingTop(0.0f);
        table2.setPaddingBottom(0.0f);
        table2.setMarginTop(0.0f);
        table2.setMarginBottom(0.0f);
        table2.addCell(new Cell(1, 1)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingTop(0.0f)
                .setPaddingBottom(0.0f)
                .add(convertDoubleToDanish(sumNoTax))
                .setBorder(Border.NO_BORDER));
        table2.addCell(new Cell(1, 1)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingTop(0.0f)
                .setPaddingBottom(0.0f)
                .add(convertDoubleToDanish(sumNoTax * 0.25))
                .setBorder(Border.NO_BORDER));
        table2.addCell(new Cell(1, 1)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingTop(0.0f)
                .setPaddingBottom(0.0f)
                .setBold()
                .add(convertDoubleToDanish(sumNoTax * 1.25))
                .setBorder(Border.NO_BORDER));
        table2.setBorder(Border.NO_BORDER);
        table2.setFixedPosition(498, -652-(table2.getNumberOfRows()*16), 70);

        document.add(table2);

        document.close();

        return outputStream.toByteArray();
    }

    private BufferedImage loadImage(String imageFilename) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getResourceAsStream(imageFilename));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return img;
    }

    private String convertDoubleToDanish(double aDouble) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        return formatter.format(aDouble);
    }
}
