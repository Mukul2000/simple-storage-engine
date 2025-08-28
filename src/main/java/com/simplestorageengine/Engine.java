package com.simplestorageengine;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Engine {
    // 1. Fixed length storage
    // 2. Data is written in binary, binary is just another serialization format
    // 3. Implement delete functionality later
    private int fixedLength; 
    private String dbName;

    Engine(int fixedLength, String dbName) {
        this.fixedLength = fixedLength;
        this.dbName = dbName;
    }

    public void write(byte[] record) throws IOException { // string of csv values
        if (record.length != fixedLength) {
            throw new IllegalArgumentException("Record length exceeds limit");
        }


        String storeString = builder.toString();
        try {
            // Create a FileWriter object with append mode set to true
            FileWriter fileWriter = new FileWriter(DB_NAME, true);

            // Write the content to the file
            fileWriter.append(storeString);

            System.out.println("Content appended successfully to " + DB_NAME);

            // Close the FileWriter to release resources
            fileWriter.close();

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String getRecord(String id) throws IOException {
        // scan through the file and get the record with the id = provided id to this function

        return "";
    }

    public void deleteRecord(String id) throws IOException {
        FindResult record = findRecord(id);
        if (record.getOffset() == -1) {
            // doesn't exist
            return;
        }

        // tombstone this record
        // ideally you wouldn't want to overwrite this, waste of disk writes
        // something needs to be done here.

    }

    private FindResult findRecord(String id) throws IOException {
        int offset = 0;
        while (offset <= 64) {
            byte[] data = readBytesAtOffset(offset, FIXED_LENGTH);
            String csvString = new String(data,  StandardCharsets.UTF_8);
            String[] recordValues = csvString.split(",");
            if (recordValues[0].equals(id)) {
                return new FindResult(data, offset, FIXED_LENGTH);
            }
            offset += FIXED_LENGTH;
        }
        return new FindResult(new byte[0], -1, -1);
    }

    private byte[] readBytesAtOffset(int offset, int length) throws IOException {
        if (offset < 0 || length < 0) {
            throw new IllegalArgumentException("Offset and length cannot be negative.");
        }
        if (length == 0) {
            return new byte[0];
        }

        ByteBuffer buffer = ByteBuffer.allocate(length);

        try (FileChannel fileChannel = FileChannel.open(Paths.get(DB_NAME), StandardOpenOption.READ)) {

            // Get the actual size of the file
            Long fileSize = fileChannel.size();

            // Check if the requested read is within file bounds
            if (offset >= fileSize) {
                System.out.println("Warning: Offset is beyond file length. No bytes will be read.");
                return new byte[0];
            }

            // Calculate how many bytes can actually be read from the given offset
            long bytesToReadFromFile = Math.min(length, fileSize - offset);
            if (bytesToReadFromFile <= 0) {
                return new byte[0];
            }

            int bytesRead = fileChannel.read(buffer, offset);

            if (bytesRead == -1) { // End of file immediately
                return new byte[0];
            } else {
                buffer.flip(); // Set limit to current position, position to 0
                byte[] result = new byte[length];
                buffer.get(result);
                return result;
            }
        }
    }
    
    class FindResult {
        private final byte[] data;
        private final int offset;
        private final int length;

        FindResult(byte[] data, int offset, int length) {
            this.data = data;
            this.offset = offset;
            this.length = length;
        }

        public byte[] getData() {
            return data;
        }

        public int getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
        }
    }
}
