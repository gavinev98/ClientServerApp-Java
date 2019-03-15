package com.gavineverett.ftpclient;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.*;
import java.io.File;

public class EchoClientHelper1 {
    private MyClientDatagramSocket mySocket;
    private InetAddress serverHost;
    private int serverPort;

    private File file;

    private final static int MAX_PACKET_SIZE = 64;

    EchoClientHelper1(String hostName, String portNum)
            throws SocketException, UnknownHostException {
        this.serverHost = InetAddress.getByName(hostName);
        this.serverPort = Integer.parseInt(portNum);
        // instantiates a datagram socket for both sending
        // and receiving data
        this.mySocket = new MyClientDatagramSocket();
    }
        //testfdff
    public String getEcho( String message)
            throws SocketException, IOException {
        String echo = "";
        mySocket.sendMessage( serverHost, serverPort, message);
        // now receive the echo
        echo = mySocket.receiveMessage();
        return echo;
    } //end getEcho


    //create methods to recieve response from the server.

        public String login(String username, String password)
           throws SocketException, IOException {
            String receivedata = "";
            String checkinglog = "\nChecking Credentials.....";
            // retrieve the message inputted by the user e.g login.
            String userCommand = "200-" + username + "-" + password + "-" + checkinglog;
            //Get the socket,
            mySocket.sendMessage(serverHost, serverPort, userCommand);
            // recieve echo back
            receivedata = mySocket.receiveMessage();
            return receivedata;
        }


        public String logout(String username, String password)
           throws SocketException, IOException {
            String receivedata = "";
            // retrieve the message inputted by the user e.g login.
            String logoutCommand = "204-" + username + "-" + password;
            //Get the socket,
            mySocket.sendMessage(serverHost, serverPort, logoutCommand);
            // recieve echo back
            receivedata = mySocket.receiveMessage();
            return receivedata;

        }

        public File fileupload() {

            String receivedata = "";

            //creating a directory to store users
            File userDirectory = new File("C:\\ClientFiles");
            //check if directory does not exist.
            if (!userDirectory.exists()) {
                userDirectory.mkdir();
                String filetemp = "Upload" + ".txt";
                File crreate = new File("C:\\ClientFiles", filetemp);
                try {
                    crreate.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            /* https://stackoverflow.com/questions/8402889/working-with-jfilechooser-getting-access-to-the-selected-file */
            final JFrame frame = new JFrame("Client-Server");
            JButton btnFile = new JButton("Upload a File");
            btnFile.addActionListener(new ActionListener() {
                //Handle open button action.
                public void actionPerformed(ActionEvent e) {
                    final JFileChooser fc = new JFileChooser();
                    //set directory to the current directory.
                    fc.setCurrentDirectory(userDirectory);
                    int returnVal = fc.showOpenDialog(frame);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        file = fc.getSelectedFile();
                        //check the the file size.
                        long file_size = file.length();
                        //check if file size is greater than 64kbs.
                        if(file_size > MAX_PACKET_SIZE)
                        {
                            System.out.println("The file selected exceeds 64kbs! Please select another file.");
                        }
                        //This is where a real application would open the file.
                        System.out.println("File: " + file.getName() + ".");
                    } else {
                        System.out.println("Open command cancelled by user.");
                    }
                    System.out.println(returnVal);
                }
            });

            frame.getContentPane().add(btnFile);
            frame.setSize(500, 500);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);


            return file;
        }




    public void filedownload()
        {

        }


    public void done( ) throws SocketException {
        mySocket.close( );
    }  //end done




} //end class
