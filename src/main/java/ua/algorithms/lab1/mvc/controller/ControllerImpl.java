package ua.algorithms.lab1.mvc.controller;

import ua.algorithms.lab1.exception.CloseConnectionException;
import ua.algorithms.lab1.exception.FetchFileLengthException;
import ua.algorithms.lab1.exception.FileAccessException;
import ua.algorithms.lab1.mvc.model.ImprovedStraightMerge;
import ua.algorithms.lab1.mvc.model.Model;
import ua.algorithms.lab1.mvc.model.StraightMerge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ControllerImpl implements Controller {
    @Override
    public boolean sortSourceFile(File source, String choice) throws IOException {
        Model model;
        try {
            if (choice.equals("1")) {
                model = StraightMerge.getInstance(source);
                model.sort();
            } else if (choice.equals("2")) {
                model = ImprovedStraightMerge.getInstance(source);
                model.sort();
            }
            return true;
        } catch (FetchFileLengthException e) {
            throw new FetchFileLengthException(e);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (FileAccessException e) {
            throw new FileAccessException(e);
        } catch (CloseConnectionException e) {
            throw new CloseConnectionException(e);
        }
    }
}
