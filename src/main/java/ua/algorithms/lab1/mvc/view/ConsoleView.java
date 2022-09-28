package ua.algorithms.lab1.mvc.view;

import ua.algorithms.lab1.exception.FetchFileLengthException;
import ua.algorithms.lab1.mvc.controller.Controller;
import ua.algorithms.lab1.mvc.model.ImprovedStraightMerge;
import ua.algorithms.lab1.mvc.model.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ConsoleView implements View {

    private final Controller controller;

    public ConsoleView(Controller controller) {
        this.controller = controller;
    }

    @Override
    public String getSourceFilePath() {
        System.out.print("Write source's path or -1 if you want to exit: ");
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }

    @Override
    public boolean openSourceFile(String path) {
        return false;
    }

    @Override
    public boolean sortSourceFile(String sourcePath, String choice) {
        File source = new File(sourcePath);
        try {
            System.out.println("Sorting...");
            return controller.sortSourceFile(source, choice);
        } catch (IOException e) {
            handleException(e);
            return false;
        }
    }

    @Override
    public void handleException(Exception ex) {
        System.out.println(ex.getMessage());
    }
}
