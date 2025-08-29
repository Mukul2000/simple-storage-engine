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

        // Put the integer into the buffer.
        buffer.putInt(id);

        buffer.putInt(age);

        // Convert the string to a byte array.
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);

        buffer.put(nameBytes);

        // Get the final byte array from the buffer for writing to the file.
        byte[] recordBuffer = buffer.array();

        try {
            engine.write(recordBuffer);

            byte[] readRecordData = engine.readRecord(0, RECORD_LENGTH);

            // Deserialize the byte array back into a string and an integer.
            ByteBuffer readBuffer = ByteBuffer.wrap(readRecordData);

            int readId = readBuffer.getInt();

            // Read the age integer.
            int readAge = readBuffer.getInt();

            // Read the name bytes and convert to a string.
            byte[] readNameBytes = new byte[NAME_LENGTH];
            readBuffer.get(readNameBytes);
            String readName = new String(readNameBytes, StandardCharsets.UTF_8).trim();



            System.out.println("Record Read Successfully!");
            System.out.println("id: " + readId);
            System.out.println("Age: " + readAge);
            System.out.println("Name: " + readName);
        } catch (IOException e) {
            System.out.println("Oops, it broke");

        }
    }
}