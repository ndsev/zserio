package zserio.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import zserio.runtime.json.JsonReader;
import zserio.runtime.json.JsonWriter;
import zserio.runtime.typeinfo.TypeInfo;
import zserio.runtime.walker.DefaultWalkFilter;
import zserio.runtime.walker.WalkFilter;
import zserio.runtime.walker.Walker;

/**
 * Zserio debug string utilities.
 * <p>
 * Note that zserio objects must be generated with <code>-withTypeInfoCode</code> zserio option to enable
 * JSON debug string!
 */
public final class DebugStringUtil
{
    /**
     * Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.
     * <p>
     * This function allows setting of indentation of JSON output together with the walk filter.
     * <p>
     * Example:
     * <blockquote><pre>
     * import java.io.StringWriter;
     * import zserio.runtime.DebugStringUtil;
     * import zserio.runtime.walker.ArrayLengthWalkFilter;
     *
     * final StringWriter writer = new StringWriter();
     * final SomeZserioObject zserioObject = new SomeZserioObject();
     * final int indent = 4;
     * final ArrayLengthWalkFilter walkFilter = new ArrayLengthWalkFilter(5);
     * DebugStringUtil.toJsonStream(zserioObject, writer, indent, walkFilter);
     * </pre></blockquote>
     *
     * @param zserioObject Zserio object to use.
     * @param writer Writer to use.
     * @param indent Indent argument for JsonWriter.
     * @param walkFilter WalkFilter to use by Walker.
     */
    public static void toJsonStream(Object zserioObject, Writer writer, int indent, WalkFilter walkFilter)
    {
        // do not close the output stream to allow usage e.g. of System.out
        final JsonWriter jsonWriter = new JsonWriter(writer, indent);
        final Walker walker = new Walker(jsonWriter, walkFilter);
        walker.walk(zserioObject);
    }

    /**
     * Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.
     * <p>
     * This function allows setting of indentation of JSON output.
     * <p>
     * Example:
     * <blockquote><pre>
     * import java.io.StringWriter;
     * import zserio.runtime.DebugStringUtil;
     *
     * final StringWriter writer = new StringWriter();
     * final SomeZserioObject zserioObject = new SomeZserioObject();
     * final int indent = 4;
     * DebugStringUtil.toJsonStream(zserioObject, writer, indent);
     * </pre></blockquote>
     *
     * @param zserioObject Zserio object to use.
     * @param writer Writer to use.
     * @param indent Indent argument for JsonWriter.
     */
    public static void toJsonStream(Object zserioObject, Writer writer, int indent)
    {
        toJsonStream(zserioObject, writer, indent, new DefaultWalkFilter());
    }

    /**
     * Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.
     * <p>
     * This function allows setting of the walk filter.
     * <p>
     * The following example shows filtering of arrays up to 5 elements:
     * <blockquote><pre>
     * import java.io.StringWriter;
     * import zserio.runtime.DebugStringUtil;
     * import zserio.runtime.walker.ArrayLengthWalkFilter;
     *
     * final StringWriter writer = new StringWriter();
     * final SomeZserioObject zserioObject = new SomeZserioObject();
     * final ArrayLengthWalkFilter walkFilter = new ArrayLengthWalkFilter(5);
     * DebugStringUtil.toJsonStream(zserioObject, writer, walkFilter);
     * </pre></blockquote>
     *
     * @param zserioObject Zserio object to use.
     * @param writer Writer to use.
     * @param walkFilter WalkFilter to use by Walker.
     */
    public static void toJsonStream(Object zserioObject, Writer writer, WalkFilter walkFilter)
    {
        toJsonStream(zserioObject, writer, DEFAULT_INDENT, walkFilter);
    }

    /**
     * Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.
     * <p>
     * Example:
     * <blockquote><pre>
     * import java.io.StringWriter;
     * import zserio.runtime.DebugStringUtil;
     *
     * final StringWriter writer = new StringWriter();
     * final SomeZserioObject zserioObject = new SomeZserioObject();
     * DebugStringUtil.toJsonStream(zserioObject, writer);
     * </pre></blockquote>
     *
     * @param zserioObject Zserio object to use.
     * @param writer Writer to use.
     */
    public static void toJsonStream(Object zserioObject, Writer writer)
    {
        toJsonStream(zserioObject, writer, DEFAULT_INDENT);
    }

