package dk.trustworks.invoicewebui.model;

import java.util.Random;

public class TrustworksColor {

    private int r;
    private int g;
    private int b;

    private static int[][] colors = {{251, 177, 77}, {0, 113, 99}, {143, 167, 138}, {252, 245, 133}, {141, 172, 191}, {5, 79, 138}, {44, 88, 109}, {4, 46, 77}};

    private static int colorCount = 0;

    private TrustworksColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
    public static TrustworksColor getRandomColor() {
        int i = new Random().nextInt(7);
        return new TrustworksColor(colors[i][0], colors[i][1], colors[i][2]);
    }

    public static TrustworksColor getNextColor() {
        if(colorCount == 7) colorCount = 0;
        else colorCount++;
        return new TrustworksColor(colors[colorCount][0], colors[colorCount][1], colors[colorCount][2]);
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }
}
