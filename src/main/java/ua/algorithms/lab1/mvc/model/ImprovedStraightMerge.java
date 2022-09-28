package ua.algorithms.lab1.mvc.model;

import ua.algorithms.lab1.exception.*;
import ua.algorithms.lab1.property.Property;

import java.io.*;
import java.util.*;

import static ua.algorithms.lab1.Utils.*;

public class ImprovedStraightMerge implements Model {
    private final RandomAccessFile outputAccess;
    private final RandomAccessFile sourceAccess;
    private final File source;
    private final File output;
    private final long sourceLength;
    private byte[] buff;
    private int buffPointer;
    private final int chunksPartSize;
    private String sourcePath;
    private final Map<Integer, File> indexChunkMap;
    private final Map<Integer, RandomAccessFile> indexChunkAccessMap;
    private static final String CHUNKS_PATH;
    private static final String OUTPUT_PATH;
    private static final int CHUNK_BYTE_SIZE = 104_857_600;

    static {
        Properties properties = Property.getInstance().getProperties();
        CHUNKS_PATH = properties.getProperty("default.path.chunks");
        OUTPUT_PATH = properties.getProperty("default.path.output");
    }

    private ImprovedStraightMerge(
            RandomAccessFile sourceAccess,
            RandomAccessFile outputAccess,
            int chunksPartSize,
            long sourceLength,
            File source,
            File output
    ) {
        this.source = source;
        this.output = output;
        this.sourceLength = sourceLength;
        this.sourceAccess = sourceAccess;
        this.outputAccess = outputAccess;
        this.chunksPartSize = chunksPartSize;
        this.buff = new byte[chunksPartSize];
        this.indexChunkMap = new HashMap<>();
        this.indexChunkAccessMap = new HashMap<>();
    }

    public static ImprovedStraightMerge getInstance(File source)
            throws FileNotFoundException, FetchFileLengthException {
        long sourceLength;
        RandomAccessFile sourceAccess, outputAccess;
        File output = new File(OUTPUT_PATH);
        try {
            sourceAccess = new RandomAccessFile(source, "r");
            sourceLength = getFileLength(sourceAccess);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(String.format("%s", e.getMessage()));
        } catch (FetchFileLengthException e) {
            throw new FetchFileLengthException(String.format("%s %s", source.getPath(), e.getMessage()), e);
        }
        try {
            outputAccess = new RandomAccessFile(output, "rw");
        } catch (FileNotFoundException e) {
            System.out.println("Output file doesn't exist, it'll be created");
            try {
                output.createNewFile();
            } catch (IOException ex) {
                throw new FileCreateException(String.format("%s (Can't create file)", output.getPath()), ex);
            }
            outputAccess = new RandomAccessFile(output, "rw");
        }
        int chunkPartSize = ((int) (CHUNK_BYTE_SIZE / (Math.ceil(source.length() / (double) CHUNK_BYTE_SIZE) + 1)) / Integer.BYTES) * Integer.BYTES;
        return new ImprovedStraightMerge(sourceAccess, outputAccess, chunkPartSize, sourceLength, source, output);
    }

    @Override
    public void sort()
            throws FileNotFoundException,
            CloseConnectionException,
            FileAccessException {
        try {
            sortChunks();
            merge();
            flush();
            close();
        } catch (FileAccessException e) {
            closeWithTryCatch();
            throw new FileAccessException(e);
        } catch (FileNotFoundException e) {
            closeWithTryCatch();
            throw new FileNotFoundException(e.getMessage());
        } catch (IOException e) {
            closeWithTryCatch();
        }
    }

    public void closeWithTryCatch() throws CloseConnectionException {
        try {
            close();
        } catch (IOException e) {
            throw new CloseConnectionException("Can't close connection", e);
        }
    }

    private void merge() throws FileAccessException {
        long length = sourceLength / Integer.BYTES;
        int[] localPointers = new int[indexChunkMap.size()];
        long[] globalPointers = new long[indexChunkMap.size()];
        Integer[][] values = initializeArr();
        for (long i = 0; i < length; i++) {
            int minValuesIndex = findIndexOfMin(values, localPointers);
            Integer minValue = values[minValuesIndex][localPointers[minValuesIndex]];
            if (buffPointer >= buff.length) {
                flush();
                if ((length * Integer.BYTES - i * Integer.BYTES) > chunksPartSize) {
                    buff = new byte[chunksPartSize];
                } else {
                    buff = new byte[(int) (length * Integer.BYTES - i * Integer.BYTES)];
                }
            }
            addToBuff(minValue);
            localPointers[minValuesIndex]++;
            globalPointers[minValuesIndex]++;
            if (localPointers[minValuesIndex] >= values[minValuesIndex].length) {
                RandomAccessFile raf = indexChunkAccessMap.get(minValuesIndex);
                if (globalPointers[minValuesIndex] < (getFileLength(raf) / Integer.BYTES)) {
                    values[minValuesIndex] = readPartOfChunk(raf, globalPointers[minValuesIndex]);
                    localPointers[minValuesIndex] = 0;
                } else {
                    values[minValuesIndex] = null;
                }
            }
        }
    }

