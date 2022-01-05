package identifiers_error.reserved_keywords;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class ReservedKeywordsErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void javaKeywordFieldName()
    {
        final String error = "java_keyword_field_name_error.zs:6:13: " +
                "Field 'abstract' clashes with a Java keyword and may not be used as an identifier!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cppKeywordFieldName()
    {
        final String error = "cpp_keyword_field_name_error.zs:6:13: " +
                "Field 'auto' clashes with a C++ keyword and may not be used as an identifier!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cppKeywordFunctionName()
    {
        final String error = "cpp_keyword_function_name_error.zs:6:19: " +
                "Function 'auto' clashes with a C++ keyword and may not be used as an identifier!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cppKeywordStructName()
    {
        final String error = "cpp_keyword_struct_name_error.zs:3:8: " +
                "Symbols defined in a package must start with an upper case letter!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void pythonKeywordFieldName()
    {
        final String error = "python_keyword_field_name_error.zs:6:13: " +
                "Field 'def' clashes with a Python keyword and may not be used as an identifier!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void pythonKeywordParameterName()
    {
        final String error = "python_keyword_parameter_name_error.zs:3:20: " +
                "Parameter 'def' clashes with a Python keyword and may not be used as an identifier!";
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
