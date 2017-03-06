package app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Galin on 21.2.2017 г..
 */
public class FileIO {

    private static final String FILE_PATH = "page_source.txt";

    public static String readFile(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String data = sb.toString();
            return data;
        }
    }
}
