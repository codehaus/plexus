package org.codehaus.plexus.logging.console;

/**
 * TODO: Document Me
 *
 * @author Andrew Williams <andy@handyande.co.uk>
 * @since 22-Oct-2006
 */
public class ANSIColoredString extends AbstractColoredString
{
    public static final int ANSI_RESET = 0;

    public static final int COLOR_BLACK = 30;
    public static final int COLOR_RED = 31;
    public static final int COLOR_GREEN = 32;
    public static final int COLOR_YELLOW = 33;
    public static final int COLOR_BLUE = 34;
    public static final int COLOR_MAGENTA = 35;
    public static final int COLOR_CYAN = 36;
    public static final int COLOR_WHITE = 37;
    public static final int COLOR_DEFAULT = 39;

    public static final int BACKGROUND_BLACK = 40;
    public static final int BACKGROUND_RED = 41;
    public static final int BACKGROUND_GREEN = 42;
    public static final int BACKGROUND_YELLOW = 43;
    public static final int BACKGROUND_BLUE = 44;
    public static final int BACKGROUND_MAGENTA = 45;
    public static final int BACKGROUND_CYAN = 46;
    public static final int BACKGROUND_WHITE = 47;
    public static final int BACKGROUND_DEFAULT = 49;

    public static final char ESCAPE = '\u001b';

    public String convertColorStart(int color)
    {
        return ESCAPE + "[" + color + "m";
    }

    public String convertColorEnd(int color)
    {
        return ESCAPE + "[" + COLOR_DEFAULT + "m";
    }
}
