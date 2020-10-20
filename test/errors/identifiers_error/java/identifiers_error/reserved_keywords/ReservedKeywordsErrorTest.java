package identifiers_error.reserved_keywords;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class ReservedKeywordsErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void javaKeywordFieldName()
    {
        final String error = "java_keyword_field_name_error.zs:6:13: " +
                "'abstract' is a reserved keyword and may not be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cppKeywordFieldName()
    {
        final String error = "cpp_keyword_field_name_error.zs:6:13: " +
                "'auto' is a reserved keyword and may not be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cppKeywordStructName()
    {
        final String error = "cpp_keyword_struct_name_error.zs:3:8: " +
                "'auto' is a reserved keyword and may not be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void pythonKeywordFieldName()
    {
        final String error = "python_keyword_field_name_error.zs:6:13: " +
                "'def' is a reserved keyword and may not be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void zserioKeywordFieldName()
    {
        final String errors[] =
        {
            "zserio_keyword_field_name_error.zs:6:11: mismatched input 'varint' expecting {", // ...
            "zserio_keyword_field_name_error.zs:6:11: 'varint' is a reserved keyword!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    private static ZserioErrors zserioErrors;
}
