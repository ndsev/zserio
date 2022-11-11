package zserio.runtime.json;

import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;

import zserio.runtime.ZserioBitmask;
import zserio.runtime.ZserioEnum;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.typeinfo.ItemInfo;
import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.typeinfo.TypeInfo;
import zserio.runtime.walker.WalkObserver;
import zserio.runtime.walker.WalkerConst;

/**
 * Walker observer which dumps zserio objects to JSON format.
 */
public class JsonWriter implements WalkObserver, AutoCloseable
{
    /**
     * Constructor.
     *
     * @param out Writer to use for writing.
     */
    public JsonWriter(Writer out)
    {
        this(out, null);
    }

    /**
     * Constructor.
     *
     * @param out Writer to use for writing.
     * @param indent Indent as a number of ' ' to be used for indentation.
     */
    public JsonWriter(Writer out, int indent)
    {
        this(out, new String(new char[indent]).replace('\0', ' '));
    }

    /**
     * Constructor.
     *
     * @param out Writer to use for writing.
     * @param indent Indent as a string to be used for indentation.
     */
    public JsonWriter(Writer out, String indent)
    {
        this.out = new PrintWriter(out);
        this.indent = indent;
        this.itemSeparator = indent == null ? DEFAULT_ITEM_SEPARATOR : DEFAULT_ITEM_SEPARATOR_WITH_INDENT;
    }

    /**
     * Sets custom item separator.
     *
     * Use with caution since setting of a wrong separator can lead to invalid JSON output.
     *
     * @param itemSeparator Item separator to set.
     */
    public void setItemSeparator(String itemSeparator)
    {
        this.itemSeparator = itemSeparator;
    }

    /**
     * Sets custom key separator.
     *
     * Use with caution since setting of a wrong separator can lead to invalid JSON output.
     *
     * @param keySeparator Key separator to set.
     */
    public void setKeySeparator(String keySeparator)
    {
        this.keySeparator = keySeparator;
    }

    /**
     * Configuration for writing of enumerable types.
     */
    public enum EnumerableFormat
    {
        /** Print as JSON integral value. */
        NUMBER,
        /**
         * Print as JSON string according to the following rules:
         *
         * <ol>
         * <li> Enums
         *   <ul>
         *   <li> when an exact match with an enumerable item is found, the item name is used - e.g.
         *        <code>"FIRST"</code>,
         *   <li> when no exact match is found, it's an invalid value, the integral value is converted to string
         *        and an appropriate comment is included - e.g.
         *        <code>"10 /<span>*</span> no match <span>*</span>/"</code>.
         *   </ul>
         * <li> Bitmasks
         *   <ul>
         *   <li> when an exact mach with or-ed bitmask values is found, it's used - e.g.
         *        <code>"READ | WRITE"</code>,
         *   <li> when no exact match is found, but some or-ed values match, the integral value is converted
         *        to string and the or-ed values are included in a comment - e.g.
         *        <code>"127 /<span>*</span> READ | CREATE <span>*</span>/"</code>,
         *   <li> when no match is found at all, the integral value is converted to string and an appropriate
         *        comment is included - e.g. <code>"13 /<span>*</span> no match <span>*</span>/"</code>.
         *   </ul>
         * </ol>
         */
        STRING
    };

    /**
     * Sets preferred formatting for enumerable types.
     *
     * @param enumerableFormat Enumerable format to use.
     */
    public void setEnumerableFormat(EnumerableFormat enumerableFormat)
    {
        this.enumerableFormat = enumerableFormat;
    }

    @Override
    public void close()
    {
        out.close();
    }

    @Override
    public void beginRoot(Object compound)
    {
        beginObject();
    }

    @Override
    public void endRoot(Object compound)
    {
        endObject();
        flush();
    }

    @Override
    public void beginArray(Object array, FieldInfo fieldInfo)
    {
        beginItem();

        writeKey(fieldInfo.getSchemaName());

        beginArray();
    }

