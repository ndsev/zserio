#include <fstream>
#include <string>
#include <vector>

#include "gtest/gtest.h"
#include "sqlite3.h"

#include "zserio/BitStreamWriter.h"
#include "without_writer_code/Tile.h"
#include "without_writer_code/WorldDb.h"

namespace without_writer_code
{

class WithoutWriterCode : public ::testing::Test
{
protected:
    bool isStringInFilePresent(const std::string& fileName, const char* str)
    {
        std::ifstream file(fileName.c_str());
        bool isPresent = false;
        std::string line;
        while (std::getline(file, line))
        {
            if (line.find(str) != std::string::npos)
            {
                isPresent = true;
                break;
            }
        }
        file.close();

        return isPresent;
    }

    void assertMethodNotPresent(const char* typeName, const char* declaration, const char* definition)
    {
        const std::string filePath = std::string(PATH) + typeName;
        if (declaration != NULL)
        {
            ASSERT_FALSE(isStringInFilePresent(filePath + ".h", declaration))
                    << "Method declaration '" << declaration << "' is present in '" << typeName << "'!";
        }
        if (definition != NULL)
        {
            ASSERT_FALSE(isStringInFilePresent(filePath + ".cpp", definition))
                    << "Method definition '" << definition << "' is present'" << typeName << "'!";
        }
    }

    void assertMethodPresent(const char* typeName, const char* declaration, const char* definition)
    {
        const std::string filePath = std::string(PATH) + typeName;
        if (declaration != NULL)
        {
            ASSERT_TRUE(isStringInFilePresent(filePath + ".h", declaration))
                    << "Method declaration '" << declaration << "' is not present in '" << typeName << "'!";
        }
        if (definition != NULL)
        {
            ASSERT_TRUE(isStringInFilePresent(filePath + ".cpp", definition))
                    << "Method definition '" << definition << "' is not present in '" << typeName << "'!";
        }
    }

    void createWorldDb(zserio::SqliteConnection& db)
    {
        sqlite3* connection = NULL;
        const int result = sqlite3_open_v2(":memory:", &connection, SQLITE_OPEN_CREATE | SQLITE_OPEN_READWRITE,
                NULL);
        db.reset(connection);
        ASSERT_EQ(SQLITE_OK, result);

        db.executeUpdate("CREATE TABLE europe(tileId INTEGER PRIMARY KEY, tile BLOB)");
        db.executeUpdate("CREATE TABLE america(tileId INTEGER PRIMARY KEY, tile BLOB)");

        zserio::BitStreamWriter writer;
        writeTile(writer);
        size_t size;
        const uint8_t* buffer = writer.getWriteBuffer(size);

        sqlite3_stmt* const stmtEurope = db.prepareStatement("INSERT INTO europe VALUES (?, ?)");
        ASSERT_TRUE(stmtEurope != NULL);
        sqlite3_bind_int(stmtEurope, 1, TILE_ID_EUROPE);
        sqlite3_bind_blob(stmtEurope, 2, buffer, static_cast<int>(size), SQLITE_TRANSIENT);
        ASSERT_EQ(SQLITE_DONE, sqlite3_step(stmtEurope));
        ASSERT_EQ(SQLITE_OK, sqlite3_finalize(stmtEurope));

        sqlite3_stmt* const stmtAmerica = db.prepareStatement("INSERT INTO america VALUES (?, ?)");
        ASSERT_TRUE(stmtAmerica != NULL);
        sqlite3_bind_int(stmtAmerica, 1, TILE_ID_AMERICA);
        sqlite3_bind_blob(stmtAmerica, 2, buffer, static_cast<int>(size), SQLITE_TRANSIENT);
        ASSERT_EQ(SQLITE_DONE, sqlite3_step(stmtAmerica));
        ASSERT_EQ(SQLITE_OK, sqlite3_finalize(stmtAmerica));
    }

