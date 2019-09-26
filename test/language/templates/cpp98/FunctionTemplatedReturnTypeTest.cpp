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
    TestStructure_uint32& uint32Test = functionTemplatedReturnType.getUint32Test();
    Holder_uint32 uint32Holder;
    uint32Holder.setValue(42);
    uint32Test.setHolder(uint32Holder);
    TestStructure_string& stringTest = functionTemplatedReturnType.getStringTest();
    Holder_string stringHolder;
    stringHolder.setValue("string");
    stringTest.setHolder(stringHolder);
    TestStructure_float32& floatTest = functionTemplatedReturnType.getFloatTest();
    floatTest.setValue(4.2f);

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
