package ctu.tools.xml;

import java.lang.reflect.Method;

public class EscapeStrategy {
    private int bits;
    Object encoder;
    Method canEncode;

    public EscapeStrategy(String encoding) {
        if ("UTF-8".equalsIgnoreCase(encoding) ||
                "UTF-16".equalsIgnoreCase(encoding)) {
            bits = 16;
        }
        else if ("ISO-8859-1".equalsIgnoreCase(encoding) ||
                "Latin1".equalsIgnoreCase(encoding)) {
            bits = 8;
        }
        else if ("US-ASCII".equalsIgnoreCase(encoding) ||
                "ASCII".equalsIgnoreCase(encoding)) {
            bits = 7;
        }
        else {
            bits = 0;
            //encoder = Charset.forName(encoding).newEncoder();
            try {
                Class charsetClass = Class.forName("java.nio.charset.Charset");
                Class encoderClass = Class.forName("java.nio.charset.CharsetEncoder");
                Method forName = charsetClass.getMethod("forName", new Class[]{String.class});
                Object charsetObj = forName.invoke(null, new Object[]{encoding});
                Method newEncoder = charsetClass.getMethod("newEncoder", null);
                encoder = newEncoder.invoke(charsetObj, null);
                canEncode = encoderClass.getMethod("canEncode", new Class[]{char.class});
            }
            catch (Exception ignored) {
            }
        }
    }

    public boolean shouldEscape(char ch) {
        if (bits == 16) {
            return false;
        }
        if (bits == 8) {
            if ((int) ch > 255)
                return true;
            else
                return false;
        }
        if (bits == 7) {
            if ((int) ch > 127)
                return true;
            else
                return false;
        }
        else {
            if (canEncode != null && encoder != null) {
                try {
                    Boolean val = (Boolean) canEncode.invoke(encoder, new Object[]{new Character(ch)});
                    return !val.booleanValue();
                }
                catch (Exception ignored) {
                }
            }
            // Return false if we don't know.  This risks not escaping
            // things which should be escaped, but also means people won't
            // start getting loads of unnecessary escapes.
            return false;
        }
    }
}

