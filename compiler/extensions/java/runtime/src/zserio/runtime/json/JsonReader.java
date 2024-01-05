package zserio.runtime.json;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import zserio.runtime.ZserioError;
import zserio.runtime.creator.ZserioTreeCreator;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.typeinfo.ItemInfo;
import zserio.runtime.typeinfo.JavaType;
import zserio.runtime.typeinfo.TypeInfo;

/**
 * Reads zserio object tree defined by a type info from a text stream.
 */
public final class JsonReader implements AutoCloseable
{
    /**
     * Constructor.
     *
     * @param reader Text stream to read.
     */
    public JsonReader(Reader reader)
    {
        this.reader = reader;
        this.creatorAdapter = new CreatorAdapter();
        this.parser = new JsonParser(reader, creatorAdapter);
    }

    @Override
    public void close() throws IOException
    {
        reader.close();
    }

    /**
     * Reads a zserio object tree defined by the given type info from the text steam.
     *
     * @param typeInfo Type info defining the expected zserio object tree.
     * @param arguments Arguments of type defining the expected zserio object tree.
     *
     * @return Zserio object tree initialized using the JSON data.
     */
    public Object read(TypeInfo typeInfo, Object... arguments)
    {
        creatorAdapter.setType(typeInfo, arguments);

        try
        {
            parser.parse();
        }
        catch (JsonParserError excpt)
        {
            throw excpt;
        }
        catch (ZserioError excpt)
        {
            throw new ZserioError(
                    excpt.getMessage() + " (JsonParser:" + parser.getLine() + ":" + parser.getColumn() + ")");
        }

        return creatorAdapter.get();
    }

    /**
     * Adapter for values which are encoded as a JSON object.
     */
    private interface ObjectValueAdapter extends JsonParser.Observer
    {
        /**
         * Gets the parsed value.
         */
        Object get();
    }

    /**
     * The adapter which allows to parse Bit Buffer object from JSON.
     */
    private static class BitBufferAdapter implements ObjectValueAdapter
    {
        /**
         * Constructor.
         */
        public BitBufferAdapter()
        {
            state = State.VISIT_KEY;
            buffer = null;
            bitSize = null;
        }

        /**
         * Gets the created Bit Buffer object.
         *
         * @return Parsed Bit Buffer object.
         */
        @Override
        public BitBuffer get()
        {
            if (buffer == null || bitSize == null)
                throw new ZserioError("JsonReader: Unexpected end in Bit Buffer!");

            final int byteSize = buffer.size();
            final byte[] array = new byte[byteSize];
            for (int i = 0; i < byteSize; ++i)
                array[i] = buffer.get(i);

            return new BitBuffer(array, bitSize);
        }

        @Override
        public void beginObject()
        {
            throw new ZserioError("JsonReader: Unexpected begin object in Bit Buffer!");
        }

        @Override
        public void endObject()
        {
            throw new ZserioError("JsonReader: Unexpected end object in Bit Buffer!");
        }

        @Override
        public void beginArray()
        {
            if (state == State.BEGIN_ARRAY_BUFFER)
                state = State.VISIT_VALUE_BUFFER;
            else
                throw new ZserioError("JsonReader: Unexpected begin array in Bit Buffer!");
        }

        @Override
        public void endArray()
        {
            if (state == State.VISIT_VALUE_BUFFER)
                state = State.VISIT_KEY;
            else
                throw new ZserioError("JsonReader: Unexpected end array in Bit Buffer!");
        }

        @Override
        public void visitKey(String key)
        {
            if (state == State.VISIT_KEY)
            {
                if (key.equals("buffer"))
                    state = State.BEGIN_ARRAY_BUFFER;
                else if (key.equals("bitSize"))
                    state = State.VISIT_VALUE_BITSIZE;
                else
                    throw new ZserioError("JsonReader: Unknown key '" + key + "' in Bit Buffer!");
            }
            else
            {
                throw new ZserioError("JsonReader: Unexpected key '" + key + "' in Bit Buffer!");
            }
        }

