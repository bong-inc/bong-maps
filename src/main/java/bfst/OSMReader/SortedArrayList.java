package bfst.OSMReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.LongSupplier;

public class SortedArrayList<T extends LongSupplier> implements Iterable<T>{
    private ArrayList<T> list;
    private boolean isSorted;

    public SortedArrayList() {
        list = new ArrayList<T>();
        isSorted = false;
    }

    public void add(T t){
        list.add(t);
    }

    public T get(long id){
        if (!isSorted){
            list.sort(Comparator.comparingLong(T::getAsLong));
            isSorted = true;
        }
        return BinarySearch(id);
    }

    private T BinarySearch(long id){
        int low = 0;
        int high = list.size() - 1;
        while(low <= high){
            int mid = (low + high) / 2;
            T midElement = list.get(mid);
            long midId = midElement.getAsLong();
            if(midId < id){
                low = mid + 1;
            } else if (midId > id) {
                high = mid - 1;
            } else {
                return midElement;
            }
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
