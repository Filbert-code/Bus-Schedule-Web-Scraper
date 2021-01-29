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
        rf.getRouteStops("https://www.communitytransit.org/busservice/schedules/route/109");

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
        Map<String, LinkedHashMap<String, String>> dest_trip_route = new LinkedHashMap<>();
        LinkedHashMap<String, String> trip_route;

        this.getUrlText(url);

        Pattern label_pattern = Pattern.compile("name=\"Trip\".*?>(.*?)</label>");
        Matcher label_matcher = label_pattern.matcher(this.text);

        while(label_matcher.find()) {
            String dest = label_matcher.group(1);
            trip_route = new LinkedHashMap<String, String>();
            dest_trip_route.put(dest, trip_route);
        }

        Pattern trip_pattern = Pattern.compile("<th.*?<p>(.*?)</p>");
        Matcher trip_matcher = trip_pattern.matcher(this.text);

        List<String> trip_arr = new ArrayList<>();
        while(trip_matcher.find()) {
            String bus_stop = trip_matcher.group(1);
            trip_arr.add(bus_stop);
        }

        trip_arr = trip_arr.subList(0, trip_arr.size() / 3);
        for(String str: trip_arr) {
            System.out.println(str);
        }

        return dest_trip_route;
    }

}
