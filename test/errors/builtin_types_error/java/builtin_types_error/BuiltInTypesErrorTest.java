package builtin_types_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class BuiltInTypesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void bitfieldCyclicDefinition()
    {
        final String error = "bitfield_cyclic_definition_error.zs:3:11: Cyclic dependency detected in "
                + "expression evaluation!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfieldInvalidLengthType()
    {
        final String error = "bitfield_invalid_length_type_error.zs:6:9: Invalid length expression for "
                + "bit field. Length must be integer!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfieldLengthFieldNotAvailable()
    {
        final String error = "bitfield_length_field_not_available_error.zs:8:9: "
                + "Unresolved symbol 'prevBitfieldLength' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfieldUnknownLength()
    {
        final String error = "bitfield_unknown_length_error.zs:5:9: "
                + "Unresolved symbol 'unknownLength' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfieldWithoutArg()
    {
        final String error = "bitfield_without_arg_error.zs:5:5: Missing length for the dynamic bitfield type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfield0()
    {
        final String error = "bitfield0_error.zs:5:9: Invalid length '0' for the dynamic bit field. "
                + "Length must be within range [1,64]!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitfield65()
    {
        final String error = "bitfield65_error.zs:5:9: Invalid length '65' for the dynamic bit field. "
                + "Length must be within range [1,64]!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void boolWithBitfieldArg()
    {
        final String error =
                "bool_with_bitfield_arg_error.zs:5:5: Referenced type 'bool' is not a dynamic bit field type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void intfieldWithoutArg()
    {
        final String error = "intfield_without_arg_error.zs:5:5: Missing length for the dynamic bitfield type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void intfield0()
    {
        final String error = "intfield0_error.zs:5:9: Invalid length '0' for the dynamic bit field. "
                + "Length must be within range [1,64]!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void intfield65()
    {
        final String error = "intfield65_error.zs:5:9: Invalid length '65' for the dynamic bit field. "
                + "Length must be within range [1,64]!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
