package ua.algorithms.lab1;

import ua.algorithms.lab1.property.Property;
import ua.algorithms.lab1.sorting.algorithms.ExternalMergeSortingAlgorithm;
import ua.algorithms.lab1.sorting.algorithms.StraightMerge;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {
        Properties properties = Property.getInstance().getProperties();
        String sourcePath = properties.getProperty("default.path.source.array.1");
        String dest1Path = properties.getProperty("default.path.dest.array.1");
        String dest2Path = properties.getProperty("default.path.dest.array.2");
//        long numberOfDigits = 10; // 4_294_967_296L 16 gb
//        long bytesNumber = 0;
//        File file = new File(sourcePath);
//        file.createNewFile();
//        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
//            raf.setLength(0);
//            long start = System.nanoTime();
//            for (long i = 0; i < numberOfDigits; i++) {
//                raf.writeInt(ThreadLocalRandom.current().nextInt(0, 100));
//                bytesNumber += Integer.BYTES;
//                double loading = (i * 100) / (double) numberOfDigits;
//                double bytes = bytesNumber / 1_073_741_824D;
//                if (i % 2 == 0) {
//                    System.out.printf("Loading - %.2f, gigabytes - %.2f\r", loading, bytes);
//                }
//            }
//            long finish = System.nanoTime();
//            System.out.println(TimeUnit.NANOSECONDS.toMinutes(finish - start));
//        }

        try (RandomAccessFile raf = new RandomAccessFile("D:\\test\\temp.bin", "rw")) {
            for (int i = 0; i < 10; i++) {
                System.out.println(raf.readInt());
            }
        }

        try (ExternalMergeSortingAlgorithm sortingAlgorithm = new StraightMerge(dest1Path, dest2Path)) {
            long start = System.nanoTime();
            sortingAlgorithm.sort(new File("D:\\test\\temp.bin"));
            long finish = System.nanoTime();
            System.out.println(TimeUnit.NANOSECONDS.toMinutes(finish - start) + " minutes");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//
//        try (RandomAccessFile raf = new RandomAccessFile("D:\\test\\temp.bin", "rw")) {
//            for (int i = 0; i < 10; i++) {
//                System.out.println(raf.readInt());
//            }
//        }
    }
}
