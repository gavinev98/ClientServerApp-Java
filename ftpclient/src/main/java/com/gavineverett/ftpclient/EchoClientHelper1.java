package com.gavineverett.ftpclient;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.swing.*;

public class EchoClientHelper1 {
    private MyClientDatagramSocket mySocket;
    private InetAddress serverHost;
    private int serverPort;

    private File file;
    private static boolean selectFile = true;
    private static boolean notComplete = false;

    private final static int MAX_PACKET_SIZE = 64;
    final static int theServerPort = 8888;
    EchoClientHelper1(String hostName, String portNum)
            throws SocketException, UnknownHostException, Exception {
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

    public String sendToServer(File file, String filename)
            throws SocketException, IOException {
        String receivedata = "";
        // retrieve the message inputted by the user e.g login.
        String filetosend = "300-" + file + "-" + filename;
        //Get the socket,
        //send normal message with request code.
        mySocket.sendMessage(serverHost, serverPort, filetosend);
        // Then send the file across the server.
        mySocket.sendFile(serverHost,serverPort,file);
        // recieve echo back
        receivedata = mySocket.receiveMessage();
        return receivedata;

    }

    public String sendDownloadRequest(String username, String filename)
            throws SocketException, IOException {
        String recievedata = "";
        //send request to server
        String downloadRequest = "500-" + username +"-"+ filename;
        //Get the socket
        mySocket.sendMessage(serverHost,serverPort, downloadRequest);
        //Retrieve the echo back from the server
        recievedata = mySocket.receiveMessage();
        return recievedata;

    }




        public File fileupload() {

            String receivedata = "";

            //creating a directory to store users
            File userDirectory = new File("C:\\Client");
            //check if directory does not exist.
            if (!userDirectory.exists()) {
                userDirectory.mkdir();
                String filetemp = "Upload" + ".txt";
                    File crreate = new File("C:\\Client", filetemp);
                    try {
                        crreate.createNewFile();

                        PrintWriter writer = new PrintWriter(crreate, "UTF-8");
                        writer.println("The first line");
                        writer.println("The second line");
                        writer.close();
                    } catch (IOException e) {
                    e.printStackTrace();
                }

            }
                /* https://stackoverflow.com/questions/8402889/working-with-jfilechooser-getting-access-to-the-selected-file */
                final JFrame frame = new JFrame("Client-Server");
                    JButton btnFile = new JButton("Upload a File");
                    while (!notComplete) {
                        frame.getContentPane().add(btnFile);
                frame.setSize(500, 500);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

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
                            if (file_size > MAX_PACKET_SIZE) {
                                System.out.println(" 308 - The file selected exceeds 64kbs! Please select another file.");
                            } else {
                                //This is where a real application would open the file.
                                System.out.println("File to be uploaded : " + file.getName() + ".");
                                notComplete = true;

                            }

                        }

                    }

                });



            }
            return file;
        }


    public void filedownload()
        {

        }


    public void done( ) throws SocketException {
        mySocket.close( );
    }  //end done


} //end class
