package org.viktor44.jtvision.examples.avscolor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Terminal color quantization demo.
 *
 * A Java port of the avscolor example: applies the terminal color quantizations
 * used by Turbo Vision to an RGB image file. Supports four modes:
 *   indexed8   - 8-color palette, stippled for bold rows
 *   indexed16  - 16-color palette
 *   indexed256 - xterm 256-color palette
 *   direct     - passthrough
 *
 * Usage: java TermColor &lt;input.png&gt; &lt;output.png&gt; [indexed8|indexed16|indexed256|direct]
 */
public final class TermColor {

    private static final int[] XTERM16_TO_RGB = {
        0x000000, 0x800000, 0x008000, 0x808000,
        0x000080, 0x800080, 0x008080, 0xC0C0C0,
        0x808080, 0xFF0000, 0x00FF00, 0xFFFF00,
        0x0000FF, 0xFF00FF, 0x00FFFF, 0xFFFFFF,
    };

    private TermColor() {}

    public static void main(String[] args) throws IOException {
        if (args.length < 2 || args.length > 3) {
            System.err.println("Usage: TermColor <input> <output> [indexed8|indexed16|indexed256|direct]");
            System.exit(1);
        }
        String mode = args.length == 3 ? args[2] : "indexed16";
        BufferedImage src = ImageIO.read(new File(args[0]));
        if (src == null)
            throw new IOException("Cannot read image: " + args[0]);

        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y) & 0xFFFFFF;
                int out;
                switch (mode) {
                    case "indexed8":   out = quantizeIndexed8(rgb, x, y); break;
                    case "indexed16":  out = quantizeIndexed16(rgb); break;
                    case "indexed256": out = quantizeIndexed256(rgb); break;
                    case "direct":     out = rgb; break;
                    default:
                        System.err.println("Unknown mode: " + mode);
                        System.exit(1);
                        return;
                }
                dst.setRGB(x, y, out);
            }
        }
        String fmt = args[1].toLowerCase().endsWith(".jpg") || args[1].toLowerCase().endsWith(".jpeg") ? "jpg" : "png";
        ImageIO.write(dst, fmt, new File(args[1]));
    }

    private static int quantizeIndexed8(int rgb, int x, int y) {
        int idx = rgbToXTerm16(rgb);
        if (idx >= 8 && (y % 2) != 0)
            idx -= 8;
        return XTERM16_TO_RGB[idx];
    }

    private static int quantizeIndexed16(int rgb) {
        return XTERM16_TO_RGB[rgbToXTerm16(rgb)];
    }

    private static int quantizeIndexed256(int rgb) {
        return xterm256ToRGB(rgbToXTerm256(rgb));
    }

    /**
     * Find the closest xterm 16-color palette index for an RGB value.
     * Uses Euclidean distance in RGB space.
     */
    public static int rgbToXTerm16(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int best = 0;
        long bestDist = Long.MAX_VALUE;
        for (int i = 0; i < 16; i++) {
            int pr = (XTERM16_TO_RGB[i] >> 16) & 0xFF;
            int pg = (XTERM16_TO_RGB[i] >> 8) & 0xFF;
            int pb = XTERM16_TO_RGB[i] & 0xFF;
            long d = sq(r - pr) + sq(g - pg) + sq(b - pb);
            if (d < bestDist) {
                bestDist = d;
                best = i;
            }
        }
        return best;
    }

    /**
     * Find the closest xterm 256-color palette index for an RGB value.
     * Indices 0..15 reuse the 16-color palette. Indices 16..231 form the
     * 6x6x6 RGB cube. Indices 232..255 form a 24-step grayscale ramp.
     */
    public static int rgbToXTerm256(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        // Nearest cube color (levels: 0, 95, 135, 175, 215, 255)
        int cr = nearestCubeLevel(r);
        int cg = nearestCubeLevel(g);
        int cb = nearestCubeLevel(b);
        int cubeIdx = 16 + 36 * cr + 6 * cg + cb;
        int cubeRgb = xterm256ToRGB(cubeIdx);
        long cubeDist = colorDist(rgb, cubeRgb);

        // Nearest grayscale
        int avg = (r + g + b) / 3;
        int grayIdx;
        int grayRgb;
        if (avg < 8) {
            grayIdx = 16;
            grayRgb = 0;
        } else if (avg >= 238) {
            grayIdx = 231;
            grayRgb = 0xFFFFFF;
        } else {
            int step = (avg - 8 + 5) / 10;
            if (step > 23) step = 23;
            grayIdx = 232 + step;
            int level = 8 + step * 10;
            grayRgb = (level << 16) | (level << 8) | level;
        }
        long grayDist = colorDist(rgb, grayRgb);

        return grayDist < cubeDist ? grayIdx : cubeIdx;
    }

    public static int xterm256ToRGB(int idx) {
        if (idx < 16)
            return XTERM16_TO_RGB[idx];
        if (idx < 232) {
            int n = idx - 16;
            int r = cubeLevelValue(n / 36);
            int g = cubeLevelValue((n / 6) % 6);
            int b = cubeLevelValue(n % 6);
            return (r << 16) | (g << 8) | b;
        }
        int level = 8 + (idx - 232) * 10;
        return (level << 16) | (level << 8) | level;
    }

    private static int cubeLevelValue(int level) {
        return level == 0 ? 0 : 55 + 40 * level;
    }

    private static int nearestCubeLevel(int c) {
        // cube levels: 0, 95, 135, 175, 215, 255
        int[] levels = {0, 95, 135, 175, 215, 255};
        int best = 0;
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < levels.length; i++) {
            int d = Math.abs(c - levels[i]);
            if (d < bestDist) {
                bestDist = d;
                best = i;
            }
        }
        return best;
    }

    private static long colorDist(int a, int b) {
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        return sq(ar - br) + sq(ag - bg) + sq(ab - bb);
    }

    private static long sq(int x) {
        return (long) x * x;
    }
}
