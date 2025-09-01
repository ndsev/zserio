#include <type_traits>

#include "gtest/gtest.h"
#include "test_utils/Assertions.h"
#include "without_writer_code/Tile.h"
#include "without_writer_code/WorldDb.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"
#include "zserio/StringView.h"
#include "zserio/pmr/PolymorphicAllocator.h"

#include "WithoutWriterCode.h"

namespace without_writer_code
{

class WithoutWriterCode : public ::testing::Test
{
protected:
    void createWorldDb(zserio::SqliteConnection& db)
    {
        sqlite3* connection = nullptr;
        const int result =
                sqlite3_open_v2(":memory:", &connection, SQLITE_OPEN_CREATE | SQLITE_OPEN_READWRITE, nullptr);
        db.reset(connection);
        ASSERT_EQ(SQLITE_OK, result);

        db.executeUpdate("CREATE TABLE europe(tileId INTEGER PRIMARY KEY, tile BLOB)");
        db.executeUpdate("CREATE TABLE america(tileId INTEGER PRIMARY KEY, tile BLOB)");

        zserio::BitStreamWriter writer(bitBuffer);
        writeTile(writer);
        const uint8_t* buffer = writer.getWriteBuffer();
        size_t writtenByteSize = (writer.getBitPosition() + 7) / 8;

        sqlite3_stmt* const stmtEurope = db.prepareStatement("INSERT INTO europe VALUES (?, ?)");
        ASSERT_TRUE(stmtEurope != nullptr);
        sqlite3_bind_int(stmtEurope, 1, TILE_ID_EUROPE);
        sqlite3_bind_blob(stmtEurope, 2, buffer, static_cast<int>(writtenByteSize), SQLITE_TRANSIENT);
        ASSERT_EQ(SQLITE_DONE, sqlite3_step(stmtEurope));
        ASSERT_EQ(SQLITE_OK, sqlite3_finalize(stmtEurope));

        sqlite3_stmt* const stmtAmerica = db.prepareStatement("INSERT INTO america VALUES (?, ?)");
        ASSERT_TRUE(stmtAmerica != nullptr);
        sqlite3_bind_int(stmtAmerica, 1, TILE_ID_AMERICA);
        sqlite3_bind_blob(stmtAmerica, 2, buffer, static_cast<int>(writtenByteSize), SQLITE_TRANSIENT);
        ASSERT_EQ(SQLITE_DONE, sqlite3_step(stmtAmerica));
        ASSERT_EQ(SQLITE_OK, sqlite3_finalize(stmtAmerica));
    }

    void writeTile(zserio::BitStreamWriter& writer)
    {
        // Tile
        writer.writeBits(VERSION_AVAILABILITY, 3);
        writer.writeBits(VERSION, 8);
        writer.writeBool(true);
        writer.writeBits(OPTIONAL_VERSION_INFO, 8);
        writer.writeBits(3 + 4, 32); // numElementsOffset
        writer.alignTo(8);
        writer.writeBits(NUM_ELEMENTS, 32);

        // offsets
        size_t offset = writer.getBitPosition() / 8 + 4 * NUM_ELEMENTS;
        for (uint32_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            writer.writeBits(static_cast<uint32_t>(offset), 32);
            const bool hasItem = i % 2 == 0;
            if (hasItem)
            {
                offset += 8;
            }
            else
            {
                offset += 3;
            }
        }

        for (uint32_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            writer.alignTo(8); // aligned because of indexed offsets
            // ItemChoiceHolder
            const bool hasItem = i % 2 == 0; // hasItem == true for even elements
            writer.writeBool(hasItem);
            if (hasItem)
            {
                // Item
                writer.writeBits(PARAMS0, 16);
                // ExtraParamUnion - choiceTag CHOICE_value32
                writer.writeVarSize(static_cast<uint32_t>(ExtraParamUnion::CHOICE_value32));
                writer.writeBits(EXTRA_PARAM, 32);
            }
            else
            {
                writer.writeBits(PARAMS1, 16);
            }
        }
    }

    void checkTile(Tile& tile)
    {
        checkTile(static_cast<const Tile&>(tile));

        // const version must be called since non-const version is not available
        static_assert(std::is_same<zserio::IBasicReflectableConstPtr<allocator_type>,
                              decltype(tile.reflectable())>::value,
                "Const version shall be called!");
    }

