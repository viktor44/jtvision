package org.viktor44.jtvision.dialogs;

import org.viktor44.jtvision.core.JtvRect;

/**
 * A {@link JtvStaticText} variant that supports runtime parameter substitution.
 * <p>
 * TParamText allows a static text view to display text with substituted parameters,
 * similar to {@link String#format}. The text string is treated as a format template
 * and the parameters supplied via {@link #setParams(Object...)} are merged into it
 * when the view is drawn.
 * <p>
 * Because users never change the parameters interactively, {@link #dataSize()} returns
 * 0 and data transfer is not supported.
 */
public class JtvParamText extends JtvStaticText {

    /** The current format parameters to substitute into {@link JtvStaticText#text}. */
    private Object[] params;

    /**
     * Creates a parameterized text view with the given bounds and format template.
     *
     * @param bounds the bounding rectangle
     * @param aText  the format string (Java {@link String#format} syntax)
     */
    public JtvParamText(JtvRect bounds, String aText) {
        super(bounds, aText);
    }

    /**
     * Returns the formatted text by substituting the current parameters into the
     * format string. Returns the raw format string if no parameters have been set or
     * if formatting fails.
     *
     * @return the formatted display text; never {@code null}
     */
    @Override
    public String getText() {
        if (text == null) {
        	return "";
        }
        if (params == null || params.length == 0) {
        	return text;
        }
        try {
            return String.format(text, params);
        }
        catch (Exception e) {
            return text;
        }
    }

    /**
     * Sets the parameters to substitute into the format string and redraws the view.
     *
     * @param params the parameter values; must match the format specifiers in the text
     */
    public void setParams(Object... params) {
        this.params = params;
        drawView();
    }

    /**
     * Returns 0 because parameterised text views do not participate in data transfer.
     *
     * @return 0
     */
    @Override
    public int dataSize() {
        return 0;
    }
}