    void writeTile(zserio::BitStreamWriter& writer)
    {
        // Tile
        writer.writeBits(VERSION_AVAILABILITY, 3);
        writer.writeBits(VERSION, 8);
        writer.writeBits(6, 32); // numElementsOffset
        writer.alignTo(8);
        writer.writeBits(NUM_ELEMENTS, 32);

        // offsets
        size_t offset = zserio::bitsToBytes(writer.getBitPosition()) + 4 * NUM_ELEMENTS;
        for (uint32_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            writer.writeBits(static_cast<uint32_t>(offset), 32);
            const bool hasItem = i % 2 == 0;
            if (hasItem)
                offset += 8;
            else
                offset += 3;
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
                writer.writeBits(PARAMS[i], 16);
                // ExtraParamUnion - choiceTag CHOICE_value32
                writer.writeVarUInt64(ExtraParamUnion::CHOICE_value32);
                writer.writeBits(EXTRA_PARAM, 32);
            }
            else
            {
                writer.writeBits(PARAMS[i], 16);
            }
        }
    }

    void checkTile(const Tile& tile)
    {
        ASSERT_EQ(VERSION, tile.getVersion());
        ASSERT_EQ(VERSION_AVAILABILITY, tile.getVersionAvailability().getValue());
        ASSERT_EQ(NUM_ELEMENTS, tile.getNumElements());

        const std::vector<ItemChoiceHolder>& data = tile.getData();
        ASSERT_EQ(NUM_ELEMENTS, data.size());

        // element 0
        ASSERT_TRUE(data[0].getHasItem());
        const ItemChoice& itemChoice0 = data[0].getItemChoice();
        ASSERT_TRUE(itemChoice0.getHasItem());
        const Item& item0 = itemChoice0.getItem();
        ASSERT_EQ(PARAMS[0], item0.getParam());
        ASSERT_EQ(ItemType::WITH_EXTRA_PARAM, item0.getItemType());
        ASSERT_EQ(ExtraParamUnion::CHOICE_value32, item0.getExtraParam().choiceTag());
        ASSERT_EQ(EXTRA_PARAM, item0.getExtraParam().getValue32());

        // element 1
        ASSERT_FALSE(data[1].getHasItem());
        const ItemChoice& itemChoice1 = data[1].getItemChoice();
        ASSERT_FALSE(itemChoice1.getHasItem());
        ASSERT_EQ(PARAMS[1], itemChoice1.getParam());
    }

    static const char* PATH;
    static const int32_t TILE_ID_EUROPE;
    static const int32_t TILE_ID_AMERICA;
    static const uint8_t VERSION_AVAILABILITY;
    static const uint8_t VERSION;
    static const uint32_t NUM_ELEMENTS;
    static const uint16_t PARAMS[2];
    static const uint32_t EXTRA_PARAM;
};

const char* WithoutWriterCode::PATH = "arguments/without_writer_code/gen/without_writer_code/";
const int32_t WithoutWriterCode::TILE_ID_EUROPE = 99;
const int32_t WithoutWriterCode::TILE_ID_AMERICA = 11;
const uint8_t WithoutWriterCode::VERSION_AVAILABILITY = 0x01;
const uint8_t WithoutWriterCode::VERSION = 8;
const uint32_t WithoutWriterCode::NUM_ELEMENTS = 2;
const uint16_t WithoutWriterCode::PARAMS[2] = { 13, 21 };
const uint32_t WithoutWriterCode::EXTRA_PARAM = 42;

TEST_F(WithoutWriterCode, checkItemTypeMethods)
{
    const char* type = "ItemType";

    assertMethodNotPresent(type, "size_t initializeOffsets<", "size_t initializeOffsets(");
    assertMethodNotPresent(type, "void write<", "void write<");

    assertMethodPresent(type, "size_t enumToOrdinal<", "size_t enumToOrdinal(");
    assertMethodPresent(type, "ItemType valueToEnum<", "ItemType valueToEnum(");
    assertMethodPresent(type, "size_t bitSizeOf<" , "size_t bitSizeOf(");
    assertMethodPresent(type, "ItemType read<", "ItemType read(");
}

