/**
 * Single writer endpoint for a RingBuffer.
 * Encapsulates write access so that only one writer interacts with the buffer.
 *
 * @param <T> the type of elements to write
 */
public class BufferWriter<T> {

    private final RingBuffer<T> buffer;

    /**
     * Creates a writer bound to the given buffer.
     *
     * @param buffer the ring buffer to write into
     * @throws IllegalArgumentException if buffer is null
     */
    public BufferWriter(RingBuffer<T> buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("Buffer must not be null.");
        }
        this.buffer = buffer;
    }

    /**
     * Writes an item into the ring buffer.
     * If the buffer is full, the oldest item is overwritten.
     *
     * @param item the item to write
     */
    public void write(T item) {
        buffer.put(item);
    }
}
