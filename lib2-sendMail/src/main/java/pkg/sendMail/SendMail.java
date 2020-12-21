package pkg.sendMail;

import java.util.*;
import javax.mail.*;
import javax.activation.*;
import javax.mail.internet.*;

public class SendMail {

    public static void main(String[] args) {
        enviaCorreu();
    }

    public static void enviaCorreu() {
        final String username = "dam.multitech@gmail.com";
        final String password = "multitech17#";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("dam.multitech@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("jferrando@ies-eugeni.cat, tscalise@ies-eugeni.cat")
            );
            message.setSubject("Testing Gmail TLS");
            message.setText("Dear Mail Crawler,"
                    + "\n\n Please do not spam my email!");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

        public static void enviaCorreu(String destination, String subject, String message){
        //enviaCorreu(destination,subject,message,null);
    }

}
