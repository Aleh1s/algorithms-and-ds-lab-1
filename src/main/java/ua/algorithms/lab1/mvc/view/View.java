package ua.algorithms.lab1.mvc.view;

public interface View {
    boolean sortSourceFile(String source, String choice);
    void handleException(Exception e);
}
