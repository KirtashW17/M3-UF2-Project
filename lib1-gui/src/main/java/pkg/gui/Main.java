package pkg.gui;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        final String loginTitle = "Iniciar Sesión";
        final String USER = "admin";
        final String PASSWORD = "admin";
        boolean loggedIn = false;
        String[] credentials;

        //String[] arxius;

        do{
            credentials = Gui.loginWindow(loginTitle);
            if (credentials[0].equals(USER) && credentials[1].equals(PASSWORD)){
                JOptionPane.showMessageDialog(null, "Las credenciales son correctas.\nHola " + credentials[0], "Acceso Concedido.", 1);
                System.out.println("Las credenciales son correctas.\nHola " + credentials[0]);
                loggedIn = true;
            } else {
                System.out.println("Acceso Denegado. Revisa las Credenciales.");
                JOptionPane.showMessageDialog(null, "Acceso Denegado. Revisa las Credenciales", "ERROR", 0);
            }
        } while(!loggedIn);

        Gui.sendMailWindow();




        /*
        arxius = Gui.selectFile();
        for (String arxiu:arxius) {
            System.out.println(arxiu);
        }
        */


    }

}
