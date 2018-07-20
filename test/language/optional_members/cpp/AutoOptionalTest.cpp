#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitFieldArray.h"

#include "optional_members/auto_optional/Container.h"

namespace optional_members
{
namespace auto_optional
{

class AutoOptionalTest : public ::testing::Test
{
protected:
    void checkContainerInBitStream(zserio::BitStreamReader& reader, int32_t nonOptionalIntValue,
            bool hasOptionalIntValue, int32_t optionalIntValue)
    {
        ASSERT_EQ(nonOptionalIntValue, reader.readSignedBits(32));
        if (hasOptionalIntValue)
        {
            ASSERT_TRUE(reader.readBool());
            ASSERT_EQ(optionalIntValue, reader.readSignedBits(32));
        }
        else
        {
            ASSERT_FALSE(reader.readBool());
        }
        reader.setBitPosition(0);
    }

    static const int32_t NON_OPTIONAL_INT_VALUE;
    static const int32_t AUTO_OPTIONAL_INT_VALUE;

    static const size_t CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL;
    static const size_t CONTAINER_BIT_SIZE_WITH_OPTIONAL;
};

const int32_t AutoOptionalTest::NON_OPTIONAL_INT_VALUE = 0x0DEADDED;
const int32_t AutoOptionalTest::AUTO_OPTIONAL_INT_VALUE = 0x0BEEFBEF;

const size_t AutoOptionalTest::CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 32 + 1;
const size_t AutoOptionalTest::CONTAINER_BIT_SIZE_WITH_OPTIONAL = 32 + 1 + 32;

TEST_F(AutoOptionalTest, hasAutoOptionalInt)
{
    Container container;
    container.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
    ASSERT_FALSE(container.hasAutoOptionalInt());

    container.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    ASSERT_TRUE(container.hasAutoOptionalInt());
}

TEST_F(AutoOptionalTest, bitSizeOf)
{
    Container container;
    container.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
    ASSERT_EQ(CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.bitSizeOf());

    container.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    ASSERT_EQ(CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.bitSizeOf());
}

TEST_F(AutoOptionalTest, initializeOffsets)
{
    Container container;
    container.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
    const size_t bitPosition = 1;
    ASSERT_EQ(bitPosition + CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.initializeOffsets(bitPosition));

    container.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    ASSERT_EQ(bitPosition + CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.initializeOffsets(bitPosition));
}

TEST_F(AutoOptionalTest, operatorEquality)
{
    Container container1;
    Container container2;
    ASSERT_TRUE(container1 == container2);

    container1.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
    container1.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    container2.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
    ASSERT_FALSE(container1 == container2);

    container2.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    ASSERT_TRUE(container1 == container2);
}

TEST_F(AutoOptionalTest, hashCode)
{
    Container container1;
    Container container2;
    ASSERT_EQ(container1.hashCode(), container2.hashCode());

    container1.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
    container1.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    container2.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
    ASSERT_NE(container1.hashCode(), container2.hashCode());

    container2.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    ASSERT_EQ(container1.hashCode(), container2.hashCode());
}

TEST_F(AutoOptionalTest, write)
{
    Container container;
    container.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);

    zserio::BitStreamWriter writeNonOptional;
    container.write(writeNonOptional);
    size_t writeNonOptionalBufferByteSize;
    const uint8_t* writeNonOptionalBuffer = writeNonOptional.getWriteBuffer(writeNonOptionalBufferByteSize);
    zserio::BitStreamReader readerNonOptional(writeNonOptionalBuffer, writeNonOptionalBufferByteSize);
    checkContainerInBitStream(readerNonOptional, NON_OPTIONAL_INT_VALUE, false, 0);
    Container readContainerNonOptional(readerNonOptional);
    ASSERT_EQ(NON_OPTIONAL_INT_VALUE, readContainerNonOptional.getNonOptionalInt());
    ASSERT_FALSE(readContainerNonOptional.hasAutoOptionalInt());

    const int autoOptionalIntValue = AUTO_OPTIONAL_INT_VALUE;
    container.setAutoOptionalInt(autoOptionalIntValue);

    zserio::BitStreamWriter writeOptional;
    container.write(writeOptional);
    size_t writeOptionalBufferByteSize;
    const uint8_t* writeOptionalBuffer = writeOptional.getWriteBuffer(writeOptionalBufferByteSize);
    zserio::BitStreamReader readerOptional(writeOptionalBuffer, writeOptionalBufferByteSize);
    checkContainerInBitStream(readerOptional, NON_OPTIONAL_INT_VALUE, true, autoOptionalIntValue);
    Container readContainerOptional(readerOptional);
    ASSERT_EQ(NON_OPTIONAL_INT_VALUE, readContainerOptional.getNonOptionalInt());
    ASSERT_TRUE(readContainerOptional.hasAutoOptionalInt());
    ASSERT_EQ(AUTO_OPTIONAL_INT_VALUE, readContainerOptional.getAutoOptionalInt());
}

} // namespace auto_optional
} // namespace optional_members
