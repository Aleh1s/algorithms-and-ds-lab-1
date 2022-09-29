package ua.algorithms.lab1.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LogUtils {

    public static void show(File file) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            for (int i = 0; i < raf.length() / Integer.BYTES; i++) {
                System.out.printf("%d ", raf.readInt());
            }
            System.out.println();
        } catch (FileNotFoundException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        }
    }

}
