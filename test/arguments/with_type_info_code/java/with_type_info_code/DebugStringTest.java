package with_type_info_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;

import zserio.runtime.walker.AndWalkFilter;
import zserio.runtime.walker.ArrayLengthWalkFilter;
import zserio.runtime.walker.DepthWalkFilter;
import zserio.runtime.walker.RegexWalkFilter;
import zserio.runtime.walker.Walker;
import zserio.runtime.walker.WalkFilter;
import zserio.runtime.DebugStringUtil;
import zserio.runtime.json.JsonReader;
import zserio.runtime.json.JsonWriter;

public class DebugStringTest
{
    @Test
    public void jsonWriterWithOptionals() throws IOException
    {
        final boolean createdOptionals = true;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets();

        DebugStringUtil.toJsonFile(withTypeInfoCode, JSON_NAME_WITH_OPTIONALS);

        final Object readObject = DebugStringUtil.fromJsonFile(WithTypeInfoCode.class,
                JSON_NAME_WITH_OPTIONALS);
        assertTrue(readObject instanceof WithTypeInfoCode);
        assertEquals(withTypeInfoCode, (WithTypeInfoCode)readObject);
    }

    @Test
    public void jsonWriterWithoutOptionals() throws IOException
    {
        final boolean createdOptionals = false;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets();

        DebugStringUtil.toJsonFile(withTypeInfoCode, JSON_NAME_WITHOUT_OPTIONALS);

        final Object readObject = DebugStringUtil.fromJsonFile(WithTypeInfoCode.class,
                JSON_NAME_WITHOUT_OPTIONALS);
        assertTrue(readObject instanceof WithTypeInfoCode);
        assertEquals(withTypeInfoCode, (WithTypeInfoCode)readObject);
    }

    @Test
    public void jsonWriterWithArrayLengthFilter() throws IOException
    {
        final boolean createdOptionals = true;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets();

        for (int i = 0; i < 11; ++i)
        {
            final String jsonFileName = getJsonNameWithArrayLengthFilter(i);
            final OutputStream outputStream = new FileOutputStream(jsonFileName);
            final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            final WalkFilter walkFilter = new ArrayLengthWalkFilter(i);
            final int indent = 4;
            try (final JsonWriter jsonWriter = new JsonWriter(writer, indent))
            {
                final Walker walker = new Walker(jsonWriter, walkFilter);
                walker.walk(withTypeInfoCode);
            }

            final InputStream inputStream = new FileInputStream(jsonFileName);
            final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            final JsonReader jsonReader = new JsonReader(reader);
            final Object readObject = jsonReader.read(WithTypeInfoCode.typeInfo());
            assertTrue(readObject instanceof WithTypeInfoCode);
            checkWithTypeInfoCodeArrayLength((WithTypeInfoCode)readObject, i);
        }
    }

    @Test
    public void jsonWriterWithDepth0Filter() throws IOException
    {
        final boolean createdOptionals = true;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets();

        final OutputStream outputStream = new FileOutputStream(JSON_NAME_WITH_DEPTH0_FILTER);
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        final WalkFilter walkFilter = new DepthWalkFilter(0);
        final int indent = 4;
        try (final JsonWriter jsonWriter = new JsonWriter(writer, indent))
        {
            final Walker walker = new Walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCode);
        }

