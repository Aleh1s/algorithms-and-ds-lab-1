package ua.algorithms.lab1.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LogUtils {

    public static void outputData(File source, long size) {
        try (RandomAccessFile raf = new RandomAccessFile(source, "rw")) {
            for (long i = 0; i < size; i++) {
                System.out.printf("%d ", raf.readInt());
            }
            System.out.println();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
