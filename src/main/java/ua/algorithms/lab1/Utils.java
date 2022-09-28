package ua.algorithms.lab1;

public class Utils {

    public static byte[] from(int[] chunk) {
        byte[] bytes = new byte[chunk.length * 4];
        for (int i = 0; i < chunk.length; i++) {
            bytes[i * 4] = (byte) (chunk[i] >> 24);
            bytes[i * 4 + 1] = (byte) (chunk[i] >> 16);
            bytes[i * 4 + 2] = (byte) (chunk[i] >> 8);
            bytes[i * 4 + 3] = (byte) chunk[i];
        }
        return bytes;
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

    public static Integer[] fromRef(byte[] chunk) {
        Integer[] ints = new Integer[chunk.length / 4];
        for (int i = 0; i < chunk.length; i += 4) {
            ints[i / 4] = chunk[i] << 24
                    | (chunk[i + 1] & 0xFF) << 16
                    | (chunk[i + 2] & 0xFF) << 8
                    | (chunk[i + 3] & 0xFF);
        }
        return ints;
    }

    public static void quickSort(int[] arr, int low, int high) {
        if (arr.length == 0)
            return;
        if (low >= high)
            return;
        int middle = low + (high - low) / 2;
        int pivot = arr[middle];
        int i = low, j = high;
        while (i <= j) {
            while (arr[i] < pivot) {
                i++;
            }
            while (arr[j] > pivot) {
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

}
