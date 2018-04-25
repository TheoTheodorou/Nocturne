/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author jakew
 */
import com.sun.java.accessibility.util.GUIInitializedListener;
import java.io.*;
import java.net.Socket;
import datapacket.Datapacket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

//ALL THE FUCNTIONS FOR EVERYTHING GO HERE
public class ServerThreading implements Runnable {

    private Socket clientSocket;
    public ServerGUI GUI;
    public Datapacket InFromClient = new Datapacket();
    public Datapacket ToClient = new Datapacket();
    public String userName;
    UserHandling user = new UserHandling();
    public static List<String> onlineUsers = new ArrayList<String>();

    public ServerThreading SetSocket(Socket client) {
        this.clientSocket = client;
        ServerThreading returnThread = new ServerThreading();
        return returnThread;
    }

    public void SetGUI(ServerGUI GUI) {
        this.GUI = GUI;
        user.GUI = GUI;
    }

    public void run() {
        try {
            //Stating which client connected to
            //System.out.println("Connected to " + client.getInetAddress());
            //Create an Input stream to send to user        
            ObjectInputStream FromClientStream = new ObjectInputStream(clientSocket.getInputStream());

            ObjectOutputStream ToClientStream = new ObjectOutputStream(clientSocket.getOutputStream());

            //Loop
            //Database db = new Database();
            //Accessing the DataBase
            InFromClient = (Datapacket) FromClientStream.readObject();
            String ip = clientSocket.getInetAddress().toString().replace("/", "");
            //System.out.println(InFromClient.GetService());
            if (null == InFromClient.GetCommand()) {
                GUI.AddToLog(ip + " has sent an invalid command");
            } else //Login attempted
            {
                switch (InFromClient.GetCommand()) {
                    //User Log in 
                    case "LOGIN":
                        String inputUserName = InFromClient.GetArray().get(0);
                        String UserPassWord = InFromClient.GetArray().get(1);

                        File userFile = new File("users/" + inputUserName + "/" + inputUserName + ".txt");
                        ToClient.SetCommand("LOGIN");
                        if (userFile.exists() && !userFile.isDirectory()) {

                            BufferedReader bufferedReader = new BufferedReader(new FileReader(userFile));
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                if (line.equals(UserPassWord)) {
                                    userName = inputUserName;
                                    addActiveUser(userName);
//                                    GUI.AddToLog("debug: " + onlineUsers.get(0));

                                    ToClient.SetSingleData("CORRECT");

                                    break;
                                }
                            }

                            //ToClient.SetSingleData("CORRECT");
                        } else {
                            ToClient.SetSingleData("INCORRECT");
                            GUI.AddToLog("Incorrect Log in attempt from: " + ip);
                        }   //Send InfoPacket To user
                        ToClientStream.writeObject(ToClient);
                        break;

                    //Log Out
                    case "LOGOUT": {
                        removeActiveUser(userName);
                        
                        ToClient.SetCommand("LOGOUT");
                        ToClient.SetSingleData("Logout");
                        ToClientStream.writeObject(ToClient);
                        GUI.AddToLog(InFromClient.GetData() + " has logged out");
                        break;
                    }
                    //Create new user
                    case "CREATE_USER": {

                        ArrayList UsersInfo = InFromClient.GetArray();

                        File registerFile = new File("users/" + userName + "/" + userName + ".txt");
                        boolean AlreadyExists = (registerFile.exists() && !registerFile.isDirectory());

                        ToClient.SetCommand("CREATE_USER");
                        if (AlreadyExists == false) {
                            user.createUser(UsersInfo);

                            byte[] Image = (byte[]) InFromClient.GetByteData();
                            
                            new File ("media/profiles").mkdirs();
                            File PhotoDirectory = new File("media/profiles/" + userName + ".png");
                            FileOutputStream FileOut = new FileOutputStream(PhotoDirectory);
                            FileOut.write(Image);

                            ToClient.SetSingleData("Registered");

                        } else {
                            ToClient.SetSingleData("UsernameExists");
                        }
                        ToClientStream.writeObject(ToClient);
                        GUI.AddToLog("Added new user: " + UsersInfo.get(0));
                        break;
                    }
                    //Upload new song
                    case "UPLOAD_SONG": {

                        ArrayList SongInformation = InFromClient.GetArray();

                        String FileName = SongInformation.get(2) + "," + SongInformation.get(3);
                        File MusicDirectory = new File("media/music/" + FileName + ".mp3");
                        File PhotoDirectory = new File("media/albums/" + FileName + ".png");
                        byte[] Song = (byte[]) InFromClient.GetByteData();
                        FileOutputStream SongOut = new FileOutputStream(MusicDirectory);
                        SongOut.write(Song);
                        byte[] CoverPhoto = (byte[]) InFromClient.GetSecondData();
                        FileOutputStream PhotoOut = new FileOutputStream(PhotoDirectory);
                        PhotoOut.write(CoverPhoto);
                        user.addSong(SongInformation);
                        user.addPost(SongInformation);
                        GUI.AddToLog(SongInformation.get(0) + " Uploaded a new song: " + FileName);

                        //System.out.println("Successfull");
                        break;
                    }
//                    }
                    //Upload new post
                    case "UPLOAD_POST": {
                        user.addPost(InFromClient.GetArray());
                        ToClient.SetCommand("UPLOAD_POST");
                        ToClient.SetSingleData("Added Post");
                        ToClientStream.writeObject(ToClient);
                        GUI.AddToLog(InFromClient.GetArray().get(0) + " made a new post");
                        break;
                    }
                    //Get My Friends
                    case "GET_FRIENDS": {
                        ArrayList<String> UsersFriends = user.GetUsersFriends(InFromClient.GetData());
                        ToClient.SetCommand("GET_FRIENDS");
                        ToClient.SetArray(UsersFriends);
                        ToClientStream.writeObject(ToClient);
                        GUI.AddToLog(InFromClient.GetData() + " requested to view all friends");
                        break;
                    }

                    //Get Active Friends
                    case "GET_ACTIVE_FRIENDS": {
                        
                        ArrayList<String> Friends = user.GetUsersFriends(InFromClient.GetData());
                        ArrayList<String> ActiveFriends = GetActiveFriends(Friends);
                        ToClient.SetCommand("GET_ACTIVE_FRIENDS");
                        ToClient.SetArray(ActiveFriends);
                        ToClientStream.writeObject(ToClient);
                        GUI.AddToLog("Getting " + InFromClient.GetData() + " active friends");
                        break;
                    }
                    //New Friend Request
                    case "NEW_FRIEND_REQUEST": {
                        ArrayList<String> Users = InFromClient.GetArray();
                        boolean UsernameExists = user.DoesUsernameExist(Users.get(1));
                        ToClient.SetCommand("NEW_FRIEND_REQUEST");
                        if (UsernameExists == true) {
                            boolean AlreadyFriends = user.AlreadyFriends(Users);
                            if (AlreadyFriends == false) {
                                ToClient.SetSingleData("Exists");
                                user.NewFriendRequest(Users);
                                GUI.AddToLog(Users.get(0) + " sent " + Users.get(1) + " a friends request");
                            } else {
                                ToClient.SetSingleData("AlreadyFriends");
                                GUI.AddToLog(Users.get(0) + " tried to send " + Users.get(1) + " a friend request, but they are already friends");
                            }
                        } else {
                            ToClient.SetSingleData("Doesnt");
                            GUI.AddToLog(Users.get(0) + " tried to send a friend request to a none existing user");
                        }
                        ToClientStream.writeObject(ToClient);
                        break;
                    }
                    //Get Friend Requests
                    case "GET_FRIEND_REQUESTS": {
                        ArrayList<String> UsersFriendRequests = user.GetUsersFriendRequests(InFromClient.GetData());
                        ToClient.SetCommand("GET_FRIEND_REQUESTS");
                        ToClient.SetArray(UsersFriendRequests);
                        ToClientStream.writeObject(ToClient);
                        GUI.AddToLog(InFromClient.GetData() + " reqested to see their friend requests");
                        break;
                    }
                    //Get Users based on Prefernces
                    case "GET_USERS_ON_PREFERENCE": {
                        ArrayList<String> Users = user.GetUsernamesOnPreferences(InFromClient.GetData());
                        ToClient.SetCommand("GET_USERS_ON_PREFERENCE");
                        ToClient.SetArray(Users);
                        ToClientStream.writeObject(ToClient);
                        GUI.AddToLog(ip + " requested to get users based on preferences");
                        break;
                    }

                    //Accept Friend Request
                    case "ACCEPT_FRIEND_REQUEST": {
                        user.AcceptFriendRequest(InFromClient.GetArray());
                        ToClient.SetCommand("GFR");
                        ToClient.SetSingleData("Accepted");
                        ToClientStream.writeObject(ToClient);
                        GUI.AddToLog(InFromClient.GetArray().get(0) + " accepted " + InFromClient.GetArray().get(1) + " friend request");
                        break;
                    }
                    //Decline Friend Request
                    case "DECLINE_FRIEND_REQUEST": {
                        user.DeclineFriendRequest(InFromClient.GetArray());
                        Datapacket Reply = new Datapacket();
                        ToClient.SetCommand("DFR");
                        ToClient.SetSingleData("Declined");
                        ToClientStream.writeObject(ToClient);
                        GUI.AddToLog(InFromClient.GetArray().get(0) + " declined " + InFromClient.GetArray().get(1) + " friend request");
                        break;
                    }
                    //Get My Songs
                    case "GET_MY_SONGS": {
                        ArrayList<String> MySongs = user.GetUserSongs(InFromClient.GetData());
                        ToClient.SetCommand("GMS");
                        ToClient.SetArray(MySongs);
                        ToClientStream.writeObject(ToClient);
                        GUI.AddToLog(ip + " requests to see " + InFromClient.GetData() + " songs");
                        break;
                    }
                    //Delete FRiend
                    case "DELETE_FRIEND": {
                        user.RemoveFriend(InFromClient.GetArray());
                        ToClient.SetCommand("DFS");
                        ToClient.SetSingleData("Removed");
                        ToClientStream.writeObject(ToClient);
                        GUI.AddToLog(InFromClient.GetArray().get(0) + "removed " + InFromClient.GetArray().get(1) + " as a friend");
                        break;
                    }
                    //Get user details
//                    case "GUD": {
//                        String Username = InFromClient.GetData();
//                        Datapacket UserInformation = new Datapacket();
//                        ArrayList<String> UsersDetails = db.GetUsersDetails(Username);
//                        ArrayList<String> UserSongs = db.GetUserSongFileName(Username);
//                        ArrayList<ArrayList<String>> UsersInfo = new ArrayList();
//                        UsersInfo.add(UsersDetails);
//                        UsersInfo.add(UserSongs);
//                        UserInformation.SetService("GUD");
//                        UserInformation.SetMultipleArray(UsersInfo);
//                        File PhotoDirectory = new File("res/Photos/" + Username + ".png");
//                        FileInputStream UserPicture = new FileInputStream(PhotoDirectory);
//                        byte[] buffer = new byte[UserPicture.available()];
//                        UserPicture.read(buffer);
//                        UserInformation.SetFirstByte(buffer);
//                        ToClientStream.writeObject(UserInformation);
//                        GUI.AddToLog("Sending " + Username + " details and songs to " + ip);
//                        break;
//                    }
                    //Get Friends Posts
                    case "GET_POSTS": {
                        String Username = InFromClient.GetData();
                        ArrayList<String> Friends = user.GetUsersFriends(Username);
                        //Add own username to retrieve own posts
                        Friends.add(InFromClient.GetData());
                        ArrayList<String> UserPosts = user.GetFriendsPosts(Friends);
                        Datapacket FriendsPosts = new Datapacket();
                        FriendsPosts.SetCommand("GET_POSTS");
                        FriendsPosts.SetArray(UserPosts);
                        ToClientStream.writeObject(FriendsPosts);
                        GUI.AddToLog(Username + " requests to see their friends posts");
                        break;
                    }
//                    //DoWnload Song
                    case "DWS": {
                        Datapacket SongData = new Datapacket();
                        File MusicDirectory = new File("media/music/" + InFromClient.GetData() + ".mp3");
                        File PhotoDirectory = new File("media/albums/" + InFromClient.GetData() + ".png");
                        FileInputStream SongFile = new FileInputStream(MusicDirectory);
                        byte[] buffer = new byte[SongFile.available()];
                        SongFile.read(buffer);
                        FileInputStream PhotoFile = new FileInputStream(PhotoDirectory);
                        byte[] buffer2 = new byte[PhotoFile.available()];
                        PhotoFile.read(buffer2);
                        SongData.SetCommand("DWS");
                        SongData.SetFirstByte(buffer);
                        SongData.SetSecondByte(buffer2);
                        ToClientStream.writeObject(SongData);
                        GUI.AddToLog(ip + " is downloading the song and cover photo for " + InFromClient.GetData());
                        break;
                    }
                    default:
                        GUI.AddToLog(ip + " has sent an invalid command");
                        break;
                }
            }
            FromClientStream.close();
            ToClientStream.close();
        } catch (IOException | ClassNotFoundException e) {
            GUI.AddToLog(e.getMessage());

        }
    }

    public void addActiveUser(String username) {
        try {
            onlineUsers.add(username);

        } catch (Exception e) {
            System.out.println("Could not add user to online register");
            GUI.AddToLog("Could not add user to online register!");
        }

    }

    public String removeActiveUser(String username) {
        int userIndex = onlineUsers.indexOf(username);
        try {
            onlineUsers.remove(userIndex);
        } catch (Exception e) {
            System.out.println("Could not remove user from online register");
            GUI.AddToLog("Could not remove user from online register");
        }
        return "success";
    }

    public ArrayList<String> GetActiveFriends(ArrayList<String> Friends) {

        //GUI.AddToLog(onlineusers.get(0).toString());
        ArrayList<String> ActiveFriends = new ArrayList();
        String CurrentFriend = "";

        for (int i = 0; i < Friends.size(); i++) {
            CurrentFriend = Friends.get(i);
            if (onlineUsers.contains(CurrentFriend)) {
                //GUI.AddToLog("found friend");
                ActiveFriends.add(CurrentFriend);
            }
        }

        return ActiveFriends;
    }

}
