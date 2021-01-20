package pkg.gui;

//Importamos las clases de Swing que vayamos a usar en nuestra librería

import pkg.FileIO.FileIO;
import pkg.sendMail.SendMail;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Gui {

    //Nombre CAT enunciado: loginUsuariPassword
    public static String[] loginWindow(String windowTitle) {
        String[] credentialsInput = new String[2];
        credentialsInput[1] = ""; //Inicializamos la contraseña para poder concatenar chars

        /*Creamos un JFrame*/
        JFrame frame = new JFrame(windowTitle);
        frame.setSize(300, 175);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        centerFrame(frame);

        //Creamos un panel dentro del JFrame
        JPanel panel = getPanel(frame);

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

        JCheckBox showPassword = new JCheckBox("Mostrar Contraseña");
        showPassword.setBounds(100, 70, 160, 25);
        panel.add(showPassword);

        JButton loginButton = new JButton("Acceder");
        loginButton.setBounds(100, 110, 100, 25);
        panel.add(loginButton);

        //</editor-fold>


        //<editor-fold desc="Creando y asignando Actions:">
        //Al pulsar enter en el campo passwordText
        Action next = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordText.requestFocus();
            }
        };

        //Al pulsar sobre mostrar contraseña
        Action togglePassword = togglePasswordAction(passwordText, showPassword);

        //Acción de Inicio de Sesión
        Action login = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (loginButton) {
                    loginButton.notify();
                }
                credentialsInput[0] = userText.getText().toLowerCase();
                for (char passchar : passwordText.getPassword()) {
                    credentialsInput[1] += passchar;
                }
            }
        };

        //Añadimos la acción de login a los 2 campos (al pulsar enter), al botón y al .
        userText.addActionListener(next);
        passwordText.addActionListener(login);
        loginButton.addActionListener(login);
        showPassword.addActionListener(togglePassword);
        //</editor-fold>

        frame.setVisible(true);

        synchronized (loginButton) {
            try {
                loginButton.wait(); //espera a loginButton.notify()
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }
        }

        //Este bloque de código de ejecuta tras pulsar el botón de Login y ejecutarse su ActionListener
        frame.dispose();
        return credentialsInput;
    }

    //Nombre CAT enunciado: finestraEnviaCorreu
    public static boolean sendMailWindow(String windowTitle, String imagePath, boolean adminUser) {
        String[] mailInput = new String[4];
        ArrayList<String> attachedFilePaths = new ArrayList<>();
        final int[] sendStatus = {-1}; //Es final y un vector para que se pueda acceder desde los Acion
        final String selectFilesTitle = "Selecconar Adjunto(s)";
        final String selectFilesPath = "~";
        //-1: Nada se ha intentadoe enviar
        //0: Envío exitoso
        //1: Error de dirección
        //2: Error de conectividad o configuración SMTP
        //-999: Error desconocido (no atrapado por la librería)

        /*Cremos un JFrame*/
        JFrame frame = new JFrame(windowTitle);
        frame.setSize(600, 560);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        centerFrame(frame);

        //Creamos un panel dentro del JFrame
        JPanel panel = getPanel(frame);

        //<editor-fold desc="Sub-elementos de 'panel' : ">

        //Si el usuario es admin se mostrará el botón de ajustes
        if (adminUser) {
            JButton settingsButton = new JButton("Ajustes");
            settingsButton.setBounds(520, 0, 80, 25);
            panel.add(settingsButton);

            Action openSettingsWindows = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        settingsWindow(frame);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            };
            settingsButton.addActionListener(openSettingsWindows);
        }

        //Destination
        JLabel destinationLabel = new JLabel("Dirección Destinatario/s:");
        destinationLabel.setBounds(25, 30, 160, 20);
        panel.add(destinationLabel);
        JTextField destinationText = new JTextField();
        destinationText.setToolTipText("Se pueden introducir múltiples direcciones separadas por comas");
        destinationText.setBounds(25, 50, 300, 20);
        panel.add(destinationText);

        //Subject
        JLabel subjectLabel = new JLabel("Asunto:");
        subjectLabel.setBounds(25, 80, 160, 20);
        panel.add(subjectLabel);
        JTextField subjectText = new JTextField();
        subjectText.setBounds(25, 100, 300, 20);
        panel.add(subjectText);

        //Message
        JLabel messageLabel = new JLabel("Cuerpo del Mensaje:");
        messageLabel.setBounds(25, 140, 160, 20);
        panel.add(messageLabel);
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBounds(25, 160, 520, 250);
        panel.add(messagePanel);
        JTextArea messageText = new JTextArea();
        messageText.setPreferredSize(new Dimension(500, 500));
        messagePanel.add(messageText);
        JScrollPane scroll = new JScrollPane(messageText);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        messagePanel.add(scroll);

        //SendButton
        JButton sendButton = new JButton("Enviar Correo");
        sendButton.setBounds(450, 500, 125, 25);
        panel.add(sendButton);

        //ExitButton
        JButton exitButton = new JButton("Salir");
        exitButton.setBounds(25, 500, 125, 25);
        panel.add(exitButton);

        //Adjunto (Label y Radio Buttons)
        JLabel attachedLabel = new JLabel("Adjunto?");
        attachedLabel.setBounds(65, 415, 70, 25);
        JRadioButton afYes = new JRadioButton("Sí");
        afYes.setBounds(50, 440, 50, 25);
        JRadioButton afNo = new JRadioButton("No");
        afNo.setBounds(100, 440, 50, 25);
        afNo.setSelected(true);
        ButtonGroup attachedFileBG = new ButtonGroup();
        attachedFileBG.add((afYes));
        attachedFileBG.add(afNo);
        panel.add(attachedLabel);
        panel.add(afYes);
        panel.add(afNo);

        //Lista de archivos adjuntos
        DefaultListModel<String> fileListModel = new DefaultListModel<>();
        JList<String> attachedList = new JList<>(fileListModel);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVisible(false);
        scrollPane.setViewportView(attachedList);
        scrollPane.setBounds(220, 420, 175, 100);
        panel.add(scrollPane);

        //Botones de añadir y remover adjuntos
        JButton addAttachmentsButton = new JButton("Añadir");
        JButton removeAttachmentButton = new JButton("Remover");
        addAttachmentsButton.setBounds(450, 430, 125, 25);
        removeAttachmentButton.setBounds(450, 460, 125, 25);
        addAttachmentsButton.setVisible(false);
        removeAttachmentButton.setVisible(false);
        panel.add(addAttachmentsButton);
        panel.add(removeAttachmentButton);

        //Imagen
        BufferedImage myPicture;
        if (imagePath != null) {
            //Try/catch para manejar excepciones sin interrumpir el programa (en caso de que no se encuentre la imagen)
            try {
                myPicture = ImageIO.read(new File(imagePath));
                JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(125, 120, Image.SCALE_FAST)));
                picLabel.setBounds(375, 30, 125, 120);
                panel.add(picLabel);
            } catch (IOException e) {
                System.out.println("No se encontró la imagen.");
            }
        }
        //</editor-fold>


        //<editor-fold desc="Creando y asignando Actions:">

        //Adjuntar Archivo
        Action attachFiles = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (fileListModel.getSize() == 0) {
                    String[] filePaths = selectFiles(selectFilesTitle, selectFilesPath);
                    attachedFilePaths.addAll(Arrays.asList(filePaths));
                    if (attachedFilePaths.size() > 0) {

                        File[] attachedFiles = new File[attachedFilePaths.size()];
                        int i = 0;

                        for (String attachedFilePath : attachedFilePaths) {
                            attachedFiles[i] = new File(attachedFilePath);
                            fileListModel.addElement(attachedFiles[i].getName());
                            i++;
                        }

                        scrollPane.setVisible(true);
                        addAttachmentsButton.setVisible(true);
                        removeAttachmentButton.setVisible(true);
                    } else {
                        afNo.setSelected(true);
                    }
                }
            }
        };

        //Remover Adjuntos (todos)
        Action removeAllFiles = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileListModel.removeAllElements();
                attachedFilePaths.clear();
                scrollPane.setVisible(false);
                addAttachmentsButton.setVisible(false);
                removeAttachmentButton.setVisible(false);

                frame.revalidate();
            }
        };

        //Remover (un) adjunto seleccionado
        Action removeFile = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = attachedList.getSelectedIndex();
                fileListModel.removeElementAt(index);
                attachedFilePaths.remove(index);
                if (attachedFilePaths.size() == 0){
                    afNo.doClick();
                }
            }
        };

        //Añadir adjuntos a los ya existentes
        Action addFiles = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] newFilePaths = selectFiles(selectFilesTitle, selectFilesPath);

                    File[] attachedFiles = new File[newFilePaths.length];
                    int i = 0;

                    for (String attachedFilePath : newFilePaths) {
                        if (!attachedFilePaths.contains(attachedFilePath)) {
                            attachedFiles[i] = new File(attachedFilePath);
                            fileListModel.addElement(attachedFiles[i].getName());
                            attachedFilePaths.add(attachedFilePath);
                        }
                        i++;
                    }

                    scrollPane.setVisible(true);
                    addAttachmentsButton.setVisible(true);
                    removeAttachmentButton.setVisible(true);

            }
        };


        afYes.addActionListener(attachFiles);
        afNo.addActionListener(removeAllFiles);
        addAttachmentsButton.addActionListener(addFiles);
        removeAttachmentButton.addActionListener(removeFile);
        frame.setVisible(true);
        destinationText.requestFocus();

        //Acción de Enviar Correo Electrónico
        Action sendEmail = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (destinationText.getText().equals("")){
                JOptionPane.showMessageDialog(frame, "Inserte al menos una dirección de destino", "Advertencia", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String[] arrayFilePaths = new String[0];

                    mailInput[0] = destinationText.getText().toLowerCase();
                    mailInput[1] = subjectText.getText();
                    mailInput[2] = messageText.getText();

                    if (attachedFilePaths.size() == 0)
                        arrayFilePaths = null;
                    else
                        arrayFilePaths = attachedFilePaths.toArray(arrayFilePaths);

                    try {
                        sendStatus[0] = SendMail.sendMail(mailInput[0], mailInput[1], mailInput[2], arrayFilePaths);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    switch (sendStatus[0]) {
                        case 0:
                            JOptionPane.showMessageDialog(frame, "El correo electrónico se envío con éxito!", "Envío Exitoso", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        case 1:
                            JOptionPane.showMessageDialog(frame, "No se pudo enviar el correo electrónico, revisa la dirección de destino.\n(ERR 1)", "Envío Fallido (ERR 1)", JOptionPane.ERROR_MESSAGE);
                            break;
                        case 2:
                            JOptionPane.showMessageDialog(frame, "No se pudo enviar el correo electrónico, revisa la conectividad a internet o la configuración SMTP.\n(ERR 2)", "Envío Fallido (ERR 2)", JOptionPane.ERROR_MESSAGE);
                            break;
                        case -999:
                        default:
                            JOptionPane.showMessageDialog(frame, "No se pudo enviar el correo electrónico por motivos desconocidos, revisa la consola para más información.\n(ERR -999)", "Envío Fallido (ERR -999)", JOptionPane.ERROR_MESSAGE);
                            break;
                    }
                }
            }
        };

        sendButton.addActionListener(sendEmail);

        Action closeApplication = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeMailWindow(exitButton, sendStatus, frame);
            }
        };

        exitButton.addActionListener(closeApplication);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                closeMailWindow(exitButton, sendStatus, frame);
            }
        });

        //</editor-fold>


        synchronized (exitButton) {
            try {
                exitButton.wait();
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }
        }

        //Bloque final, se ejecuta al cerrar la ventana de envío de correos electrónicos
        switch (sendStatus[0]){
            case 0:
                System.out.println("El correo electrónico se envió con éxito");
                return true;
            case -1:
                System.out.println("No ha sido enviado ningún correo electrónico");
                break;
            default:
                System.out.println("El correo electrónico no ha sido enviado debido a un error.");
                break;
        }
        return false;
    }

    //Nombre CAT enunciado: finestraEnviaCorreu - 2 args
    public static boolean sendMailWindow(String windowTitle, String imagePath) {
        return sendMailWindow(windowTitle, imagePath, false);
    }

    //Nombre CAT enunciado: finestraEnviaCorreu - 1 arg
    public static boolean sendMailWindow(String windowTitle) {
        return sendMailWindow(windowTitle, null);
    }

    //Nombre CAT enunciado: escullFitxer
    // Seleccionar múltiples archivos (devuelve una array de strings que representan la ruta absoluta de cada archivo
    public static String[] selectFiles(String windowTitle, String defaultPath) {
        String[] selectedFilesPaths;
        new File("~");
        int state;

        JFileChooser fileChooser = new JFileChooser(defaultPath);
        fileChooser.setDialogTitle(windowTitle);
        fileChooser.setMultiSelectionEnabled(true);
        state = fileChooser.showOpenDialog(null);


        if (state == JFileChooser.APPROVE_OPTION) {
            File[] arxiusSeleccionats = fileChooser.getSelectedFiles();

            selectedFilesPaths = new String[arxiusSeleccionats.length];

            for (int i = 0; i < arxiusSeleccionats.length; i++) {
                selectedFilesPaths[i] = arxiusSeleccionats[i].getAbsolutePath();
            }

            return selectedFilesPaths;
        }
        return new String[0];
    }

    //EXTRA METHOD (ettingWindow)
    public static void settingsWindow(JFrame parent) throws Exception {
        JDialog settingsDialog = new JDialog(parent);
        JTabbedPane tabbedPane = new JTabbedPane();

        JComponent smtpPanel = new JPanel();
        smtpPanel.setLayout(null);

        //<editor-fold desc="Hijos de smtpPanel: ">
        //Host
        JLabel hostLabel = new JLabel("Host:");
        hostLabel.setBounds(10, 10, 40, 20);
        smtpPanel.add(hostLabel);
        JTextField hostInput = new JTextField();
        hostInput.setText(FileIO.getSMTPProp("host"));
        hostInput.setBounds(50, 10, 150, 20);
        smtpPanel.add(hostInput);

        //Cifrado
        JLabel encrLabel = new JLabel("Cifrado:");
        encrLabel.setBounds(10, 40, 60, 20);
        smtpPanel.add(encrLabel);
        JRadioButton noEncrRB = new JRadioButton("Ninguno");
        noEncrRB.setBounds(65, 40, 80, 20);
        JRadioButton sslRB = new JRadioButton("SSL");
        sslRB.setBounds(145, 40, 50, 20);
        JRadioButton tlsRB = new JRadioButton("TLS");
        tlsRB.setBounds(195, 40, 50, 20);
        ButtonGroup encrBG = new ButtonGroup();
        encrBG.add(noEncrRB);
        encrBG.add(sslRB);
        encrBG.add(tlsRB);
        smtpPanel.add(noEncrRB);
        smtpPanel.add(sslRB);
        smtpPanel.add(tlsRB);
        if (FileIO.getSMTPProp("TLS").equals("true")) {
            tlsRB.setSelected(true);
        } else if (FileIO.getSMTPProp("SSL").equals("true")){
            sslRB.setSelected(true);
        }

        //Puerto
        JLabel portLabel = new JLabel("Puerto:");
        portLabel.setBounds(10, 70, 50, 20);
        smtpPanel.add(portLabel);
        JTextField portInput = new JTextField();
        portInput.setBounds(60, 70, 40, 20);
        smtpPanel.add(portInput);
        portInput.setText(FileIO.getSMTPProp("port"));

        //Nombre de Usuario
        JLabel usernameLabel = new JLabel("Usuario:");
        usernameLabel.setBounds(10, 115, 60, 20);
        smtpPanel.add(usernameLabel);
        JTextField usernameInput = new JTextField();
        usernameInput.setBounds(85, 115, 175, 20);
        smtpPanel.add(usernameInput);
        usernameInput.setText(FileIO.getSMTPProp("username"));

        //Contraseña
        JLabel pswdLabel = new JLabel("Contraseña:");
        pswdLabel.setBounds(10, 145, 75, 20);
        smtpPanel.add(pswdLabel);
        JPasswordField pswdInput = new JPasswordField();
        pswdInput.setBounds(85, 145, 135, 20);
        smtpPanel.add(pswdInput);
        JCheckBox showPswd = new JCheckBox("Mostrar Contraseña");
        showPswd.setBounds(65, 170, 170, 20);
        smtpPanel.add(showPswd);

        //Botón guardar y salir
        JButton saveAndExitBTN = new JButton("Guardar y salir");
        saveAndExitBTN.setBounds(120,210, 140,25);
        smtpPanel.add(saveAndExitBTN);

        //Botón salir
        JButton exitBTN = new JButton("Salir");
        exitBTN.setBounds(50,210, 65,25);
        smtpPanel.add(exitBTN);
        //</editor-fold>

        //<editor-fold desc="Actions smtpPanel and Common: ">
        Action togglePassword = togglePasswordAction(pswdInput, showPswd);
        showPswd.addActionListener(togglePassword);

        Action saveAndExit = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host, port, encr, username;
                StringBuilder password = new StringBuilder();
                char[] pswdChars;
                host = hostInput.getText();
                port = portInput.getText();
                username = usernameInput.getText();

                if (sslRB.isSelected()){
                    encr = "SSL";
                } else if (tlsRB.isSelected()){
                    encr = "TLS";
                } else {
                    encr = "none";
                }

                pswdChars = pswdInput.getPassword();
                if (pswdChars.length == 0) {
                    try {
                        password = new StringBuilder(FileIO.getSMTPProp("password"));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                } else {
                    for (char pswdChar : pswdChars) {
                        password.append(pswdChar);
                    }
                }
                    try {
                        FileIO.writeSMTPConfiguration(host, port, encr, username, password.toString());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                settingsDialog.dispose();
            }
        };
        saveAndExitBTN.addActionListener(saveAndExit);

        Action setSSL = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                portInput.setText("465");
            }
        };
        sslRB.addActionListener(setSSL);

        Action setTLS = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                portInput.setText("587");
            }
        };
        tlsRB.addActionListener(setTLS);

        Action setNone = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                portInput.setText("25");
            }
        };
        noEncrRB.addActionListener(setNone);


        Action exit = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingsDialog.dispose();
            }
        };

        exitBTN.addActionListener(exit);
        //</editor-fold>

        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(null);

        //<editor-fold desc="Hijos de usersPanel">

        //UserLabel y Lista de USuarios
        JLabel usersLabel = new JLabel("Usuarios: ");
        usersLabel.setBounds(45, 10, 65, 20);
        usersPanel.add(usersLabel);

        DefaultListModel<String> fileListModel = new DefaultListModel<>();
        for(String username : FileIO.getUserList()){
            fileListModel.addElement(username);
        }
        JList<String> userList = new JList<>(fileListModel);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(userList);
        scrollPane.setBounds(10, 40, 130, 100);
        usersPanel.add(scrollPane);

        //Cambiar Contraseña (Label, Input y Button)
        JLabel newPasswordLabel = new JLabel("Nueva Contraseña:");
        newPasswordLabel.setBounds(150, 30, 140, 20);
        usersPanel.add(newPasswordLabel);
        JPasswordField newPasswordInput = new JPasswordField();
        newPasswordInput.setBounds(150, 50, 160, 20);
        usersPanel.add(newPasswordInput);
        JButton changePswdBTN = new JButton("Cambiar Contraseña");
        changePswdBTN.setBounds(150, 80, 160, 20);
        usersPanel.add(changePswdBTN);

        //Botón Eliminar Usuario
        JButton delUserBTN = new JButton("Eliminar Usuario");
        delUserBTN.setBounds(150, 110, 160, 20);
        usersPanel.add(delUserBTN);

        //Separador
        JSeparator separator = new JSeparator();
        separator.setBounds(0, 150, 325, 1);
        usersPanel.add(separator);

        //Nuevo usuario - Nombre
        JLabel newUsernameLabel = new JLabel("Nombre de Usuario:");
        newUsernameLabel.setBounds(12, 160, 135, 20);
        usersPanel.add(newUsernameLabel);
        JTextField newUsernameInput = new JTextField();
        newUsernameInput.setBounds(10,185, 135, 20);
        usersPanel.add(newUsernameInput);

        //Nuevo usuario - Contraseña
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setBounds(180, 160, 90, 20);
        usersPanel.add(passwordLabel);
        JPasswordField passwordInput = new JPasswordField();
        passwordInput.setBounds(155,185, 140, 20);
        usersPanel.add(passwordInput);

        //Botones Añadir Usuario y Salir
        JButton addUserBTN = new JButton("Añadir Usuario");
        addUserBTN.setBounds(120,210, 140,25);
        usersPanel.add(addUserBTN);
        JButton exitBTN2 = new JButton("Salir");
        exitBTN2.setBounds(50,210, 65,25);
        exitBTN2.addActionListener(exit);
        usersPanel.add(exitBTN2);
        //</editor-fold>

        //<editor-fold desc="Actions de userPanel">
        Action createUser = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username =  newUsernameInput.getText();
                if (!username.equals("")) {
                    StringBuilder password = new StringBuilder();
                    char[] pswdChars = passwordInput.getPassword();
                    for (char pswdChar : pswdChars) {
                        password.append(pswdChar);
                    }
                    try {
                        boolean result = FileIO.addUser(username, password.toString());
                        if (result)
                            fileListModel.addElement(newUsernameInput.getText());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        };
        addUserBTN.addActionListener(createUser);

        Action removeUser = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userList.getSelectedValue() != null) {
                    if (userList.getSelectedValue().equals("admin")) {
                        JOptionPane.showMessageDialog(settingsDialog, "No se puede remover el usuario administrador.", "Advertencia", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        try {
                            FileIO.deleteUser(userList.getSelectedValue());
                            fileListModel.removeElement(userList.getSelectedValue());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
        };
        delUserBTN.addActionListener(removeUser);

        Action changePassword = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userList.getSelectedValue() != null) {
                    StringBuilder password = new StringBuilder();
                    char[] pswdChars = newPasswordInput.getPassword();
                    for (char pswdChar : pswdChars) {
                        password.append(pswdChar);
                    }
                    try {
                        FileIO.changePassword(userList.getSelectedValue(), password.toString());
                        newPasswordInput.setText("");
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        };
        changePswdBTN.addActionListener(changePassword);

        //</editor-fold>

        tabbedPane.addTab("SMTP", null, smtpPanel,
                "Configuración SMTP");

        tabbedPane.addTab("Usuarios", null, usersPanel,
                "Crear, Modificar y Eliminar Usuarios");
        settingsDialog.setSize(325, 305);
        settingsDialog.setResizable(false);
        settingsDialog.setLocationRelativeTo(parent);

        settingsDialog.add(tabbedPane);
        settingsDialog.setTitle("Configuración");
        settingsDialog.setVisible(true);



    }

    //Private methods created during refactoring (extracting common code)
    private static void centerFrame(JFrame frame) {
        //Método creado durante un refactoring (código repetido)

        frame.setResizable(false);

        //Centramos el JFrame en medio a la pantalla
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int frameX = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int frameY = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(frameX, frameY);
    }

    private static JPanel getPanel(JFrame frame) {
        //Método creado durante un refactoring (código repetido)
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);
        return panel;
    }

    private static void closeMailWindow(JButton exitButton, int[] sendStatus, JFrame frame) {
        //Método creado durante un refactoring (código repetido)
        synchronized (exitButton) {
            if (sendStatus[0] != 0){
                if (JOptionPane.showConfirmDialog(frame,
                        "El correo electrónico no ha sido enviado, estás segur@ que deseas salir?", "Cerrar Aplicación?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    exitButton.notify();
                    frame.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Adiós!", "Adiós", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                exitButton.notify();
            }
        }
    }

    private static Action togglePasswordAction(JPasswordField pswdInput, JCheckBox showPswd) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPswd.isSelected()) {
                    pswdInput.setEchoChar((char) 0);
                } else {
                    pswdInput.setEchoChar('*');
                }
            }
        };
    }

    /* OLD: Seleccionar solamente 1 archivo.
    public static String selectFile() {
        JFileChooser fitxer = new JFileChooser();
        fitxer.showOpenDialog(null);

        File arxiuSeleccionat = fitxer.getSelectedFile();

        if (arxiuSeleccionat == null) {
            return "";
        }

        return arxiuSeleccionat.getAbsolutePath();
    }*/

}
