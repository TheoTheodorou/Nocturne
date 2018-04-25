/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nocturne;

/**
 *
 * @author jakew
 */

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadTimer extends Thread{
    
    private Home form;
    private boolean Request;
    
    public ThreadTimer SetForm(Home screen)
    {
        this.form = screen;
        ThreadTimer PassBack = new ThreadTimer();
        return PassBack;
    }
    
    public void SetRequest (boolean request)
    {
        this.Request = request;
    }
    
    @Override
    public void run() 
    {
        while(Request == true)
        {
            try {
                  form.RefreshOnlineFriends();
                  form.RefreshFriendRequests();
                  form.RefreshPosts();
                  form.RefreshSongs();
                  form.RefreshFriends();
                  Thread.sleep(2000);
            } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                  Logger.getLogger(ThreadTimer.class.getName()).log(Level.SEVERE, null, ex);
            }
                    
        }
    }
    
}