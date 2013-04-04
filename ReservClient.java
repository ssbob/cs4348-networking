//Client side written by Caleb Dean
/*
 * Client side program for the Flight Reservation System
 * This program handles all UI and user interactions with the server
 * Compilation: javac ReservClient.java
 * Execution: java ReserveClient <HostName> <PortNumber>
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

class ReservClient {
    static int portNumber = -1;
    static String hostName = "";
    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    Scanner sc = new Scanner(System.in);
    boolean loggedIn = false;
    boolean exitMenuCheck = false;

    public void connect() {
        //Create socket connection
        try {
            System.out.println("Connecting to " + hostName + " on port " + portNumber);
            socket = new Socket(hostName, portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("No I/O");
            System.exit(1);
        }
    }

    public void work() {
        while (true) {
        	String entry = "";
        	String data = "";
        	do
        	{
	            //prompt for login name
	        	System.out.println("Please enter your first name as your user ID: ");
	            entry = sc.nextLine().toUpperCase().split(" ")[0];
	            if (!entry.equals(""))
	            {
		            //Send login name
		            out.println("Login%" + entry);
		            try {
		                data = in.readLine();
		                System.out.println(data);
		                //verify login
		                if (data.equals("Success")) 
		                {
		                    System.out.println("Welcome to the Flight Reservation System " + entry + "\n");
		                    loggedIn = true;
		                } 
		                else
		                {
		                    System.out.println(entry + " is already logged in.\n");
		                }
		            } catch (IOException e) 
		            {
		                System.out.println("Read failed for logging in");
		                out.println("Exit");
		                System.exit(1);
		            }
	            }
		        else 
		        {
		        	System.out.println("Please dont try to crash this system. Thank you :)");
		        }
        	}while (!loggedIn);
            if (loggedIn) 
            {
                exitMenuCheck = false;
                while (!exitMenuCheck) 
                {
                    //print menu
                    System.out.println("Menu:\n" +
                            "1. Display/Reserve Flights\n" +
                            "2. Display/Cancel Reservations\n" +
                            "3. Exit\n\n" +
                            "Please enter your menu choice:");
                    String temp = sc.nextLine();
                    int choice;
                    try
                    {
                        choice = Integer.parseInt(temp);
                    }
                    catch (Exception e)
                    {
                    	choice = 0;
                    }
                    switch (choice) {
                        case 1:        // prompt cities
                            System.out.println("Please enter the Airport Code for the Departure and Arrival cities.\n" +
                                    "(e.g. DFW LAX");
                            // input cities
                            entry = sc.nextLine();
                            try {
                                // ask for flight list
                                out.println("Flights%" + entry.split(" ")[0].toUpperCase() + "%" + entry.split("\\s")[1].toUpperCase());
                                try {
                                    // wait for response
                                    data = in.readLine();
                                    if (data.equals("")) {
                                        System.out.println("No flights found between those two airports.\n");
                                    } else {
                                        String[] list = data.split("#");
                                        try {
                                            //display flight list
                                            for (int i = 1; i < list.length; i++) {
                                                System.out.println((i) + ". " +
                                                        list[i].split("%")[0] + "\t" +
                                                        list[i].split("%")[1] + "\t" +
                                                        list[i].split("%")[2] + "\t" +
                                                        list[i].split("%")[3] + "\t" +
                                                        list[i].split("%")[4]);
                                            }

                                            //prompt for reservation choice
                                            System.out.println("\nPlease enter the menu number of the flight to reserve it, or 0 to continue.");
                                            //input entry
                                            temp = sc.nextLine();
                                            int choice2;
                                            try
                                            {
                                                choice2 = Integer.parseInt(temp);
                                            }
                                            catch (Exception e)
                                            {
                                            	choice2 = 0;
                                            }
                                            if (choice2 > 0 && choice2 <= list.length) {
                                                try {
                                                    //send reservation selection to server
                                                    out.println("Reserve%" + list[choice2].split("%")[4]);
                                                    try {
                                                        //read and print confirmation
                                                        data = in.readLine();
                                                        if (data.equals(""))
                                                            System.out.println("Flight could not be reserved.\n");
                                                        else
                                                            System.out.println("Confirmation code " + data +
                                                                    " for flight " + list[choice2].split("%")[4] +
                                                                    " coming out of " + list[choice2].split("%")[0] +
                                                                    " at " + list[choice2].split("%")[2] +
                                                                    " to " + list[choice2].split("%")[1] +
                                                                    " on " + list[choice2].split("%")[3] + ".\n");
                                                    } catch (IOException e) {
                                                        System.out.println("Read failed confirmed reservation.\n");
                                                    }
                                                } catch (Exception e) {
                                                    System.out.println("Invalid choice.\n");
                                                }
                                            }
                                        } catch (Exception e) {
                                            System.out.println("Corrupted Data Received.\n");
                                        }
                                    }
                                } catch (IOException e) {
                                    System.out.println("Read failed for displaying flights.\n");
                                }
                            } catch (Exception e) {
                                System.out.println("Invalid city format.\n");
                            }
                            break;

                        case 2:       //request reserved flights
                            out.println("Booked");
                            // wait for list from server
                            try {
                                data = in.readLine();
                                //reserved flights menu
                                String[] options = data.split("#");
                                for (int i = 1; i < options.length; i++) {
                                    System.out.println((i) + ". " +
                                            options[i].split("%")[0] + "\t" +
                                            options[i].split("%")[1] +"\t" +
                                            options[i].split("%")[2] +"\t" +
                                            options[i].split("%")[3] +"\t" +
                                            options[i].split("%")[4] +"\t" +
                                            options[i].split("%")[5]);
                                }
                                System.out.println("Please select which reservation to cancel or press 0 to return.");
                                //input cancellation choice
                                String temp2 = sc.nextLine();
                                int choice2;
                                try 
                                {
                                	choice2 = Integer.parseInt(temp2);
                                }
                                catch (Exception e)
                                {
                                	System.out.println("Invalid input.\n");
                                	choice2 = 0;
                                }
                                if (choice2 != 0) {
                                    try {
                                        //request cancellation
                                        out.println("Cancel%" + options[choice2].split("%")[0]);
                                        try {
                                            //wait for cancellation confirmation
                                            String confirmation = in.readLine();
                                            if (confirmation.equals("Success"))
                                                System.out.println("Reservation " + options[choice2].split("%")[0] + " cancelled.\n");
                                            else
                                                System.out.println("Cancellation failed");
                                        } catch (IOException e) {
                                            System.out.println("No success message received");
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Invalid input.\n");
                                    }
                                }


                            } catch (IOException e) {
                                System.out.println("Read failed at reserved flights.\n");
                            }
                            break;
                        case 3:
                            System.out.println("\nThank for you choosing Flight Reservation System 1.0");
                            out.println("Exit");
                            loggedIn = false;
                            exitMenuCheck = true;
                            break;
                        default:
                            System.out.println("Invalid input.\n");
                            break;
                    }
                }
            }
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        ReservClient client = new ReservClient();
        try {
            portNumber = Integer.parseInt(args[1]);
            if (portNumber < 1001 || portNumber > 65535) {
                System.out.println("Port number out of range. Please use ports between 1001 and 65535");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Port number not an integer.");
            System.exit(1);
        }
        hostName = args[0];
        client.connect();
        client.work();
    }
}
