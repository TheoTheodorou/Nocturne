/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nocturne;

/**
 *
 * @author Theo
 */
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Chat extends javax.swing.JFrame {

    /**
     * Creates new form Login
     */
    public String userName;
    public String friendName;

    InetAddress address;
    Socket msgServer;
    DataOutputStream outToMsgServer;
    DataInputStream inFromMsgServer;

    String chatName;
    boolean cont = true;

    public Chat(String username, String friendname) {
        initComponents();

        closeWindow();

        // Gets usernames
        username = _username;
        recieveUsername = _recieveUsername;

        // Sets background
        getContentPane().setBackground(background);

        //Outputs name of user they're messaging 
        lblUsername.setText(recieveUsername);

        // Creates white lines
        lblTopLine.setBorder(new LineBorder(foreground, 4));
        lblBottomLine.setBorder(new LineBorder(foreground, 4));

        // Styles Scroll panes
        spMessages.getViewport().setBackground(background);
        spMessages.setForeground(foreground);
        spMessages.setBorder(null);
        spMessages.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        spMessages.setHorizontalScrollBarPolicy(spMessages.HORIZONTAL_SCROLLBAR_NEVER);

        spNewMessage.setBackground(background);
        spNewMessage.setForeground(foreground);
        spNewMessage.setBorder(null);
        spNewMessage.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        spNewMessage.setHorizontalScrollBarPolicy(spNewMessage.HORIZONTAL_SCROLLBAR_NEVER);

        // Styles text fields
        taFilePath.setBackground(background);
        taFilePath.setBorder(null);
        taFilePath.setEditable(false);
        taFilePath.setLineWrap(true);

        taNewMessage.setLineWrap(true);

        // Styles buttons
        cmdSend.setContentAreaFilled(false);
        cmdSend.setBackground(background);
        cmdSend.setBorder(new LineBorder(foreground, buttonBorder));
        cmdSend.setForeground(foreground);

        cmdAttach.setContentAreaFilled(false);
        cmdAttach.setBackground(background);
        cmdAttach.setBorder(new LineBorder(foreground, buttonBorder));
        cmdAttach.setForeground(foreground);

        cmdClear.setContentAreaFilled(false);
        cmdClear.setBackground(background);
        cmdClear.setBorder(new LineBorder(foreground, buttonBorder));
        cmdClear.setForeground(foreground);

        // Sets profile picture of user you're talking to
        lblProfilePicture.setBorder(new LineBorder(foreground, 2));

        // Styles message tabel
        tabelMessages.setShowVerticalLines(false);
        tabelMessages.setShowHorizontalLines(true);
        tabelMessages.setBackground(background);
        tabelMessages.setForeground(foreground);
        tabelMessages.setGridColor(highlight);
        tabelMessages.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabelMessages.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabelMessages.getColumnModel().getColumn(1).setPreferredWidth(568);

        // Creates an info packet to send the username of the person you're talking to
        InfoPacket FindUserDetails = new InfoPacket();
        FindUserDetails.SetService("GUD");
        FindUserDetails.SetSingleData(recieveUsername);

        try {

            // Creates connection with music server
            Socket MainServer = new Socket("localhost", 9090);
            ObjectOutputStream OutToServer = new ObjectOutputStream(MainServer.getOutputStream());
            ObjectInputStream FromServerStream = new ObjectInputStream(MainServer.getInputStream());

            // Sends username to server
            OutToServer.writeObject(FindUserDetails);

            // Recieves users profile picture from server
            InfoPacket ServerReply = (InfoPacket) FromServerStream.readObject();

            //Closes connection to music server
            OutToServer.close();
            FromServerStream.close();

            // Gets a byte array of the profile pictuew
            byte[] ProfileImage = (byte[]) ServerReply.GetByteData();

            // Saves the profile picture locally
            //String profilePicture = "/Users/edwardcelella/Documents/University/Systems Software/Shitify/Sams Work/MusicServer/res/Photos" + recieveUsername + ".png";
            String profilePicture = new File("res/Photos/" + recieveUsername + ".png").getAbsolutePath();
            FileOutputStream FileOut = new FileOutputStream(profilePicture);
            FileOut.write(ProfileImage);

            // Displays profile picture in label
            lblProfilePicture.setIcon(ResizeImage(profilePicture));

            // Creates connection with messaging server
            address = InetAddress.getByName("localhost");
            msgServer = new Socket(address, 9091);
            inFromMsgServer = new DataInputStream(msgServer.getInputStream());
            outToMsgServer = new DataOutputStream(msgServer.getOutputStream());

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // Runs thread which continuously updates message table
        Thread th = new Thread(new Runnable() {
            public void run() {
                SyncChatMessages();
            }
        });
        th.start();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_left = new javax.swing.JPanel();
        lbl_chooseFile = new javax.swing.JLabel();
        btn_sendMessage = new javax.swing.JButton();
        seperator1 = new javax.swing.JSeparator();
        ta_chosenFile = new javax.swing.JTextArea();
        lbl_sendMessage = new javax.swing.JLabel();
        btn_chooseFile = new javax.swing.JButton();
        sp_sendMessage = new javax.swing.JScrollPane();
        ta_sendMessage = new javax.swing.JTextArea();
        lbl_cancel = new javax.swing.JLabel();
        pnl_Right = new javax.swing.JPanel();
        sp_dialogue = new javax.swing.JScrollPane();
        ta_dialogue = new javax.swing.JTextArea();
        pnl_toolbar = new javax.swing.JPanel();
        icon_exit = new javax.swing.JLabel();
        lbl_title = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(0, 51, 153));
        setLocationByPlatform(true);
        setUndecorated(true);
        setResizable(false);
        setSize(new java.awt.Dimension(600, 330));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnl_left.setBackground(new java.awt.Color(7, 51, 73));
        pnl_left.setPreferredSize(new java.awt.Dimension(250, 260));

        lbl_chooseFile.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lbl_chooseFile.setForeground(new java.awt.Color(57, 113, 177));
        lbl_chooseFile.setText("(Optional) Choose File:");

        btn_sendMessage.setBackground(new java.awt.Color(57, 113, 177));
        btn_sendMessage.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        btn_sendMessage.setForeground(new java.awt.Color(255, 255, 255));
        btn_sendMessage.setText("Send");
        btn_sendMessage.setPreferredSize(new java.awt.Dimension(250, 25));
        btn_sendMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sendMessageActionPerformed(evt);
            }
        });

        ta_chosenFile.setBackground(new java.awt.Color(7, 51, 73));
        ta_chosenFile.setColumns(20);
        ta_chosenFile.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        ta_chosenFile.setForeground(new java.awt.Color(255, 255, 255));
        ta_chosenFile.setRows(5);
        ta_chosenFile.setText("Chosen File: ");

        lbl_sendMessage.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lbl_sendMessage.setForeground(new java.awt.Color(57, 113, 177));
        lbl_sendMessage.setText("Send Message:");

        btn_chooseFile.setBackground(new java.awt.Color(57, 113, 177));
        btn_chooseFile.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        btn_chooseFile.setForeground(new java.awt.Color(255, 255, 255));
        btn_chooseFile.setText("Choose File");
        btn_chooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_chooseFileActionPerformed(evt);
            }
        });

        ta_sendMessage.setBackground(new java.awt.Color(7, 51, 73));
        ta_sendMessage.setColumns(20);
        ta_sendMessage.setForeground(new java.awt.Color(255, 255, 255));
        ta_sendMessage.setRows(5);
        sp_sendMessage.setViewportView(ta_sendMessage);

        lbl_cancel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lbl_cancel.setForeground(new java.awt.Color(57, 113, 177));
        lbl_cancel.setText("or Cancel");
        lbl_cancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_cancelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout pnl_leftLayout = new javax.swing.GroupLayout(pnl_left);
        pnl_left.setLayout(pnl_leftLayout);
        pnl_leftLayout.setHorizontalGroup(
            pnl_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_leftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_leftLayout.createSequentialGroup()
                        .addGroup(pnl_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnl_leftLayout.createSequentialGroup()
                                .addComponent(btn_sendMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lbl_cancel))
                            .addComponent(lbl_sendMessage)
                            .addComponent(sp_sendMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(seperator1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnl_leftLayout.createSequentialGroup()
                        .addGroup(pnl_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ta_chosenFile, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                            .addGroup(pnl_leftLayout.createSequentialGroup()
                                .addComponent(lbl_chooseFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_chooseFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        pnl_leftLayout.setVerticalGroup(
            pnl_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_leftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_sendMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_sendMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seperator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_chooseFile)
                    .addComponent(btn_chooseFile, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(ta_chosenFile, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_sendMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_cancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(pnl_left, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 250, 260));

        pnl_Right.setBackground(new java.awt.Color(32, 33, 35));
        pnl_Right.setPreferredSize(new java.awt.Dimension(250, 260));

        ta_dialogue.setBackground(new java.awt.Color(32, 33, 35));
        ta_dialogue.setColumns(20);
        ta_dialogue.setForeground(new java.awt.Color(255, 255, 255));
        ta_dialogue.setRows(5);
        sp_dialogue.setViewportView(ta_dialogue);

        javax.swing.GroupLayout pnl_RightLayout = new javax.swing.GroupLayout(pnl_Right);
        pnl_Right.setLayout(pnl_RightLayout);
        pnl_RightLayout.setHorizontalGroup(
            pnl_RightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_RightLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sp_dialogue, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnl_RightLayout.setVerticalGroup(
            pnl_RightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_RightLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(sp_dialogue, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        getContentPane().add(pnl_Right, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 30, 250, 260));

        pnl_toolbar.setBackground(new java.awt.Color(57, 113, 177));
        pnl_toolbar.setPreferredSize(new java.awt.Dimension(600, 30));
        pnl_toolbar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                pnl_toolbarMouseDragged(evt);
            }
        });
        pnl_toolbar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnl_toolbarMousePressed(evt);
            }
        });

        icon_exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nocturne/resources/cancel.png"))); // NOI18N
        icon_exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                icon_exitMousePressed(evt);
            }
        });

        lbl_title.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        lbl_title.setForeground(new java.awt.Color(255, 255, 255));
        lbl_title.setText("Chat To Friends");

        javax.swing.GroupLayout pnl_toolbarLayout = new javax.swing.GroupLayout(pnl_toolbar);
        pnl_toolbar.setLayout(pnl_toolbarLayout);
        pnl_toolbarLayout.setHorizontalGroup(
            pnl_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_toolbarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_title, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE)
                .addComponent(icon_exit, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnl_toolbarLayout.setVerticalGroup(
            pnl_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_title, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
            .addComponent(icon_exit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(pnl_toolbar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    int xx;
    int xy;
    private void pnl_toolbarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnl_toolbarMousePressed
        xx = evt.getX();
        xy = evt.getY();
    }//GEN-LAST:event_pnl_toolbarMousePressed

    private void pnl_toolbarMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnl_toolbarMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xx, y - xy);
    }//GEN-LAST:event_pnl_toolbarMouseDragged

    private void btn_sendMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_sendMessageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_sendMessageActionPerformed

    private void btn_chooseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_chooseFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_chooseFileActionPerformed

    private void lbl_cancelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_cancelMousePressed
        this.dispose();
    }//GEN-LAST:event_lbl_cancelMousePressed

    private void icon_exitMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_icon_exitMousePressed
        this.dispose();
    }//GEN-LAST:event_icon_exitMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_chooseFile;
    private javax.swing.JButton btn_sendMessage;
    private javax.swing.JLabel icon_exit;
    private javax.swing.JLabel lbl_cancel;
    private javax.swing.JLabel lbl_chooseFile;
    private javax.swing.JLabel lbl_sendMessage;
    private javax.swing.JLabel lbl_title;
    private javax.swing.JPanel pnl_Right;
    private javax.swing.JPanel pnl_left;
    private javax.swing.JPanel pnl_toolbar;
    private javax.swing.JSeparator seperator1;
    private javax.swing.JScrollPane sp_dialogue;
    private javax.swing.JScrollPane sp_sendMessage;
    private javax.swing.JTextArea ta_chosenFile;
    private javax.swing.JTextArea ta_dialogue;
    private javax.swing.JTextArea ta_sendMessage;
    // End of variables declaration//GEN-END:variables

    public void SyncChatMessages() {

        // Joins both usernames in alphabetical order and joins them to get chat file name
        int compare = username.compareTo(recieveUsername);
        if (compare < 0) {
            chatName = username + recieveUsername;
        } else {
            chatName = recieveUsername + username;
        }

        // Gets filepath and file
        String chatFilePath = new File("res/Chats/" + chatName + ".txt").getAbsolutePath();
        chatFile = new File(chatFilePath);

        // Used to add rows to tabel
        DefaultTableModel tabelModel = (DefaultTableModel) tabelMessages.getModel();

        int lineCount = 0;
        int rowNum = 0;
        int height, newHeight, expansion, length;

        try {
            // Checks if file exists
            if (!(chatFile.exists())) {
                // Creates file if one isn't present
                chatFile.createNewFile();
            } else {

                // Outputs all local stored messages
                FileReader fileIn = new FileReader(chatFilePath);
                BufferedReader bufferIn = new BufferedReader(fileIn);
                String line = null;

                // Outputs locally stored messages
                while ((line = bufferIn.readLine()) != null) {

                    // Seperates username from message
                    java.util.List<String> lineBreakdown = Arrays.asList(line.split("§"));
                    // Stores message length
                    length = lineBreakdown.get(1).length();
                    // Adds html tags so text wraps
                    line = "<html>" + lineBreakdown.get(1) + "</html>";

                    // Adds username and message to tabel
                    Object[] row = {lineBreakdown.get(0), line};
                    tabelModel.addRow(row);

                    // Scrolls to bottom of scroll pane
                    spMessages.getVerticalScrollBar().setValue(spMessages.getVerticalScrollBar().getMaximum());

                    // Calculates requires row height to display message
                    height = tabelMessages.getRowHeight(rowNum);
                    newHeight = tabelMessages.prepareRenderer(tabelMessages.getCellRenderer(rowNum, 1), rowNum, 1).getPreferredSize().height;
                    expansion = (length / 50);
                    if (expansion > 1) {
                        newHeight = newHeight * expansion;
                    }

                    // Alters row height
                    tabelMessages.setRowHeight(rowNum, newHeight);

                    rowNum += 1;
                    lineCount += 1;

                }
                bufferIn.close();
                fileIn.close();
            }

            //Sends chatname and amount of lines saved on the client file to the server
            outToMsgServer.writeUTF(chatName);
            outToMsgServer.writeUTF(username);
            outToMsgServer.writeUTF(Integer.toString(lineCount));

            // Loop to catch all incoming messages 
            String recieveLine = null;

            while (cont) {

                // Gets new message from server
                recieveLine = inFromMsgServer.readUTF();

                // Seperates username from message
                java.util.List<String> lineBreakdown = Arrays.asList(recieveLine.split("§"));
                // Stores message length
                length = lineBreakdown.get(2).length();
                // Adds html tags so text wraps
                recieveLine = "<html>" + lineBreakdown.get(2) + "</html>";

                // Outputs line to GUI
                Object[] rowData = {lineBreakdown.get(1), recieveLine};
                tabelModel.addRow(rowData);

                // Scrolls to bottom of scroll pane
                spMessages.getVerticalScrollBar().setValue(spMessages.getVerticalScrollBar().getMaximum());

                // Calculates requires row height to display message
                height = tabelMessages.getRowHeight(rowNum);
                newHeight = tabelMessages.prepareRenderer(tabelMessages.getCellRenderer(rowNum, 1), rowNum, 1).getPreferredSize().height;
                expansion = (length / 50);
                if (expansion > 1) {
                    newHeight = newHeight * expansion;
                }

                // Alters row height
                tabelMessages.setRowHeight(rowNum, newHeight);

                //Writes new line to local text file
                FileWriter fileOut = new FileWriter(chatFile.getAbsolutePath(), true);
                BufferedWriter bufferOut = new BufferedWriter(fileOut);
                bufferOut.write(lineBreakdown.get(1) + "§" + lineBreakdown.get(2));
                bufferOut.newLine();
                bufferOut.close();
                fileOut.close();

                rowNum += 1;

                if (lineBreakdown.get(0).equals("F")) {

                    // Creates new buffer for file
                    byte[] fileBtyes = new byte[Integer.parseInt(lineBreakdown.get(3))];

                    // Reads byte array in from client
                    inFromMsgServer.readFully(fileBtyes, 0, fileBtyes.length);

                    // File path to home 
                    String homeLocation = System.getProperty("user.home");

                    System.out.println(homeLocation);
                    // Saves file
                    File fileSavePath = new File(homeLocation + "/Downloads/" + lineBreakdown.get(4));
                    FileOutputStream FileOut = new FileOutputStream(fileSavePath);
                    FileOut.write(fileBtyes);
                    FileOut.close();
                }

            }

        } catch (IOException e) {
            // Prints error message
            System.out.println(e.getMessage());
        }

    }

    public void closeWindow() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cont = false;
                try {
                    outToMsgServer.writeUTF("E" + "§" + "E");
                } catch (IOException e2) {
                    // Prints error message
                    System.out.println(e2.getMessage());
                }
                e.getWindow().dispose();
            }

        });

}