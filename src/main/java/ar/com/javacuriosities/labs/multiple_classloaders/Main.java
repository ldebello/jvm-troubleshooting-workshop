package ar.com.javacuriosities.labs.multiple_classloaders;

import ar.com.javacuriosities.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int COUNT_CLASSLOADER_A = 5;
    private static final int COUNT_CLASSLOADER_B = 10;
    private static final int COUNT_CLASSLOADER_C = 15;

    public static void main(String[] args) {
        CustomLoader loaderA = new CustomLoader("A");
        CustomLoader loaderB = new CustomLoader("B");
        CustomLoader loaderC = new CustomLoader("C");

        try {
            List<Object> objects = new ArrayList<>();

            DataObject dataObject = new DataObject();

            Object dataObjectClassloaderA = null;
            Object dataObjectClassloaderB = null;
            Object dataObjectClassloaderC = null;

            objects.add(dataObject);

            for (int i = 0; i < COUNT_CLASSLOADER_A; i++) {
                dataObjectClassloaderA = loaderA.loadClass(DataObject.class.getCanonicalName()).newInstance();
                objects.add(dataObjectClassloaderA);
            }

            for (int i = 0; i < COUNT_CLASSLOADER_B; i++) {
                dataObjectClassloaderB = loaderB.loadClass(DataObject.class.getCanonicalName()).newInstance();
                objects.add(dataObjectClassloaderB);
            }

            for (int i = 0; i < COUNT_CLASSLOADER_C; i++) {
                dataObjectClassloaderC = loaderC.loadClass(DataObject.class.getCanonicalName()).newInstance();
                objects.add(dataObjectClassloaderC);
            }

            Utils.waitForUser();

            dataObject = (DataObject) dataObjectClassloaderA;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}