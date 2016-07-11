package com.example.dibyojyoti.mytest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


import org.eclipse.paho.client.mqttv3.util.Strings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class connectMQTT extends Service {

    // Service Binding with Activity MapActivity
    IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        return super.openFileInput(name);
    }

    public class LocalBinder extends Binder {
        public connectMQTT getServerInstance() {
            return connectMQTT.this;
        }
    }

    public String getTime() {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return mDateFormat.format(new Date());
    }

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;
    MapsActivity mapsActivityinstance = null;
    MqttConnection mqttConnection = null;
    String myuniqueID;
    public connectMQTT() {
    }

    /** Called when the service is being created. */

    @Override
    public void onCreate() {
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Get the user name and number.
        Bundle bundle = intent.getExtras();
        myuniqueID = bundle.getString("mytopic");
        String name= bundle.getString("myname");

        // Start MQTT Pub/Sub
        mqttConnection = new MqttConnection();
        try {
            mqttConnection.createConnection(myuniqueID, name);
        }catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return mStartMode;
    }

    /**
     *  This method is called from MapsActivity whenever user location is updated.
     * @return the list of friends and their friends updated location.
     */
    public ArrayList<Myfriend> getOthersData(){
        if(mqttConnection != null){
            return mqttConnection.getOthersData();
        }
        else{
            return null;
        }
    }

    /**
     * This method publishes the new location of user to all friends.
     * @param latitude
     * @param longitude
     */
    public void publishnewlocation(double latitude, double longitude){
        if(mqttConnection != null)
            mqttConnection.publishlocation(latitude,longitude, this);

    }
    public void connectiontoMytopic(){
        if(mqttConnection != null)
            mqttConnection.connectiontoMytopic();

    }


    /**
     * This method publishes the help message to one or two friends based on lat lang and trust level.
     * @param msg
     * @param myfriend
     */
    public void publishHelpMsg(String msg, Myfriend myfriend, double latitude, double longitude){
        if(mqttConnection != null)
            mqttConnection.publishHelpMsg(msg, myfriend, this, latitude,longitude);

    }

    /** A client is binding to the service with bindService() */

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        try {
            if(mqttConnection != null)
                mqttConnection.closeConnection();

        }catch (IOException e){
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "service is stopped" , Toast.LENGTH_SHORT).show();

    }
}
