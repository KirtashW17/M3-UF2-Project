package pkg.sendMail;

public class SendMail {

    public static void enviaCorreu(String destination, String subject, String message, String[] attachedFiles){

    }

    public static void enviaCorreu(String destination, String subject, String message){
        enviaCorreu(destination,subject,message,null);
    }

}
