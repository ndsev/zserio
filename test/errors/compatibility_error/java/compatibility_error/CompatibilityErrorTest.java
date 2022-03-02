package compatibility_error;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class CompatibilityErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void packedArrayInTemplate240()
    {
        final String errors[] = {
            "packed_array_in_template_240_error.zs:1:30: " +
                    "Root package requires compatibility with version '2.4.0'!",
            "packed_array_in_template_240_error.zs:7:14: " +
                    "Packed arrays binary encoding has been changed in version '2.5.0'!",
            "Java Generator: Compatibility check failed!"
        };

        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void packedCompoundArray242()
    {
        final String errors[] = {
            "packed_compound_array_242_error.zs:1:30: " +
                    "Root package requires compatibility with version '2.4.2'!",
            "packed_compound_array_242_error.zs:16:25: " +
                    "Packed arrays binary encoding has been changed in version '2.5.0'!",
            "Java Generator: Compatibility check failed!"
        };

        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void packedUInt32Array241()
    {
        final String errors[] = {
            "packed_uint32_array_241_error.zs:1:30: " +
                    "Root package requires compatibility with version '2.4.1'!",
            "packed_uint32_array_241_error.zs:7:19: " +
                    "Packed arrays binary encoding has been changed in version '2.5.0'!",
            "Java Generator: Compatibility check failed!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void versionLessThanMinSupported()
    {
        final String error = "version_less_than_min_supported_error.zs:1:30: " +
                "Root package specifies unsupported compatibility version '2.3.2', " +
                "minimum supported version is '2.4.0'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongCompatibilityVersionFormat()
    {
        final String error = "wrong_compatibility_version_format_error.zs:2:30: " +
                "Failed to parse version string: '2.5.0-rc1' as a version!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