        final InputStream inputStream = new FileInputStream(JSON_NAME_WITH_DEPTH0_FILTER);
        final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final JsonReader jsonReader = new JsonReader(reader);
        final Object readObject = jsonReader.read(WithTypeInfoCode.typeInfo());
        assertTrue(readObject instanceof WithTypeInfoCode);
        checkWithTypeInfoCodeDepth0((WithTypeInfoCode)readObject);
    }

    @Test
    public void jsonWriterWithDepth1ArrayLength0Filter() throws IOException
    {
        final boolean createdOptionals = true;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets();

        final OutputStream outputStream = new FileOutputStream(JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER);
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        final ArrayList<WalkFilter> walkFilters = new ArrayList<WalkFilter>();
        walkFilters.add(new DepthWalkFilter(1));
        walkFilters.add(new ArrayLengthWalkFilter(0));
        final WalkFilter walkFilter = new AndWalkFilter(walkFilters);
        final int indent = 4;
        try (final JsonWriter jsonWriter = new JsonWriter(writer, indent))
        {
            final Walker walker = new Walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCode);
        }

        final InputStream inputStream = new FileInputStream(JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER);
        final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final JsonReader jsonReader = new JsonReader(reader);
        final Object readObject = jsonReader.read(WithTypeInfoCode.typeInfo());
        assertTrue(readObject instanceof WithTypeInfoCode);
        checkWithTypeInfoCodeDepth1ArrayLength0((WithTypeInfoCode)readObject);
    }

    @Test
    public void jsonWriterWithDepth5Filter() throws IOException
    {
        final boolean createdOptionals = true;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets();

        final OutputStream outputStream = new FileOutputStream(JSON_NAME_WITH_DEPTH5_FILTER);
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        final WalkFilter walkFilter = new DepthWalkFilter(5);
        final int indent = 4;
        try (final JsonWriter jsonWriter = new JsonWriter(writer, indent))
        {
            final Walker walker = new Walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCode);
        }

        final InputStream inputStream = new FileInputStream(JSON_NAME_WITH_DEPTH5_FILTER);
        final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final JsonReader jsonReader = new JsonReader(reader);
        final Object readObject = jsonReader.read(WithTypeInfoCode.typeInfo());
        assertTrue(readObject instanceof WithTypeInfoCode);
        assertEquals(withTypeInfoCode, (WithTypeInfoCode)readObject);
    }

    @Test
    public void jsonWriterWithRegexFilter() throws IOException
    {
        final boolean createdOptionals = false;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets();

        final OutputStream outputStream = new FileOutputStream(JSON_NAME_WITH_REGEX_FILTER);
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        final WalkFilter walkFilter = new RegexWalkFilter(".*fieldOffset");
        final int indent = 4;
        try (final JsonWriter jsonWriter = new JsonWriter(writer, indent))
        {
            final Walker walker = new Walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCode);
        }

        final InputStream inputStream = new FileInputStream(JSON_NAME_WITH_REGEX_FILTER);
        final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final JsonReader jsonReader = new JsonReader(reader);
        final Object readObject = jsonReader.read(WithTypeInfoCode.typeInfo());
        assertTrue(readObject instanceof WithTypeInfoCode);
        checkWithTypeInfoCodeRegex((WithTypeInfoCode)readObject);
    }

    private void checkWithTypeInfoCodeArrayLength(WithTypeInfoCode withTypeInfoCode, int maxArrayLength)
    {
        assertTrue(withTypeInfoCode.getComplexStruct().getArray().length <= maxArrayLength);
        assertTrue(withTypeInfoCode.getComplexStruct().getArrayWithLen().length <= maxArrayLength);
        assertTrue(withTypeInfoCode.getComplexStruct().getParamStructArray().length <= maxArrayLength);
        for (ParameterizedStruct paramStruct : withTypeInfoCode.getComplexStruct().getParamStructArray())
        {
            assertTrue(paramStruct.getArray().length <= maxArrayLength);
        }
        assertTrue(withTypeInfoCode.getComplexStruct().getDynamicBitFieldArray().length <= maxArrayLength);

        assertTrue(withTypeInfoCode.getParameterizedStruct().getArray().length <= maxArrayLength);
        assertTrue(withTypeInfoCode.getTemplatedParameterizedStruct().getArray().length <= maxArrayLength);
        assertTrue(withTypeInfoCode.getExternArray().length <= maxArrayLength);
        assertTrue(withTypeInfoCode.getImplicitArray().length <= maxArrayLength);
    }

    private void checkWithTypeInfoCodeDepth0(WithTypeInfoCode withTypeInfoCode)
    {
        assertEquals(null, withTypeInfoCode.getSimpleStruct());
        assertEquals(null, withTypeInfoCode.getComplexStruct());
        assertEquals(null, withTypeInfoCode.getParameterizedStruct());
        assertEquals(null, withTypeInfoCode.getRecursiveStruct());
        assertEquals(null, withTypeInfoCode.getRecursiveUnion());
        assertEquals(null, withTypeInfoCode.getRecursiveChoice());
        assertEquals(null, withTypeInfoCode.getSelector());
        assertEquals(null, withTypeInfoCode.getSimpleChoice());
        assertEquals(null, withTypeInfoCode.getTemplatedStruct());
        assertEquals(null, withTypeInfoCode.getTemplatedParameterizedStruct());
        assertEquals(null, withTypeInfoCode.getExternData());
        assertEquals(null, withTypeInfoCode.getExternArray());
        assertEquals(null, withTypeInfoCode.getImplicitArray());
    }

    private void checkWithTypeInfoCodeDepth1ArrayLength0(WithTypeInfoCode withTypeInfoCode)
    {
        assertNotEquals(null, withTypeInfoCode.getSimpleStruct());
        assertEquals(null, withTypeInfoCode.getComplexStruct().getSimpleStruct());
        assertEquals(null, withTypeInfoCode.getComplexStruct().getAnotherSimpleStruct());
        assertEquals(null, withTypeInfoCode.getComplexStruct().getOptionalSimpleStruct());
        assertEquals(null, withTypeInfoCode.getComplexStruct().getArray());
        assertEquals(null, withTypeInfoCode.getComplexStruct().getDynamicBitField());
        assertEquals(null, withTypeInfoCode.getComplexStruct().getDynamicBitFieldArray());
        assertNotEquals(null, withTypeInfoCode.getParameterizedStruct());
        assertNotEquals(null, withTypeInfoCode.getRecursiveStruct());
        assertNotEquals(null, withTypeInfoCode.getRecursiveUnion());
        assertNotEquals(null, withTypeInfoCode.getRecursiveChoice());
        assertNotEquals(null, withTypeInfoCode.getSelector());
        assertNotEquals(null, withTypeInfoCode.getSimpleChoice());
        assertNotEquals(null, withTypeInfoCode.getTemplatedStruct());
        assertNotEquals(null, withTypeInfoCode.getTemplatedParameterizedStruct());
        assertNotEquals(null, withTypeInfoCode.getExternData());
        assertEquals(0, withTypeInfoCode.getExternArray().length);
        assertEquals(0, withTypeInfoCode.getImplicitArray().length);
    }

    private void checkWithTypeInfoCodeRegex(WithTypeInfoCode withTypeInfoCode)
    {
        assertNotEquals(0, withTypeInfoCode.getSimpleStruct().getFieldOffset());
        assertNotEquals(0, withTypeInfoCode.getComplexStruct().getSimpleStruct().getFieldOffset());
        assertNotEquals(0, withTypeInfoCode.getComplexStruct().getAnotherSimpleStruct().getFieldOffset());
    }

    private String getJsonNameWithArrayLengthFilter(int arrayLength)
    {
        return "with_type_info_code_array_length_" + arrayLength + ".json";
    }

    private static final String JSON_NAME_WITH_OPTIONALS = "with_type_info_code_optionals.json";
    private static final String JSON_NAME_WITHOUT_OPTIONALS = "with_type_info_code.json";
    private static final String JSON_NAME_WITH_DEPTH0_FILTER = "with_type_info_code_depth0.json";
    private static final String JSON_NAME_WITH_DEPTH5_FILTER = "with_type_info_code_depth5.json";
    private static final String JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER =
            "with_type_info_code_depth1_array_length0.json";
    private static final String JSON_NAME_WITH_REGEX_FILTER = "with_type_info_code_regex.json";
}
