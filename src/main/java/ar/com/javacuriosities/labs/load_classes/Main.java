package ar.com.javacuriosities.labs.load_classes;

import ar.com.javacuriosities.utils.Utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {

    public static final String CLASS_NAME = "Test";

    public static void main(String[] args) {
        Utils.start(args);

        File resources = new File("classes.jar");

        while (true) {
            try {
                URLClassLoader customClassLoader = new URLClassLoader(new URL[]{resources.toURI().toURL()});

                Class clazz = customClassLoader.loadClass(CLASS_NAME);
                customClassLoader.close();

                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}