TEST_F(WithoutWriterCode, checkVersionAvailabilityMethods)
{
    const char* type = "VersionAvailability";

    assertMethodNotPresent(type, "size_t initializeOffsets(", "size_t VersionAvailability::initializeOffsets(");
    assertMethodNotPresent(type, "void write(", "void VersionAvailability::write(");

    assertMethodPresent(type, "constexpr VersionAvailability() noexcept", NULL);
    assertMethodPresent(type, "constexpr VersionAvailability(Values value) noexcept :", NULL);
    assertMethodPresent(type, "VersionAvailability(::zserio::BitStreamReader&",
            "VersionAvailability::VersionAvailability(::zserio::BitStreamReader&");
    assertMethodPresent(type, "VersionAvailability(underlying_type value)",
            "VersionAvailability::VersionAvailability(underlying_type value)");
    assertMethodPresent(type, "constexpr explicit operator underlying_type() const", NULL);
    assertMethodPresent(type, "constexpr underlying_type getValue() const", NULL);
    assertMethodPresent(type, "size_t bitSizeOf(size_t bitPosition = 0) const",
            "size_t VersionAvailability::bitSizeOf(size_t) const");
    assertMethodPresent(type, "int hashCode() const", "int VersionAvailability::hashCode() const");
    assertMethodPresent(type, "void read(::zserio::BitStreamReader& in)",
            "void VersionAvailability::read(::zserio::BitStreamReader& in)");
    assertMethodPresent(type, "std::string toString() const",
            "std::string VersionAvailability::toString() const");
}

TEST_F(WithoutWriterCode, checkExtraParamUnionMethods)
{
    const char* type = "ExtraParamUnion";

    assertMethodNotPresent(type, " ExtraParamUnion()", "ExtraParamUnion::ExtraParamUnion()");
    assertMethodNotPresent(type, "size_t initializeOffsets(", "size_t ExtraParamUnion::initializeOffsets(");
    assertMethodNotPresent(type, "void write(", "void ExtraParamUnion::write(");

    assertMethodPresent(type, "ExtraParamUnion(::zserio::BitStreamReader&",
            "ExtraParamUnion::ExtraParamUnion(::zserio::BitStreamReader&");
    assertMethodPresent(type, "ChoiceTag choiceTag(", "ChoiceTag ExtraParamUnion::choiceTag(");
    assertMethodPresent(type, "uint16_t getValue16(", "uint16_t ExtraParamUnion::getValue16(");
    assertMethodPresent(type, "uint32_t getValue32(", "uint32_t ExtraParamUnion::getValue32(");
    assertMethodPresent(type, "size_t bitSizeOf(", "size_t ExtraParamUnion::bitSizeOf(");
    assertMethodPresent(type, "bool operator==(", "bool ExtraParamUnion::operator==(");
    assertMethodPresent(type, "int hashCode(", "int ExtraParamUnion::hashCode(");
}

TEST_F(WithoutWriterCode, checkItemMethods)
{
    const char* type = "Item";

    assertMethodNotPresent(type, " Item()", "Item::Item()");
    assertMethodNotPresent(type, "size_t initializeOffsets(", "size_t Item::initializeOffsets(");
    assertMethodNotPresent(type, "void write(", "void Item::write(");

    assertMethodPresent(type, "Item(::zserio::BitStreamReader&", "Item::Item(::zserio::BitStreamReader&");
    assertMethodPresent(type, "Item(const Item&", "Item::Item(const Item&");
    assertMethodPresent(type, "Item& operator=(const Item&", "Item& Item::operator=(const Item&");
    assertMethodPresent(type, "Item(Item&&", "Item::Item(Item&&");
    assertMethodPresent(type, "Item& operator=(Item&&", "Item& Item::operator=(Item&&");
    assertMethodPresent(type, "void initialize(", "void Item::initialize(");
    assertMethodPresent(type, "bool isInitialized(", "bool Item::isInitialized(");
    assertMethodPresent(type, "uint16_t getParam(", "uint16_t Item::getParam(");
    assertMethodPresent(type, "& getExtraParam(", "& Item::getExtraParam(");
    assertMethodPresent(type, "bool hasExtraParam(", "bool Item::hasExtraParam(");
    assertMethodPresent(type, "size_t bitSizeOf(", "size_t Item::bitSizeOf(");
    assertMethodPresent(type, "bool operator==(", "bool Item::operator==(");
    assertMethodPresent(type, "int hashCode(", "int Item::hashCode(");
}

