/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datapacket;

/**
 *
 * @author jakew
 */
import java.io.Serializable;
import java.util.ArrayList;

//This contains requests and replies packaged with the data.
public class Datapacket implements Serializable {
    //This string is used to identify what process the data should go through.
    private String Command;
    //This string is used to pass single quantities of data.
    private String StringData;
    //This string stores the username of the person who is sending/reciving the datapacket.
    private String UserName;
    //This is a standard array.
    private ArrayList MultipleData;
    //This is an array which stores other arrays.
    private ArrayList<ArrayList<String>> MultiDimensionArrayList;
    //This is an array of bytes which is used to send data. For example a file.
    private byte[] FirstByteArray;
    //This is the same as the first byte array, but this is used when you want to send another file in the same datapacket.
    private byte[] SecondByteArray;
    
    //Get and Set the datapacket variables.
    public void SetCommand(String Command) {
        this.Command = Command;
    }

    public String GetCommand() {
        return Command;
    }
    
    public void SetUserName(String userName) {
        this.UserName = userName;
    }

    public String GetUserName() {
        return UserName;
    }

    public void SetStringData(String Data) {
        this.StringData = Data;
    }

    public String GetData() {
        return StringData;
    }

    public void SetArray(ArrayList<String> MultiData) {
        this.MultipleData = MultiData;
    }

    public ArrayList<String> GetArray() {
        return MultipleData;
    }
    
    public void SetMultipleArray(ArrayList<ArrayList<String>> MultipleArrayData) {
      this.MultiDimensionArrayList = MultipleArrayData;
    }
    
    public ArrayList<ArrayList<String>> GetMultipleArray() {
        return MultiDimensionArrayList;
    }

    public void SetFirstByte(byte[] FirstByte) {
        this.FirstByteArray = FirstByte;
    }

    public byte[] GetByteData() {
        return FirstByteArray;
    }

    public void SetSecondByte(byte[] SecondByte) {
        this.SecondByteArray = SecondByte;
    }

    public byte[] GetSecondData() {
        return SecondByteArray;
    }
}

