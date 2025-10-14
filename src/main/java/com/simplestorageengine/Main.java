package com.simplestorageengine;

import java.io.IOException;


/*
    * Table format:
    * id - int
    * name - string
    * age - int
*/

public class Main {

    private static final int RECORD_HEADER_LENGTH = 1 + 8; // 1 byte for tombstone, 8 bytes for next pointer
    private static final int FILE_HEADER_LENGTH = 8; // 8 bytes for free list pointer 
    private static final int NAME_LENGTH = 40;
    private static final int RECORD_LENGTH = 100 + RECORD_HEADER_LENGTH;
    private static final String filePath = "database.dat";

    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        Engine engine = new Engine(RECORD_LENGTH, filePath, FILE_HEADER_LENGTH, RECORD_HEADER_LENGTH);
        Driver driver = new Driver(engine);
        driver.run();
    }
}