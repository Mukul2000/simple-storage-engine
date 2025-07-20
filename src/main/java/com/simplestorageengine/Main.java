package com.simplestorageengine;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Engine engine = new Engine();
        /*
         * Table format:
         * id - int
         * name - string
         * salary - int
         * dept_name - string
         */
        try {
            File file = new File("database.csv");
            file.delete();
            engine.write("1,srinivas,1,cs");
            engine.write("2,mukul,10,electronics");
            System.out.println(engine.getRecord("1"));
            System.out.println(engine.getRecord("2"));
            System.out.println(engine.getRecord("3"));
        } catch (IOException e) {
            System.out.println("Oops");
        }
    }
}