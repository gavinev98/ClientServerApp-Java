package com.gavineverett.ftpclient;



import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.security.KeyStore;
import java.security.SecureRandom;


public class DTLSClient extends JFrame implements ActionListener {

    final static String trustStoreFile = "public.jks"; // The trust FileName
    final static String keyStoreFile = "herong.jks";
    final static String passwd = "gavin1234"; // The Password

    final static int theServerPort = 8888;


    EchoClientHelper1 helper;

    {
        try {
            helper = new EchoClientHelper1("localhost", "8888");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    JFrame frame;
    static JFrame frame2;
    JTextField field;
    JPasswordField field1;
    JTextArea txtArea;
    JLabel l;
    JPanel panel;
    public static String creds;
    static String helper1;

    JButton jbutton;
    JButton upload;
    JButton download;
    JButton logout;


    static boolean userSession;
    static String filename = "";
    static String closeapp = "";
    static String status = "";
    static String username = "";
    static String password = "";
    static String downloadOption = "";


    DTLSClient() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("Login");
        frame.setSize(500, 200);
        frame.getContentPane().setBackground(Color.pink);
        frame.setLocation(300, 200);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);


        l = new JLabel("Enter Username: ");
        l.setLocation(10, 10);
        l.setSize(l.getPreferredSize());
        frame.add(l);


        field = new JTextField();
        field.setColumns(15);
        field.setSize(field.getPreferredSize());

        field.setLocation(150, 10);
        field.setToolTipText("Enter Username: ");
        frame.add(field);

        l = new JLabel("Enter your password: ");
        l.setLocation(10, 40);
        l.setSize(l.getPreferredSize());
        frame.add(l);

        field1 = new JPasswordField();
        field1.setColumns(15);
        field1.setSize(field.getPreferredSize());

        field1.setLocation(150, 40);
        field1.setToolTipText("Enter a password");
        frame.add(field1);

        jbutton = new JButton("Log in");
        jbutton.setSize(jbutton.getPreferredSize());
        jbutton.setLocation(150, 80);
        frame.add(jbutton);
        frame.setVisible(true);

        JFrame.setDefaultLookAndFeelDecorated(true);
        frame2 = new JFrame("Client Server");
        frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame2.setSize(500, 420);
        frame2.setLocationRelativeTo(null);
        txtArea = new JTextArea("\n Welcome to the server! " + username, 5, 5);
        txtArea.setBounds(20,130,450,250);
        txtArea.setForeground(Color.CYAN);
        txtArea.setBackground(Color.BLACK);
        logout = new JButton("Logout");
        logout.setBounds(5,20,100,100);
        logout.addActionListener(this);
        download = new JButton("Download a file");
        download.setBounds(140,20,150,100);
        download.addActionListener(this);
        upload = new JButton("Upload a file");
        upload.setBounds(320,20,150,100);
        upload.addActionListener(this);
        panel = new JPanel();
        panel.add(logout);
        panel.add(txtArea);
        panel.add(download);
        panel.add(upload);
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(600, 800));


        frame2.getContentPane().add(panel);


        jbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                username = field.getText();
                password = field1.getText();

                try {
                    helper1 = helper.login(username, password);
                    JOptionPane.showMessageDialog(null, helper1);


                    userSession(helper1);


                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }


    //connection handshake for DTLS.
    void doClientSide() throws Exception {
        try {

            KeyStore ks = KeyStore.getInstance("JKS");
            KeyStore ts = KeyStore.getInstance("JKS");


            //convert password to char array.
            char[] passphrase = passwd.toCharArray();

            try (FileInputStream fis = new FileInputStream(trustStoreFile)) {
                ts.load(fis, passphrase);
            }

            try (FileInputStream fis = new FileInputStream(keyStoreFile)) {
                ks.load(fis, passphrase);
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, passphrase);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ts);

            SSLContext sslCtx = SSLContext.getInstance("DTLSv1.0", "SunJSSE");

            sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());

            SSLEngine engine = sslCtx.createSSLEngine("localhost", theServerPort);
            engine.setUseClientMode(true);

