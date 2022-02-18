#include "gtest/gtest.h"

#include "functions/structure_string/TestStructure.h"
#include "functions/structure_string/StringPool.h"

using namespace zserio::literals;

namespace functions
{
namespace structure_string
{

TEST(StructureStringTest, getPoolConst)
{
    TestStructure testStructure;
    ASSERT_EQ("POOL_CONST"_sv, testStructure.funcGetPoolConst());
}

TEST(StructureStringTest, getPoolFiled)
{
    TestStructure testStructure;
    ASSERT_EQ("POOL_FIELD"_sv, testStructure.funcGetPoolField());
}

TEST(StructureStringTest, getConst)
{
    TestStructure testStructure;
    ASSERT_EQ("CONST"_sv, testStructure.funcGetConst());
}

TEST(StructureStringTest, getField)
{
    TestStructure testStructure;
    ASSERT_EQ("FIELD"_sv, testStructure.funcGetField());
}

} // namespace structure_string
} // namespace functions
