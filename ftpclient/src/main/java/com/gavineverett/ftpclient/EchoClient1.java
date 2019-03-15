package com.gavineverett.ftpclient;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.File;

import javax.swing.JFileChooser;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class EchoClient1 {
    String username ="";
    String option = "";
    static boolean userSession;
    static  String closeapp = "";
    static String fileupload = "F";
    static String filename = "";
    public static void main(String[] args) {


        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        try {
            System.out.println("Welcome to the client.\n" +
                    "What is the name of the server host?");
            String hostName = br.readLine();
            if (hostName.length() == 0) // if user did not enter a name
                hostName = "localhost";  //   use the default host name
            System.out.println("What is the port number of the server host?");
            String portNum = br.readLine();
            if (portNum.length() == 0)
                portNum = "7";          // default port number
            EchoClientHelper1 helper =
                    new EchoClientHelper1(hostName, portNum);
            boolean done = false;
            String message, echo;
            while (!done) {
                InputStreamReader convert = new InputStreamReader(System.in);
                BufferedReader reading = new BufferedReader(convert);

                System.out.println("\nHello and welcome to the system");


                    System.out.print("Please enter a username of your choice: ");
                    String userNameVal = reading.readLine();
                    System.out.println("Please enter your password");
                    String passwordVal =  reading.readLine();


                    String status = helper.login(userNameVal, passwordVal);

                    System.out.println(status);

                    //extracting username for logout
                    String [] credentials = status.split(":");
                    String repsonseCode = credentials[0];




                //checking the response code recieved from server.
                switch (repsonseCode)
                {
                    case "304":
                        userSession = true;
                        done =true;
                        break;

                    case "506":
                        userSession = false;
                        done =false;
                        break;

                    case "204":
                        userSession = true;
                        done =true;
                        break;
                }


                while(userSession){

                    //output returned message from server
                    System.out.print("---------- CLIENT APPLICATION SERVER -------------");

                    System.out.print("\nPlease Choose an option , F(FileUpload), D(FileDownload), L(Logout):");

                    //Read input for command line
                    InputStreamReader converter = new InputStreamReader(System.in);
                    BufferedReader in = new BufferedReader(converter);
                    closeapp = in.readLine();

                    switch (closeapp)
                    {
                        case "F":
                            //fileupload
                            //retrieve file from the upload
                            File file = helper.fileupload();
                            filename = file.getName();
                            System.out.println("Sending file to server(please wait) : " + filename);
                            //send the file to the server

                           String msg =  helper.sendToServer(file, filename);
                            //retrieve message back from server.
                            System.out.println(msg);

                            break;


                        case "D":
                            //download
                             break;


                        case "L":
                            String logoutMSG = helper.logout(userNameVal, passwordVal);
                            System.out.println(logoutMSG);
                            userSession = false;
                            break;

                    }

                }

                //bring user back to login screen.
                done = false;

                //close connection



            } // end while



        } // end try
        catch (Exception ex) {
            ex.printStackTrace( );
        } // end catch
    } //end main

} // end class
