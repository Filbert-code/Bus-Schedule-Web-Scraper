import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteFinder implements IRouteFinder{

    private String text;

    public static void main(String[] args) throws Exception{
        RouteFinder rf = new RouteFinder();
        String URL = "https://www.communitytransit.org/busservice/schedules/";
        String html = rf.getUrlText(URL);
//        System.out.println(html);
        rf.getBusRoutesUrls('b');
    }

    private String getUrlText(String URL) throws Exception{
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
        Matcher matcher = pattern.matcher(text);

        while(matcher.find()) {
            System.out.println(matcher.group(0));

        }

        Map<String, String> routeUrlMap = new HashMap<>();
        String route_id;
        String url;

        Map<String, Map<String, String>> destRouteUrlMap = new HashMap<>();
        return destRouteUrlMap;
    }

    public Map<String, LinkedHashMap<String, String>> getRouteStops(final String url) {
        Map<String, LinkedHashMap<String, String>> map = new LinkedHashMap<>();
        return map;
    }
}
