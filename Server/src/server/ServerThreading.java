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
    UserHandling user = new UserHandling();
    public static List<String> onlineUsers = new ArrayList<String>();
    //Sets the socket based of the connecting client
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
       
            //Create an I/O stream to send to user        
            ObjectInputStream FromClientStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream ToClientStream = new ObjectOutputStream(clientSocket.getOutputStream());
            //Retreieve the datapacket sent by the client and read the information
            InFromClient = (Datapacket) FromClientStream.readObject();
            //Stores the ip of the clientsocket (clientsocket defined earlier in class)
            String ip = clientSocket.getInetAddress().toString().replace("/", "");
            //If there has been no command sent by the client, output the ip of the client and declare the error
            if (null == InFromClient.GetCommand()) {
                GUI.AddToLog(ip + " has sent an invalid command");
            } else //Login attempted
            {
                //Switch cases begin, switches occording to the command recieved.
                switch (InFromClient.GetCommand()) {
                    //User Log in 
                    case "LOGIN":
                        //Retreieves the username and password from the client
                         GUI.AddToLog("User : " + ip + " attempting login...");
                        
                        String inputUserName = InFromClient.GetArray().get(0);
                        String userPassWord = InFromClient.GetArray().get(1);

                        File userFile = new File("users/" + inputUserName + "/" + inputUserName + ".txt");
                        //Sets the current command to Login
                        ToClient.SetCommand("LOGIN");
                        //checks to see if the file exits...
                        if (userFile.exists() && !userFile.isDirectory()) {
                            BufferedReader bufferedReader = new BufferedReader(new FileReader(userFile));
                            String line;
                            //Loop through
                            while ((line = bufferedReader.readLine()) != null) {
                                //if the password matches return to the client "CORRECT"
                                if (line.equals(userPassWord)) {
                                    //adds an active user and passes through the username.
                                    addActiveUser(inputUserName);
                                    ToClient.SetSingleData("CORRECT");
                                    GUI.AddToLog("User : " + ip + " successfully logged in.");
                                    break;
                                }
                            }
                        } else { //there was no match...
                            ToClient.SetSingleData("INCORRECT");
                            GUI.AddToLog("Incorrect Log in attempt from: " + ip);
                        }
                        //send the datapacket to the client
                        ToClientStream.writeObject(ToClient);
                        break;

                    //Log Out
                    case "LOGOUT": {
                        //Removes the active user
                        GUI.AddToLog("User : " + ip + " attempting log out.");
                        removeActiveUser(InFromClient.GetData());
                        //Set the command "LOGOUT"
                        ToClient.SetCommand("LOGOUT");
                        ToClient.SetSingleData("Logout");
                        //send the datapacket to the client
                        ToClientStream.writeObject(ToClient);
                        //log the action
                        GUI.AddToLog(InFromClient.GetData() + " has logged out");
                        break;
                    }
                    //Create new user
                    case "CREATE_USER": {
                        GUI.AddToLog("Atttempting to create a user...");
                        //Retreieve the data from the datapacket
                        ArrayList UsersInfo = InFromClient.GetArray();
                        String userName = UsersInfo.get(0).toString();
                        //creates a new file if one doesnt already exist
                        File registerFile = new File("users/" + userName + "/" + userName + ".txt");
                        boolean AlreadyExists = (registerFile.exists() && !registerFile.isDirectory());
                        //Set the command "CREATE_USER"
                        ToClient.SetCommand("CREATE_USER");
                        //if the file exists...
                        if (AlreadyExists == false) {
                            //create the user based of the datapacket 
                            user.createUser(UsersInfo);
                            //store the users profile picture
                            byte[] Image = (byte[]) InFromClient.GetByteData();
                            new File ("media/profiles").mkdirs();
                            File PhotoDirectory = new File("media/profiles/" + userName + ".png");
                            FileOutputStream FileOut = new FileOutputStream(PhotoDirectory);
                            FileOut.write(Image);
                            //tell the client they have registered
                            ToClient.SetSingleData("Registered");
                            GUI.AddToLog("The registration has been successful.");

                        } else {
                            GUI.AddToLog("This username already exists!");
                            ToClient.SetSingleData("UsernameExists");
                        }
                        //Sends back the to client object
                        ToClientStream.writeObject(ToClient);
                        GUI.AddToLog("Added new user: " + UsersInfo.get(0));
                        break;
                    }
                    //Upload new song
                    case "UPLOAD_SONG": {
                        GUI.AddToLog("Uploading a song...");
                        //Retreives the song information stored inside the datapacket
                        ArrayList SongInformation = InFromClient.GetArray();
                        //Writes the information to file
                        new File("media/music").mkdirs();
                        new File("media/albums").mkdirs();
                        String FileName = SongInformation.get(2) + "," + SongInformation.get(3);
                        File MusicDirectory = new File("media/music/" + FileName + ".mp3");
                        File PhotoDirectory = new File("media/albums/" + FileName + ".png");
                        //upload the song 
                        byte[] Song = (byte[]) InFromClient.GetByteData();
                        FileOutputStream SongOut = new FileOutputStream(MusicDirectory);
                        SongOut.write(Song);
                        byte[] CoverPhoto = (byte[]) InFromClient.GetSecondData();
                        FileOutputStream PhotoOut = new FileOutputStream(PhotoDirectory);
                        PhotoOut.write(CoverPhoto);
                        user.addSong(SongInformation);
                        user.addPost(SongInformation);
                        GUI.AddToLog(SongInformation.get(0) + " Uploaded a new song: " + FileName);
                        break;
                    }
                    //Upload new post
                    case "UPLOAD_POST": {
                        GUI.AddToLog("Uploading a post...");
                        //Retreives the post information from the client
                        user.addPost(InFromClient.GetArray());
                        ToClient.SetCommand("UPLOAD_POST");
                        ToClient.SetSingleData("Added Post");
                        //send the datapacket back to the client
                        ToClientStream.writeObject(ToClient);
                        //logs which client made a post
                        GUI.AddToLog(InFromClient.GetArray().get(0) + " made a new post");
                        break;
                    }
                    //Get My Friends
                    case "GET_FRIENDS": {
                        GUI.AddToLog("Get friends, request made...");
                        //retrieves the users friends and stores them in an array
                        ArrayList<String> UsersFriends = user.GetUsersFriends(InFromClient.GetData());
                        ToClient.SetCommand("GET_FRIENDS");
                        ToClient.SetArray(UsersFriends);
                        ToClientStream.writeObject(ToClient);
                        //logs which client requested their friends list
                        GUI.AddToLog(InFromClient.GetData() + " requested to view all friends");
                        break;
                    }
                    //Get Active Friends
                    case "GET_ACTIVE_FRIENDS": {
                        GUI.AddToLog("Get active friends, request made...");
                        //retrieves the users friends and stores them in an array
                        ArrayList<String> Friends = user.GetUsersFriends(InFromClient.GetData());
                        //creates a new array which stores onnly the active friends
                        ArrayList<String> ActiveFriends = GetActiveFriends(Friends);
                        ToClient.SetCommand("GET_ACTIVE_FRIENDS");
                        ToClient.SetArray(ActiveFriends);
                        ToClientStream.writeObject(ToClient);
                        //logs which client requested to view thier active friends
                        GUI.AddToLog("Getting " + InFromClient.GetData() + " active friends");
                        break;
                    }
                    //New Friend Request
                    case "NEW_FRIEND_REQUEST": {
                        GUI.AddToLog("New friend request being created...");
                        //retrieves the users from the client
                        ArrayList<String> Users = InFromClient.GetArray();
                        //checks to see if a user exists
                        boolean UsernameExists = user.DoesUsernameExist(Users.get(1).toString());
                        ToClient.SetCommand("NEW_FRIEND_REQUEST");
                        if (UsernameExists == true) {
                            //if the user exists...
                            boolean AlreadyFriends = user.AlreadyFriends(Users);
                            if (AlreadyFriends == false) {
                                //...and theyre not already friends
                                ToClient.SetSingleData("Exists");
                                //creates a new friend request
                                user.NewFriendRequest(Users);
                                //logs which user sent a friend request and which user receieved it 
                                GUI.AddToLog(Users.get(0) + " sent " + Users.get(1) + " a friends request");
                            } else {
                                //..otherwise they are already friends
                                ToClient.SetSingleData("AlreadyFriends");
                                //logs which user sent a friend request and which user receieved it 
                                GUI.AddToLog(Users.get(0) + " tried to send " + Users.get(1) + " a friend request, but they are already friends");
                            }
                        } else {
                            //tried to friend request a ghost
                            ToClient.SetSingleData("Doesnt");
                            //logs which user sent a friend request and which user receieved it 
                            GUI.AddToLog(Users.get(0) + " tried to send a friend request to a none existing user");
                        }
                        //sends the datapacket to the client
                        ToClientStream.writeObject(ToClient);
                        break;
                    }
                    //Get Friend Requests
                    case "GET_FRIEND_REQUESTS": {
                        GUI.AddToLog("Get friend requests, request made...");
                        //Retrieves the users friend requests 
                        ArrayList<String> UsersFriendRequests = user.GetUsersFriendRequests(InFromClient.GetData());
                        ToClient.SetCommand("GET_FRIEND_REQUESTS");
                        ToClient.SetArray(UsersFriendRequests);
                        //sends the datapacket to the client
                        ToClientStream.writeObject(ToClient);
                        //logs which client requested to see their pending friend requests
                        GUI.AddToLog(InFromClient.GetData() + " reqested to see their friend requests");
                        break;
                    }
                    //Get Users based on Prefernces
                    case "GET_USERS_ON_PREFERENCE": {
                        GUI.AddToLog("Get users preference filter, request made...");
                        //retrieves the users preferences
                        ArrayList<String> Users = user.GetUsernamesOnPreferences(InFromClient.GetData());
                        ToClient.SetCommand("GET_USERS_ON_PREFERENCE");
                        ToClient.SetArray(Users);
                        //sends the datapacket to the client
                        ToClientStream.writeObject(ToClient);
                        //logs which client requested to view users based on a preference
                        GUI.AddToLog(ip + " requested to get users based on preferences");
                        break;
                    }
                    //Accept Friend Request
                    case "ACCEPT_FRIEND_REQUEST": {
                        GUI.AddToLog("Request to accept a friend made...");
                        //accepts the friend request
                        user.AcceptFriendRequest(InFromClient.GetArray());
                        ToClient.SetCommand("ACCEPT_FRIEND_REQUEST");
                        ToClient.SetSingleData("Accepted");
                        //sends the datapacket to the client
                        ToClientStream.writeObject(ToClient);
                        //logs which client accepted the friend request and from who
                        GUI.AddToLog(InFromClient.GetArray().get(0) + " accepted " + InFromClient.GetArray().get(1) + " friend request");
                        break;
                    }
                    //Decline Friend Request
                    case "DECLINE_FRIEND_REQUEST": {
                        GUI.AddToLog("Request to decline a friend made...");
                        //declines the friend request
                        user.DeclineFriendRequest(InFromClient.GetArray());
                        //Datapacket Reply = new Datapacket();
                        ToClient.SetCommand("DECLINE_FRIEND_REQUEST");
                        ToClient.SetSingleData("Declined");
                        //sends the datapacket to the client
                        ToClientStream.writeObject(ToClient);
                        //logs which client declined the friend request and from who
                        GUI.AddToLog(InFromClient.GetArray().get(0) + " declined " + InFromClient.GetArray().get(1) + " friend request");
                        break;
                    }
                    //Get My Songs
                    case "GET_MY_SONGS": {
                        GUI.AddToLog("Request to retrieve a users songs made...");
                        //retreives all the users songs
                        ArrayList<String> MySongs = user.GetUserSongs(InFromClient.GetData());
                        ToClient.SetCommand("GET_MY_SONGS");
                        ToClient.SetArray(MySongs);
                        //sends the datapacket to the client
                        ToClientStream.writeObject(ToClient);
                        //logs which client requested to view their songs
                        GUI.AddToLog(ip + " requests to see " + InFromClient.GetData() + " songs");
                        break;
                    }
                    //Delete Friend
                    case "DELETE_FRIEND": {
                        GUI.AddToLog("Request to delete a friend made...");
                        //deletes a friend
                        user.RemoveFriend(InFromClient.GetArray());
                        ToClient.SetCommand("DELETE_FRIEND");
                        ToClient.SetSingleData("Removed");
                        //sends the datapacket to the client
                        ToClientStream.writeObject(ToClient);
                        //logs which client deleted a friend and who it was
                        GUI.AddToLog(InFromClient.GetArray().get(0) + "removed " + InFromClient.GetArray().get(1) + " as a friend");
                        break;
                    }
                    //Get user details
                    //NEEDS FIXING
                    case "GET_USER_DETAILS": {
                        GUI.AddToLog("Request to view user details made...");
                        //retrieves the users data from the client 
                        String Username = InFromClient.GetData();
                        Datapacket UserInformation = new Datapacket();
                        //NEEDS FIXING HERE * NEED TO RETREIVE FROM FILE
                        ArrayList<String> UsersDetails = user.GetUsersDetails(Username);
                        ArrayList<String> UserSongs = user.GetUserSongs(Username);
                        //creates a multi dimensional array
                        ArrayList<ArrayList<String>> UsersInfo = new ArrayList();
                        //stores the user details
                        UsersInfo.add(UsersDetails);
                        //stores the users songs
                        UsersInfo.add(UserSongs);
                        UserInformation.SetCommand("GET_USER_DETAILS");
                        UserInformation.SetMultipleArray(UsersInfo);
                        //retrieves the users photo from file
                        File PhotoDirectory = new File("media/profiles/" + Username + ".png");
                        FileInputStream UserPicture = new FileInputStream(PhotoDirectory);
                        byte[] buffer = new byte[UserPicture.available()];
                        UserPicture.read(buffer);
                        UserInformation.SetFirstByte(buffer);
                        //sends the datapacket to the client
                        ToClientStream.writeObject(UserInformation);
                        //logs which user is recieving the information
                        GUI.AddToLog("Sending " + Username + " details and songs to " + ip);
                        break;
                    }
                    //Get Posts
                    case "GET_POSTS": {
                        GUI.AddToLog("Request to view posts made...");
                        //retrieves the users data
                        String Username = InFromClient.GetData();
                        ArrayList<String> Friends = user.GetUsersFriends(Username);
                        //Add own username to retrieve own posts
                        Friends.add(InFromClient.GetData());
                        ArrayList<String> UserPosts = user.GetFriendsPosts(Friends);
                        Datapacket FriendsPosts = new Datapacket();
                        FriendsPosts.SetCommand("GET_POSTS");
                        FriendsPosts.SetArray(UserPosts);
                        //sends the datapacket to the client
                        ToClientStream.writeObject(FriendsPosts);
                        //logs which user requested to view posts
                        GUI.AddToLog(Username + " requests to see posts");
                        break;
                    }
                    //Download Song
                    case "DOWNLOAD_SONG": {
                        GUI.AddToLog("Request to download a song made...");
                        //creates a new datapacket
                        Datapacket SongData = new Datapacket();
                        //retrieves all the song data
                        File MusicDirectory = new File("media/music/" + InFromClient.GetData() + ".mp3");
                        File PhotoDirectory = new File("media/albums/" + InFromClient.GetData() + ".png");
                        FileInputStream SongFile = new FileInputStream(MusicDirectory);
                        byte[] buffer = new byte[SongFile.available()];
                        SongFile.read(buffer);
                        FileInputStream PhotoFile = new FileInputStream(PhotoDirectory);
                        byte[] buffer2 = new byte[PhotoFile.available()];
                        PhotoFile.read(buffer2);
                        SongData.SetCommand("DOWNLOAD_SONG");
                        SongData.SetFirstByte(buffer);
                        SongData.SetSecondByte(buffer2);
                        //send the datapacket to the client
                        ToClientStream.writeObject(SongData);
                        //logs which user is downloading the song data.
                        GUI.AddToLog(ip + " is downloading the song and cover photo for " + InFromClient.GetData());
                        break;
                    }
                    default:
                        //log an invalid command sent by a client
                        GUI.AddToLog(ip + " has sent an invalid command");
                        break;
                }
            }
            //close the streams
            FromClientStream.close();
            ToClientStream.close();
        } catch (IOException | ClassNotFoundException e) {
            //log any errors
            GUI.AddToLog(e.getMessage());
        }
    }
    //adds an active user
    public void addActiveUser(String username) {
        try {
            onlineUsers.add(username);
            GUI.AddToLog("User: " + username + " is now active.");

        } catch (Exception e) {
            //logs any errors
            System.out.println("Could not add user to online register : " + e);
            GUI.AddToLog("Could not add user to online register! : " + e);
        }
    }
    //removes an active user, for example when logging our
    public String removeActiveUser(String username) {
        int userIndex = onlineUsers.indexOf(username);
        try {
            onlineUsers.remove(userIndex);
            GUI.AddToLog("Removed active user: " + username);
        } catch (Exception e) {
            //logs any errors
            System.out.println("Could not remove user from online register : " + e);
            GUI.AddToLog("Could not remove user from online register! : " + e);
        }
        return "success";
    }
    //retrieves an array of all the user who are active
    public ArrayList<String> GetActiveFriends(ArrayList<String> Friends) {
        ArrayList<String> ActiveFriends = new ArrayList();
        String CurrentFriend = "";
        for (int i = 0; i < Friends.size(); i++) {
            CurrentFriend = Friends.get(i);
            if (onlineUsers.contains(CurrentFriend)) {
                GUI.AddToLog("Found Active Friend");
                ActiveFriends.add(CurrentFriend);
            }
        }
        return ActiveFriends;
    }
}
