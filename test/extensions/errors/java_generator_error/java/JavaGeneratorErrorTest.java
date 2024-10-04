package java_generator_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class JavaGeneratorErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void bitmaskValuesClassClash()
    {
        final String error = "bitmask_values_class_clash_error.zs:4:15: "
                + "Class name 'Values' generated for bitmask clashes with its inner class 'Values' "
                + "generated in Java code.";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void sqlTableParameterProviderClassClash()
    {
        final String error = "sql_table_parameter_provider_class_clash_error.zs:3:11: "
                + "Class name 'ParameterProvider' generated for SQL table clashes with its inner class "
                + "'ParameterProvider' generated in Java code.";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