        @Override
        public void visitValue(Object value)
        {
            if (state == State.VISIT_VALUE_BUFFER && value instanceof BigInteger)
            {
                if (buffer == null)
                    buffer = new ArrayList<Byte>();

                // bit buffer stores 8-bit unsigned values in byte type
                final BigInteger intValue = (BigInteger)value;
                if (intValue.compareTo(BigInteger.ZERO) < 0 || intValue.compareTo(BigInteger.valueOf(255)) > 0)
                {
                    throw new ZserioError("JsonReader: Cannot create byte for Bit Buffer from value '" +
                            value.toString() + "'!");
                }
                buffer.add(((BigInteger)value).byteValue());
            }
            else if (state == State.VISIT_VALUE_BITSIZE && value instanceof BigInteger)
            {
                try
                {
                    bitSize = ((BigInteger)value).longValueExact();
                }
                catch (ArithmeticException excpt)
                {
                    throw new ZserioError("JsonReader: Cannot create long for Bit Buffer size from value '" +
                                    value.toString() + "'!",
                            excpt);
                }
                state = State.VISIT_KEY;
            }
            else
            {
                throw new ZserioError("JsonReader: Unexpected value '" + value + "' in Bit Buffer!");
            }
        }

        private enum State
        {
            VISIT_KEY,
            BEGIN_ARRAY_BUFFER,
            VISIT_VALUE_BUFFER,
            VISIT_VALUE_BITSIZE
        }

        private State state;
        private List<Byte> buffer;
        private Long bitSize;
    }

    /**
     * The adapter which allows to parse bytes object from JSON.
     */
    private static class BytesAdapter implements ObjectValueAdapter
    {
        /**
         * Constructor.
         */
        public BytesAdapter()
        {
            state = State.VISIT_KEY;
            buffer = null;
        }

        /**
         * Gets the created bytes object.
         *
         * @return Parsed bytes object.
         */
        @Override
        public byte[] get()
        {
            if (buffer == null)
                throw new ZserioError("JsonReader: Unexpected end in bytes!");

            final byte[] bytes = new byte[buffer.size()];
            for (int i = 0; i < bytes.length; ++i)
                bytes[i] = buffer.get(i);

            return bytes;
        }

        @Override
        public void beginObject()
        {
            throw new ZserioError("JsonReader: Unexpected begin object in bytes!");
        }

        @Override
        public void endObject()
        {
            throw new ZserioError("JsonReader: Unexpected end object in bytes!");
        }

        @Override
        public void beginArray()
        {
            if (state == State.BEGIN_ARRAY_BUFFER)
                state = State.VISIT_VALUE_BUFFER;
            else
                throw new ZserioError("JsonReader: Unexpected begin array in bytes!");
        }

        @Override
        public void endArray()
        {
            if (state == State.VISIT_VALUE_BUFFER)
                state = State.VISIT_KEY;
            else
                throw new ZserioError("JsonReader: Unexpected end array in bytes!");
        }

        @Override
        public void visitKey(String key)
        {
            if (state == State.VISIT_KEY)
            {
                if (key.equals("buffer"))
                    state = State.BEGIN_ARRAY_BUFFER;
                else
                    throw new ZserioError("JsonReader: Unknown key '" + key + "' in bytes!");
            }
            else
            {
                throw new ZserioError("JsonReader: Unexpected key '" + key + "' in bytes!");
            }
        }

        @Override
        public void visitValue(Object value)
        {
            if (state == State.VISIT_VALUE_BUFFER && value instanceof BigInteger)
            {
                if (buffer == null)
                    buffer = new ArrayList<Byte>();

                // bit buffer stores 8-bit unsigned values in byte type
                final BigInteger intValue = (BigInteger)value;
                if (intValue.compareTo(BigInteger.ZERO) < 0 || intValue.compareTo(BigInteger.valueOf(255)) > 0)
                {
                    throw new ZserioError(
                            "JsonReader: Cannot create byte for bytes from value '" + value.toString() + "'!");
                }
                buffer.add(((BigInteger)value).byteValue());
            }
            else
            {
                throw new ZserioError("JsonReader: Unexpected value '" + value + "' in bytes!");
            }
        }

        private enum State
        {
            VISIT_KEY,
            BEGIN_ARRAY_BUFFER,
            VISIT_VALUE_BUFFER,
        }

        private State state;
        private List<Byte> buffer;
    }

    /**
     * The adapter which allows to use ZserioTreeCreator as an JsonReader observer.
     */
    private static class CreatorAdapter implements JsonParser.Observer
    {
        /**
         * Constructor.
         */
        public CreatorAdapter()
        {
            creator = null;
            keyStack = new Stack<String>();
            object = null;
            objectValueAdapter = null;
        }

