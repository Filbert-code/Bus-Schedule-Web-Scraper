import java.util.Map;
import java.util.Scanner;

public class RouteFinderClient {
    public static void main(String[] args) throws Exception{
        String URL = "https://www.communitytransit.org/busservice/schedules/";
        RouteFinder rf = new RouteFinder(URL);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter a letter that your destinations start with ");
        char dest = scanner.next().toLowerCase().charAt(0);
        System.out.println();
        printBusRoutesUrls(rf, dest);

        System.out.print("Please enter your destination: ");
        String dest_complete = scanner.next();
        System.out.print("Please enter your route ID: ");
        String route_id = scanner.next();

        String route_url = rf.getUrlFromDestRoute(route_id, dest_complete);
        printRouteStops(rf, route_url);
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

    public static void printRouteStops(RouteFinder rf, String url) {

    }
}
