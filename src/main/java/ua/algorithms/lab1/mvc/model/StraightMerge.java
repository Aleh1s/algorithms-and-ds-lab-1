package ua.algorithms.lab1.mvc.model;

import ua.algorithms.lab1.exception.*;
import ua.algorithms.lab1.property.Property;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static ua.algorithms.lab1.utils.Utils.fromRef;

public class StraightMerge implements Model {

    private final RandomAccessFile outputAccess;
    private final RandomAccessFile buff1Access;
    private final RandomAccessFile buff2Access;
    private final long sourceLength;
    private byte[] buff;
    private int buffPointer;
    private static final File output;
    private static final File buff1;
    private static final File buff2;
    private static final int DEFAULT_BUFF_SIZE;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private static final int BUFF_1 = 0;
    private static final int BUFF_2 = 1;

    private StraightMerge(
            RandomAccessFile outputAccess,
            RandomAccessFile buff1Access,
            RandomAccessFile buff2Access,
            long sourceLength
    ) {
        this.outputAccess = outputAccess;
        this.buff1Access = buff1Access;
        this.buff2Access = buff2Access;
        this.sourceLength = sourceLength;
        this.buff = new byte[0];
    }

    static {
        Properties properties = Property.getInstance().getProperties();
        buff1 = new File(properties.getProperty("default.path.buff1"));
        buff2 = new File(properties.getProperty("default.path.buff2"));
        output = new File(properties.getProperty("default.path.output"));
        DEFAULT_BUFF_SIZE = Integer.parseInt(properties.getProperty("default.buffer.size"));
    }

    public static StraightMerge getInstance(File source) throws CopyFileException, FileNotFoundException {
        copySourceFile(source);
        RandomAccessFile outputAccess = openConnection(output);
        createFile(buff1);
        RandomAccessFile buff1Access = openConnection(buff1);
        createFile(buff2);
        RandomAccessFile buff2Access = openConnection(buff2);
        long sourceLength = source.length();
        return new StraightMerge(outputAccess, buff1Access, buff2Access, sourceLength);
    }

