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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Send {
      // Declares global variables
    Socket client;
    DataOutputStream outToClient;
    DataInputStream inFromClient;
    
    String username;
    String chatName;
    String chatFilePath;
    File chatFile;
    
    JTextArea logTextArea;
    JScrollPane logScrollPane;

    public Send(Socket _client, JTextArea _logTextArea, JScrollPane _logScrollPane){

        try{
            // Creates output and input stream to the client
            client = _client;
            inFromClient = new DataInputStream(client.getInputStream());
            outToClient = new DataOutputStream(client.getOutputStream());
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        
        // Passes text area and scroll pane for text append
        logTextArea = _logTextArea;
        logScrollPane = _logScrollPane;
    }
    
    public void run(){

        try{

            // Gets chat file name and size of file from client
            chatName = inFromClient.readUTF();
            username = inFromClient.readUTF();
            int clientLineCount = Integer.parseInt(inFromClient.readUTF());
            
            logTextArea.setText(logTextArea.getText() + "Chat accessed: " + chatName + "\n");
            logScrollPane.getVerticalScrollBar().setValue(logScrollPane.getVerticalScrollBar().getMaximum());

            // Opens file
            chatFilePath = new File("Chats/" + chatName + ".txt").getAbsolutePath();
            chatFile = new File(chatFilePath);

            // Checks if file exists
            if(!(chatFile.exists())){
                    // Creates new file if it doesn't exist
                    chatFile.createNewFile();
                    logTextArea.setText(logTextArea.getText() + "New chat file created for " + chatName + "\n");
                    logScrollPane.getVerticalScrollBar().setValue(logScrollPane.getVerticalScrollBar().getMaximum());
            }

            //Stores the date of the last modification to the file
            long fileModTime = chatFile.lastModified();


            // Outputs new lines to the client 
            clientLineCount = sendMessages(clientLineCount, chatFile);
            logTextArea.setText(logTextArea.getText() + chatName + " file synced with user \n");
            logScrollPane.getVerticalScrollBar().setValue(logScrollPane.getVerticalScrollBar().getMaximum());

            // Runs thread which continuously updates message table
            Thread th2 = new Thread((Runnable) new Receive(inFromClient, chatFile, client, logTextArea, logScrollPane, chatName));
            th2.start();

            while(th2.getState()!=Thread.State.TERMINATED){
                if(fileModTime != chatFile.lastModified()){
                    
                    fileModTime = chatFile.lastModified();
                    clientLineCount = sendMessages(clientLineCount, chatFile);
                    
                }
            }
        }
        catch(IOException e){
                System.out.println(e.getMessage());
        }
        
        // Disconnect message
        logTextArea.setText(logTextArea.getText() + "Disconnect from chat: " + chatName + "\n");
        logScrollPane.getVerticalScrollBar().setValue(logScrollPane.getVerticalScrollBar().getMaximum());
}
    
public int sendMessages(int clientLineCount, File chatFile){

    int currentLine = 0;

    try{

        // Creates a file reader 
        FileReader fileIn = new FileReader(chatFile); 
        BufferedReader bufferIn = new BufferedReader(fileIn);
        String line = null;

        // Iterates through each line of the file sending to the client all lines which
        // dont have locally
        while ((line = bufferIn.readLine()) != null) {
            
            if (currentLine >= clientLineCount){
               
                outToClient.writeUTF(line);
                
                List<String> lineBreakdown = Arrays.asList(line.split("§"));
                if(lineBreakdown.get(0).equals("F") && !(lineBreakdown.get(1).equals(username))){
                    String filePath = "Chats/Files/" + lineBreakdown.get(4);
                    FileInputStream sendFile;
                    sendFile = new FileInputStream(filePath);
                    byte [] fileBytes = new byte[sendFile.available()];
                    sendFile.read(fileBytes);
                    outToClient.write(fileBytes, 0, fileBytes.length);
                    sendFile.close();
                    //File deleteFile = new File(filePath);
                    //deleteFile.delete();
                }
                
            }
            currentLine += 1;
        }

                    // Closes file reader 
        bufferIn.close();
        fileIn.close();

    }catch(IOException e){
        System.out.println(e.getMessage());
    }

    return currentLine;

}

}

