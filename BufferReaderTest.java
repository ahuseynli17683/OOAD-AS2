package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BufferReaderTest {
    @Test
    void readerShouldNotAllowNullName() {

        RingBuffer<String> buffer = new RingBuffer<>(3);

        BufferReader<String> reader =
                new BufferReader<>(buffer, null);

        assertNotNull(reader.getName(),
                "Reader name should never be null");
    }

    @Test
    void readerShouldNotAllowEmptyName() {

        RingBuffer<String> buffer = new RingBuffer<>(3);

        BufferReader<String> reader =
                new BufferReader<>(buffer, "");

        assertFalse(reader.getName().isBlank(),
                "Reader name should not be empty");
    } 
}
