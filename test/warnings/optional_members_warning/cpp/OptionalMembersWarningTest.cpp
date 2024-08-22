#include <string>
#include <vector>

#include "gtest/gtest.h"
#include "test_utils/ZserioErrorOutput.h"

class OptionalMembersWarningTest : public ::testing::Test
{
protected:
    OptionalMembersWarningTest() :
            zserioWarnings("warnings/optional_members_warning")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(OptionalMembersWarningTest, optionalReferencesInArrayLength)
{
    const std::string warning1 =
            "optional_references_in_array_length.zs:9:11: Field 'array2' is not optional "
            "and contains reference to optional field 'arrayLength' in array length.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning1));

    const std::string warning2 =
            "optional_references_in_array_length.zs:10:20: Field 'array3' is optional and contains "
            "reference to another optional field 'arrayLength' in array length which might not be present.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning2));
}

TEST_F(OptionalMembersWarningTest, optionalReferencesInBitfieldLength)
{
    const std::string warning1 =
            "optional_references_in_bitfield_length.zs:9:18: Field 'bitfield2' is not optional "
            "and contains reference to optional field 'numBits' in dynamic bitfield length.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning1));

    const std::string warning2 =
            "optional_references_in_bitfield_length.zs:10:27: Field 'bitfield3' is optional and contains "
            "reference to another optional field 'numBits' in dynamic bitfield length which might not be "
            "present.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning2));
}

TEST_F(OptionalMembersWarningTest, optionalReferencesInConstraint)
{
    const std::string warning1 =
            "optional_references_in_constraint.zs:9:11: Field 'value3' is not optional "
            "and contains reference to optional field 'value1' in constraint.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning1));

    const std::string warning2 =
            "optional_references_in_constraint.zs:10:20: Field 'value4' is optional and contains "
            "reference to another optional field 'value1' in constraint which might not be present.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning2));

    const std::string warning3 =
            "optional_references_in_constraint.zs:15:11: Field 'anotherValue' has different optional "
            "condition than field 'anotherValue' referenced in constraint.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning3));
}

TEST_F(OptionalMembersWarningTest, optionalReferencesInOffset)
{
    const std::string warning1 =
            "optional_references_in_offset.zs:22:11: Field 'value2' is not optional "
            "and contains reference to optional field 'optionalOffset' in offset.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning1));

    const std::string warning2 =
            "optional_references_in_offset.zs:26:11: Field 'value3' is not optional "
            "and contains reference to optional field 'offsetHolder' in offset.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning2));

    const std::string warning3 =
            "optional_references_in_offset.zs:30:11: Field 'value4' is not optional "
            "and contains reference to optional field 'offset' in offset.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning3));

    const std::string warning4 =
            "optional_references_in_offset.zs:38:21: Field 'value6' is optional and contains reference to "
            "another optional field 'anotherOptionalOffset' in offset which might not be present.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning4));
}

TEST_F(OptionalMembersWarningTest, optionalReferencesInOptionalClause)
{
    const std::string warning1 =
            "optional_references_in_optional_clause.zs:14:11: Field 'value7' does not have left "
            "'and' condition of optional field 'value1' referenced in optional clause.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning1));

    const std::string warning2 =
            "optional_references_in_optional_clause.zs:15:11: Field 'value8' does not have left "
            "'and' condition of optional field 'value1' referenced in optional clause.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning2));

    const std::string warning3 =
            "optional_references_in_optional_clause.zs:16:11: Field 'value9' does not have left "
            "'and' condition of optional field 'value1' referenced in optional clause.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning3));
}

TEST_F(OptionalMembersWarningTest, optionalReferencesInTypeArguments)
{
    const std::string warning1 =
            "optional_references_in_type_arguments.zs:34:31: Field 'blackTonesArray1' is not optional "
            "and contains reference to optional field 'numBlackTones' in type arguments.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning1));

    const std::string warning2 =
            "optional_references_in_type_arguments.zs:35:39: Field 'blackTonesArray2' is not optional "
            "and contains reference to optional field 'numBlackTones' in type arguments.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning2));

    const std::string warning3 =
            "optional_references_in_type_arguments.zs:37:31: Field 'blackTones1' is not optional "
            "and contains reference to optional field 'numBlackTones' in type arguments.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning3));

    const std::string warning4 =
            "optional_references_in_type_arguments.zs:38:39: Field 'blackTones2' is not optional "
            "and contains reference to optional field 'numBlackTones' in type arguments.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning4));

    const std::string warning5 =
            "optional_references_in_type_arguments.zs:40:35: Field 'autoBlackTonesArray1' is not "
            "optional and contains reference to optional field 'autoNumBlackTones' in "
            "type arguments.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning5));

    const std::string warning6 =
            "optional_references_in_type_arguments.zs:41:43: Field 'autoBlackTonesArray2' is not "
            "optional and contains reference to optional field 'autoNumBlackTones' in "
            "type arguments.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning6));

    const std::string warning7 =
            "optional_references_in_type_arguments.zs:43:35: Field 'autoBlackTones1' is not optional "
            "and contains reference to optional field 'autoNumBlackTones' in type arguments.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning7));

    const std::string warning8 =
            "optional_references_in_type_arguments.zs:44:43: Field 'autoBlackTones2' is not optional "
            "and contains reference to optional field 'autoNumBlackTones' in type arguments.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning8));

    const std::string warning9 =
            "optional_references_in_type_arguments.zs:46:47: Field 'blackAndWhiteTones' "
            "has different optional condition than field 'numWhiteTones' referenced in type arguments.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning9));

    const std::string warning10 =
            "optional_references_in_type_arguments.zs:47:51: Field 'mixedTones' is optional and contains "
            "reference to another optional field 'autoNumBlackTones' in type arguments which might not "
            "be present.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning10));

    const std::string warning11 =
            "optional_references_in_type_arguments.zs:48:44: Field 'mixedTonesArray' is optional and "
            "contains reference to another optional field 'autoNumBlackTones' in type arguments which "
            "might not be present.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning11));
}