    void checkTile(const Tile& tile)
    {
        ASSERT_EQ(VERSION_AVAILABILITY, tile.getVersionAvailability().getValue());
        ASSERT_EQ(VERSION, tile.getVersion());
        ASSERT_EQ(OPTIONAL_VERSION_INFO, tile.getOptionalVersionInfo());

        ASSERT_EQ(NUM_ELEMENTS, tile.getNumElements());

        const auto& data = tile.getData();
        ASSERT_EQ(NUM_ELEMENTS, data.size());

        // element 0
        ASSERT_TRUE(data[0].getHasItem());
        const ItemChoice& itemChoice0 = data[0].getItemChoice();
        ASSERT_TRUE(itemChoice0.getHasItem());
        const Item& item0 = itemChoice0.getItem();
        ASSERT_EQ(PARAMS0, item0.getParam());
        ASSERT_EQ(ItemType::WITH_EXTRA_PARAM, item0.getItemType());
        ASSERT_EQ(ExtraParamUnion::CHOICE_value32, item0.getExtraParam().choiceTag());
        ASSERT_EQ(EXTRA_PARAM, item0.getExtraParam().getValue32());

        // element 1
        ASSERT_FALSE(data[1].getHasItem());
        const ItemChoice& itemChoice1 = data[1].getItemChoice();
        ASSERT_FALSE(itemChoice1.getHasItem());
        ASSERT_EQ(PARAMS1, itemChoice1.getParam());

        auto reflectable = tile.reflectable();
        ASSERT_TRUE(reflectable);

        // not implemented without writer code
        zserio::BitBuffer dummyBitBuffer;
        zserio::BitStreamWriter writer(dummyBitBuffer);
        ASSERT_THROW(reflectable->write(writer), zserio::CppRuntimeException);
    }

    static const std::string BLOB_NAME;
    static const char* const PATH;
    static const int32_t TILE_ID_EUROPE;
    static const int32_t TILE_ID_AMERICA;
    static const uint8_t VERSION_AVAILABILITY;
    static const uint8_t VERSION;
    static const uint8_t OPTIONAL_VERSION_INFO;
    static const uint32_t NUM_ELEMENTS;
    static const uint16_t PARAMS0;
    static const uint16_t PARAMS1;
    static const uint32_t EXTRA_PARAM;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string WithoutWriterCode::BLOB_NAME = "arguments/without_writer_code/without_writer_code.blob";
const char* const WithoutWriterCode::PATH =
        "arguments/without_writer_code/gen_without_setters/without_writer_code/";
const int32_t WithoutWriterCode::TILE_ID_EUROPE = 99;
const int32_t WithoutWriterCode::TILE_ID_AMERICA = 11;
const uint8_t WithoutWriterCode::VERSION_AVAILABILITY = 0x01;
const uint8_t WithoutWriterCode::VERSION = 8;
const uint8_t WithoutWriterCode::OPTIONAL_VERSION_INFO = 0xBA;
const uint32_t WithoutWriterCode::NUM_ELEMENTS = 2;
const uint16_t WithoutWriterCode::PARAMS0 = 13;
const uint16_t WithoutWriterCode::PARAMS1 = 21;
const uint32_t WithoutWriterCode::EXTRA_PARAM = 42;

TEST_F(WithoutWriterCode, checkItemTypeMethods)
{
    const char* type = "ItemType";

    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets<", "size_t initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write<", "void write<");

    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::ITEM_TYPE_TYPE_INFO, MethodNames::ITEM_TYPE_TYPE_INFO);
    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::ITEM_TYPE_REFLECTABLE, MethodNames::ITEM_TYPE_REFLECTABLE);

    ASSERT_METHOD_PRESENT(PATH, type, "size_t enumToOrdinal<", "size_t enumToOrdinal(");
    ASSERT_METHOD_PRESENT(PATH, type, "ItemType valueToEnum<", "ItemType valueToEnum(");
    ASSERT_METHOD_PRESENT(PATH, type, "size_t bitSizeOf<::without_writer_code::ItemType>",
            "size_t bitSizeOf(::without_writer_code::ItemType");
    ASSERT_METHOD_PRESENT(PATH, type, "ItemType read<::without_writer_code::ItemType",
            "ItemType read(::zserio::BitStreamReader&");
}

