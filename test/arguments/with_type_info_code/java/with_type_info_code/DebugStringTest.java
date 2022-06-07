package with_type_info_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedReader;

import zserio.runtime.walker.AndWalkFilter;
import zserio.runtime.walker.ArrayLengthWalkFilter;
import zserio.runtime.walker.DepthWalkFilter;
import zserio.runtime.walker.RegexWalkFilter;
import zserio.runtime.walker.Walker;
import zserio.runtime.walker.WalkFilter;
import zserio.runtime.json.JsonWriter;

public class DebugStringTest
{
    @Test
    public void jsonWriterWithOptionals() throws IOException
    {
        final boolean createdOptionals = true;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets(0);

        final OutputStream outputStream = new FileOutputStream(JSON_NAME_WITH_OPTIONALS);
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        final int indent = 4;
        try (final JsonWriter jsonWriter = new JsonWriter(writer, indent))
        {
            final Walker walker = new Walker(jsonWriter);
            walker.walk(withTypeInfoCode);
        }
        checkJsonFile(JSON_NAME_WITH_OPTIONALS);
    }

    @Test
    public void jsonWriterWithoutOptionals() throws IOException
    {
        final boolean createdOptionals = false;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets(0);

        final OutputStream outputStream = new FileOutputStream(JSON_NAME_WITHOUT_OPTIONALS);
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        final int indent = 4;
        try (final JsonWriter jsonWriter = new JsonWriter(writer, indent))
        {
            final Walker walker = new Walker(jsonWriter);
            walker.walk(withTypeInfoCode);
        }
        checkJsonFile(JSON_NAME_WITHOUT_OPTIONALS);
    }

    @Test
    public void jsonWriterWithArrayLengthFilter() throws IOException
    {
        final boolean createdOptionals = true;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets(0);

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
            checkJsonFile(jsonFileName);
        }
    }

    @Test
    public void jsonWriterWithDepth0Filter() throws IOException
    {
        final boolean createdOptionals = true;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets(0);

        final OutputStream outputStream = new FileOutputStream(JSON_NAME_WITH_DEPTH0_FILTER);
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        final WalkFilter walkFilter = new DepthWalkFilter(0);
        final int indent = 4;
        try (final JsonWriter jsonWriter = new JsonWriter(writer, indent))
        {
            final Walker walker = new Walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCode);
        }
        checkJsonFile(JSON_NAME_WITH_DEPTH0_FILTER);
    }

    @Test
    public void jsonWriterWithDepth1ArrayLength0Filter() throws IOException
    {
        final boolean createdOptionals = true;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets(0);

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
        checkJsonFile(JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER);
    }

    @Test
    public void jsonWriterWithDepth5Filter() throws IOException
    {
        final boolean createdOptionals = true;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets(0);

        final OutputStream outputStream = new FileOutputStream(JSON_NAME_WITH_DEPTH5_FILTER);
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        final WalkFilter walkFilter = new DepthWalkFilter(5);
        final int indent = 4;
        try (final JsonWriter jsonWriter = new JsonWriter(writer, indent))
        {
            final Walker walker = new Walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCode);
        }
        checkJsonFile(JSON_NAME_WITH_DEPTH5_FILTER);
    }

    @Test
    public void jsonWriterWithRegexFilter() throws IOException
    {
        final boolean createdOptionals = false;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        withTypeInfoCode.initializeOffsets(0);

        final OutputStream outputStream = new FileOutputStream(JSON_NAME_WITH_REGEX_FILTER);
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        final WalkFilter walkFilter = new RegexWalkFilter(".*fieldOffset");
        final int indent = 4;
        try (final JsonWriter jsonWriter = new JsonWriter(writer, indent))
        {
            final Walker walker = new Walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCode);
        }
        checkJsonFile(JSON_NAME_WITH_REGEX_FILTER);
    }

    private String getJsonNameWithArrayLengthFilter(int arrayLength)
    {
        return "with_type_info_code_array_length_" + arrayLength + ".json";
    }

    private void checkJsonFile(String createdJsonFileName) throws IOException
    {
        final String jsonDataFileName = "data" + File.separator + createdJsonFileName;

        try (final BufferedReader jsonCreatedReader =
                    Files.newBufferedReader(Paths.get(createdJsonFileName),StandardCharsets.UTF_8);
             final BufferedReader jsonExpectedReader =
                    Files.newBufferedReader(Paths.get(jsonDataFileName),StandardCharsets.UTF_8))
        {
            String createdLine;
            String expectedLine;
            do
            {
                createdLine = jsonCreatedReader.readLine();
                expectedLine = jsonExpectedReader.readLine();
                assertEquals(createdLine, expectedLine);
            } while (createdLine != null && expectedLine != null);
        }
    }

    private static final String JSON_NAME_WITH_OPTIONALS = "with_type_info_code_optionals.json";
    private static final String JSON_NAME_WITHOUT_OPTIONALS = "with_type_info_code.json";
    private static final String JSON_NAME_WITH_DEPTH0_FILTER = "with_type_info_code_depth0.json";
    private static final String JSON_NAME_WITH_DEPTH5_FILTER = "with_type_info_code_depth5.json";
    private static final String JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER =
            "with_type_info_code_depth1_array_length0.json";
    private static final String JSON_NAME_WITH_REGEX_FILTER = "with_type_info_code_regex.json";
}
