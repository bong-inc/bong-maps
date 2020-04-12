package bfst.OSMReader;

public class NodeContainer {

    private long[] ids;
    private float[] lons;
    private float[] lats;

    private int fill;

    private boolean sorted;

    public NodeContainer(){
        ids = new long[10];
        lons = new float[10];
        lats = new float[10];

        fill = 0;
    }

    public int getSize(){
        return fill;
    }

    public void add(long id, float lon, float lat){
        if(sorted) sorted = false;
        if(fill == ids.length){
            extend();
        }
        ids[fill] = id;
        lons[fill] = lon;
        lats[fill] = lat;

        fill++;
    }

    private void extend(){
        long[] cpyIds = ids;
        ids = new long[cpyIds.length * 2];
        for(int i = 0; i < cpyIds.length; i++){
            ids[i] = cpyIds[i];
        }

        float[] cpyLons = lons;
        lons = new float[cpyLons.length * 2];
        for(int i = 0; i < cpyLons.length; i++){
            lons[i] = cpyLons[i];
        }

        float[] cpyLats = lats;
        lats = new float[cpyLats.length * 2];
        for(int i = 0; i < cpyLats.length; i++){
            lats[i] = cpyLats[i];
        }
    }

    public Node get(long id){
        if(!sorted){
            sort(0, fill-1);
            sorted = true;
        }
        int index = BinarySearchIndex(id);
        if(index == -1){
            return null;
        }
        return new Node(ids[index], lons[index], lats[index]);
    }

    public int getIndex(long id){
        return BinarySearchIndex(id);
    }

    public float getLonFromIndex(int index){
        return lons[index];
    }
    public float getLatFromIndex(int index){
        return lats[index];
    }

    private int BinarySearchIndex(long id){
        int low = 0;
        int high = fill - 1;
        while (low <= high){
            int mid = (low + high)/2;
            long midId = ids[mid];
            if(midId < id) {
                low = mid + 1;
            } else if (midId > id) {
                high = mid -1;
            } else {
                return mid;
            }
        }
        return -1;
    }

    private void sort(int left, int right){
        if(left < right){
            int mid = (left+right)/2;

            sort(left, mid);
            sort(mid+1, right);

            merge(left, mid, right);
        }
    }

    private void merge(int left, int mid, int right){
        int n1 = mid - left + 1;
        int n2 = right - mid;

        long[] lIds = new long[n1];
        long[] rIds = new long[n2];
        float[] lLons = new float[n1];
        float[] rLons = new float[n2];
        float[] lLats = new float[n1];
        float[] rLats = new float[n2];

        for(int i = 0; i < n1; i++){
            lIds[i] = ids[left + i];
            lLons[i] = lons[left + i];
            lLats[i] = lats[left + i];
        }
        for(int i = 0; i < n2; i++){
            rIds[i] = ids[mid + 1 + i];
            rLons[i] = lons[mid + 1 + i];
            rLats[i] = lats[mid + 1 + i];
        }

        int i = 0;
        int j = 0;

        int k = left;
        while (i < n1 && j < n2){
            if(lIds[i] <= rIds[j]){
                ids[k] = lIds[i];
                lats[k] = lLats[i];
                lons[k] = lLons[i];
                i++;
            } else {
                ids[k] = rIds[j];
                lats[k] = rLats[j];
                lons[k] = rLons[j];
                j++;
            }
            k++;
        }

        while (i < n1){
            ids[k] = lIds[i];
            lats[k] = lLats[i];
            lons[k] = lLons[i];
            i++;
            k++;
        }

        while (j < n2){
            ids[k] = rIds[j];
            lats[k] = rLats[j];
            lons[k] = rLons[j];
            j++;
            k++;
        }
    }
}
