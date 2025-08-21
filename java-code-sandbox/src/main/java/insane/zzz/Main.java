package insane.zzz;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        try {
            File file = new File("example.txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("Hello, World!");
            writer.close();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}