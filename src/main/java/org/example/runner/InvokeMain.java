package org.example.runner;

import java.lang.reflect.Method;

public class InvokeMain {

    // Works both from terminal and programmatically
    public static void main(String className, String... targetArgs) {
        invoke(className, targetArgs);
    }

    private static void invoke(String className, String[] targetArgs) {
        try {
            Class<?> c = Class.forName(className);
            Method main = c.getDeclaredMethod("main", String[].class);
            main.setAccessible(true);
            System.out.format("Invoking %s.main()%n", c.getName());
            main.invoke(null, (Object) targetArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}