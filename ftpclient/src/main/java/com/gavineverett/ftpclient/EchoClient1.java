package com.gavineverett.ftpclient;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EchoClient1 {
    String username ="";
    String option = "";
    static boolean userSession;
    static  String closeapp = "";
    static String fileupload = "F";
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
                InputStreamReader convert = new InputStreamReader(System.in);
                BufferedReader reading = new BufferedReader(convert);

                System.out.println("\nHello and welcome to the system");


                    System.out.print("Please enter a username of your choice: ");
                    String userNameVal = reading.readLine();
                    System.out.println("Please enter your password");
                    String passwordVal =  reading.readLine();

                    String status = helper.login(userNameVal, passwordVal);

                    System.out.println(status);


                done =false;



                userSession = true;

                while(userSession){

                    //output returned message from server
                    System.out.print("---------- CLIENT APPLICATION SERVER -------------");

                    System.out.print("\nPlease Choose an option , F(FileUpload), D(FileDownload), L(Logout)");

                    //Read input for command line
                    InputStreamReader converter = new InputStreamReader(System.in);
                    BufferedReader in = new BufferedReader(converter);
                    closeapp = in.readLine();

                    switch (closeapp)
                    {
                        case "F":
                            //fileupload
                            break;


                        case "D":
                            //download
                             break;


                        case "L":
                            userSession = false;
                            break;

                    }

                }

                //close connection



            } // end while



        } // end try
        catch (Exception ex) {
            ex.printStackTrace( );
        } // end catch
    } //end main
} // end class
