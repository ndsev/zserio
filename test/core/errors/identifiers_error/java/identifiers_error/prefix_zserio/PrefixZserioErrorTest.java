package identifiers_error.prefix_zserio;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;
import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class PrefixZserioErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void zserioPackageName()
    {
        final String error = "zserio.zs:1:9: "
                + "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void zserioPrefixPackageName()
    {
        final String error = "zserio_prefix_package_name_error.zs:1:9: "
                + "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void arrayFieldName()
    {
        final String error = "array_field_name_error.zs:6:12: "
                + "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void choiceName()
    {
        final String error = "choice_name_error.zs:9:8: "
                + "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constName()
    {
        final String error = "const_name_error.zs:5:14: "
                + "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void fieldName()
    {
        final String error = "field_name_error.zs:7:12: "
                + "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void functionName()
    {
        final String error = "function_name_error.zs:12:19: "
                + "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void parameterName()
    {
        final String error = "parameter_name_error.zs:8:26: "
                + "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void structName()
    {
        final String error = "struct_name_error.zs:8:8: "
                + "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void subtypeName()
    {
        final String error = "subtype_name_error.zs:5:16: "
                + "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unionName()
    {
        final String error = "union_name_error.zs:8:7: "
                + "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void zserioSubpackageName()
    {
        final String error = "zserio_subpackage_name_error.zs:1:23: "
                + "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
