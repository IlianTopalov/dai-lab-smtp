package ch.heig.dai.lab.smtp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class ListReader {

    private ListReader() {}

    public static List<String> readList(String fileName) throws IOException {
        List<String> list = new LinkedList<>();

        // Open file, read and append each line to <list>
        BufferedReader reader = new BufferedReader(new InputStreamReader(
            new FileInputStream(fileName),
            StandardCharsets.UTF_8
        ));

        String line;
        while ((line = reader.readLine()) != null)
            list.add(line);

        reader.close();

        return list;
    }
}
