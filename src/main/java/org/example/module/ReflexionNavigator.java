package org.example.module;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static java.lang.System.out;

public class ReflexionNavigator {

    static void main(String[] args) throws Exception {
        Class<?> c = Class.forName(args[0]);
        ReflexionNavigator navigator = new ReflexionNavigator();

        Method printMembers = ReflexionNavigator.class
                .getDeclaredMethod("printMembers", Member[].class, String.class);
        printMembers.setAccessible(true);

        printMembers.invoke(navigator, c.getFields(),       "Public fields");
        printMembers.invoke(navigator, c.getDeclaredFields(),"All fields");
        printMembers.invoke(navigator, c.getConstructors(),  "Public Constructors");
        printMembers.invoke(navigator, c.getMethods(),       "Public Methods");
        printMembers.invoke(navigator, c.getDeclaredMethods(),"All methods");
    }

    private static void printMembers(Member[] mbrs, String s) {
        out.format("%s:%n", s);
        for (Member mbr : mbrs) {
            if (mbr instanceof Field) {
                out.format(" %s%n", ((Field) mbr).toGenericString());
            }
            else if (mbr instanceof Constructor) {
                out.format(" %s%n", ((Constructor) mbr).toGenericString());
            }
            else if (mbr instanceof Method) {
                out.format(" %s%n", ((Method) mbr).toGenericString());
            }
        }
        if (mbrs.length == 0) {
            out.format(" -- No %s --%n", s);
        }
        out.format("%n");
    }
}