public class Main {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println(" Ring Buffer — Single Writer, Multi Reader");
        System.out.println("========================================\n");

        final int CAPACITY = 5;
        RingBuffer<Integer> buffer = new RingBuffer<>(CAPACITY);
        BufferWriter<Integer> writer = new BufferWriter<>(buffer);

        BufferReader<Integer> reader1 = new BufferReader<>(buffer, "Reader-1");
        BufferReader<Integer> reader2 = new BufferReader<>(buffer, "Reader-2");

        System.out.println("--- Scenario 1: Basic write & independent reads ---");
        System.out.println("Writer writes: 10");
        writer.write(10);
        System.out.println("Writer writes: 20");
        writer.write(20);
        System.out.println("Writer writes: 30");
        writer.write(30);

        System.out.println("Reader-1 reads: " + reader1.read());
        System.out.println("Reader-1 reads: " + reader1.read());
        System.out.println("Reader-2 reads: " + reader2.read());
        System.out.println();

        System.out.println("--- Scenario 2: Overwrite (slow reader) ---");
        System.out.println("Writer writes: 40");
        writer.write(40);
        System.out.println("Writer writes: 50");
        writer.write(50);
        System.out.println("Writer writes: 60");
        writer.write(60);
        System.out.println("Writer writes: 70");
        writer.write(70);
        System.out.println("Writer writes: 80");
        writer.write(80);

        System.out.println();
        System.out.println("Reader-1 (was at seq 2, oldest available is seq 3):");
        while (reader1.hasNext()) {
            System.out.println("  Reader-1 reads: " + reader1.read());
        }

        System.out.println();
        System.out.println("Reader-2 (was at seq 1, oldest available is seq 3):");
        while (reader2.hasNext()) {
            System.out.println("  Reader-2 reads: " + reader2.read());
        }

        System.out.println();
        System.out.println("--- Scenario 3: Late-joining reader ---");
        BufferReader<Integer> reader3 = new BufferReader<>(buffer, "Reader-3");
        System.out.println("Reader-3 created (starts at current write position).");
        System.out.println("Reader-3 hasNext? " + reader3.hasNext());

        System.out.println("Writer writes: 90");
        writer.write(90);
        System.out.println("Writer writes: 100");
        writer.write(100);
        System.out.println("Reader-3 reads: " + reader3.read());
        System.out.println("Reader-3 reads: " + reader3.read());
        System.out.println("Reader-3 hasNext? " + reader3.hasNext());

        System.out.println("\n========== Done ==========");
    }
}
