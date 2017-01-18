# TK3FinalProject
TK3FinalProject andriod app MyCircle

Course: Ubiquitous / Mobile Computing

Masters studies

MSc: Distributed Software Systems University: Technische Universität Darmstadt

Not availble for public use. 

Video Link:

https://youtu.be/jH65MuFvlxY

How to execute:
We have not build any installable apk. The project can be unzipped and imported in android studio and executed from android studio.
Please make sure the android device is GPS and Internet enabled and provides permission to the apps to use both.

Motivation:
Creation of a spider web of friends for helping each other in times of need by means of location tracking and exchange of messages.

Platform : Android
Pub/Sub: mosquitto MQTT (paho client iplementation)
Broker: HiveMQ MQTT broker used as default broker

Perquisites for using MyCirlce:
GPS should be enabled.
Working internet connection.

PURPOSE: Our project, “My Circle” is an application that was developed for assisting friends during times of need.The project focuses on how we can help our friends or they can help us irrespective of the time and location. It is an application wherein all friends are connected to each other and this connection is represented in the form of a spider web. Friends can seek help and offer help based on the trust level and also their location. 

The above diagram depicts the project architecture of “My Circle” application. This application was mainly developed for connecting and helping friends irrespective of their place location. Hence, for the same we have made use of Mosquitto, which is an open source message broker that implements the MQTT (Message Queuing Telemetry Transport) protocol. MQTT is a publish /subscribe messaging pattern consisting of a MQTT broker. A broker makes note of the publishers and subscribers and also it guarantees the delivery of message to the subscriber. The communication pattern can be clearly understood from the above diagram. The nodes here represent the phones with android operating system, internet and with My Circle application installed. Communication here is happening amongst the friends and MQTT broker here takes note of the publishers and subscribers and facilitates the forwarding of messages to the subscribers. The subscribers here subscribe to a topic with the help of their respective contact numbers. Note: We used public broker provided by hivemq. 
Database Architecture: We used SQLite database and three tables called “contacts”,”msgs” and “fmsg” are used to store contacts, messages and auto forwarded messages.   

TECHNOLOGIES USED:  Mosquitto MQTT(Mosquitto-Message Queuing Telemetry Transport)  GPS - Global Positioning System  GSM - Global System For Mobile Communication  Android  Smart phone with Android Operating System and internet connection  Google Maps  

WORKING: Taking into consideration that all my friends in My Circle, including myself are currently in different locations in Darmstadt and all of us are connected to each other through My Circle application which is installed on each of our phones and also considering that our phones have internet connection. Now let us assume that person A wants to buy some medicine from a medical store near Pallaswiesen Strasse, but the shop will be opened only for another 20 minutes. Person A can’t make it on time, so he/she sends a help message to friends residing near Pallaswiesen strasse. Person A 
gets to know about his friend’s current location with the help of the GPS which highlights the current location of friends on a map. Firstly, this will be possible only if you have a phone with an internet connection and My Circle application installed on it. Secondly, you should have added your friends in the My Circle application, in order for the application to track the latitude and longitude with exact position of your friends with the help of an algorithm. Once these conditions are satisfied then you will be able to send messages, receive messages, and seek help from your friends depending on their current location. 

IMPLEMENTATION: We used Android Studio 2.1.2 for developing the My Circle application. And in order to facilitate the communication between the end-to-end devices, we have used the Mosquitto MQTT publish-subscribe messaging pattern. We used google maps service provided by android. For database we used SQLlite also provided in Android.   

USER INTERFACE AND FUNCTIONALITY (as per the program execution order): This section will cover the user interface part and other functions associated with it. When you install My Circle application on your phone, a My Circle icon appears on your phone as you can see like fig. 2.  Step 1: When you click open the My Circle, a screen as shown in fig. 3 appears. The red highlighted point is your current point (location) in the map. Step 2: Now, you press the bottom left button on the android phone. A menu appears with a Settings option, and Add Friend, etc. As shown in fig. 4. Step 3: Click on the Settings option and now you will get fig. 5. 

Step 4: Now click on General, and the fig. 6 will be displayed. Here you should go ahead and update your name and contact number. Make sure to enable the Enable Connect and Enable Auto FW. Both should be in ON mode. 
 Enable Connect: This enables connection to the Mosquitto MQTT Broker which facilitates the sending and receiving of messages from friends. The MQTT, which facilitates data transmission among the end devices, also helps in locating the current location of a person on the google maps, provided the phone has internet with GPS in ON mode.   Enable Auto Forward: In case your connected friends are not near the target location, the help message is automatically forwarded to your friend’s friend who are in the proximity of the target location. 
  
Step 5: Now press the back button, you would get back the same screen as shown in fig. 5. Here click on Notifications and make sure the options, New message notification and vibrate are in ON mode. See fig. 7.This makes it easier for the new to know when he receives messages from his friends. Step 6: Again press the back button and this time, click on the Data & sync option, this will pop up a screen showing the synchronization frequency with respect to time and distance. See fig. 8. With the Sync frequency (Time) fig. 9 and Sync frequency (Distance) fig. 10, you can choose the time span and distance range according to your requirement and convenience. They are used in the google maps service to retrieve the location. Each time a user changes his/her location, a method is fired which in turn changes the latitudinal and longitudinal co-ordinates and the corresponding changes are updated. Now this is where the time and distance that were selected come into play. 

Information regarding how frequently the change is requested is displayed in the measures of time (in minutes) and distance (in meters) as per your selection. The location update is sent to the GPS based on the values of these parameters. Now the current location or position that is retrieved is shown by a red marker and this is liable to frequent changes. 

Step 7: See fig. 4 again. But this time you select the Add Friend option. See fig. 11. Here you can enter the name and contact number of your friend with whom you would like to connect on My Circle and you can also assign the trust level to each of your added friends. After you enter the details, click on ADD CONTACT button and now the data will be saved. You can also delete contact by long pressing contact row. 

Step 8: This step is about searching for a particular location on google maps. In the fig. 12, we search for Luisencenter which is the city center of Darmstadt and the result now shows up as you can see below, the target location is highlighted in red. 

Step 9: My Circle Connection & Location Tracking: With the help of MQTT, mobile phones with internet connection and My Circle application installed, each user can send /receive messages and retrieve the current location information of themselves and their friends with the help of algorithms. Their implementation is dynamic in nature because as and when a user changes his/her position, the corresponding latitudinal and longitudinal co-ordinates are updated and this data is populated on google maps in the form of a marker. The red marker says about your location, the green marker implies the friends to whom you are directly connected and the blue marker is used for indicating your friend’s friends to whom you are not directly connected. Fig. 13 will help us to understand this part better. 
Step 10: When you click on Send message text appearing on main screen on top right corner, one pop will be open to enter help message and send it to your friend/s. if you have more than two friends in a friend circle then application will select two friends having maximum trust level and send message to them accordingly. If you have less than or equal to two friends then message goes to them. Step 11: Go to fig. 4 again. There are two menu items named Sent & Received Messages. We are displaying sent and received messages in a List view whichever sent by user to friends or received by friends from user. On click of any particular message row, a pop with message body appears like given in fig. 16  

CONCLUSION: On a concluding note, we would like to inform you all that our project, “My Circle” has been successfully implemented with the help of various technologies including ubiquitous technology. This project can be an example of how mobile-to-mobile communication can be used today in the best way possible, with our present era having IoT (Internet of Things) as one of the prominent research focuses. 
