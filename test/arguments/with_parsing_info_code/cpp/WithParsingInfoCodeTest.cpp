#include "gtest/gtest.h"
#include "test_utils/Assertions.h"
#include "with_parsing_info_code/Main.h"
#include "zserio/BitBuffer.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/Vector.h"

namespace with_parsing_info_code
{

class WithParsingInfoCode : public ::testing::Test
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

        // items, 3-elements packed array {1000, 10}, {1001, 12}, {1002, 14}
        writer.writeVarSize(3);
        writer.writeBool(true);
        writer.writeBits(1, 6);
        writer.writeBits(ITEMS_VALUE0, 32);
        writer.writeBool(true);
        writer.writeBits(2, 6);
        writer.writeSignedBits(ITEMS_EXTRA_VALUE0, 8);

        writer.writeSignedBits(1, 2);
        writer.writeSignedBits(2, 3);

        writer.writeSignedBits(1, 2);
        writer.writeSignedBits(2, 3);
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

        // items
        ASSERT_EQ(3, main.getItems().size());
        ASSERT_EQ(ITEMS_VALUE0, main.getItems().at(0).getValue());
        ASSERT_EQ(ITEMS_EXTRA_VALUE0, main.getItems().at(0).getExtraValue());
        ASSERT_EQ(ITEMS_VALUE1, main.getItems().at(1).getValue());
        ASSERT_EQ(ITEMS_EXTRA_VALUE1, main.getItems().at(1).getExtraValue());
        ASSERT_EQ(ITEMS_VALUE2, main.getItems().at(2).getValue());
        ASSERT_EQ(ITEMS_EXTRA_VALUE2, main.getItems().at(2).getExtraValue());
    }

    void checkParsingInfo(const Main& main)
    {
        // main
        ASSERT_EQ(BIT_POSITION_MAIN, main.parsingInfo().getBitPosition());
        ASSERT_EQ(BIT_SIZE_MAIN, main.parsingInfo().getBitSize());

        // itemChoice
        ASSERT_EQ(BIT_POSITION_ITEM_CHOICE, main.getItemChoice().parsingInfo().getBitPosition());
        ASSERT_EQ(BIT_SIZE_ITEM_CHOICE, main.getItemChoice().parsingInfo().getBitSize());

        // item
        ASSERT_EQ(BIT_POSITION_ITEM, main.getItemChoice().getItem().parsingInfo().getBitPosition());
        ASSERT_EQ(BIT_SIZE_ITEM, main.getItemChoice().getItem().parsingInfo().getBitSize());

        // valueUnion
        ASSERT_EQ(BIT_POSITION_VALUE_UNION, main.getValueUnion().parsingInfo().getBitPosition());
        ASSERT_EQ(BIT_SIZE_VALUE_UNION, main.getValueUnion().parsingInfo().getBitSize());

        // simpleStruct
        ASSERT_EQ(BIT_POSITION_SIMPLE_STRUCT, main.getSimpleStruct().parsingInfo().getBitPosition());
        ASSERT_EQ(BIT_SIZE_SIMPLE_STRUCT, main.getSimpleStruct().parsingInfo().getBitSize());

        // item
        ASSERT_EQ(BIT_POSITION_MAIN_ITEM, main.getItem().parsingInfo().getBitPosition());
        ASSERT_EQ(BIT_SIZE_MAIN_ITEM, main.getItem().parsingInfo().getBitSize());

        // items
        ASSERT_EQ(3, main.getItems().size());
        ASSERT_EQ(BIT_POSITION_ITEMS0, main.getItems().at(0).parsingInfo().getBitPosition());
        ASSERT_EQ(BIT_SIZE_ITEMS0, main.getItems().at(0).parsingInfo().getBitSize());
        ASSERT_EQ(BIT_POSITION_ITEMS1, main.getItems().at(1).parsingInfo().getBitPosition());
        ASSERT_EQ(BIT_SIZE_ITEMS1, main.getItems().at(1).parsingInfo().getBitSize());
        ASSERT_EQ(BIT_POSITION_ITEMS2, main.getItems().at(2).parsingInfo().getBitPosition());
        ASSERT_EQ(BIT_SIZE_ITEMS2, main.getItems().at(2).parsingInfo().getBitSize());
    }

    static const char* const PATH;

    static const uint32_t ITEM_VALUE;
    static const int8_t EXTRA_VALUE;
    static const uint32_t UINT32_VALUE;
    static const char* const STRING_VALUE;
    static const bool OPTIONAL_VALUE;
    static const uint32_t ITEMS_VALUE0;
    static const int8_t ITEMS_EXTRA_VALUE0;
    static const uint32_t ITEMS_VALUE1;
    static const int8_t ITEMS_EXTRA_VALUE1;
    static const uint32_t ITEMS_VALUE2;
    static const int8_t ITEMS_EXTRA_VALUE2;

    static const size_t BIT_SIZE_ITEM_CHOICE;
    static const size_t BIT_SIZE_ITEM;
    static const size_t BIT_SIZE_VALUE_UNION;
    static const size_t BIT_SIZE_SIMPLE_STRUCT;
    static const size_t BIT_SIZE_MAIN_ITEM;
    static const size_t BIT_SIZE_MAIN;
    static const size_t BIT_SIZE_ITEMS0;
    static const size_t BIT_SIZE_ITEMS1;
    static const size_t BIT_SIZE_ITEMS2;

    static const size_t BIT_POSITION_MAIN;
    static const size_t BIT_POSITION_ITEM_CHOICE;
    static const size_t BIT_POSITION_ITEM;
    static const size_t BIT_POSITION_VALUE_UNION;
    static const size_t BIT_POSITION_SIMPLE_STRUCT;
    static const size_t BIT_POSITION_MAIN_ITEM;
    static const size_t BIT_POSITION_ITEMS0;
    static const size_t BIT_POSITION_ITEMS1;
    static const size_t BIT_POSITION_ITEMS2;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const char* const WithParsingInfoCode::PATH = "arguments/with_parsing_info_code/gen/with_parsing_info_code/";

