package com.gavineverett.server;

/* SslSocketServer.java
 - Copyright (c) 2014, HerongYang.com, All Rights Reserved.
 */
 /*
 Since my SSL socket server does not require client authentication, we can create a SSL socket client with the default SSL socket factory. Here is my sample program,
 SslSocketServer.java, which can be used to communicate with SslReverseEchoer.java:
 */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Scanner;
import javax.net.ssl.*;

public class SslSocketServer {

    //defining codes
    public static final String login = "200";
    public static final String logout = "204";
    public static final String fileUpload = "300";
    public static final String fileDownload = "500";
    static File userDirectory;
    private static boolean isCreated = false;


    final static String keyStoreFile = "herong.jks"; // The FileName
    final static String trustFile = "public.jks"; // The FileName
    final static String passwd = "gavin1234"; // The Password

    /*
     * Which Port to Listen SSL Connections
     */
    final static int theServerPort = 8888;

    /*
     * Turn on SSL debugging?
     */
   static boolean debug = false;

    void doServerSide() throws Exception {

        KeyStore ks = KeyStore.getInstance("JKS");


        char[] passphrase = passwd.toCharArray();

        try (FileInputStream fis = new FileInputStream(keyStoreFile)) {
            ks.load(fis, passphrase);
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext sslCtx = SSLContext.getInstance("DTLS");

        sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        SSLEngine engine = sslCtx.createSSLEngine("localhost", theServerPort);
        engine.setUseClientMode(false);





        try {

            MyServerDatagramSocket mySocket = new MyServerDatagramSocket(8888);


            System.out.println("Client-Server Application ready! Secured by DTLS.");
            while (true) {  // forever loop

                // SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();


                DatagramMessage request =
                        mySocket.receiveMessageAndSender();

                String message = request.getMessage();


                System.out.println("Request Received!");
                System.out.println(engine.getSession());

                //For Writing Back to the Client
                //  OutputStream sslOS = sslSocket.getOutputStream();

                //Read from the Client
                // BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(sslIS));


                //when the message is retrieved the client will send back a code followed by the request of login.
                String code = message;
                System.out.println(code);

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
                        byte[] request1 = mySocket.recieveFile();
                        //remove whitespace from file coming onto server.
                        String filetoUpload = credentials[2].trim();
                        System.out.println(filetoUpload);
                        System.out.println("File received");
                        System.out.println("Performing upload operation");
                        // convert byte array and upload to local directory
                        String uploadFile = performUploadOperation(request1, filetoUpload);
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
                        mySocket.sendMessage(request.getAddress(), request.getPort(),
                                downloadFile);
                        break;


                }


            }


        } catch (Exception ex) {
            ex.printStackTrace();
        } // end catch
    }



    public static String performLoginOperation(String username, String password) {
        try {
            //check for txt file
            final String usertxt = username;
            final String filename = usertxt;
            final File file = new File("C:\\ServerSession\\", filename);
            final File creds = new File("C:\\ServerSession\\", filename + "\\" + "creds.txt");
            //boolean variable if user exists
            boolean userExists = creds.exists();
            // check if user exists by checking file directory.
            if (userExists) {
                //If users file is there then we check for password inside of file.
                Scanner in = new Scanner(new FileReader(creds));
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
                file.mkdir();
                creds.createNewFile();
                String storeUserpass = password;
                //write the new user password to the file.
                PrintWriter writePass = new PrintWriter(creds, "UTF-8");
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

    public static void main(String[] args) throws Exception {


        //creating a directory to store users
        userDirectory = new File("C:\\ServerSession");
        //check if directory does not exist.
        if (!userDirectory.exists()) {
            userDirectory.mkdir();
        }


        System.setProperty("javax.net.ssl.keyStore", keyStoreFile);
        System.setProperty("javax.net.ssl.keyStorePassword", passwd);
       // if (debug)
           // System.setProperty("javax.net.debug", "all");

        // java -Djavax.net.ssl.keyStore=/tmp/trustFilename
        // -Djavax.net.ssl.keyStorePassword=123456 SSLServerExample
        new SslSocketServer().doServerSide();

    }

}