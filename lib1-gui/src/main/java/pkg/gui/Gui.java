package pkg.gui;

//Importamos las clases de Swing que vayamos a usar en nuestra librería
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Gui {

    public static String[] loginUserPassword(String windowTitle){

        JFrame frame = new JFrame(windowTitle);
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);

        panel.setLayout(null);

        //<editor-fold desc="Elementos de 'panel' : ">
        JLabel userLabel = new JLabel("Usuario");
        userLabel.setBounds(10, 10, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 40, 160, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Acceder");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);
        //</editor-fold>

        frame.setVisible(true);


        //FIXME Devolvemos una array vacía para que no de error el IDE; Posteriormente en esta array devolveremos
        // usuario y contraseña insertados por el usuario.
        //TODO: Implementar hashing SHA, podemos guardar la contraseña en otro archivo o en el propio programa
        // ON BUTTONN (loginButton) click -> Check user and password, if is correct go to Email Sending Window
        // (2nd method)
        return new String[0];
    }

    public static boolean sendMailWindow(){

        //FIXME Devolvemos un booleano para que no de error el IDE
        return false;
    }

    public static String selectFile(){

        //FIXME Devolvemos una String vacía para que no de error el IDE
        return "";
    }



}
