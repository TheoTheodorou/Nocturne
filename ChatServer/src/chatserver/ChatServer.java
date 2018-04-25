/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.net.*; import java.util.*; import java.io.*;

/**
 *
 * @author Theo
 */
    
public class ChatServer {
    public static void main(String[]args)throws IOException{
        ServerSocket server = new ServerSocket(9898);
        while(true){
            System.out.println("Waiting");
            //establish connection
            Socket chatclient = server.accept();
            System.out.println("Connected" + chatclient.getInetAddress());
            //create IO Streams
            DataInputStream inFromClient = new DataInputStream(chatclient.getInputStream());
            DataOutputStream outToClient = new DataOutputStream(chatclient.getOutputStream());
            System.out.println(inFromClient.readUTF()); //get any data from the client
            //example
            Date date = new Date();
            outToClient.writeUTF(date.toString()); //send date to client
            chatclient.close();
        }
    }
    
}

