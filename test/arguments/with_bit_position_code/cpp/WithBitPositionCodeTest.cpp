#include "gtest/gtest.h"
#include "test_utils/Assertions.h"
#include "with_bit_position_code/Main.h"
#include "zserio/BitBuffer.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/Vector.h"

namespace with_bit_position_code
{

class WithBitPositionCode : public ::testing::Test
{
protected:
    void writeMain(zserio::BitStreamWriter& writer)
    {
        // itemChoice
        writer.writeBits(ITEM_VALUE, 32);
        writer.writeSignedBits(EXTRA_VALUE, 8);

        // valueUnion
        writer.writeVarSize(1);
        writer.writeBits(UINT32_VALUE, 32);

        // simpleStruct
        writer.writeString(STRING_VALUE);
        writer.writeBool(true);
        writer.writeBool(OPTIONAL_VALUE);

        // item
        writer.writeBits(ITEM_VALUE, 32);
    }

    void checkMain(const Main& main)
    {
        // itemChoice
        ASSERT_EQ(true, main.getItemChoice().getHasItem());
        ASSERT_EQ(true, main.getItemChoice().getItem().getNeedsExtra());
        ASSERT_EQ(ITEM_VALUE, main.getItemChoice().getItem().getValue());
        ASSERT_EQ(EXTRA_VALUE, main.getItemChoice().getItem().getExtraValue());

        // valueUnion
        ASSERT_EQ(UINT32_VALUE, main.getValueUnion().getValue32());

        // simpleStruct
        ASSERT_EQ(STRING_VALUE, main.getSimpleStruct().getStringValue());
        ASSERT_EQ(OPTIONAL_VALUE, main.getSimpleStruct().getOptionalValue());

        // item
        ASSERT_EQ(false, main.getItem().getNeedsExtra());
        ASSERT_EQ(ITEM_VALUE, main.getItem().getValue());
    }

    void checkBitPositions(const Main& main)
    {
        // main
        ASSERT_EQ(BIT_POSITION_MAIN, main.bitPosition());

        // itemChoice
        ASSERT_EQ(BIT_POSITION_ITEM_CHOICE, main.getItemChoice().bitPosition());

        // item
        ASSERT_EQ(BIT_POSITION_ITEM, main.getItemChoice().getItem().bitPosition());

        // valueUnion
        ASSERT_EQ(BIT_POSITION_VALUE_UNION, main.getValueUnion().bitPosition());

        // simpleStruct
        ASSERT_EQ(BIT_POSITION_SIMPLE_STRUCT, main.getSimpleStruct().bitPosition());

        // item
        ASSERT_EQ(BIT_POSITION_MAIN_ITEM, main.getItem().bitPosition());
    }

    static const char* const PATH;

    static const uint32_t ITEM_VALUE;
    static const int8_t EXTRA_VALUE;
    static const uint32_t UINT32_VALUE;
    static const char* const STRING_VALUE;
    static const bool OPTIONAL_VALUE;

    static const size_t BIT_POSITION_MAIN;
    static const size_t BIT_POSITION_ITEM_CHOICE;
    static const size_t BIT_POSITION_ITEM;
    static const size_t BIT_POSITION_VALUE_UNION;
    static const size_t BIT_POSITION_SIMPLE_STRUCT;
    static const size_t BIT_POSITION_MAIN_ITEM;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const char* const WithBitPositionCode::PATH = "arguments/with_bit_position_code/gen/with_bit_position_code/";

const uint32_t WithBitPositionCode::ITEM_VALUE = 0xDEADBEEF;
const int8_t WithBitPositionCode::EXTRA_VALUE = -34;
const uint32_t WithBitPositionCode::UINT32_VALUE = 0xCAFECAFE;
const char* const WithBitPositionCode::STRING_VALUE = "Supercalifragilisticexpialidocious";
const bool WithBitPositionCode::OPTIONAL_VALUE = true;

const size_t WithBitPositionCode::BIT_POSITION_MAIN = 0;
const size_t WithBitPositionCode::BIT_POSITION_ITEM_CHOICE = 0;
const size_t WithBitPositionCode::BIT_POSITION_ITEM = 0;
const size_t WithBitPositionCode::BIT_POSITION_VALUE_UNION = 32 + 8;
const size_t WithBitPositionCode::BIT_POSITION_SIMPLE_STRUCT = BIT_POSITION_VALUE_UNION + 8 + 32;
const size_t WithBitPositionCode::BIT_POSITION_MAIN_ITEM =
        BIT_POSITION_SIMPLE_STRUCT + 8 + 8 * sizeof("Supercalifragilisticexpialidocious") - 8 + 1 + 1;

TEST_F(WithBitPositionCode, checkBitPositionMethods)
{
    ASSERT_METHOD_PRESENT(PATH, "Item", "size_t bitPosition() const", "size_t Item::bitPosition() const");
    ASSERT_METHOD_PRESENT(
            PATH, "ItemChoice", "size_t bitPosition() const", "size_t ItemChoice::bitPosition() const");
    ASSERT_METHOD_PRESENT(
            PATH, "ValueUnion", "size_t bitPosition() const", "size_t ValueUnion::bitPosition() const");
    ASSERT_METHOD_PRESENT(
            PATH, "SimpleStruct", "size_t bitPosition() const", "size_t SimpleStruct::bitPosition() const");
    ASSERT_METHOD_PRESENT(PATH, "Main", "size_t bitPosition() const", "size_t Main::bitPosition() const");
}

TEST_F(WithBitPositionCode, readConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeMain(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const Main main(reader);
    checkMain(main);
    checkBitPositions(main);
}

TEST_F(WithBitPositionCode, copyConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeMain(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const Main main(reader);
    const Main mainCopy(main);
    checkMain(mainCopy);
    checkBitPositions(mainCopy);
}

TEST_F(WithBitPositionCode, moveConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeMain(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Main main(reader);
    const Main mainMove(std::move(main));
    checkMain(mainMove);
    checkBitPositions(mainMove);
}

TEST_F(WithBitPositionCode, assignmentOperator)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeMain(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const Main main(reader);
    Main mainAssign;
    mainAssign = main;
    checkMain(mainAssign);
    checkBitPositions(mainAssign);
}

TEST_F(WithBitPositionCode, moveAssignmentOperator)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeMain(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Main main(reader);
    Main mainMoveAssign;
    mainMoveAssign = std::move(main);
    checkMain(mainMoveAssign);
    checkBitPositions(mainMoveAssign);
}

} // namespace with_bit_position_code
