/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

/**
 *
 * @author jakew
 */


import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Receive {
    Socket client;
    DataInputStream inFromClient;
    File chatFile;
    boolean runServer = true;
    
    JTextArea logTextArea;
    JScrollPane logScrollPane;
    String chatName;



public Receive(DataInputStream _inFromClient, File _chatFile, Socket _client, JTextArea _logTextArea, JScrollPane _logScrollPane, String _chatName){

        client = _client;
        inFromClient = _inFromClient;
        chatFile = _chatFile;
        
        // Passes text area and scroll pane for text append
        logTextArea = _logTextArea;
        logScrollPane = _logScrollPane;
        chatName = _chatName;

}

   public void run(){
        
        while(runServer){
            try{
                
                // Reads and splits message
                String newMessage = inFromClient.readUTF();
                List<String> lineBreakdown = Arrays.asList(newMessage.split("§"));
                
                // Closes thread if chat window is closed
                if(lineBreakdown.get(0).equals("E")){
                    runServer = false;
                }
                
                // Runs if message or file is sent
                if (lineBreakdown.get(0).equals("T") || lineBreakdown.get(0).equals("F")){
                    
                    // Runs if file is sent
                    if (lineBreakdown.get(0).equals("F")){
                        
                        // Creates new buffer for file
                        byte[] fileBtyes = new byte[Integer.parseInt(lineBreakdown.get(3))];
                        
                        // Reads byte array in from client
                        inFromClient.readFully(fileBtyes, 0, fileBtyes.length);
                        
                        // Saves file
                        File fileSavePath = new File("Chats/Files/" + lineBreakdown.get(4));
                        FileOutputStream FileOut = new FileOutputStream(fileSavePath);
                        FileOut.write(fileBtyes);
                        FileOut.close();
                        
                        logTextArea.setText(logTextArea.getText() + lineBreakdown.get(1) + " sent file '" + lineBreakdown.get(4) + "' to chat: " + chatName + "\n");
                        logScrollPane.getVerticalScrollBar().setValue(logScrollPane.getVerticalScrollBar().getMaximum());

                    }
                    
                    // Writes new message to text file
                    FileWriter fileOut = new FileWriter(chatFile.getAbsolutePath(), true);
                    BufferedWriter bufferOut = new BufferedWriter(fileOut); 
                    bufferOut.write(newMessage);
                    bufferOut.newLine();
                    bufferOut.close();
                    fileOut.close();
                    
                    logTextArea.setText(logTextArea.getText() + lineBreakdown.get(1) + " sent message to chat: " + chatName + "\n");
                    logScrollPane.getVerticalScrollBar().setValue(logScrollPane.getVerticalScrollBar().getMaximum());
                    
                    
                }
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
    }  
}
