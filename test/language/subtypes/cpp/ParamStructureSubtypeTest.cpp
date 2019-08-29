#include <cstdio>
#include <string>
#include <fstream>

#include "gtest/gtest.h"

#include "subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h"

namespace subtypes
{
namespace param_structure_subtype
{

namespace
{
    bool isCodeInFilePresent(const char* fileName, const char* code)
    {
        std::ifstream file(fileName);
        bool isPresent = false;
        std::string line;
        while (std::getline(file, line))
        {
            if (line.find(code) != std::string::npos)
            {
                isPresent = true;
                break;
            }
        }
        file.close();

        return isPresent;
    }
}

TEST(ParamStructureSubtypeTest, TestSubtype)
{
    // just check that ParameterizedSubtype is defined and that it's same as the ParameterizedStruct
    ParameterizedSubtypeStruct s;
    ParameterizedSubtype& parameterizedSubtype = s.getParameterizedSubtype();
    ParameterizedStruct& parameterizedStruct = s.getParameterizedSubtype();
    std::vector<ParameterizedSubtype>& parameterizedSubtypeArray = s.getParameterizedSubtypeArray();
    std::vector<ParameterizedStruct>& parameterizedStructArray = s.getParameterizedSubtypeArray();
    ASSERT_EQ(&parameterizedSubtype, &parameterizedStruct);
    ASSERT_EQ(&parameterizedSubtypeArray, &parameterizedStructArray);

    // ensure that include to the subtype is present and that the subtype is used in
    // ParameterizedSubtypeStruct's accessors
    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            "#include <subtypes/param_structure_subtype/ParameterizedSubtype.h>"));

    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            "::subtypes::param_structure_subtype::ParameterizedSubtype& getParameterizedSubtype()"));
    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            "const ::subtypes::param_structure_subtype::ParameterizedSubtype& getParameterizedSubtype() const"));
    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            "void setParameterizedSubtype"
                "(const ::subtypes::param_structure_subtype::ParameterizedSubtype& parameterizedSubtype_)"));

    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            "::std::vector<::subtypes::param_structure_subtype::ParameterizedSubtype>& "
                "getParameterizedSubtypeArray()"));
    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            "const ::std::vector<::subtypes::param_structure_subtype::ParameterizedSubtype>& "
                "getParameterizedSubtypeArray() const"));
    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            "void setParameterizedSubtypeArray(const ::std::vector<::subtypes::param_structure_subtype::"
                "ParameterizedSubtype>& parameterizedSubtypeArray_)"));
    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            "void setParameterizedSubtypeArray(::std::vector<::subtypes::param_structure_subtype::"
                "ParameterizedSubtype>&& parameterizedSubtypeArray_)"));
}

} // namespace param_structure_subtype
} // namespace subtypes