        /**
         * Sets type which shall be created next. Resets the current object.
         *
         * @param typeInfo Type info of the type which is to be created.
         * @param arguments Arguments of type defining the expected zserio object tree.
         */
        public void setType(TypeInfo typeInfo, Object... arguments)
        {
            creator = new ZserioTreeCreator(typeInfo, arguments);
            object = null;
        }

        /**
         * Gets the created zserio object tree.
         *
         * @return Zserio object tree.
         */
        public Object get()
        {
            if (object == null)
                throw new ZserioError("JsonReader: Zserio tree not created!");

            return object;
        }

        @Override
        public void beginObject()
        {
            if (objectValueAdapter != null)
            {
                objectValueAdapter.beginObject();
            }
            else
            {
                if (creator == null)
                    throw new ZserioError("JsonReader: Adapter not initialized!");

                if (keyStack.isEmpty())
                {
                    creator.beginRoot();
                }
                else
                {
                    final String lastKey = keyStack.peek();
                    if (!lastKey.isEmpty())
                    {
                        final JavaType javaType = creator.getFieldType(lastKey).getJavaType();
                        if (javaType == JavaType.BIT_BUFFER)
                            objectValueAdapter = new BitBufferAdapter();
                        else if (javaType == JavaType.BYTES)
                            objectValueAdapter = new BytesAdapter();
                        else
                            creator.beginCompound(lastKey);
                    }
                    else
                    {
                        final JavaType javaType = creator.getElementType().getJavaType();
                        if (javaType == JavaType.BIT_BUFFER)
                            objectValueAdapter = new BitBufferAdapter();
                        else if (javaType == JavaType.BYTES)
                            objectValueAdapter = new BytesAdapter();
                        else
                            creator.beginCompoundElement();
                    }
                }
            }
        }

        @Override
        public void endObject()
        {
            if (objectValueAdapter != null)
            {
                final Object objectValue = objectValueAdapter.get();
                objectValueAdapter = null;
                visitValue(objectValue);
            }
            else
            {
                if (creator == null)
                    throw new ZserioError("JsonReader: Adapter not initialized!");

                if (keyStack.isEmpty())
                {
                    object = creator.endRoot();
                    creator = null;
                }
                else
                {
                    final String lastKey = keyStack.peek();
                    if (!lastKey.isEmpty())
                    {
                        creator.endCompound();
                        keyStack.pop(); // finish member
                    }
                    else
                    {
                        creator.endCompoundElement();
                    }
                }
            }
        }

        @Override
        public void beginArray()
        {
            if (objectValueAdapter != null)
            {
                objectValueAdapter.beginArray();
            }
            else
            {
                if (creator == null)
                    throw new ZserioError("JsonReader: Adapter not initialized!");

                if (keyStack.isEmpty())
                    throw new ZserioError("JsonReader: ZserioTreeCreator expects json object!");

                creator.beginArray(keyStack.peek());

                keyStack.push("");
            }
        }

        @Override
        public void endArray()
        {
            if (objectValueAdapter != null)
            {
                objectValueAdapter.endArray();
            }
            else
            {
                if (creator == null)
                    throw new ZserioError("JsonReader: Adapter not initialized!");

                creator.endArray();

                keyStack.pop(); // finish array
                keyStack.pop(); // finish member
            }
        }

        @Override
        public void visitKey(String key)
        {
            if (objectValueAdapter != null)
            {
                objectValueAdapter.visitKey(key);
            }
            else
            {
                if (creator == null)
                    throw new ZserioError("JsonReader: Adapter not initialized!");

                keyStack.push(key);
            }
        }

        @Override
        public void visitValue(Object value)
        {
            if (objectValueAdapter != null)
            {
                objectValueAdapter.visitValue(value);
            }
            else
            {
                if (creator == null)
                    throw new ZserioError("JsonReader: Adapter not initialized!");

                if (keyStack.isEmpty())
                    throw new ZserioError("JsonReader: ZserioTreeCreator expects json object!");

                final String lastKey = keyStack.peek();
                if (!lastKey.isEmpty())
                {
                    final TypeInfo expectedTypeInfo = creator.getFieldType(lastKey);
                    creator.setValue(lastKey, convertValue(value, expectedTypeInfo));
                    keyStack.pop(); // finish member
                }
                else
                {
                    final TypeInfo expectedTypeInfo = creator.getElementType();
                    creator.addValueElement(convertValue(value, expectedTypeInfo));
                }
            }
        }

