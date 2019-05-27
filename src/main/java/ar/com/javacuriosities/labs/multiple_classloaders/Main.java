package ar.com.javacuriosities.labs.multiple_classloaders;

import ar.com.javacuriosities.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int COUNT_CLASS_LOADER_A = 5;
    private static final int COUNT_CLASS_LOADER_B = 10;
    private static final int COUNT_CLASS_LOADER_C = 15;

    public static void main(String[] args) {
        Utils.start(args);

        CustomLoader loaderA = new CustomLoader("A");
        CustomLoader loaderB = new CustomLoader("B");
        CustomLoader loaderC = new CustomLoader("C");

        try {
            List<Object> objects = new ArrayList<>();

            DataObject dataObject = new DataObject();

            Object dataObjectClassLoaderA = null;
            Object dataObjectClassLoaderB = null;
            Object dataObjectClassLoaderC = null;

            objects.add(dataObject);

            for (int i = 0; i < COUNT_CLASS_LOADER_A; i++) {
                dataObjectClassLoaderA = loaderA.loadClass(DataObject.class.getCanonicalName()).newInstance();
                objects.add(dataObjectClassLoaderA);
            }

            for (int i = 0; i < COUNT_CLASS_LOADER_B; i++) {
                dataObjectClassLoaderB = loaderB.loadClass(DataObject.class.getCanonicalName()).newInstance();
                objects.add(dataObjectClassLoaderB);
            }

            for (int i = 0; i < COUNT_CLASS_LOADER_C; i++) {
                dataObjectClassLoaderC = loaderC.loadClass(DataObject.class.getCanonicalName()).newInstance();
                objects.add(dataObjectClassLoaderC);
            }

            Utils.waitForUser();

            dataObject = (DataObject) dataObjectClassLoaderA;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}