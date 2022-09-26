package ua.algorithms.lab1.sorting.algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ImprovedStraightMerge extends ExternalStraightMergeSortingAlgorithm {

    private RandomAccessFile source;
    private RandomAccessFile buff1;
    private RandomAccessFile buff2;

//    private void initialize(File source) {
//        try {
//            this.buff1 = new RandomAccessFile(super.getBuff1(), "rw");
//            this.buff2 = new RandomAccessFile(super.getBuff2(), "rw");
//            this.source = new RandomAccessFile(source, "rw");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static void sort(File source) {

    }

}
