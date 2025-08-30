package com.simplestorageengine;

import java.io.IOException;


/*
    * Table format:
    * id - int
    * name - string
    * age - int
*/

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        Driver driver = new Driver();
        driver.run();
    }
}