const uint32_t WithParsingInfoCode::ITEM_VALUE = 0xDEADBEEF;
const int8_t WithParsingInfoCode::EXTRA_VALUE = -34;
const uint32_t WithParsingInfoCode::UINT32_VALUE = 0xCAFECAFE;
const char* const WithParsingInfoCode::STRING_VALUE = "Supercalifragilisticexpialidocious";
const bool WithParsingInfoCode::OPTIONAL_VALUE = true;
const uint32_t WithParsingInfoCode::ITEMS_VALUE0 = 1000;
const int8_t WithParsingInfoCode::ITEMS_EXTRA_VALUE0 = 10;
const uint32_t WithParsingInfoCode::ITEMS_VALUE1 = 1001;
const int8_t WithParsingInfoCode::ITEMS_EXTRA_VALUE1 = 12;
const uint32_t WithParsingInfoCode::ITEMS_VALUE2 = 1002;
const int8_t WithParsingInfoCode::ITEMS_EXTRA_VALUE2 = 14;

const size_t WithParsingInfoCode::BIT_SIZE_ITEM_CHOICE = 32 + 8;
const size_t WithParsingInfoCode::BIT_SIZE_ITEM = 32 + 8;
const size_t WithParsingInfoCode::BIT_SIZE_VALUE_UNION = 8 + 32;
const size_t WithParsingInfoCode::BIT_SIZE_SIMPLE_STRUCT =
        // string length in varsize + string + optional flag in bool + bool
        8 + 8 * sizeof("Supercalifragilisticexpialidocious") - 8 + 1 + 1;
const size_t WithParsingInfoCode::BIT_SIZE_MAIN_ITEM = 32;
const size_t WithParsingInfoCode::BIT_SIZE_ITEMS0 =
        1 + 6 + 32 + 1 + 6 + 8; // packing descriptor + value 0 + packing descriptor + extra value 0
