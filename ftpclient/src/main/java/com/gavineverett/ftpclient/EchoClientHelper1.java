package com.gavineverett.ftpclient;


import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class EchoClientHelper1 {
    private MyClientDatagramSocket mySocket;
    private InetAddress serverHost;
    private int serverPort;



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

        public void filupload()
        {

        }

        public void filedownload()
        {

        }


    public void done( ) throws SocketException {
        mySocket.close( );
    }  //end done





} //end class
