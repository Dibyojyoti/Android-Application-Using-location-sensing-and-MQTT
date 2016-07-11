package com.example.dibyojyoti.mytest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AddFriend extends AppCompatActivity {


    ArrayList<String> addArray = new ArrayList<String>();
    Button newFriendbutton;
    DbHelperFrndList dbHelperFrndList;

    //This field contains list of added friends
    ArrayList<String> friendsdetails;

    /**
     * This method reads friends data from Contacts table in database and displays as listview.
     */
    void refreshfriendslist(){
        dbHelperFrndList = new DbHelperFrndList(getApplicationContext());
        HashMap<String,DbHelperFrndList.Frnd> friendslist = dbHelperFrndList.getAllContacts();
        Iterator myVeryOwnIterator = friendslist.keySet().iterator();
        friendsdetails = new ArrayList<String>();
        DbHelperFrndList.Frnd frnd;
        while(myVeryOwnIterator.hasNext()) {
            String key=(String)myVeryOwnIterator.next();
            frnd = (DbHelperFrndList.Frnd) friendslist.get(key);
            Log.d("AddFriend added key:", key);
            friendsdetails.add(frnd.getName() +" : "+ frnd.getTrustlvl() + "%    (" + key + ")");
        }
        ListView listView = (ListView) findViewById(R.id.listViewContact);
        Log.d("AddFriend size:", "" + friendsdetails.size());
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendsdetails);
        listView.setAdapter(adapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        Spinner spinner = (Spinner) findViewById(R.id.trust_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.trust_levels, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        refreshfriendslist();

        //On Add button click, the friend details are inserted to Contacts database.
        newFriendbutton = (Button) findViewById(R.id.addnewContact);
        newFriendbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView name = (TextView)findViewById(R.id.newContactName);
                TextView contact = (TextView)findViewById(R.id.newContactNo);
                Spinner spinner = (Spinner) findViewById(R.id.trust_spinner);
                if(!(contact.getText().toString().equals("") || name.getText().toString().equals("") || spinner.getSelectedItem().toString().equals(""))){
                    dbHelperFrndList = new DbHelperFrndList(getApplicationContext());
                    dbHelperFrndList.insertContact(contact.getText().toString(), name.getText().toString(), spinner.getSelectedItem().toString() );
                    Toast.makeText(AddFriend.this, "Friend added successfully", Toast.LENGTH_SHORT).show();
                    name.setText("");
                    contact.setText("");
                    refreshfriendslist();
                }else{
                    Toast.makeText(AddFriend.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // On longpress of friends list, Friend can be detelted.
        ListView list = (ListView) findViewById(R.id.listViewContact);
        list.setLongClickable(true);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            String contactno;
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d("position", "" + friendsdetails.get(position));
                String selecteditem = friendsdetails.get(position);
                contactno= selecteditem.substring(selecteditem.indexOf('(') +1 ,selecteditem.indexOf(')'));
                Log.d("contactno", "" + contactno);
                new AlertDialog.Builder(AddFriend.this)
                        .setTitle("Deleting Friend")
                        .setMessage("" +
                                "" +
                                "" +
                                "" +
                                "Are you sure you want to delete this Contact?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelperFrndList = new DbHelperFrndList(AddFriend.this);
                                dbHelperFrndList.deleteContact(contactno);
                                refreshfriendslist();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        //Inflate  the menu; this adds items  to the action  bar  if it  is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }




}