    private static long getFileLength(RandomAccessFile raf) throws FetchFileLengthException {
        try {
            return raf.length();
        } catch (IOException e) {
            throw new FetchFileLengthException("(Failed to get file's length)", e);
        }
    }

    private void flush() throws WriteToFileException {
        try {
            writeToFile(outputAccess, buff);
        } catch (WriteToFileException e) {
            throw new WriteToFileException(String.format("%s %s", output.getPath(), e.getMessage()), e);
        }
        buffPointer = 0;
    }

    private static void writeToFile(RandomAccessFile raf, byte[] toWrite) throws WriteToFileException {
        try {
            raf.write(toWrite);
        } catch (IOException e) {
            throw new WriteToFileException("(Can't write to file)", e);
        }
    }

    private void addToBuff(int value) {
        buff[buffPointer++] = (byte) (value >> 24);
        buff[buffPointer++] = (byte) (value >> 16);
        buff[buffPointer++] = (byte) (value >> 8);
        buff[buffPointer++] = (byte) value;
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

    private Integer[][] initializeArr() throws FileAccessException {
        int length = indexChunkMap.size();
        Integer[][] values = new Integer[length][];
        for (int i = 0; i < length; i++) {
            RandomAccessFile raf = indexChunkAccessMap.get(i);
            try {
                values[i] = readPartOfChunk(raf, 0);
            } catch (FileAccessException e) {
                File badFile = indexChunkMap.get(i);
                throw new FileAccessException(String.format("%s %s", badFile.getPath(), e.getMessage()), e);
            }
        }
        return values;
    }

    private Integer[] readPartOfChunk(RandomAccessFile raf, long prt)
            throws FetchFileLengthException, ReadFromFileException {
        byte[] buff;
        if (((getFileLength(raf) / Integer.BYTES) - prt) > chunksPartSize / Integer.BYTES) {
            buff = new byte[chunksPartSize];
        } else {
            buff = new byte[(int) (((getFileLength(raf) / Integer.BYTES) - prt) * Integer.BYTES)];
        }
        try {
            raf.read(buff);
        } catch (IOException e) {
            throw new ReadFromFileException("(Failed to read bytes from file)", e);
        }
        return fromRef(buff);
    }

    public void sortChunks() throws FileAccessException, FileNotFoundException {
        int chunksNumber = (int) Math.ceil(sourceLength / (double) CHUNK_BYTE_SIZE);
        for (int i = 0; i < chunksNumber; i++) {
            int bytesToRead;
            if (sourceLength - (long) i * CHUNK_BYTE_SIZE > CHUNK_BYTE_SIZE)
                bytesToRead = CHUNK_BYTE_SIZE;
            else
                bytesToRead = (int) (sourceLength - i * CHUNK_BYTE_SIZE);
            int[] chunk = readChunk(bytesToRead);
            quickSort(chunk, 0, chunk.length - 1);
            writeChunk(chunk);
        }
    }

    private void writeChunk(int[] chunk)
            throws FileNotFoundException, FileAccessException {
        int index = indexChunkMap.size();
        String chunkName = String.format("Chunk%d", index);
        File chunkBinFile = new File(CHUNKS_PATH, chunkName);
        createFile(chunkBinFile);
        indexChunkMap.put(index, chunkBinFile);
        RandomAccessFile chunkFileAccess = openChunkConnection(chunkBinFile);
        indexChunkAccessMap.put(index, chunkFileAccess);
        byte[] bytesChuck = from(chunk);
        try {
            writeToFile(chunkFileAccess, bytesChuck);
        } catch (WriteToFileException e) {
            throw new WriteToFileException(String.format("%s %s", chunkBinFile.getPath(), e.getMessage()), e);
        }
        try {
            chunkFileAccess.seek(0);
        } catch (IOException e) {
            throw new FileAccessException(String.format("%s (Can't seek position 0)", chunkBinFile.getPath()), e);
        }
    }

    private void createFile(File file) throws FileCreateException {
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new FileCreateException(String.format("%s (%s)", file.getPath(), e.getMessage()), e);
        }
    }

    private RandomAccessFile openChunkConnection(File file) throws FileNotFoundException {
        try {
            return new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(String.format("%s (%s)", file.getPath(), e.getMessage()));
        }
    }

    private int[] readChunk(int chunkSize) throws ReadFromFileException {
        return from(readChunkBytes(chunkSize));
    }

    private byte[] readChunkBytes(int chunkSize) throws ReadFromFileException {
        byte[] chunk = new byte[chunkSize];
        try {
            sourceAccess.read(chunk);
        } catch (IOException e) {
            throw new ReadFromFileException(String.format("%s (Exception while reading chunk from file)", source.getPath()), e);
        }
        return chunk;
    }

    public void close() throws IOException {
        sourceAccess.close();
        outputAccess.close();
        for (RandomAccessFile raf : indexChunkAccessMap.values()) raf.close();
        for (File f : indexChunkMap.values()) f.delete();
    }
}
