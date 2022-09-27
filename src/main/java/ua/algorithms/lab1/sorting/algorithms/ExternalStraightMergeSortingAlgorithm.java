package ua.algorithms.lab1.sorting.algorithms;

import ua.algorithms.lab1.exception.CanNotCreateDestinationFileException;
import ua.algorithms.lab1.exception.MethodNotImplementedException;
import ua.algorithms.lab1.property.Property;

import java.io.*;
import java.util.Properties;

public abstract class ExternalStraightMergeSortingAlgorithm {

    protected static final File buff1;
    protected static final File buff2;
    protected static final File output;

    public static void sort(File source) {
        // Has no default implementation
    }

    static {
        Properties properties = Property.getInstance().getProperties();
        buff1 = new File(properties.getProperty("default.path.buff1"));
        buff2 = new File(properties.getProperty("default.path.buff2"));
        output = new File(properties.getProperty("default.path.output"));
    }
}
