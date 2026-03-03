/**
 * Core ring buffer data structure with fixed capacity.
 * Stores elements in a circular array and tracks write position
 * using a monotonically increasing sequence number.
 *
 * @param <T> the type of elements stored in the buffer
 */
public class RingBuffer<T> {

    private final Object[] data;
    private final int capacity;
    private int writePos;
    private long writeCount; // total number of items ever written (sequence counter)

    /**
     * Creates a new RingBuffer with the given capacity.
     *
     * @param capacity maximum number of elements the buffer can hold
     * @throws IllegalArgumentException if capacity is not positive
     */
    public RingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be a positive integer.");
        }
        this.data = new Object[capacity];
        this.capacity = capacity;
        this.writePos = 0;
        this.writeCount = 0;
    }

    /**
     * Stores an item at the current write position and advances the position.
     * If the buffer is full, the oldest element is overwritten.
     *
     * @param item the item to store
     */
    synchronized void put(T item) {
        data[writePos] = item;
        writePos = (writePos + 1) % capacity;
        writeCount++;
    }

    /**
     * Retrieves the element at the given array index.
     *
     * @param index the physical index in the internal array
     * @return the element at that index
     */
    @SuppressWarnings("unchecked")
    synchronized T get(int index) {
        return (T) data[index];
    }

    /**
     * @return the fixed capacity of this buffer
     */
    int getCapacity() {
        return capacity;
    }

    /**
     * @return the total number of items written since creation
     */
    synchronized long getWriteCount() {
        return writeCount;
    }

    /**
     * Converts a logical sequence number to a physical array index.
     *
     * @param sequence the logical sequence number
     * @return the corresponding index in the internal array
     */
    int toIndex(long sequence) {
        return (int) (sequence % capacity);
    }
}
