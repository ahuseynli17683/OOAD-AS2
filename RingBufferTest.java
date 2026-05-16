package org.example;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RingBufferTest {
    @Test
    void toIndexShouldNotProduceNegativeIndex() {

        RingBuffer<Integer> buffer = new RingBuffer<>(3);

        int index = buffer.toIndex(-1);

        assertTrue(index >= 0, "toIndex() produced negative array index");
    }
}