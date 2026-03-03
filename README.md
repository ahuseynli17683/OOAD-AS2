# Ring Buffer — Multiple Readers, Single Writer

## Project Overview

A generic, fixed-capacity **Ring Buffer** (circular buffer) implemented in Java.  
The design supports **one writer** and **multiple independent readers**.  
When the buffer is full the writer overwrites the oldest data; slow readers that
fall behind are notified of skipped items and fast-forwarded automatically.

---

## Design & Class Responsibilities

| Class | Responsibility |
|---|---|
| **`RingBuffer<T>`** | Core data structure. Owns the fixed-size circular array, the current write position and a monotonically-increasing write-count (sequence number). Provides package-private helpers used by `BufferWriter` and `BufferReader`. |
| **`BufferWriter<T>`** | Single-writer endpoint. Accepts items via `write(T)` and delegates storage to `RingBuffer.put()`. Enforces the "one writer" role by design (only one instance is created). |
| **`BufferReader<T>`** | Independent reader endpoint. Each instance holds its own `readSequence` so readers never interfere with one another. Detects when items have been overwritten (slow-reader case) and reports the number of missed items. |
| **`Main`** | Demo / test driver that exercises all three scenarios: basic reads, overwrite detection, and late-joining readers. |

### Key Design Decisions

* **Sequence-number tracking** — instead of a simple head/tail pair, a global
  `writeCount` lets each reader independently determine what is still available.
* **Separation of concerns** — reading logic lives in `BufferReader`, writing
  logic lives in `BufferWriter`, and raw storage lives in `RingBuffer`.
* **Thread safety** — critical sections in `RingBuffer` are `synchronized` so
  the design is safe for concurrent access.

---

## UML Class Diagram

```
┌─────────────────────────────────────┐
│          RingBuffer<T>              │
├─────────────────────────────────────┤
│ - data        : Object[]           │
│ - capacity    : int                 │
│ - writePos    : int                 │
│ - writeCount  : long                │
├─────────────────────────────────────┤
│ + RingBuffer(capacity: int)         │
│ ~ put(item: T): void               │
│ ~ get(index: int): T               │
│ ~ getCapacity(): int               │
│ ~ getWriteCount(): long            │
│ ~ toIndex(sequence: long): int     │
└──────────┬──────────────────────────┘
           │
     ┌─────┴──────┐
     │             │
     ▼             ▼
┌──────────────┐ ┌────────────────────────────┐
│BufferWriter<T>│ │      BufferReader<T>        │
├──────────────┤ ├────────────────────────────┤
│ - buffer     │ │ - buffer : RingBuffer<T>    │
│              │ │ - readSequence : long       │
│              │ │ - name : String             │
├──────────────┤ ├────────────────────────────┤
│ + write(T)   │ │ + read(): T                │
│              │ │ + hasNext(): boolean        │
│              │ │ + getMissedCount(): long    │
│              │ │ + getName(): String         │
└──────────────┘ └────────────────────────────┘
```

> **Relationships**  
> `BufferWriter` ──*uses*──▶ `RingBuffer`  
> `BufferReader` ──*uses*──▶ `RingBuffer`  
> One `BufferWriter` per buffer; many `BufferReader`s per buffer.

---

## UML Sequence Diagram — `write()`

```
  Client          BufferWriter          RingBuffer
    │                  │                     │
    │  write(item)     │                     │
    │─────────────────►│                     │
    │                  │   put(item)         │
    │                  │────────────────────►│
    │                  │                     │── store item at data[writePos]
    │                  │                     │── writePos = (writePos+1) % capacity
    │                  │                     │── writeCount++
    │                  │      return         │
    │                  │◄────────────────────│
    │     return       │                     │
    │◄─────────────────│                     │
```

---

## UML Sequence Diagram — `read()`

```
  Client          BufferReader            RingBuffer
    │                  │                       │
    │   read()         │                       │
    │─────────────────►│                       │
    │                  │  getWriteCount()      │
    │                  │──────────────────────►│
    │                  │◄──────────────────────│  (currentWriteCount)
    │                  │                       │
    │                  │── if readSeq >= currentWriteCount
    │                  │      return null (nothing to read)
    │                  │                       │
    │                  │── if readSeq < oldest available
    │                  │      log skipped items│
    │                  │      readSeq = oldestAvailable
    │                  │                       │
    │                  │  toIndex(readSeq)     │
    │                  │──────────────────────►│
    │                  │◄──────────────────────│  (index)
    │                  │                       │
    │                  │  get(index)           │
    │                  │──────────────────────►│
    │                  │◄──────────────────────│  (item)
    │                  │                       │
    │                  │── readSequence++      │
    │    item          │                       │
    │◄─────────────────│                       │
```

---

## How to Compile & Run

### Prerequisites
* **Java 8+** (JDK)

### Compile

```bash
cd src
javac ringbuffer/*.java
```

### Run

```bash
cd src
java ringbuffer.Main
```

### Expected Output (abbreviated)

```
========================================
 Ring Buffer — Single Writer, Multi Reader
========================================

--- Scenario 1: Basic write & independent reads ---
Writer  -> write(10), write(20), write(30)
Reader-1 reads: 10
Reader-1 reads: 20
Reader-2 reads: 10

--- Scenario 2: Overwrite (slow reader) ---
Writer  -> write(40), write(50), write(60), write(70), write(80)

Reader-1 (was at seq 2, oldest available is seq 3):
  [Reader-1] Skipped 1 item(s) — overwritten before they could be read.
  Reader-1 reads: 40
  Reader-1 reads: 50
  ...
```

---

## License

This project is for educational purposes (OOAD Assignment 2).
