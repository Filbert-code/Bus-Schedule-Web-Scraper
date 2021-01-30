import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception{
        String URL = "https://www.communitytransit.org/busservice/schedules/";
        RouteFinder rf = new RouteFinder(URL);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while(running) {
            System.out.print("Please enter a letter that your destinations start with ");
            char dest = scanner.next().toLowerCase().charAt(0);
            System.out.println();
            printBusRoutesUrls(rf, dest);

            System.out.print("Please enter your destination: ");
            String dest_complete = scanner.next();
            System.out.print("Please enter your route ID: ");
            String route_id = scanner.next();
            System.out.println();

            String route_url = rf.getUrlFromDestRoute(route_id, dest_complete);
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

    public static void printBusRoutesUrls(RouteFinder rf, char dest) {
        Map<String, Map<String, String>> map = rf.getBusRoutesUrls(dest);
        for(String destination: map.keySet()) {
            System.out.println("Destination: " + destination);
            for(String route: map.get(destination).keySet()) {
                System.out.println("Bus Number: " + route);
            }
            System.out.println("+++++++++++++++++++++++++++++++++++");
        }
    }

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
