package ar.com.javacuriosities.labs.file_leak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Worker implements Runnable {

    private final int id;

    public Worker(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("Example.txt");

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            try {
                int count = 0;
                String line = br.readLine();
                while (line != null) {
                    if (count % 100 == 0) {
                        System.out.println(line);
                    }
                    count++;
                    line = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
