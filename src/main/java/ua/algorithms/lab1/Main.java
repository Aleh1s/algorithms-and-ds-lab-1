package ua.algorithms.lab1;

import ua.algorithms.lab1.mvc.controller.ControllerImpl;
import ua.algorithms.lab1.mvc.view.ConsoleView;
import ua.algorithms.lab1.mvc.view.View;
import ua.algorithms.lab1.property.Property;
import ua.algorithms.lab1.utils.Utils;

import java.io.*;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    private static final String SOURCE_PATH;
    private static final String OUTPUT_PATH;
    private static final int CHUNK_SIZE;

    static {
        Properties properties = Property.getInstance().getProperties();
        SOURCE_PATH = properties.getProperty("default.path.source");
        OUTPUT_PATH = properties.getProperty("default.path.output");
        CHUNK_SIZE = Integer.parseInt(properties.getProperty("default.chunk.size"));
    }

    public static void main(String[] args) {
        boolean retry = true;
        int length = 0;
        while (retry) {
            System.out.print("Write length of file to generate data (measured in ints) or negative value to exit: ");
            try {
                length = new Scanner(System.in).nextInt();
                retry = false;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input value");
            }
        }
        if (length > 0) {
            generateSrcFile(length);
            boolean exit = false;
            View view = new ConsoleView(new ControllerImpl());
            while (!exit) {
                boolean isChoiceCorrect = false;
                while (!isChoiceCorrect) {
                    System.out.println("Choose sorting algorithm: \n1. Straight merge\n2. Improved straight merge");
                    String choice = new Scanner(System.in).nextLine();
                    int ch;
                    try {
                        ch = Integer.parseInt(choice);
                        if (ch >= 1 && ch <= 2) {
                            isChoiceCorrect = true;
                            view.sortSourceFile(SOURCE_PATH, choice);
                            test(OUTPUT_PATH);
                        } else {
                            System.err.println("Bad choice! Try again.");
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Input value must be digit! Try again.");
                    } catch (IOException e) {
                        System.err.println("Exception while testing");
                        System.exit(0);
                    }
                }
                System.out.print("Write 1 to continue or -1 to exit: ");
                if (new Scanner(System.in).nextLine().equals("-1")) {
                    exit = true;
                }
            }
        }
    }

    private static void generateSrcFile(int length) {
        long bytesToWrite = (long) length * Integer.BYTES;
        try (RandomAccessFile raf = new RandomAccessFile(SOURCE_PATH, "rw")) {
            raf.setLength(0);
            System.out.println("Generating...");
            if (length > CHUNK_SIZE / Integer.BYTES) {
                while (bytesToWrite > 0) {
                    int[] random = new int[CHUNK_SIZE / Integer.BYTES];
                    Utils.fillRandom(random);
                    writeInts(random, raf);
                    bytesToWrite -= CHUNK_SIZE;
                }
            } else {
                int[] random = new int[(int) length];
                Utils.fillRandom(random);
                writeInts(random, raf);
            }
            System.out.println("Completed");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Exception while generating data");
        }
    }

    private static void writeInts(int[] arr, RandomAccessFile raf) throws IOException {
        byte[] from = Utils.from(arr);
        raf.write(from);
    }

    private static void test(String path) throws IOException {
        System.out.println("Testing...");
        boolean passed = true;
        try (RandomAccessFile raf = new RandomAccessFile(path, "rw")) {
            long length = raf.length();
            if (length / Integer.BYTES <= 1) {
                System.out.println("Passed");
                return;
            }
            if (length > CHUNK_SIZE) {
                long toRead = length;
                while (toRead > 0) {
                    byte[] bytes = new byte[CHUNK_SIZE];
                    passed = Utils.testHelper(raf, bytes);
                    if (!passed) {
                        break;
                    }
                    toRead -= CHUNK_SIZE;
                }
            } else {
                byte[] bytes = new byte[(int) length];
                passed = Utils.testHelper(raf, bytes);
            }
            if (passed) {
                System.out.println("Passed");
            } else {
                System.err.println("Failed");
            }
        }
    }
}
