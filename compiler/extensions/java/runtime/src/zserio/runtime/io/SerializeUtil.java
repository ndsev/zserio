package zserio.runtime.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import zserio.runtime.ZserioEnum;
import zserio.runtime.ZserioError;

/**
 * Provides help methods for serialization and deserialization of generated objects.
 * <p>
 * These utilities are not used by generated code and they are provided only for user convenience.
 */
public final class SerializeUtil
{
    /**
     * Serializes generated object to the bit buffer.
     * <p>
     * Before serialization, the method calls initializeOffsets() on the given zserio object.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.io.SerializeUtil;
     *
     * final SomeZserioObject object = new SomeZserioObject();
     * final BitBuffer bitBuffer = SerializeUtil.serialize(object);
     * </pre></blockquote>
     *
     * @param <T> Type of generated object.
     * @param object Generated object to serialize.
     *
     * @return Bit buffer which represents generated object in binary format.
     */
    public static <T extends Writer> BitBuffer serialize(T object)
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            serializeToWriter(object, writer);
            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
        catch (IOException exception)
        {
            throw new ZserioError("SerializeUtil: " + exception, exception);
        }
    }

    /**
     * Deserializes bit buffer to the generated object.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.io.SerializeUtil;
     *
     * final SomeZserioObject object = new SomeZserioObject();
     * final BitBuffer bitBuffer = SerializeUtil.serialize(object);
     * final SomeZserioObject readObject = SerializeUtil.deserialize(SomeZserioObject.class, bitBuffer);
     * </pre></blockquote>
     *
     * @param <T> Type of generated object.
     * @param clazz Class instance of the generated object to deserialize.
     * @param bitBuffer Bit buffer which represents generated object in binary format.
     * @param arguments Additional arguments needed for reader constructor (optional).
     *
     * @return Generated object created from given bit buffer.
     */
    public static <T> T deserialize(final Class<T> clazz, BitBuffer bitBuffer, Object... arguments)
    {
        return deserializeFromBytes(clazz, bitBuffer.getBuffer(), arguments);
    }

    /**
     * Serializes generated object to the byte array.
     * <p>
     * Before serialization, the method calls initializeOffsets() on the given zserio object.
     * <p>
     * This is a convenient method for users which do not need exact number of bits to which the given object
     * will be serialized.
     * <p>
     * However, it's still possible that not all bits of the last byte are used. In this case, only most
     * significant bits of the corresponding size are used.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.io.SerializeUtil;
     *
     * final SomeZserioObject object = new SomeZserioObject();
     * final byte[] buffer = SerializeUtil.serializeToBytes(object);
     * </pre></blockquote>
     *
     * @param <T> Type of generated object.
     * @param object Generated object to serialize.
     *
     * @return Byte array which represents generated object in binary format.
     */
    public static <T extends Writer> byte[] serializeToBytes(T object)
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            serializeToWriter(object, writer);
            return writer.toByteArray();
        }
        catch (IOException exception)
        {
            throw new ZserioError("SerializeUtil: " + exception, exception);
        }
    }

    /**
     * Deserializes byte array to the generated object.
     * <p>
     * This method can potentially use all bits of the last byte even if not all of them were written during
     * serialization (because there is no way how to specify exact number of bits). Thus, it could allow reading
     * behind stream (possibly in case of damaged data).
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.io.SerializeUtil;
     *
     * final SomeZserioObject object = new SomeZserioObject();
     * final byte[] buffer = SerializeUtil.serializeToBytes(object);
     * final SomeZserioObject readObject = SerializeUtil.deserializeFromBytes(SomeZserioObject.class, buffer);
     * </pre></blockquote>
     *
     * @param <T> Type of generated object.
     * @param clazz Class instance of the generated object to deserialize.
     * @param buffer Byte array which represents generated object in binary format.
     * @param arguments Additional arguments needed for reader constructor (optional).
     *
     * @return Generated object created from given byte array.
     */
    public static <T> T deserializeFromBytes(final Class<T> clazz, byte[] buffer, Object... arguments)
    {
        try (final BitStreamReader reader = new ByteArrayBitStreamReader(buffer))
        {
            return deserializeFromReader(clazz, reader, arguments);
        }
        catch (IOException exception)
        {
            throw new ZserioError("SerializeUtil: " + exception, exception);
        }
    }

    /**
     * Serializes generated object to the file using file name.
     * <p>
     * Before serialization, the method calls initializeOffsets() on the given zserio object.
     * <p>
     * This is a convenient method for users to easily write given generated object to file.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.io.SerializeUtil;
     *
     * final SomeZserioObject object = new SomeZserioObject();
     * SerializeUtil.serializeToFile(object, "FileName.bin");
     * </pre></blockquote>
     *
     * @param <T> Type of generated object.
     * @param object Generated object to serialize.
     * @param fileName Name of the file to write.
     */
    public static <T extends Writer> void serializeToFile(T object, String fileName)
    {
        serializeToFile(object, new File(fileName));
    }

    /**
     * Serializes generated object to the file.
     * <p>
     * Before serialization, the method calls initializeOffsets() on the given zserio object.
     * <p>
     * This is a convenient method for users to easily write given generated object to file.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.io.SerializeUtil;
     *
     * final SomeZserioObject object = new SomeZserioObject();
     * SerializeUtil.serializeToFile(object, "FileName.bin");
     * </pre></blockquote>
     *
     * @param <T> Type of generated object.
     * @param object Generated object to serialize.
     * @param file File to write.
     */
    public static <T extends Writer> void serializeToFile(T object, File file)
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            serializeToWriter(object, writer);
            final byte[] bytes = writer.toByteArray();
            try (final FileOutputStream outputStream = new FileOutputStream(file))
            {
                outputStream.write(bytes);
                outputStream.flush();
            }
        }
        catch (IOException exception)
        {
            throw new ZserioError("SerializeUtil: " + exception, exception);
        }
    }

    /**
     * Deserializes file to the generated object using file name.
     * <p>
     * This is a convenient method for users to easily read given generated object from file.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.io.SerializeUtil;
     *
     * final String fileName = "FileName.bin";
     * final SomeZserioObject object = new SomeZserioObject();
     * SerializeUtil.serializeToFile(object, fileName);
     * final SomeZserioObject readObject = SerializeUtil.deserializeFromFile(SomeZserioObject.class, fileName);
     * </pre></blockquote>
     *
     * @param <T> Type of generated object.
     * @param clazz Class instance of the generated object to deserialize.
     * @param fileName Name of the file which represents generated object in binary format.
     * @param arguments Additional arguments needed for reader constructor (optional).
     *
     * @return Generated object created from given file contents.
     */
    public static <T> T deserializeFromFile(final Class<T> clazz, String fileName, Object... arguments)
    {
        return deserializeFromFile(clazz, new File(fileName), arguments);
    }

    /**
     * Deserializes file to the generated object.
     * <p>
     * This is a convenient method for users to easily read given generated object from file.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.io.SerializeUtil;
     *
     * final String fileName = "FileName.bin";
     * final SomeZserioObject object = new SomeZserioObject();
     * SerializeUtil.serializeToFile(object, fileName);
     * final SomeZserioObject readObject = SerializeUtil.deserializeFromFile(SomeZserioObject.class, fileName);
     * </pre></blockquote>
     *
     * @param <T> Type of generated object.
     * @param clazz Class instance of the generated object to deserialize.
     * @param file File which represents generated object in binary format.
     * @param arguments Additional arguments needed for reader constructor (optional).
     *
     * @return Generated object created from given file contents.
     */
    public static <T> T deserializeFromFile(final Class<T> clazz, File file, Object... arguments)
    {
        try
        {
            final byte[] fileContent = Files.readAllBytes(file.toPath());
            try (final BitStreamReader reader = new ByteArrayBitStreamReader(fileContent))
            {
                return deserializeFromReader(clazz, reader, arguments);
            }
        }
        catch (IOException exception)
        {
            throw new ZserioError("SerializeUtil: " + exception, exception);
        }
    }

    private static <T extends Writer> void serializeToWriter(T object, BitStreamWriter writer)
            throws IOException
    {
        object.initializeOffsets(writer.getBitPosition());
        object.write(writer);
    }

    private static <T> T deserializeFromReader(
            final Class<T> clazz, BitStreamReader reader, Object... arguments)
    {
        try
        {
            if (Arrays.asList(clazz.getInterfaces()).contains(ZserioEnum.class))
            {
                final Method method = clazz.getMethod("readEnum", BitStreamReader.class);
                return clazz.cast(method.invoke(null, reader));
            }
            else
            {
                final Class<?>[] ctorArgumentTypes = new Class<?>[ arguments.length + 1 ];
                ctorArgumentTypes[0] = BitStreamReader.class;
                // please note that arguments are always boxed and object parameters are always unboxed
                for (int i = 0; i < arguments.length; ++i)
                    ctorArgumentTypes[i + 1] = toUnboxedClass(arguments[i].getClass());
                final Constructor<T> constructor = clazz.getConstructor(ctorArgumentTypes);

                final Object[] ctorArguments = new Object[arguments.length + 1];
                ctorArguments[0] = reader;
                for (int i = 0; i < arguments.length; ++i)
                    ctorArguments[i + 1] = arguments[i];
                return constructor.newInstance(ctorArguments);
            }
        }
        catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException exception)
        {
            throw new ZserioError("SerializeUtil: " + exception, exception);
        }
    }

    private static Class<?> toUnboxedClass(Class<?> clazz)
    {
        final Class<?> unboxedClazz = boxedToUnboxedClassMap.get(clazz);
        return (unboxedClazz == null) ? clazz : unboxedClazz;
    }

    private static final Map<Class<?>, Class<?>> boxedToUnboxedClassMap = new HashMap<Class<?>, Class<?>>();
    static
    {
        boxedToUnboxedClassMap.put(Boolean.class, boolean.class);
        boxedToUnboxedClassMap.put(Byte.class, byte.class);
        boxedToUnboxedClassMap.put(Short.class, short.class);
        boxedToUnboxedClassMap.put(Character.class, char.class);
        boxedToUnboxedClassMap.put(Integer.class, int.class);
        boxedToUnboxedClassMap.put(Long.class, long.class);
        boxedToUnboxedClassMap.put(Float.class, float.class);
        boxedToUnboxedClassMap.put(Double.class, double.class);
    }
}
