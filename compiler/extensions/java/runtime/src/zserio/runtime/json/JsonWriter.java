package zserio.runtime.json;

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;

import zserio.runtime.ZserioEnum;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.typeinfo.FieldInfo;
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
            out.write('\n');

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
            out.write('\n');

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
            out.write('\n');

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
        case STRING:
            JsonEncoder.encodeString(out, (String)value);
            break;
        case BIT_BUFFER:
            writeBitBuffer((BitBuffer)value);
            break;
        case ENUM:
            JsonEncoder.encodeIntegral(out, ((ZserioEnum)value).getGenericValue());
            break;
        case BITMASK:
            writeBitmask(value, fieldInfo);
            break;
        default:
            throw new ZserioError("JsonWriter: Unexpected not-null value of type '" +
                    fieldInfo.getTypeInfo().getSchemaName() + "'!");
        }

        flush();
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
            JsonEncoder.encodeIntegral(out, byteValue);
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

    private void writeBitmask(Object bitmaskValue, FieldInfo fieldInfo)
    {
        try
        {
            final Method method = bitmaskValue.getClass().getDeclaredMethod("getValue");
            final Object result = method.invoke(bitmaskValue);
            if (result instanceof Number)
            {
                JsonEncoder.encodeIntegral(out, (Number)result);
            }
            else
            {
                throw new ZserioError("JsonWriter: Unexpected value type for Bitmask '" +
                        fieldInfo.getTypeInfo().getSchemaName() + "'!");
            }
        }
        catch (NoSuchMethodException | SecurityException |
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw new ZserioError("JsonWriter: Failed to get value from zserio Bitmask '" +
                    fieldInfo.getTypeInfo().getSchemaName() + "'!");
        }
    }

    private void flush()
    {
        out.flush();
        if (out.checkError())
            throw new ZserioError("JsonWriter: Output stream error occured!");
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

    private final PrintWriter out;
    private final String indent;
    private String itemSeparator;
    private String keySeparator = DEFAULT_KEY_SEPARATOR;

    private boolean isFirst = true;
    private int level = 0;
}
