package com.home.codingassignment;

import com.home.codingassignment.task2.Task2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

@SpringBootApplication
public class CodingAssignmentApplication {

    public static void main(String[] args) throws SQLException, FileNotFoundException {

        SpringApplication.run(CodingAssignmentApplication.class, args);

        if (args.length == 2) {
            if (args[0].equals("-f")) {
                String filename = args[1];
                File file = new File(filename);

                if (file.exists()) {
                    // System.getProperty("user.dir") + "/src/main/java/com/home/codingassignment/task2/fo_random.txt"
                    Task2.readAndSaveBets(filename);
                } else {

                    throw new FileNotFoundException(filename + " not found");

                }

            } else {
                throw new IllegalArgumentException(args[0]);
            }
        }
    }

}
