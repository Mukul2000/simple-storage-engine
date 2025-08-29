package com.simplestorageengine;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class Engine {
    // 1. Fixed length storage
    // 2. Data is written in binary, binary is just another serialization format
    // 3. Implement delete functionality later
    private int fixedLength; 
    private String filePath;

    Engine(int fixedLength, String filePath) {
        this.fixedLength = fixedLength;
        this.filePath = filePath;
    }

    public void write(byte[] record) throws IOException { // string of csv values
        if (record.length != fixedLength) {
            throw new IllegalArgumentException("Record length exceeds limit");
        }

        try (FileOutputStream fos = new FileOutputStream(this.filePath, true)) {
            // 3. Write the entire block of binary data to the file.
            fos.write(record);
        } catch (IOException e) {
            System.err.println("An I/O error occurred while writing to the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public byte[] readRecord(long recordNumber, int recordLength) {
        // Use RandomAccessFile for the ability to seek to a specific position.
        try (RandomAccessFile raf = new RandomAccessFile(this.filePath, "r")) {
            // Calculate the byte offset of the desired record.
            long offset = recordNumber * recordLength;

            // Check if the offset is within the file's bounds.
            if (offset >= raf.length()) {
                System.err.println("Error: Record number " + recordNumber + " is out of bounds.");
                return null;
            }

            // Seek to the calculated position in the file.
            raf.seek(offset);

            // Create a byte array to hold the record data.
            byte[] recordData = new byte[recordLength];

            // Read the record from the file.
            int bytesRead = raf.read(recordData);

            // Verify that we read the entire record.
            if (bytesRead != recordLength) {
                System.err.println("Error: Incomplete record read. Expected " + recordLength +
                                   " bytes, but read " + bytesRead + ".");
                throw new IOException("Incomplete record read");
            }
            return recordData;
        } catch (IOException e) {
            System.err.println("An I/O error occurred while reading the file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
