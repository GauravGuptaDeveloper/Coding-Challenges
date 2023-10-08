package com.learning.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtility {
    public List<String> readFile(String fileName) {
        List<String> fileContent = new ArrayList<>();

        try {
            File file = new File(fileName);

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            Stream<String> lines = bufferedReader.lines();
            fileContent = lines.collect(Collectors.toList());

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Caught exception while reading file " + e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileContent;
    }

    public String readFileContentAsString(String fileName) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));

            String str;
            while ((str = in.readLine()) != null) {
                builder.append(str)
//                        .append("\n")
                ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public byte[] readBytes(String fileName) {
        try {
            return Files.readAllBytes(Paths.get(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
