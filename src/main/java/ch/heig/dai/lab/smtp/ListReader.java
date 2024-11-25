package ch.heig.dai.lab.smtp;

import java.io.*;
import java.util.ArrayList;

public class ListReader {
    private ListReader() {}
    public static ArrayList<String> readList(String fileName) {
        ArrayList<String> list = new ArrayList<String>() {};

        // Open file, read and append each line to <list>
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }

            reader.close();
            return list;

        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