    @Override
    public void endArray(Object array, FieldInfo fieldInfo)
    {
        endArray();

        endItem();
    }

    @Override
    public void beginCompound(Object compound, FieldInfo fieldInfo, int elementIndex)
    {
        beginItem();

        if (elementIndex == WalkerConst.NOT_ELEMENT)
            writeKey(fieldInfo.getSchemaName());

        beginObject();
    }

    @Override
    public void endCompound(Object compound, FieldInfo fieldInfo, int elementIndex)
    {
        endObject();

        endItem();
    }

    @Override
    public void visitValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        beginItem();

        if (elementIndex == WalkerConst.NOT_ELEMENT)
            writeKey(fieldInfo.getSchemaName());

        writeValue(value, fieldInfo);

        endItem();
    }

    private void beginItem()
    {
        if (!isFirst)
            out.write(itemSeparator);

        if (indent != null)
            out.write(System.lineSeparator());

        writeIndent();
    }

    private void endItem()
    {
        isFirst = false;
    }

    private void beginObject()
    {
        out.write('{');

        isFirst = true;
        level += 1;
    }

    private void endObject()
    {
        if (indent != null)
            out.write(System.lineSeparator());

        level -= 1;

        writeIndent();

        out.write('}');
    }

    private void beginArray()
    {
        out.write('[');

        isFirst = true;
        level += 1;
    }

    private void endArray()
    {
        if (indent != null)
            out.write(System.lineSeparator());

        level -= 1;

        writeIndent();

        out.write(']');
    }

    private void writeIndent()
    {
        if (indent != null && !indent.isEmpty())
        {
            for (int i = 0; i < level; ++i)
                out.write(indent);
        }
    }

    private void writeKey(String key)
    {
        JsonEncoder.encodeString(out, key);
        out.write(keySeparator);
        flush();
    }

    private void writeValue(Object value, FieldInfo fieldInfo)
    {
        if (value == null)
        {
            JsonEncoder.encodeNull(out);
            return;
        }

        switch (fieldInfo.getTypeInfo().getJavaType())
        {
        case BOOLEAN:
            JsonEncoder.encodeBool(out, (boolean)value);
            break;
        case BYTE:
        case SHORT:
        case INT:
        case LONG:
            JsonEncoder.encodeIntegral(out, ((Number)value).longValue());
            break;
        case BIG_INTEGER:
            JsonEncoder.encodeIntegral(out, (BigInteger)value);
            break;
        case FLOAT:
        case DOUBLE:
            JsonEncoder.encodeFloatingPoint(out, ((Number)value).doubleValue());
            break;
        case BYTES:
            writeBytes((byte[])value);
            break;
        case STRING:
            JsonEncoder.encodeString(out, (String)value);
            break;
        case BIT_BUFFER:
            writeBitBuffer((BitBuffer)value);
            break;
        case ENUM:
            if (enumerableFormat == EnumerableFormat.STRING)
                writeStringifiedEnum((ZserioEnum)value, fieldInfo.getTypeInfo());
            else
                JsonEncoder.encodeIntegral(out, ((ZserioEnum)value).getGenericValue());
            break;
        case BITMASK:
            if (enumerableFormat == EnumerableFormat.STRING)
                writeStringifiedBitmask((ZserioBitmask)value, fieldInfo.getTypeInfo());
            else
                JsonEncoder.encodeIntegral(out, ((ZserioBitmask)value).getGenericValue());
            break;
        default:
            throw new ZserioError("JsonWriter: Unexpected not-null value of type '" +
                    fieldInfo.getTypeInfo().getSchemaName() + "'!");
        }

        flush();
    }

    private void writeBytes(byte[] bytes)
    {
        beginObject();
        beginItem();
        writeKey("buffer");
        beginArray();
        for (byte byteValue : bytes)
        {
            beginItem();
            // note that we don't want to have negative numbers in bytes
            JsonEncoder.encodeIntegral(out, Byte.toUnsignedInt(byteValue));
            endItem();
        }
        endArray();
        endItem();
        endObject();
    }

    private void writeBitBuffer(BitBuffer bitBuffer)
    {
        beginObject();
        beginItem();
        writeKey("buffer");
        beginArray();
        for (byte byteValue : bitBuffer.getBuffer())
        {
            beginItem();
            // note that we don't want to have negative numbers in bit buffer
            JsonEncoder.encodeIntegral(out, Byte.toUnsignedInt(byteValue));
            endItem();
        }
        endArray();
        endItem();
        beginItem();
        writeKey("bitSize");
        JsonEncoder.encodeIntegral(out, bitBuffer.getBitSize());
        endItem();
        endObject();
    }

    private void writeStringifiedEnum(ZserioEnum zserioEnum, TypeInfo typeInfo)
    {
        final BigInteger enumValue = numberToBigInteger(zserioEnum.getGenericValue());
        for (ItemInfo itemInfo : typeInfo.getEnumItems())
        {
            // exact match
            if (enumValue.equals(itemInfo.getValue()))
            {
                JsonEncoder.encodeString(out, itemInfo.getSchemaName());
                return;
            }
        }

        // no match
        JsonEncoder.encodeString(out, enumValue.toString() + " /* no match */");
    }

    private void writeStringifiedBitmask(ZserioBitmask zserioBitmask, TypeInfo typeInfo)
    {
        StringBuilder stringValue = new StringBuilder();
        final BigInteger bitmaskValue = numberToBigInteger(zserioBitmask.getGenericValue());
        BigInteger valueCheck = BigInteger.ZERO;
        for (ItemInfo itemInfo : typeInfo.getBitmaskValues())
        {
            final boolean isZero = itemInfo.getValue().equals(BigInteger.ZERO);
            if ((!isZero && bitmaskValue.and(itemInfo.getValue()).equals(itemInfo.getValue())) ||
                    (isZero && bitmaskValue.equals(BigInteger.ZERO)))
            {
                valueCheck = valueCheck.or(itemInfo.getValue());
                if (stringValue.length() > 0)
                    stringValue.append(" | ");
                stringValue.append(itemInfo.getSchemaName());
            }
        }

        if (stringValue.length() == 0)
        {
            // no match
            stringValue.append(bitmaskValue.toString());
            stringValue.append(" /* no match */");
        }
        else if (!bitmaskValue.equals(valueCheck))
        {
            // partial match
            stringValue = new StringBuilder(bitmaskValue.toString())
                    .append(" /* partial match: ")
                    .append(stringValue.toString())
                    .append(" */");
        }
        // else exact match

        JsonEncoder.encodeString(out, stringValue.toString());
    }

    private static BigInteger numberToBigInteger(Number number)
    {
        if (number instanceof BigInteger)
            return (BigInteger)number;
        else
            return BigInteger.valueOf(number.longValue());
    }

    private void flush()
    {
        out.flush();
        if (out.checkError())
            throw new ZserioError("JsonWriter: Output stream error occurred!");
    }

    /**
     * Default item separator used when indent is not set (i.e. is null).
     */
    public static final String DEFAULT_ITEM_SEPARATOR = ", ";

    /**
     * Default item separator used when indent is not null.
     */
    public static final String DEFAULT_ITEM_SEPARATOR_WITH_INDENT = ",";

    /**
     * Default key separator.
     */
    public static final String DEFAULT_KEY_SEPARATOR = ": ";

    /**
     * Default configuration for enumerable types.
     */
    public static final EnumerableFormat DEFAULT_ENUMERABLE_FORMAT = EnumerableFormat.STRING;

    private final PrintWriter out;
    private final String indent;
    private String itemSeparator;
    private String keySeparator = DEFAULT_KEY_SEPARATOR;
    private EnumerableFormat enumerableFormat = DEFAULT_ENUMERABLE_FORMAT;

    private boolean isFirst = true;
    private int level = 0;
}
