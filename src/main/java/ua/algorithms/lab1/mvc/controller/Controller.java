package ua.algorithms.lab1.mvc.controller;

import ua.algorithms.lab1.mvc.model.Model;

import java.io.File;
import java.io.IOException;

public interface Controller {

    boolean sortSourceFile(File source, String choice) throws IOException;

}
