package bfst.render;

class Timer {
    private long start;
    private long spent;

    public Timer() {
        this.start = 0;
        this.spent = 0;
        this.play();
    }

    public void play() {
        start = System.nanoTime();
    }
    public double check() {
        return (System.nanoTime() - start + spent) / 1e9;
    }
    public void pause() {
        spent += System.nanoTime() - start;
    }
}