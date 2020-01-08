package bitmask_types_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class BitmaskTypesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void bitmaskTypeValueError()
    {
        final String error =
                "bitmask_type_value_error.zs:7:18: Bitmask value 'WRITE' is not an integral type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cyclicDefinition()
    {
        final String error =
                "cyclic_definition_error.zs:6:26: Unresolved symbol 'READ' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cyclicDefinitionUsingBitmaskValue()
    {
        final String error = "cyclic_definition_using_constant_error.zs:11:35: " +
                "Cyclic dependency detected in expression evaluation!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cyclicDefinitionUsingConstant()
    {
        final String error = "cyclic_definition_using_bitmask_value_error.zs:7:21: " +
                "'WRITE' undefined in bitmask 'OtherPermissions'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void duplicatedBitmaskValue()
    {
        final String error =
                "duplicated_bitmask_value_error.zs:7:18: Bitmask 'DARK_BLUE' has duplicated value (1)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void duplicatedBitmaskValueName()
    {
        final String errors[] =
        {
            "duplicated_bitmask_value_name_error.zs:6:5:     First defined here",
            "duplicated_bitmask_value_name_error.zs:7:5: 'READ' is already defined in this scope!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void int32Bitmask()
    {
        final String error = "int32_bitmask_error.zs:3:15: " +
                "Bitmask 'IntBitmask' cannot use int32 type! Only unsigned integer types are allowed!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void notAvailableBitmaskValue()
    {
        final String error = "not_available_bitmask_value_error.zs:6:26: " +
                "'BLACK' undefined in bitmask 'NotAvailableBitmaskValue'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void outOfRangeBitmaskExplicitValue()
    {
        final String error = "out_of_range_bitmask_value_error.zs:7:5: " +
                "Bitmask 'WRITE' has value (256) out of range <0,255>!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void outOfRangeBitmaskNegativeValue()
    {
        final String error = "out_of_range_bitmask_negative_value_error.zs:7:18: " +
                "Bitmask value 'WRITE(-1) cannot be negative!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void outOfRangeBitmaskValue()
    {
        final String error = "out_of_range_bitmask_explicit_value_error.zs:7:18: " +
                "Bitmask 'WRITE' has value (256) out of range <0,255>!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void stringBitmaskError()
    {
        final String error = "string_bitmask_error.zs:3:16: " +
                "Bitmask 'StringBitmask' cannot use string type! " +
                "Only unsigned integer types are allowed!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
