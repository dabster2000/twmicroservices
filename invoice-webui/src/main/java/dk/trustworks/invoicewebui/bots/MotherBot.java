package dk.trustworks.invoicewebui.bots;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.dialog.DialogOpenRequest;
import com.github.seratch.jslack.api.methods.response.dialog.DialogOpenResponse;
import com.github.seratch.jslack.api.model.dialog.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
public class MotherBot {

    @Value("${motherSlackBotToken}")
    private String motherSlackToken;


    //@Scheduled(fixedDelay = 10000)
    public void execute() throws IOException, SlackApiException {
        Slack slack = Slack.getInstance();

        String triggerId = "trigger-id";


        DialogTextElement quanityTextElement = DialogTextElement.builder()
                .subtype(DialogSubType.NUMBER)
                .label("Quantity")
                .name("quantity")
                .hint("The number you need")
                .maxLength(3)
                .minLength(1)
                .placeholder("Required quantity")
                .value("1")
                .build();

        DialogSelectElement colourSelectElement = DialogSelectElement.builder()
                .name("colour")
                .label("Colour")
                .placeholder("Choose your preferred colour")
                .options(Arrays.asList(
                        DialogOption.builder().label("Red").value("#FF0000").build(),
                        DialogOption.builder().label("Green").value("#00FF00").build(),
                        DialogOption.builder().label("Blue").value("#0000FF").build(),
                        DialogOption.builder().label("Black").value("#000000").build(),
                        DialogOption.builder().label("White").value("#FFFFFF").build()
                ))
                .build();


        Dialog dialog = Dialog.builder()
                .title("Request pens")
                .callbackId("pens-1122")
                .elements(Arrays.asList(quanityTextElement, colourSelectElement))
                .submitLabel("")
                .build();

        DialogOpenResponse openDialogResponse = slack.methods().dialogOpen(
                DialogOpenRequest.builder()
                        .token(motherSlackToken)
                        .triggerId(triggerId)
                        .dialog(dialog)
                        .build());

    }

}
