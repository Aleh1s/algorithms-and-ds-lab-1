package ua.algorithms.lab1;

import ua.algorithms.lab1.property.Property;
import ua.algorithms.lab1.sorting.algorithms.ExternalMergeSortingAlgorithm;
import ua.algorithms.lab1.sorting.algorithms.StraightMergeOfBinary;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Properties properties = Property.getInstance().getProperties();
        String sourcePath = properties.getProperty("default.path.source.array.1");
        String dest1Path = properties.getProperty("default.path.dest.array.1");
        String dest2Path = properties.getProperty("default.path.dest.array.2");
        List<Long> list = List.of(2_621_512L);
        for (Long length : list) {
            long numberOfDigits = length; // 4_294_967_296L 16 gb 268_435_456L 1gb 2_621_440L 10mb
            long bytesNumber = 0;
            try (RandomAccessFile raf = new RandomAccessFile(sourcePath, "rw")) {
                raf.setLength(0);
                long start = System.nanoTime();
                for (long i = 0; i < numberOfDigits; i++) {
                    raf.writeInt(ThreadLocalRandom.current().nextInt(0, 10_000));
                    bytesNumber += Integer.BYTES;
                    double loading = (i * 100) / (double) numberOfDigits;
                    double bytes = bytesNumber / 1_073_741_824D;
                    if (i % 2 == 0) {
                        System.out.printf("Loading - %.2f, gigabytes - %.2f\r", loading, bytes);
                    }
                }
                long finish = System.nanoTime();
                System.out.println("Time to generate - " + TimeUnit.NANOSECONDS.toMinutes(finish - start));
            }

//            try (RandomAccessFile raf = new RandomAccessFile(sourcePath, "rw")) {
//                for (int i = 0; i < numberOfDigits; i++) {
//                    System.out.printf("%d ", raf.readInt());
//                }
//                System.out.println();
//            }

//            System.out.println("-".repeat(120));

            try (ExternalMergeSortingAlgorithm sortingAlgorithm = new StraightMergeOfBinary(dest1Path, dest2Path)) {
                long start = System.nanoTime();
                sortingAlgorithm.sort(new File(sourcePath));
                long finish = System.nanoTime();
                System.out.println("Time to sort - " + TimeUnit.NANOSECONDS.toMinutes(finish - start) + " minutes");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

//            System.out.println("-".repeat(120));

//            try (RandomAccessFile raf = new RandomAccessFile(sourcePath, "rw")) {
//                for (int i = 0; i < numberOfDigits; i++) {
//                    System.out.printf("%d ", raf.readInt());
//                }
//                System.out.println();
//            }

            boolean passed = true;
            int previous;
            int curr = 0;
            long iteration = 0;
            try (RandomAccessFile raf = new RandomAccessFile(sourcePath, "rw")) {
                previous = raf.readInt();
                for (long i = 0; i < numberOfDigits - 1; i++) {
                    curr = raf.readInt();
                    if (previous <= curr) {
                        previous = curr;
                    } else {
                        iteration = i;
                        passed = false;
                        break;
                    }
                }
            }

            System.out.println(passed ? length + " - passed" : String.format("%d - prev - %d, curr - %d, iteration - %d", length, previous, curr, iteration));
            System.out.println("-".repeat(120));
        }
    }
}
