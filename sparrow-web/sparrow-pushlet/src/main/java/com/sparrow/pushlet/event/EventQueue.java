package com.sparrow.pushlet.event;


public class EventQueue {
    private int capacity = 8;
    private Event[] queue = null;
    private int front, rear;

    public EventQueue() {
        this(8);
    }

    public EventQueue(int capacity) {
        this.capacity = capacity;
        queue = new Event[capacity];
        front = rear = 0;
    }

    public synchronized boolean enQueue(Event item) throws InterruptedException {
        return enQueue(item, -1);
    }

    public synchronized boolean enQueue(Event item, long maxWaitTime) throws InterruptedException {
        while (isFull()) {
            if (maxWaitTime > 0) {
                wait(maxWaitTime);
                if (isFull()) {
                    return false;
                }
            } else {
                wait();
            }
        }
        queue[rear] = item;
        rear = next(rear);
        notifyAll();
        return true;
    }

    public synchronized Event deQueue() throws InterruptedException {
        return deQueue(-1);
    }

    public synchronized Event deQueue(long maxWaitTime) throws InterruptedException {
        while (isEmpty()) {
            if (maxWaitTime >= 0) {
                wait(maxWaitTime);
                if (isEmpty()) {
                    return null;
                }
            } else {
                wait();
            }
        }
        Event result = fetchNext();
        notifyAll();
        return result;
    }

    public synchronized Event[] deQueueAll(long maxWaitTime) throws InterruptedException {
        while (isEmpty()) {
            if (maxWaitTime >= 0) {
                wait(maxWaitTime);
                if (isEmpty()) {
                    return null;
                }
            } else {
                wait();
            }
        }
        Event[] events = new Event[getSize()];
        for (int i = 0; i < events.length; i++) {
            events[i] = fetchNext();
        }
        notifyAll();
        return events;
    }

    public synchronized int getSize() {
        return (rear >= front) ? (rear - front) : (capacity - front + rear);
    }

    public synchronized boolean isEmpty() {
        return front == rear;
    }

    public synchronized boolean isFull() {
        return (next(rear) == front);
    }

    private int next(int index) {
        return (index + 1 < capacity ? index + 1 : 0);
    }

    private Event fetchNext() {
        Event temp = queue[front];
        queue[front] = null;
        front = next(front);
        return temp;
    }

    public static void p(String s) {
        System.out.println(s);
    }
}
