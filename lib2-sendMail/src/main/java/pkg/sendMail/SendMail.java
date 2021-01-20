package pkg.sendMail;

import com.sun.mail.util.MailConnectException;
import pkg.FileIO.FileIO;

import java.io.File;
import java.util.*;
import javax.mail.*;
import javax.activation.*;
import javax.mail.internet.*;

public class SendMail {

    //Método auxiliar
    private static void addAttachment(Multipart multipart, String filename) throws MessagingException {
        File attachedFile = new File(filename);
        DataSource source = new FileDataSource(filename);
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(attachedFile.getName());
        multipart.addBodyPart(messageBodyPart);
    }

    //El enunciado pedía solo el argumeno destino y adjunto, sin embargo consideramos que
     //pasándoles todos los argumentos (asunto, mensaje) la librería está más desacoplada de la GUI
    public static int sendMail(String to, String subject, String messageText, String[] attachedFilePaths) throws Exception {
        final String username = FileIO.getSMTPProp("username");  //SMTP USER
        final String password = FileIO.getSMTPProp("password"); //SMTP PASSWORD


        //AJUSTES SMTP
        Properties prop = new Properties();
        prop.put("mail.smtp.host", FileIO.getSMTPProp("host"));
        prop.put("mail.smtp.port", FileIO.getSMTPProp("port"));
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", FileIO.getSMTPProp("TLS")); //TLS
        prop.put("mail.smtp.ssl.enable", FileIO.getSMTPProp("SSL")); //SSL
        prop.put("mail.smtp.connectiontimeout", 1000);   //Timeout en ms

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Creando objeto MimeMessage
            Message message = new MimeMessage(session);

            // Definir "desde:"
            message.setFrom(new InternetAddress(username));

            // Definir destinatario(s)
            InternetAddress[] emails = InternetAddress.parse(to);
            for (InternetAddress email : emails){
                email.validate();
            }
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Definir asunto
            message.setSubject(subject);

            // Crear el cuerpo del mensaje
            BodyPart messageBodyPart = new MimeBodyPart();

            // Asignar el mensaje al cuerpo
            messageBodyPart.setText(messageText);

            // Crear un mensaje multipartes (para adjuntos)
            Multipart multipart = new MimeMultipart();

            // Asignamos el cuerpo a la primera parte del mensaje
            multipart.addBodyPart(messageBodyPart);

            // Y después añadimos los adjuntos a la segunda parte
            if (attachedFilePaths != null){
                //ForEach item in attached files for multiple files
                for (String attachedFilePath : attachedFilePaths) {
                    addAttachment(multipart, attachedFilePath);
                }
                message.setContent(multipart);
            }  else {
                message.setText(messageText);
            }

            // Enviamos el mensaje
            Transport.send(message);

            System.out.println("Correo enviado con éxito");
            return 0; //Envío correcto libre de errores


            //Gestión de excepciones
        } catch (AddressException e){
            System.err.println("El correo electrónico no se pudo enviar! Revisa la dirección de destino");
            return 1;
        } catch (MailConnectException e){
            System.err.println("No se pudo establecer conexión con el servidor de correo electrónico. \nRevisa tu " +
                    "conectividad a internet y/o la configruación SMTP.");
            return 2; //No se envío el correo
        } catch (MessagingException e){
            System.err.println("Ha ocurrido un error inesperado! Revisa los ajustes SMTP");
            System.out.println(e.getClass());
            e.printStackTrace();
            return -999;
        }
    }

    public static int sendMail(String to, String subject, String message, String attachedFilePath) throws Exception {
        String[] attachedFilePaths = {attachedFilePath};
        return sendMail(to,subject,message,attachedFilePaths);
    }

    public static int sendMail(String to, String subject, String message) throws Exception {
        return sendMail(to,subject,message,(String[]) null);
    }



}
