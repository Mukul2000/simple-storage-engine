package com.simplestorageengine;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Driver {
    private static final int RECORD_HEADER_LENGTH = 1 + 8; // 1 byte for tombstone, 8 bytes for next pointer
    private static final int FILE_HEADER_LENGTH = 8; // 8 bytes for free list pointer 
    private static final int NAME_LENGTH = 40;
    private static final int RECORD_LENGTH = 100 + RECORD_HEADER_LENGTH;
    private static final String filePath = "database.dat";

    Engine engine;

    Driver() {
        engine = new Engine(RECORD_LENGTH, filePath, FILE_HEADER_LENGTH, RECORD_HEADER_LENGTH);
    }

    public void run() {
        try {
            System.out.println("Inserting records");
            insertRecord(1, 25, "John Doe");
            insertRecord(2, 23, "bad boy");

            readRecord(1);
            readRecord(0); // Non-existent record
            readRecord(2);

            // engine.findRecord(1);
            // engine.findRecord(2);
            // engine.findRecord(5); // Non-existent record
        } catch (IOException e) {
            System.err.println("An I/O error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // private void deleteRecord(int recordId) {
    //     int recordNumber = engine.findRecord(recordId);
    //     if (recordNumber == -1) {
    //         System.out.println("Record with ID " + recordId + " not found. Cannot delete.");
    //         return;
    //     }

    //     long offset = recordNumber * RECORD_LENGTH;

    //     // Mark the record as deleted by setting the tombstone byte to 1.
    //     try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
    //         raf.seek(offset);
    //         raf.write(1); // Set tombstone to 1 (deleted)
    //         System.out.println("Record with ID " + recordId + " marked as deleted.");


    //         // update the free list pointer in the header
    //         raf.seek(0);
            
    //     } catch (IOException e) {
    //         System.err.println("An I/O error occurred while marking the record as deleted: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }

    private void insertRecord(int id, int age, String name) throws IOException {
        // Create a ByteBuffer with the fixed record length.
        ByteBuffer buffer = ByteBuffer.allocate(RECORD_LENGTH);

        buffer.put((byte) 1); // Tombstone byte (0 = deleted, 1 = active)

        buffer.putLong(0L); // Next pointer (0 = end of free list)

        // Put the integer into the buffer.
        buffer.putInt(id);

        buffer.putInt(age);

        // Convert the string to a byte array.
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);

        buffer.put(nameBytes);

        // Get the final byte array from the buffer for writing to the file.
        byte[] recordBuffer = buffer.array();

        engine.write(recordBuffer);
    }

    private void readRecord(int id) {
        int recordNumber = engine.searchRecordById(id);
        if (recordNumber == -1) {
            System.out.println("Record with ID " + id + " not found.");
            return;
        }

        byte[] recordData = engine.read(recordNumber);
        if (recordData == null) {
            System.out.println("Failed to read record id " + id);
            return;
        }

        // Deserialize the byte array back into a string and an integer.
        ByteBuffer readBuffer = ByteBuffer.wrap(recordData);

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
        System.out.println("-------------------------\n\n");
    }
}