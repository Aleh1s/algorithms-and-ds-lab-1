package ua.algorithms.lab1;

import ua.algorithms.lab1.exception.FetchFileLengthException;
import ua.algorithms.lab1.mvc.controller.ControllerImpl;
import ua.algorithms.lab1.mvc.model.ImprovedStraightMerge;
import ua.algorithms.lab1.mvc.model.Model;
import ua.algorithms.lab1.mvc.view.ConsoleView;
import ua.algorithms.lab1.mvc.view.View;
import ua.algorithms.lab1.property.Property;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        String path;
        boolean exit = false;
        View view = new ConsoleView(new ControllerImpl());
        while (!exit) {
            System.out.print("Write path of file to sort or -1 to exit: ");
            if (!(path = new Scanner(System.in).nextLine()).equals("-1")) {
                boolean isChoiceCorrect = false;
                while (!isChoiceCorrect) {
                    System.out.println("Choose sorting algorithm: \n1. Straight merge\n2. Improved straight merge");
                    String choice = new Scanner(System.in).nextLine();
                    if (choice.equals("1")) {
                        isChoiceCorrect = true;
                    } else if (choice.equals("2")) {
                        isChoiceCorrect = true;
                        view.sortSourceFile(path, choice);
                    } else {
                        System.out.println("Bad choice! Try again.");
                    }
                }
            } else {
                exit = true;
            }
        }
//        Properties properties = Property.getInstance().getProperties();
//        String sourcePath = properties.getProperty("default.path.source");
//        int chunkSize = 104_857_600;
//        int numberOfChunks = 5;
//        try (RandomAccessFile raf = new RandomAccessFile(sourcePath, "rw")) {
//            raf.setLength(0);
//            for (int i = 0; i < numberOfChunks; i++) {
//                int[] random = new int[chunkSize / Integer.BYTES];
//                for (int j = 0; j < random.length; j++) {
//                    random[j] = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
//                }
//                byte[] from = Utils.from(random);
//                raf.write(from);
//            }
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        long start = System.nanoTime();
//        ImprovedStraightMerge.sort(new File(sourcePath));
//        long finish = System.nanoTime();
//        System.out.println("Sorting took " + TimeUnit.NANOSECONDS.toMinutes(finish - start) + " minutes");
//
//        try (RandomAccessFile raf = new RandomAccessFile(outputPath, "rw")) {
//            boolean passed = true;
//            int previous = 0;
//            int curr = 0;
//            for (int i = 0; i < numberOfChunks; i++) {
//                byte[] chunk = new byte[chunkSize];
//                raf.read(chunk, 0, chunk.length);
//                int[] from = ImprovedStraightMerge.from(chunk);
//                previous = from[0];
//                for (int j = 1; j < from.length; j++) {
//                    curr = from[j];
//                    if (previous <= curr) {
//                        previous = curr;
//                    } else {
//                        passed = false;
//                        break;
//                    }
//                }
//                if (!passed) {
//                    break;
//                }
//            }
//            System.out.println(passed ? "Passed" : String.format("Not passed, prev - %d, curr - %d", previous, curr));
//        }
    }
}
