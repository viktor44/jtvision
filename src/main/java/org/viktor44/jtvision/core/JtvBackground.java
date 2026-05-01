package org.viktor44.jtvision.core;

import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;

import org.viktor44.jtvision.views.JtvView;

import lombok.Getter;
import lombok.Setter;

/**
 * The desktop background view, displayed behind all windows on the desktop.
 * <p>
 * {@code TBackground} fills its entire area with a single repeating {@link #pattern}
 * character drawn in palette color 1.  By default the desktop uses
 * {@link JtvDesktop#defaultBkgrnd} (light-shade block {@code ░}, CP437 {@code 0xB0}) as the
 * pattern, giving the classic Turbo Vision appearance.
 * <p>
 * To customise the desktop background, replace the factory passed to the
 * {@link JtvDesktop} constructor or override {@link JtvDesktop#initBackground}.
 * 
 * <h3>Colour palette</h3>
 * 
 * The single-entry palette {@link #cpBackground} maps to:
 * <ol>
 *   <li>Background fill colour.</li>
 * </ol>
 */
public class JtvBackground extends JtvView {

	/**
     * Single-entry colour palette:
     * <ol>
     *   <li>{@code 1} — background fill colour.</li>
     * </ol>
     */
    private static final JtvPalette cpBackground = new JtvPalette(new int[] {1});

    /** The character tiled across the background area. */
    @Getter
    @Setter
    private char pattern;

    /**
     * Constructs a background view covering {@code bounds} and filled with {@code aPattern}.
     *
     * @param bounds   the area to cover (normally the desktop extent)
     * @param pattern the fill character
     */
    public JtvBackground(JtvRect bounds, char pattern) {
        super(bounds);
        this.growMode = gfGrowHiX | gfGrowHiY;
        this.pattern = pattern;
    }

    /** Redraws the entire background by tiling {@link #pattern} across every row. */
    @Override
    public void draw() {
        JtvDrawBuffer b = new JtvDrawBuffer();
        b.moveChar(0, pattern, getColor(1), size.getX());
        writeLine(0, 0, size.getX(), size.getY(), b);
    }

    /**
     * Returns the background's single-entry palette, mapping color index 1 upward through
     * the owner's palette chain.
     *
     * @return the background palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpBackground;
    }

//    /** The character tiled across the background area. */
//    public char getPattern() {
//		return pattern;
//	}
//
//    /** The character tiled across the background area. */
//	public void setPattern(char pattern) {
//		this.pattern = pattern;
//	}
}
