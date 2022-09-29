package ua.algorithms.lab1.mvc.model;

import ua.algorithms.lab1.exception.*;
import ua.algorithms.lab1.property.Property;
import ua.algorithms.lab1.utils.MutableInt;
import ua.algorithms.lab1.utils.MutableLong;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class StraightMerge implements Model {
    private final RandomAccessFile buff1Access;
    private final RandomAccessFile buff2Access;
    private final RandomAccessFile outputAccess;
    private static final String BUFF_1_PATH;
    private static final String BUFF_2_PATH;
    private static final String OUTPUT_PATH;

    static {
        Properties properties = Property.getInstance().getProperties();
        BUFF_1_PATH = properties.getProperty("default.path.buff1");
        BUFF_2_PATH = properties.getProperty("default.path.buff2");
        OUTPUT_PATH = properties.getProperty("default.path.output");
    }

    private StraightMerge(
            RandomAccessFile buff1Access,
            RandomAccessFile buff2Access,
            RandomAccessFile outputAccess
    ) {
        this.buff1Access = buff1Access;
        this.buff2Access = buff2Access;
        this.outputAccess = outputAccess;
    }

    public static StraightMerge getInstance(File source) throws FileNotFoundException, CopyFileException {
        copySourceFile(source, new File(OUTPUT_PATH));
        RandomAccessFile outputAccess = openConnection(OUTPUT_PATH);
        createFile(BUFF_1_PATH);
        RandomAccessFile buff1Access = openConnection(BUFF_1_PATH);
        createFile(BUFF_2_PATH);
        RandomAccessFile buff2Access = openConnection(BUFF_2_PATH);
        return new StraightMerge(buff1Access, buff2Access, outputAccess);
    }

    private static void copySourceFile(File source, File output) throws CopyFileException {
        Path original = source.toPath();
        Path copied = output.toPath();
        try {
            Files.copy(original, copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new CopyFileException(e);
        }
    }

    private static void createFile(String path) {
        try {
            new File(path).createNewFile();
        } catch (IOException e) {
            throw new FileCreateException(e);
        }
    }

    private static RandomAccessFile openConnection(String path) throws FileNotFoundException {
        try {
            return new RandomAccessFile(path, "rw");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    public void sort() throws FileNotFoundException, CloseConnectionException, FileAccessException {
        long length = getFileLength(outputAccess) / Integer.BYTES,
                groupsNumber = length, digitsInGroup = 1L;
        if (length < 2)
            return;
        long start = System.nanoTime();
        while (groupsNumber != 1) {
            MutableLong buff1Size = MutableLong.defaultValue(),
                    buff2Size = MutableLong.defaultValue();
            separate(digitsInGroup, groupsNumber, length, buff1Size, buff2Size);
            moveFilesPointersToStart();
            merge(digitsInGroup, groupsNumber, buff1Size, buff2Size);
            moveFilesPointersToStart();
            digitsInGroup *= 2;
            groupsNumber = (int) Math.ceil(groupsNumber / 2f);
        }
        long finish = System.nanoTime();
        System.out.printf("Sorting took %d seconds\n", TimeUnit.NANOSECONDS.toSeconds(finish - start));
        try {
            close();
        } catch (IOException e) {
            throw new CloseConnectionException("Can't close connection");
        }
    }

    private long getFileLength(RandomAccessFile raf) throws FetchFileLengthException {
        try {
            return raf.length();
        } catch (IOException e) {
            throw new FetchFileLengthException("Failed to fetch file length", e);
        }
    }

    private void moveFilesPointersToStart() throws FileAccessException {
        moveFilePointerToStart(outputAccess);
        moveFilePointerToStart(buff1Access);
        moveFilePointerToStart(buff2Access);
    }

    public void moveFilePointerToStart(RandomAccessFile raf) throws FileAccessException {
        try {
            raf.seek(0);
        } catch (IOException e) {
            throw new FileAccessException("Failed to seek start");
        }
    }

    private void merge(Long digitsInGroup, Long groupsNumber, MutableLong buff1Size, MutableLong buff2Size)
            throws FileAccessException {
        MutableLong pBuff1 = MutableLong.defaultValue(), pBuff2 = MutableLong.defaultValue();
        MutableInt read1 = MutableInt.defaultValue(), read2 = MutableInt.defaultValue();
        for (long i = 0; i < Math.ceil(groupsNumber / 2f); i++) {
            long lastGroupsIndex = digitsInGroup * (i + 1);
            if (pBuff1.equals(buff1Size)) {
                try {
                    read2.setValue(readInt(buff2Access));
                } catch (ReadFromFileException e) {
                    throw new ReadFromFileException(String.format("%s %s", BUFF_2_PATH, e.getMessage()), e);
                }
                writeRemainderFromBuff2(read2, pBuff2, buff2Size, lastGroupsIndex);
            } else if (pBuff2.equals(buff2Size)) {
                try {
                    read1.setValue(readInt(buff1Access));
                } catch (ReadFromFileException e) {
                    throw new ReadFromFileException(String.format("%s %s", BUFF_1_PATH, e.getMessage()), e);
                }
                writeRemainderFromBuff1(read1, pBuff1, buff1Size, lastGroupsIndex);
            } else {
                try {
                    read1.setValue(readInt(buff1Access));
                } catch (ReadFromFileException e) {
                    throw new ReadFromFileException(String.format("%s %s", BUFF_1_PATH, e.getMessage()), e);
                }
                try {
                    read2.setValue(readInt(buff2Access));
                } catch (ReadFromFileException e) {
                    throw new ReadFromFileException(String.format("%s %s", BUFF_2_PATH, e.getMessage()), e);
                }
                while ((pBuff1.getValue().compareTo(lastGroupsIndex) < 0 && pBuff1.compareTo(buff1Size) < 0)
                        && (pBuff2.getValue().compareTo(lastGroupsIndex) < 0 && pBuff2.compareTo(buff2Size) < 0)) {
                    if (read1.compareTo(read2) > 0) {
                        writeCurrentAndReadNext(outputAccess, buff2Access, read2, pBuff2, buff2Size, lastGroupsIndex);
                    } else {
                        writeCurrentAndReadNext(outputAccess, buff1Access, read1, pBuff1, buff1Size, lastGroupsIndex);
                    }
                }
                if (pBuff1.getValue().equals(lastGroupsIndex) || pBuff1.equals(buff1Size)) {
                    writeRemainderFromBuff2(read2, pBuff2, buff2Size, lastGroupsIndex);
                } else {
                    writeRemainderFromBuff1(read1, pBuff1, buff1Size, lastGroupsIndex);
                }
            }
        }
    }

    private void writeRemainderFromBuff1(MutableInt read, MutableLong pBuff1, MutableLong buff1Size, Long lastGroupsIndex)
            throws FileAccessException {
        try {
            while (pBuff1.getValue().compareTo(lastGroupsIndex) < 0) {
                writeCurrentAndReadNext(outputAccess, buff1Access, read, pBuff1, buff1Size, lastGroupsIndex);
            }
        } catch (WriteToFileException e) {
            throw new WriteToFileException(String.format("%s %s", OUTPUT_PATH, e.getMessage()), e);
        } catch (ReadFromFileException e) {
            throw new ReadFromFileException(String.format("%s %s", BUFF_1_PATH, e.getMessage()), e);
        }
    }

    private void writeRemainderFromBuff2(MutableInt read, MutableLong pBuff2, MutableLong buff2Size, Long lastGroupsIndex)
            throws FileAccessException {
        try {
            while (pBuff2.getValue().compareTo(lastGroupsIndex) < 0) {
                writeCurrentAndReadNext(outputAccess, buff2Access, read, pBuff2, buff2Size, lastGroupsIndex);
            }
        } catch (WriteToFileException e) {
            throw new WriteToFileException(String.format("%s %s", OUTPUT_PATH, e.getMessage()), e);
        } catch (ReadFromFileException e) {
            throw new ReadFromFileException(String.format("%s %s", BUFF_2_PATH, e.getMessage()), e);
        }
    }

    private void writeCurrentAndReadNext(
            RandomAccessFile dest,
            RandomAccessFile src,
            MutableInt curr,
            MutableLong ptr,
            MutableLong buffSize,
            Long lastGroupsIndex
    ) throws WriteToFileException, ReadFromFileException {
        writeInt(dest, curr.getValue());
        ptr.increment();
        if (!ptr.getValue().equals(lastGroupsIndex) && ptr.compareTo(buffSize) < 0) {
            curr.setValue(readInt(src));
        }
    }

    private void separate(
            Long digitsInGroup,
            Long groupNumber,
            Long sourceSize,
            MutableLong buff1Size,
            MutableLong buff2Size
    ) throws FileAccessException {
        for (int i = 0; i < groupNumber; i++) {
            for (int j = 0; j < digitsInGroup; j++) {
                if ((i * digitsInGroup) + j < sourceSize) {
                    try {
                        if (i % 2 == 0) {
                            try {
                                writeInt(buff1Access, readInt(outputAccess));
                                buff1Size.increment();
                            } catch (WriteToFileException e) {
                                throw new WriteToFileException(String.format("%s %s", BUFF_1_PATH, e.getMessage()), e);
                            }
                        } else {
                            try {
                                writeInt(buff2Access, readInt(outputAccess));
                                buff2Size.increment();
                            } catch (WriteToFileException e) {
                                throw new WriteToFileException(String.format("%s %S", BUFF_1_PATH, e.getMessage()), e);
                            }
                        }
                    } catch (ReadFromFileException e) {
                        throw new ReadFromFileException(String.format("%s %s", OUTPUT_PATH, e.getMessage()), e);
                    }
                } else {
                    break;
                }
            }
        }
    }

    private static void writeInt(RandomAccessFile raf, int value) throws WriteToFileException {
        try {
            raf.writeInt(value);
        } catch (IOException e) {
            throw new WriteToFileException("(Failed while writing to file)", e);
        }
    }

    private static int readInt(RandomAccessFile raf) throws ReadFromFileException {
        try {
            return raf.readInt();
        } catch (IOException e) {
            throw new ReadFromFileException("(Failed while reading from file)", e);
        }
    }

    public void close() throws IOException {
        outputAccess.close();
        buff1Access.close();
        buff2Access.close();
        new File(BUFF_1_PATH).delete();
        new File(BUFF_2_PATH).delete();
    }
}
