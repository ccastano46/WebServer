package org.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import org.example.module.ClassScanner;
import org.example.module.annotations.GetMapping;
import org.example.module.annotations.RequestParam;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServerMain {

    private static final Map<String, Method> routeMethodMap = new HashMap<>();
    private static final Map<String, Object> routeInstanceMap = new HashMap<>();

    public static void main(String[] args) throws Exception {


        List<Class<?>> controllers = ClassScanner.findRestControllers();

        for (Class<?> controllerClass : controllers) {
            Object instance = controllerClass.getDeclaredConstructor().newInstance();

            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    String path = method.getAnnotation(GetMapping.class).value();
                    routeMethodMap.put(path, method);
                    routeInstanceMap.put(path, instance);
                    System.out.println("Registered route: GET " + path);
                }
            }
        }


        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);


        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/") || path.equals("/static/index.html")) {
                try {
                    serveDashboard(exchange);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return;
            }

            if (path.equals("/styles.css")) {
                try {
                    serveStaticFile(exchange, "static/styles.css", "text/css");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            if (path.equals("/script.js")) {
                try {
                    serveStaticFile(exchange, "static/script.js", "application/javascript");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return;
            }


            try {
                handleApiRequest(exchange, path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


        server.createContext("/api/routes", exchange -> {
            String json = buildRoutesJson();
            try {
                sendResponse(exchange, 200, "application/json", json.getBytes());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Servidor corriendo en http://localhost:8080");
    }

    // -------------------------------------------------------
    // Handler principal de la API
    // -------------------------------------------------------
    private static void handleApiRequest(HttpExchange exchange, String path) throws Exception {
        Method method = routeMethodMap.get(path);

        String response;
        int statusCode;

        if (method == null) {
            response = "404 - Ruta no encontrada: " + path;
            statusCode = 404;
        } else {
            try {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> queryParams = parseQueryParams(query);
                Object[] methodArgs = resolveArgs(method, queryParams);
                Object instance = routeInstanceMap.get(path);
                Object result = method.invoke(instance, methodArgs);
                response = result != null ? result.toString() : "";
                statusCode = 200;
            } catch (Exception e) {
                response = "500 - Error interno: " + e.getMessage();
                statusCode = 500;
            }
        }

        sendResponse(exchange, statusCode, "application/json", response.getBytes());
    }

    // -------------------------------------------------------
    // Sirve el HTML del dashboard
    // -------------------------------------------------------
    private static void serveDashboard(HttpExchange exchange) throws Exception {
        var stream = WebServerMain.class.getClassLoader()
                .getResourceAsStream("static/index.html");

        if (stream == null) {
            sendResponse(exchange, 404, "text/plain", "index.html not found".getBytes());
            return;
        }

        byte[] bytes = stream.readAllBytes();
        sendResponse(exchange, 200, "text/html; charset=UTF-8", bytes);
    }

    // -------------------------------------------------------
    // Construye el JSON con todas las rutas registradas
    // -------------------------------------------------------
    private static String buildRoutesJson() {
        StringBuilder json = new StringBuilder("[");

        List<Map.Entry<String, Method>> entries = new ArrayList<>(routeMethodMap.entrySet());

        for (int e = 0; e < entries.size(); e++) {
            String path = entries.get(e).getKey();
            Method m = entries.get(e).getValue();
            Parameter[] params = m.getParameters();

            json.append("{");
            json.append("\"path\":\"").append(path).append("\",");
            json.append("\"params\":[");

            List<Parameter> annotatedParams = new ArrayList<>();
            for (Parameter p : params) {
                if (p.isAnnotationPresent(RequestParam.class)) annotatedParams.add(p);
            }

            for (int i = 0; i < annotatedParams.size(); i++) {
                RequestParam rp = annotatedParams.get(i).getAnnotation(RequestParam.class);
                json.append("{");
                json.append("\"name\":\"").append(rp.value()).append("\",");
                json.append("\"default\":\"").append(rp.defaultValue()).append("\"");
                json.append("}");
                if (i < annotatedParams.size() - 1) json.append(",");
            }

            json.append("]}");
            if (e < entries.size() - 1) json.append(",");
        }

        json.append("]");
        return json.toString();
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private static void serveStaticFile(HttpExchange exchange, String resource, String contentType) throws Exception {
        var stream = WebServerMain.class.getClassLoader().getResourceAsStream(resource);
        if (stream == null) {
            sendResponse(exchange, 404, "text/plain", "Not found".getBytes());
            return;
        }
        sendResponse(exchange, 200, contentType, stream.readAllBytes());
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) params.put(kv[0], kv[1]);
        }
        return params;
    }

    private static Object[] resolveArgs(Method method, Map<String, String> queryParams) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                RequestParam rp = parameters[i].getAnnotation(RequestParam.class);
                args[i] = queryParams.getOrDefault(rp.value(), rp.defaultValue());
            }
        }
        return args;
    }

    private static void sendResponse(HttpExchange exchange, int status, String contentType, byte[] body) throws Exception {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, body.length);
        OutputStream os = exchange.getResponseBody();
        os.write(body);
        os.close();
    }
}