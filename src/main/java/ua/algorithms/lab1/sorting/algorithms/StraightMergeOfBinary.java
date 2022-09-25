package ua.algorithms.lab1.sorting.algorithms;

import ua.algorithms.lab1.util.MutableInt;
import ua.algorithms.lab1.util.MutableLong;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class StraightMergeOfBinary extends ExternalMergeSortingAlgorithm {

    private RandomAccessFile buff1;
    private RandomAccessFile buff2;
    private RandomAccessFile source;

    public StraightMergeOfBinary(String dest1, String dest2) {
        super(dest1, dest2);
    }

    @Override
    public void sort(File source) {
        try {
            initialize(source);
            long groupNumber = source.length() / Integer.BYTES, digitsInGroup = 1L;
            while (groupNumber != 1) {
                separate(digitsInGroup, groupNumber);
                moveFilesPointersToStart();
                merge(digitsInGroup, groupNumber);
                moveFilesPointersToStart();
                digitsInGroup *= 2;
                groupNumber /= 2;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveFilesPointersToStart() throws IOException {
        source.seek(0);
        buff1.seek(0);
        buff2.seek(0);
    }

    private void merge(Long digitsInGroup, Long groupNumber) throws IOException {
        for (long i = 0; i < (groupNumber / 2); i++) {
            MutableLong pBuff1 = MutableLong.defaultValue(), pBuff2 = MutableLong.defaultValue();
            MutableInt read1 = MutableInt.of(readNextInt(buff1)), read2 = MutableInt.of(readNextInt(buff2));
            while (pBuff1.getValue().compareTo(digitsInGroup) < 0 && pBuff2.getValue().compareTo(digitsInGroup) < 0) {
                if (read1.compareTo(read2) > 0) {
                    writeCurrentAndReadNext(source, buff2, read2, pBuff2, digitsInGroup);
                } else {
                    writeCurrentAndReadNext(source, buff1, read1, pBuff1, digitsInGroup);
                }
            }
            if (pBuff1.getValue().equals(digitsInGroup)) {
                while (pBuff2.getValue().compareTo(digitsInGroup) < 0) {
                    writeCurrentAndReadNext(source, buff2, read2, pBuff2, digitsInGroup);
                }
            } else {
                while (pBuff1.getValue().compareTo(digitsInGroup) < 0) {
                    writeCurrentAndReadNext(source, buff1, read1, pBuff1, digitsInGroup);
                }
            }
        }
    }

    private void writeCurrentAndReadNext(RandomAccessFile dest,
                                         RandomAccessFile src,
                                         MutableInt curr,
                                         MutableLong ptr,
                                         Long digitsInGroup) throws IOException {
        writeInt(dest, curr.getValue());
        ptr.increment();
        if (!ptr.getValue().equals(digitsInGroup)) {
            curr.setValue(readNextInt(src));
        }
    }

    private Integer readNextInt(RandomAccessFile src) throws IOException {
        return src.readInt();
    }

    private void separate(Long digitsInGroup, Long groupNumber) throws IOException {
        for (int i = 0; i < groupNumber; i++) {
            for (int j = 0; j < digitsInGroup; j++) {
                if (i % 2 == 0) {
                    writeInt(buff1, source.readInt());
                } else {
                    writeInt(buff2, source.readInt());
                }
            }
        }
    }

    private void writeInt(RandomAccessFile dest, Integer toWrite) throws IOException {
        dest.writeInt(toWrite);
    }

    private void initialize(File source) {
        try {
            this.buff1 = new RandomAccessFile(super.getDest1(), "rw");
            this.buff2 = new RandomAccessFile(super.getDest2(), "rw");
            this.source = new RandomAccessFile(source, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        source.close();
        buff1.close();
        buff2.close();
    }
}
