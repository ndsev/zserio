#include "choice_types/bool_choice_with_default/BoolChoiceWithDefault.h"
#include "gtest/gtest.h"
#include "test_utils/TestUtility.h"

namespace choice_types
{
namespace bool_choice_with_default
{

using AllocatorType = BoolChoiceWithDefault::allocator_type;

class BoolChoiceWithDefaultTest : public ::testing::Test
{
protected:
    static void writeData(zserio::BitStreamWriter& writer, bool /*selector*/, uint8_t value)
    {
        writer.writeBits(value, 8);
    }
};

TEST_F(BoolChoiceWithDefaultTest, constructors)
{
    {
        BoolChoiceWithDefault data;
        ASSERT_THROW(data.getSelector(), zserio::CppRuntimeException);
    }
    {
        BoolChoiceWithDefault data = {};
        ASSERT_THROW(data.getSelector(), zserio::CppRuntimeException);
    }
    {
        BoolChoiceWithDefault data(AllocatorType{});
        ASSERT_THROW(data.getSelector(), zserio::CppRuntimeException);
    }
}

TEST_F(BoolChoiceWithDefaultTest, initialize)
{
    const bool selector = true;
    BoolChoiceWithDefault data;
    data.initialize(selector);
    ASSERT_EQ(selector, data.getSelector());
}

TEST_F(BoolChoiceWithDefaultTest, bitSizeOf)
{
    BoolChoiceWithDefault data;
    data.initialize(true);
    ASSERT_EQ(8, data.bitSizeOf());
}

TEST_F(BoolChoiceWithDefaultTest, comparisonOperators)
{
    BoolChoiceWithDefault data;
    data.initialize(true);
    data.setField(234);
    BoolChoiceWithDefault equalData;
    equalData.initialize(true);
    equalData.setField(234);
    BoolChoiceWithDefault lessThanData;
    lessThanData.initialize(true);
    lessThanData.setField(233);
    test_utils::comparisonOperatorsTest(data, equalData, lessThanData);
}

TEST_F(BoolChoiceWithDefaultTest, writeRead)
{
    BoolChoiceWithDefault data;
    data.initialize(false);
    data.setField(99);
    test_utils::writeReadTest(data, false);
}

TEST_F(BoolChoiceWithDefaultTest, read)
{
    const int8_t value = 99;
    const bool selector = false;
    BoolChoiceWithDefault data;
    data.initialize(selector);
    data.setField(value);

    test_utils::readTest(
            [&](zserio::BitStreamWriter& writer) { writeData(writer, selector, value); }, data, selector);
}

TEST_F(BoolChoiceWithDefaultTest, hash)
{
    BoolChoiceWithDefault data;
    data.initialize(false);
    data.setField(99);
    BoolChoiceWithDefault data2 = data;
    const size_t hashValue = 31586;
    test_utils::hashTest(data, hashValue, data2);
}

} // namespace bool_choice_with_default
} // namespace choice_types
