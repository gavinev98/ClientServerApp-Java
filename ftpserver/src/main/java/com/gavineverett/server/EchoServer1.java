package com.gavineverett.server;

import java.io.File;

public class EchoServer1 {
    //defining codes
    public static final String login = "200";
    public static final String logout = "204";
    public static final String fileUpload = "300";
    public static final String fileDownload = "500";

    public static void main(String[] args) {

        //creating a directory to store users
        File userDirectory = new File("C:\\ServerSession");
        //check if directory does not exist.
        if(!userDirectory.exists())
        {
            userDirectory.mkdir();
        }


        int serverPort = 7;    // default port
        if (args.length == 1 )
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
                String message = request.getMessage( );
                System.out.println("message received: "+ message);
                // Now send the echo to the requestor
                mySocket.sendMessage(request.getAddress( ),
                        request.getPort( ), message);


                //when the message is retrieved the client will send back a code followed by the request of login.
                  String code = message.substring(0, 3);

                //create a switch statement to decipher between the codes chosen.
                switch (code)
                {
                    case login:
                        //logincode
                        String recieveMessage = request.getMessage();
                        String extractUserName = recieveMessage.substring(14, 19);
                        System.out.println("The username is: " + extractUserName);
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
            ex.printStackTrace( );
        } // end catch
    } //end main


    //methods to perform actions.

    public String performLoginOperation(String username)
    {
        return null;
    }

} // end class