        private static Object convertValue(Object value, TypeInfo typeInfo)
        {
            if (value == null)
                return null;

            final JavaType expectedJavaType = typeInfo.getJavaType();
            switch (expectedJavaType)
            {
            case ENUM:
                if (value instanceof String)
                    return createEnum((String)value, typeInfo);
                if (value instanceof BigInteger)
                    return createEnum((BigInteger)value, typeInfo);
                break;

            case BITMASK:
                if (value instanceof String)
                    return createBitmask((String)value, typeInfo);
                if (value instanceof BigInteger)
                    return createBitmask((BigInteger)value, typeInfo);
                break;

            case FLOAT:
                if (value instanceof Double)
                    return ((Double)value).floatValue();
                break;

            default:
                if (value instanceof BigInteger)
                    return convertNumber((BigInteger)value, typeInfo, expectedJavaType);
                break;
            }

            // possible type mismatch => just leave it to creator, it will check and report better message
            return value;
        }

        private static Object createEnum(String stringValue, TypeInfo typeInfo)
        {
            if (!stringValue.isEmpty())
            {
                final char firstChar = stringValue.charAt(0);
                if ((firstChar >= 'A' && firstChar <= 'Z') || (firstChar >= 'a' && firstChar <= 'z') ||
                        firstChar == '_')
                {
                    final BigInteger value = parseEnumStringValue(stringValue, typeInfo);
                    if (value != null)
                        return createEnum(value, typeInfo);
                }
                // else it's a no match
            }

            throw new ZserioError("JsonReader: Cannot create enum '" + typeInfo.getSchemaName() +
                    "' from string value '" + stringValue + "'!");
        }

        private static Object createEnum(BigInteger value, TypeInfo typeInfo)
        {
            try
            {
                final Class<?> enumClass = typeInfo.getJavaClass();
                final TypeInfo enumUnderlyingTypeInfo = typeInfo.getUnderlyingType();
                final Class<?> enumUnderlyingClass = enumUnderlyingTypeInfo.getJavaClass();
                final Method toEnum = enumClass.getMethod("toEnum", enumUnderlyingClass);
                final JavaType enumUnderlyingJavaType = enumUnderlyingTypeInfo.getJavaType();

                return toEnum.invoke(null, convertNumber(value, typeInfo, enumUnderlyingJavaType));
            }
            catch (ClassCastException | SecurityException | IllegalAccessException | IllegalArgumentException |
                    InvocationTargetException | NoSuchMethodException excpt)
            {
                throw new ZserioError("JsonReader: Cannot create enum '" + typeInfo.getSchemaName() +
                                "' from value '" + value.toString() + "'!",
                        excpt);
            }
        }

        private static Object createBitmask(String stringValue, TypeInfo typeInfo)
        {
            if (!stringValue.isEmpty())
            {
                final char firstChar = stringValue.charAt(0);
                if ((firstChar >= 'A' && firstChar <= 'Z') || (firstChar >= 'a' && firstChar <= 'z') ||
                        firstChar == '_')
                {
                    final BigInteger value = parseBitmaskStringValue(stringValue, typeInfo);
                    if (value != null)
                        return createBitmask(value, typeInfo);
                }
                else if (firstChar >= '0' && firstChar <= '9') // bitmask can be only unsigned
                {
                    final BigInteger value = parseBitmaskNumericStringValue(stringValue);
                    if (value != null)
                        return createBitmask(value, typeInfo);
                }
            }

            throw new ZserioError("JsonReader: Cannot create bitmask '" + typeInfo.getSchemaName() +
                    "' from string value '" + stringValue + "'!");
        }

