package domain;

import java.util.Random;

public class Polynomial {
    private static Random random = new Random();

    private int degree;
    private int[] coefficients;

    public Polynomial(int[] coefficients) {
        if ((null == coefficients) || (coefficients.length < 1)) {
            throw new NullPointerException("null: coefficients");
        }

        this.degree = coefficients.length - 1;
        this.coefficients = coefficients.clone();
    }

    public static Polynomial generateOne(int degree) {
        if (degree < 0) {
            return null;
        }

        int[] coefficients = new int[degree + 1];

        for (int i = 0; i <= degree; i++) {
            coefficients[i] = random.nextInt(10); // intre 0 si 10
        }

        return new Polynomial(coefficients);
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int[] getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(int[] coefficients) {
        this.coefficients = coefficients;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= this.degree; i++) {
            int c = this.coefficients[i];
            if (c < 0) {
                c *= 1;
            } else if (c > 0) {
                sb.append("+");
            } else {
                continue;
            }

            sb.append(String.format("%dX^%d ", c, i));
        }

        return sb.toString();
    }

    public static Polynomial shift(Polynomial p, int offset) {
        int[] result = new int[offset + p.getDegree() + 1];

        for (int i = 0; i < offset; i++) {
            result[i] = 0;
        }

        for (int i = 0; i <= p.getDegree(); i++) {
            result[i + offset] = p.getCoefficients()[i];
        }

        return new Polynomial(result);
    }

    public static Polynomial add(Polynomial a, Polynomial b) {
        int[] result = new int[Math.max(a.getDegree(), b.getDegree()) + 1];

        int minDegree = Math.min(a.getDegree(), b.getDegree());

        for (int i = 0; i <= minDegree; i++) {
            result[i] = a.getCoefficients()[i] + b.getCoefficients()[i];
        }

        if (minDegree != result.length - 1) {
            if (a.getDegree() == minDegree) {
                for (int i = minDegree + 1; i <= b.getDegree(); i++) {
                    result[i] = b.getCoefficients()[i];
                }
            } else if (b.getDegree() == minDegree) {
                for (int i = minDegree + 1; i <= a.getDegree(); i++) {
                    result[i] = a.getCoefficients()[i];
                }
            }
        }

        return new Polynomial(result);
    }

    public static Polynomial subtract(Polynomial a, Polynomial b) {
        int[] minusB = b.getCoefficients().clone();

        for (int i = 0; i < minusB.length; i++) {
            minusB[i] *= -1;
        }

        Polynomial bb = new Polynomial(minusB);
        return add(a, bb);
    }
}
