package ar.com.javacuriosities.labs.file_leak;

import ar.com.javacuriosities.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Utils.start(args);

        File firstBook = new File("resources/FirstBook.txt");
        File secondBook = new File("resources/SecondBook.txt");
        File thirdBook = new File("resources/ThirdBook.txt");

        readFileAndNotClose(firstBook);
        readFileAndNotClose(secondBook);
        readFileAndNotClose(thirdBook);

        Utils.waitForUser();
    }

    public static void readFileAndNotClose(File file) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);

                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
