#include "gtest/gtest.h"
#include "parameterized_types/parameterized_nested_in_array/Holder.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/SerializeUtil.h"

namespace parameterized_types
{
namespace parameterized_nested_in_array
{

class ParameterizedNestedInArrayTest : public ::testing::Test
{
protected:
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024);
};

TEST_F(ParameterizedNestedInArrayTest, writeRead)
{
    Holder holder({{Element{Parameterized{6}}}}, {{Element{Parameterized{6}}}});

    const auto bitBuffer = zserio::serialize(holder);
    const Holder readHolder = zserio::deserialize<Holder>(bitBuffer);

    ASSERT_EQ(holder, readHolder);
}

TEST_F(ParameterizedNestedInArrayTest, parameterCheckExceptionInArray)
{
    Holder holder({{Element{Parameterized{7}}}}, {{}});
    holder.getElementArray().at(0).getParameterized().initialize(6); // wrong value

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(holder.write(writer), zserio::CppRuntimeException);
}

TEST_F(ParameterizedNestedInArrayTest, parameterCheckExceptionInPackedArray)
{
    Holder holder({{}}, {{Element{Parameterized{7}}}});
    holder.getElementPackedArray().at(0).getParameterized().initialize(6); // wrong value

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(holder.write(writer), zserio::CppRuntimeException);
}

} // namespace parameterized_nested_in_array
} // namespace parameterized_types
