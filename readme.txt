Readme.txt
Flight Reserveration System
by Caleb Dean and Sean Fay

Compilation: 
1. javac reservServer.java
2. javac Flight.java
3. javac ClientWorker.java
4. javac ReservClient.java

Runtime:
1. java reservServer flights.txt port# 
2. java ReservClient server_name port#

Where port# is the port# you wish to use, as long as the # is the same for both client and server
there should be no problem. This was tested on desktop computers in the CS lab, along with on net15(server) 
and net16(client). server_name is obvious, and flights.txt is the text file provided by the Professor.

The client menu is self-explanatory, and should provide adequate output to the user.
The server displays only a few things, including startup information, and how to quit. It will then display 
that it is ready for clients to communicate, and will indicate when a user logs on (in all upper-case) 
along with when the user logs off or is disconnected. 