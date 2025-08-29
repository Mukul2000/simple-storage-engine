package com.simplestorageengine;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Driver {
    private static final int RECORD_HEADER_LENGTH = 1 + 8; // 1 byte for tombstone, 8 bytes for next pointer
    private static final int NAME_LENGTH = 40;
    private static final int RECORD_LENGTH = 100 + RECORD_HEADER_LENGTH;
    private static final String filePath = "database.dat";

    Engine engine;

    Driver() {
        initializeDbFile();
        engine = new Engine(RECORD_LENGTH, filePath);
    }

    public void run() {
        try {
            insertRecord(1, 25, "John Doe");
            insertRecord(2, 30, "Jane Smith");
            insertRecord(3, 22, "Alice Johnson");

            readRecord(0);
            readRecord(1);
            readRecord(2);

            findRecord(1);
            findRecord(2);
            findRecord(5); // Non-existent record
        } catch (IOException e) {
            System.err.println("An I/O error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteRecord(int recordId) {
        int recordNumber = findRecord(recordId);
        if (recordNumber == -1) {
            System.out.println("Record with ID " + recordId + " not found. Cannot delete.");
            return;
        }

        long offset = recordNumber * RECORD_LENGTH;

        // Mark the record as deleted by setting the tombstone byte to 1.
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
            raf.seek(offset);
            raf.write(1); // Set tombstone to 1 (deleted)
            System.out.println("Record with ID " + recordId + " marked as deleted.");


            // update the free list pointer in the header
            raf.seek(0);
            
        } catch (IOException e) {
            System.err.println("An I/O error occurred while marking the record as deleted: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeDbFile() {
        new File(filePath).delete();
        File file = new File(filePath);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            // Write the initial free list pointer (0, indicating an empty list).
            raf.writeLong(0);
        } catch (IOException e) {
            System.err.println("Error initializing file header: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int findRecord(int id) { // return record number
        // search a record by it's ID
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[RECORD_LENGTH];
            int bytesRead;
            int recordNumber = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                if (bytesRead < RECORD_LENGTH) {
                    System.err.println("Incomplete record read. Expected " + RECORD_LENGTH + " bytes, but read "
                            + bytesRead + ".");
                    break;
                }

                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                int currentId = byteBuffer.getInt();

                if (currentId == id) {
                    System.out.println("Record with ID " + id + " found.");
                    // Deserialize and print the record details
                    int age = byteBuffer.getInt();
                    byte[] nameBytes = new byte[NAME_LENGTH];
                    byteBuffer.get(nameBytes);
                    String name = new String(nameBytes, StandardCharsets.UTF_8).trim();

                    System.out.println("ID: " + currentId);
                    System.out.println("Age: " + age);
                    System.out.println("Name: " + name);
                    return recordNumber;
                }
                recordNumber++;
            }
            System.out.println("Record with ID " + id + " not found.");
            return -1; // Record not found
        } catch (IOException e) {
            System.err.println("An I/O error occurred while searching for the record: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    private void insertRecord(int id, int age, String name) throws IOException {
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

        engine.write(recordBuffer);
    }

    private void readRecord(int recordNumber) {
        byte[] recordData = engine.readRecord(recordNumber, RECORD_LENGTH);
        if (recordData == null) {
            System.out.println("Failed to read record number " + recordNumber);
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