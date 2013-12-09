package se.kth.oberg.matn.mtbench.model;

public class WorkItem implements Runnable {
    private int exponent;
    private int loops;

    private WorkItem(int loops, int exponent) {
        this.exponent = exponent;
        this.loops = loops;
    }

    public int getLoops() {
        return loops;
    }

    @Override
    public void run() {
        for (int i = 0; i < loops; i++) {
            work(8973423.2323 + loops, exponent);
        }
    }

    private static double work(double d, int depth) {
        if (depth <= 0) {
            return d;
        }
        return work(Math.sin(Math.cos(Math.sqrt(Math.log(Math.log10(Math.toRadians(Math.toDegrees(d + 0.1))))))), depth - 1)
                + work(Math.sin(Math.cos(Math.sqrt(Math.log(Math.log10(Math.toRadians(Math.toDegrees(d - 0.1))))))), depth - 1);
    }

    public static Builder createBuilder(int exponent) {
        return new Builder(exponent);
    }

    public static class Builder {
        private int exponent;

        private Builder(int exponent) {
            this.exponent = exponent;
        }

        public WorkItem build(int loops) {
            return new WorkItem(loops, exponent);
        }
    }
}
