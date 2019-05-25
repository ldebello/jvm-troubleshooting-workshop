package ar.com.javacuriosities.labs.multiple_classloaders;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) {
        CustomLoader loaderA = new CustomLoader("A");
        CustomLoader loaderB = new CustomLoader("B");
        CustomLoader loaderC = new CustomLoader("C");

        try {
            runMethod("startLeak", loaderA.loadClass(Leaker.class.getCanonicalName()));
            runMethod("startNonLeak", loaderB.loadClass(Leaker.class.getCanonicalName()));
            runMethod("startFastLeak", loaderC.loadClass(Leaker.class.getCanonicalName()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void runMethod(String method, Class<?> clazz) {
        try {
            Method m = clazz.getMethod(method);
            m.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}