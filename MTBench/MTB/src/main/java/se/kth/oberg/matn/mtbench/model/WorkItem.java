package se.kth.oberg.matn.mtbench.model;

import java.util.concurrent.Callable;

public class WorkItem implements Callable<Double> {
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
    public Double call() throws Exception {
        double sum = 0;
        for (int i = 0; i < loops; i++) {
            sum += work(8973423.2323, exponent);
        }
        return sum;
    }

    private static double work(double d, int depth) {
        if (depth <= 0) {
            return d;
        }
        return work(Math.sin(Math.cos(Math.sqrt(Math.abs(Math.log(Math.abs(Math.log10(Math.abs(Math.toRadians(Math.toDegrees(d + 0.1)))))))))), depth - 1)
                + work(Math.sin(Math.cos(Math.sqrt(Math.abs(Math.log(Math.abs(Math.log10(Math.abs(Math.toRadians(Math.toDegrees(d - 0.1)))))))))), depth - 1);
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
