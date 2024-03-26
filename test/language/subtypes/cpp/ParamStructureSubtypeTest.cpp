#include <cstdio>
#include <fstream>
#include <string>

#include "gtest/gtest.h"
#include "subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h"
#include "zserio/RebindAlloc.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace subtypes
{
namespace param_structure_subtype
{

using allocator_type = zserio::RebindAlloc<ParameterizedSubtypeStruct::allocator_type, uint8_t>;

template <typename ALLOC>
struct MethodNames
{
    // TODO[Mi-L@]: Since we don't know allocator name provided by user, we use just the fixed substring here
    static constexpr const char* GET_PARAMETERIZED_SUBTYPE_ARRAY = ">& getAnotherParameterizedSubtypeArray()";
    static constexpr const char* GET_PARAMETERIZED_SUBTYPE_ARRAY_CONST =
            ">& getAnotherParameterizedSubtypeArray() const";
    static constexpr const char* SET_PARAMETERIZED_SYBTYPE_ARRAY =
            "void setAnotherParameterizedSubtypeArray(const ::";
    static constexpr const char* SET_PARAMETERIZED_SYBTYPE_ARRAY_RVALUE =
            "void setAnotherParameterizedSubtypeArray(::";
};

template <>
struct MethodNames<zserio::pmr::PropagatingPolymorphicAllocator<uint8_t>>
{
    static constexpr const char* GET_PARAMETERIZED_SUBTYPE_ARRAY =
            "::zserio::pmr::vector<::subtypes::param_structure_subtype::AnotherParameterizedSubtype>& "
            "getAnotherParameterizedSubtypeArray()";
    static constexpr const char* GET_PARAMETERIZED_SUBTYPE_ARRAY_CONST =
            "const ::zserio::pmr::vector<::subtypes::param_structure_subtype::AnotherParameterizedSubtype>& "
            "getAnotherParameterizedSubtypeArray() const";
    static constexpr const char* SET_PARAMETERIZED_SYBTYPE_ARRAY =
            "void setAnotherParameterizedSubtypeArray("
            "const ::zserio::pmr::vector<::subtypes::param_structure_subtype::AnotherParameterizedSubtype>& "
            "anotherParameterizedSubtypeArray_)";
    static constexpr const char* SET_PARAMETERIZED_SYBTYPE_ARRAY_RVALUE =
            "void setAnotherParameterizedSubtypeArray("
            "::zserio::pmr::vector<::subtypes::param_structure_subtype::"
            "AnotherParameterizedSubtype>&& anotherParameterizedSubtypeArray_)";
};

template <>
struct MethodNames<::std::allocator<uint8_t>>
{
    static constexpr const char* GET_PARAMETERIZED_SUBTYPE_ARRAY =
            "::zserio::vector<::subtypes::param_structure_subtype::AnotherParameterizedSubtype>& "
            "getAnotherParameterizedSubtypeArray()";
    static constexpr const char* GET_PARAMETERIZED_SUBTYPE_ARRAY_CONST =
            "const ::zserio::vector<::subtypes::param_structure_subtype::AnotherParameterizedSubtype>& "
            "getAnotherParameterizedSubtypeArray() const";
    static constexpr const char* SET_PARAMETERIZED_SYBTYPE_ARRAY =
            "void setAnotherParameterizedSubtypeArray("
            "const ::zserio::vector<::subtypes::param_structure_subtype::"
            "AnotherParameterizedSubtype>& anotherParameterizedSubtypeArray_)";
    static constexpr const char* SET_PARAMETERIZED_SYBTYPE_ARRAY_RVALUE =
            "void setAnotherParameterizedSubtypeArray(::zserio::vector<::subtypes::param_structure_subtype::"
            "AnotherParameterizedSubtype>&& anotherParameterizedSubtypeArray_)";
};

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
} // namespace

TEST(ParamStructureSubtypeTest, testSubtype)
{
    // just check that Another/ParameterizedSubtype is defined and that it's same as the ParameterizedStruct
    ParameterizedSubtypeStruct subtypeStruct;
    ParameterizedSubtype& parameterizedSubtype = subtypeStruct.getParameterizedSubtype();
    ParameterizedStruct& parameterizedStruct = subtypeStruct.getParameterizedSubtype();
    const ::zserio::vector<AnotherParameterizedSubtype>& anotherParameterizedSubtypeArray =
            subtypeStruct.getAnotherParameterizedSubtypeArray();
    const ::zserio::vector<ParameterizedStruct>& parameterizedStructArray =
            subtypeStruct.getAnotherParameterizedSubtypeArray();
    ASSERT_EQ(&parameterizedSubtype, &parameterizedStruct);
    ASSERT_EQ(&anotherParameterizedSubtypeArray, &parameterizedStructArray);

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
            "const ::subtypes::param_structure_subtype::ParameterizedSubtype& "
            "getParameterizedSubtype() const"));
    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            "void setParameterizedSubtype"
            "(const ::subtypes::param_structure_subtype::ParameterizedSubtype& parameterizedSubtype_)"));

    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            MethodNames<allocator_type>::GET_PARAMETERIZED_SUBTYPE_ARRAY));
    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            MethodNames<allocator_type>::GET_PARAMETERIZED_SUBTYPE_ARRAY_CONST));
    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            MethodNames<allocator_type>::SET_PARAMETERIZED_SYBTYPE_ARRAY));
    ASSERT_TRUE(isCodeInFilePresent(
            "language/subtypes/gen/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h",
            MethodNames<allocator_type>::SET_PARAMETERIZED_SYBTYPE_ARRAY_RVALUE));
}

} // namespace param_structure_subtype
} // namespace subtypes
