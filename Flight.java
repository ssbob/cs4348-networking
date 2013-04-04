/**
 * Created with IntelliJ IDEA.
 * User: Sean Fay
 * Date: 4/16/12
 * Time: 12:24 PM
 */
public class Flight {

    String depart, arrive, time, amPM, airline;
    int flightNum;

    public Flight(String depart, String arrive, String time, String amPM, String airline, int flightNum) {

        this.flightNum = flightNum;
        this.depart = depart;
        this.arrive = arrive;
        this.time = time;
        this.amPM = amPM;
        this.airline = airline;
    }

    public int getFlightNum() {
        return flightNum;
    }

    public String getDepart() {
        return depart;
    }

    public String getArrive() {
        return arrive;
    }

    public String getTime() {
        return time;
    }

    public String getAmPM() {
        return amPM;
    }

    public String getAirline() {
        return airline;
    }
}
