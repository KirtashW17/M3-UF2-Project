package pkg.main;


import pkg.gui.Gui;
import javax.swing.*;
import pkg.FileIO.FileIO;



public class Main {

    public static void main(String[] args) throws Exception {

        final String loginTitle = "Iniciar Sesión";
        final String mailWindowTitle = "Enviar Correo Elecrónico";
        String IMAGEPATH = "mail.png";
        String[] credentials;
        boolean loggedIn = false;
        boolean isAdmin = false;
        boolean mailSent;

        if(!FileIO.fileExists("users.json")){
            FileIO.writeUsersFile(); //Write Basic Users File
            //ADMIN CREDENTIALS:  -- Admin can access to settings
                //user: admin
                //password: admin
            //USER CREDENTIALS:
                //user: user
                //password: user
        }
        if (!FileIO.fileExists("smtpConfig.json")){
            FileIO.writeSMTPConfiguration(); //Write Default SMTP Configuration
        }

        /*DEMO: Podemos modificar la ruta de los archivos de configuración y de usuario
        FileIO.setSmtpFilePath("smtpConfig2.json");
        FileIO.writeSMTPConfiguration();
        FileIO.writeSMTPProp("host","example.com");
        FileIO.setUserFilePath("users2.json");
        FileIO.writeUsersFile();
        FileIO.addUser("user2","user2");
        FileIO.deleteUser("user");
         */


        do{
            credentials = Gui.loginWindow(loginTitle);
            if (FileIO.authenticateUser(credentials[0],credentials[1])){
                JOptionPane.showMessageDialog(null, "Las credenciales son correctas.\nHola " + credentials[0], "Acceso Concedido.", 1);
                System.out.println("Las credenciales son correctas.\nHola " + credentials[0]);
                loggedIn = true;
                if (credentials[0].toLowerCase().equals("admin")){
                    isAdmin = true;
                }
            } else {
                System.out.println("Acceso Denegado. Revisa las Credenciales.");
                JOptionPane.showMessageDialog(null, "Acceso Denegado. Revisa las Credenciales", "ERROR", 0);
            }
        } while(!loggedIn);

        mailSent = Gui.sendMailWindow("Enviar Correo Electrónico", IMAGEPATH, isAdmin);

        System.out.println("sendMailWindow() ha retornado " + mailSent);






        /*
        arxius = Gui.selectFile();
        for (String arxiu:arxius) {
            System.out.println(arxiu);
        }
        */


    }

}