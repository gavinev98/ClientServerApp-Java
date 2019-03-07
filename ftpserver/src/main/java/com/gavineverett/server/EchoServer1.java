package com.gavineverett.server;

import java.io.File;
import java.io.IOException;

public class EchoServer1 {
    //defining codes
    public static final String login = "200";
    public static final String logout = "204";
    public static final String fileUpload = "300";
    public static final String fileDownload = "500";
    static File userDirectory;

    public static void main(String[] args) {

        //creating a directory to store users
        userDirectory = new File("C:\\ServerSession");
        //check if directory does not exist.
        if (!userDirectory.exists()) {
            userDirectory.mkdir();
        }


        int serverPort = 7;    // default port
        if (args.length == 1)
            serverPort = Integer.parseInt(args[0]);
        try {
            // instantiates a datagram socket for both sending
            // and receiving data
            MyServerDatagramSocket mySocket = new MyServerDatagramSocket(serverPort);
            System.out.println("Echo server ready.");
            while (true) {  // forever loop
                DatagramMessage request =
                        mySocket.receiveMessageAndSender();
                System.out.println("Request received");
                String message = request.getMessage();
                System.out.println("message received: " + message);

                //when the message is retrieved the client will send back a code followed by the request of login.
                String code = message.substring(0, 3);

                //create a switch statement to decipher between the codes chosen.
                switch (code) {
                    case login:
                        //logincode
                        String recieveMessage = request.getMessage();
                        String extractUserName = recieveMessage.substring(14, 19);
                        //check for users validity.
                        String authCheck = performLoginOperation(extractUserName);
                        //send back relevant information back to the client.
                        mySocket.sendMessage(request.getAddress(),
                                request.getPort(), authCheck);
                        break;

                    case logout:
                        //logoutcode
                        break;

                    case fileUpload:
                        //fileuploadcode
                        break;

                    case fileDownload:
                        //filedownload
                        break;


                }

            } //end while
        } // end try
        catch (Exception ex) {
            ex.printStackTrace();
        } // end catch
    } //end main


    //methods to perform actions.

    public static String performLoginOperation(String username) {
        try {
            //check for txt file
            final String usertxt = username;
            final String filename = usertxt + ".txt";
            final File file = new File("C:\\ServerSession", filename);
            //boolean variable if user exists
            boolean userExists = file.exists();
            // check if user exists by checking file directory.
            if (userExists) {
                String auth = "Welcome back to the system: " + username;
                return auth;
            } else {
                //If user does not exist create a new file for the user and add to directory.
                file.createNewFile();
                String registered = "You have been successfully registered: " + username + " enjoy!";
                //return string registered if user has successfully been registered.
                return registered;
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
       return null;
    }
}
    // end class