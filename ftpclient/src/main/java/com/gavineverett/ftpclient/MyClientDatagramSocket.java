package com.gavineverett.ftpclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MyClientDatagramSocket extends DatagramSocket {
    static final int MAX_LEN = 100;
    MyClientDatagramSocket( ) throws SocketException{
        super( );
    }
    MyClientDatagramSocket(int portNo) throws SocketException {
        super(portNo);
    }
    public void sendMessage(InetAddress receiverHost,
                            int receiverPort,
                            String message)
            throws IOException {
        byte[ ] sendBuffer = message.getBytes( );

        DatagramPacket datagram =
                new DatagramPacket(sendBuffer, sendBuffer.length,
                        receiverHost, receiverPort);
        this.send(datagram);
    } // end sendMessage

    public String receiveMessage()
            throws IOException {
        byte[ ] receiveBuffer = new byte[MAX_LEN];
        DatagramPacket datagram =
                new DatagramPacket(receiveBuffer, MAX_LEN);
        this.receive(datagram);
        String message = new String(receiveBuffer);

        return message;
    } //end receiveMessage


    public void sendFile(InetAddress receiverHost,
                            int receiverPort,
                            File message)
            throws IOException {
        byte[ ] sendBuffer = message.getBytes( );

        //read the file sent to the server
        FileInputStream readInput = new FileInputStream(message);


        DatagramPacket datagram =
                new DatagramPacket(sendBuffer, sendBuffer.length,
                        receiverHost, receiverPort);
        this.send(datagram);
    } // end sendMessage




} //end class
