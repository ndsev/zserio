#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "optional_members/optional_expression/Container.h"
#include "optional_members/optional_expression/BasicColor.h"

namespace optional_members
{
namespace optional_expression
{

class OptionalExpressionTest : public ::testing::Test
{
protected:
    void fillBlackColor(BlackColor& blackColor, uint8_t numBlackTones)
    {
        auto& tones = blackColor.getTones();
        for (uint8_t i = 0; i < numBlackTones; ++i)
            tones.push_back(i + 1);
    }

    void checkContainerInBitStream(zserio::BitStreamReader& reader, BasicColor basicColor,
        uint8_t numBlackTones)
    {
        ASSERT_EQ(zserio::enumToValue(basicColor), reader.readBits(8));

        if (basicColor == BasicColor::BLACK)
        {
            ASSERT_EQ(numBlackTones, reader.readBits(8));
            for (uint8_t i = 0; i < numBlackTones; ++i)
                ASSERT_EQ(i + 1, reader.readSignedBits(32));
        }

        reader.setBitPosition(0);
    }

    static const uint8_t NUM_BLACK_TONES;
    static const size_t CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL;
    static const size_t CONTAINER_BIT_SIZE_WITH_OPTIONAL;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint8_t OptionalExpressionTest::NUM_BLACK_TONES = 2;
const size_t OptionalExpressionTest::CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 8;
const size_t OptionalExpressionTest::CONTAINER_BIT_SIZE_WITH_OPTIONAL = 8 + 8 + 32 * NUM_BLACK_TONES;

TEST_F(OptionalExpressionTest, fieldConstructor)
{
    BlackColor blackColor;
    fillBlackColor(blackColor, NUM_BLACK_TONES);
    const Container containerWithOptionals(BasicColor::BLACK, NUM_BLACK_TONES, blackColor);
    ASSERT_TRUE(containerWithOptionals.isNumBlackTonesSet());
    ASSERT_TRUE(containerWithOptionals.isNumBlackTonesUsed());
    ASSERT_TRUE(containerWithOptionals.isBlackColorSet());
    ASSERT_TRUE(containerWithOptionals.isBlackColorUsed());

    const Container containerWithoutOptionals(BasicColor::WHITE, zserio::NullOpt, zserio::NullOpt);
    ASSERT_FALSE(containerWithoutOptionals.isNumBlackTonesSet());
    ASSERT_FALSE(containerWithoutOptionals.isNumBlackTonesUsed());
    ASSERT_FALSE(containerWithoutOptionals.isBlackColorSet());
    ASSERT_FALSE(containerWithoutOptionals.isBlackColorUsed());
}

TEST_F(OptionalExpressionTest, isNumBlackTonesSetAndUsed)
{
    Container container;
    container.setBasicColor(BasicColor::WHITE);
    ASSERT_FALSE(container.isNumBlackTonesSet());
    ASSERT_FALSE(container.isNumBlackTonesUsed());

    container.setBasicColor(BasicColor::BLACK);
    const uint8_t numBlackTones = NUM_BLACK_TONES;
    container.setNumBlackTones(numBlackTones);
    ASSERT_TRUE(container.isNumBlackTonesSet());
    ASSERT_TRUE(container.isNumBlackTonesUsed());
    ASSERT_EQ(numBlackTones, container.getNumBlackTones());

    container.setBasicColor(BasicColor::WHITE); // set but not used
    ASSERT_TRUE(container.isNumBlackTonesSet());
    ASSERT_FALSE(container.isNumBlackTonesUsed());

    container.setBasicColor(BasicColor::BLACK);
    container.resetNumBlackTones(); // used but not set
    ASSERT_FALSE(container.isNumBlackTonesSet());
    ASSERT_TRUE(container.isNumBlackTonesUsed());
}

TEST_F(OptionalExpressionTest, resetNumBlackTones)
{
    Container container;
    container.setBasicColor(BasicColor::BLACK);
    container.setNumBlackTones(NUM_BLACK_TONES);
    ASSERT_TRUE(container.isNumBlackTonesSet());
    ASSERT_TRUE(container.isNumBlackTonesUsed());

    container.resetNumBlackTones(); // used but not set
    ASSERT_FALSE(container.isNumBlackTonesSet());
    ASSERT_TRUE(container.isNumBlackTonesUsed());
    ASSERT_THROW(container.getNumBlackTones(), zserio::CppRuntimeException);
}

TEST_F(OptionalExpressionTest, isBlackColorSetAndUsed)
{
    Container container;
    container.setBasicColor(BasicColor::WHITE);
    ASSERT_FALSE(container.isBlackColorSet());
    ASSERT_FALSE(container.isBlackColorUsed());

    container.setBasicColor(BasicColor::BLACK);
    BlackColor blackColor;
    fillBlackColor(blackColor, NUM_BLACK_TONES);
    container.setBlackColor(blackColor);
    ASSERT_TRUE(container.isBlackColorSet());
    ASSERT_TRUE(container.isBlackColorUsed());

    container.setBasicColor(BasicColor::WHITE); // set but not used
    ASSERT_TRUE(container.isBlackColorSet());
    ASSERT_FALSE(container.isBlackColorUsed());

    container.setBasicColor(BasicColor::BLACK);
    container.resetBlackColor(); // used but not set
    ASSERT_FALSE(container.isBlackColorSet());
    ASSERT_TRUE(container.isBlackColorUsed());
}

TEST_F(OptionalExpressionTest, resetBlackColor)
{
    Container container;
    container.setBasicColor(BasicColor::BLACK);
    BlackColor blackColor;
    fillBlackColor(blackColor, NUM_BLACK_TONES);
    container.setBlackColor(blackColor);
    ASSERT_TRUE(container.isBlackColorSet());
    ASSERT_TRUE(container.isBlackColorUsed());

    container.resetBlackColor(); // used but not set
    ASSERT_FALSE(container.isBlackColorSet());
    ASSERT_TRUE(container.isBlackColorUsed());
    ASSERT_THROW(container.getBlackColor(), zserio::CppRuntimeException);
}

TEST_F(OptionalExpressionTest, bitSizeOf)
{
    Container container;
    container.setBasicColor(BasicColor::WHITE);
    const size_t bitSizeWithoutOptional = CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL;
    ASSERT_EQ(bitSizeWithoutOptional, container.bitSizeOf());

    container.setBasicColor(BasicColor::BLACK);
    container.setNumBlackTones(NUM_BLACK_TONES);
    BlackColor blackColor;
    fillBlackColor(blackColor, NUM_BLACK_TONES);
    container.setBlackColor(blackColor);
    container.initializeChildren();
    const size_t bitSizeWithOptional = CONTAINER_BIT_SIZE_WITH_OPTIONAL;
    ASSERT_EQ(bitSizeWithOptional, container.bitSizeOf());

    container.setBasicColor(BasicColor::WHITE); // set but not used
    ASSERT_EQ(bitSizeWithoutOptional, container.bitSizeOf());
}

TEST_F(OptionalExpressionTest, initializeOffsets)
{
    Container container;
    container.setBasicColor(BasicColor::WHITE);
    const size_t bitPosition = 1;
    const size_t bitSizeWithoutOptional = CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL;
    ASSERT_EQ(bitPosition + bitSizeWithoutOptional, container.initializeOffsets(bitPosition));

    container.setBasicColor(BasicColor::BLACK);
    container.setNumBlackTones(NUM_BLACK_TONES);
    BlackColor blackColor;
    fillBlackColor(blackColor, NUM_BLACK_TONES);
    container.setBlackColor(blackColor);
    container.initializeChildren();
    const size_t bitSizeWithOptional = CONTAINER_BIT_SIZE_WITH_OPTIONAL;
    ASSERT_EQ(bitPosition + bitSizeWithOptional, container.initializeOffsets(bitPosition));

    container.setBasicColor(BasicColor::WHITE); // set but not used
    ASSERT_EQ(bitPosition + bitSizeWithoutOptional, container.initializeOffsets(bitPosition));
}

TEST_F(OptionalExpressionTest, operatorEquality)
{
    Container container1;
    Container container2;

    container1.setBasicColor(BasicColor::WHITE);
    container2.setBasicColor(BasicColor::BLACK);
    container2.setNumBlackTones(NUM_BLACK_TONES);
    BlackColor blackColor;
    fillBlackColor(blackColor, NUM_BLACK_TONES);
    container2.setBlackColor(blackColor);
    container2.initializeChildren();
    ASSERT_FALSE(container1 == container2);

    container2.setBasicColor(BasicColor::WHITE); // set but not used
    ASSERT_TRUE(container1 == container2);
}

TEST_F(OptionalExpressionTest, hashCode)
{
    Container container1;
    Container container2;

    container1.setBasicColor(BasicColor::WHITE);
    container2.setBasicColor(BasicColor::BLACK);
    container2.setNumBlackTones(NUM_BLACK_TONES);
    BlackColor blackColor;
    fillBlackColor(blackColor, NUM_BLACK_TONES);
    container2.setBlackColor(blackColor);
    container2.initializeChildren();
    ASSERT_NE(container1.hashCode(), container2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(1703, container1.hashCode());
    ASSERT_EQ(2393199, container2.hashCode());

    container2.setBasicColor(BasicColor::WHITE); // set but not used
    ASSERT_EQ(container1.hashCode(), container2.hashCode());
}

TEST_F(OptionalExpressionTest, write)
{
    Container container;
    container.setBasicColor(BasicColor::WHITE);
    container.initializeChildren();

    zserio::BitStreamWriter writerWhite(bitBuffer);
    container.write(writerWhite);

    zserio::BitStreamReader readerWhite(writerWhite.getWriteBuffer(), writerWhite.getBitPosition(),
            zserio::BitsTag());
    checkContainerInBitStream(readerWhite, BasicColor::WHITE, NUM_BLACK_TONES);
    Container readContainerWhite(readerWhite);

    ASSERT_EQ(BasicColor::WHITE, readContainerWhite.getBasicColor());
    ASSERT_FALSE(readContainerWhite.isNumBlackTonesSet());
    ASSERT_FALSE(readContainerWhite.isNumBlackTonesUsed());
    ASSERT_FALSE(readContainerWhite.isBlackColorSet());
    ASSERT_FALSE(readContainerWhite.isBlackColorUsed());

    container.setBasicColor(BasicColor::BLACK);
    const uint8_t numBlackTones = NUM_BLACK_TONES;
    container.setNumBlackTones(numBlackTones);
    BlackColor blackColor;
    fillBlackColor(blackColor, numBlackTones);
    container.setBlackColor(blackColor);
    container.initializeChildren();

    zserio::BitStreamWriter writerBlack(bitBuffer);
    container.write(writerBlack);

    zserio::BitStreamReader readerBlack(writerBlack.getWriteBuffer(), writerBlack.getBitPosition(),
            zserio::BitsTag());
    checkContainerInBitStream(readerBlack, BasicColor::BLACK, numBlackTones);
    Container readContainerBlack(readerBlack);

    ASSERT_EQ(BasicColor::BLACK, readContainerBlack.getBasicColor());
    ASSERT_TRUE(readContainerBlack.isNumBlackTonesSet());
    ASSERT_TRUE(readContainerBlack.isNumBlackTonesUsed());
    ASSERT_TRUE(readContainerBlack.isBlackColorSet());
    ASSERT_TRUE(readContainerBlack.isBlackColorUsed());
    ASSERT_EQ(numBlackTones, readContainerBlack.getNumBlackTones());
    ASSERT_EQ(numBlackTones, readContainerBlack.getBlackColor().getNumBlackTones());
    ASSERT_EQ(blackColor.getTones(), readContainerBlack.getBlackColor().getTones());

    container.setBasicColor(BasicColor::WHITE); // set but not used
    zserio::BitStreamWriter writerBlackToWhite(bitBuffer);
    container.write(writerBlackToWhite);

    zserio::BitStreamReader readerBlackToWhite(writerBlackToWhite.getWriteBuffer(),
            writerBlackToWhite.getBitPosition(), zserio::BitsTag());
    checkContainerInBitStream(readerBlackToWhite, BasicColor::WHITE, NUM_BLACK_TONES);
    Container readContainerBlackToWhite(readerBlackToWhite);

    ASSERT_EQ(BasicColor::WHITE, readContainerBlackToWhite.getBasicColor());
    ASSERT_FALSE(readContainerBlackToWhite.isNumBlackTonesSet());
    ASSERT_FALSE(readContainerBlackToWhite.isNumBlackTonesUsed());
    ASSERT_FALSE(readContainerBlackToWhite.isBlackColorSet());
    ASSERT_FALSE(readContainerBlackToWhite.isBlackColorUsed());
}

} // namespace optional_expression
} // namespace optional_members
