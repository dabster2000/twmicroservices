package dk.trustworks.invoicewebui.web.faq.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.jobs.FaqPowerpointPreloader;
import dk.trustworks.invoicewebui.model.Faq;
import dk.trustworks.invoicewebui.network.clients.DropboxAPI;
import dk.trustworks.invoicewebui.repositories.FaqRepository;
import dk.trustworks.invoicewebui.security.Authorizer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.viritin.fields.MTextField;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dk.trustworks.invoicewebui.model.RoleType.EDITOR;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;

@SpringComponent
@SpringUI
public class FaqCanvas extends VerticalLayout {

    @Autowired
    private DropboxAPI dropboxAPI;

    @Autowired
    private FaqRepository faqRepository;

    @Autowired
    private Authorizer authorizer;

    @Autowired
    private FaqPowerpointPreloader faqPowerpointPreloader;

    ///Users/hans/Dropbox (TrustWorks ApS)/Shared/TrustWorks/Intra/faq/pptx

    @Transactional
    public FaqCanvas init() {
        this.removeAllComponents();
        Map<Faq, ResponsiveColumn> cardColumns = new HashMap<>();

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

        ResponsiveRow searchRow = responsiveLayout.addRow();
        searchRow.addColumn().withDisplayRules(12, 12, 4, 4)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 1)
                .withOffset(ResponsiveLayout.DisplaySize.MD, 1)
                .withComponent(new MTextField("filter")
                        .withFullWidth()
                        .addTextChangeListener(event -> {
                            for (Faq faq : cardColumns.keySet()) {
                                if (faq.getTitle().length() == 0 || faq.getContent().length() == 0) continue;
                                if (StringUtils.containsIgnoreCase(faq.getTitle(), event.getValue())) {
                                    cardColumns.get(faq).setVisibilityRules(true, true, true, true);
                                } else try {
                                    if (StringUtils.containsIgnoreCase(new String(getDecoder().decode(faq.getContent()), "utf-8"), event.getValue())) {
                                        cardColumns.get(faq).setVisibilityRules(true, true, true, true);
                                    } else {
                                        cardColumns.get(faq).setVisibilityRules(false, false, false, false);
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }));

        ResponsiveRow cardRow = responsiveLayout.addRow();

        String group = "";
        FaqCardDesign faqCard = null;
        List<Faq> faqList = faqRepository.findByOrderByTitleAsc();

        /*
        Map<String, List<String>> imageSlideShows = faqPowerpointPreloader.getImageSlideShows();
        for (String filename : imageSlideShows.keySet()) {
            PhotosFaqCardDesign photosCard = new PhotosFaqCardDesign();
            photosCard.getLblTitle().setValue(filename);
            ResponsiveColumn cardColumn = cardRow.addColumn()
                    .withDisplayRules(12, 12, 6, 6)
                    .withComponent(photosCard);
            try {
                cardColumns.put(new Faq(filename, filename, getEncoder().encodeToString(filename.getBytes("utf-8"))), cardColumn);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            int i = 0;
            for (String imagePath : imageSlideShows.get(filename)) {
                if(i==0) {
                    byte[] specificFile = dropboxAPI.getSpecificBinaryFile(imagePath);
                    StreamResource streamResource = new StreamResource((StreamResource.StreamSource) () -> new ByteArrayInputStream(specificFile), "logo"+i+".png");
                    com.vaadin.ui.Image image = new Image("", streamResource);

                    MVerticalLayout cssLayout = new MVerticalLayout().withComponent(image).withFullWidth().withFullHeight().withMargin(false);
                    cssLayout.setId(filename);
                    photosCard.getTabSheet().addTab(cssLayout, "" + (i++));

                    image.setWidth("100%");
                    image.setHeight("100%");
                } else {
                    MVerticalLayout cssLayout = new MVerticalLayout().withFullWidth().withFullHeight().withMargin(false);
                    cssLayout.setId(filename);
                    photosCard.getTabSheet().addTab(cssLayout, "" + (i++));
                }
            }

            photosCard.getTabSheet().addSelectedTabChangeListener(event -> {
                int count = 0;
                List<String> imagepaths = imageSlideShows.get(filename);
                for (Component component : photosCard.getTabSheet()) {
                    if(count > 0) {
                        if(((VerticalLayout) component).getComponentCount() > 0) continue;
                        byte[] specificFile = dropboxAPI.getSpecificBinaryFile(imagepaths.get(count));
                        StreamResource streamResource = new StreamResource((StreamResource.StreamSource) () -> new ByteArrayInputStream(specificFile), "logo"+count+".png");
                        com.vaadin.ui.Image image = new Image("", streamResource);
                        ((VerticalLayout) component).addComponent(image);
                        image.setWidth("100%");
                        image.setHeight("100%");
                    }
                    count++;
                }

            });
        }
        */

        /*
        Map<String, String> pdfs = faqPowerpointPreloader.getPdfs();
        for (String filename : pdfs.keySet()) {
            byte[] specificFile = dropboxAPI.getSpecificBinaryFile(pdfs.get(filename));
            System.out.println("specificFile.length = " + specificFile.length);
            StreamResource streamResource = new StreamResource((StreamResource.StreamSource) () -> new ByteArrayInputStream(specificFile), filename+".pdf");
            //WTPdfViewer pdfViewer = new WTPdfViewer();
            //PdfViewer pdfViewer = new PdfViewer(streamResource);
            //pdfViewer.setResource(streamResource);
            //pdfViewer.setHeight("100%");
            //pdfViewer.setWidth("100%");
            //pdfViewer.setAngleButtonVisible(false);
            //pdfViewer.setDownloadBtnVisible(false);


            PdfFaqCardDesign pdfFaqCardDesign = new PdfFaqCardDesign();
            pdfFaqCardDesign.getLblTitle().setValue(filename);
            //pdfFaqCardDesign.getContent().addComponent(pdfViewer);
            ResponsiveColumn cardColumn = cardRow.addColumn()
                    .withDisplayRules(12, 12, 6, 6)
                    .withComponent(pdfFaqCardDesign);
            try {
                cardColumns.put(new Faq(filename, filename, getEncoder().encodeToString(filename.getBytes("utf-8"))), cardColumn);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        */
        /*
        Map<String, List<byte[]>> slidesShows = faqPowerpointPreloader.getSlidesShows();
        for (String filename : slidesShows.keySet()) {
            PhotosFaqCardDesign photosCard = new PhotosFaqCardDesign();
            photosCard.getLblTitle().setValue(filename);
            ResponsiveColumn cardColumn = cardRow.addColumn()
                    .withDisplayRules(12, 12, 6, 6)
                    .withComponent(photosCard);
            try {
                cardColumns.put(new Faq(filename, filename, getEncoder().encodeToString(filename.getBytes("utf-8"))), cardColumn);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            int i = 0;
            for (byte[] bytes : slidesShows.get(filename)) {
                StreamResource streamResource = new StreamResource((StreamResource.StreamSource) () -> new ByteArrayInputStream(bytes), "logo"+i+".png");
                com.vaadin.ui.Image image = new Image("", streamResource);
                photosCard.getTabSheet().addTab(image, "" + (i++));
                image.setWidth("100%");
                image.setHeight("100%");
            }
        }
        */

        for (Faq faq : faqList) {
            if (!faq.getFaqgroup().equals(group)) {
                faqCard = new FaqCardDesign();
                ResponsiveColumn cardColumn = cardRow.addColumn()
                        .withDisplayRules(12, 12, 6, 4)
                        .withComponent(faqCard);
                cardColumns.put(faq, cardColumn);
            }

            createFaqCard(cardColumns, cardRow, faqCard, faq);
        }
        if (authorizer.hasAccess(EDITOR)) createAddFaqCard(cardColumns, cardRow);

        this.addComponent(responsiveLayout);
        return this;
    }

    private void createAddFaqCard(Map<Faq, ResponsiveColumn> cardColumns, ResponsiveRow cardRow) {
        AddFaqCardDesign addFaqCardDesign = new AddFaqCardDesign();

        final Faq addFaq = new Faq("", "", "");
        ResponsiveColumn cardColumn = cardRow.addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(addFaqCardDesign);
        cardColumns.put(addFaq, cardColumn);

        addFaqCardDesign.getBtnAddFaq().addClickListener(event -> {
            FaqCardDesign cardDesign = new FaqCardDesign();
            Faq faq1 = null;
            try {
                faq1 = new Faq("NewGroup", "New Title", getEncoder().encodeToString("New Content".getBytes("utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            faqRepository.save(faq1);
            cardRow.removeComponent(cardColumns.get(addFaq));
            ResponsiveColumn cardColumn2 = cardRow.addColumn()
                    .withDisplayRules(12, 12, 6, 4)
                    .withComponent(cardDesign);
            cardColumns.put(new Faq(), cardColumn2);
            createFaqCard(cardColumns, cardRow, cardDesign, faq1);
            ResponsiveColumn cardColumn3 = cardRow.addColumn()
                    .withDisplayRules(12, 12, 6, 4)
                    .withComponent(addFaqCardDesign);
            cardColumns.put(addFaq, cardColumn3);
        });
    }

    private void createFaqCard(Map<Faq, ResponsiveColumn> cardColumns, ResponsiveRow cardRow, FaqCardDesign faqCard, Faq faq) {
        faqCard.getLblTitle().setValue(faq.getTitle());
        try {
            faqCard.getLblContent().setValue(new String(getDecoder().decode(faq.getContent()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        faqCard.getTxtTitle().setValue(faq.getTitle());
        try {
            faqCard.getTxtContent().setValue(new String(getDecoder().decode(faq.getContent()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final FaqCardDesign sFaqCard = faqCard;

        faqCard.getBtnSave().addClickListener(event -> {
            faq.setTitle(sFaqCard.getTxtTitle().getValue());
            try {
                faq.setContent(getEncoder().encodeToString(sFaqCard.getTxtContent().getValue().getBytes("utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println("faq = " + faq);
            sFaqCard.getLblTitle().setValue(sFaqCard.getTxtTitle().getValue());
            sFaqCard.getLblContent().setValue(sFaqCard.getTxtContent().getValue());
            faqRepository.save(faq);

            sFaqCard.getLblContent().setVisible(true);
            sFaqCard.getLblTitle().setVisible(true);
            sFaqCard.getBtnEdit().setVisible(true);
            sFaqCard.getTxtContent().setVisible(false);
            sFaqCard.getTxtTitle().setVisible(false);
            sFaqCard.getBtnSave().setVisible(false);
            sFaqCard.getBtnDelete().setVisible(false);
            sFaqCard.getPanel().setHeight("400px");
        });

        faqCard.getBtnDelete().addClickListener(event -> {
            cardRow.removeComponent(cardColumns.get(faq));
            faqRepository.delete(faq);
        });

        if (!authorizer.hasAccess(EDITOR)) faqCard.getBtnEdit().setVisible(false);
        faqCard.getBtnEdit().addClickListener(event -> {
            sFaqCard.getLblContent().setVisible(false);
            sFaqCard.getLblTitle().setVisible(false);
            event.getButton().setVisible(false);
            sFaqCard.getTxtContent().setVisible(true);
            sFaqCard.getTxtTitle().setVisible(true);
            sFaqCard.getBtnSave().setVisible(true);
            sFaqCard.getBtnDelete().setVisible(true);
            sFaqCard.getPanel().setHeight("700px");
        });
    }
}
