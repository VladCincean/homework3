import domain.Polynomial;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Main {
    public static Polynomial sequentialRegular(Polynomial a, Polynomial b) {
        int[] result = new int[a.getDegree() + b.getDegree() + 1];

        for (int i = 0; i < result.length; i++) {
            result[i] = 0;
        }

        for (int i = 0; i <= a.getDegree(); i++) {
            for (int j = 0; j <= a.getDegree(); j++) {
                result[i + j] += a.getCoefficients()[i] * b.getCoefficients()[j];
            }
        }

//        IntStream.range(0, a.getCoefficients().length)
//                .forEach(i -> {
//                    IntStream.range(0, b.getCoefficients().length)
//                            .forEach(j -> {
//                                result[i + j] += a.getCoefficients()[i] + b.getCoefficients()[j];
//                            });
//                });

        return new Polynomial(result);
    }

    public static Polynomial parallelRegular(Polynomial a, Polynomial b) {
        int[] result = new int[a.getDegree() + b.getDegree() + 1];

        for (int i = 0; i < result.length; i++) {
            result[i] = 0;
        }

        IntStream.range(0, a.getCoefficients().length)
                .parallel()
                .forEach(i -> {
                    IntStream.range(0, b.getCoefficients().length)
                            .parallel()
                            .forEach(j -> {
                                result[i + j] += a.getCoefficients()[i] + b.getCoefficients()[j];
                            });
                });

        return new Polynomial(result);
    }

    public static Polynomial sequentialKaratsuba(Polynomial p, Polynomial q) {
        if (p.getDegree() != q.getDegree()) {
            return null;
        }

        if (p.getDegree() < 3) {
            return sequentialRegular(p, q);
        }

        int mid = Math.max(p.getDegree(), q.getDegree()) / 2;

        Polynomial p1 = new Polynomial(Arrays.copyOfRange(p.getCoefficients(), 0, mid));
        Polynomial p2 = new Polynomial(Arrays.copyOfRange(p.getCoefficients(), mid, p.getCoefficients().length));
        Polynomial q1 = new Polynomial(Arrays.copyOfRange(q.getCoefficients(), 0, mid));
        Polynomial q2 = new Polynomial(Arrays.copyOfRange(q.getCoefficients(), mid, q.getCoefficients().length));

        Polynomial p1q1 = sequentialKaratsuba(p1, q1);
        Polynomial p1p2q1q2 = sequentialRegular(Polynomial.add(p1, p2), Polynomial.add(q1, q2));
        Polynomial p2q2 = sequentialRegular(p2, q2);

        Polynomial r1 = Polynomial.shift(p2q2, 4);
        Polynomial r2 = Polynomial.subtract(Polynomial.subtract(p1p2q1q2, p2q2), p1q1);
        Polynomial r3 = Polynomial.shift(r2, 2);
        Polynomial r4 = Polynomial.add(Polynomial.add(r1, r3), p1q1);

        return r4;
    }

    public static Polynomial parallelKaratsuba(Polynomial p, Polynomial q) throws ExecutionException, InterruptedException {
        if (p.getDegree() < 3) {
            return sequentialRegular(p, q);
        }

        int mid = Math.max(p.getDegree(), q.getDegree()) / 2;

        Polynomial p1 = new Polynomial(Arrays.copyOfRange(p.getCoefficients(), 0, mid));
        Polynomial p2 = new Polynomial(Arrays.copyOfRange(p.getCoefficients(), mid, p.getCoefficients().length));
        Polynomial q1 = new Polynomial(Arrays.copyOfRange(q.getCoefficients(), 0, mid));
        Polynomial q2 = new Polynomial(Arrays.copyOfRange(q.getCoefficients(), mid, q.getCoefficients().length));

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        Callable<Polynomial> tP1Q1 = () -> parallelKaratsuba(p1, q1);
        Callable<Polynomial> tP1P2Q1Q2 = () -> parallelKaratsuba(
                Polynomial.add(p1, p2),
                Polynomial.add(q1, q2)
        );
        Callable<Polynomial> tP2Q2 = () -> parallelKaratsuba(p2, q2);

        Future<Polynomial> fP1Q1 = executor.submit(tP1Q1);
        Future<Polynomial> fP1P2Q1Q2 = executor.submit(tP1P2Q1Q2);
        Future<Polynomial> fP2Q2 = executor.submit(tP2Q2);

        executor.shutdown();

        Polynomial p1q1 = fP1Q1.get();
        Polynomial p1p2q1q2 = fP1P2Q1Q2.get();
        Polynomial p2q2 = fP2Q2.get();

        Polynomial r1 = Polynomial.shift(p2q2, 4);
        Polynomial r2 = Polynomial.subtract(Polynomial.subtract(p1p2q1q2, p2q2), p1q1);
        Polynomial r3 = Polynomial.shift(r2, 2);
        Polynomial r4 = Polynomial.add(Polynomial.add(r1, r3), p1q1);

        return r4;
    }

    public static void main(String[] args) {
        Polynomial p = Polynomial.generateOne(5);
        Polynomial q = Polynomial.generateOne(5);
        Polynomial r = null;

        long startTime = 0;
        long endTime = 0;
        double total = 0;

        startTime = System.nanoTime();

        try {
//            r = sequentialRegular(p, q);
//            r = parallelRegular(p, q);
//            r = sequentialKaratsuba(p, q);
            r = parallelKaratsuba(p, q);
        } catch (Exception e) {
            e.printStackTrace();
        }

        endTime = System.nanoTime();
        total = (endTime - startTime) / 1000000000.0;
        System.out.println("Time: " + total);

        System.out.println(p);
        System.out.println(q);
        System.out.println(r);
    }
}
