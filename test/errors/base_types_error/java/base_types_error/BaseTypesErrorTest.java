package base_types_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class BaseTypesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void bitfieldCyclicDefinition()
    {
        final String error = ":4:11: Cyclic dependency detected in expression evaluation!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfieldInt33LengthType()
    {
        final String error =
                ":6:9: Invalid length expression for bitfield. Length cannot exceed 32-bits integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfieldInvalidLengthType()
    {
        final String error = ":6:9: Invalid length expression for bitfield. Length must be integer!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfieldLengthFieldNotAvailable()
    {
        final String error = ":8:9: Field 'prevBitfieldLength' is not available!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfieldUInt64LengthType()
    {
        final String error =
                ":6:9: Invalid length expression for bitfield. Length cannot exceed 32-bits integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfieldUnknownLength()
    {
        final String error = ":5:9: Unresolved symbol in 'unknownLength' expression!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfield0()
    {
        final String error = ":5:9: Invalid length '0' for bitfield. Length must be within range [1,63]!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfield64()
    {
        final String error = ":5:9: Invalid length '64' for bitfield. Length must be within range [1,63]!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void intfield0()
    {
        final String error = ":5:9: Invalid length '0' for bitfield. Length must be within range [1,64]!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void intfield65()
    {
        final String error = ":5:9: Invalid length '65' for bitfield. Length must be within range [1,64]!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
