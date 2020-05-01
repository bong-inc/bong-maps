package bfst.routeFinding;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class IndexMinPQ<Key extends Comparable<Key>>  {
    private long n;
    private HashMap<Long, Long> pq;
    private HashMap<Long, Long> qp;
    private HashMap<Long, Key> keys;

    public IndexMinPQ() {
        n = 0;
        keys = new HashMap<>();
        pq = new HashMap<>();
        qp = new HashMap<>();
    }

    public Key getFromKeys(long i) {
        return keys.get(i);
    }

    public long getFromPq(long i) {
        return pq.get(i);
    }

    public long getFromQp(long i) {
        return qp.get(i);
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public boolean contains(long i) {
        return qp.containsKey(i);
    }

    public long size() {
        return n;
    }

    public void insert(long i, Key key) {
        if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
        n++;
        qp.put(i,n);
        pq.put(n, i);
        keys.put(i, key);
        swim(n);
    }

    public long delMin() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        long min = pq.get(1L);
        exch(1, n--);
        sink(1);
        assert min == pq.get(n+1);
        qp.remove(min);
        keys.remove(min);
        return min;
    }

    public void decreaseKey(long i, Key key) {
        if (keys.get(i).compareTo(key) == 0)
            throw new IllegalArgumentException("Calling decreaseKey() with a key equal to the key in the priority queue");
        if (keys.get(i).compareTo(key) < 0)
            throw new IllegalArgumentException("Calling decreaseKey() with a key strictly greater than the key in the priority queue");
        keys.put(i, key);
        swim(qp.get(i));
    }

    public boolean greater(long i, long j) {
        return keys.get(pq.get(i)).compareTo(keys.get(pq.get(j))) > 0;
    }

    public void exch(long i, long j) {
        long swap = pq.get(i);
        pq.put(i, pq.get(j));
        pq.put(j, swap);
        qp.put(pq.get(i), i);
        qp.put(pq.get(j), j);
    }

    private void swim(long k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(long k) {
        while (2*k <= n) {
            long j = 2*k;
            if (j < n && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }
}
