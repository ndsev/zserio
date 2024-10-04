package identifiers_error.reserved_keywords;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class ReservedKeywordsErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void zserioKeywordFieldName()
    {
        final String errors[] = {
                "zserio_keyword_field_name_error.zs:6:11: mismatched input 'varint' expecting {", // ...
                "zserio_keyword_field_name_error.zs:6:11: 'varint' is a reserved keyword!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    private static ZserioErrorOutput zserioErrors;
}
