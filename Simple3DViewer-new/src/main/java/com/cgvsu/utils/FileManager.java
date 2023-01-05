package com.cgvsu.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public FileManager() {
    }

    public static List<String> readAllLinesFromFile(String path) throws IOException {
        if (path == null) {
            return null;
        } else {
            return isFileExist(path) ? Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8) : null;
        }
    }

    public static String readAllTextFromFile(String path) throws IOException {
        if (path == null) {
            return null;
        } else {
            return isFileExist(path) ? Files.readString(Paths.get(path), StandardCharsets.UTF_8) : null;
        }
    }

    public static void createFileWithText(String filePath, ArrayList<String> vertices, ArrayList<String> textureVertices, ArrayList<String> normals, ArrayList<String> poly) throws IOException {
        if (!isFileExist(filePath)) {
            File file = new File(filePath);
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            String s = filePath.substring(0, filePath.indexOf("."));
            fw.write("# object" + s + "\n\n");
            writeToFile(fw, vertices);
            countLine(fw, vertices.size(), " vertices");
            writeToFile(fw, normals);
            countLine(fw, normals.size(), " vertex normals");
            writeToFile(fw, textureVertices);
            countLine(fw, textureVertices.size(), " texture coords");
            writeToFile(fw, poly);
            countLine(fw, poly.size(), " polygons");
            fw.close();
        }
    }

    public static void writeToFile(FileWriter fw, ArrayList<String> line) throws IOException {
        if (line != null) {
            for (int l = 0; l < line.size(); l++) {
                fw.write(line.get(l) + "\n");
            }
            fw.write("\n");
        }
    }

    private static void countLine(FileWriter fw, int size, String type) throws IOException {
        String s = "# ";
        s += size + 1;
        s += type + " \n\n";
        fw.write(s);
    }

    public static boolean isFileExist(String filePath) {
        return filePath == null ? false : (new File(filePath)).exists();
    }
}

