package org.example.runner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.example.module.annotations.GetMapping;
import org.example.module.annotations.RequestParam;
import org.example.module.annotations.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.*;

public class RunGetMapping {
    private static final Map<String, Method> controllerMethods = new HashMap<>();
    private static final Map<String, Object> controllerInstances = new HashMap<>();
    // Stores the @RequestParam metadata per path
    private static final Map<String, List<RequestParam>> controllerParams = new HashMap<>();

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            loadController(Class.forName(args[0]));
        } else {
            scanClasspath();
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", RunGetMapping::handleRequest);
        server.start();
        printMenu();
    }

    private static void printMenu() {
        System.out.println("\n==============================");
        System.out.println("  Server running at http://localhost:8080");
        System.out.println("==============================");
        System.out.println("  Available endpoints:");
        for (String path : controllerMethods.keySet()) {
            System.out.println("   GET " + path + "  →  http://localhost:8080" + path);
        }
        System.out.println("==============================\n");
    }

    private static void loadController(Class<?> c) throws Exception {
        if (!c.isAnnotationPresent(RestController.class)) {
            System.out.println("Class does not have @RestController: " + c.getName());
            return;
        }
        Object instance = c.getDeclaredConstructor().newInstance();
        for (Method m : c.getDeclaredMethods()) {
            if (m.isAnnotationPresent(GetMapping.class)) {
                String path = m.getAnnotation(GetMapping.class).value();
                controllerMethods.put(path, m);
                controllerInstances.put(path, instance);

                // Register @RequestParam metadata for this path
                List<RequestParam> params = new ArrayList<>();
                for (Parameter p : m.getParameters()) {
                    if (p.isAnnotationPresent(RequestParam.class)) {
                        params.add(p.getAnnotation(RequestParam.class));
                    }
                }
                controllerParams.put(path, params);
                System.out.println("Registered: GET " + path + " | params: " + params.size());
            }
        }
    }

    private static void scanClasspath() throws Exception {
        URL root = RunGetMapping.class.getClassLoader().getResource("");
        if (root == null) throw new RuntimeException("Could not obtain classpath root");
        scanDirectory(new File(root.toURI()), "");
    }

    private static void scanDirectory(File dir, String packageName) throws Exception {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                String sub = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                scanDirectory(file, sub);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> c = Class.forName(className);
                    if (c.isAnnotationPresent(RestController.class)) {
                        System.out.println("Found @RestController: " + className);
                        loadController(c);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {}
            }
        }
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String path  = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getRawQuery();

        try {
            // Serve index.html at root
            if (path.equals("/")) {
                String html = buildIndexHtml();
                send(exchange, 200, "text/html; charset=UTF-8", html.getBytes());
                return;
            }

            // Serve static files (css, js, images)
            if (path.startsWith("/static/")) {
                serveStaticFile(exchange, path);
                return;
            }

            // Handle @GetMapping endpoints
            String result = handle(path, query);
            if (result == null) {
                send(exchange, 404, "text/plain", "404 - Not found".getBytes());
            } else {
                send(exchange, 200, "text/plain; charset=UTF-8", result.getBytes());
            }
        } catch (Exception e) {
            send(exchange, 500, "text/plain", ("Error: " + e.getMessage()).getBytes());
        }
    }

    private static String handle(String path, String rawQuery) throws Exception {
        Method m = controllerMethods.get(path);
        if (m == null) return null;

        Map<String, String> queryParams = parseQuery(rawQuery);
        Parameter[] parameters = m.getParameters();
        Object[] methodArgs = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                RequestParam rp = parameters[i].getAnnotation(RequestParam.class);
                methodArgs[i] = queryParams.getOrDefault(rp.value(), rp.defaultValue());
            }
        }
        return m.invoke(controllerInstances.get(path), methodArgs).toString();
    }

    // Builds index.html dynamically replacing placeholders
    private static String buildIndexHtml() throws Exception {
        InputStream is = RunGetMapping.class.getClassLoader()
                .getResourceAsStream("template/index.html");
        if (is == null) throw new RuntimeException("template/index.html not found");
        String template = new String(is.readAllBytes());

        // Build endpoint cards
        StringBuilder cards = new StringBuilder();
        StringBuilder forms = new StringBuilder();

        for (String path : controllerMethods.keySet()) {
            List<RequestParam> params = controllerParams.getOrDefault(path, List.of());

            // Card with link
            String exampleUrl = buildExampleUrl(path, params);
            cards.append("<div class=\"endpoint-card\">")
                    .append("<h3>GET ").append(path).append("</h3>");

            if (!params.isEmpty()) {
                cards.append("<p>Params: ");
                for (RequestParam rp : params) {
                    cards.append("<code>").append(rp.value())
                            .append("</code> (default: <em>").append(rp.defaultValue()).append("</em>) ");
                }
                cards.append("</p>");
            }

            cards.append("<a href=\"").append(exampleUrl).append("\" class=\"btn\">")
                    .append("Try: ").append(exampleUrl).append("</a>")
                    .append("</div>\n");

            // Form per endpoint (only if it has params)
            if (!params.isEmpty()) {
                forms.append("<div class=\"test-form\">")
                        .append("<h3>").append(path).append("</h3>");
                for (RequestParam rp : params) {
                    forms.append("<label>").append(rp.value()).append(": </label>")
                            .append("<input type=\"text\" id=\"input-").append(path.replace("/", "_"))
                            .append("-").append(rp.value()).append("\" placeholder=\"")
                            .append(rp.defaultValue()).append("\"> ");
                }
                forms.append("<button onclick=\"sendRequest('").append(path).append("')\">Send</button>")
                        .append("</div>")
                        .append("<div id=\"result-").append(path.replace("/", "_"))
                        .append("\" class=\"result-box\"></div>\n");
            }
        }

        return template
                .replace("{{endpoints}}", cards.toString())
                .replace("{{forms}}", forms.toString());
    }

    // Builds an example URL with default param values
    private static String buildExampleUrl(String path, List<RequestParam> params) {
        if (params.isEmpty()) return path;
        StringBuilder url = new StringBuilder(path).append("?");
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) url.append("&");
            url.append(params.get(i).value()).append("=").append(params.get(i).defaultValue());
        }
        return url.toString();
    }

    // Serves static files from resources/static/
    private static void serveStaticFile(HttpExchange exchange, String path) throws IOException {
        String resourcePath = path.substring(1); // remove leading /
        InputStream is = RunGetMapping.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            send(exchange, 404, "text/plain", "File not found".getBytes());
            return;
        }
        String contentType = path.endsWith(".css") ? "text/css"
                : path.endsWith(".js")  ? "application/javascript"
                : path.endsWith(".png")  ? "image/png"
                : "text/plain";
        byte[] bytes = is.readAllBytes();
        send(exchange, 200, contentType, bytes);
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> map = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) return map;
        for (String pair : rawQuery.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }

    private static void send(HttpExchange ex, int code, String type, byte[] body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(code, body.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body);
        }
    }
}