        private static Object createBitmask(BigInteger value, TypeInfo typeInfo)
        {
            try
            {
                final Class<?> bitmaskClass = typeInfo.getJavaClass();
                final TypeInfo bitmaskUnderlyingTypeInfo = typeInfo.getUnderlyingType();
                final Class<?> bitmaskUnderlyingClass = bitmaskUnderlyingTypeInfo.getJavaClass();
                final Class<?>[] parameterType = new Class<?>[] {bitmaskUnderlyingClass};
                final Constructor<?> constructor = bitmaskClass.getConstructor(parameterType);
                final JavaType bitmaskUnderlyingJavaType = bitmaskUnderlyingTypeInfo.getJavaType();

                return constructor.newInstance(convertNumber(value, typeInfo, bitmaskUnderlyingJavaType));
            }
            catch (ClassCastException | InstantiationException | SecurityException | IllegalAccessException |
                    IllegalArgumentException | InvocationTargetException | NoSuchMethodException excpt)
            {
                throw new ZserioError("JsonReader: Cannot create bitmask '" + typeInfo.getSchemaName() +
                                "' from value '" + value.toString() + "'!",
                        excpt);
            }
        }

        private static BigInteger parseEnumStringValue(String stringValue, TypeInfo typeInfo)
        {
            for (ItemInfo itemInfo : typeInfo.getEnumItems())
            {
                if (stringValue.equals(itemInfo.getSchemaName()))
                    return itemInfo.getValue();
            }

            return null;
        }

        private static BigInteger parseBitmaskStringValue(String stringValue, TypeInfo typeInfo)
        {
            BigInteger value = BigInteger.ZERO;
            final String[] identifiers = stringValue.split("\\|");
            for (String identifierWithSpaces : identifiers)
            {
                final String identifier = identifierWithSpaces.trim();
                boolean match = false;
                for (ItemInfo itemInfo : typeInfo.getBitmaskValues())
                {
                    if (identifier.equals(itemInfo.getSchemaName()))
                    {
                        match = true;
                        value = value.or(itemInfo.getValue());
                        break;
                    }
                }

                if (!match)
                    return null;
            }

            return value;
        }

        private static BigInteger parseBitmaskNumericStringValue(String stringValue)
        {
            int numberLen = 1;
            while (stringValue.charAt(numberLen) >= '0' && stringValue.charAt(numberLen) <= '9')
                numberLen++;
            try
            {
                return new BigInteger(stringValue.substring(0, numberLen));
            }
            catch (NumberFormatException excpt)
            {
                return null;
            }
        }

        private static Object convertNumber(BigInteger value, TypeInfo typeInfo, JavaType expectedJavaType)
        {
            switch (expectedJavaType)
            {
            case BYTE:
                return createByte(value, typeInfo);

            case SHORT:
                return createShort(value, typeInfo);

            case INT:
                return createInt(value, typeInfo);

            case LONG:
                return createLong(value, typeInfo);

            default:
                return value;
            }
        }

        private static byte createByte(BigInteger value, TypeInfo typeInfo)
        {
            try
            {
                return value.byteValueExact();
            }
            catch (ArithmeticException excpt)
            {
                throw new ZserioError("JsonReader: Cannot create byte '" + typeInfo.getSchemaName() +
                                "' from value '" + value.toString() + "'!",
                        excpt);
            }
        }

        private static short createShort(BigInteger value, TypeInfo typeInfo)
        {
            try
            {
                return value.shortValueExact();
            }
            catch (ArithmeticException excpt)
            {
                throw new ZserioError("JsonReader: Cannot create short '" + typeInfo.getSchemaName() +
                                "' from value '" + value.toString() + "'!",
                        excpt);
            }
        }

        private static int createInt(BigInteger value, TypeInfo typeInfo)
        {
            try
            {
                return value.intValueExact();
            }
            catch (ArithmeticException excpt)
            {
                throw new ZserioError("JsonReader: Cannot create int '" + typeInfo.getSchemaName() +
                                "' from value '" + value.toString() + "'!",
                        excpt);
            }
        }

        private static long createLong(BigInteger value, TypeInfo typeInfo)
        {
            try
            {
                return value.longValueExact();
            }
            catch (ArithmeticException excpt)
            {
                throw new ZserioError("JsonReader: Cannot create long '" + typeInfo.getSchemaName() +
                                "' from value '" + value.toString() + "'!",
                        excpt);
            }
        }

        private ZserioTreeCreator creator;
        private final Stack<String> keyStack;
        private Object object;
        private ObjectValueAdapter objectValueAdapter;
    }

    private final Reader reader;
    private final CreatorAdapter creatorAdapter;
    private final JsonParser parser;
}
