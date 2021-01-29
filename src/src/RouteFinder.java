import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RouteFinder implements IRouteFinder{

    private String text;

    public static void main(String[] args) {
        RouteFinder rf = new RouteFinder();
//        rf.getUrlText();
    }

    private String getUrlText(String URL) {
        return "";
    }

    public Map<String, Map<String, String>> getBusRoutesUrls(final char destInitial) {
        Map<String, Map<String, String>> map = new HashMap<>();
        return map;
    }

    public Map<String, LinkedHashMap<String, String>> getRouteStops(final String url) {
        Map<String, LinkedHashMap<String, String>> map = new LinkedHashMap<>();
        return map;
    }
}
