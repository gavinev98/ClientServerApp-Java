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

    public void done( ) throws SocketException {
        mySocket.close( );
    }  //end done

} //end class
