package ua.algorithms.lab1.sorting.algorithms;

import ua.algorithms.lab1.property.Property;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class ImprovedStraightMerge extends ExternalStraightMergeSortingAlgorithm {

    private static RandomAccessFile buff1Access;
    private static RandomAccessFile buff2Access;
    private static RandomAccessFile outputAccess;
    private static RandomAccessFile sourceAccess;
    private static Map<Integer, File> indexFileMap;
    private static Map<Integer, RandomAccessFile> indexFileAccessMap;
    private static final String CHUNKS_PATH;

    static {
        CHUNKS_PATH = Property.getInstance().getProperties().getProperty("default.path.chunks");
    }

    private static final int CHUNK_BYTE_SIZE = 104_857_600;

    private static void initialize(File source) {
        try {
            ExternalStraightMergeSortingAlgorithm.output.createNewFile();
            outputAccess = new RandomAccessFile(ExternalStraightMergeSortingAlgorithm.output, "rw");
            sourceAccess = new RandomAccessFile(source, "r");
            indexFileMap = new HashMap<>();
            indexFileAccessMap = new HashMap<>();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sort(File source) {
        try {
            initialize(source);
            sortChunks();
            merge();
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void merge() throws IOException {
        int length = (int) (sourceAccess.length() / Integer.BYTES);
        int[] pointers = new int[indexFileMap.size()];
        Integer[] values = getFirstValuesFromEveryChunk();
        for (int i = 0; i < length; i++) {
            int minValuesIndex = findIndexOfMin(values);
            Integer minValue = values[minValuesIndex];
            writeInt(outputAccess, minValue);
            pointers[minValuesIndex]++;
            RandomAccessFile raf = indexFileAccessMap.get(minValuesIndex);
            if ((raf.length() / Integer.BYTES) > pointers[minValuesIndex]) {
                values[minValuesIndex] = readInt(raf);
            } else {
                values[minValuesIndex] = null;
            }
        }
    }

    private static int findIndexOfMin(Integer[] values) {
        int index = 0;
        while (values[index] == null) index++;
        Integer minValue = values[index];
        for (int i = index + 1; i < values.length; i++) {
            if (values[i] != null && minValue.compareTo(values[i]) > 0) {
                minValue = values[i];
                index = i;
            }
        }
        return index;
    }

    private static Integer[] getFirstValuesFromEveryChunk() throws IOException {
        int length = indexFileMap.size();
        Integer[] values = new Integer[length];
        for (int i = 0; i < length; i++) {
            RandomAccessFile raf = indexFileAccessMap.get(i);
            values[i] = readInt(raf);
        }
        return values;
    }

    private static int readInt(RandomAccessFile raf) throws IOException {
        byte[] buff = new byte[Integer.BYTES];
        raf.read(buff);
        return from(buff)[0];
    }

    private static void writeInt(RandomAccessFile raf, int value) throws IOException {
        byte[] buff = new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
        raf.write(buff);
    }

//    private static long chunksLength() throws IOException {
//        long sum = 0L;
//        for (RandomAccessFile raf : indexFileAccessMap.values()) sum += raf.length();
//        return sum;
//    }

    public static void sortChunks() throws IOException {
        long length = sourceAccess.length();
        int chunksNumber = (int) Math.ceil(length / (double) CHUNK_BYTE_SIZE);
        for (int i = 0; i < chunksNumber; i++) {
            int bytesToRead;
            if (length - (long) i * CHUNK_BYTE_SIZE > CHUNK_BYTE_SIZE)
                bytesToRead = CHUNK_BYTE_SIZE;
            else
                bytesToRead = (int) (length - i * CHUNK_BYTE_SIZE);
            int[] chunk = readChunk(bytesToRead);
            quickSort(chunk, 0, chunk.length - 1);
            writeChunk(chunk);
        }
    }

    private static void writeChunk(int[] chunk) throws IOException {
        int index = indexFileMap.size();
        System.out.println("Index - " + index);
        String chunkName = String.format("Chunk%d", index);
        System.out.println("Chunk name - " + chunkName);
        File chunkBinFile = new File(CHUNKS_PATH, chunkName);
        boolean isCreated = chunkBinFile.createNewFile();
        System.out.println("Is created - " + isCreated);
        indexFileMap.put(index, chunkBinFile);
        RandomAccessFile chunkFileAccess = new RandomAccessFile(chunkBinFile, "rw");
        indexFileAccessMap.put(index, chunkFileAccess);
        byte[] bytesChuck = from(chunk);
//        System.out.println("Chunk" + index + " " + Arrays.toString(chunk));
        chunkFileAccess.write(bytesChuck);
        chunkFileAccess.seek(0);
        System.out.println("_".repeat(120));
    }

    private static void quickSort(int[] arr, int low, int high) {
        if (arr.length == 0)
            return;

        if (low >= high)
            return;

        int middle = low + (high - low) / 2;
        int opora = arr[middle];

        int i = low, j = high;
        while (i <= j) {
            while (arr[i] < opora) {
                i++;
            }

            while (arr[j] > opora) {
                j--;
            }

            if (i <= j) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }
        }

        if (low < j)
            quickSort(arr, low, j);

        if (high > i)
            quickSort(arr, i, high);
    }

    private static int[] readChunk(int chunkSize) throws IOException {
        byte[] chunk = new byte[chunkSize];
        sourceAccess.read(chunk);
        return from(chunk);
    }

    public static int[] from(byte[] chunk) {
        int[] ints = new int[chunk.length / 4];
        for (int i = 0; i < chunk.length; i += 4) {
            ints[i / 4] = chunk[i] << 24
                    | (chunk[i + 1] & 0xFF) << 16
                    | (chunk[i + 2] & 0xFF) << 8
                    | (chunk[i + 3] & 0xFF);
        }
        return ints;
    }

    public static byte[] from(int[] chuck) {
        byte[] bytes = new byte[chuck.length * 4];
        for (int i = 0; i < chuck.length; i++) {
            bytes[i * 4] = (byte) (chuck[i] >> 24);
            bytes[i * 4 + 1] = (byte) (chuck[i] >> 16);
            bytes[i * 4 + 2] = (byte) (chuck[i] >> 8);
            bytes[i * 4 + 3] = (byte) chuck[i];
        }
        return bytes;
    }

    public static void close() throws IOException {
        sourceAccess.close();
        ExternalStraightMergeSortingAlgorithm.output.delete();
        for (RandomAccessFile raf : indexFileAccessMap.values()) raf.close();
        for (File f : indexFileMap.values()) f.delete();
    }

}
