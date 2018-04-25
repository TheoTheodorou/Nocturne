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

    private String Command;
    private String SingleData;
    private String UserName;
    private ArrayList MultipleData;
    private ArrayList<ArrayList<String>> MultipleArrayData;
    private byte[] FirstByteArray;
    private byte[] SecondByteArray;
    
    
    

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

    public void SetSingleData(String Data) {
        this.SingleData = Data;
    }

    public String GetData() {
        return SingleData;
    }

    public void SetArray(ArrayList<String> MultiData) {
        this.MultipleData = MultiData;
    }

    public ArrayList<String> GetArray() {
        return MultipleData;
    }

    public void SetMultipleArray(ArrayList<ArrayList<String>> MultipleArrayData) {
        this.MultipleArrayData = MultipleArrayData;
    }

    public ArrayList<ArrayList<String>> GetMultipleArray() {
        return MultipleArrayData;
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

