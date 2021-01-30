import com.sun.source.tree.Tree;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class RouteFinderClient {
    public static void main(String[] args) throws Exception{
        String URL = "https://www.communitytransit.org/busservice/schedules/";
        RouteFinder rf = new RouteFinder(URL);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter a letter that your destinations start with ");
        char dest = scanner.next().toLowerCase().charAt(0);
        System.out.println();
        printBusRoutesUrls(rf, dest);
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
}
