package pkg.gui;

//Importamos las clases de Swing que vayamos a usar en nuestra librería
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Gui {

    //static JFrame frame = new JFrame();

    public static String[] loginWindow(String windowTitle){

        //final String USER = "admin";
        //final char[] PASSWORD = {'a','d','m','i','n'};
        String[] credentialsInput = new String[2];
        credentialsInput[1] = "";

        /*Cremos un JFrame*/
        JFrame frame = new JFrame(windowTitle);
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        //frame.setTitle(windowTitle);

        //Centramos el JFrame en medio a la pantalla
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int frameX = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int frameY = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(frameX, frameY);

        //Creamos un panel dentro del JFrame
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);

        //<editor-fold desc="Sub-elementos de 'panel' : ">
        JLabel userLabel = new JLabel("Usuario");
        userLabel.setBounds(10, 10, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(15);
        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(15);
        passwordText.setBounds(100, 40, 160, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Acceder");
        loginButton.setBounds(100, 80, 100, 25);
        panel.add(loginButton);

        //Al pulsar enter en el campo passwordText
        Action next = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordText.requestFocus();
            }
        };

        //Acción de Inicio de Sesión
        Action login = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                synchronized (loginButton) {
                    loginButton.notify();
                }
                credentialsInput[0] = userText.getText().toLowerCase();
                for (char passchar:passwordText.getPassword()) {
                    credentialsInput[1] += passchar;
                }

                /*
                if (userText.getText().equals(USER) && Arrays.equals(PASSWORD, passwordText.getPassword())) {
                    userText.setText("");

                } else {
                    //frame.setVisible(false);
                }
                passwordText.setText("");
                */
            }
        };

        //Añadimos la acción de login a los 2 campos (al pulsar enter) y al botón.
        userText.addActionListener( next );
        passwordText.addActionListener( login );
        loginButton.addActionListener( login );

        //</editor-fold>

        frame.setVisible(true);

        synchronized(loginButton) {
            try {
                loginButton.wait();
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }
        }
        frame.dispose();
        return credentialsInput;


        //FIXME Devolvemos una array vacía para que no de error el IDE; Posteriormente en esta array devolveremos
        // usuario y contraseña insertados por el usuario.
        //TODO: Implementar hashing SHA, podemos guardar la contraseña en otro archivo o en el propio programa
        // ON BUTTONN (loginButton) click -> Check user and password, if is correct go to Email Sending Window
        // (2nd method)
    }

    public static boolean sendMailWindow() throws IOException {

        /*Cremos un JFrame*/
        JFrame frame = new JFrame("Enviar Correo Electrónico");
        frame.setSize(600, 560);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        //Centramos el JFrame en medio a la pantalla
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int frameX = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int frameY = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(frameX, frameY);

        //Creamos un panel dentro del JFrame
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);

        JLabel destinationLabel = new JLabel("Dirección Destinatario");
        destinationLabel.setBounds(25, 30, 160, 20);
        panel.add(destinationLabel);

        JTextField destinationText = new JTextField();
        destinationText.setBounds(25, 50, 300, 20);
        panel.add(destinationText);

        BufferedImage myPicture = ImageIO.read(new File("mail.png"));
        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(125,120, Image.SCALE_FAST)));
        picLabel.setBounds(375, 30, 125, 120);
        panel.add(picLabel);

        JLabel subjectLabel = new JLabel("Asunto");
        subjectLabel.setBounds(25, 80, 160, 20);
        panel.add(subjectLabel);

        JTextField subjectText = new JTextField();
        subjectText.setBounds(25,100,300,20);
        panel.add(subjectText);

        JLabel messageLabel = new JLabel("Cuerpo del Mensaje");
        messageLabel.setBounds(25,140,160,20);
        panel.add(messageLabel);

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel,1));
        messagePanel.setBounds(25,160,520,250);
        panel.add(messagePanel);

        JTextArea messageText = new JTextArea();
        messageText.setPreferredSize(new Dimension(500,500));

        messagePanel.add(messageText);
        /*messageText.setBorder();
        messageText.setLineWrap(true);*/
        JScrollPane scroll = new JScrollPane(messageText);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        messagePanel.add(scroll);

        JButton sendButton = new JButton("Enviar Correo");
        sendButton.setBounds(25,450,125,25);
        panel.add(sendButton);

        JLabel statusLabel = new JLabel();

        JLabel adjuntoLabel = new JLabel("Adjunto?");
        adjuntoLabel.setBounds(415,425, 70, 25);
        panel.add(adjuntoLabel);

        JRadioButton afYes = new JRadioButton("Sí");
        afYes.setBounds(400,450,45,25);
        JRadioButton afNo = new JRadioButton("No");
        afNo.setBounds(450,450,45,25);
        afNo.setSelected(true);
        ButtonGroup attachedFileBG = new ButtonGroup();
        attachedFileBG.add((afYes));
        attachedFileBG.add(afNo);
        panel.add(afYes);
        panel.add(afNo);

        JLabel attachedFileNames = new JLabel();
        attachedFileNames.setBounds(400,450,100,25);
        panel.add(attachedFileNames);




        DefaultListModel<String> demoList = new DefaultListModel<String>();
        //demoList.addElement("aa");
        //demoList.addElement("bb");
        JList<String> attachedList = new JList<String>(demoList);
        //attachedList.setBounds(220, 420, 175, 100);
        //panel.add(attachedList);
        //attachedList.setSelectedIndex(1);
        //System.out.println(attachedList.getSelectedValue());
        //demoList.removeElementAt(attachedList.getSelectedIndex());
        //attachedList.setBackground(null);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVisible(false);
        scrollPane.setViewportView(attachedList);
        scrollPane.setBounds(220, 420, 175, 100);
        panel.add(scrollPane);




        Action attachFile = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String[] attachedFilePaths = selectFile();
                File[] attachedFiles = new File[attachedFilePaths.length];
                int i = 0;

                for (String attachedFilePath:attachedFilePaths) {
                    attachedFiles[i] = new File(attachedFilePath);
                    i++;
                }

                //attachedFileNames.setText(attachedFile.getName());

                for (File attachedFile:attachedFiles) {
                    demoList.addElement(attachedFile.getName());
                }

                frame.revalidate();
                scrollPane.setVisible(true);
            }
        };

        Action removeAttached = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                attachedFileNames.setText("");

                demoList.removeAllElements();
                scrollPane.setVisible(false);

                frame.revalidate();
            }
        };

        afYes.addActionListener(attachFile);
        afNo.addActionListener(removeAttached);






        frame.setVisible(true);



        //FIXME Devolvemos un booleano para que no de error el IDE
        return false;
    }

    public static String[] selectFile(){
        String[] selectedFilesPaths;
        new File("~");

        JFileChooser fitxer = new JFileChooser();
        fitxer.setMultiSelectionEnabled(true);
        fitxer.showOpenDialog(null);

        File[] arxiusSeleccionats = fitxer.getSelectedFiles();

        selectedFilesPaths = new String[arxiusSeleccionats.length];

        for(int i = 0; i<arxiusSeleccionats.length; i++){
            selectedFilesPaths[i] = arxiusSeleccionats[i].getAbsolutePath();
        }

        return selectedFilesPaths;
    }

    /*Select just 1 file
    public static String selectFile(){
        JFileChooser fitxer = new JFileChooser();
        fitxer.showOpenDialog(null);

        File arxiuSeleccionat = fitxer.getSelectedFile();

        return arxiuSeleccionat.getAbsolutePath();
    }*/



}
