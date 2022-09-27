package ua.algorithms.lab1;

import ua.algorithms.lab1.property.Property;
import ua.algorithms.lab1.sorting.algorithms.ExternalStraightMergeSortingAlgorithm;
import ua.algorithms.lab1.sorting.algorithms.ImprovedStraightMerge;
import ua.algorithms.lab1.sorting.algorithms.StraightMerge;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        Properties properties = Property.getInstance().getProperties();
        String sourcePath = properties.getProperty("default.path.source");
        String outputPath = properties.getProperty("default.path.output");
        int chunkSize = 104_857_600;
        int numberOfChunks = 3;
        try (RandomAccessFile raf = new RandomAccessFile(sourcePath, "rw")) {
            raf.setLength(0);
            for (int i = 0; i < numberOfChunks; i++) {
                int[] random;
                if (i != 2) {
                    random = new int[chunkSize / Integer.BYTES];
                } else {
                    random = new int[(chunkSize / 2) / Integer.BYTES];
                }
                for (int j = 0; j < random.length; j++) {
                    random[j] = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
                }
                byte[] from = ImprovedStraightMerge.from(random);
                raf.write(from);
            }
        }

        long start = System.nanoTime();
        ImprovedStraightMerge.sort(new File(sourcePath));
        long finish = System.nanoTime();
        System.out.println(TimeUnit.NANOSECONDS.toMinutes(finish - start));

        try (RandomAccessFile raf = new RandomAccessFile(outputPath, "rw")) {
            boolean passed = true;
            int previous = 0;
            int curr = 0;
            for (int i = 0; i < numberOfChunks; i++) {
                byte[] chunk;
                if (i != 2) {
                    chunk = new byte[chunkSize];
                } else {
                    chunk = new byte[chunkSize / 2];
                }
                raf.read(chunk, 0, chunk.length);
                int[] from = ImprovedStraightMerge.from(chunk);
                previous = from[0];
                for (int j = 1; j < from.length; j++) {
                    curr = from[j];
                    if (previous <= curr) {
                        previous = curr;
                    } else {
                        passed = false;
                        break;
                    }
                }
                if (!passed) {
                    break;
                }
            }
            System.out.println(passed ? "Passed" : String.format("Not passed, prev - %d, curr - %d", previous, curr));
        }
    }
}
