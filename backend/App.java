// backend/App.java
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.http.*;

public class App {
  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(System.getenv().getOrDefault("PORT","8080"));
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

    server.createContext("/api/health", ex -> {
      if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { preflight(ex); return; }
      cors(ex);
      send(ex, 200, "text/plain", "ok");
    });

    server.createContext("/api/weather", ex -> {
      if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { preflight(ex); return; }
      cors(ex);
      try {
        String q = parseQuery(ex.getRequestURI().getRawQuery()).get("q");
        if (q == null || q.isBlank()) { send(ex, 400, "application/json", "{\"error\":\"q required\"}"); return; }
        String key = System.getenv("OWM_API_KEY");
        if (key == null || key.isBlank()) { send(ex, 500, "application/json", "{\"error\":\"missing OWM_API_KEY\"}"); return; }

        String url = "https://api.openweathermap.org/data/2.5/weather?q="
            + URLEncoder.encode(q, StandardCharsets.UTF_8)
            + "&appid=" + key + "&units=metric";

        HttpResponse<String> r = HttpClient.newHttpClient()
          .send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        send(ex, r.statusCode(), "application/json", r.body());
      } catch (Exception e) {
        send(ex, 500, "application/json", "{\"error\":\"internal\",\"detail\":\"" + esc(e.toString()) + "\"}");
      }
    });

    server.setExecutor(null);
    server.start();
    System.out.println("Disasterverse backend running on http://localhost:" + port);
  }

  // ---- helpers ----
  static java.util.Map<String,String> parseQuery(String q){
    java.util.Map<String,String> map = new java.util.HashMap<>();
    if (q == null || q.isEmpty()) return map;
    for (String p : q.split("&")) {
      int i = p.indexOf('=');
      String k = i > 0 ? p.substring(0,i) : p;
      String v = i > 0 ? p.substring(i+1) : "";
      map.put(urlDecode(k), urlDecode(v));
    }
    return map;
  }
  static String urlDecode(String s){ try { return URLDecoder.decode(s, StandardCharsets.UTF_8); } catch(Exception e){ return s; } }

  static void cors(HttpExchange ex){
    ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,OPTIONS");
    ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    ex.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
  }
  static void preflight(HttpExchange ex) throws IOException { cors(ex); ex.sendResponseHeaders(204, -1); ex.close(); }

  static void send(HttpExchange ex, int code, String type, String body) throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    ex.getResponseHeaders().add("Content-Type", type + "; charset=utf-8");
    ex.sendResponseHeaders(code, bytes.length);
    try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
  }
  static String esc(String s){ return s.replace("\\","\\\\").replace("\"","\\\""); }
}
