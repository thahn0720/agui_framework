package thahn.java.agui.graphics;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class GaussianBlur {
    
    public static BufferedImage changeImageWidth(BufferedImage image, int width, int imgHeight) {
            int height = (int) (width / imgHeight * (double) imgHeight);
            BufferedImage tmp = new BufferedImage(width, height, image.getType());
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(image, 0, 0, tmp.getWidth(), tmp.getHeight(), null);
            g2.dispose();
            return tmp;
    }

    public static GraphicsConfiguration getGraphicsConfiguration() {
            return GraphicsEnvironment.getLocalGraphicsEnvironment().
            getDefaultScreenDevice().getDefaultConfiguration();
    }
    public static BufferedImage createCompatibleImage(int width, int height) {
            return getGraphicsConfiguration().createCompatibleImage(width, height);
    }

    public static ConvolveOp getGaussianBlurFilter(int radius, boolean horizontal) {
        if (radius < 1) {
            throw new IllegalArgumentException("Radius must be >= 1");
        }

        int size = radius * 2 + 1;
        float[] data = new float[size];

        float sigma = radius / 3.0f;
        float twoSigmaSquare = 2.0f * sigma * sigma;


        float sigmaRoot = sigma * 2.5066f;

        float total = 0.0f;

        for (int i = -radius; i <= radius; i++) {
                float distance = i * i;
                int index = i + radius;
                data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
                total += data[index];
        }

        for (int i = 0; i < data.length; i++) {
                data[i] /= total;
        }        

        Kernel kernel = null;
        if (horizontal) {
            kernel = new Kernel(size, 1, data);
        } else {
            kernel = new Kernel(1, size, data);
        }

        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);        
    }
}
