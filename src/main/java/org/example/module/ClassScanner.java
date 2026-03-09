package org.example.module;

import org.example.module.annotations.RestController;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {

    public static List<Class<?>> findRestControllers() throws Exception {
        List<Class<?>> controllers = new ArrayList<>();


        URL root = ClassScanner.class.getClassLoader().getResource("");
        if (root == null) throw new RuntimeException("Impossible to read classpath");

        File rootDir = new File(root.toURI());

        scanDirectory(rootDir, "", controllers);

        return controllers;
    }

    private static void scanDirectory(File dir, String packageName, List<Class<?>> result) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                String subPackage = packageName.isEmpty()
                        ? file.getName()
                        : packageName + "." + file.getName();
                scanDirectory(file, subPackage, result);

            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");

                try {
                    Class<?> c = Class.forName(className);
                    if (c.isAnnotationPresent(RestController.class)) {
                        System.out.println("Found @RestController: " + className);
                        result.add(c);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                }
            }
        }
    }
}