TEST_F(WithoutWriterCode, checkVersionAvailabilityMethods)
{
    const char* type = "VersionAvailability";

    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, "size_t initializeOffsets(", "size_t VersionAvailability::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void VersionAvailability::write(");

    ASSERT_METHOD_PRESENT(PATH, type, "constexpr VersionAvailability() noexcept", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "VersionAvailability(::zserio::BitStreamReader&",
            "VersionAvailability::VersionAvailability(::zserio::BitStreamReader&");
    ASSERT_METHOD_PRESENT(PATH, type, "constexpr VersionAvailability(Values value) noexcept :", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "constexpr explicit VersionAvailability(underlying_type value)", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "~VersionAvailability() = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "VersionAvailability(const VersionAvailability&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(
            PATH, type, "VersionAvailability& operator=(const VersionAvailability&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "VersionAvailability(VersionAvailability&&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(
            PATH, type, "VersionAvailability& operator=(VersionAvailability&&) = default;", nullptr);

    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::TYPE_INFO_DECLARATION,
            MethodNames::VERSION_AVAILABILITY_TYPE_INFO_DEFINITION);
    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::VERSION_AVAILABILITY_REFLECTABLE_DECLARATION,
            MethodNames::VERSION_AVAILABILITY_REFLECTABLE_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "constexpr explicit operator underlying_type() const", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "constexpr underlying_type getValue() const", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "size_t bitSizeOf(size_t bitPosition = 0) const",
            "size_t VersionAvailability::bitSizeOf(size_t) const");
    ASSERT_METHOD_PRESENT(
            PATH, type, "uint32_t hashCode() const", "uint32_t VersionAvailability::hashCode() const");
    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::TO_STRING_DECLARATION, MethodNames::TO_STRING_DEFINITION);
}

TEST_F(WithoutWriterCode, checkExtraParamUnionMethods)
{
    const char* type = "ExtraParamUnion";

    ASSERT_METHOD_NOT_PRESENT(PATH, type, " ExtraParamUnion()", "ExtraParamUnion::ExtraParamUnion()");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, "size_t initializeOffsets(", "size_t ExtraParamUnion::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void ExtraParamUnion::write(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, MethodNames::REFLECTABLE_DECLARATION,
            MethodNames::EXTRA_PARAM_UNION_REFLECTABLE_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "ExtraParamUnion(::zserio::BitStreamReader&",
            "ExtraParamUnion::ExtraParamUnion(::zserio::BitStreamReader&");
    ASSERT_METHOD_PRESENT(PATH, type, "~ExtraParamUnion() = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "ExtraParamUnion(const ExtraParamUnion&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "ExtraParamUnion& operator=(const ExtraParamUnion&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "ExtraParamUnion(ExtraParamUnion&&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "ExtraParamUnion& operator=(ExtraParamUnion&&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "ExtraParamUnion(::zserio::PropagateAllocatorT,",
            "ExtraParamUnion::ExtraParamUnion(::zserio::PropagateAllocatorT,");

    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::TYPE_INFO_DECLARATION,
            MethodNames::EXTRA_PARAM_UNION_TYPE_INFO_DEFINITION);
    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::REFLECTABLE_CONST_DECLARATION,
            MethodNames::EXTRA_PARAM_UNION_REFLECTABLE_CONST_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "ChoiceTag choiceTag(", "ChoiceTag ExtraParamUnion::choiceTag(");
    ASSERT_METHOD_PRESENT(PATH, type, "uint16_t getValue16(", "uint16_t ExtraParamUnion::getValue16(");
    ASSERT_METHOD_PRESENT(PATH, type, "uint32_t getValue32(", "uint32_t ExtraParamUnion::getValue32(");
    ASSERT_METHOD_PRESENT(PATH, type, "size_t bitSizeOf(", "size_t ExtraParamUnion::bitSizeOf(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool operator==(", "bool ExtraParamUnion::operator==(");
    ASSERT_METHOD_PRESENT(PATH, type, "uint32_t hashCode(", "uint32_t ExtraParamUnion::hashCode(");
}

