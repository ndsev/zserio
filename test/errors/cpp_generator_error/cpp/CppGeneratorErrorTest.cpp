#include "gtest/gtest.h"

#include "test_utils/ZserioErrors.h"

class CppGeneratorErrorTest : public ::testing::Test
{
protected:

    CppGeneratorErrorTest()
    :   zserioErrors("errors/cpp_generator_error")
    {}

    const test_utils::ZserioErrors zserioErrors;
};

TEST_F(CppGeneratorErrorTest, wrongSetCppAllocator)
{
    ASSERT_TRUE(zserioErrors.isPresent(
        "[ERROR] C++11 Generator: The specified option 'setCppAllocator' has unknown allocator 'wrong'!"));
}

TEST_F(CppGeneratorErrorTest, bitmaskValuesClassClash)
{
    ASSERT_TRUE(zserioErrors.isPresent(
            "bitmask_values_class_clash_error.zs:4:15: "
            "Class name 'Values' generated for bitmask clashes with its inner class 'Values' "
            "generated in C++ code."));
}

TEST_F(CppGeneratorErrorTest, sqlTableIParameterProviderClassClash)
{
    ASSERT_TRUE(zserioErrors.isPresent(
            "sql_table_i_parameter_provider_class_clash_error.zs:3:11: "
            "Class name 'IParameterProvider' generated for SQL table clashes with its inner class "
            "'IParameterProvider' generated in C++ code."));
}

TEST_F(CppGeneratorErrorTest, sqlTableReaderClassClash)
{
    ASSERT_TRUE(zserioErrors.isPresent(
            "sql_table_reader_class_clash_error.zs:3:11: "
            "Class name 'Reader' generated for SQL table clashes with its inner class 'Reader' "
            "generated in C++ code."));
}

TEST_F(CppGeneratorErrorTest, sqlTableRowClassClash)
{
    ASSERT_TRUE(zserioErrors.isPresent(
            "sql_table_row_class_clash_error.zs:3:11: "
            "Class name 'Row' generated for SQL table clashes with its inner class 'Row' "
            "generated in C++ code."));
}
