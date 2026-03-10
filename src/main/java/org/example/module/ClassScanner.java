package org.example.module;

import org.example.module.annotations.RestController;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {

    public static List<Class<?>> findRestControllers() throws Exception {
        List<Class<?>> restControllerClasses = new ArrayList<>();

        URL classpathRoot = ClassScanner.class.getClassLoader().getResource("");
        if (classpathRoot == null) throw new RuntimeException("Unable to read classpath root");

        File classpathRootDirectory = new File(classpathRoot.toURI());

        scanDirectory(classpathRootDirectory, "", restControllerClasses);

        return restControllerClasses;
    }

    private static void scanDirectory(File currentDirectory, String packageName, List<Class<?>> foundControllers) {
        for (File fileEntry : currentDirectory.listFiles()) {
            if (fileEntry.isDirectory()) {
                String childPackageName = packageName.isEmpty()
                        ? fileEntry.getName()
                        : packageName + "." + fileEntry.getName();
                scanDirectory(fileEntry, childPackageName, foundControllers);

            } else if (fileEntry.getName().endsWith(".class")) {
                String fullyQualifiedClassName = packageName + "." + fileEntry.getName().replace(".class", "");

                try {
                    Class<?> loadedClass = Class.forName(fullyQualifiedClassName);
                    if (loadedClass.isAnnotationPresent(RestController.class)) {
                        System.out.println("Found @RestController: " + fullyQualifiedClassName);
                        foundControllers.add(loadedClass);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                }
            }
        }
    }
}
