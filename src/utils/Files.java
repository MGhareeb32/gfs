package utils;

import gfs.data.FileContent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Files {

    public static FileContent readFile(FileContent filename)
        throws FileNotFoundException {

        File file = new File(filename.path);
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

    public static void writeFile(FileContent filename)
        throws IOException {

        File file = new File(filename.path);
        String oldData = file.exists() ?
            readFile(new FileContent(filename.path, null)).data : "";
        FileWriter fw = new FileWriter(file);
        fw.write(oldData + filename.data);
        fw.close();
    }

    public static void getFiles(File dir, ArrayList<File> out) {
        if (!dir.exists() || dir.listFiles() == null)
            return;
        for (File f : dir.listFiles())
            if (f.isFile())
                out.add(f);
            else
                getFiles(f, out);
    }

    public static void main(String[] args) throws IOException {
        // list
        ArrayList<File> out = new ArrayList<File>(); 
        getFiles(new File("."), out);
        for (File file : out) {
            System.out.println(file);
        }
        // write
        System.out.println(readFile(new FileContent("lala", null)).data);
        writeFile(new FileContent("lala", "x"));
        System.out.println(readFile(new FileContent("lala", null)).data);
        writeFile(new FileContent("lala", "a"));
        System.out.println(readFile(new FileContent("lala", null)).data);
    }
}
