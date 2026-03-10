package org.example.runner;

import org.example.module.ClassScanner;
import org.example.module.annotations.GetMapping;
import org.example.module.annotations.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WebServerMain {

    public static void main(String[] args) throws Exception {
        List<Class<?>> restControllers = ClassScanner.findRestControllers();

        List<String> endpointPaths = new ArrayList<>();
        List<String> controllerClassNames = new ArrayList<>();
        List<List<String>> endpointParameterNames = new ArrayList<>();

        for (Class<?> controllerClass : restControllers) {
            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    String endpointPath = method.getAnnotation(GetMapping.class).value();

                    List<String> parameterNames = new ArrayList<>();
                    for (Parameter parameter : method.getParameters()) {
                        if (parameter.isAnnotationPresent(RequestParam.class)) {
                            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                            parameterNames.add(requestParam.value());
                        }
                    }

                    endpointPaths.add(endpointPath);
                    controllerClassNames.add(controllerClass.getName());
                    endpointParameterNames.add(parameterNames);

                    if (parameterNames.isEmpty()) {
                        System.out.println("Registered: GET " + endpointPath + " | params: 0");
                    } else {
                        System.out.println("Registered: GET " + endpointPath
                                + " | params: " + parameterNames.size()
                                + " -> " + parameterNames);
                    }
                }
            }
        }

        Scanner consoleInput = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n==============================");
            System.out.println("  Endpoint Menu");
            System.out.println("==============================");
            for (int i = 0; i < endpointPaths.size(); i++) {
                List<String> paramNames = endpointParameterNames.get(i);
                if (paramNames.isEmpty()) {
                    System.out.println("  " + (i + 1) + ". GET " + endpointPaths.get(i));
                } else {
                    System.out.println("  " + (i + 1) + ". GET " + endpointPaths.get(i)
                            + "  params: " + paramNames);
                }
            }
            System.out.println("  0. Exit");
            System.out.println("==============================");
            System.out.print("Select an endpoint: ");

            String userInput = consoleInput.nextLine().trim();

            if (userInput.equals("0")) {
                System.out.println("Shutting down...");
                running = false;
                continue;
            }

            int selectedIndex;
            try {
                selectedIndex = Integer.parseInt(userInput) - 1;
            } catch (NumberFormatException exception) {
                System.out.println("Invalid option. Please enter a number.");
                continue;
            }

            if (selectedIndex < 0 || selectedIndex >= endpointPaths.size()) {
                System.out.println("Invalid option. Please select a number between 0 and " + endpointPaths.size() + ".");
                continue;
            }

            String selectedPath = endpointPaths.get(selectedIndex);
            String selectedControllerClassName = controllerClassNames.get(selectedIndex);
            List<String> selectedParameterNames = endpointParameterNames.get(selectedIndex);

            StringBuilder urlBuilder = new StringBuilder(selectedPath);

            if (!selectedParameterNames.isEmpty()) {
                urlBuilder.append("?");
                for (int i = 0; i < selectedParameterNames.size(); i++) {
                    String paramName = selectedParameterNames.get(i);
                    System.out.print("Enter value for '" + paramName + "': ");
                    String paramValue = consoleInput.nextLine().trim();
                    if (i > 0) {
                        urlBuilder.append("&");
                    }
                    urlBuilder.append(paramName).append("=").append(paramValue);
                }
            }

            String fullUrl = urlBuilder.toString();

            System.out.println("\n--- Invoking via InvokeMain -> RunGetMapping ---");
            InvokeMain.main(
                    "org.example.runner.RunGetMapping",
                    selectedControllerClassName,
                    fullUrl
            );
            System.out.println("--- End of invocation ---");
        }

        consoleInput.close();
    }
}
