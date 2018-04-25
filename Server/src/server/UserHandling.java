/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Theo
 */
public class UserHandling {

    ServerGUI GUI;

    public void createUser(ArrayList userInfo) throws FileNotFoundException, UnsupportedEncodingException, IOException {

        // Assign the user details from the given array
        String registerUsername = userInfo.get(0).toString();
        String registerPassword = userInfo.get(1).toString();
        String registerFirstName = userInfo.get(2).toString();
        String registerLastName = userInfo.get(3).toString();
        String registerEmail = userInfo.get(4).toString();

        // Create new directories if needed
        String preferences = "";
        new File("users/preferences").mkdirs();
        new File("users/" + registerUsername).mkdirs();
        List<String> userList = new ArrayList<String>();

        // Write the name of the user to their selected preferences, creating a file if needed
        for (int i = 5; i < userInfo.size(); i++) {

            File newPrefFile = new File("users/preferences/" + userInfo.get(i) + ".txt");
            newPrefFile.createNewFile();

            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("users/preferences/" + userInfo.get(i) + ".txt", true)));
            writer.write(registerUsername + "%");
            writer.close();
        }

        for (int i = 5; i < userInfo.size(); i++) {
            preferences = preferences + userInfo.get(i) + ",";
        }
        preferences = preferences.substring(0, preferences.length() - 1);

        // Add details to a list
        userList.add(registerFirstName);

        userList.add(registerLastName);

        userList.add(registerEmail);

        userList.add(registerPassword);

        userList.add(preferences);

        File newUserFile = new File("users/" + registerUsername + "/" + registerUsername + ".txt");
        newUserFile.createNewFile();
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(newUserFile, true)));

        for (String str : userList) {
            writer.println(str);
        }
        writer.close();
    }

    public void addSong(ArrayList<String> songInformation) throws IOException {

        String username = songInformation.get(0);
        String artistName = songInformation.get(2);
        String songName = songInformation.get(3);
        String genre = songInformation.get(4);

        File musicFile = new File("users/" + username + "songs.txt");
        musicFile.createNewFile();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(musicFile, true)));
        writer.println(username + "," + artistName + "," + songName + "," + genre);
        writer.close();

    }

    public void addPost(ArrayList<String> postInformation) throws IOException {

        String username = postInformation.get(0);
        String postType = postInformation.get(1);
        String message = "", userMood = "";
        new File("media/posts").mkdirs();
        File newUserPost = new File("media/posts/posts.txt");
        newUserPost.createNewFile();

        if ("SongUpload".equals(postType)) {

            message = postInformation.get(2) + "," + postInformation.get(3);

            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(newUserPost, true)));
            writer.println(username + "," + postType + "," + message);
            writer.close();

        } else if ("TextPost".equals(postType)) {

            message = postInformation.get(2);
            userMood = postInformation.get(3);

            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(newUserPost, true)));
            writer.println(username + "," + postType + "," + message);
            writer.close();
        }
    }

    public boolean DoesUsernameExist(String username) {

        boolean Exists = false;
        File userFile = new File("users/" + username);
        if (userFile.exists() && userFile.isDirectory()) {
            Exists = true;
        } else {
            Exists = false;
        }
        return Exists;
    }

    public boolean AlreadyFriends(ArrayList<String> users) throws IOException {
        boolean AlreadyFriends = false;
        //User[0] to retrieve friends for
        //if User[1] is in the ArrayList then they are already friends
        ArrayList<String> AllFriends = GetUsersFriends(users.get(0));

        for (int i = 0; i < AllFriends.size(); i++) {
            if (users.get(1).equals(AllFriends.get(i))) {
                AlreadyFriends = true;
            }
        }

        ArrayList<String> FriendRequests = GetUsersFriendRequests(users.get(0));

        for (int i = 0; i < FriendRequests.size(); i++) {
            if (users.get(1).equals(FriendRequests.get(i))) {
                AlreadyFriends = true;
            }
        }

        return AlreadyFriends;
    }

    public ArrayList<String> GetUsersFriends(String username) throws FileNotFoundException, IOException {

        File friendFile = new File("users/" + username + "/friends.txt");
        friendFile.createNewFile();

        BufferedReader reader = new BufferedReader(new FileReader(friendFile));
        String line;
        List<String> friendList = new ArrayList<String>();

        while ((line = reader.readLine()) != null) {
            friendList.add(line);
        }
        reader.close();

        ArrayList<String> returnList = new ArrayList(friendList);
        return returnList;
    }

    public ArrayList<String> GetUsersFriendRequests(String username) throws FileNotFoundException, IOException {

        File newUserFile = new File("users/" + username + "/friendRequests.txt");
        newUserFile.createNewFile();

        BufferedReader reader = new BufferedReader(new FileReader(newUserFile));
        String line;
        List<String> friendList = new ArrayList<String>();

        while ((line = reader.readLine()) != null) {
            friendList.add(line);
        }
        reader.close();

        ArrayList<String> FriendsList = new ArrayList(friendList);
        return FriendsList;
    }

    public void NewFriendRequest(ArrayList<String> Users) throws FileNotFoundException, IOException {

        String firstUser = Users.get(0);
        String secondUser = Users.get(1);

        File firstUserFile = new File("users/" + firstUser + "/friendRequests.txt");
        File secondUserFile = new File("users/" + secondUser + "/friendRequests.txt");
        firstUserFile.createNewFile();
        secondUserFile.createNewFile();

        PrintWriter firstWriter = new PrintWriter(new BufferedWriter(new FileWriter(firstUserFile, true)));
        PrintWriter secondWriter = new PrintWriter(new BufferedWriter(new FileWriter(secondUserFile, true)));

        firstWriter.println(secondUser);
        firstWriter.close();

        secondWriter.println(firstUser);
        secondWriter.close();
    }

    public ArrayList<String> GetUserSongs(String username) throws FileNotFoundException, IOException {

        File newUserFile = new File("users/" + username + "/songs.txt");
        newUserFile.createNewFile();

        BufferedReader reader = new BufferedReader(new FileReader("users/" + username + "/songs.txt"));
        String line;
        List<String> songList = new ArrayList<String>();

        while ((line = reader.readLine()) != null) {
            String split[] = line.split(",");
            songList.add(split[0]);
        }

        ArrayList<String> returnList = new ArrayList(songList);
        return returnList;
    }

    public void AcceptFriendRequest(ArrayList<String> users) throws FileNotFoundException, IOException {

        String firstUser = users.get(0);
        String secondUser = users.get(1);

        File firstUserFile = new File("users/" + firstUser + "/friends.txt");
        File secondUserFile = new File("users/" + secondUser + "/friends.txt");
        firstUserFile.createNewFile();
        secondUserFile.createNewFile();

        PrintWriter firstWriter = new PrintWriter(new BufferedWriter(new FileWriter(firstUserFile, true)));
        PrintWriter secondWriter = new PrintWriter(new BufferedWriter(new FileWriter(secondUserFile, true)));

        firstWriter.println(secondUser);
        firstWriter.close();

        secondWriter.println(firstUser);
        secondWriter.close();
    }

    public void DeclineFriendRequest(ArrayList<String> users) throws FileNotFoundException, IOException {

        String firstUser = users.get(0);
        String secondUser = users.get(1);

        File firstUserFile = new File("users/" + firstUser + "/friendRequest.txt");
        File secondUserFile = new File("users/" + secondUser + "/friendRequest.txt");
        firstUserFile.createNewFile();
        secondUserFile.createNewFile();

        BufferedReader firstReader = new BufferedReader(new FileReader(firstUserFile));
        BufferedReader secondReader = new BufferedReader(new FileReader(secondUserFile));
        String line1;
        String line2;

        List<String> firstList = new ArrayList<String>();
        while ((line1 = firstReader.readLine()) != null) {
            firstList.add(line1);
        }
        firstReader.close();

        List<String> secondList = new ArrayList<String>();
        while ((line2 = secondReader.readLine()) != null) {
            secondList.add(line2);
        }
        secondReader.close();

        ArrayList<String> firstFriendList = new ArrayList(firstList);
        ArrayList<String> secondFriendList = new ArrayList(secondList);

        firstFriendList.remove(secondUser);
        secondFriendList.remove(firstUser);

        PrintWriter firstWriter = new PrintWriter(new BufferedWriter(new FileWriter(firstUserFile, true)));
        PrintWriter secondWriter = new PrintWriter(new BufferedWriter(new FileWriter(secondUserFile, true)));

        for (int i = 0; i < firstFriendList.size() - 1; i++) {
            firstWriter.println(firstFriendList.get(i));
        }
        firstWriter.close();

        for (int i = 0; i < secondFriendList.size() - 1; i++) {
            secondWriter.println(secondFriendList.get(i));
        }
        secondWriter.close();
    }

    public void RemoveFriend(ArrayList<String> users) throws FileNotFoundException, IOException {

        String firstUser = users.get(0);
        String secondUser = users.get(1);

        File firstUserFile = new File("users/" + firstUser + "/friends.txt");
        File secondUserFile = new File("users/" + secondUser + "/friends.txt");
        firstUserFile.createNewFile();
        secondUserFile.createNewFile();

        BufferedReader firstReader = new BufferedReader(new FileReader(firstUserFile));
        BufferedReader secondReader = new BufferedReader(new FileReader(secondUserFile));
        String line1;
        String line2;

        List<String> firstList = new ArrayList<String>();
        while ((line1 = firstReader.readLine()) != null) {
            firstList.add(line1);
        }
        firstReader.close();

        List<String> secondList = new ArrayList<String>();
        while ((line2 = secondReader.readLine()) != null) {
            secondList.add(line2);
        }
        secondReader.close();

        ArrayList<String> firstFriendList = new ArrayList(firstList);
        ArrayList<String> secondFriendList = new ArrayList(secondList);

        firstFriendList.remove(secondUser);
        secondFriendList.remove(firstUser);

        PrintWriter firstWriter = new PrintWriter(new BufferedWriter(new FileWriter(firstUserFile, true)));
        PrintWriter secondWriter = new PrintWriter(new BufferedWriter(new FileWriter(secondUserFile, true)));

        for (int i = 0; i < firstFriendList.size() - 1; i++) {
            firstWriter.println(firstFriendList.get(i));
        }
        firstWriter.close();

        for (int i = 0; i < secondFriendList.size() - 1; i++) {
            secondWriter.println(secondFriendList.get(i));
        }
        secondWriter.close();
    }

    public ArrayList<String> GetUsernamesOnPreferences(String musicPref) throws IOException {

        File preferenceFile = new File("users/preferences/" + musicPref + ".txt");
        preferenceFile.createNewFile();
        String username;

        BufferedReader reader = new BufferedReader(new FileReader(preferenceFile));
        String line;

        List<String> preferenceList = new ArrayList<String>();
        while ((line = reader.readLine()) != null) 
        {
            username = line.substring(0,line.length()-1);
            preferenceList.add(username);
        }

        ArrayList<String> returnList = new ArrayList(preferenceList);
        return returnList;
    }

    public ArrayList<String> GetFriendsPosts(ArrayList<String> friends) throws IOException {

        String username = "";
        new File("media/posts").mkdirs();
        File postsFile = new File("media/posts/posts.txt");
        postsFile.createNewFile();

        BufferedReader reader = new BufferedReader(new FileReader("media/posts/posts.txt"));
        String line;

        List<String> postsList = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {

            username = line.split(",")[0];
            for (int i = 0; i < friends.size(); i++) {
                if (friends.get(i).equals(username)) {
                    postsList.add(line);
                }
            }
        }

        ArrayList<String> returnList = new ArrayList(postsList);
        return returnList;
    }

}
