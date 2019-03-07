package com.gavineverett.ftpclient;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EchoClient1 {
    String username ="";
    String option = "";
    boolean userSession;
    static final String endMessage = ".";
    public static void main(String[] args) {
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        try {
            System.out.println("Welcome to the Echo client.\n" +
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
                System.out.println("Hello and welcome to the system");

                        System.out.print("Please enter a username of your choice: ");
                        InputStreamReader convert = new InputStreamReader(System.in);
                        BufferedReader reading = new BufferedReader(convert);

                        String usersValue = reading.readLine();

                        helper.login(usersValue);


            } // end while


        } // end try
        catch (Exception ex) {
            ex.printStackTrace( );
        } // end catch
    } //end main
} // end class
