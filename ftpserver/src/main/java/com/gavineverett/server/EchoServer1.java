package com.gavineverett.server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class EchoServer1 {
    //defining codes
    public static final String login = "200";
    public static final String logout = "204";
    public static final String fileUpload = "300";
    public static final String fileDownload = "500";
    static File userDirectory;
    private static boolean isCreated = false;

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
            System.out.println("Client-Server Application ready!");
            while (true) {  // forever loop
                DatagramMessage request =
                        mySocket.receiveMessageAndSender();
                System.out.println("Request received");
                String message = request.getMessage();
                System.out.println("message received: " + message);

                //when the message is retrieved the client will send back a code followed by the request of login.
                String code = message;

                //splitting datagram message into components to access data.
                String[] credentials = code.split("-");
                String typeRequest = credentials[0];
                String username = credentials[1];
                String password = credentials[2];


                //create a switch statement to decipher between the codes chosen.
                switch (typeRequest) {
                    case login:
                        //login
                        String extractUserName = username;
                        String extractPassword = password;
                        //check for users validity.
                        String authCheck = performLoginOperation(extractUserName, extractPassword);
                        //send back relevant information back to the client.
                        mySocket.sendMessage(request.getAddress(),
                                request.getPort(), authCheck);
                        break;

                    case logout:
                        //logout
                        String loggedOutUser = username;
                        //Perform logout
                        String logoutCheck = performLogoutOperation(loggedOutUser);
                        //send message back to the client.
                        mySocket.sendMessage(request.getAddress(),
                                request.getPort(), logoutCheck);
                        break;

                    case fileUpload:
                        //fileupload
                        //Receive the message data from the client.
                        byte[] request1 =  mySocket.recieveFile();
                        //remove whitespace from file coming onto server.
                        String filetoUpload = credentials[2].trim();
                        System.out.println(filetoUpload);
                        System.out.println("File received");
                        System.out.println("Performing upload operation");
                        //convert byte array and upload to local directory
                        String uploadFile =  performUploadOperation(request1, filetoUpload);
                        //send response back to the client.
                        mySocket.sendMessage(request.getAddress(),
                                request.getPort(), uploadFile);
                        break;

                    case fileDownload:
                        //filedownload
                        // credentials one will bring back the 3nd parameter in the message received.
                        String filenameRecieved = credentials[2].trim();
                        System.out.println("Download Request received: " + filenameRecieved);
                        String downloadFile = performDownloadOperation(filenameRecieved);
                        //send file to the client for downloading
                        mySocket.sendMessage(request.getAddress(),request.getPort(),
                                 downloadFile);
                        break;


                }

            } //end while
        } // end try
        catch (Exception ex) {
            ex.printStackTrace();
        } // end catch
    } //end main


    //methods to perform actions.

    public static String performLoginOperation(String username, String password) {
        try {
            //check for txt file
            final String usertxt = username;
            final String filename = usertxt + ".txt";
            final File file = new File("C:\\ServerSession", filename);
            //boolean variable if user exists
            boolean userExists = file.exists();
            // check if user exists by checking file directory.
            if (userExists) {
                //If users file is there then we check for password inside of file.
                Scanner in = new Scanner(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                while (in.hasNext()) {
                    sb.append(in.next());
                }
                in.close();
                String outString = sb.toString();
                if (outString.equals(password)) {
                    String auth = "202:" + "Welcome back to the system: " + username;
                    createTempFileForDownload();
                    return auth;
                } else {
                    String invalidCredentials = "405:" + "Invalid credentials! User exists!";
                    return invalidCredentials;
                }

            } else {
                //If user does not exist create a new file for the user and add to directory.
                file.createNewFile();
                String storeUserpass = password;
                //write the new user password to the file.
                PrintWriter writePass = new PrintWriter(file, "UTF-8");
                //write password to file.
                writePass.println(storeUserpass);
                writePass.close();
                //output sucessfully registered message.
                String registered = "204:" + "You have been successfully registered: " + username + " enjoy!";
                createTempFileForDownload();
                //return string registered if user has successfully been registered.
                return registered;
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return null;
    }


    public static String performLogoutOperation(String username) {
        try {
            String logoutMessage = "420: " + "The user: " + username + " has been successfully logged out.";

            return logoutMessage;

        } catch (Exception ie) {

            ie.printStackTrace();
        }
        return null;
    }

    public static String performUploadOperation(byte[] file, String filename)
            throws IOException {
        //creating a directory to store file.
        //creating a directory to store users
        File userDirectory = new File("C:\\ServerUploads");
        //check if directory does not exist.
        if (!userDirectory.exists()) {
            userDirectory.mkdir();
        }

            //create new file in the directory.
            File crreate = new File(userDirectory + "\\" +  filename);
            if(!crreate.exists())
            crreate.createNewFile();

        //check if file exists before writing byte array to the file.
        if(crreate.exists() && userDirectory.exists()) {
            //convert the byte array retrieved to a file and upload to server folder.
            try (FileOutputStream fos = new FileOutputStream(crreate)) {
                //write file to directory.
                fos.write(file);
                System.out.println("Success wrote to file");
            }
        }
            // Return string with response 600 and name of file uploaded.
        String sucess = "302 - The file: " + crreate.getName() +   " has been successfully uploaded!";
        return sucess;
    }

    public static String performDownloadOperation(String filename)
            throws IOException {
        //create temp folder for file.
        // acquire the filename to be downloaded.
        String fileToDownload = "C:\\ServerSession\\"+ "test\\" + filename;
        //find the file and read all the contents of the file.

        //read all bytes from the file.
        String content = new String(Files.readAllBytes(Paths.get(fileToDownload)));

        //returning the file possibly need array??>.
        return content;
    }


    //creating temporary file for the download operation.
    public static void createTempFileForDownload()
    {
        String fileName="Download";
        File tagFile=new File("C:\\ServerSession\\"+ "test");
        if(!tagFile.exists()){
            try {
                tagFile.mkdir();

                File createFile =new File("C:\\ServerSession\\"+ "test\\" +  fileName+".txt");
                if(!createFile.exists())
                    createFile.createNewFile();

                PrintWriter writer = new PrintWriter(createFile, "UTF-8");
                writer.println("The first line");
                writer.println("The second line");
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

    // end class