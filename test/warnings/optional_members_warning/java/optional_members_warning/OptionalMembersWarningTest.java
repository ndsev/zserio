package optional_members_warning;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class OptionalMembersWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioErrorOutput();
    }

    @Test
    public void optionalReferencesInArrayLength()
    {
        String warning = "optional_references_in_array_length.zs:9:11: Field 'array2' is not optional "
                + "and contains reference to optional field 'arrayLength' in array length.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_array_length.zs:10:20: Field 'array3' is not optional "
                + "and contains reference to optional field 'arrayLength' in array length.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void optionalReferencesInBitfieldLength()
    {
        String warning = "optional_references_in_bitfield_length.zs:9:18: Field 'bitfield2' is not optional "
                + "and contains reference to optional field 'numBits' in dynamic bitfield length.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_bitfield_length.zs:10:27: Field 'bitfield3' is not optional "
                + "and contains reference to optional field 'numBits' in dynamic bitfield length.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void optionalReferencesInConstraint()
    {
        String warning = "optional_references_in_constraint.zs:9:11: Field 'value3' is not optional "
                + "and contains reference to optional field 'value1' in constraint.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_constraint.zs:10:20: Field 'value4' is not optional "
                + "and contains reference to optional field 'value1' in constraint.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_constraint.zs:15:11: Field 'anotherValue' has different optional "
                + "condition than field 'anotherValue' referenced in constraint.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void optionalReferencesInOffset()
    {
        String warning = "optional_references_in_offset.zs:22:11: Field 'value2' is not optional "
                + "and contains reference to optional field 'optionalOffset' in offset.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_offset.zs:26:11: Field 'value3' is not optional "
                + "and contains reference to optional field 'offsetHolder' in offset.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_offset.zs:30:11: Field 'value4' is not optional "
                + "and contains reference to optional field 'offset' in offset.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void optionalReferencesInOptionalClause()
    {
        String warning = "optional_references_in_optional_clause.zs:14:11: Field 'value7' does not have left "
                + "'and' condition of optional field 'value1' referenced in optional clause.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_optional_clause.zs:15:11: Field 'value8' does not have left "
                + "'and' condition of optional field 'value1' referenced in optional clause.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_optional_clause.zs:16:11: Field 'value9' does not have left "
                + "'and' condition of optional field 'value1' referenced in optional clause.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void optionalReferencesInTypeArguments()
    {
        String warning =
                "optional_references_in_type_arguments.zs:34:31: Field 'blackTonesArray1' is not optional "
                + "and contains reference to optional field 'numBlackTones' in type arguments.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_type_arguments.zs:35:39: Field 'blackTonesArray2' is not optional "
                + "and contains reference to optional field 'numBlackTones' in type arguments.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_type_arguments.zs:37:31: Field 'blackTones1' is not optional "
                + "and contains reference to optional field 'numBlackTones' in type arguments.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_type_arguments.zs:38:39: Field 'blackTones2' is not optional "
                + "and contains reference to optional field 'numBlackTones' in type arguments.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_type_arguments.zs:40:35: Field 'autoBlackTonesArray1' is not "
                + "optional and contains reference to optional field 'autoNumBlackTones' in "
                + "type arguments.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_type_arguments.zs:41:43: Field 'autoBlackTonesArray2' is not "
                + "optional and contains reference to optional field 'autoNumBlackTones' in "
                + "type arguments.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_type_arguments.zs:43:35: Field 'autoBlackTones1' is not optional "
                + "and contains reference to optional field 'autoNumBlackTones' in type arguments.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_type_arguments.zs:44:43: Field 'autoBlackTones2' is not optional "
                + "and contains reference to optional field 'autoNumBlackTones' in type arguments.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_type_arguments.zs:46:47: Field 'blackAndWhiteTones' "
                + "has different optional condition than field 'numWhiteTones' referenced in type arguments.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_type_arguments.zs:47:51: Field 'mixedTones' "
                +
                "has different optional condition than field 'autoNumBlackTones' referenced in type arguments.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_type_arguments.zs:48:44: Field 'mixedTonesArray' is not optional "
                + "and contains reference to optional field 'autoNumBlackTones' in type arguments.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    private static ZserioErrorOutput zserioWarnings;
}