    /**
     * Gets debug string in JSON format using Walker with JsonWriter for given zserio object.
     * <p>
     * This function allows setting of indentation of JSON output together with the walk filter.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.DebugStringUtil;
     * import zserio.runtime.walker.ArrayLengthWalkFilter;
     *
     * final SomeZserioObject zserioObject = new SomeZserioObject();
     * final int indent = 4;
     * final ArrayLengthWalkFilter walkFilter = new ArrayLengthWalkFilter(5);
     * System.out.println(DebugStringUtil.toJsonString(zserioObject, indent, walkFilter));
     * </pre></blockquote>
     *
     * @param zserioObject Zserio object to use.
     * @param indent Indent argument for JsonWriter.
     * @param walkFilter WalkFilter to use by Walker.
     *
     * @return JSON debug string.
     */
    public static String toJsonString(Object zserioObject, int indent, WalkFilter walkFilter)
    {
        final StringWriter stringWriter = new StringWriter();
        toJsonStream(zserioObject, stringWriter, indent, walkFilter);
        return stringWriter.toString();
    }

    /**
     * Gets debug string in JSON format using Walker with JsonWriter for given zserio object.
     * <p>
     * This function allows setting of indentation of JSON output.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.DebugStringUtil;
     *
     * final SomeZserioObject zserioObject = new SomeZserioObject();
     * final int indent = 4;
     * System.out.println(DebugStringUtil.toJsonString(zserioObject, indent));
     * </pre></blockquote>
     *
     * @param zserioObject Zserio object to use.
     * @param indent Indent argument for JsonWriter.
     *
     * @return JSON debug string.
     */
    public static String toJsonString(Object zserioObject, int indent)
    {
        return toJsonString(zserioObject, indent, new DefaultWalkFilter());
    }

    /**
     * Gets debug string in JSON format using Walker with JsonWriter for given zserio object.
     * <p>
     * This function allows setting of the walk filter.
     * <p>
     * The following example shows filtering of arrays up to 5 elements:
     * <blockquote><pre>
     * import zserio.runtime.DebugStringUtil;
     * import zserio.runtime.walker.ArrayLengthWalkFilter;
     *
     * final SomeZserioObject zserioObject = new SomeZserioObject();
     * final ArrayLengthWalkFilter walkFilter = new ArrayLengthWalkFilter(5);
     * System.out.println(DebugStringUtil.toJsonString(zserioObject, walkFilter));
     * </pre></blockquote>
     *
     * @param zserioObject Zserio object to use.
     * @param walkFilter WalkFilter to use by Walker.
     *
     * @return JSON debug string.
     */
    public static String toJsonString(Object zserioObject, WalkFilter walkFilter)
    {
        return toJsonString(zserioObject, DEFAULT_INDENT, walkFilter);
    }

    /**
     * Gets debug string in JSON format using Walker with JsonWriter for given zserio object.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.DebugStringUtil;
     *
     * final SomeZserioObject zserioObject = new SomeZserioObject();
     * System.out.println(DebugStringUtil.toJsonString(zserioObject));
     * </pre></blockquote>
     *
     * @param zserioObject Zserio object to use.
     *
     * @return JSON debug string.
     */
    public static String toJsonString(Object zserioObject)
    {
        return toJsonString(zserioObject, DEFAULT_INDENT);
    }

    /**
     * Writes contents of given zserio object to debug file in JSON format using Walker with JsonWriter.
     * <p>
     * This function allows setting of indentation of JSON output together with the walk filter.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.DebugStringUtil;
     * import zserio.runtime.walker.ArrayLengthWalkFilter;
     *
     * final SomeZserioObject zserioObject = new SomeZserioObject();
     * final int indent = 4;
     * final ArrayLengthWalkFilter walkFilter = new ArrayLengthWalkFilter(5);
     * DebugStringUtil.toJsonFile(zserioObject, "FileName.json", indent, walkFilter);
     * </pre></blockquote>
     *
     * @param zserioObject Zserio object to use.
     * @param fileName Name of file to write.
     * @param indent Indent argument for JsonWriter.
     * @param walkFilter WalkFilter to use by Walker.
     *
     * @throws IOException When the output file cannot be created correctly.
     */
    public static void toJsonFile(Object zserioObject, String fileName, int indent, WalkFilter walkFilter)
            throws IOException
    {
        try (
            final OutputStream outputStream = new FileOutputStream(fileName);
            final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        )
        {
            toJsonStream(zserioObject, writer, indent, walkFilter);
        }
    }

