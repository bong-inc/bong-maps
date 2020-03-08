package bfst.render;

/**
 * Render
 */
public class Render {
    private int MAX_FPS = 60;
    private double FRAME_PERIOD = 1.0 / MAX_FPS;

    private Timer timer;
    private double lastRender;
    private Renderable renderable;

    public Render(Renderable renderable) {
        this.renderable = renderable;
        this.timer = new Timer();
        this.lastRender = this.timer.check();
    }

    /**
     * This function will render if it's time to render.
     * This is the GOTO method.
     */
    public void requestRender() {
        if (this.shouldRender()) {
            this.render();
        }
    }

    public void render() {
        // call render process here
        this.renderable.render();
        this.lastRender = this.timer.check();
    }
    
    public boolean shouldRender() {
        double delta = this.timer.check() - this.lastRender;

        // System.out.println(String.format("delta: %s | frame: %s", delta, this.FRAME_PERIOD));
        if (delta >= this.FRAME_PERIOD) {
            return true;
        }
        
        return false;
    }
}