package utils;

import gfs.data.FileContent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Files {

    public static FileContent readFile(File root, FileContent filename)
            throws FileNotFoundException {

        File file = new File(root.getPath() + File.separator + filename.path);
        StringBuilder b = new StringBuilder();
        Scanner fr = new Scanner(file);
        while (fr.hasNext()) {
            b.append(fr.nextLine());
            if (fr.hasNext())
                b.append("\n");
        }
        fr.close();
        return new FileContent(filename.path, b.toString());
    }

    public static void writeFile(File root, FileContent filename)
            throws IOException {

        File file = new File(root.getPath() + File.separator + filename.path);
        file.getParentFile().mkdirs();
        String oldData = file.exists() ?
            readFile(root, new FileContent(filename.path, null)).data : "";
        FileWriter fw = new FileWriter(file);
        fw.write(oldData + filename.data);
        fw.flush();
        fw.close();
    }

    public static void getFiles(File root, ArrayList<File> out) {
        if (root == null || !root.exists())
            return;
        if (root.isDirectory())
            for (File f : root.listFiles())
                getFiles(f, out);
        else
            out.add(root);
    }

    public static void deleteDir(File root) {
        if (root == null || !root.exists())
            return;
        if (root.isDirectory())
            for (File f : root.listFiles())
                deleteDir(f);
        root.delete();
    }

    public static void main(String[] args) throws IOException {
        // list
        ArrayList<File> out = new ArrayList<File>();
        getFiles(new File("."), out);
        for (File file : out) {
            System.out.println(file);
        }
        // write
        File root = new File("./gfs");
        String file = "file.txt";
        writeFile(root, new FileContent(file, "x"));
        System.out.println(readFile(root, new FileContent(file, null)).data);
        writeFile(root, new FileContent(file, "a"));
        System.out.println(readFile(root, new FileContent(file, null)).data);
        deleteDir(root);
    }
}