const size_t WithParsingInfoCode::BIT_SIZE_ITEMS1 = 2 + 3; // value delta + extra value delta
const size_t WithParsingInfoCode::BIT_SIZE_ITEMS2 = 2 + 3; // value delta + extra value delta
const size_t WithParsingInfoCode::BIT_SIZE_MAIN = BIT_SIZE_ITEM_CHOICE + BIT_SIZE_VALUE_UNION +
        BIT_SIZE_SIMPLE_STRUCT + BIT_SIZE_MAIN_ITEM + 8 + BIT_SIZE_ITEMS0 + BIT_SIZE_ITEMS1 + BIT_SIZE_ITEMS2;

const size_t WithParsingInfoCode::BIT_POSITION_MAIN = 0;
const size_t WithParsingInfoCode::BIT_POSITION_ITEM_CHOICE = 0;
const size_t WithParsingInfoCode::BIT_POSITION_ITEM = 0;
const size_t WithParsingInfoCode::BIT_POSITION_VALUE_UNION = BIT_SIZE_ITEM_CHOICE;
const size_t WithParsingInfoCode::BIT_POSITION_SIMPLE_STRUCT = BIT_POSITION_VALUE_UNION + BIT_SIZE_VALUE_UNION;
const size_t WithParsingInfoCode::BIT_POSITION_MAIN_ITEM = BIT_POSITION_SIMPLE_STRUCT + BIT_SIZE_SIMPLE_STRUCT;
const size_t WithParsingInfoCode::BIT_POSITION_ITEMS0 = BIT_POSITION_MAIN_ITEM + BIT_SIZE_MAIN_ITEM + 8;
const size_t WithParsingInfoCode::BIT_POSITION_ITEMS1 = BIT_POSITION_ITEMS0 + BIT_SIZE_ITEMS0;
const size_t WithParsingInfoCode::BIT_POSITION_ITEMS2 = BIT_POSITION_ITEMS1 + BIT_SIZE_ITEMS1;

TEST_F(WithParsingInfoCode, checkParsingInfoMethods)
{
    ASSERT_METHOD_PRESENT(PATH, "Item", "const ::zserio::ParsingInfo& parsingInfo() const",
            "const ::zserio::ParsingInfo& Item::parsingInfo() const");
    ASSERT_METHOD_PRESENT(PATH, "ItemChoice", "const ::zserio::ParsingInfo& parsingInfo() const",
            "const ::zserio::ParsingInfo& ItemChoice::parsingInfo() const");
    ASSERT_METHOD_PRESENT(PATH, "ValueUnion", "const ::zserio::ParsingInfo& parsingInfo() const",
            "const ::zserio::ParsingInfo& ValueUnion::parsingInfo() const");
    ASSERT_METHOD_PRESENT(PATH, "SimpleStruct", "const ::zserio::ParsingInfo& parsingInfo() const",
            "const ::zserio::ParsingInfo& SimpleStruct::parsingInfo() const");
    ASSERT_METHOD_PRESENT(PATH, "Main", "const ::zserio::ParsingInfo& parsingInfo() const",
            "const ::zserio::ParsingInfo& Main::parsingInfo() const");
}

TEST_F(WithParsingInfoCode, readConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeMain(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const Main main(reader);
    checkMain(main);
    checkParsingInfo(main);
}

TEST_F(WithParsingInfoCode, copyConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeMain(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const Main main(reader);
    const Main mainCopy(main);
    checkMain(mainCopy);
    checkParsingInfo(mainCopy);
}

TEST_F(WithParsingInfoCode, moveConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeMain(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Main main(reader);
    const Main mainMove(std::move(main));
    checkMain(mainMove);
    checkParsingInfo(mainMove);
}

TEST_F(WithParsingInfoCode, assignmentOperator)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeMain(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const Main main(reader);
    Main mainAssign;
    mainAssign = main;
    checkMain(mainAssign);
    checkParsingInfo(mainAssign);
}

TEST_F(WithParsingInfoCode, moveAssignmentOperator)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeMain(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Main main(reader);
    Main mainMoveAssign;
    mainMoveAssign = std::move(main);
    checkMain(mainMoveAssign);
    checkParsingInfo(mainMoveAssign);
}

} // namespace with_parsing_info_code