TEST_F(WithoutWriterCode, checkItemChoiceMethods)
{
    const char* type = "ItemChoice";

    assertMethodNotPresent(type, " ItemChoice()", "ItemChoice::ItemChoice()");
    assertMethodNotPresent(type, "size_t initializeOffsets(", "size_t ItemChoice::initializeOffsets(");
    assertMethodNotPresent(type, "void write(", "void ItemChoice::write(");

    assertMethodPresent(type, "ItemChoice(::zserio::BitStreamReader&",
            "ItemChoice::ItemChoice(::zserio::BitStreamReader&");
    assertMethodPresent(type, "ItemChoice(const ItemChoice&", "ItemChoice::ItemChoice(const ItemChoice&");
    assertMethodPresent(type, "ItemChoice& operator=(const ItemChoice&",
            "ItemChoice& ItemChoice::operator=(const ItemChoice&");
    assertMethodPresent(type, "ItemChoice(ItemChoice&&", "ItemChoice::ItemChoice(ItemChoice&&");
    assertMethodPresent(type, "ItemChoice& operator=(ItemChoice&&",
            "ItemChoice& ItemChoice::operator=(ItemChoice&&");

    assertMethodPresent(type, "void initialize(", "void ItemChoice::initialize(");
    assertMethodPresent(type, "bool isInitialized(", "bool ItemChoice::isInitialized(");
    assertMethodPresent(type, "void initializeChildren(", "void ItemChoice::initializeChildren(");
    assertMethodPresent(type, "bool getHasItem(", "bool ItemChoice::getHasItem(");
    assertMethodPresent(type, "Item& getItem(", "Item& ItemChoice::getItem(");
    assertMethodPresent(type, "uint16_t getParam(", "uint16_t ItemChoice::getParam(");
    assertMethodPresent(type, "size_t bitSizeOf(", "size_t ItemChoice::bitSizeOf(");
    assertMethodPresent(type, "bool operator==(", "bool ItemChoice::operator==(");
    assertMethodPresent(type, "int hashCode(", "int ItemChoice::hashCode(");
}

TEST_F(WithoutWriterCode, checkItemChoiceHolderMethods)
{
    const char* type = "ItemChoiceHolder";

    assertMethodNotPresent(type, " ItemChoiceHolder()", "ItemChoiceHolder::ItemChoiceHolder()");
    assertMethodNotPresent(type, "size_t initializeOffsets(", "size_t ItemChoiceHolder::initializeOffsets(");
    assertMethodNotPresent(type, "void write(", "void ItemChoiceHolder::write(");

    assertMethodPresent(type, "ItemChoiceHolder(::zserio::BitStreamReader&",
            "ItemChoiceHolder::ItemChoiceHolder(::zserio::BitStreamReader&");
    assertMethodPresent(type, "ItemChoiceHolder(const ItemChoiceHolder&",
            "ItemChoiceHolder::ItemChoiceHolder(const ItemChoiceHolder&");
    assertMethodPresent(type, "ItemChoiceHolder& operator=(const ItemChoiceHolder&",
            "ItemChoiceHolder& ItemChoiceHolder::operator=(const ItemChoiceHolder&");
    assertMethodPresent(type, "ItemChoiceHolder(ItemChoiceHolder&&",
            "ItemChoiceHolder::ItemChoiceHolder(ItemChoiceHolder&&");
    assertMethodPresent(type, "ItemChoiceHolder& operator=(ItemChoiceHolder&&",
            "ItemChoiceHolder& ItemChoiceHolder::operator=(ItemChoiceHolder&&");
    assertMethodPresent(type, "void initializeChildren(", "void ItemChoiceHolder::initializeChildren(");
    assertMethodPresent(type, "bool getHasItem(", "bool ItemChoiceHolder::getHasItem(");
    assertMethodPresent(type, "ItemChoice& getItemChoice(", "ItemChoice& ItemChoiceHolder::getItemChoice(");
    assertMethodPresent(type, "size_t bitSizeOf(", "size_t ItemChoiceHolder::bitSizeOf(");
    assertMethodPresent(type, "bool operator==(", "bool ItemChoiceHolder::operator==(");
    assertMethodPresent(type, "int hashCode(", "int ItemChoiceHolder::hashCode(");
}

