package com.example.dibyojyoti.mytest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.*;

import android.preference.PreferenceManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import com.example.dibyojyoti.mytest.connectMQTT.LocalBinder;
import static com.google.android.gms.common.api.GoogleApiClient.*;

//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return super.onSearchRequested(searchEvent);
    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    public static final int MY_LOCATION_REQUEST_CODE = 200;
    public static  long MIN_TIME_BETWN_UPDATES = 1; // 1 minute
    public static  float MIN_DIST_CHNG = 1.0f; // 10 mitures
    private GoogleMap mMap;
    DbHelperFrndList dbHelperFrndList;
    SharedPreferences prefs;
    //my location, trying to get from location service
    Location myLocation=null;

    //my latitude and longitude
    LatLng me=null;
    LatLng searchLatLng=null;
    //my marker
    ArrayList<Marker> arraymyself = new ArrayList<Marker>();
    //myriends marker
    ArrayList<Marker> arraymyfrndsMarker = new ArrayList<Marker>();
    //my  connection polygon
    ArrayList<Polygon> arraymyfrndsPolygon = new ArrayList<Polygon>();
    //Reciver marker
    ArrayList<Polygon> arrayreceiverpolygon = new ArrayList<Polygon>();
    //my circles
    ArrayList<Circle> arraymyCircle = new ArrayList<Circle>();
    //friend's connections
    ArrayList<Polygon> arrayfPolygon = new ArrayList<Polygon>();
    //friend's friends marker
    ArrayList<Marker> arrayfMarker = new ArrayList<Marker>();
    //friend's circles
    ArrayList<Circle> arrayfCircle = new ArrayList<Circle>();
    //map contains friend number and cordinate, towhom i will send msgs
    HashMap<String,LatLng> msgSendList = new HashMap<String,LatLng>();
    //list to hold my friends which will be shown in map
    private ArrayList<Myfriend> Myfriends = new ArrayList<Myfriend>();
	private boolean firsttimeDraw=false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    connectMQTT mServer=null;
    boolean mBounded = false;

    @Override
    public void onStart() {
        super.onStart();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.dibyojyoti.mytest/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        firsttimeDraw=false;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.dibyojyoti.mytest/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_media_play);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new Builder(this).addApi(AppIndex.API).build();
    }

    private void sendhelpmessage( double slat, double slon, String helpmsg) {
            dbHelperFrndList = new DbHelperFrndList(getApplicationContext());
            if (helpmsg != null && !helpmsg.equals("")) {
                Location searchLocation = new Location("");
                searchLocation.setLatitude(slat);
                searchLocation.setLongitude(slon);
                Map<Myfriend, Location> mapOfFrndsLocation = new HashMap<Myfriend, Location>();
                Map<Double, Myfriend> mapOfFrndsDistances = new HashMap<Double, Myfriend>();
                List<Double> listOfdistances = new ArrayList<Double>();
                for (int i = 0; i < Myfriends.size(); i++) {
                    Myfriend friend = Myfriends.get(i);
                    LatLng friendLatLng = friend.getLatLng();
                    Location loc = new Location("");
                    loc.setLatitude(friendLatLng.latitude);
                    loc.setLongitude(friendLatLng.longitude);
                    mapOfFrndsLocation.put(friend, loc);
                }
                int i = 0;
                for (Myfriend friend : mapOfFrndsLocation.keySet()) {
                    //Get distance in KM
                    double distance = Double.valueOf(searchLocation.distanceTo(mapOfFrndsLocation.get(friend))) / 1000;
                    //only look for friends in 10KM distance
                    if (distance <= 10.00d) {
                        listOfdistances.add(distance);
                        mapOfFrndsDistances.put(distance, friend);
                    }
                    i++;
                }
                i = 0;
                Collections.sort(listOfdistances);
                if (listOfdistances.size() == 1) {
                    Myfriend friend1 = mapOfFrndsDistances.get(listOfdistances.get(0));
                    mServer.publishHelpMsg(helpmsg, friend1, searchLocation.getLatitude(), searchLocation.getLongitude());
                    addToSenderList(friend1.getMobNumber(), friend1.getLatLng());
                    dbHelperFrndList.insertMsg(friend1.getMobNumber(), friend1.getName(), helpmsg, "S", new Date().toString(), "11");
                    clearHelpMsg();
                    //show message receivers on map
                    showMsgReciver();
                } else if (listOfdistances.size() == 2) {
                    Myfriend friend1 = mapOfFrndsDistances.get(listOfdistances.get(0));
                    Myfriend friend2 = mapOfFrndsDistances.get(listOfdistances.get(1));
                    mServer.publishHelpMsg(helpmsg, friend1, searchLocation.getLatitude(), searchLocation.getLongitude());
                    mServer.publishHelpMsg(helpmsg, friend2, searchLocation.getLatitude(), searchLocation.getLongitude());
                    addToSenderList(friend1.getMobNumber(), friend1.getLatLng());
                    addToSenderList(friend2.getMobNumber(), friend2.getLatLng());
                    dbHelperFrndList.insertMsg(friend1.getMobNumber(), friend1.getName(), helpmsg, "S", "", "");
                    dbHelperFrndList.insertMsg(friend2.getMobNumber(), friend2.getName(), helpmsg, "S", "", "");
                    clearHelpMsg();
                    //show message receivers on map
                    showMsgReciver();
                } else if (listOfdistances.size() > 2) {
                    // find maximum two trust levels from all
                    List<Integer> listOfTrustLvl = new ArrayList<Integer>();
                    Map<Integer, Myfriend> mapOfTrustAndFriend = new HashMap<Integer, Myfriend>();
                    for (Double distance : mapOfFrndsDistances.keySet()) {
                        Myfriend friend = mapOfFrndsDistances.get(distance);
                        Integer trustlevel = Integer.valueOf(dbHelperFrndList.getContactsColumnTlvl(friend.getMobNumber()));
                        listOfTrustLvl.add(trustlevel);
                        mapOfTrustAndFriend.put(trustlevel, friend);
                    }
                    Collections.sort(listOfTrustLvl);
                    Collections.reverse(listOfTrustLvl);
                    Myfriend friend1 = mapOfTrustAndFriend.get(listOfTrustLvl.get(0));
                    Myfriend friend2 = mapOfTrustAndFriend.get(listOfTrustLvl.get(1));
                    String trustlevel1 = dbHelperFrndList.getContactsColumnTlvl(friend1.getMobNumber());
                    String trustlevel2 = dbHelperFrndList.getContactsColumnTlvl(friend2.getMobNumber());
                    mServer.publishHelpMsg(helpmsg, friend1, searchLocation.getLatitude(), searchLocation.getLongitude());
                    mServer.publishHelpMsg(helpmsg, friend2, searchLocation.getLatitude(), searchLocation.getLongitude());
                    addToSenderList(friend1.getMobNumber(), friend1.getLatLng());
                    addToSenderList(friend2.getMobNumber(), friend2.getLatLng());
                    dbHelperFrndList.insertMsg(friend1.getMobNumber(), friend1.getName(), helpmsg, "S", "", "");
                    dbHelperFrndList.insertMsg(friend2.getMobNumber(), friend2.getName(), helpmsg, "S", "", "");
                    clearHelpMsg();
                    //show message receivers on map
                    showMsgReciver();
                } else {
                    Toast.makeText(MapsActivity.this, "Either no friends are online or nearby target :( ", Toast.LENGTH_SHORT).show();
                }
            }
        }

    @Override
    protected void onResume() {
        super.onResume();
        Intent mIntent = new Intent(this, connectMQTT.class);
        bindService(mIntent, mConnection, BIND_ADJUST_WITH_ACTIVITY);
        //send own help message
        if(prefs != null && prefs.getString("helpmsg","No help msg") != null && !prefs.getString("helpmsg","No help msg").equals("")) {
            String helpmsg = prefs.getString("helpmsg", "No help msg");
            if (searchLatLng != null && Myfriends != null) {
                sendhelpmessage(searchLatLng.latitude, searchLatLng.longitude, helpmsg);
            }
        }

    }

    private void clearHelpMsg() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("helpmsg", "");
        editor.commit();
    }
    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);

    }

    //to populate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
   
    //if user provides permission then check set my location layer and try to get location
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED    &&
                     (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(MapsActivity.this, "permission granted finally", Toast.LENGTH_SHORT).show();
                mMap.setMyLocationEnabled(true);
                getMyLocation();
            }
            else{
                Toast.makeText(MapsActivity.this, "permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * Manipulates the map once available,This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, need to install
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setPadding(0, 0, 0, 0);      //setting the dotted padding
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //get the time and distance frequency to call location update
        MIN_TIME_BETWN_UPDATES = (long)Integer.parseInt(prefs.getString("sync_frequency","1"));
        MIN_DIST_CHNG = (float) Integer.parseInt(prefs.getString("sync_frequency_dist","2"));
        //check if app has location access permission, if not ask user
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);    //setting my location button layer
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            getMyLocation();

        } else {
            //request for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
        //listener for clicking on friend marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
              @Override
              public boolean onMarkerClick(Marker marker) {
                  //draw friend list
                  if(Myfriends != null) {
                      for (Myfriend friend : Myfriends) {
                          if (friend.getName().equals(marker.getTitle())) {
                              //if friend not selected, remove prev friend's details
                              if (marker.getAlpha() == 0.5f) {
                                  //clear old marker drawings
                                  for (Polygon p : arrayfPolygon) {
                                      p.remove();
                                  }
                                  for (Circle c : arrayfCircle) {
                                      c.remove();
                                  }
                                  for (Marker m : arrayfMarker) {
                                      m.remove();
                                  }
                                  arrayfPolygon.clear();
                                  arrayfCircle.clear();
                                  arrayfMarker.clear();
                                  //now add current friend's details
                                  //draw firend's circle
                                  MIN_DIST_CHNG = (float) Integer.parseInt(prefs.getString("sync_frequency_dist","2"));
                                  //drawFrndCircles((int)MIN_DIST_CHNG, (int)MIN_DIST_CHNG/5, marker.getPosition(), Color.DKGRAY);
                                  drawFrndCircles(100, 20, marker.getPosition(), Color.DKGRAY);
                                  //draw firend's friend and connections
                                  for (Myfriend ffriend : friend.getFriendFriends()) {
                                      Marker m = mMap.addMarker(new MarkerOptions().position(ffriend.getLatLng()).title(ffriend.getName()).alpha(0.5f)
                                              .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                      arrayfMarker.add(m);
                                      Polygon pg = mMap.addPolygon(new PolygonOptions().add(marker.getPosition(), ffriend.getLatLng())
                                              .strokeColor(Color.BLUE).strokeWidth(2f).geodesic(true));
                                      arrayfPolygon.add(pg);

                                  }
                                  marker.setAlpha(1.0f);
                                  marker.showInfoWindow();
                                  //add friend to message send list
                                  //addToSenderList(friend.getMobNumber(), friend.getLatLng());
                              }
                              //if friend already seleted and user clicks on same friend, deselect friend
                              else {
                                  for (Polygon p : arrayfPolygon) {
                                      p.remove();
                                  }
                                  for (Circle c : arrayfCircle) {
                                      c.remove();
                                  }
                                  for (Marker m : arrayfMarker) {
                                      m.remove();
                                  }
                                  arrayfPolygon.clear();
                                  arrayfCircle.clear();
                                  arrayfMarker.clear();
                                  marker.setAlpha(0.5f);
                                  marker.showInfoWindow();
                                  //removeFromSenderList(friend.getMobNumber(), friend.getLatLng());
                              }
                          }
                      }
                  }
                  return true;
              }
              private void drawFrndCircles(int maxCircleWidth, int incement, LatLng center, int color) {
                  for (int i = incement; i <= maxCircleWidth; i = i + incement) {
                      Circle c = mMap.addCircle(new CircleOptions()
                              .center(center)
                              .radius(i).strokeColor(color).strokeWidth(2f));
                      arrayfCircle.add(c);
                  }
              }
        }
        );
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    //to handle menu click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.send_msg:
                Intent i4 = new Intent(MapsActivity.this, SendMessage.class);
                startActivity(i4);
                return true;
            case R.id.add_friend:
                Intent i1 = new Intent(MapsActivity.this, AddFriend.class);
                startActivity(i1);
                return true;
            case R.id.sent_msgs:
                Intent i2 = new Intent(MapsActivity.this, SentMessages.class);
                startActivity(i2);
                return true;
            case R.id.rcvd_msgs:
                Intent i3 = new Intent(MapsActivity.this, ReceivedMessages.class);
                startActivity(i3);
                return true;
            case R.id.register_me:
                Intent i = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
        
    //method to remove from senderlist
    private void removeFromSenderList(String number,LatLng latlng){
        msgSendList.remove(number);
    }
    //method to ad in senderlist
    private void addToSenderList(String number,LatLng latlng){
        msgSendList.put(number,latlng);
    }

    //method to highlight friends who received message
    private  void showMsgReciver(){
        //show stroke to message receiver
        if(Myfriends != null && Myfriends.size() != 0) {
            for (String key : msgSendList.keySet()) {
                Polygon p_reciver = mMap.addPolygon(new PolygonOptions().add(me, msgSendList.get(key))
                        .strokeColor(Color.YELLOW).strokeWidth(6f).geodesic(true));
                arrayreceiverpolygon.add(p_reciver);
            }
        }
    }

    //draws my friends and connections
    private void drawFriends(){
        //hide message receiver stroke after short time
        for (Polygon p : arrayreceiverpolygon) {
            p.remove();
        }
        for (Polygon p : arraymyfrndsPolygon) {
            p.remove();
        }
        for (Marker m : arraymyfrndsMarker) {
            m.remove();
        }
        if(Myfriends != null && Myfriends.size() != 0)
            for (Myfriend friend : Myfriends) {
                Marker m = mMap.addMarker(new MarkerOptions().position(friend.getLatLng()).title(friend.getName()).alpha(0.5f)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                arraymyfrndsMarker.add(m);
                Polygon p = mMap.addPolygon(new PolygonOptions().add(me, friend.getLatLng())
                        .strokeColor(Color.RED).strokeWidth(3f).geodesic(true));
                arraymyfrndsPolygon.add(p);
            }
    }
    //draws my circle
    private void drawCircles(int maxCircleWidth, int incement, LatLng center, int color) {
        for (Circle c : arraymyCircle) {
            c.remove();
        }
        for (int i = incement; i <= maxCircleWidth; i = i + incement) {
            Circle c = mMap.addCircle(new CircleOptions()
                    .center(center).radius(i).strokeColor(color).strokeWidth(3f));
            arraymyCircle.add(c);
        }
    }

    private void initFriends() {
        if(mServer != null) {
            Myfriends = mServer.getOthersData();
        }
    }

    // when search button is pressed
    public  void onSearch (View view) {
        EditText txtLocation = (EditText) findViewById(R.id.TFAddres);
        String strLoc = txtLocation != null && txtLocation.getText() != null && !txtLocation.getText().equals("") ? txtLocation.getText().toString() : "" ;
        List<Address> addressList = null;
        if(strLoc != null && !strLoc.equals("")) {
            Geocoder lObjGeoCoder = new Geocoder(this);
            try {
                addressList = lObjGeoCoder.getFromLocationName(strLoc,1);
                if(addressList !=null && addressList.size()!=0) {
                    Address address = addressList.get(0);
                    searchLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(searchLatLng).title(strLoc));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(searchLatLng));
                }
            } catch (IOException e) {
                Log.d("Location","not found");
                e.printStackTrace();
            } catch (NullPointerException e2) {
                Log.d("Location","not found");
                Toast.makeText(MapsActivity.this, "Enter any location to search", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MapsActivity.this, "Enter any location to search", Toast.LENGTH_SHORT).show();
        }
    }
    //method to get my location and my frinds location iteratively and drawing my marker and friends marker
    private void getMyLocation() {
        String zip = null;
        Location location = null;
        double latitude,longitude;

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation= location;
                Marker myself;
                if(myLocation != null) {
                    me = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    for(Marker m: arraymyself){
                        m.remove();
                    }
                    myself=mMap.addMarker(new MarkerOptions().position(me).title("Me"));
                    arraymyself.add(myself);
                    //create friends arraylist
                    // Add a marker for myself and move the camera
                    if(firsttimeDraw == false){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 12.0f));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me, 12.0f));
                        firsttimeDraw = true;
                    }
                    MIN_DIST_CHNG = (float) Integer.parseInt(prefs.getString("sync_frequency_dist","2"));
                    //drawCircles((int)MIN_DIST_CHNG, (int)MIN_DIST_CHNG/5, me, Color.RED);
                    drawCircles(1000, 200, me, Color.RED);
			    }

                if(mBounded && mServer != null){
                    //publish my location periodically
                    if(prefs.getBoolean("switch_mqtt",false)) {
                        mServer.publishnewlocation(myLocation.getLatitude(), myLocation.getLongitude());
                        mServer.connectiontoMytopic();
                        initFriends();
                        drawFriends();
                    }

                    //forward help message
                    DbHelperFrndList dbHelperFwdMsg = new DbHelperFrndList(getApplicationContext());
                    Cursor c = dbHelperFwdMsg.getDataFMsg();
                    if((c.moveToFirst()) || c.getCount()!=0) {
                        Toast.makeText(MapsActivity.this, "You have received a message", Toast.LENGTH_SHORT).show();
                        String id = c.getString(0);
                        String from = c.getString(1);
                        String name = c.getString(2);
                        String msg = c.getString(3);
                        String slat = c.getString(4);
                        String slng = c.getString(5);

                        c.close();

                        //Auto forward possible only when the autoforward option is enabled.
                        prefs = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
                        Boolean autoforward_status = prefs.getBoolean("switch_autofwd", false);
                        Boolean mqttservice_status = prefs.getBoolean("switch_mqtt", false);
                        if(autoforward_status && mqttservice_status) {
                            Location tmplocation = new Location("");
                            tmplocation.setLatitude(Double.parseDouble(slat));
                            tmplocation.setLongitude(Double.parseDouble(slng));
                            if (myLocation != null && Myfriends != null) {
                                double distance = Double.valueOf(myLocation.distanceTo(tmplocation)) / 1000.00d;
                                //if my location is more than 2 km of target then do auto fwd to my friends
                                if (distance > 2.00d) {
                                    sendhelpmessage(Double.parseDouble(slat), Double.parseDouble(slng), msg);
                                    Toast.makeText(MapsActivity.this, " Auto forwarding Message...", Toast.LENGTH_SHORT).show();
                                }
                                //noe delete the auto fwd msg
                                dbHelperFwdMsg.deleteFMessage(id);
                            }
                        }
                    }
                    else {
                        c.close();
                    }
                }

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras){}
            @Override
            public void onProviderEnabled(String provider){}
            @Override
            public void onProviderDisabled(String provider){}

            };
        try {
            // request for location request
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWN_UPDATES, MIN_DIST_CHNG, locationListener);

        } catch (SecurityException e){
            e.printStackTrace();
        }

    }
    // service connection to access connectMQTT service
    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MapsActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            mServer = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MapsActivity.this, "Service is connected", Toast.LENGTH_SHORT).show();
            mBounded = true;
            LocalBinder mLocalBinder = (LocalBinder)service;
            mServer = mLocalBinder.getServerInstance();
        }
    };
}


