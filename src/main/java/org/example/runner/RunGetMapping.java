package org.example.runner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.example.module.annotations.GetMapping;
import org.example.module.annotations.RequestParam;
import org.example.module.annotations.RestController;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RunGetMapping {
    private static final Map<String, Method> controllerMethods = new HashMap<>();
    private static final Map<String, Object> controllerInstances = new HashMap<>();

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            // Load POJO
            loadController(Class.forName(args[0]));
        } else {
            // Scan ClassPath
            scanClasspath();
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", RunGetMapping::handleRequest);
        server.start();
        System.out.println("Server in http://localhost:8080");
    }

    // Load a specific controller
    private static void loadController(Class<?> c) throws Exception {
        if (!c.isAnnotationPresent(RestController.class)) {
            System.out.println("The class doesn't have @RestController: " + c.getName());
            return;
        }
        Object instance = c.getDeclaredConstructor().newInstance();
        for (Method m : c.getDeclaredMethods()) {
            if (m.isAnnotationPresent(GetMapping.class)) {
                String path = m.getAnnotation(GetMapping.class).value();
                controllerMethods.put(path, m);
                controllerInstances.put(path, instance);
                System.out.println("Registered: GET " + path);
            }
        }
    }

    // Scan Classpath looking for @RestController annotations
    private static void scanClasspath() throws Exception {
        URL root = RunGetMapping.class.getClassLoader().getResource("");
        if (root == null) throw new RuntimeException("The classpath could not be obtained");

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
            String result = handle(path, query);
            if (result == null) {
                send(exchange, 404, "text/plain", "404 - Not found");
            } else {
                send(exchange, 200, "text/plain; charset=UTF-8", result);
            }
        } catch (Exception e) {
            send(exchange, 500, "text/plain", "Error: " + e.getMessage());
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

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> map = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) return map;
        for (String pair : rawQuery.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }

    private static void send(HttpExchange ex, int code, String type, String body) throws IOException {
        byte[] bytes = body.getBytes();
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}