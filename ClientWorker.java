import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

class ClientWorker implements Runnable 
{
	private boolean loggedIn = false;
	private Socket client;
	private ArrayList<String> users;
	private ArrayList<Flight> flights;
	private ArrayList<Flight> reservedFlights = new ArrayList<Flight>();
	private ArrayList<String> confirmations = new ArrayList<String>();

	BufferedReader in = null;
	PrintWriter out = null;

	ClientWorker(Socket client, ArrayList<String> users, ArrayList<Flight> flights)
	{
		this.client = client;
		this.users = users;
		this.flights = flights;
		try
		{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
		}
		catch (IOException e)
		{
			System.out.println("in or out failed");
			System.exit(-1);
		}
	}

	// Everything the server is supposed to do with the "client"
	@Override
	public void run()

	{
		//System.out.println(confirmGenerator());
		String line;
		try
		{
			do {
				// Receive text from client
				line = in.readLine();
				if(line.contains("Login")) {
					// Checking login ID against users (pointer)
					if(users.contains(line.split("%")[1])) {
						out.println("Failure");
						System.out.printf("User %s already logged in, sorry.\n", line.split("%")[1]);
					}
					else {
						users.add(line.split("%")[1]);
						out.println("Success");
						System.out.printf("User %s successfully logged in, have fun!\n", line.split("%")[1]);
						loggedIn = true;
					}
				} 

			}while(!loggedIn);

			// while logged in
			while(loggedIn) {

				// Getting flight criteria, searching, and returning list of flights
				String arrive = "";
				String depart = "";
				line = in.readLine();
				if(line.contains("Flights")) {
					depart = line.split("%")[1];
					arrive = line.split("%")[2];
					String flightList = "#";
					for(Flight flight : flights) {
						if(depart.equalsIgnoreCase(flight.getDepart()) && arrive.equalsIgnoreCase(flight.getArrive())) {
							flightList += flight.getDepart() 
									+ "%" + flight.getArrive()
									+ "%" + flight.getTime()
									+ " " + flight.getAmPM()
									+ "%" + flight.getAirline()
									+ "%" + flight.getFlightNum()
									+ "#";
						}
					}
					if(!flightList.isEmpty()) {
						flightList = flightList.substring(0, (flightList.length()-1));
					}
					System.out.println("Sent to client: " + flightList);
					out.println(flightList);
				}

				// Reservation section
				else if(line.contains("Reserve")) {
					try {
						// Adding flight based on Flight # sent from client to reservedFlights, and confirmations
						int flight2Reserve = Integer.parseInt(line.split("%")[1]);
						reservedFlights.add(findFlightByID(flight2Reserve));
						String temp = confirmGenerator();
						confirmations.add(temp);
						out.println(temp);
					}
					catch(Exception e) {
						System.out.println("Flight not integer");
					}

				}
				
				// Cancellations section
				else if(line.contains("Cancel")) {
					try {
						//Cancellation of flight, removing from reservedFlights, and confirmations.
						if(confirmations.contains(line.split("%")[1])) {
							reservedFlights.remove(confirmations.indexOf(line.split("%")[1]));
							confirmations.remove(confirmations.indexOf(line.split("%")[1]));
							out.println("Success");
						}
					}
					catch (Exception e) {
						System.out.println("Could not cancel.");
						out.println("Failed");
					}
				}
				
				// Get bookings!
				else if(line.contains("Booked")) {
					try {
						
						// Getting booked flights for user, sending list to user.
						String flightList = "#";
						for(Flight flight : reservedFlights) {
							flightList += confirmations.get(reservedFlights.indexOf(flight))
										+ "%" + flight.getDepart() 
										+ "%" + flight.getArrive()
										+ "%" + flight.getTime()
										+ " " + flight.getAmPM()
										+ "%" + flight.getAirline()
										+ "%" + flight.getFlightNum()
										+ "#";

						}
						out.println(flightList);
					}
					catch (Exception e)
					{
						System.out.println("Unable to get bookings");
					}
				}
			}
			
           out.println(line);
		}
		catch (IOException e)
		{
			System.out.println("Read failed");
			System.exit(-1);
		}

		try
		{
			client.close();
		}
		catch (IOException e)
		{
			System.out.println("Close failed");
			System.exit(-1);
		}
	}

	Flight findFlightByID(int id) {
		for(Flight flight : flights) {
			if(flight.getFlightNum() == id) {
				return flight;
			}
		}
		return null;
	}

	Flight findFlightByARC(String city) {
		for(Flight flight: flights) {
			if(city.equals(flight.getArrive())) {
				return flight;
			}
		}
		return null;
	}

	Flight findFlightByDPC(String city) {
		for(Flight flight: flights) {
			if(city.equals(flight.getDepart())) {
				return flight;
			}
		}
		return null;
	}

  // Random alpha-numeric generator for the confirmation code
	private String confirmGenerator() {
		int length = 4;
		Random random = new Random((new Date()).getTime());
		char[] values = {'A','B','C','D','E','F','G','H','I','J',
				'K','L','M','N','O','P','Q','R','S','T',
				'U','V','W','X','Y','Z','0','1','2','3',
				'4','5','6','7','8','9'};

		String confNum = "";

		for (int i=0;i<length;i++) {
			int idx=random.nextInt(values.length);
			confNum += values[idx];
		}

		return confNum;
	}
}