            //begin handshake when client connects to server
            engine.beginHandshake();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    //return the specific jframe depending on the outcome.
    public JFrame userSession(String logcode) {
        //extracting username for logout
        String[] credentials = logcode.split(":");
        String repsonseCode = credentials[0];

        //checking the response code recieved from server.
        switch (repsonseCode) {
            case "304":
                frame2.setVisible(true);
                frame.setVisible(false);
                // userSession = true;
                // done =true;
                break;

            case "506":
                frame2.setVisible(false);
                frame.setVisible(true);
                //userSession = false;

                // done =false;
                break;

            case "202":
                txtArea.setText("Welcome back: " + username);
                //display files that they have downloaded/uploaded
                File folder = new File("C:\\ServerSession\\" + username);
                //store files in an array.
                File[] listOfFiles = folder.listFiles();
                txtArea.append("\nFiles you  have downloaded: ");
                //loop over files in directory
                for (int i = 1; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        txtArea.append("\n" + listOfFiles[i].getName());
                    }
                    if(listOfFiles[i].length() == 0)
                    {
                        txtArea.append("No files currently downloaded");
                    }
                }

                frame2.setVisible(true);
                frame.setVisible(false);
                //userSession = true;

                //done =true;
                break;

        }
        return frame2;
    }





        public void actionPerformed (ActionEvent e){

            //accuire button clicked
            if (e.getSource() == logout) {
                closeapp = "L";
            }

            if (e.getSource() == upload) {
                closeapp = "F";
            }
            if (e.getSource() == download) {
                closeapp = "D";
            }


            switch (closeapp) {
                case "F":
                    //fileupload
                    //retrieve file from the upload
                    File file = helper.fileupload();
                    // retrieve name and extension from file.
                    filename = file.getName();

                    if (filename.length() == 0) {
                        JOptionPane.showMessageDialog(null, "No File Received!");
                    }
                    txtArea.append("\nSending file to server(please wait) : " + filename);
                    //send the file to the server
                    String msg = null;
                    try {
                        msg = helper.sendToServer(file, filename);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    //retrieve message back from server.
                    txtArea.append("\nResponse: " + msg);
                    break;


                case "D":
                    //download
                    File folder = new File("C:\\ServerSession\\" + "test\\");
                    File[] listOfFiles = folder.listFiles();
                    //setting value to blank as I am not sending anything in the request.
                    // loop over contents of directory
                    for (int i = 0; i < listOfFiles.length; i++) {
                        //ask user for the name of a file they wish to download , show a list of files in the temporary directory.
                        txtArea.append("-----\nHere is a list of files:---" + "\n" + listOfFiles[i].getName());
                        String downloadoption = JOptionPane.showInputDialog("Please enter the name of a file that you wish to download");

                        String fileChosen = downloadoption;

                        txtArea.append("\nThank you: " + fileChosen + " is now being processed");

                        //create a file in the client directory with the name the user has requested
                        File createFile = new File("C:\\ServerSession\\", username + "\\" + fileChosen);
                        if (!createFile.exists()) {
                            try {
                                createFile.createNewFile();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        //receive information back from the server which should include the content of the file.
                        String receiveMsg = null;
                        try {
                            receiveMsg = helper.sendDownloadRequest(username, fileChosen);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        txtArea.append("\nDownloading file...");
                        txtArea.append("\nFile successfully downloaded");
                        //get the data received from the server and write it to the file.

                        if (receiveMsg.length() > 0) {
                            PrintWriter writeToFile = null;
                            try {
                                writeToFile = new PrintWriter(createFile);
                            } catch (FileNotFoundException e1) {
                                e1.printStackTrace();
                            }
                            //witting to file.
                            writeToFile.write(receiveMsg);
                            writeToFile.close();
                        } else {
                            txtArea.append("\nNo data received");
                        }

                    }
                    break;


                case "L":
                    String logoutMSG = null;
                    try {
                        logoutMSG = helper.logout(username, password);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(null,logoutMSG,"logout",JOptionPane.INFORMATION_MESSAGE);
                    frame2.dispose();
                    txtArea.setText("Welcome: ");
                    frame.setVisible(true);
                    break;

            }

            //bring user back to login screen.
            // done = false;
        }




    public static void main(String args[])
    {

        System.setProperty("javax.net.ssl.TrustStore", keyStoreFile);
        System.setProperty("javax.net.ssl.TrustStorePassword", passwd);
        //uncomment to check for debug
        System.setProperty("javax.net.debug", "all");

        java.security.Security.setProperty("jdk.tls.disabledAlgorithms",
                "SSL, RC4, MD5withRSA, DH keySize < 768, EC keySize < 224");
       // if (debug)
        //    System.setProperty("javax.net.debug", "all");

        // java -Djavax.net.ssl.trustStore=/tmp/trustFilename -Djavax.net.ssl.trustStorePassword=123456 SSLClientExample
        try {
            new DTLSClient().doClientSide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new DTLSClient();



    }

}
