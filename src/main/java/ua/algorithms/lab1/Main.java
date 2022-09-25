package ua.algorithms.lab1;

import ua.algorithms.lab1.property.Property;
import ua.algorithms.lab1.sorting.algorithms.ExternalMergeSortingAlgorithm;
import ua.algorithms.lab1.sorting.algorithms.StraightMergeOfBinary;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
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
        long numberOfDigits = 262_144L; // 4_294_967_296L 16 gb 268_435_456L 1gb 2_621_440L 10mb
//        long bytesNumber = 0;
//        try (RandomAccessFile raf = new RandomAccessFile(sourcePath, "rw")) {
//            raf.setLength(0);
//            long start = System.nanoTime();
//            for (long i = 0; i < numberOfDigits; i++) {
//                raf.writeInt(ThreadLocalRandom.current().nextInt(0, 1_000));
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

//        boolean passed = true;
//        int previous;
//        int curr = 0;
//        long iteration = 0;
//        try (RandomAccessFile raf = new RandomAccessFile(sourcePath, "rw")) {
//            previous = raf.readInt();
//            for (long i = 0; i < numberOfDigits - 1; i++) {
//                curr = raf.readInt();
//                if (previous <= curr) {
//                    previous = curr;
//                } else {
//                    iteration = i;
//                    passed = false;
//                    break;
//                }
//            }
//        }
//        System.out.println(passed ? "Passed" : String.format("prev - %d, curr - %d, iteration - %d", previous, curr, iteration));

//        try (RandomAccessFile raf = new RandomAccessFile(sourcePath, "rw")) {
//            for (int i = 0; i < numberOfDigits; i++) {
//                int digit = raf.readInt();
//                if (i < 10 || i > numberOfDigits - 10) {
//                    System.out.printf("%d ", digit);
//                }
//            }
//            System.out.println();
//        }
//
//        System.out.println("-".repeat(120));
//
//        try (ExternalMergeSortingAlgorithm sortingAlgorithm = new StraightMergeOfBinary(dest1Path, dest2Path)) {
//            long start = System.nanoTime();
//            sortingAlgorithm.sort(new File(sourcePath));
//            long finish = System.nanoTime();
//            System.out.println(TimeUnit.NANOSECONDS.toMinutes(finish - start) + " minutes");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        System.out.println("-".repeat(120));
//
//        try (RandomAccessFile raf = new RandomAccessFile(sourcePath, "rw")) {
//            for (int i = 0; i < numberOfDigits; i++) {
//                int digit = raf.readInt();
//                if (i < 10 || i > numberOfDigits - 10) {
//                    System.out.printf("%d ", digit);
//                }
//            }
//            System.out.println();
//        }

//        straightMerge(new int[]{8, 23, 5, 65, 44, 33, 16, 3, 9, 10});
    }

    // 8 23 5 65 44 33 16 3 9 10

//    public static void straightMerge(int[] arr) {
//        int groupNumber = arr.length, digitsInGroup = 1, pBuff1 = 0, pBuff2 = 0, pArr = 0;
//        while (groupNumber != 1) {
//            int buff1Length, buff2Length;
//            if (digitsInGroup <= arr.length) {
//                if (groupNumber % 2 == 0) {
//                    int reminder = (groupNumber * digitsInGroup) - arr.length;
//                    buff1Length = ((groupNumber / 2) * digitsInGroup) + reminder;
//                    buff2Length = (groupNumber / 2) * digitsInGroup;
//                } else {
//
//                }
//            } else {
//                buff1Length = (groupNumber / 2) * digitsInGroup;
//                buff2Length = arr.length - buff1Length;
//            }
//            int[] buff1 = new int[buff1Length];
//            int[] buff2 = new int[buff2Length];
//            for (int i = 0; i < groupNumber; i++) {
//                for (int j = 0; j < digitsInGroup; j++) {
//                    if (pArr < arr.length) {
//                        if (i % 2 == 0) {
//                            buff1[pBuff1++] = arr[pArr++];
//                        } else {
//                            buff2[pBuff2++] = arr[pArr++];
//                        }
//                    } else {
//                        break;
//                    }
//                }
//            }
//            pBuff1 = 0;
//            pBuff2 = 0;
//            pArr = 0;
//            System.out.println("buff1 - " + Arrays.toString(buff1) + "pBuff1 - " + pBuff1);
//            System.out.println("buff2 - " + Arrays.toString(buff2) + "pBuff2 - " + pBuff2);
//            System.out.println("----------------------------------------------------------");
//            groupNumber = (int) ceil(groupNumber / 2f);
//            digitsInGroup *= 2;
//        }
//    }
}
