package ua.algorithms.lab1.sorting.algorithms;

import ua.algorithms.lab1.Main;
import ua.algorithms.lab1.util.LogUtils;
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
            long sourceSize = source.length() / Integer.BYTES,
                    groupsNumber = sourceSize, digitsInGroup = 1L;
            while (groupsNumber != 1) {
                MutableLong buff1Size = MutableLong.defaultValue(),
                        buff2Size = MutableLong.defaultValue();
//                System.out.printf("Source size - %d, digits in group - %d, groupsNumber - %d\n", sourceSize, digitsInGroup, groupsNumber);
                separate(digitsInGroup, groupsNumber, sourceSize, buff1Size, buff2Size);
//                System.out.print("Buff1 - ");
//                LogUtils.outputData(super.getDest1(), buff1Size.getValue());
//                System.out.print("Buff2 - ");
//                LogUtils.outputData(super.getDest2(), buff1Size.getValue());
                moveFilesPointersToStart();
                merge(digitsInGroup, groupsNumber, buff1Size, buff2Size);
//                System.out.print("Source - ");
//                LogUtils.outputData(source, sourceSize);
                moveFilesPointersToStart();
                digitsInGroup *= 2;
                groupsNumber = (int) Math.ceil(groupsNumber / 2f);
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

    private void merge(Long digitsInGroup, Long groupsNumber, MutableLong buff1Size, MutableLong buff2Size) throws IOException {
        MutableLong pBuff1 = MutableLong.defaultValue(), pBuff2 = MutableLong.defaultValue();
        MutableInt read1 = MutableInt.defaultValue(), read2 = MutableInt.defaultValue();
        for (long i = 0; i < Math.ceil(groupsNumber / 2f); i++) {
            long lastGroupsIndex = digitsInGroup * (i + 1);
            if (pBuff1.equals(buff1Size)) {
                read2.setValue(readNextInt(buff2));
                while (pBuff2.getValue().compareTo(lastGroupsIndex) < 0) {
                    writeCurrentAndReadNext(source, buff2, read2, pBuff2, lastGroupsIndex);
                }
            } else if (pBuff2.equals(buff2Size)) {
                read1.setValue(readNextInt(buff1));
                while (pBuff1.getValue().compareTo(lastGroupsIndex) < 0) {
                    writeCurrentAndReadNext(source, buff1, read1, pBuff1, lastGroupsIndex);
                }
            } else {
                read1.setValue(readNextInt(buff1));
                read2.setValue(readNextInt(buff2));
                while ((pBuff1.getValue().compareTo(lastGroupsIndex) < 0 && pBuff1.compareTo(buff1Size) < 0)
                        && (pBuff2.getValue().compareTo(lastGroupsIndex) < 0 && pBuff2.compareTo(buff2Size) < 0)) {
                    if (read1.compareTo(read2) > 0) {
                        writeCurrentAndReadNext(source, buff2, read2, pBuff2, lastGroupsIndex);
                    } else {
                        writeCurrentAndReadNext(source, buff1, read1, pBuff1, lastGroupsIndex);
                    }
                }
                if (pBuff1.getValue().equals(lastGroupsIndex) || pBuff1.equals(buff1Size)) {
                    while (pBuff2.getValue().compareTo(lastGroupsIndex) < 0) {
                        writeCurrentAndReadNext(source, buff2, read2, pBuff2, lastGroupsIndex);
                    }
                } else {
                    while (pBuff1.getValue().compareTo(lastGroupsIndex) < 0) {
                        writeCurrentAndReadNext(source, buff1, read1, pBuff1, lastGroupsIndex);
                    }
                }
            }
        }
    }

    private void writeCurrentAndReadNext(RandomAccessFile dest,
                                         RandomAccessFile src,
                                         MutableInt curr,
                                         MutableLong ptr,
                                         long lastGroupsIndex) throws IOException {
        writeInt(dest, curr.getValue());
        ptr.increment();
        if (!ptr.getValue().equals(lastGroupsIndex)) {
            curr.setValue(readNextInt(src));
        }
    }

    private Integer readNextInt(RandomAccessFile src) throws IOException {
        return src.readInt();
    }

    private void separate(
            Long digitsInGroup,
            Long groupNumber,
            Long sourceSize,
            MutableLong buff1Size,
            MutableLong buff2Size
    ) throws IOException {
        for (int i = 0; i < groupNumber; i++) {
            for (int j = 0; j < digitsInGroup; j++) {
                if ((i * digitsInGroup) + j < sourceSize) {
                    if (i % 2 == 0) {
                        writeInt(buff1, source.readInt());
                        buff1Size.increment();
                    } else {
                        writeInt(buff2, source.readInt());
                        buff2Size.increment();
                    }
                } else {
                    break;
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
