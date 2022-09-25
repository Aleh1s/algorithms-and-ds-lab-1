//package ua.algorithms.lab1.sorting.algorithms;
//
//import ua.algorithms.lab1.util.MutableLong;
//
//import java.io.*;
//
//import static ua.algorithms.lab1.util.MutableLong.*;
//
//public class StraightMerge extends ExternalMergeSortingAlgorithm {
//
//    private RandomAccessFile buff1;
//    private RandomAccessFile buff2;
//    private RandomAccessFile source;
//
//    public StraightMerge(String dest1, String dest2) {
//        super(dest1, dest2);
//    }
//
//    @Override
//    public void sort(File source) {
//        try {
//            initialize(source);
//            int groupNumber = 0, digitsInGroup = 1;
//            while (groupNumber != 2) {
//                groupNumber = separate(digitsInGroup);
//                buff1.seek(0);
//                buff2.seek(0);
//                this.source.setLength(0);
//                merge(digitsInGroup, groupNumber);
//                this.source.seek(0);
//                buff1.setLength(0);
//                buff2.setLength(0);
//                digitsInGroup *= 2;
//            }
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void merge(int digitsInGroup, int groupNumber) throws IOException {
//        int counter = 0;
//        while ((groupNumber / 2) > counter) {
//            MutableLong pBuff1 = defaultValue(), pBuff2 = defaultValue(),
//                       read1 = of(readNextInt(buff1)), read2 = of(readNextInt(buff2));
//            while (pBuff1.getValue().compareTo(digitsInGroup) < 0 && pBuff2.getValue().compareTo(digitsInGroup) < 0) {
//                if (read1.compareTo(read2) > 0)
//                    writeCurrentAndReadNext(source, buff2, read2, pBuff2, digitsInGroup);
//                else
//                    writeCurrentAndReadNext(source, buff1, read1, pBuff1, digitsInGroup);
//            }
//            if (pBuff1.getValue().equals(digitsInGroup))
//                while (pBuff2.getValue().compareTo(digitsInGroup) < 0)
//                    writeCurrentAndReadNext(source, buff2, read2, pBuff2, digitsInGroup);
//            else
//                while (pBuff1.getValue().compareTo(digitsInGroup) < 0)
//                    writeCurrentAndReadNext(source, buff1, read1, pBuff1, digitsInGroup);
//            counter++;
//        }
//    }
//
//    private void writeCurrentAndReadNext(RandomAccessFile dest,
//                                         RandomAccessFile src,
//                                         MutableLong curr,
//                                         MutableLong ptr,
//                                         Integer digitsInGroup) throws IOException {
//        writeInt(dest, curr.getValue());
//        ptr.increment();
//        if (!ptr.getValue().equals(digitsInGroup))
//            curr.setValue(readNextInt(src));
//    }
//
//    private int readNextInt(RandomAccessFile source) throws IOException {
//        MutableLong buff = MutableLong.defaultValue();
//        readDigitToBuff(source, buff);
//        return buff.getValue();
//    }
//
//    private void initialize(File source) {
//        try {
//            this.buff1 = new RandomAccessFile(super.getDest1(), "rw");
//            this.buff1.setLength(0);
//            this.buff2 = new RandomAccessFile(super.getDest2(), "rw");
//            this.buff2.setLength(0);
//            this.source = new RandomAccessFile(source, "rw");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private int separate(int digitsNumberInGroup) throws IOException {
//        boolean eof = false;
//        int groupCounter = 0;
//        while (!eof) {
//            eof = separateGroup(digitsNumberInGroup, groupCounter);
//            groupCounter++;
//        }
//        return groupCounter;
//    }
//
//    private boolean separateGroup(int digitsNumberInGroup, int groupCounter) throws IOException {
//        boolean eof = false;
//        for (int k = 0; k < digitsNumberInGroup && !eof; k++) {
//            MutableLong buff = MutableLong.defaultValue();
//            eof = readDigitToBuff(source, buff);
//            if (groupCounter % 2 == 0) {
//                writeInt(buff1, buff.getValue());
//            } else {
//                writeInt(buff2, buff.getValue());
//            }
//        }
//        return eof;
//    }
//
//    private static boolean readDigitToBuff(RandomAccessFile source, MutableLong buff) throws IOException {
//        boolean eof = false;
//        try {
//            buff.setValue(source.readInt());
//        } catch (EOFException e) {
//            eof = true;
//        }
//        return eof;
//    }
//
//    private void writeInt(RandomAccessFile dest, Integer i) throws IOException {
//        dest.writeInt(i);
//    }
//
//    @Override
//    public void close() throws IOException {
//        buff1.close();
//        buff2.close();
//        source.close();
//    }
//}
