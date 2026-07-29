#include "gtest/gtest.h"
#include "optional_members/optional_enumeration/BasicColor.h"
#include "optional_members/optional_enumeration/Container.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace optional_members
{
namespace optional_enumeration
{

class OptionalEnumerationTest : public ::testing::Test
{
protected:
    void checkContainerInBitStream(zserio::BitStreamReader& reader)
    {
        ASSERT_EQ(0, reader.readBits(1));
        reader.setBitPosition(0);
    }

    void checkContainerInBitStream(zserio::BitStreamReader& reader, BasicColor basicColor)
    {
        ASSERT_EQ(1, reader.readBits(1));
        ASSERT_EQ(zserio::enumToValue(basicColor), reader.readBits(8));
        reader.setBitPosition(0);
    }

    static const size_t CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL;
    static const size_t CONTAINER_BIT_SIZE_WITH_OPTIONAL;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const size_t OptionalEnumerationTest::CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 1;
const size_t OptionalEnumerationTest::CONTAINER_BIT_SIZE_WITH_OPTIONAL = 1 + 8;

TEST_F(OptionalEnumerationTest, fieldConstructor)
{
    Container container(zserio::NullOpt);
    ASSERT_FALSE(container.isBasicColorSet());
    ASSERT_FALSE(container.isBasicColorUsed());

    container.setBasicColor(BasicColor::WHITE);
    ASSERT_EQ(BasicColor::WHITE, container.getBasicColor());
    ASSERT_TRUE(container.isBasicColorSet());
    ASSERT_TRUE(container.isBasicColorUsed());
}

TEST_F(OptionalEnumerationTest, isBasicColorSetAndUsed)
{
    Container container(zserio::NullOpt);
    ASSERT_FALSE(container.isBasicColorSet());
    ASSERT_FALSE(container.isBasicColorUsed());

    container.setBasicColor(BasicColor::WHITE);
    ASSERT_TRUE(container.isBasicColorSet());
    ASSERT_TRUE(container.isBasicColorUsed());
    ASSERT_EQ(BasicColor::WHITE, container.getBasicColor());
}

TEST_F(OptionalEnumerationTest, resetBasicColor)
{
    Container container(BasicColor::WHITE);
    ASSERT_TRUE(container.isBasicColorSet());
    ASSERT_TRUE(container.isBasicColorUsed());

    container.resetBasicColor();
    ASSERT_FALSE(container.isBasicColorSet());
    ASSERT_FALSE(container.isBasicColorUsed());
    ASSERT_THROW(container.getBasicColor(), zserio::CppRuntimeException);
}

TEST_F(OptionalEnumerationTest, bitSizeOf)
{
    Container container(zserio::NullOpt);
    ASSERT_EQ(CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.bitSizeOf());

    container.setBasicColor(BasicColor::WHITE);
    ASSERT_EQ(CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.bitSizeOf());
}

TEST_F(OptionalEnumerationTest, initializeOffsets)
{
    Container container(zserio::NullOpt);
    const size_t bitPosition = 1;
    ASSERT_EQ(bitPosition + CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.initializeOffsets(bitPosition));

    container.setBasicColor(BasicColor::WHITE);
    ASSERT_EQ(bitPosition + CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.initializeOffsets(bitPosition));
}

TEST_F(OptionalEnumerationTest, operatorEquality)
{
    Container container1(zserio::NullOpt);
    Container container2(zserio::NullOpt);
    ASSERT_TRUE(container1 == container2);

    container1.setBasicColor(BasicColor::WHITE);
    ASSERT_FALSE(container1 == container2);

    container2.setBasicColor(BasicColor::BLACK);
    ASSERT_FALSE(container1 == container2);
}

TEST_F(OptionalEnumerationTest, operatorLessThan)
{
    Container container1(zserio::NullOpt);
    Container container2(zserio::NullOpt);
    ASSERT_FALSE(container1 < container2);
    ASSERT_FALSE(container2 < container1);

    container1.setBasicColor(BasicColor::WHITE);
    ASSERT_FALSE(container1 < container2);
    ASSERT_TRUE(container2 < container1);

    container2.setBasicColor(BasicColor::BLACK);
    ASSERT_FALSE(container1 < container2);
    ASSERT_TRUE(container2 < container1);
}

TEST_F(OptionalEnumerationTest, hashCode)
{
    Container container1(zserio::NullOpt);
    Container container2(zserio::NullOpt);
    ASSERT_EQ(container1.hashCode(), container2.hashCode());

    container1.setBasicColor(BasicColor::WHITE);
    ASSERT_NE(container1.hashCode(), container2.hashCode());

    container2.setBasicColor(BasicColor::BLACK);
    ASSERT_NE(container1.hashCode(), container2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(1703, container1.hashCode());
    ASSERT_EQ(1702, container2.hashCode());
}

TEST_F(OptionalEnumerationTest, write)
{
    Container container(zserio::NullOpt);
    zserio::BitStreamWriter writerWithout(bitBuffer);
    container.write(writerWithout);

    zserio::BitStreamReader readerWithout(
            writerWithout.getWriteBuffer(), writerWithout.getBitPosition(), zserio::BitsTag());
    checkContainerInBitStream(readerWithout);
    Container readContainerWithout(readerWithout);
    ASSERT_FALSE(readContainerWithout.isBasicColorSet());
    ASSERT_FALSE(readContainerWithout.isBasicColorUsed());

    container.setBasicColor(BasicColor::WHITE);
    zserio::BitStreamWriter writerWith(bitBuffer);
    container.write(writerWith);

    zserio::BitStreamReader readerWith(
            writerWith.getWriteBuffer(), writerWith.getBitPosition(), zserio::BitsTag());
    checkContainerInBitStream(readerWith, BasicColor::WHITE);
    Container readContainerWith(readerWith);
    ASSERT_EQ(BasicColor::WHITE, readContainerWith.getBasicColor());
    ASSERT_TRUE(readContainerWith.isBasicColorSet());
    ASSERT_TRUE(readContainerWith.isBasicColorUsed());
}

} // namespace optional_enumeration
} // namespace optional_members
