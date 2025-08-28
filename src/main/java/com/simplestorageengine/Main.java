package com.simplestorageengine;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    private static final int NAME_LENGTH = 40;
    private static final int RECORD_LENGTH = 100;
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Engine engine = new Engine(RECORD_LENGTH, "database.dat");
        /*
         * Table format:
         * id - int
         * name - string
         * age - int
         */
        int id = 1;
        String name = "Jane Doe";
        int age = 25;

        // Create a ByteBuffer with the fixed record length.
        ByteBuffer buffer = ByteBuffer.allocate(RECORD_LENGTH);

        // Convert the string to a byte array.
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);

        // Put the name bytes into the buffer.
        // The put() method will automatically advance the buffer's position.
        buffer.put(nameBytes);

        // To pad the remaining space in the name field, we can explicitly move the position.
        // This ensures the next piece of data (the age) starts at the correct offset.
        buffer.position(NAME_LENGTH);
        
        // Put the integer into the buffer.
        buffer.putInt(id);

        buffer.position(NAME_LENGTH + Integer.BYTES);
        buffer.putInt(age);

        // Get the final byte array from the buffer for writing to the file.
        byte[] recordBuffer = buffer.array();

        try {
            engine.write(recordBuffer);
        } catch (IOException e) {
            System.out.println("Oops");
        }
    }
}