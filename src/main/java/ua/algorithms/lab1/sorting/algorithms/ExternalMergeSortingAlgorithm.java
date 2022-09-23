package ua.algorithms.lab1.sorting.algorithms;

import ua.algorithms.lab1.exception.CanNotCreateDestinationFileException;

import java.io.*;
import java.nio.file.Files;

public abstract class ExternalMergeSortingAlgorithm implements Closeable {

    private final File dest1;
    private final File dest2;

    public abstract void sort(File source);

    public ExternalMergeSortingAlgorithm(
            String dest1,
            String dest2
    ) {
        this.dest1 = openFileHelper(dest1);
        this.dest2 = openFileHelper(dest2);
    }

    private static File openFileHelper(String path) {
        File open = new File(path);
        if (!open.exists()) {
            try {
                open.createNewFile();
            } catch (IOException e) {
                throw new CanNotCreateDestinationFileException("Can not create destination file");
            }
        }
        return open;
    }

    public File getDest1() {
        return dest1;
    }

    public File getDest2() {
        return dest2;
    }
}
