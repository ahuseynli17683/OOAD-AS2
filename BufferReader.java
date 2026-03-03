/**
 * An independent reader for a RingBuffer.
 * Each BufferReader maintains its own read position (sequence number),
 * so multiple readers can consume the same buffer without interfering
 * with each other.
 *
 * @param <T> the type of elements to read
 */
public class BufferReader<T> {

    private final RingBuffer<T> buffer;
    private long readSequence;
    private final String name;

    /**
     * Creates a reader bound to the given buffer.
     * The reader starts reading from the current write position
     * (i.e. it will only see items written after its creation).
     *
     * @param buffer the ring buffer to read from
     * @param name   a human-readable name for this reader
     * @throws IllegalArgumentException if buffer is null
     */
    public BufferReader(RingBuffer<T> buffer, String name) {
        if (buffer == null) {
            throw new IllegalArgumentException("Buffer must not be null.");
        }
        this.buffer = buffer;
        this.name = name;
        this.readSequence = buffer.getWriteCount();
    }

    /**
     * Reads the next available item from the buffer.
     * If this reader has fallen behind and items have been overwritten,
     * skipped items are reported and the reader fast-forwards.
     *
     * @return the next item, or {@code null} if nothing new is available
     */
    public T read() {
        long currentWriteCount = buffer.getWriteCount();

        // Nothing new to read
        if (readSequence >= currentWriteCount) {
            return null;
        }

        // Detect if this reader was lapped (slow reader scenario)
        long oldestAvailable = currentWriteCount - buffer.getCapacity();
        if (readSequence < oldestAvailable) {
            long missed = oldestAvailable - readSequence;
            System.out.println("[" + name + "] Skipped " + missed
                    + " item(s) — overwritten before they could be read.");
            readSequence = oldestAvailable;
        }

        int index = buffer.toIndex(readSequence);
        T item = buffer.get(index);
        readSequence++;
        return item;
    }

    /**
     * Checks whether there is at least one unread item for this reader.
     *
     * @return {@code true} if unread items exist
     */
    public boolean hasNext() {
        return readSequence < buffer.getWriteCount();
    }

    /**
     * Returns the number of items this reader would miss if it
     * tried to read right now (due to overwrites).
     *
     * @return number of missed items, or 0 if none
     */
    public long getMissedCount() {
        long currentWriteCount = buffer.getWriteCount();
        long oldestAvailable = currentWriteCount - buffer.getCapacity();
        if (readSequence < oldestAvailable) {
            return oldestAvailable - readSequence;
        }
        return 0;
    }

    /**
     * @return the human-readable name of this reader
     */
    public String getName() {
        return name;
    }
}
