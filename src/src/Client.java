/*
    Author: Alex Filbert
 */
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception{
        String URL = "https://www.communitytransit.org/busservice/schedules/";
        RouteFinder rf = new RouteFinder();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while(running) {
            System.out.print("Please enter a letter that your destination starts with: ");
            char dest = scanner.next().charAt(0);
            System.out.println();
            Map<String,Map<String, String>> map = printBusRoutesUrls(rf, dest);

            System.out.print("Please enter your destination: ");
            String dest_complete = scanner.next();
            System.out.print("Please enter your route ID: ");
            String route_id = scanner.next();
            System.out.println();

            String route_url = "";
            try {
                route_url = map.get(dest_complete).get(route_id);
            } catch(Exception e) {
                System.out.println("Could not find a bus schedule with the given destination and route combination.\n");
                continue;
            }

            printRouteStops(rf, route_url);

            System.out.println();
            System.out.print("Do you want to check a different destination? Please type Y to continue or press any other key to exit ");
            String user_input = scanner.next();
            if(!user_input.toLowerCase().equals("y")) {
                running = false;
            }
            System.out.println();
        }

    }

    // prints out all the routes beginning with the given argument char value
    public static Map<String,Map<String, String>> printBusRoutesUrls(RouteFinder rf, char dest) {
        Map<String,Map<String, String>>  map = rf.getBusRoutesUrls(dest);
        for(String destination: map.keySet()) {
            System.out.println("Destination: " + destination);
            for(String route: map.get(destination).keySet()) {
                System.out.println("Bus Number: " + route);
            }
            System.out.println("+++++++++++++++++++++++++++++++++++");
        }
        return map;
    }

    // prints the destinations and bus stops associated with the given url
    public static void printRouteStops(RouteFinder rf, String url) throws Exception{
        Map<String, LinkedHashMap<String, String>> dest_trip_route = rf.getRouteStops(url);
        for (String dest: dest_trip_route.keySet()) {
            System.out.println("Destination:" + dest);
            for(String bus_stop: dest_trip_route.get(dest).keySet()) {
                System.out.println("Stop number: " + bus_stop + " is " + dest_trip_route.get(dest).get(bus_stop));
            }
        }
    }
}
