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
    public void write_record_success() throws IOException {
    }
}
