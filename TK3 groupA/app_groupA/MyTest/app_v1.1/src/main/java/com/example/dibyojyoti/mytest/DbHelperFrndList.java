package com.example.dibyojyoti.mytest;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.Cursor;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import android.content.ContentValues;
import android.util.Log;

/**
 * Class to access Database.
 * Created by Dibyojyoti on 6/19/2016.
 */
public class DbHelperFrndList extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyCircleDB.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_TLVL = "trustlvl";

    public static final String MSGS_TABLE_NAME = "msgs";
    public static final String MSGS_COLUMN_ID = "id";
    public static final String MSGS_COLUMN_MSGID = "msgid";
    public static final String MSGS_COLUMN_PHONE = "phone";
    public static final String MSGS_COLUMN_NAME = "name";
    public static final String MSGS_COLUMN_MSG = "message";
    public static final String MSGS_COLUMN_MSGTYPE = "type";
    public static final String MSGS_COLUMN_DATE = "date";
    public static final String MSGS_COLUMN_TIME = "time";

    public static final String FWDM_TABLE_NAME = "fmsg";
    public static final String FWDM_COLUMN_PHONE = "phone";
    public static final String FWDM_COLUMN_NAME = "name";
    public static final String FWDM_COLUMN_MSG = "message";
    public static final String FWDM_COLUMN_LAT = "latitude";
    public static final String FWDM_COLUMN_LON = "longitude";


    private HashMap hp;

    public DbHelperFrndList(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    /**
     * Creating Contacts and Messages table.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table "+CONTACTS_TABLE_NAME+
                        " (unique_id integer primary key, id text, name text, trustlvl text)"
        );
        db.execSQL(
                "create table "+MSGS_TABLE_NAME+
                        " (id integer primary key,msgid text,phone text,name text,message text,type text,date text,time text)"
        );
        db.execSQL(
                "create table "+FWDM_TABLE_NAME+
                        " (id integer primary key, phone text,name text,message text,latitude text,longitude text)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+CONTACTS_TABLE_NAME +")");
        db.execSQL("DROP TABLE IF EXISTS "+MSGS_TABLE_NAME+")");
        onCreate(db);
    }

    /**
     * Method to insert new contact details.
     * @param id
     * @param name
     * @param trustlvl
     * @return
     */
    public boolean insertContact  (String id, String name,String trustlvl)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_ID, id);
        contentValues.put(CONTACTS_COLUMN_NAME, name);
        contentValues.put(CONTACTS_COLUMN_TLVL, trustlvl);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    /**
     * Method to insert newly send message details.
     * @param phone
     * @param name
     * @param message
     * @param type
     * @param date
     * @param time
     * @return
     */
    public boolean insertMsg  (String phone, String name,String message,String type,String date,String time)
    {
        Log.d("Inside insertmsg","Inside insertmsg");
        Integer msgid =  Integer.parseInt(getlastMsgsId()) + 1;
        String currentdate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( new Date() );
        int hours = calendar.get( Calendar.HOUR_OF_DAY );
        int minutes = calendar.get( Calendar.MINUTE );
        String currenttime = hours + ":" + minutes;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("msgid", msgid.toString());
        contentValues.put("phone", phone);
        contentValues.put("name", name);
        contentValues.put("message", message);
        contentValues.put("type", type);
        contentValues.put("date", currentdate);
        contentValues.put("time", currenttime);
        db.insert(MSGS_TABLE_NAME, null, contentValues);

        return true;
    }

    public boolean insertFMsg  (String phone, String name,String message,String lat,String lon)
    {
        Log.d("Inside insertFmsg","Inside insertmsg");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FWDM_COLUMN_PHONE, phone);
        contentValues.put(FWDM_COLUMN_NAME, name);
        contentValues.put(FWDM_COLUMN_MSG, message);
        contentValues.put(FWDM_COLUMN_LAT, lat);
        contentValues.put(FWDM_COLUMN_LON, lon);
        db.insert(FWDM_TABLE_NAME, null, contentValues);
        return true;
    }

    /**
     * Method to get Selected friends data.
     * @param id
     * @return
     */
    public Cursor getDataContact(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where id="+id+"", null );
        return res;
    }

    /**
     * Method to get detials of selected message.
     * @param msgid
     * @return
     */
    public Cursor getDataMsg(String msgid){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+MSGS_TABLE_NAME+" where msgid="+msgid+"", null );
        return res;
    }

    /**
     * Method to get detials of message to be forwarded.
     * @return
     */
    public Cursor getDataFMsg(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+FWDM_TABLE_NAME+"" , null );
        return res;
    }

    /**
     * Method to get the number of rows in Contact table.
     * @return
     */
    public int numberOfRowsInContact(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }



    public String getlastMsgsId()
    {
        HashMap<String,DispMessage> array_list = new HashMap<String, DispMessage>();
        SQLiteDatabase db = DbHelperFrndList.this.getReadableDatabase();
        Cursor res = db.rawQuery( "select " + MSGS_COLUMN_MSGID+" from "+MSGS_TABLE_NAME+" WHERE "+MSGS_COLUMN_MSGID+" = (SELECT MAX("+MSGS_COLUMN_MSGID+") FROM "+MSGS_TABLE_NAME+ ")", null );
        Log.d("getlastMsgsId", "cursor"+ res.toString());
        String lastmssgid = "0";
        if(!(res.moveToFirst()) || res.getCount()==0)
            lastmssgid="0";
        else{
            res.moveToLast();
            lastmssgid = res.getString(0);
        }
        Log.d("getlastMsgsId", "lastmssgid"+ lastmssgid);
        return lastmssgid;
    }

    /**
     * Method to get the number of rows in message table.
     * @return
     */
    public int numberOfRowsInMsgs(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MSGS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact(String id, String name,String trustlvl)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("trustlvl", trustlvl);

        db.update("contacts", contentValues, "id = ? ", new String[] { id }  );
        return true;
    }

    /**
     * Method to delete contact.
     * @param id
     * @return
     */
    public Integer deleteContact (String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { id });
    }

    /**
     * Method to delete Message.
     * @param msgid
     * @return
     */
    public Integer deleteMessage (String msgid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(MSGS_TABLE_NAME,
                "msgid = ? ",
                new String[] { msgid });
    }

    /**
     * Method to delete message to forward
     * @param id
     * @return
     */
    public Integer deleteFMessage (String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(FWDM_TABLE_NAME,
                "id = ? ",
                new String[] { id });
    }

    /**
     * Method to get Trust level of Contact.
     * @param id
     * @return trustlevel
     */
    public String getContactsColumnTlvl (String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + CONTACTS_COLUMN_TLVL + " FROM "+ CONTACTS_TABLE_NAME + " WHERE " + CONTACTS_COLUMN_ID + " = '" + id + "'";
        Cursor  cursor = db.rawQuery(query,null);
        String trustlevel = "0";
        if (cursor.moveToFirst()) {
            trustlevel =  cursor.getString(cursor.getColumnIndex(CONTACTS_COLUMN_TLVL));
        }
        return trustlevel;
    }
    /**
     * Method to get all contacts from Contacts Table.
     * @return
     */
    public HashMap<String,Frnd> getAllContacts()
    {
        HashMap<String,Frnd> array_list = new HashMap<String,Frnd>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Frnd frnd = new Frnd();
            frnd.setName(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            frnd.setTrustlvl(res.getString(res.getColumnIndex(CONTACTS_COLUMN_TLVL)));
            array_list.put(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ID)),frnd);
            res.moveToNext();
        }
        return array_list;
    }

    /**
     * Method to get all received messages from Messages Table.
     * @return
     */
    public HashMap<String,DispMessage> getAllRecevMsgs()
    {
        HashMap<String,DispMessage> array_list = new HashMap<String, DispMessage>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+MSGS_TABLE_NAME+" where "+MSGS_COLUMN_MSGTYPE + " = \"R\"", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            DispMessage dispMessage = new DispMessage();
            dispMessage.setPh(res.getString(res.getColumnIndex(MSGS_COLUMN_PHONE)));
            dispMessage.setName(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            dispMessage.setMsg(res.getString(res.getColumnIndex(MSGS_COLUMN_MSG)));
            dispMessage.setMsgtype(res.getString(res.getColumnIndex(MSGS_COLUMN_MSGTYPE)));
            dispMessage.setDate(res.getString(res.getColumnIndex(MSGS_COLUMN_DATE)));
            dispMessage.setTime(res.getString(res.getColumnIndex(MSGS_COLUMN_TIME)));
            array_list.put(res.getString(res.getColumnIndex(MSGS_COLUMN_MSGID)),dispMessage);
            res.moveToNext();
        }
        return array_list;
    }

    /**
     * Method to get all sent messages from Messages Table.
     * @return
     */
    public HashMap<String,DispMessage> getAllSentMsgs()
    {
        HashMap<String,DispMessage> array_list = new HashMap<String, DispMessage>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+MSGS_TABLE_NAME+" where "+MSGS_COLUMN_MSGTYPE + " = \"S\"", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            DispMessage dispMessage = new DispMessage();
            dispMessage.setPh(res.getString(res.getColumnIndex(MSGS_COLUMN_PHONE)));
            dispMessage.setName(res.getString(res.getColumnIndex(MSGS_COLUMN_NAME)));
            dispMessage.setMsg(res.getString(res.getColumnIndex(MSGS_COLUMN_MSG)));
            dispMessage.setMsgtype(res.getString(res.getColumnIndex(MSGS_COLUMN_MSGTYPE)));
            dispMessage.setDate(res.getString(res.getColumnIndex(MSGS_COLUMN_DATE)));
            dispMessage.setTime(res.getString(res.getColumnIndex(MSGS_COLUMN_TIME)));
            array_list.put(res.getString(res.getColumnIndex(MSGS_COLUMN_MSGID)),dispMessage);
            res.moveToNext();
        }
        return array_list;
    }

    class Frnd{
        private String name;
        private String trustlvl;

        void Frnd(){}
        void setName(String name){this.name=name;}
        void setTrustlvl(String trustlvl){this.trustlvl=trustlvl;}
        public String getTrustlvl(){ return this.trustlvl;}
        public String getName(){ return this.name;}
    }
    class DispMessage{
        private String ph;
        private String name;
        private String msg;
        private String msgtype;
        private String date;
        private String time;

        void DispMessage(){}
        public void setPh(String ph){this.ph=ph;}
        public void setName(String name){this.name=name;}
        public void setMsg(String msg){this.msg=msg;}
        public void setMsgtype(String msgtype){this.msgtype=msgtype;}
        public void setDate(String date){this.date=date;}
        public void setTime(String time){this.time=time;}

        public String getPh(){return this.ph;}
        public String getName(){ return this.name;}
        public String getMsg(){ return this.msg;}
        public String getMsgtype(){return this.msgtype;}
        public String getDate(){return this.date;}
        public String getTime(){return this.time;}

    }
}

