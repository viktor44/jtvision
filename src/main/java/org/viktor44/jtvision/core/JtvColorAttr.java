package org.viktor44.jtvision.core;

/**
 * Encodes a classic CGA/BIOS 8-bit terminal color attribute.
 *
 * <p>The byte is split into two nibbles:
 * <ul>
 *   <li>Bits 3–0 (low nibble): foreground color index (0–15)</li>
 *   <li>Bits 7–4 (high nibble): background color index (0–7; bit 7 may enable blinking)</li>
 * </ul>
 * The 16 standard color constants defined here correspond to the IBM PC BIOS color palette.
 * Use {@link #TColorAttr(int, int)} to build an attribute from separate foreground and
 * background values, or pass the raw packed byte directly to {@link #TColorAttr(int)}.
 */
public class JtvColorAttr {
    /** Black (0x00). */
    public static final int BLACK = 0x00;
    /** Blue (0x01). */
    public static final int BLUE = 0x01;
    /** Green (0x02). */
    public static final int GREEN = 0x02;
    /** Cyan (0x03). */
    public static final int CYAN = 0x03;
    /** Red (0x04). */
    public static final int RED = 0x04;
    /** Magenta (0x05). */
    public static final int MAGENTA = 0x05;
    /** Brown / dark yellow (0x06). */
    public static final int BROWN = 0x06;
    /** Light gray (0x07). */
    public static final int LIGHT_GRAY = 0x07;
    /** Dark gray / bright black (0x08). */
    public static final int DARK_GRAY = 0x08;
    /** Light blue (0x09). */
    public static final int LIGHT_BLUE = 0x09;
    /** Light green (0x0A). */
    public static final int LIGHT_GREEN = 0x0A;
    /** Light cyan (0x0B). */
    public static final int LIGHT_CYAN = 0x0B;
    /** Light red / bright red (0x0C). */
    public static final int LIGHT_RED = 0x0C;
    /** Light magenta / bright magenta (0x0D). */
    public static final int LIGHT_MAGENTA = 0x0D;
    /** Yellow / bright brown (0x0E). */
    public static final int YELLOW = 0x0E;
    /** White / bright gray (0x0F). */
    public static final int WHITE = 0x0F;

    private final int value;

    /** Constructs the default attribute: light-gray foreground on black background ({@code 0x07}). */
    public JtvColorAttr() {
        this.value = 0x07;
    }

    /**
     * Constructs a color attribute from a packed 8-bit value.
     *
     * @param value raw attribute byte (high nibble = background, low nibble = foreground)
     */
    public JtvColorAttr(int value) {
        this.value = value & 0xFF;
    }

    /**
     * Constructs a color attribute from separate foreground and background color indices.
     *
     * @param fg foreground color index (0–15, see color constants)
     * @param bg background color index (0–7)
     */
    public JtvColorAttr(int fg, int bg) {
        this.value = ((bg & 0x0F) << 4) | (fg & 0x0F);
    }

    /**
     * Returns the foreground color index (bits 3–0 of the attribute byte).
     *
     * @return foreground color, 0–15
     */
    public int getForeground() {
        return value & 0x0F;
    }

    /**
     * Returns the background color index (bits 7–4 of the attribute byte).
     *
     * @return background color, 0–7 (bit 3 may be the blink flag)
     */
    public int getBackground() {
        return (value >> 4) & 0x0F;
    }

    /**
     * Returns the raw 8-bit attribute value.
     *
     * @return packed color byte
     */
    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof JtvColorAttr)) return false;
        return value == ((JtvColorAttr) obj).value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
