package com.gavineverett.ftpclient;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import javax.swing.*;
import java.awt.*;
import java.io.*;
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

                    case "202":
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
                            // retrieve name and extension from file.
                            filename = file.getName();
                            System.out.println("Sending file to server(please wait) : " + filename);
                            //send the file to the server
                           String msg =  helper.sendToServer(file, filename);
                            //retrieve message back from server.
                            System.out.println(msg);
                            break;


                        case "D":
                            //download
                            File folder = new File("C:\\ServerSession\\" + "test\\");
                            File[] listOfFiles = folder.listFiles();
                            //setting value to blank as I am not sending anything in the request.
                            // loop over contents of directory
                            for (int i = 0; i < listOfFiles.length; i++) {
                                //ask user for the name of a file they wish to download , show a list of files in the temporary directory.
                                System.out.println("-----\nHere is a list of files:---" + "\n" +  listOfFiles[i].getName());
                                System.out.println("Please enter the name of a file that you wish to download");
                                //Read input for command line
                                InputStreamReader convertoption = new InputStreamReader(System.in);
                                BufferedReader in1 = new BufferedReader(convertoption);
                                String fileChosen = in1.readLine();
                                System.out.println("Thank you: " + fileChosen + " is now being processed");

                                //create a file in the client directory with the name the user has requested
                                File createFile = new File("C:\\Client\\",fileChosen);
                                if(!createFile.exists())
                                    createFile.createNewFile();
                                //receive information back from the server which should include the content of the file.
                                String receiveMsg = helper.sendDownloadRequest(userNameVal,fileChosen);
                                System.out.println("Downloading file...");
                                System.out.println("File successfully downloaded");
                                //get the data received from the server and write it to the file.

                                if(receiveMsg.length() > 0) {
                                    PrintWriter writeToFile = new PrintWriter(createFile);
                                    //witting to file.
                                    writeToFile.write(receiveMsg);
                                    writeToFile.close();
                                }
                                else
                                {
                                    System.out.println("No data received");
                                }

                            }
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