TEST_F(WithoutWriterCode, checkItemMethods)
{
    const char* type = "Item";

    ASSERT_METHOD_NOT_PRESENT(PATH, type, " Item()", "Item::Item()");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setExtraParam(", "void Item::setExtraParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "bool isExtraParamSet(", "bool Item::isExtraParamSet(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void resetExtraParam(", "void Item::resetExtraParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setParam(", "void Item::setParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Item::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void Item::write(");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, MethodNames::REFLECTABLE_DECLARATION, MethodNames::ITEM_REFLECTABLE_DEFINITION);

    ASSERT_METHOD_PRESENT(
            PATH, type, "Item(::zserio::BitStreamReader&", "Item::Item(::zserio::BitStreamReader&");
    ASSERT_METHOD_PRESENT(PATH, type, "~Item() = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "Item(const Item&", "Item::Item(const Item&");
    ASSERT_METHOD_PRESENT(PATH, type, "Item& operator=(const Item&", "Item& Item::operator=(const Item&");
    ASSERT_METHOD_PRESENT(PATH, type, "Item(Item&&", "Item::Item(Item&&");
    ASSERT_METHOD_PRESENT(PATH, type, "Item& operator=(Item&&", "Item& Item::operator=(Item&&");
    ASSERT_METHOD_PRESENT(
            PATH, type, "Item(::zserio::PropagateAllocatorT,", "Item::Item(::zserio::PropagateAllocatorT,");

    ASSERT_METHOD_PRESENT(
            PATH, type, MethodNames::TYPE_INFO_DECLARATION, MethodNames::ITEM_TYPE_INFO_DEFINITION);
    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::REFLECTABLE_CONST_DECLARATION,
            MethodNames::ITEM_REFLECTABLE_CONST_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "void initialize(", "void Item::initialize(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool isInitialized(", "bool Item::isInitialized(");
    ASSERT_METHOD_PRESENT(PATH, type, "ItemType getItemType() const", "ItemType Item::getItemType() const");
    ASSERT_METHOD_PRESENT(PATH, type, "uint16_t getParam(", "uint16_t Item::getParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "& getExtraParam(", "& Item::getExtraParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool isExtraParamUsed(", "bool Item::isExtraParamUsed(");
    ASSERT_METHOD_PRESENT(PATH, type, "size_t bitSizeOf(", "size_t Item::bitSizeOf(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool operator==(", "bool Item::operator==(");
    ASSERT_METHOD_PRESENT(PATH, type, "uint32_t hashCode(", "uint32_t Item::hashCode(");
}

TEST_F(WithoutWriterCode, checkItemChoiceMethods)
{
    const char* type = "ItemChoice";

    ASSERT_METHOD_NOT_PRESENT(PATH, type, " ItemChoice()", "ItemChoice::ItemChoice()");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t ItemChoice::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void ItemChoice::write(");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, MethodNames::REFLECTABLE_DECLARATION, MethodNames::ITEM_CHOICE_REFLECTABLE_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "ItemChoice(::zserio::BitStreamReader&",
            "ItemChoice::ItemChoice(::zserio::BitStreamReader&");
    ASSERT_METHOD_PRESENT(PATH, type, "~ItemChoice() = default;", nullptr);
    ASSERT_METHOD_PRESENT(
            PATH, type, "ItemChoice(const ItemChoice&", "ItemChoice::ItemChoice(const ItemChoice&");
    ASSERT_METHOD_PRESENT(PATH, type, "ItemChoice& operator=(const ItemChoice&",
            "ItemChoice& ItemChoice::operator=(const ItemChoice&");
    ASSERT_METHOD_PRESENT(PATH, type, "ItemChoice(ItemChoice&&", "ItemChoice::ItemChoice(ItemChoice&&");
    ASSERT_METHOD_PRESENT(
            PATH, type, "ItemChoice& operator=(ItemChoice&&", "ItemChoice& ItemChoice::operator=(ItemChoice&&");
    ASSERT_METHOD_PRESENT(PATH, type, "ItemChoice(::zserio::PropagateAllocatorT,",
            "ItemChoice::ItemChoice(::zserio::PropagateAllocatorT,");

    ASSERT_METHOD_PRESENT(
            PATH, type, MethodNames::TYPE_INFO_DECLARATION, MethodNames::ITEM_CHOICE_TYPE_INFO_DEFINITION);
    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::REFLECTABLE_CONST_DECLARATION,
            MethodNames::ITEM_CHOICE_REFLECTABLE_CONST_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "void initialize(", "void ItemChoice::initialize(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool isInitialized(", "bool ItemChoice::isInitialized(");
    ASSERT_METHOD_PRESENT(PATH, type, "void initializeChildren(", "void ItemChoice::initializeChildren(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool getHasItem(", "bool ItemChoice::getHasItem(");
    ASSERT_METHOD_PRESENT(PATH, type, "Item& getItem(", "Item& ItemChoice::getItem(");
    ASSERT_METHOD_PRESENT(PATH, type, "uint16_t getParam(", "uint16_t ItemChoice::getParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "size_t bitSizeOf(", "size_t ItemChoice::bitSizeOf(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool operator==(", "bool ItemChoice::operator==(");
    ASSERT_METHOD_PRESENT(PATH, type, "uint32_t hashCode(", "uint32_t ItemChoice::hashCode(");
}

TEST_F(WithoutWriterCode, checkItemChoiceHolderMethods)
{
    const char* type = "ItemChoiceHolder";

    ASSERT_METHOD_NOT_PRESENT(PATH, type, " ItemChoiceHolder()", "ItemChoiceHolder::ItemChoiceHolder()");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, "size_t initializeOffsets(", "size_t ItemChoiceHolder::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void ItemChoiceHolder::write(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, MethodNames::REFLECTABLE_DECLARATION,
            MethodNames::ITEM_CHOICE_HOLDER_REFLECTABLE_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "ItemChoiceHolder(::zserio::BitStreamReader&",
            "ItemChoiceHolder::ItemChoiceHolder(::zserio::BitStreamReader&");
    ASSERT_METHOD_PRESENT(PATH, type, "~ItemChoiceHolder() = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "ItemChoiceHolder(const ItemChoiceHolder&",
            "ItemChoiceHolder::ItemChoiceHolder(const ItemChoiceHolder&");
    ASSERT_METHOD_PRESENT(PATH, type, "ItemChoiceHolder& operator=(const ItemChoiceHolder&",
            "ItemChoiceHolder& ItemChoiceHolder::operator=(const ItemChoiceHolder&");
    ASSERT_METHOD_PRESENT(PATH, type, "ItemChoiceHolder(ItemChoiceHolder&&",
            "ItemChoiceHolder::ItemChoiceHolder(ItemChoiceHolder&&");
    ASSERT_METHOD_PRESENT(PATH, type, "ItemChoiceHolder& operator=(ItemChoiceHolder&&",
            "ItemChoiceHolder& ItemChoiceHolder::operator=(ItemChoiceHolder&&");
    ASSERT_METHOD_PRESENT(PATH, type, "ItemChoiceHolder(::zserio::PropagateAllocatorT,",
            "ItemChoiceHolder::ItemChoiceHolder(::zserio::PropagateAllocatorT,");

    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::TYPE_INFO_DECLARATION,
            MethodNames::ITEM_CHOICE_HOLDER_TYPE_INFO_DEFINITION);
    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::REFLECTABLE_CONST_DECLARATION,
            MethodNames::ITEM_CHOICE_HOLDER_REFLECTABLE_CONST_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "void initializeChildren(", "void ItemChoiceHolder::initializeChildren(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool getHasItem(", "bool ItemChoiceHolder::getHasItem(");
    ASSERT_METHOD_PRESENT(
            PATH, type, "ItemChoice& getItemChoice(", "ItemChoice& ItemChoiceHolder::getItemChoice(");
    ASSERT_METHOD_PRESENT(PATH, type, "size_t bitSizeOf(", "size_t ItemChoiceHolder::bitSizeOf(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool operator==(", "bool ItemChoiceHolder::operator==(");
    ASSERT_METHOD_PRESENT(PATH, type, "uint32_t hashCode(", "uint32_t ItemChoiceHolder::hashCode(");
}

TEST_F(WithoutWriterCode, checkTileMethods)
{
    const char* type = "Tile";

    ASSERT_METHOD_NOT_PRESENT(PATH, type, " Tile()", "Tile::Tile()");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setVersion(", "void Tile::setVersion(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "bool isVersionSet(", "bool Tile::isVersionSet(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void resetVersion(", "void Tile::resetVersion(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setNumElementOffset(", "void Tile::setNumElementOffset(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setVersionString(", "void Tile::setVersionString(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "bool isVersionStringSet(", "bool Tile::isVersionStringSet(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void resetVersionString(", "void Tile::resetVersionString(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setOptionalVersionInfo(", "void Tile::setOptionalVersionInfo(");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, "bool isOptionalVersionInfoSet(", "bool Tile::isOptionalVersionInfoSet(");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, "void resetOptionalVersionInfo(", "void Tile::resetOptionalVersionInfo(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setNumElements(", "void Tile::setNumElements(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setData(", "void Tile::setData(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Tile::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void Tile::write(");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, MethodNames::REFLECTABLE_DECLARATION, MethodNames::TILE_REFLECTABLE_DEFINITION);

    ASSERT_METHOD_PRESENT(
            PATH, type, "Tile(::zserio::BitStreamReader&", "Tile::Tile(::zserio::BitStreamReader&");
    ASSERT_METHOD_PRESENT(PATH, type, "~Tile() = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "Tile(const Tile&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "Tile& operator=(const Tile&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "Tile(Tile&&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "Tile& operator=(Tile&&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(
            PATH, type, "Tile(::zserio::PropagateAllocatorT,", "Tile::Tile(::zserio::PropagateAllocatorT,");

    ASSERT_METHOD_PRESENT(
            PATH, type, MethodNames::TYPE_INFO_DECLARATION, MethodNames::TILE_TYPE_INFO_DEFINITION);
    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::REFLECTABLE_CONST_DECLARATION,
            MethodNames::TILE_REFLECTABLE_CONST_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "void initializeChildren(", "void Tile::initializeChildren(");
    ASSERT_METHOD_PRESENT(PATH, type, "VersionAvailability getVersionAvailability() const",
            "VersionAvailability Tile::getVersionAvailability() const");
    ASSERT_METHOD_PRESENT(PATH, type, "uint8_t getVersion(", "uint8_t Tile::getVersion(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool isVersionUsed(", "bool Tile::isVersionUsed(");
    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::GET_VERSION_STRING_DECLARATION,
            MethodNames::GET_VERSION_STRING_DEFINITION);
    ASSERT_METHOD_PRESENT(PATH, type, "bool isVersionStringUsed(", "bool Tile::isVersionStringUsed(");
    ASSERT_METHOD_PRESENT(
            PATH, type, "uint8_t getOptionalVersionInfo(", "uint8_t Tile::getOptionalVersionInfo(");
    ASSERT_METHOD_PRESENT(
            PATH, type, "bool isOptionalVersionInfoUsed(", "bool Tile::isOptionalVersionInfoUsed(");
    ASSERT_METHOD_PRESENT(PATH, type, "uint32_t getNumElementsOffset(", "uint32_t Tile::getNumElementsOffset(");
    ASSERT_METHOD_PRESENT(PATH, type, "uint32_t getNumElements(", "uint32_t Tile::getNumElements(");
    ASSERT_METHOD_PRESENT(PATH, type, "& getOffsets() const", "& Tile::getOffsets() const");
    ASSERT_METHOD_PRESENT(PATH, type, "& getData() const", "& Tile::getData() const");
    ASSERT_METHOD_PRESENT(PATH, type, "size_t bitSizeOf(", "size_t Tile::bitSizeOf(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool operator==(", "bool Tile::operator==");
    ASSERT_METHOD_PRESENT(PATH, type, "uint32_t hashCode(", "uint32_t Tile::hashCode(");

    const auto& typeInfo = Tile::typeInfo();
    ASSERT_EQ("without_writer_code.Tile", zserio::toString(typeInfo.getSchemaName()));

    // not implemented without writer code
    ASSERT_THROW(typeInfo.createInstance(), zserio::CppRuntimeException);
}

TEST_F(WithoutWriterCode, checkGeoMapTableMethods)
{
    const char* type = "GeoMapTable";

    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void createTable(", "void GeoMapTable::createTable(");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, "void createOrdinaryRowIdTable(", "void GeoMapTable::createOrdinaryRowIdTable(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void deleteTable(", "void GeoMapTable::deleteTable(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void GeoMapTable::write(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void update(", "void GeoMapTable::update(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void writeRow(", "void GeoMapTable::writeRow(");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, "void appendCreateTableToQuery(", "void GeoMapTable::appendCreateTableToQuery(");

    ASSERT_METHOD_PRESENT(PATH, type, "GeoMapTable(::zserio::SqliteConnection&",
            "GeoMapTable::GeoMapTable(::zserio::SqliteConnection&");
    ASSERT_METHOD_PRESENT(PATH, type, "~GeoMapTable() = default", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "GeoMapTable(const GeoMapTable&) = delete;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "GeoMapTable& operator=(const GeoMapTable&) = delete;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "GeoMapTable(GeoMapTable&&) = delete;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "GeoMapTable& operator=(GeoMapTable&&) = delete;", nullptr);

    ASSERT_METHOD_PRESENT(
            PATH, type, MethodNames::TYPE_INFO_DECLARATION, MethodNames::GEO_MAP_TABLE_TYPE_INFO_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "Reader createReader(", "Reader GeoMapTable::createReader(");
}

TEST_F(WithoutWriterCode, checkWorldDbMethods)
{
    const char* type = "WorldDb";

    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void createSchema(", "void WorldDb::createSchema(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void deleteSchema(", "void WorldDb::deleteSchema(");

    ASSERT_METHOD_PRESENT(
            PATH, type, MethodNames::WORLD_DB_CTOR_DECLARATION, MethodNames::WORLD_DB_CTOR_DEFINITION);
    ASSERT_METHOD_PRESENT(PATH, type, "WorldDb(sqlite3*", "WorldDb::WorldDb(sqlite3*");
    ASSERT_METHOD_PRESENT(PATH, type, "~WorldDb()", "WorldDb::~WorldDb(");
    ASSERT_METHOD_PRESENT(PATH, type, "WorldDb(const WorldDb&) = delete;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "WorldDb& operator=(const WorldDb&) = delete;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "WorldDb(WorldDb&&) = delete;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "WorldDb& operator=(WorldDb&&) = delete;", nullptr);

    ASSERT_METHOD_PRESENT(
            PATH, type, MethodNames::TYPE_INFO_DECLARATION, MethodNames::WORLD_DB_TYPE_INFO_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "::zserio::SqliteConnection& connection(",
            "::zserio::SqliteConnection& WorldDb::connection(");
    ASSERT_METHOD_PRESENT(PATH, type, "GeoMapTable& getEurope(", "GeoMapTable& WorldDb::getEurope(");
    ASSERT_METHOD_PRESENT(PATH, type, "GeoMapTable& getAmerica(", "GeoMapTable& WorldDb::getAmerica(");
    ASSERT_METHOD_PRESENT(PATH, type, "static ::zserio::StringView databaseName() noexcept",
            "::zserio::StringView WorldDb::databaseName() noexcept");
    ASSERT_METHOD_PRESENT(PATH, type,
            "static const ::std::array<::zserio::StringView, 2>& tableNames() noexcept",
            "const ::std::array<::zserio::StringView, 2>& WorldDb::tableNames() noexcept");
}

TEST_F(WithoutWriterCode, readConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeTile(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Tile tile = Tile(reader);

    checkTile(tile);
}

TEST_F(WithoutWriterCode, readFile)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeTile(writer);
    zserio::writeBufferToFile(writer, BLOB_NAME);

    const Tile tile = zserio::deserializeFromFile<Tile>(BLOB_NAME);
    checkTile(tile);
}

TEST_F(WithoutWriterCode, readWorldDb)
{
    zserio::SqliteConnection db;
    createWorldDb(db);

    WorldDb worldDb(db.getConnection());

    const GeoMapTable& europe = worldDb.getEurope();
    GeoMapTable::Reader europeReader = europe.createReader();
    ASSERT_TRUE(europeReader.hasNext());
    GeoMapTable::Row europeRow = europeReader.next();
    ASSERT_FALSE(europeReader.hasNext());

    ASSERT_EQ(TILE_ID_EUROPE, europeRow.getTileId());
    checkTile(europeRow.getTile());

    const GeoMapTable& america = worldDb.getAmerica();
    GeoMapTable::Reader americaReader = america.createReader();
    ASSERT_TRUE(americaReader.hasNext());
    GeoMapTable::Row americaRow = americaReader.next();
    ASSERT_FALSE(americaReader.hasNext());

    ASSERT_EQ(TILE_ID_AMERICA, americaRow.getTileId());
    checkTile(americaRow.getTile());
}

} // namespace without_writer_code
