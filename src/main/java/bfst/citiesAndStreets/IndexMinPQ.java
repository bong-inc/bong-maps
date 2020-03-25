package bfst.citiesAndStreets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IndexMinPQ<Key extends Comparable<Key>>  {
    private int n;           // number of elements on PQ
    private HashMap<Integer, Long> pq;
    private HashMap<Long, Integer> qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private HashMap<Long, Key> keys;

    /**
     * Initializes an empty indexed priority queue with indices between {@code 0}
     * and {@code maxN - 1}.
     * @param  maxN the keys on this priority queue are index from {@code 0}
     *         {@code maxN - 1}
     * @throws IllegalArgumentException if {@code maxN < 0}
     */
    public IndexMinPQ() {
        n = 0;
        keys = new HashMap<>();    // make this of length maxN??
        pq = new HashMap<>();
        qp = new HashMap<>();                   // make this of length maxN??
    }
    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty;
     *         {@code false} otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Is {@code i} an index on this priority queue?
     *
     * @param  i an index
     * @return {@code true} if {@code i} is an index on this priority queue;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     */
    public boolean contains(long i) {
        return qp.containsKey(i);
    }

    /**
     * Returns the number of keys on this priority queue.
     *
     * @return the number of keys on this priority queue
     */
    public int size() {
        return n;
    }

    /**
     * Associates key with index {@code i}.
     *
     * @param  i an index
     * @param  key the key to associate with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if there already is an item associated
     *         with index {@code i}
     */
    public void insert(long i, Key key) {
        if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
        n++;
        qp.put(i,n);
        pq.put(n, i);
        keys.put(i, key);
        swim(n);
    }


    /**
     * Removes a minimum key and returns its associated index.
     * @return an index associated with a minimum key
     * @throws NoSuchElementException if this priority queue is empty
     */
    public long delMin() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        long min = pq.get(1);
        exch(1, n--);
        sink(1);
        assert min == pq.get(n+1);
        qp.remove(min);
        keys.remove(min);
        return min;
    }

    /**
     * Decrease the key associated with index {@code i} to the specified value.
     *
     * @param  i the index of the key to decrease
     * @param  key decrease the key associated with index {@code i} to this key
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if {@code key >= keyOf(i)}
     * @throws NoSuchElementException no key is associated with index {@code i}
     */
    public void decreaseKey(long i, Key key) {
        if (keys.get(i).compareTo(key) == 0)
            throw new IllegalArgumentException("Calling decreaseKey() with a key equal to the key in the priority queue");
        if (keys.get(i).compareTo(key) < 0)
            throw new IllegalArgumentException("Calling decreaseKey() with a key strictly greater than the key in the priority queue");
        keys.put(i, key);
        swim(qp.get(i));
    }



    /***************************************************************************
     * General helper functions.
     ***************************************************************************/
    private boolean greater(int i, int j) {
        return keys.get(pq.get(i)).compareTo(keys.get(pq.get(j))) > 0;
    }

    private void exch(int i, int j) {
        long swap = pq.get(i);
        pq.put(i, pq.get(j));
        pq.put(j, swap);
        qp.put(pq.get(i), i);
        qp.put(pq.get(j), j);
    }


    /***************************************************************************
     * Heap helper functions.
     ***************************************************************************/
    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

}
