package msqueue;

import kotlinx.atomicfu.AtomicRef;

public class MSQueue implements Queue {
    private AtomicRef<Node> head;
    private AtomicRef<Node> tail;

    public MSQueue() {
        Node dummy = new Node(0);
        this.head = new AtomicRef<>(dummy);
        this.tail = new AtomicRef<>(dummy);
    }

    @Override
    public void enqueue(int x) {
        Node newTail = new Node(x);
        while (true) {
            Node currentTail = tail.getValue();
            if (currentTail.next.compareAndSet(null, newTail)) {
                tail.compareAndSet(currentTail, newTail);
                return;
            } else {
                tail.compareAndSet(currentTail, currentTail.next.getValue());
            }
        }
    }

    @Override
    public int dequeue() {
        while (true) {
            Node currentHead = head.getValue();
            Node currentTail = tail.getValue();
            if (currentTail.next.getValue() != null) {
                tail.compareAndSet(currentTail, currentTail.next.getValue());
            }

            if (currentHead.next.getValue() == null) {
                return Integer.MIN_VALUE;
            }

            if (head.compareAndSet(currentHead, currentHead.next.getValue())) {
                return currentHead.next.getValue().x;
            }
        }
    }

    @Override
    public int peek() {
        Node currentHead = head.getValue();
        Node currentTail = tail.getValue();

        if (currentTail.next.getValue() != null) {
            tail.compareAndSet(currentTail, currentTail.next.getValue());
        }

        if (currentHead.next.getValue() == null) {
            return Integer.MIN_VALUE;
        } else {
            return currentHead.next.getValue().x;
        }
    }

    private static class Node {
        private final int x;
        private final AtomicRef<Node> next;

        Node(int x) {
            this.x = x;
            this.next = new AtomicRef<>(null);
        }
    }
}