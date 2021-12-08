#include "gtest/gtest.h"

#include "functions/structure_string/TestStructure.h"
#include "functions/structure_string/StringPool.h"

using namespace zserio::literals;

namespace functions
{
namespace structure_string
{

TEST(FunctionsStructureStringTest, getPoolConst)
{
    TestStructure testStructure;
    ASSERT_EQ("POOL_CONST"_sv, testStructure.funcGetPoolConst());
}

TEST(FunctionsStructureStringTest, getPoolFiled)
{
    TestStructure testStructure;
    ASSERT_EQ("POOL_FIELD", testStructure.funcGetPoolField());
}

TEST(FunctionsStructureStringTest, getConst)
{
    TestStructure testStructure;
    ASSERT_EQ("CONST"_sv, testStructure.funcGetConst());
}

TEST(FunctionsStructureStringTest, getField)
{
    TestStructure testStructure;
    ASSERT_EQ("FIELD", testStructure.funcGetField());
}

} // namespace structure_string
} // namespace functions
