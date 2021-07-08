#include "gtest/gtest.h"

#include "templates/function_templated_return_type/FunctionTemplatedReturnType.h"

#include "zserio/RebindAlloc.h"

using namespace zserio::literals;

namespace templates
{
namespace function_templated_return_type
{

using allocator_type = FunctionTemplatedReturnType::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;

TEST(FunctionTemplatedReturnTypeTest, readWrite)
{
    FunctionTemplatedReturnType functionTemplatedReturnType;
    functionTemplatedReturnType.setHasHolder(true);
    functionTemplatedReturnType.setUint32Test(TestStructure_uint32{zserio::NullOpt, Holder_uint32{42}});
    functionTemplatedReturnType.setStringTest(TestStructure_string{zserio::NullOpt,
            Holder_string{string_type{"string"}}});
    functionTemplatedReturnType.setFloatTest(TestStructure_float32{4.2f, zserio::NullOpt});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    functionTemplatedReturnType.write(writer);

    // write first to ensure that initializeChildren is called
    ASSERT_EQ(42, functionTemplatedReturnType.getUint32Test().funcGet());
    ASSERT_EQ("string"_sv, functionTemplatedReturnType.getStringTest().funcGet());
    ASSERT_EQ(4.2f, functionTemplatedReturnType.getFloatTest().funcGet());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    FunctionTemplatedReturnType readFunctionTemplatedReturnType(reader);

    ASSERT_TRUE(functionTemplatedReturnType == readFunctionTemplatedReturnType);
}

} // namespace function_templated_return_type
} // namespace templates
