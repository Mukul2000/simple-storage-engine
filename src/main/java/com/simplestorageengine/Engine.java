package com.simplestorageengine;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class Engine {
    private int recordLength; 
    private int fileHeaderLength; 
    private String filePath;
    private int recordHeaderLength;

    Engine(int recordLength, String filePath, int fileHeaderLength, int recordHeaderLength) {
        this.recordLength = recordLength;
        this.filePath = filePath;
        this.fileHeaderLength = fileHeaderLength;
        this.recordHeaderLength = recordHeaderLength;
        initializeDbFile();
    }

    public void write(byte[] record) throws IOException { // string of csv values
        if (record.length != recordLength) {
            throw new IllegalArgumentException("Record length exceeds limit");
        }

        try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
            // 1. Check the free list pointer in the file header to see if there are any deleted records that can be reused.
            raf.seek(0);
            long freeListHead = raf.readLong();
            if (freeListHead != 0) {
                // 2. If there are deleted records, reuse the space of the first deleted record in the free list.
                long offset = fileHeaderLength + freeListHead * recordLength;
                raf.seek(offset);
            } else {
                raf.seek(raf.length()); // append to the end of the file
            }
            raf.write(record);
        } catch(IOException e) {
            System.err.println("An I/O error occurred while accessing the file: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public byte[] read(int recordNumber) {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            long offset = fileHeaderLength + recordNumber * recordLength;
            
            if (offset >= raf.length()) {
                System.err.println("Error: Record number " + recordNumber + " is out of bounds.");
                return null;
            }

            raf.seek(offset);
            byte status = raf.readByte();
            if (status == 0) {
                System.err.println("Error: Record " + recordNumber + " is a deleted record.");
                return null;
            }

            raf.skipBytes(8); // Skip the next-free pointer
            
            byte[] recordData = new byte[recordLength - recordHeaderLength];
            int bytesRead = raf.read(recordData);

            if (bytesRead != recordLength - recordHeaderLength) {
                return null;
            }

            return recordData;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteRecord(int recordNumber) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
            raf.seek(0);
            long nextToHead = raf.readLong();
            int offset = fileHeaderLength + recordNumber * recordLength;
            if (offset >= raf.length()) {
                System.err.println("Error: Record number " + recordNumber + " is out of bounds.");
                return;
            }

            // add this deleted record next to the head of the free list.
            // insertions become very fast
            raf.seek(offset);
            raf.write(0); // Mark the record as deleted by setting the tombstone byte to 0.
            raf.writeLong(nextToHead);

            raf.seek(0);
            raf.writeLong(recordNumber); // freelist stores record number of the next record
        } catch (IOException e) {
            System.err.println("An I/O error occurred while marking the record as deleted: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // search a record by it's ID and return the record number
    public int searchRecordById(int id) {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            long numRecords = (raf.length() - fileHeaderLength) / recordLength;

            for (int i = 0; i < numRecords; i++) {
                // Seek to the start of the current record.
                long offset = fileHeaderLength + i * recordLength;
                raf.seek(offset);

                // Read the status byte and check if the record is active.
                byte status = raf.readByte();
                if (status == 1) {
                    // Skip the next-free pointer.
                    raf.skipBytes(8);
                    
                    // Read the ID of the current record.
                    int currentId = raf.readInt();
                    if (currentId == id) {
                        return i;
                    }
                }
            }
            return -1;
        } catch (IOException e) {
            System.err.println("An I/O error occurred during search: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    private void initializeDbFile() {
        File file = new File(filePath);
        if (!file.exists() || file.length() < fileHeaderLength) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                // Write the initial free list pointer (0, indicating an empty list).
                raf.writeLong(0);
            } catch (IOException e) {
                System.err.println("Error initializing file header: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
