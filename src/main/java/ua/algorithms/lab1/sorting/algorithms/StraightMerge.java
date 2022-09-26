package ua.algorithms.lab1.sorting.algorithms;

import ua.algorithms.lab1.util.LogUtils;
import ua.algorithms.lab1.util.MutableInt;
import ua.algorithms.lab1.util.MutableLong;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class StraightMerge extends ExternalStraightMergeSortingAlgorithm {

    private static RandomAccessFile buff1Access;
    private static RandomAccessFile buff2Access;
    private static RandomAccessFile sourceAccess;

    private StraightMerge() {}

    public static void sort(File source) {
        try {
            initialize(source);
            long sourceSize = source.length() / Integer.BYTES,
                    groupsNumber = sourceSize, digitsInGroup = 1L;
            while (groupsNumber != 1) {
//                System.out.println("-".repeat(120));
//                System.out.println("Start separating");
                MutableLong buff1Size = MutableLong.defaultValue(),
                        buff2Size = MutableLong.defaultValue();
                separate(digitsInGroup, groupsNumber, sourceSize, buff1Size, buff2Size);
//                System.out.println("Buff1 - ");
//                LogUtils.outputData(ExternalStraightMergeSortingAlgorithm.buff1, buff1Size.getValue());
//                System.out.println("Buff2 - ");
//                LogUtils.outputData(ExternalStraightMergeSortingAlgorithm.buff2, buff2Size.getValue());
                moveFilesPointersToStart();
//                System.out.println("-".repeat(120));
//                System.out.println("Start merging");
                merge(digitsInGroup, groupsNumber, buff1Size, buff2Size);
//                System.out.println("Source - ");
//                LogUtils.outputData(source, sourceSize);
                moveFilesPointersToStart();
                digitsInGroup *= 2;
                groupsNumber = (int) Math.ceil(groupsNumber / 2f);
            }
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void moveFilesPointersToStart() throws IOException {
        sourceAccess.seek(0);
        buff1Access.seek(0);
        buff2Access.seek(0);
    }

    private static void merge(Long digitsInGroup, Long groupsNumber, MutableLong buff1Size, MutableLong buff2Size) throws IOException {
        MutableLong pBuff1 = MutableLong.defaultValue(), pBuff2 = MutableLong.defaultValue();
        MutableInt read1 = MutableInt.defaultValue(), read2 = MutableInt.defaultValue();
        for (long i = 0; i < Math.ceil(groupsNumber / 2f); i++) {
            long lastGroupsIndex = digitsInGroup * (i + 1);
            if (pBuff1.equals(buff1Size)) {
                read2.setValue(buff2Access.readInt());
                writeRemainderToBuff2(read2, pBuff2, buff2Size, lastGroupsIndex);
            } else if (pBuff2.equals(buff2Size)) {
                read1.setValue(buff1Access.readInt());
                writeRemainderToBuff1(read1, pBuff1, buff1Size, lastGroupsIndex);
            } else {
                read1.setValue(buff1Access.readInt());
                read2.setValue(buff2Access.readInt());
                while ((pBuff1.getValue().compareTo(lastGroupsIndex) < 0 && pBuff1.compareTo(buff1Size) < 0)
                        && (pBuff2.getValue().compareTo(lastGroupsIndex) < 0 && pBuff2.compareTo(buff2Size) < 0)) {
                    if (read1.compareTo(read2) > 0) {
                        writeCurrentAndReadNext(sourceAccess, buff2Access, read2, pBuff2, buff2Size, lastGroupsIndex);
                    } else {
                        writeCurrentAndReadNext(sourceAccess, buff1Access, read1, pBuff1, buff1Size, lastGroupsIndex);
                    }
                }
                if (pBuff1.getValue().equals(lastGroupsIndex) || pBuff1.equals(buff1Size)) {
                    writeRemainderToBuff2(read2, pBuff2, buff2Size, lastGroupsIndex);
                } else {
                    writeRemainderToBuff1(read1, pBuff1, buff1Size, lastGroupsIndex);
                }
            }
        }
    }

    private static void writeRemainderToBuff1(MutableInt read, MutableLong pBuff1, MutableLong buff1Size, Long lastGroupsIndex)
            throws IOException {
        while (pBuff1.getValue().compareTo(lastGroupsIndex) < 0) {
            writeCurrentAndReadNext(sourceAccess, buff1Access, read, pBuff1, buff1Size, lastGroupsIndex);
        }
    }

    private static void writeRemainderToBuff2(MutableInt read, MutableLong pBuff2, MutableLong buff2Size, Long lastGroupsIndex)
            throws IOException {
        while (pBuff2.getValue().compareTo(lastGroupsIndex) < 0) {
            writeCurrentAndReadNext(sourceAccess, buff2Access, read, pBuff2, buff2Size, lastGroupsIndex);
        }
    }

    private static void writeCurrentAndReadNext(
            RandomAccessFile dest,
            RandomAccessFile src,
            MutableInt curr,
            MutableLong ptr,
            MutableLong buffSize,
            Long lastGroupsIndex
    )
            throws IOException {
        dest.writeInt(curr.getValue());
        ptr.increment();
        if (!ptr.getValue().equals(lastGroupsIndex) && ptr.compareTo(buffSize) < 0) {
            curr.setValue(src.readInt());
        }
    }

    private static void separate(
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
                        buff1Access.writeInt(sourceAccess.readInt());
                        buff1Size.increment();
                    } else {
                        buff2Access.writeInt(sourceAccess.readInt());
                        buff2Size.increment();
                    }
                } else {
                    break;
                }
            }
        }
    }

    private static void initialize(File source) {
        try {
            ExternalStraightMergeSortingAlgorithm.buff1.createNewFile();
            ExternalStraightMergeSortingAlgorithm.buff2.createNewFile();
            buff1Access = new RandomAccessFile(ExternalStraightMergeSortingAlgorithm.buff1, "rw");
            buff2Access = new RandomAccessFile(ExternalStraightMergeSortingAlgorithm.buff2, "rw");
            sourceAccess = new RandomAccessFile(source, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close() throws IOException {
        sourceAccess.close();
        buff1Access.close();
        buff2Access.close();
        ExternalStraightMergeSortingAlgorithm.buff1.delete();
        ExternalStraightMergeSortingAlgorithm.buff2.delete();
    }
}
