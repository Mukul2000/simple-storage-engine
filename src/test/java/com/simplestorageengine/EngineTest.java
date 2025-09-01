package com.simplestorageengine;

import org.junit.Test;
import static org.mockito.Mockito.*;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

public class EngineTest {
    private static final int RECORD_HEADER_LENGTH = 1 + 8; // 1 byte for tombstone, 8 bytes for next pointer
    private static final int FILE_HEADER_LENGTH = 8; // 8 bytes for free list pointer 
    private static final int NAME_LENGTH = 40;
    private static final int RECORD_LENGTH = 100 + RECORD_HEADER_LENGTH;
    private static final String filePath = "database_test.dat";

    @Test
    public void write_record_success() throws IOException {}

    @Test
    public void write_record_recordLengthExceedsLimit_throwsException() {}

    @Test 
    public void write_record_throwsIOException_propagatesException() throws IOException {}

    @Test
    public void write_record_appends_to_end_of_file_when_no_free_space() throws IOException {}

    @Test
    public void write_record_reuses_deleted_record_space_when_available() throws IOException {}

    @Test
    public void read_record_success() throws IOException {}

    @Test
    public void read_record_recordNumberOutOfBounds_returnsNull() throws IOException {}

    @Test
    public void read_record_deletedRecord_returnsNull() throws IOException {}

    @Test
    public void read_record_throwsIOException_propagatesException() throws IOException {}

    @Test
    public void read_record_incompleteRead_throwsIOException() throws IOException {}

    @Test
    public void initializeDbFile_createsFileWithHeader() throws IOException {}

    @Test
    public void initializeDbFile_fileAlreadyExists_recreates_new() throws IOException {}

    @Test
    public void initializeDbFile_throwsIOException_propagatesException() throws IOException {}
    
    @Test
    public void delete_record_success() throws IOException {}
    
    @Test
    public void delete_record_recordNotFound_noOp() throws IOException {}

    @Test
    public void delete_record_throwsIOException_propagatesException() throws IOException {}

    @Test
    public void delete_record_offset_out_of_bounds_throwsIOException() throws IOException {}

    @Test
    public void delete_record_updates_free_list_pointer_in_header() throws IOException {}

    @Test
    public void search_record_found_returns_record_number() throws IOException {}

    @Test
    public void search_record_not_found_returns_minus_one() throws IOException {}

    @Test
    public void search_record_throwsIOException_propagatesException() throws IOException {}

}
