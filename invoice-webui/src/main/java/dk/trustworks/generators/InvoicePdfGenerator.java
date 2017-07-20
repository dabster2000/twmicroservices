package dk.trustworks.generators;

import dk.trustworks.network.dto.Invoice;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;

/**
 * Created by hans on 10/07/2017.
 */
public class InvoicePdfGenerator {

    public static void main(String[] args) throws IOException
    {
        //String formTemplate = "invoice-webui/src/main/resources/faktura2.pdf";

        // load the document
        //PDDocument document = PDDocument.load(new File(formTemplate));
        PDDocument document = new PDDocument();

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage( page );

        //Retrieving the pages of the document
        //PDPage page = document.getPage(0);
        System.out.println("page.getBleedBox().getUpperRightY() = " + page.getBleedBox().getUpperRightY());
        System.out.println("page.getBleedBox().getWidth() = " + page.getBleedBox().getWidth());
        System.out.println("page.getBleedBox().getHeight() = " + page.getBleedBox().getHeight());
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        //Creating PDImageXObject object
        PDImageXObject pdImage = PDImageXObject.createFromFile("invoice-webui/src/main/resources/faktura-high.png", document);

        //Drawing the image in the PDF document
        contentStream.drawImage(pdImage, 0, 0, 595f, 842f);

        System.out.println("Image inserted");


        //Begin the Content stream
        contentStream.beginText();

        float leading = 17.0f;

        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.setLeading(leading);

        //Setting the position for the line
        contentStream.newLineAtOffset(46, PDRectangle.A4.getHeight() - 123 - leading);

        String clientname = "AP Pension";
        contentStream.showText(clientname);

        contentStream.newLine();

        contentStream.setFont(PDType1Font.HELVETICA, 14);
        String address = "Østbanegade 135";
        contentStream.showText(address);

        contentStream.newLine();
        String zipCity = "2100 København Ø";
        contentStream.showText(zipCity);

        contentStream.newLine();
        String cvr = "CVR: 12341234";
        contentStream.showText(cvr);

        contentStream.newLine();
        String space = " ";
        contentStream.showText(space);

        contentStream.newLine();
        String attention = "Att: Jørgen Nygaard";
        contentStream.showText(attention);

        contentStream.endText();
        contentStream.beginText();

        contentStream.newLineAtOffset(135, 546 );
        String description = "Projekt ID: A_2014-9 FSS Advice STP - For perioden: juni 2017";
        contentStream.showText(description);

        contentStream.endText();
        contentStream.beginText();

        // Task 42, 380
        //contentStream.newLineAtOffset(-4, - 257 );
        contentStream.newLineAtOffset(42, 440 );
        String task1 = "BEC Arkitektur";
        contentStream.showText(task1);

        // Konsulent 154, 380
        contentStream.newLineAtOffset(112, 0);
        String konsulent1 = "Hans Lassen";
        contentStream.showText(konsulent1);

        // Timepris 307,380
        contentStream.newLineAtOffset(153, 0);
        String timepris1 = "1250 kr";
        contentStream.showText(timepris1);

        // Timer 420, 380
        contentStream.newLineAtOffset( 113, 0);
        String timer = "125.5";
        contentStream.showText(timer);

        // Timer 420, 380
        contentStream.newLineAtOffset( 90, 0);
        String amount = "156.875";
        contentStream.showText(amount);



        //Ending the content stream
        contentStream.endText();

        System.out.println("Content added");

        //Closing the content stream
        contentStream.close();



        document.save("invoice-webui/target/FillFormField.pdf");
        document.close();
    }


    public void createInvoice(Invoice invoice) {
        /*
        try {
            File sampleFile = null;
            sampleFile = new File(System.getProperty("user.home") + "/invoice-template.xlsx");
            Workbook wb = null;
            try {
                System.out.println("1");
                wb = WorkbookFactory.create(sampleFile);
                System.out.println("create");
                Sheet sheet = wb.getSheet("Faktura");
                System.out.println("sheet");
                TreeItem<MonthlyReportProperty> selectedItem = monthlyBudgetTreeTableView.getSelectionModel().getSelectedItem();
                System.out.println("selected");
                MonthlyReportProperty projectReport = selectedItem.getValue();
                System.out.println("report");
                String taskUUID = projectReport.taskUUID;
                System.out.println(taskUUID);


                // Contact information
                sheet.getRow(7).getCell(1).setCellValue(clientData.getClientName());
                sheet.getRow(8).getCell(1).setCellValue(clientData.getStreetNameNumber());
                sheet.getRow(9).getCell(1).setCellValue(clientData.getPostalCode() + " " + clientData.getCity());
                if(clientData.getCvr().trim().length() > 0)
                    sheet.getRow(10).getCell(1).setCellValue("CVR: "+clientData.getCvr());
                else if(clientData.getEAN().trim().length() > 0)
                    sheet.getRow(10).getCell(1).setCellValue("CVR: "+clientData.getEAN());
                sheet.getRow(12).getCell(1).setCellValue("Att: "+clientData.getContactPerson());

                System.out.println("3");
                // Invoice information
                sheet.getRow(14).getCell(5).setCellValue("Faktura nr: "+txtInvoiceNumber.getText());
                sheet.getRow(15).getCell(5).setCellValue("Dato: "+new DateTime().minusMonths(1).withDayOfMonth(new DateTime().minusMonths(1).dayOfMonth().getMaximumValue()).toString("dd MMM yyyy"));

                // Description
                sheet.getRow(18).getCell(2).setCellValue(task.getProject().getCustomerReference() + "\nFor perioden: " + new DateTime().withYear(Integer.parseInt(txtYear.getText())).withMonthOfYear(Integer.parseInt(txtReportMonth.getText())).toString("MMMM yyyy", new Locale("da", "DK")));
                System.out.println("4");
                // Tasks
                int taskRow = 30;
                for (TreeItem<MonthlyReportProperty> taskItem : selectedItem.getChildren()) {
                    MonthlyReportProperty taskProperty = taskItem.getValue();
                    sheet.getRow(taskRow).getCell(1).setCellValue(taskProperty.name.getValue());
                    sheet.getRow(taskRow).getCell(2).setCellValue(taskProperty.workerName.getValue());
                    sheet.getRow(taskRow).getCell(4).setCellValue(taskProperty.rate.getValue());
                    sheet.getRow(taskRow).getCell(5).setCellValue(taskProperty.hours.getValue());
                    sheet.getRow(taskRow).getCell(6).setCellValue(taskProperty.sum.getValue());
                    taskRow++;
                }
                System.out.println("5");
                // Sums
                sheet.getRow(55).getCell(6).setCellValue(projectReport.sum.doubleValue());
                sheet.getRow(56).getCell(6).setCellValue(projectReport.sum.doubleValue() * 0.25);
                sheet.getRow(57).getCell(6).setCellValue(projectReport.sum.doubleValue() * 1.25);
                System.out.println(parseExample());
            } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            System.out.println("6");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Invoice");
            ExtensionFilter filter1 = new ExtensionFilter(
                    "Excel file (*.xlsx)", "*.xlsx");
            fileChooser.getExtensionFilters().add(filter1);
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try {
                    wb.write(new FileOutputStream(file));
                    //Files.copy(new FileInputStream(sampleFile), file.toPath());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        */
    }

}