    private static void createFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new FileCreateException(e);
        }
    }

    private static void copySourceFile(File source) throws CopyFileException {
        Path original = source.toPath();
        Path copied = StraightMerge.output.toPath();
        try {
            Files.copy(original, copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new CopyFileException(e);
        }
    }

    private static RandomAccessFile openConnection(File file) throws FileNotFoundException {
        try {
            return new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Override
    public void sort() throws FileNotFoundException, CloseConnectionException, FileAccessException {
        long digitsNum = 1;
        if (sourceLength / Integer.BYTES <= 1) {
            System.out.println("The data is already sorted");
            return;
        }
        try {
            long start = System.nanoTime();
            while (digitsNum < sourceLength / Integer.BYTES) {
                distribution(digitsNum);
                merge(digitsNum);
                digitsNum *= 2;
            }
            long finish = System.nanoTime();
            System.out.printf("Sorting took %d seconds%n", TimeUnit.NANOSECONDS.toSeconds(finish - start));
            close();
        } catch (FileAccessException e) {
            closeWithTryCatch();
            throw new FileAccessException(e);
        } catch (IOException e) {
            closeWithTryCatch();
        }
    }

    private void closeWithTryCatch() throws CloseConnectionException {
        try {
            close();
        } catch (IOException e) {
            throw new CloseConnectionException("Can't close connection", e);
        }
    }

    private void flush() throws WriteToFileException {
        try {
            if (buff.length > 0) {
                outputAccess.write(buff);
                buffPointer = 0;
            }
        } catch (IOException e) {
            throw new WriteToFileException(String.format("%s (Failed while flushing)", output.getPath()), e);
        }
    }

    private void merge(long digitsNum) throws FileAccessException {
        long[] globalPointers = new long[2];
        int[] localPointers = new int[2];
        Integer[][] values = initializeArr(digitsNum);
        for (long i = 0; i < sourceLength / Integer.BYTES; i++) {
            int index;
            long endOfGroupIndex;
            Integer minValue;
            if (buffPointer >= buff.length) {
                flush();
                if ((sourceLength - i * Integer.BYTES) > DEFAULT_BUFF_SIZE) {
                    buff = new byte[DEFAULT_BUFF_SIZE];
                } else {
                    buff = new byte[(int) (sourceLength - i * Integer.BYTES)];
                }
            }
            if (Objects.isNull(values[BUFF_1])) {
                index = BUFF_2;
                minValue = values[BUFF_2][localPointers[BUFF_2]];
            } else if (Objects.isNull(values[BUFF_2])) {
                index = BUFF_1;
                minValue = values[BUFF_1][localPointers[BUFF_1]];
            } else {
                index = findIndexOfMin(values, localPointers);
                minValue = values[index][localPointers[index]];
            }
            addToBuff(minValue);
            endOfGroupIndex = ((globalPointers[index] / digitsNum) + 1) * digitsNum;
            localPointers[index]++;
            globalPointers[index]++;
            if (localPointers[index] >= values[index].length) {
                if (globalPointers[index] < endOfGroupIndex
                        && globalPointers[index] < getFileLength(index == BUFF_1 ? buff1Access : buff2Access) / Integer.BYTES) {
                    values[index] = readPart(index == BUFF_1 ? buff1Access : buff2Access, digitsNum, globalPointers[index]);
                    localPointers[index] = 0;
                } else {
                    values[index] = null;
                }
            }
            if (Objects.isNull(values[BUFF_1]) && Objects.isNull(values[BUFF_2])) {
                values[BUFF_1] = readPart(buff1Access, digitsNum, globalPointers[BUFF_1]);
                values[BUFF_2] = readPart(buff2Access, digitsNum, globalPointers[BUFF_2]);
                localPointers[BUFF_1] = 0;
                localPointers[BUFF_2] = 0;
            }
        }
        flush();
        try {
            outputAccess.seek(0);
        } catch (IOException e) {
            throw new FileAccessException(String.format("%s (Failed to seek)", output.getPath()), e);
        }
        clearBuffers();
    }

    private void clearBuffers() throws FileAccessException {
        try {
            buff1Access.setLength(0);
        } catch (IOException e) {
            throw new FileAccessException(String.format("%s (Failed to set length)", buff1.getPath()), e);
        }
        try {
            buff2Access.setLength(0);
        } catch (IOException e) {
            throw new FileAccessException(String.format("%s (Failed to set length)", buff2.getPath()), e);
        }
    }

    private static int findIndexOfMin(Integer[][] values, int[] pointers) {
        int index = 0;
        while (values[index] == null) index++;
        Integer minValue = values[index][pointers[index]];
        for (int i = index + 1; i < values.length; i++) {
            if (values[i] != null && minValue.compareTo(values[i][pointers[i]]) > 0) {
                minValue = values[i][pointers[i]];
                index = i;
            }
        }
        return index;
    }

    private void addToBuff(int value) {
        buff[buffPointer++] = (byte) (value >> 24);
        buff[buffPointer++] = (byte) (value >> 16);
        buff[buffPointer++] = (byte) (value >> 8);
        buff[buffPointer++] = (byte) value;
    }

    private Integer[][] initializeArr(long digitsNum) throws FileAccessException {
        Integer[][] values = new Integer[2][];
        try {
            values[0] = readPart(buff1Access, digitsNum, 0);
        } catch (FetchFileLengthException | ReadFromFileException e) {
            throw new FileAccessException(String.format("%s %s", buff1.getPath(), e.getMessage()), e);
        }
        try {
            values[1] = readPart(buff2Access, digitsNum, 0);
        } catch (FetchFileLengthException | ReadFromFileException e) {
            throw new FileAccessException(String.format("%s %s", buff2.getPath(), e.getMessage()), e);
        }
        return values;
    }

    private Integer[] readPart(RandomAccessFile raf, long digitsNum, long ptr)
            throws FetchFileLengthException, ReadFromFileException {
        if (ptr < getFileLength(raf) / Integer.BYTES) {
            byte[] buff;
            long currGroupEndIndex = (((ptr / digitsNum) + 1) * digitsNum);
            long toRead = Math.min((getFileLength(raf) / Integer.BYTES - ptr), currGroupEndIndex - ptr) * Integer.BYTES;
            if (toRead >= DEFAULT_BUFF_SIZE) {
                buff = new byte[DEFAULT_BUFF_SIZE];
            } else {
                buff = new byte[(int) toRead];
            }
            try {
                raf.read(buff);
            } catch (IOException e) {
                throw new ReadFromFileException("(Failed to read from file)", e);
            }
            return fromRef(buff);
        } else {
            return null;
        }
    }

    private static long getFileLength(RandomAccessFile raf) throws FetchFileLengthException {
        try {
            return raf.length();
        } catch (IOException e) {
            throw new FetchFileLengthException("(Failed to get file's length)", e);
        }
    }

    private void distribution(long digitsNum) throws FileAccessException {
        for (long i = 0; i < Math.ceil((sourceLength / (double) Integer.BYTES) / (double) digitsNum); i++) {
            long remainder = sourceLength - (i * digitsNum) * Integer.BYTES;
            long bytesToRead = digitsNum * Integer.BYTES;
            long size = Math.min(remainder, bytesToRead);
            if (i % 2 == 0) {
                try {
                    moveGroup(buff1Access, size);
                } catch (WriteToFileException e) {
                    throw new WriteToFileException(String.format("%s %s", buff1.getPath(), e.getMessage()), e);
                }
            } else {
                try {
                    moveGroup(buff2Access, size);
                } catch (WriteToFileException e) {
                    throw new WriteToFileException(String.format("%s %s", buff2.getPath(), e.getMessage()), e);
                }
            }
        }
        moveFilePointersToStart();
    }

    private void moveFilePointersToStart() throws FileAccessException {
        try {
            buff1Access.seek(0);
            buff2Access.seek(0);
            outputAccess.seek(0);
        } catch (IOException e) {
            throw new FileAccessException("(Failed to seek file position)", e);
        }
    }

    private void moveGroup(RandomAccessFile buff, long size) throws ReadFromFileException, WriteToFileException {
        try {
            if (size > MAX_ARRAY_SIZE) {
                long bytesWritten = 0;
                for (int i = 0; i < Math.ceil(size / (double) MAX_ARRAY_SIZE); i++) {
                    long remainder = size - bytesWritten;
                    int length = remainder > MAX_ARRAY_SIZE ? MAX_ARRAY_SIZE : (int) remainder;
                    writeGroup(buff, readGroup(outputAccess, length));
                    bytesWritten += length;
                }
            } else {
                writeGroup(buff, readGroup(outputAccess, (int) size));
            }
        } catch (ReadFromFileException e) {
            throw new ReadFromFileException(String.format("%s %s", output.getPath(), e.getMessage()), e);
        }
    }

    private void writeGroup(RandomAccessFile dest, byte[] group) throws WriteToFileException {
        try {
            dest.write(group);
        } catch (IOException e) {
            throw new WriteToFileException("(Failed while writing group to file)", e);
        }
    }

    private byte[] readGroup(RandomAccessFile src, int length) throws ReadFromFileException {
        byte[] group = new byte[length];
        try {
            src.read(group);
        } catch (IOException e) {
            throw new ReadFromFileException("(Failed while reading group from file)", e);
        }
        return group;
    }

    public void close() throws IOException {
        outputAccess.close();
        buff1Access.close();
        buff2Access.close();
        buff1.delete();
        buff2.delete();
    }
}
