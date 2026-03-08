package org.example.runner;

import org.example.module.annotations.GetMapping;
import org.example.module.annotations.RequestParam;
import org.example.module.annotations.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class RunGetMapping {
    private static final Map<String, Method> controllerMethods = new HashMap<>();

    static void main(String[] args) throws Exception {
        Class<?> c = Class.forName(args[0]);

        if (c.isAnnotationPresent(RestController.class)) {
            Object instance = c.getDeclaredConstructor().newInstance();

            for (Method m : c.getDeclaredMethods()) {
                if (m.isAnnotationPresent(GetMapping.class)) {
                    GetMapping a = m.getAnnotation(GetMapping.class);
                    controllerMethods.put(a.value(), m);
                }
            }

            String[] pathParts = args[1].split("\\?");
            String path = pathParts[0];
            Map<String, String> queryParams = new HashMap<>();

            if (pathParts.length > 1) {
                for (String param : pathParts[1].split("&")) {
                    String[] kv = param.split("=");
                    queryParams.put(kv[0], kv[1]);
                }
            }

            System.out.println("Invoking method for path: " + path);
            Method m = controllerMethods.get(path);

            Parameter[] parameters = m.getParameters();
            Object[] methodArgs = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                    RequestParam rp = parameters[i].getAnnotation(RequestParam.class);
                    methodArgs[i] = queryParams.getOrDefault(rp.value(), rp.defaultValue());
                }
            }

            System.out.println(m.invoke(instance, methodArgs));
        }
    }
}
