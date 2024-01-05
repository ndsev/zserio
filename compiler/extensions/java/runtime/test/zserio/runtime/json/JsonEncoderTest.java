package zserio.runtime.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class JsonEncoderTest
{
    @Test
    public void encodeNull()
    {
        final StringWriter stringWriter = new StringWriter();
        try (final PrintWriter printWriter = new PrintWriter(stringWriter))
        {
            JsonEncoder.encodeNull(printWriter);
            assertEquals("null", stringWriter.toString());
        }
    }

    @Test
    public void encodeBoolean()
    {
        final StringWriter stringWriter = new StringWriter();
        try (final PrintWriter printWriter = new PrintWriter(stringWriter))
        {
            JsonEncoder.encodeBool(printWriter, true);
            assertEquals("true", stringWriter.toString());

            stringWriter.getBuffer().setLength(0);

            JsonEncoder.encodeBool(printWriter, false);
            assertEquals("false", stringWriter.toString());
        }
    }

    @Test
    public void encodeIntegral()
    {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter))
        {
            JsonEncoder.encodeIntegral(printWriter, 0xff);
            assertEquals(Integer.toString(0xff), stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, 0xffff);
            assertEquals(Integer.toString(0xffff), stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, 0xffffffff);
            assertEquals(Long.toString(0xffffffff), stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            final BigInteger uint64Max = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
            JsonEncoder.encodeIntegral(printWriter, uint64Max);
            assertEquals(uint64Max.toString(), stringWriter.toString());

            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, Byte.MIN_VALUE);
            assertEquals(Byte.toString(Byte.MIN_VALUE), stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, Byte.MAX_VALUE);
            assertEquals(Byte.toString(Byte.MAX_VALUE), stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, Short.MIN_VALUE);
            assertEquals(Short.toString(Short.MIN_VALUE), stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, Short.MAX_VALUE);
            assertEquals(Short.toString(Short.MAX_VALUE), stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, Integer.MIN_VALUE);
            assertEquals(Integer.toString(Integer.MIN_VALUE), stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, Integer.MAX_VALUE);
            assertEquals(Integer.toString(Integer.MAX_VALUE), stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, Long.MIN_VALUE);
            assertEquals(Long.toString(Long.MIN_VALUE), stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, Long.MAX_VALUE);
            assertEquals(Long.toString(Long.MAX_VALUE), stringWriter.toString());

            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, Long.MIN_VALUE);
            assertEquals("-9223372036854775808", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, -1000);
            assertEquals("-1000", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, 0);
            assertEquals("0", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, 1000);
            assertEquals("1000", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeIntegral(printWriter, uint64Max);
            assertEquals("18446744073709551615", stringWriter.toString());
        }
    }

    @Test
    public void encodeFloatingPoint()
    {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter))
        {
            JsonEncoder.encodeFloatingPoint(printWriter, -1.0);
            assertEquals("-1.0", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeFloatingPoint(printWriter, 0.0);
            assertEquals("0.0", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeFloatingPoint(printWriter, 1.0);
            assertEquals("1.0", stringWriter.toString());

            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeFloatingPoint(printWriter, 3.5);
            assertEquals("3.5", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeFloatingPoint(printWriter, 9.875);
            assertEquals("9.875", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeFloatingPoint(printWriter, 0.6171875);
            assertEquals("0.6171875", stringWriter.toString());

            // TODO[Mi-L@]: Note that scientific format is different from C++ and Python.
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeFloatingPoint(printWriter, 1e20);
            assertEquals("1.0E20", stringWriter.toString());

            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeFloatingPoint(printWriter, Double.NaN);
            assertEquals("NaN", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeFloatingPoint(printWriter, Float.NaN);
            assertEquals("NaN", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeFloatingPoint(printWriter, Double.POSITIVE_INFINITY);
            assertEquals("Infinity", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeFloatingPoint(printWriter, Float.POSITIVE_INFINITY);
            assertEquals("Infinity", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeFloatingPoint(printWriter, Double.NEGATIVE_INFINITY);
            assertEquals("-Infinity", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeFloatingPoint(printWriter, Float.NEGATIVE_INFINITY);
            assertEquals("-Infinity", stringWriter.toString());
        }
    }

    @Test
    public void encodeString()
    {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter);)
        {
            JsonEncoder.encodeString(printWriter, "");
            assertEquals("\"\"", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeString(printWriter, "test");
            assertEquals("\"test\"", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeString(printWriter, "München");
            assertEquals("\"München\"", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeString(printWriter, "€");
            assertEquals("\"€\"", stringWriter.toString());

            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeString(printWriter, "\\");
            assertEquals("\"\\\\\"", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeString(printWriter, "\"");
            assertEquals("\"\\\"\"", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeString(printWriter, "\b");
            assertEquals("\"\\b\"", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeString(printWriter, "\f");
            assertEquals("\"\\f\"", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeString(printWriter, "\n");
            assertEquals("\"\\n\"", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeString(printWriter, "\r");
            assertEquals("\"\\r\"", stringWriter.toString());
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeString(printWriter, "\t");
            assertEquals("\"\\t\"", stringWriter.toString());

            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeString(
                    printWriter, "\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2");
            assertEquals("\"\\n\\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\\\\\"'Hello World2\"",
                    stringWriter.toString());

            // <= 0x1F -> unicode escape
            stringWriter.getBuffer().setLength(0);
            JsonEncoder.encodeString(printWriter, new String(new byte[] {0x1F}, StandardCharsets.UTF_8));
            assertEquals("\"\\u001f\"", stringWriter.toString());
        }
    }
}
