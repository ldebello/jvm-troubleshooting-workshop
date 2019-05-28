package ar.com.javacuriosities.labs.native_memory;

import ar.com.javacuriosities.utils.Utils;

import java.nio.ByteBuffer;

public class Main {
    private static ByteBuffer humonguosBuffer = ByteBuffer.allocateDirect(1024 * 1024 * 1024);

    public static void main(String[] args) {
        System.out.println("Direct allocation: " + humonguosBuffer.capacity());
        Utils.waitForUser();
    }
}
