import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteFinder implements IRouteFinder{

    final Map<String, Map<String, String>> completeDestRouteUrlMap = new HashMap<>();
    private String text;

    public static void main(String[] args) throws Exception{
        RouteFinder rf = new RouteFinder();
        String URL = "https://www.communitytransit.org/busservice/schedules/";
        String html = rf.getUrlText(URL);
//        System.out.println(html);
        Map<String, Map<String, String>> m = rf.getBusRoutesUrls('b');
//        for (String destination: rf.completeDestRouteUrlMap.keySet()) {
//            System.out.println(destination);
//            for (String str: rf.completeDestRouteUrlMap.get(destination).keySet()) {
//                System.out.println(str + ", " + rf.completeDestRouteUrlMap.get(destination).get(str));
//            }
//        }
        rf.getRouteStops("https://www.communitytransit.org/busservice/schedules/route/532-535");

    }

    private String getUrlText(String URL) throws Exception{
        text = "";
        URLConnection bus_sch_website = new URL(URL).openConnection();
        bus_sch_website.setRequestProperty("user-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        BufferedReader in = new BufferedReader(new InputStreamReader(bus_sch_website.getInputStream()));
        String inputLine = "";
        String text = "";
        while ((inputLine = in.readLine()) != null) {
            this.text += inputLine;
        }
        in.close();
        return this.text;
    }

    public Map<String, Map<String, String>> getBusRoutesUrls(final char destInitial) {

        Pattern pattern = Pattern.compile("(<h3>(.*?)</h3>.*?)?<strong><a\shref=\"(.*?)\".*?>(.*?)</a>");
        Matcher matcher = pattern.matcher(this.text);

        final Map<String, Map<String, String>> userDestRouteUrlMap = new HashMap<>();
        Map<String, String> routeUrlMap = new HashMap<>();
        Map<String, String> routeUrlMap2 = new HashMap<>();
        String route;
        String url;
        String dest = "";
        while(matcher.find()) {
            if(matcher.group(1) != null) {
                dest = matcher.group(2);
                if(Character.toLowerCase(dest.charAt(0)) == destInitial) {
                    routeUrlMap = new HashMap<>();
                    userDestRouteUrlMap.put(dest, routeUrlMap);
                }
                // creating a new routeUrlMap that will not be saved to destRouteUrlMap
                routeUrlMap2 = new HashMap<>();
                completeDestRouteUrlMap.put(dest, routeUrlMap2);
            }
            route = matcher.group(4);
            url = "https://www.communitytransit.org/busservice" + matcher.group(3);
            routeUrlMap.put(route, url);
            routeUrlMap2.put(route, url);
        }
        return userDestRouteUrlMap;
    }

    public Map<String, LinkedHashMap<String, String>> getRouteStops(final String url) throws Exception{

        this.getUrlText(url);

        Pattern label_pattern = Pattern.compile("name=\"Trip\".*?>(.*?)</label>");
        Matcher label_matcher = label_pattern.matcher(this.text);

        ArrayList<String> dest_arr = new ArrayList<>();
        while(label_matcher.find()) {
            String dest = label_matcher.group(1);
            dest = rid_Of_Amp(dest);
            dest_arr.add(dest);
        }

        Pattern wd_pattern = Pattern.compile("<div id=\"Weekday(.*?)id=\"Weekday(.*?)</table>");
        Matcher wd_matcher = wd_pattern.matcher(this.text);
        String weekday_route_1 = "";
        String weekday_route_2 = "";
        while(wd_matcher.find()){
            weekday_route_1 = wd_matcher.group(1);
            weekday_route_2 = wd_matcher.group(2);
        }

        Pattern route_pattern = Pattern.compile("<th.*?<p>(.*?)</p>");
        Matcher route_1_matcher = route_pattern.matcher(weekday_route_1);
        Matcher route_2_matcher = route_pattern.matcher(weekday_route_2);

        List<String> route_1_stops = new ArrayList<>();
        while(route_1_matcher.find()) {
            String bus_stop = route_1_matcher.group(1);
            if(bus_stop.contains("amp;")){
                bus_stop = rid_Of_Amp(bus_stop);
            }
            route_1_stops.add(bus_stop);
        }

        List<String> route_2_stops = new ArrayList<>();
        while(route_2_matcher.find()) {
            String bus_stop = route_2_matcher.group(1);
            if(bus_stop.contains("amp;")){
                bus_stop = rid_Of_Amp(bus_stop);
            }
            route_2_stops.add(bus_stop);
        }

        Map<String, LinkedHashMap<String, String>> dest_trip_route = fill_dest_trip_route(route_1_stops,
                                                                                          route_2_stops,
                                                                                          dest_arr);
        // print results
        for (String dest: dest_trip_route.keySet()) {
            System.out.println("Destination:" + dest);
            for(String bus_stop: dest_trip_route.get(dest).keySet()) {
                System.out.println("Stop number: " + bus_stop + " is " + dest_trip_route.get(dest).get(bus_stop));
            }
        }

        return dest_trip_route;
    }

    private static Map<String, LinkedHashMap<String, String>> fill_dest_trip_route(
            List<String> route_1_stops,
            List<String> route_2_stops,
            ArrayList<String> dest_arr) {
        // appending bus stop data to the LinkedHashMaps
        Map<String, LinkedHashMap<String, String>> dest_trip_route = new LinkedHashMap<>();
        LinkedHashMap<String, String> trip_route;
        trip_route = new LinkedHashMap<String, String>();
        for(int bus_stop_ind = 0; bus_stop_ind < route_1_stops.size(); bus_stop_ind++) {
            trip_route.put(String.valueOf(bus_stop_ind + 1), route_1_stops.get(bus_stop_ind));
        }
        dest_trip_route.put(dest_arr.get(0), trip_route);
        trip_route = new LinkedHashMap<String, String>();
        for(int bus_stop_ind = 0; bus_stop_ind < route_2_stops.size(); bus_stop_ind++) {
            trip_route.put(String.valueOf(route_2_stops.size() - bus_stop_ind), route_2_stops.get(bus_stop_ind));
        }
        dest_trip_route.put(dest_arr.get(1), trip_route);
        return dest_trip_route;
    }

    private static String rid_Of_Amp(String dest) {
        if(dest.contains("amp;")) {
            int amp_ind = dest.indexOf("amp;");
            dest = dest.substring(0, amp_ind) + dest.substring(amp_ind+4);
        }
        return dest;
    }

}