TEST_F(WithoutWriterCode, checkTileMethods)
{
    const char* type = "Tile";

    assertMethodNotPresent(type, " Tile()", "Tile::Tile()");
    assertMethodNotPresent(type, "size_t initializeOffsets(", "size_t Tile::initializeOffsets(");
    assertMethodNotPresent(type, "void write(", "void Tile::write(");

    assertMethodPresent(type, "Tile(::zserio::BitStreamReader&", "Tile::Tile(::zserio::BitStreamReader&");
    assertMethodPresent(type, "void initializeChildren(", "void Tile::initializeChildren(");
    assertMethodPresent(type, "uint8_t getVersion(", "uint8_t Tile::getVersion(");
    assertMethodPresent(type, "uint32_t getNumElementsOffset(", "uint32_t Tile::getNumElementsOffset(");
    assertMethodPresent(type, "uint32_t getNumElements(", "uint32_t Tile::getNumElements(");
    assertMethodPresent(type, "& getData(", "& Tile::getData(");
    assertMethodPresent(type, "size_t bitSizeOf(", "size_t Tile::bitSizeOf(");
    assertMethodPresent(type, "bool operator==(", "bool Tile::operator==");
    assertMethodPresent(type, "int hashCode(", "int Tile::hashCode(");
}

TEST_F(WithoutWriterCode, checkGeoMapTableMethods)
{
    const char* type = "GeoMapTable";

    assertMethodNotPresent(type, "void createTable(", "void GeoMapTable::createTable(");
    assertMethodNotPresent(type, "void createOrdinaryRowIdTable(",
            "void GeoMapTable::createOrdinaryRowIdTable(");
    assertMethodNotPresent(type, "void deleteTable(", "void GeoMapTable::deleteTable(");
    assertMethodNotPresent(type, "void write(", "void GeoMapTable::write(");
    assertMethodNotPresent(type, "void update(", "void GeoMapTable::update(");
    assertMethodNotPresent(type, "void writeRow(", "void GeoMapTable::writeRow(");
    assertMethodNotPresent(type, "void appendCreateTableToQuery(",
            "void GeoMapTable::appendCreateTableToQuery(");

    assertMethodPresent(type, "GeoMapTable(::zserio::SqliteConnection&",
            "GeoMapTable::GeoMapTable(::zserio::SqliteConnection&");
    assertMethodPresent(type, "~GeoMapTable() = default", ""); // default, i.e. nothing in cpp
    assertMethodPresent(type, "Reader createReader(", "Reader GeoMapTable::createReader(");
}

TEST_F(WithoutWriterCode, checkWorldDbMethods)
{
    const char* type = "WorldDb";

    assertMethodNotPresent(type,"void createSchema(", "void WorldDb::createSchema(");
    assertMethodNotPresent(type, "void deleteSchema(", "void WorldDb::deleteSchema(");

    assertMethodPresent(type, "WorldDb(const ::std::string&", "WorldDb::WorldDb(const ::std::string&");
    assertMethodPresent(type, "WorldDb(sqlite3*", "WorldDb::WorldDb(sqlite3*");
    assertMethodPresent(type, "~WorldDb()", "WorldDb::~WorldDb(");
    assertMethodPresent(type, "sqlite3* connection(", "sqlite3* WorldDb::connection(");
    assertMethodPresent(type, "GeoMapTable& getEurope(", "GeoMapTable& WorldDb::getEurope(");
    assertMethodPresent(type, "GeoMapTable& getAmerica(", "GeoMapTable& WorldDb::getAmerica(");
}

TEST_F(WithoutWriterCode, readConstructor)
{
    zserio::BitStreamWriter writer;

    writeTile(writer);

    size_t size;
    const uint8_t* buffer = writer.getWriteBuffer(size);
    zserio::BitStreamReader reader(buffer, size);
    Tile tile = Tile(reader);

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
