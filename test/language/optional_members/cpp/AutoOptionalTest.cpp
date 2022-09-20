#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

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

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const int32_t AutoOptionalTest::NON_OPTIONAL_INT_VALUE = static_cast<int32_t>(0xDEADDEAD);
const int32_t AutoOptionalTest::AUTO_OPTIONAL_INT_VALUE = static_cast<int32_t>(0xBEEFBEEF);

const size_t AutoOptionalTest::CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 32 + 1;
const size_t AutoOptionalTest::CONTAINER_BIT_SIZE_WITH_OPTIONAL = 32 + 1 + 32;

TEST_F(AutoOptionalTest, emptyConstructor)
{
    const Container container;
    ASSERT_FALSE(container.isAutoOptionalIntSet());
    ASSERT_FALSE(container.isAutoOptionalIntUsed());
}

TEST_F(AutoOptionalTest, fieldConstructor)
{
    const Container containerWithOptional(NON_OPTIONAL_INT_VALUE, AUTO_OPTIONAL_INT_VALUE);
    ASSERT_TRUE(containerWithOptional.isAutoOptionalIntSet());
    ASSERT_TRUE(containerWithOptional.isAutoOptionalIntUsed());
    ASSERT_EQ(AUTO_OPTIONAL_INT_VALUE, containerWithOptional.getAutoOptionalInt());

    const Container containerWithoutOptional(NON_OPTIONAL_INT_VALUE, zserio::NullOpt);
    ASSERT_FALSE(containerWithoutOptional.isAutoOptionalIntSet());
    ASSERT_FALSE(containerWithoutOptional.isAutoOptionalIntUsed());
}

TEST_F(AutoOptionalTest, isAutoOptionalIntSetAndUsed)
{
    Container container;
    container.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
    ASSERT_FALSE(container.isAutoOptionalIntSet());
    ASSERT_FALSE(container.isAutoOptionalIntUsed());

    container.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    ASSERT_TRUE(container.isAutoOptionalIntSet());
    ASSERT_TRUE(container.isAutoOptionalIntUsed());
}

TEST_F(AutoOptionalTest, resetAutoOptionalInt)
{
    Container container;
    container.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    ASSERT_TRUE(container.isAutoOptionalIntSet());
    ASSERT_TRUE(container.isAutoOptionalIntUsed());

    container.resetAutoOptionalInt();
    ASSERT_FALSE(container.isAutoOptionalIntSet());
    ASSERT_FALSE(container.isAutoOptionalIntUsed());
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

    container1.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
    container1.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    container2.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
    ASSERT_FALSE(container1 == container2);

    container2.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    ASSERT_TRUE(container1 == container2);

    container1.resetAutoOptionalInt();
    ASSERT_FALSE(container1 == container2);
}

TEST_F(AutoOptionalTest, hashCode)
{
    Container container1;
    Container container2;

    container1.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
    container1.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    container2.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
    ASSERT_NE(container1.hashCode(), container2.hashCode());

    container2.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
    ASSERT_EQ(container1.hashCode(), container2.hashCode());

    container1.resetAutoOptionalInt();
    ASSERT_NE(container1.hashCode(), container2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(3735937536, container1.hashCode());
    ASSERT_EQ(3994118383, container2.hashCode());
}

TEST_F(AutoOptionalTest, write)
{
    Container container;
    container.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);

    zserio::BitStreamWriter writeNonOptional(bitBuffer);
    container.write(writeNonOptional);

    zserio::BitStreamReader readerNonOptional(writeNonOptional.getWriteBuffer(),
            writeNonOptional.getBitPosition(), zserio::BitsTag());
    checkContainerInBitStream(readerNonOptional, NON_OPTIONAL_INT_VALUE, false, 0);
    Container readContainerNonOptional(readerNonOptional);
    ASSERT_EQ(NON_OPTIONAL_INT_VALUE, readContainerNonOptional.getNonOptionalInt());
    ASSERT_FALSE(readContainerNonOptional.isAutoOptionalIntSet());
    ASSERT_FALSE(readContainerNonOptional.isAutoOptionalIntUsed());

    const int autoOptionalIntValue = AUTO_OPTIONAL_INT_VALUE;
    container.setAutoOptionalInt(autoOptionalIntValue);

    zserio::BitStreamWriter writeOptional(bitBuffer);
    container.write(writeOptional);

    zserio::BitStreamReader readerOptional(writeOptional.getWriteBuffer(),
            writeOptional.getBitPosition(), zserio::BitsTag());
    checkContainerInBitStream(readerOptional, NON_OPTIONAL_INT_VALUE, true, autoOptionalIntValue);
    Container readContainerOptional(readerOptional);
    ASSERT_EQ(NON_OPTIONAL_INT_VALUE, readContainerOptional.getNonOptionalInt());
    ASSERT_TRUE(readContainerOptional.isAutoOptionalIntSet());
    ASSERT_TRUE(readContainerOptional.isAutoOptionalIntUsed());
    ASSERT_EQ(AUTO_OPTIONAL_INT_VALUE, readContainerOptional.getAutoOptionalInt());
}

} // namespace auto_optional
} // namespace optional_members
