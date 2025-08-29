package com.simplestorageengine;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/*
    * Table format:
    * id - int
    * name - string
    * age - int
*/

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");

        Driver driver = new Driver();
        driver.run();
    }
}