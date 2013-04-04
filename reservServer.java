import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

class reservServer {

    static int socketNum;
    ServerSocket server = null;

    static ArrayList<Flight> flights = new ArrayList<Flight>();
    static ArrayList<String> users = new ArrayList<String>();

    public static void main(String[] args) {

        // Setup the server object
        reservServer server = new reservServer();

        // Create the scanner object to read in the text file
        Scanner fileIn = null;

        // Making sure the flights.txt file is present
        try {
            fileIn = new Scanner(new File(args[0]));
            socketNum = Integer.parseInt(args[1]);
            System.out.println("File opened");
        } catch (IOException e) {
            System.out.println("Read failed");
            System.exit(1);
        }

        // Parse the input line in flights.txt into individual parts,
        // then create a Flight object with all the information,
        // and finally add this new Flight into the flights ArrayList.
        while(fileIn.hasNext()) {

            String lineIn = fileIn.nextLine();
            String[] parts = lineIn.split(" ");
            String departureCity = parts[0];
            String arrivalCity = parts[1];
            String departureTime = parts[2];
            String departureAMPM = parts[3];
            String airline = parts[4];
            String flightNum = parts[5];
            Flight myFlight = new Flight(departureCity, arrivalCity, departureTime, departureAMPM,
                    airline, Integer.parseInt(flightNum));
            flights.add(myFlight);
        }

        // Testing to see if the # of Flight objects in flights is the same # of lines in flights.txt
        System.out.println(flights.size() + " flights have been loaded into the system.");

        // Start listening on the socket
        server.listenSocket();

        // All done!
        System.exit(0);
    }

    static Flight findFlightByID(int id) {
        for(Flight flight : flights) {
            if(flight.getFlightNum() == id) {
                return flight;
            }
        }
        return null;
    }

    static Flight findFlightByARC(String city) {
        for(Flight flight: flights) {
            if(city.equals(flight.getArrive())) {
                return flight;
            }
        }
        return null;
    }

    static Flight findFlightByDPC(String city) {
        for(Flight flight: flights) {
            if(city.equals(flight.getDepart())) {
                return flight;
            }
        }
        return null;
    }

    public void listenSocket()
    {
        try
        {
            server = new ServerSocket(socketNum);
            System.out.println("Server running, use ctrl-C to end");
        }
        catch (IOException e)
        {
            System.out.println("Error creating socket");
            System.exit(-1);
        }
        while(true)
        {
            ClientWorker w;
            try
            {
                System.out.println("Waiting for client...");
                w = new ClientWorker(server.accept(), users, flights);
                System.out.println("Client Connected!");
                Thread t = new Thread(w);
                t.start();
            }
            catch (IOException e)
            {
                System.out.println("Accept failed");
                System.exit(-1);
            }
        }
    }

    protected void finalize()
    {
        try
        {
            server.close();
        }
        catch (IOException e)
        {
            System.out.println("Could not close socket");
            System.exit(-1);
        }
    }

}




