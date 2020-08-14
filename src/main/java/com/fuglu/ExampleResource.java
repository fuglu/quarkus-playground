package com.fuglu;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Path("/mail")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExampleResource {

    @Inject
    SqsClient sqs;

    @Inject
    SesClient ses;

    String queueUrl = "http://localhost:8010/queue/MailQueue";

    static ObjectReader MAIL_READER = new ObjectMapper().readerFor(Mail.class);

    static ObjectWriter MAIL_WRITER = new ObjectMapper().writerFor(Mail.class);

    @GET
    public Response getMail() throws JsonProcessingException {
        List<Message> messages =
                sqs.receiveMessage(m -> m.maxNumberOfMessages(10).queueUrl(queueUrl)).messages();

        var mails =
                messages.stream().map(Message::body).map(this::toMail).collect(Collectors.toList());

        mails.forEach(mail -> {

            var id = ses
                    .sendEmail(req -> req.source(mail.getFrom())
                            .destination(d -> d.toAddresses(mail.getTo()))
                            .message(msg -> msg.subject(sub -> sub.data(mail.getSubject()))
                                    .body(b -> b.text(txt -> txt.data(mail.getBody())))))
                    .messageId();
            System.out.println(id);
        });


        return Response.ok().entity(mails).build();
    }

    @POST
    public Response sendMail(Mail request) throws JsonProcessingException {
        System.out.println(request);
        String message = MAIL_WRITER.writeValueAsString(request);
        System.out.println(message);

        SendMessageResponse response =
                sqs.sendMessage(m -> m.queueUrl(queueUrl).messageBody(message));
        System.out.println(response);
        return Response.ok().entity(response.messageId()).build();
    }

    private Mail toMail(String message) {
        try {
            return MAIL_READER.readValue(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
