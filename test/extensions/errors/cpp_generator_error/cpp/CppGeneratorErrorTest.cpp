#include "gtest/gtest.h"
#include "test_utils/ZserioErrorOutput.h"

class CppGeneratorErrorTest : public ::testing::Test
{
protected:
    CppGeneratorErrorTest() :
            zserioErrorsWrongSetCppAllocator(
                    "errors/cpp_generator_error", "zserio_log_wrong_set_cpp_allocator.txt"),
            zserioErrorsBitmaskValuesClassClash(
                    "errors/cpp_generator_error", "zserio_log_bitmask_values_class_clash.txt"),
            zserioErrorsSqlTableProviderClassClash(
                    "errors/cpp_generator_error", "zserio_log_sql_table_provider_class_clash.txt"),
            zserioErrorsSqlTableReaderClassClash(
                    "errors/cpp_generator_error", "zserio_log_sql_table_reader_class_clash.txt"),
            zserioErrorsSqlTableRowClassClash(
                    "errors/cpp_generator_error", "zserio_log_sql_table_row_class_clash.txt")
    {}

    const test_utils::ZserioErrorOutput zserioErrorsWrongSetCppAllocator;
    const test_utils::ZserioErrorOutput zserioErrorsBitmaskValuesClassClash;
    const test_utils::ZserioErrorOutput zserioErrorsSqlTableProviderClassClash;
    const test_utils::ZserioErrorOutput zserioErrorsSqlTableReaderClassClash;
    const test_utils::ZserioErrorOutput zserioErrorsSqlTableRowClassClash;
};

TEST_F(CppGeneratorErrorTest, wrongSetCppAllocator)
{
    ASSERT_TRUE(zserioErrorsWrongSetCppAllocator.isPresent(
            "[ERROR] C++11 Generator: The specified option 'setCppAllocator' has unknown allocator 'wrong'!"));
}

TEST_F(CppGeneratorErrorTest, bitmaskValuesClassClash)
{
    ASSERT_TRUE(zserioErrorsBitmaskValuesClassClash.isPresent(
            "bitmask_values_class_clash_error.zs:4:15: "
            "Class name 'Values' generated for bitmask clashes with its inner class 'Values' "
            "generated in C++ code."));
}

TEST_F(CppGeneratorErrorTest, sqlTableProviderClassClash)
{
    ASSERT_TRUE(zserioErrorsSqlTableProviderClassClash.isPresent(
            "sql_table_provider_class_clash_error.zs:4:11: "
            "Class name 'IParameterProvider' generated for SQL table clashes with its inner class "
            "'IParameterProvider' generated in C++ code."));
}

TEST_F(CppGeneratorErrorTest, sqlTableReaderClassClash)
{
    ASSERT_TRUE(zserioErrorsSqlTableReaderClassClash.isPresent(
            "sql_table_reader_class_clash_error.zs:3:11: "
            "Class name 'Reader' generated for SQL table clashes with its inner class 'Reader' "
            "generated in C++ code."));
}

TEST_F(CppGeneratorErrorTest, sqlTableRowClassClash)
{
    ASSERT_TRUE(zserioErrorsSqlTableRowClassClash.isPresent(
            "sql_table_row_class_clash_error.zs:3:11: "
            "Class name 'Row' generated for SQL table clashes with its inner class 'Row' "
            "generated in C++ code."));
}
