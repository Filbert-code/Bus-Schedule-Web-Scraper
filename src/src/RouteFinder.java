/*
    Author: Alex Filbert
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
    Class for retrieving html from https://www.communitytransit.org/busservice/schedules/ and returning
    bus route information in the greater-Seattle area. getUrlText returns the html as a String.
    getBusRoutesUrls returns a Map with bus route information and urls based on user input. getRouteStops
    returns the specific bus stops in the given route url.
 */
public class RouteFinder implements IRouteFinder{

    // stores the destination, and corresponding Map of routes with route URLs. <destination, Map<route, url>>
    public final Map<String, Map<String, String>> completeDestRouteUrlMap;
    // the html of the webpage being parsed (returned by getUrlText)
    private String text;

    // constructor
    public RouteFinder() throws Exception{
        completeDestRouteUrlMap = new HashMap<>(); // initialize Map
        text = getUrlText(TRANSIT_WEB_URL); // gets html from Community Transit website
    }

    /**
     * The function returns the HTML of the webpage from the given URL
     * @param String This represents a webpage URL
     * @return String HTML of the webpage from the given URL
     * @throws Exception
     */
    private String getUrlText(String URL){
        text = "";
        try {
            URLConnection bus_sch_website = new URL(URL).openConnection();
            bus_sch_website.setRequestProperty("user-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            BufferedReader in = new BufferedReader(new InputStreamReader(bus_sch_website.getInputStream()));
            String inputLine = "";
            String text = "";
            // append lines of input HTML to the text member variable
            while ((inputLine = in.readLine()) != null) {
                this.text += inputLine;
            }
            in.close();
        } catch (IOException e) {
            System.out.println("There was a problem reading the input HTML from the bus schedule website.");
            throw new RuntimeException();
        }

        return this.text;
    }

    /**
     * The function returns the route URLs for a specific destination initial using the URL text
     * @param destInitial This represents a destination (e.g. b/B is initial for Bellevue, Bothell, ...)
     * @return key/value map of the routes with key is destination and
     *       value is an inner map with a pair of route ID and the route page URL
     *       (e.g. of a map element <Brier, <111, https://www.communitytransit.org/busservice/schedules/route/111>>)
     */
    public Map<String, Map<String, String>> getBusRoutesUrls(final char destInitial) {

        // throws an exception if the user did not enter a letter
        if(!Character.isLetter(destInitial)) {
            System.out.println("A letter was not entered.");
            throw new RuntimeException();
        }

        // regular expression for matching the destinations and routes
        Pattern pattern = Pattern.compile("(<h3>(.*?)</h3>.*?)?<strong><a\shref=\"(.*?)\".*?>(.*?)</a>");
        Matcher matcher = pattern.matcher(this.text);

        final Map<String, Map<String, String>> userDestRouteUrlMap = new HashMap<>();
        Map<String, String> routeUrlMap = new HashMap<>();
        Map<String, String> routeUrlMap2 = new HashMap<>();
        String route;
        String url;
        String dest = "";
        // loop through the matched Strings and append them to the appropriate key/value in UserDestRouteUrlMap
        while(matcher.find()) {
            if(matcher.group(1) != null) {
                dest = matcher.group(2);
                routeUrlMap = new HashMap<>();
                if(Character.toLowerCase(dest.charAt(0)) == Character.toLowerCase(destInitial)) {
                    userDestRouteUrlMap.put(dest, routeUrlMap);
                }
                // creating a new routeUrlMap that will not be saved to destRouteUrlMap
                routeUrlMap2 = new HashMap<>();
                // append to the member variable Map to be access later by the getRouteStops function
                completeDestRouteUrlMap.put(dest, routeUrlMap2);
            }
            route = matcher.group(4);
            // prepend the rest of the URL
            url = "https://www.communitytransit.org/busservice" + matcher.group(3);
            routeUrlMap.put(route, url);
            routeUrlMap2.put(route, url);
        }
        return userDestRouteUrlMap;
    }

    /**
     * The function returns route stops, grouped by destination To/From, for a certain route ID url
     * @param url: the URL of the route that you want to get its bus stops
     * @return map of the stops grouped by destination with key is the destination (e.g. To Bellevue)
     *  and value is the list of stops in the same order that it was parsed on
     * (e.g. of a map element <To Mountlake Terrace, <<1, Brier Rd &amp; 228th Pl SW>, <2, 228th St SW &amp; 48th Ave W>, ...>>)
     */
    public Map<String, LinkedHashMap<String, String>> getRouteStops(final String url){
        // get HTML from the given URL
        try {
            this.getUrlText(url);
        } catch (Exception e) {
            System.out.println("Route ID was not found.");
            throw new RuntimeException();
        }

        // regular expression for the two route destinations on each page
        Pattern label_pattern = Pattern.compile("name=\"Trip\".*?>(.*?)</label>");
        Matcher label_matcher = label_pattern.matcher(this.text);

        // append the two route destinations to an array
        ArrayList<String> dest_arr = new ArrayList<>();
        while(label_matcher.find()) {
            String dest = label_matcher.group(1);
            dest = rid_Of_Amp(dest); // call helper method to delete unnecessary characters
            dest_arr.add(dest);
        }
        // regular expression grabbing all the HTML that contains the bus stop information
        Pattern wd_pattern = Pattern.compile("<div id=\"Weekday(.*?)id=\"Weekday(.*?)</table>");
        Matcher wd_matcher = wd_pattern.matcher(this.text);
        // each String stores all the HTML that contains the bus stop information for each of the two destinations
        String weekday_route_1 = "";
        String weekday_route_2 = "";
        while(wd_matcher.find()){
            weekday_route_1 = wd_matcher.group(1);
            weekday_route_2 = wd_matcher.group(2);
        }
        // regular expression for matching the bus stops
        Pattern route_pattern = Pattern.compile("<th.*?<p>(.*?)</p>");
        Matcher route_1_matcher = route_pattern.matcher(weekday_route_1);
        Matcher route_2_matcher = route_pattern.matcher(weekday_route_2);

        // appending all of the first route bus stops to an array
        List<String> route_1_stops = new ArrayList<>();
        while(route_1_matcher.find()) {
            String bus_stop = route_1_matcher.group(1);
            if(bus_stop.contains("amp;")){
                bus_stop = rid_Of_Amp(bus_stop);
            }
            route_1_stops.add(bus_stop);
        }

        // appending all of the second route bus stops to an array
        List<String> route_2_stops = new ArrayList<>();
        while(route_2_matcher.find()) {
            String bus_stop = route_2_matcher.group(1);
            if(bus_stop.contains("amp;")){
                bus_stop = rid_Of_Amp(bus_stop);
            }
            route_2_stops.add(bus_stop);
        }

        Map<String, LinkedHashMap<String, String>> dest_trip_route;
        // calling a helper method to combine the route arrays and destination array into a Map
        dest_trip_route = fill_dest_trip_route(route_1_stops, route_2_stops, dest_arr);
        return dest_trip_route;
    }

    /**
     * The function is a helper method for getRouteStops. Appends all the bus stops to each of the two
     * bus routes for the destination to a Map.
     * @param route_1_stops List of Strings for the bus stops of the first route
     * @param route_2_stops List of Strings for the bus stops of the second route
     * @param dest_arr ArrayList of Strings for all the destinations
     * @return Map<String, LinkedHashMap<String, String>> Map where the key is the bus destination
     *         and the value is a LinkedHashMap of enumerated bus stops to get to the destination
     */
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
        // error handling for if the route stops are empty
        try{
            dest_trip_route.put(dest_arr.get(0), trip_route);
        } catch (Exception e){
            System.out.println("Your chosen route does not have any bus stops currently so none can be displayed.");
            throw new RuntimeException();
        }

        trip_route = new LinkedHashMap<String, String>();
        for(int bus_stop_ind = 0; bus_stop_ind < route_2_stops.size(); bus_stop_ind++) {
            trip_route.put(String.valueOf(route_2_stops.size() - bus_stop_ind), route_2_stops.get(bus_stop_ind));
        }
        dest_trip_route.put(dest_arr.get(1), trip_route);
        return dest_trip_route;
    }

    /**
     * The function is a helper method for getRouteStops. Some Destination and route names that are
     * taken from the HTML have redundant characters ("amp;") inside the Strings. This function removes
     * those characters.
     * @param dest Destination of the bus route or bus stop
     * @return String destination without the characters ("amp;") inside it
     */
    private static String rid_Of_Amp(String dest) {
        if(dest.contains("amp;")) {
            int amp_ind = dest.indexOf("amp;");
            dest = dest.substring(0, amp_ind) + dest.substring(amp_ind+4);
        }
        return dest;
    }
}
