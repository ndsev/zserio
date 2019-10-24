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
        std::vector<int32_t>& tones = blackColor.getTones();
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
};

const uint8_t OptionalExpressionTest::NUM_BLACK_TONES = 2;
const size_t OptionalExpressionTest::CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 8;
const size_t OptionalExpressionTest::CONTAINER_BIT_SIZE_WITH_OPTIONAL = 8 + 8 + 32 * NUM_BLACK_TONES;

TEST_F(OptionalExpressionTest, fieldConstructor)
{
    BlackColor blackColor;
    fillBlackColor(blackColor, NUM_BLACK_TONES);
    const Container containerWithOptionals(BasicColor::BLACK, NUM_BLACK_TONES, blackColor);
    ASSERT_TRUE(containerWithOptionals.hasNumBlackTones());
    ASSERT_TRUE(containerWithOptionals.hasBlackColor());

    const Container containerWithoutOptionals(BasicColor::WHITE, zserio::NullOpt, zserio::NullOpt);
    ASSERT_FALSE(containerWithoutOptionals.hasNumBlackTones());
    ASSERT_FALSE(containerWithoutOptionals.hasBlackColor());
}

TEST_F(OptionalExpressionTest, resetNumBlackTones)
{
    Container container;
    container.setBasicColor(BasicColor::BLACK);
    container.setNumBlackTones(NUM_BLACK_TONES);
    ASSERT_TRUE(container.hasNumBlackTones());

    ASSERT_NO_THROW(container.getNumBlackTones());
    container.resetNumBlackTones();
    ASSERT_THROW(container.getNumBlackTones(), zserio::CppRuntimeException);
}

TEST_F(OptionalExpressionTest, hasNumBlackTones)
{
    Container container;
    container.setBasicColor(BasicColor::WHITE);
    ASSERT_FALSE(container.hasNumBlackTones());

    container.setBasicColor(BasicColor::BLACK);
    const uint8_t numBlackTones = NUM_BLACK_TONES;
    container.setNumBlackTones(numBlackTones);
    ASSERT_TRUE(container.hasNumBlackTones());
    ASSERT_EQ(numBlackTones, container.getNumBlackTones());
}

TEST_F(OptionalExpressionTest, resetBlackColor)
{
    Container container;
    container.setBasicColor(BasicColor::BLACK);
    BlackColor blackColor;
    fillBlackColor(blackColor, NUM_BLACK_TONES);
    container.setBlackColor(blackColor);
    ASSERT_TRUE(container.hasBlackColor());

    ASSERT_NO_THROW(container.getBlackColor());
    container.resetBlackColor();
    ASSERT_THROW(container.getBlackColor(), zserio::CppRuntimeException);
}

TEST_F(OptionalExpressionTest, hasBlackColor)
{
    Container container;
    container.setBasicColor(BasicColor::WHITE);
    ASSERT_FALSE(container.hasBlackColor());

    container.setBasicColor(BasicColor::BLACK);
    BlackColor blackColor;
    fillBlackColor(blackColor, NUM_BLACK_TONES);
    container.setBlackColor(blackColor);
    ASSERT_TRUE(container.hasBlackColor());
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
}

TEST_F(OptionalExpressionTest, write)
{
    Container container;
    container.setBasicColor(BasicColor::WHITE);

    zserio::BitStreamWriter writerWhite;
    container.write(writerWhite);
    size_t writerWhiteBufferByteSize;
    const uint8_t* writerWhiteBuffer = writerWhite.getWriteBuffer(writerWhiteBufferByteSize);
    zserio::BitStreamReader readerWhite(writerWhiteBuffer, writerWhiteBufferByteSize);
    checkContainerInBitStream(readerWhite, BasicColor::WHITE, NUM_BLACK_TONES);
    Container readContainerWhite(readerWhite);

    ASSERT_EQ(BasicColor::WHITE, readContainerWhite.getBasicColor());
    ASSERT_FALSE(readContainerWhite.hasNumBlackTones());
    ASSERT_FALSE(readContainerWhite.hasBlackColor());

    container.setBasicColor(BasicColor::BLACK);
    const uint8_t numBlackTones = NUM_BLACK_TONES;
    container.setNumBlackTones(numBlackTones);
    BlackColor blackColor;
    fillBlackColor(blackColor, numBlackTones);
    container.setBlackColor(blackColor);

    zserio::BitStreamWriter writerBlack;
    container.write(writerBlack);
    size_t writerBlackBufferByteSize;
    const uint8_t* writerBlackBuffer = writerBlack.getWriteBuffer(writerBlackBufferByteSize);
    zserio::BitStreamReader readerBlack(writerBlackBuffer, writerBlackBufferByteSize);
    checkContainerInBitStream(readerBlack, BasicColor::BLACK, numBlackTones);
    Container readContainerBlack(readerBlack);

    ASSERT_EQ(BasicColor::BLACK, readContainerBlack.getBasicColor());
    ASSERT_TRUE(readContainerBlack.hasNumBlackTones());
    ASSERT_TRUE(readContainerBlack.hasBlackColor());
    ASSERT_EQ(numBlackTones, readContainerBlack.getNumBlackTones());
    ASSERT_EQ(numBlackTones, readContainerBlack.getBlackColor().getNumBlackTones());
    ASSERT_EQ(blackColor.getTones(), readContainerBlack.getBlackColor().getTones());
}

} // namespace optional_expression
} // namespace optional_members
