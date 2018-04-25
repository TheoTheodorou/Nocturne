/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author Theo
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import datapacket.Datapacket;

public class Server {
    public static void main(String[] args) throws IOException {
        //When the server is running, display the server GUI
        ServerGUI serverGUI = new ServerGUI();
        serverGUI.setVisible(true);
        
        try {
            ServerSocket server = new ServerSocket(9090);
                    //Constantly "listening" (waiting) for a client.
                        while (true) 
                    { 
                        //When a client connects create a new thread to handle the connection and commands.
                        //Then start the thread. And continue to wait for more clients;
                        
                        System.out.println("Waiting for client...");
                        //New Thread
                        ServerThreading st = new ServerThreading();  
                        st.SetSocket(server.accept());
                        st.SetGUI(serverGUI);
                        Thread t1 = new Thread(st);
                        //Starts the thread
                        t1.start();                       
                    }            
        }
        //Catch any IOExceptions and print the error
        catch (IOException e)
        {
            //Print any errors
            System.err.println("Error - " + e.getMessage());
        }
    }

}
