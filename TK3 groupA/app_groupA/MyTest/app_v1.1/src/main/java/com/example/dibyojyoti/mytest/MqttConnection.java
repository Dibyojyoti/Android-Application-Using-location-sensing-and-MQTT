package com.example.dibyojyoti.mytest;
/**
 * Created by Dibyojyoti on 6/15/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.example.dibyojyoti.mytest.DbHelperFrndList.Frnd;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MqttConnection implements MqttCallback{
    static String topic = "";                                       //The topic user is subscribed to i.e Phone number
    static int qos             = 0;                                 //Quality of service for messages
    static int clientqos       = 0;                                 //Quality of service for client
    static String broker       = "tcp://broker.hivemq.com:1883";    //Public Broker
	//static String broker       = "tcp://test.mosquitto.org:1883";    //Public Broker
    static String clientId_pub;                                     //MQTTClient
    static String myname ="";                                       //User name

    DbHelperFrndList dbHelperFrndList;                              //For connecting to the database
    Context service;

    //List of all user friends and there friends.
    static ArrayList<Myfriend> myfriends = new ArrayList<Myfriend>();
    MemoryPersistence persistence = new MemoryPersistence();
    //MQTT Client
    MqttClient sampleClient = null;

    /**
     * Method invoked from MapsActivity to get friends details.
     * @return Arraylist of friends
     */
    public ArrayList<Myfriend> getOthersData(){
        return myfriends;
    }

    /*
	 * (Re-)connect to the message broker
	 */
    private boolean connectToBroker()
    {
        try
        {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false); //The connection is persistent.
            if(!sampleClient.isConnected()){
                sampleClient.connect(connOpts);
            }
            return true;
        }
        catch (MqttException e)
        {
            return false;
        }
    }


    /*
     * Send a request to the message broker to be sent messages published with
     *  the specified topic name. Wildcards are allowed.
     */
    private void subscribetotopic()
    {
        try
        {
            if(sampleClient.isConnected())
               sampleClient.subscribe(topic.trim(), clientqos);
        }
        catch (IllegalArgumentException e)
        {
            Log.e("mqtt", "subscribe failed - illegal argument", e);
        } catch (MqttException e) {
            Log.e("mqtt", "subscribe failed - MqttException", e);
        }

    }

    /**
     *  Initialising MQTTClient, connecting to broker and subscribing to his number.
     * @param uniqueID  user Phone number
     * @param name      user name
     * @throws IOException
     */
    public void createConnection(String uniqueID, String name) throws IOException {
        try {
            myname = name;
            if(sampleClient == null){
                topic = uniqueID;
                clientId_pub = topic + "_sender";
                sampleClient = new MqttClient(broker, clientId_pub, persistence);
            }else if(topic.trim() != uniqueID.trim()) {
                topic = uniqueID;
                clientId_pub = topic + "_sender";
                sampleClient = new MqttClient(broker, clientId_pub, persistence);
            }
            MqttConnectOptions connOpts = new MqttConnectOptions();
            sampleClient.setCallback(this);
            connOpts.setCleanSession(false); //asha
            if(!sampleClient.isConnected()) {
                sampleClient.connect(connOpts);
                sampleClient.subscribe(topic.trim(), clientqos);
            }
            //Seding Hello message
            MqttMessage message = new MqttMessage();
            String json = "{ \"msgtype\": \"Hello\"" + ", \"from\": \"" + topic+"\"}";
            message.setPayload(json.getBytes());
            message.setQos(qos);
            if(sampleClient.isConnected()) {
                sampleClient.publish(topic.trim(), message);
            }
            else{
                if(connectToBroker()){
                    subscribetotopic();
                    sampleClient.publish(topic.trim(), message);
                }
            }

        } catch(MqttException me) {
            Log.d("MQTT", me.toString());
            me.printStackTrace();
        }
    }


    public void publishHelpMsg(String msg, Myfriend myfriend, connectMQTT context, double latitude, double longitude){
        try {
            MqttMessage message= new MqttMessage();
            service = context;
            String json = "{ \"msgtype\": \"help\"" + ",\"msg\": \"" + msg + "\", \"from\": \"" + topic.trim()+"\", \"name\": \"" + myname + "\", \"latitude\": \"" + latitude + "\", \"longitude\":\" " + longitude + "\"}";
            message.setPayload(json.getBytes());
            message.setQos(qos);
            //message.setQos(1);
            if(!sampleClient.isConnected()) {
                if(connectToBroker()){
                    subscribetotopic();
                }
            }
            sampleClient.publish(myfriend.getMobNumber().trim(), message);
        } catch(MqttException me) {
            Log.d("MQTT", me.toString());
            me.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    /**
     * This method is called from connectMQTT service to publish new location of user to all friends
     * @param latitude
     * @param longitude
     * @param context
     */
    public void publishlocation(double latitude, double longitude, connectMQTT context){

        try {
            dbHelperFrndList = new DbHelperFrndList(context);
            service = context;
            HashMap<String, Frnd> friendslist = new HashMap<String, Frnd>();
            friendslist = dbHelperFrndList.getAllContacts();
            Iterator myVeryOwnIterator = friendslist.keySet().iterator();
            String friendsoffriend = "\"friends\":[";
            int i=1;
            if (myfriends != null) {
                for (Myfriend friend : myfriends) { {
                    if(i == myfriends.size())
                        friendsoffriend += "{ \"latitude\": \"" +friend.getLatLng().latitude +"\", \"longitude\": \"" + friend.getLatLng().longitude + "\"}";
                    else
                        friendsoffriend += "{ \"latitude\": \"" +friend.getLatLng().latitude +"\", \"longitude\": \"" + friend.getLatLng().longitude + "\"},";
                    i++;
                }
                }
            }
            friendsoffriend += "]";

            MqttMessage message= new MqttMessage();
            String json = "{ \"msgtype\": \"location\"" + ",\"latitude\": \"" + latitude + "\", \"longitude\": \"" + longitude + "\", \"from\": \"" + topic+"\", \"name\": \"" + myname + "\", "+ friendsoffriend+"}";
            message.setPayload(json.getBytes());
            message.setQos(qos);

            /*if(!sampleClient.isConnected()) {
                if(connectToBroker()){
                    subscribetotopic();
                }
            }*/
            while (myVeryOwnIterator.hasNext()) {
                if(!sampleClient.isConnected()) {
                    if(connectToBroker()){
                        subscribetotopic();
                    }
                }
                String key = (String) myVeryOwnIterator.next();
                Frnd frnd = (Frnd) friendslist.get(key);
                sampleClient.publish(key.trim(), message);
                sampleClient.disconnect();
            }
        } catch(MqttException me) {
            Log.d("MQTT", me.toString());
            me.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    //Callbacks to track message published and delivery.
    @Override
    public void connectionLost(Throwable cause){
        //dont connect when connection lost somehow when it tries to connect before publishing
        //if there is already one connection it throws Connection lost error
        //as at a time only one connection is expected
        //instead before each publish we reconnect and after publish disconnect
        //and periodically we connect after we publish location so we will receive all messages
        //if(connectToBroker()){
        //    subscribetotopic();
        //}
    }
    //subscribe to own topic.
    public void connectiontoMytopic(){
        if(connectToBroker()){
            subscribetotopic();
        }
    }

    @Override
    public void messageArrived(String receivedtopic,MqttMessage message) throws Exception {
        JSONObject jo = new JSONObject(message.toString());
        if(jo.getString("msgtype").trim().equals("Hello")){
            Log.d("Hello message", message.toString());
        }
        String from = jo.getString("from").trim();
        String to = receivedtopic.trim();

        if( to.equals(topic.trim()) && jo.getString("msgtype").trim().equals("location")){
            JSONArray arraytemp = new JSONArray();
            if(!jo.getString("friends").trim().equals("[]"))
                arraytemp = jo.getJSONArray("friends");
            updatelocation(jo.getString("latitude").trim(), jo.getString("longitude").trim(),
                    jo.getString("name").trim(),jo.getString("from").trim() , arraytemp ) ;
        }

        if( to.equals(topic.trim()) && jo.getString("msgtype").trim().equals("help")){
            dbHelperFrndList = new DbHelperFrndList(service);
            //Toast.makeText(service, "You have a new message!" , Toast.LENGTH_LONG).show();

            dbHelperFrndList.insertMsg(from, jo.getString("name"), jo.getString("msg"), "R", "", "");
            dbHelperFrndList.insertFMsg(from, jo.getString("name"),  jo.getString("msg"), jo.getString("latitude"), jo.getString("longitude"));

        }
    }

    /**
     * Adding Freinds details and their freinds
     * @param latitude
     * @param longitude
     * @param name
     * @param contact
     * @param fof
     * @throws Exception
     */
    private void updatelocation(String latitude,String longitude,String name,String contact, JSONArray fof) throws Exception{

        Myfriend myfriend = new Myfriend(contact);
        Iterator<Myfriend> iterator = myfriends.iterator();
        while (iterator.hasNext()) {
            Myfriend mf = iterator.next();
            if (!mf.getMobNumber().equals(contact)) continue;
            else if (mf.getMobNumber().equals(contact))
            {
                iterator.remove();
            }
        }
        myfriend.setLatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        myfriend.setMobNumber(contact);
        myfriend.setName(name);
        if(fof != null){
            ArrayList<Myfriend> fofriend = new ArrayList<Myfriend>();

            for(int y=0; y < fof.length(); y++){

                JSONObject latlan =  new JSONObject(fof.get(y).toString());
                Myfriend temp = new Myfriend("0000");
                temp.setMobNumber("0000");
                temp.setName("");

                temp.setLatLng( Double.parseDouble(latlan.getString("latitude")), Double.parseDouble(latlan.getString("longitude")));
                temp.setFriendFriends(null);
                fofriend.add(temp);
            }
            myfriend.setFriendFriends(fofriend);
        }
        myfriends.add(myfriend);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token){
            Log.d("message delivered:","x");
    }

    public  void closeConnection() throws IOException {
        try {
            if(sampleClient.isConnected()){
                sampleClient.disconnect();
                sampleClient.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



}

