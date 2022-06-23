package zserio.runtime.json;

import java.io.PrintWriter;

/**
 * Converts zserio values to Json string representation.
 */
class JsonEncoder
{
    /**
     * Encodes JSON null value to the output stream using the given writer.
     *
     * @param out Writer to use.
     */
    public static void encodeNull(PrintWriter out)
    {
        out.write("null");
    }

    /**
     * Encodes JSON boolean value to the output stream using the given writer.
     *
     * @param out Writer to use.
     * @param value Value to encode.
     */
    public static void encodeBool(PrintWriter out, boolean value)
    {
        out.write(value ? "true" : "false");
    }

    /**
     * Encodes JSON integral value to the output stream using the given writer.
     *
     * @param out Writer to use.
     * @param value Value to encode.
     */
    public static void encodeIntegral(PrintWriter out, Number value)
    {
        out.print(value);
    }

    /**
     * Encodes JSON floating-point value to the output stream using the given writer.
     *
     * @param out Writer to use.
     * @param value Value to encode.
     */
    public static void encodeFloatingPoint(PrintWriter out, double value)
    {
        out.print(value);
    }

    /**
     * Encodes JSON string value to the output stream using the given writer.
     *
     * Note that this method performs escaping necessary to get a proper JSON string.
     *
     * @param out Writer to use.
     * @param value Value to encode.
     */
    public static void encodeString(PrintWriter out, String value)
    {
        out.write('"');
        for (char ch : value.toCharArray())
        {
            switch (ch)
            {
            case '\\':
            case '"':
                out.write('\\');
                out.write(ch);
                break;
            case '\b':
                out.write('\\');
                out.write('b');
                break;
            case '\f':
                out.write('\\');
                out.write('f');
                break;
            case '\n':
                out.write('\\');
                out.write('n');
                break;
            case '\r':
                out.write('\\');
                out.write('r');
                break;
            case '\t':
                out.write('\\');
                out.write('t');
                break;
            default:
                if (ch <= 0x1F)
                {
                    out.write('\\');
                    out.write('u');
                    out.write('0');
                    out.write('0');
                    out.write(Integer.toHexString((ch >> 4) & 0xf));
                    out.write(Integer.toHexString(ch & 0xf));
                }
                else
                {
                    out.write(ch);
                }
                break;
            }
        }
        out.write('"');
    }
}
