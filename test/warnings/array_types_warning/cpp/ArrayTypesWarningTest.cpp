#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "test_utils/ZserioErrorOutput.h"

class ArrayTypesWarningTest : public ::testing::Test
{
protected:
    ArrayTypesWarningTest()
    :   zserioWarnings("warnings/array_types_warning")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(ArrayTypesWarningTest, packedArrayChoiceHasNoPackableField)
{
    const std::string warning = "packed_array_choice_has_no_packable_field.zs:40:12: "
            "'ChoiceWithoutPackableField' doesn't contain any packable field.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(ArrayTypesWarningTest, packedArrayStructHasNoPackableField)
{
    const std::string warning = "packed_array_struct_has_no_packable_field.zs:48:12: "
            "'StructWithoutPackable' doesn't contain any packable field.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(ArrayTypesWarningTest, packedArrayTemplateHasNoPackableField)
{
    const std::vector<std::string> warnings1 =
    {
        "packed_array_template_has_no_packable_field.zs:22:13: "
                  "    In instantiation of 'Template' required from here",
        "packed_array_template_has_no_packable_field.zs:5:12: 'string' is not packable element type."
    };
    ASSERT_TRUE(zserioWarnings.isPresent(warnings1));

    const std::vector<std::string> warnings2 =
    {
        "packed_array_template_has_no_packable_field.zs:24:13: "
                "    In instantiation of 'Template' required from here",
        "packed_array_template_has_no_packable_field.zs:5:12: "
                "'Unpackable' doesn't contain any packable field."
    };
    ASSERT_TRUE(zserioWarnings.isPresent(warnings2));
}

TEST_F(ArrayTypesWarningTest, packedArrayUnionHasNoPackableField)
{
    const std::string warning = "packed_array_union_has_no_packable_field.zs:25:12: "
            "'UnionWithoutPackableField' doesn't contain any packable field.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(ArrayTypesWarningTest, packedArrayUnpackableBoolElement)
{
    const std::string warning =
            "packed_array_unpackable_bool_element.zs:23:12: 'bool' is not packable element type.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(ArrayTypesWarningTest, packedArrayUnpackableExternElement)
{
    const std::string warning =
            "packed_array_unpackable_extern_element.zs:6:12: 'extern' is not packable element type.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(ArrayTypesWarningTest, packedArrayUnpackableFloatElement)
{
    const std::string warning =
            "packed_array_unpackable_float_element.zs:6:12: 'float64' is not packable element type.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(ArrayTypesWarningTest, packedArrayUnpackableStringElement)
{
    const std::string warning =
            "packed_array_unpackable_string_element.zs:6:12: 'string' is not packable element type.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}
