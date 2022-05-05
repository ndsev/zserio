package zserio.runtime;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import zserio.runtime.json.JsonWriter;
import zserio.runtime.walker.DefaultWalkFilter;
import zserio.runtime.walker.WalkFilter;
import zserio.runtime.walker.Walker;

/**
 * Zserio debug string utilities.
 *
 * Note that zserio objects must be generated with -withTypeInfoCode zserio option.
 */
public class DebugStringUtil
{
    /**
     * Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.
     *
     * @param zserioObject Zserio object to use.
     * @param writer Writer to use.
     * @param indent Indent argument for JsonWriter.
     * @param walkFilter WalkFilter to use by Walker.
     */
    public static void toJsonStream(Object zserioObject, Writer writer, int indent, WalkFilter walkFilter)
    {
        try (final JsonWriter jsonWriter = new JsonWriter(writer, indent))
        {
            final Walker walker = new Walker(jsonWriter, walkFilter);
            walker.walk(zserioObject);
        }
    }

    /**
     * Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.
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
     *
     * @param zserioObject Zserio object to use.
     * @param fileName Name of file to write.
     * @param indent Indent argument for JsonWriter.
     * @param walkFilter WalkFilter to use by Walker.
     *
     * @throws FileNotFoundException When the output file cannot be created.
     */
    public static void toJsonFile(Object zserioObject, String fileName, int indent, WalkFilter walkFilter)
            throws FileNotFoundException
    {
        final OutputStream outputStream = new FileOutputStream(fileName);
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        toJsonStream(zserioObject, writer, indent, walkFilter);
    }

    /**
     * Writes contents of given zserio object to debug file in JSON format using Walker with JsonWriter.
     *
     * @param zserioObject Zserio object to use.
     * @param fileName Name of file to write.
     * @param indent Indent argument for JsonWriter.
     *
     * @throws FileNotFoundException When the output file cannot be created.
     */
    public static void toJsonFile(Object zserioObject, String fileName, int indent) throws FileNotFoundException
    {
        toJsonFile(zserioObject, fileName, indent, new DefaultWalkFilter());
    }

    /**
     * Writes contents of given zserio object to debug file in JSON format using Walker with JsonWriter.
     *
     * @param zserioObject Zserio object to use.
     * @param fileName Name of file to write.
     * @param walkFilter WalkFilter to use by Walker.
     *
     * @throws FileNotFoundException When the output file cannot be created.
     */
    public static void toJsonFile(Object zserioObject, String fileName, WalkFilter walkFilter)
            throws FileNotFoundException
    {
        toJsonFile(zserioObject, fileName, DEFAULT_INDENT, walkFilter);
    }

    /**
     * Writes contents of given zserio object to debug file in JSON format using Walker with JsonWriter.
     *
     * @param zserioObject Zserio object to use.
     * @param fileName Name of file to write.
     *
     * @throws FileNotFoundException When the output file cannot be created.
     */
    public static void toJsonFile(Object zserioObject, String fileName) throws FileNotFoundException
    {
        toJsonFile(zserioObject, fileName, DEFAULT_INDENT);
    }

    private static final int DEFAULT_INDENT = 4;
}