    /**
     * Writes contents of given zserio object to debug file in JSON format using Walker with JsonWriter.
     * <p>
     * This function allows setting of indentation of JSON output.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.DebugStringUtil;
     *
     * final SomeZserioObject zserioObject = new SomeZserioObject();
     * final int indent = 4;
     * DebugStringUtil.toJsonFile(zserioObject, "FileName.json", indent);
     * </pre></blockquote>
     *
     * @param zserioObject Zserio object to use.
     * @param fileName Name of file to write.
     * @param indent Indent argument for JsonWriter.
     *
     * @throws IOException When the output file cannot be created correctly.
     */
    public static void toJsonFile(Object zserioObject, String fileName, int indent) throws IOException
    {
        toJsonFile(zserioObject, fileName, indent, new DefaultWalkFilter());
    }

    /**
     * Writes contents of given zserio object to debug file in JSON format using Walker with JsonWriter.
     * <p>
     * This function allows setting of the walk filter.
     * <p>
     * The following example shows filtering of arrays up to 5 elements:
     * <blockquote><pre>
     * import zserio.runtime.DebugStringUtil;
     * import zserio.runtime.walker.ArrayLengthWalkFilter;
     *
     * final SomeZserioObject zserioObject = new SomeZserioObject();
     * final ArrayLengthWalkFilter walkFilter = new ArrayLengthWalkFilter(5);
     * DebugStringUtil.toJsonFile(zserioObject, "FileName.json", walkFilter);
     * </pre></blockquote>
     *
     * @param zserioObject Zserio object to use.
     * @param fileName Name of file to write.
     * @param walkFilter WalkFilter to use by Walker.
     *
     * @throws IOException When the output file cannot be created correctly.
     */
    public static void toJsonFile(Object zserioObject, String fileName, WalkFilter walkFilter)
            throws IOException
    {
        toJsonFile(zserioObject, fileName, DEFAULT_INDENT, walkFilter);
    }

    /**
     * Writes contents of given zserio object to debug file in JSON format using Walker with JsonWriter.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.DebugStringUtil;
     *
     * final SomeZserioObject zserioObject = new SomeZserioObject();
     * DebugStringUtil.toJsonFile(zserioObject, "FileName.json");
     * </pre></blockquote>
     *
     * @param zserioObject Zserio object to use.
     * @param fileName Name of file to write.
     *
     * @throws IOException When the output file cannot be created correctly.
     */
    public static void toJsonFile(Object zserioObject, String fileName) throws IOException
    {
        toJsonFile(zserioObject, fileName, DEFAULT_INDENT);
    }

    /**
     * Parses JSON debug string from given text stream and creates instance of the requested zserio object.
     * <p>
     * The created zserio object is filled according to the data contained in the debug string.
     * <p>
     * Note that the created object can be only partially initialized depending on the data stored in the
     * JSON debug string.
     * <p>
     * Example:
     * <blockquote><pre>
     * import java.io.StringReader;
     * import zserio.runtime.DebugStringUtil;
     *
     * final Reader reader = new StringReader("{}");
     * final Object zserioObject = DebugStringUtil.fronJsonStream(SomeZserioObject.typeInfo(), reader);
     * </pre></blockquote>
     *
     * @param typeInfo Type info of the generated zserio object to create.
     * @param reader Text stream to use.
     * @param arguments Arguments of the generated zserio object.
     *
     * @return Instance of the requested zserio object.
     */
    public static Object fromJsonStream(TypeInfo typeInfo, Reader reader, Object... arguments)
    {
        final JsonReader jsonReader = new JsonReader(reader);
        return jsonReader.read(typeInfo, arguments);
    }

    /**
     * Parses JSON debug string from given text stream and creates instance of the requested zserio object.
     * <p>
     * The created zserio object is filled according to the data contained in the debug string.
     * <p>
     * Note that the created object can be only partially initialized depending on the data stored in the
     * JSON debug string.
     * <p>
     * Example:
     * <blockquote><pre>
     * import java.io.StringReader;
     * import zserio.runtime.DebugStringUtil;
     *
     * final Reader reader = new StringReader("{}");
     * final Object zserioObject = DebugStringUtil.fronJsonStream(SomeZserioObject.class, reader);
     * </pre></blockquote>
     *
     * @param zserioClass Class instance of the generated zserio object to create.
     * @param reader Text stream to use.
     * @param arguments Arguments of the generated zserio object.
     *
     * @return Instance of the requested zserio object.
     */
    public static Object fromJsonStream(Class<?> zserioClass, Reader reader, Object... arguments)
    {
        return fromJsonStream(getTypeInfo(zserioClass), reader, arguments);
    }

