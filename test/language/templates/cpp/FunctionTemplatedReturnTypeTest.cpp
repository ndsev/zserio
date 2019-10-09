#include "gtest/gtest.h"

#include "templates/function_templated_return_type/FunctionTemplatedReturnType.h"

namespace templates
{
namespace function_templated_return_type
{

TEST(FunctionTemplatedReturnTypeTest, readWrite)
{
    FunctionTemplatedReturnType functionTemplatedReturnType;
    functionTemplatedReturnType.setHasHolder(true);
    functionTemplatedReturnType.setUint32Test(TestStructure_uint32{zserio::NullOpt, Holder_uint32{42}});
    functionTemplatedReturnType.setStringTest(TestStructure_string{zserio::NullOpt,
            Holder_string{std::string{"string"}}});
    functionTemplatedReturnType.setFloatTest(TestStructure_float32{4.2f, zserio::NullOpt});

    zserio::BitStreamWriter writer;
    functionTemplatedReturnType.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    // write first to ensure that initializeChildren is called
    ASSERT_EQ(42, functionTemplatedReturnType.getUint32Test().funcGet());
    ASSERT_EQ(std::string("string"), functionTemplatedReturnType.getStringTest().funcGet());
    ASSERT_EQ(4.2f, functionTemplatedReturnType.getFloatTest().funcGet());

    zserio::BitStreamReader reader(buffer, bufferSize);
    FunctionTemplatedReturnType readFunctionTemplatedReturnType(reader);

    ASSERT_TRUE(functionTemplatedReturnType == readFunctionTemplatedReturnType);
}

} // namespace function_templated_return_type
} // namespace templates
