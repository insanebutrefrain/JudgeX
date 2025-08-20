package insane.model;


import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int content = 13456;
        try (FileWriter writer = new FileWriter("output.txt")) {
            writer.write(content);
        } catch (IOException e) {
        }
    }
}