import java.util.Map;
import java.util.Scanner;
public class RouteFinderClient {
    public static void main(String[] args) throws Exception{
        String URL = "https://www.communitytransit.org/busservice/schedules/";
        RouteFinder rf = new RouteFinder(URL);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter a letter that your destinations start with ");
        char dest = scanner.next().toLowerCase().charAt(0);
        printBusRoutesUrls(rf, dest);
    }

    public static void printBusRoutesUrls(RouteFinder rf, char dest) {
        Map<String, Map<String, String>> map = rf.getBusRoutesUrls(dest);
        for(String destination: map.keySet()) {
            System.out.println(destination);
            for(String route: map.get(destination).keySet()) {
                System.out.println(route);
            }
        }
    }
}