    /**
     * Parses JSON debug string and creates instance of the requested zserio object.
     * <p>
     * The created zserio object is filled according to the data contained in the debug string.
     * <p>
     * Note that the created object can be only partially initialized depending on the data stored in the
     * JSON debug string.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.DebugStringUtil;
     *
     * final Object zserioObject = DebugStringUtil.fronJsonStream(SomeZserioObject.typeInfo(), "{}");
     * </pre></blockquote>
     *
     * @param typeInfo Type info of the generated zserio object to create.
     * @param jsonString JSON debug string to parse.
     * @param arguments Arguments of the generated zserio object.
     *
     * @return Instance of the requested zserio object.
     */
    public static Object fromJsonString(TypeInfo typeInfo, String jsonString, Object... arguments)
    {
        final Reader reader = new StringReader(jsonString);
        return fromJsonStream(typeInfo, reader, arguments);
    }

    /**
     * Parses JSON debug string and creates instance of the requested zserio object.
     * <p>
     * The created zserio object is filled according to the data contained in the debug string.
     * <p>
     * Note that the created object can be only partially initialized depending on the data stored in the
     * JSON debug string.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.DebugStringUtil;
     *
     * final Object zserioObject = DebugStringUtil.fronJsonStream(SomeZserioObject.class, "{}");
     * </pre></blockquote>
     *
     * @param zserioClass Class instance of the generated zserio object to create.
     * @param jsonString JSON debug string to parse.
     * @param arguments Arguments of the generated zserio object.
     *
     * @return Instance of the requested zserio object.
     */
    public static Object fromJsonString(Class<?> zserioClass, String jsonString, Object... arguments)
    {
        return fromJsonString(getTypeInfo(zserioClass), jsonString, arguments);
    }

    /**
     * Parses JSON debug file and creates instance of the requested zserio object.
     * <p>
     * The created zserio object is filled according to the data contained in the debug string.
     * <p>
     * Note that the created object can be only partially initialized depending on the data stored in the
     * JSON debug file.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.DebugStringUtil;
     *
     * final Object zserioObject = DebugStringUtil.fronJsonStream(SomeZserioObject.typeInfo(), "{}");
     * </pre></blockquote>
     *
     * @param typeInfo Type info of the generated zserio object to create.
     * @param fileName Name of the JSON debug file.
     * @param arguments Arguments of the generated zserio object.
     *
     * @return Instance of the requested zserio object.
     *
     * @throws FileNotFoundException If given JSON debug file name does not exist.
     */
    public static Object fromJsonFile(TypeInfo typeInfo, String fileName, Object... arguments)
            throws FileNotFoundException
    {
        final InputStream inputStream = new FileInputStream(fileName);
        final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return fromJsonStream(typeInfo, reader, arguments);
    }

    /**
     * Parses JSON debug file and creates instance of the requested zserio object.
     * <p>
     * The created zserio object is filled according to the data contained in the debug string.
     * <p>
     * Note that the created object can be only partially initialized depending on the data stored in the
     * JSON debug file.
     * <p>
     * Example:
     * <blockquote><pre>
     * import zserio.runtime.DebugStringUtil;
     *
     * final Object zserioObject = DebugStringUtil.fronJsonStream(SomeZserioObject.class, "{}");
     * </pre></blockquote>
     *
     * @param zserioClass Class instance of the generated zserio object to create.
     * @param fileName Name of the JSON debug file.
     * @param arguments Arguments of the generated zserio object.
     *
     * @return Instance of the requested zserio object.
     *
     * @throws FileNotFoundException If given JSON debug file name does not exist.
     */
    public static Object fromJsonFile(Class<?> zserioClass, String fileName, Object... arguments)
            throws FileNotFoundException
    {
        return fromJsonFile(getTypeInfo(zserioClass), fileName, arguments);
    }

    private static TypeInfo getTypeInfo(Class<?> zserioClass)
    {
        try
        {
            final Method typeInfoMethod = zserioClass.getMethod("typeInfo");
            if (!typeInfoMethod.getReturnType().equals(TypeInfo.class))
                throw new ZserioError("DebugStringUtil: Zserio object has wrong typeInfo method!");

            return (TypeInfo)typeInfoMethod.invoke(null);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                InvocationTargetException e)
        {
            throw new ZserioError("DebugStringUtil: Zserio object must have type info enabled " +
                    "(see zserio option -withTypeInfoCode)!");
        }
    }

    private static final int DEFAULT_INDENT = 4;
}
