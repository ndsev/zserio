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
        ASSERT_FALSE(isStringInFilePresent(filePath + ".h", declaration))
                << "Method declaration '" << declaration << "' is present in '" << typeName << "'!";
        ASSERT_FALSE(isStringInFilePresent(filePath + ".cpp", definition))
                << "Method definition '" << definition << "' is present'" << typeName << "'!";
    }

    void assertMethodPresent(const char* typeName, const char* declaration, const char* definition)
    {
        const std::string filePath = std::string(PATH) + typeName;
        ASSERT_TRUE(isStringInFilePresent(filePath + ".h", declaration))
                << "Method declaration '" << declaration << "' is not present in '" << typeName << "'!";
        ASSERT_TRUE(isStringInFilePresent(filePath + ".cpp", definition))
                << "Method definition '" << definition << "' is not present in '" << typeName << "'!";
    }

    void createWorldDb(zserio::SqliteConnection& db)
    {
        sqlite3* connection = NULL;
        const int result = sqlite3_open_v2(":memory:", &connection, SQLITE_OPEN_CREATE | SQLITE_OPEN_READWRITE,
                NULL);
        ASSERT_EQ(SQLITE_OK, result);
        db.reset(connection);

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
        writer.writeBits(VERSION, 8);
        writer.writeBits(5, 32); // numElementsOffset
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
        ASSERT_EQ(NUM_ELEMENTS, tile.getNumElements());

        const zserio::ObjectArray<ItemChoiceHolder>& data = tile.getData();
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
    static const uint8_t VERSION;
    static const uint32_t NUM_ELEMENTS;
    static const uint16_t PARAMS[2];
    static const uint32_t EXTRA_PARAM;
};

const char* WithoutWriterCode::PATH = "arguments/without_writer_code/gen/without_writer_code/";
const int32_t WithoutWriterCode::TILE_ID_EUROPE = 99;
const int32_t WithoutWriterCode::TILE_ID_AMERICA = 11;
const uint8_t WithoutWriterCode::VERSION = 8;
const uint32_t WithoutWriterCode::NUM_ELEMENTS = 2;
const uint16_t WithoutWriterCode::PARAMS[2] = { 13, 21 };
const uint32_t WithoutWriterCode::EXTRA_PARAM = 42;

TEST_F(WithoutWriterCode, checkItemTypeMethods)
{
    const char* type = "ItemType";

    assertMethodNotPresent(type,
            "size_t initializeOffsets(size_t", "size_t ItemType::initializeOffsets(size_t");
    assertMethodNotPresent(type,
            "void write(zserio::BitStreamWriter&", "void ItemType::write(zserio::BitStreamWriter&");

    // enum must have empty constructor:
    // - when used as a parameter of a compound type, it's required in its copy constructor
    assertMethodPresent(type,
            "ItemType()", "ItemType::ItemType()");
    assertMethodPresent(type,
            "ItemType(zserio::BitStreamReader&", "ItemType::ItemType(zserio::BitStreamReader&");
    assertMethodPresent(type,
            "operator e_ItemType() const" , "ItemType::operator e_ItemType() const");
    assertMethodPresent(type,
            "int8_t getValue() const", "int8_t ItemType::getValue() const");
    assertMethodPresent(type,
            "size_t bitSizeOf(size_t", "size_t ItemType::bitSizeOf(size_t");
    assertMethodPresent(type,
            "bool operator==(const ItemType&", "bool ItemType::operator==(const ItemType&");
    assertMethodPresent(type,
            "bool operator==(e_ItemType ", "bool ItemType::operator==(e_ItemType ");
    assertMethodPresent(type,
            "int hashCode() const", "int ItemType::hashCode() const");
    assertMethodPresent(type,
            "void read(zserio::BitStreamReader&", "void ItemType::read(zserio::BitStreamReader&");
}

TEST_F(WithoutWriterCode, checkExtraParamUnionMethods)
{
    const char* type = "ExtraParamUnion";

    assertMethodNotPresent(type,
            "ExtraParamUnion()", "ExtraParamUnion::ExtraParamUnion()");
    assertMethodNotPresent(type,
            "size_t initializeOffsets(size_t", "size_t ExtraParamUnion::initializeOffsets(size_t");
    assertMethodNotPresent(type,
            "void write(zserio::BitStreamWriter&", "void ExtraParamUnion::write(zserio::BitStreamWriter&");

    assertMethodPresent(type,
            "ExtraParamUnion(zserio::BitStreamReader&",
            "ExtraParamUnion::ExtraParamUnion(zserio::BitStreamReader&");
    assertMethodPresent(type,
            "ChoiceTag choiceTag() const", "ChoiceTag ExtraParamUnion::choiceTag() const");
    assertMethodPresent(type,
            "uint16_t getValue16() const", "uint16_t ExtraParamUnion::getValue16() const");
    assertMethodPresent(type,
            "uint32_t getValue32() const", "uint32_t ExtraParamUnion::getValue32() const");
    assertMethodPresent(type,
            "size_t bitSizeOf(size_t", "size_t ExtraParamUnion::bitSizeOf(size_t");
    assertMethodPresent(type,
            "bool operator==(const ExtraParamUnion&",
            "bool ExtraParamUnion::operator==(const ExtraParamUnion&");
    assertMethodPresent(type,
            "int hashCode() const", "int ExtraParamUnion::hashCode() const");
    assertMethodPresent(type,
            "void read(zserio::BitStreamReader&", "void ExtraParamUnion::read(zserio::BitStreamReader&");
}

TEST_F(WithoutWriterCode, checkItemMethods)
{
    const char* type = "Item";

    assertMethodNotPresent(type,
            "Item()", "Item::Item()");
    assertMethodNotPresent(type,
            "size_t initializeOffsets(size_t", "size_t Item::initializeOffsets(size_t");
    assertMethodNotPresent(type,
            "void write(zserio::BitStreamWriter&", "void Item::write(zserio::BitStreamWriter&");

    assertMethodPresent(type,
            "Item(zserio::BitStreamReader&", "Item::Item(zserio::BitStreamReader&");
    assertMethodPresent(type,
            "Item(const Item&", "Item::Item(const Item&");
    assertMethodPresent(type,
            "Item& operator=(const Item&", "Item& Item::operator=(const Item&");
    assertMethodPresent(type,
            "void initialize(", "void Item::initialize(");
    assertMethodPresent(type,
            "uint16_t getParam() const", "uint16_t Item::getParam() const");
    assertMethodPresent(type,
            "ExtraParamUnion& getExtraParam() const", "ExtraParamUnion& Item::getExtraParam() const");
    assertMethodPresent(type,
            "bool hasExtraParam() const", "bool Item::hasExtraParam() const");
    assertMethodPresent(type,
            "size_t bitSizeOf(size_t", "size_t Item::bitSizeOf(size_t");
    assertMethodPresent(type,
            "bool operator==(const Item&", "bool Item::operator==(const Item&");
    assertMethodPresent(type,
            "int hashCode() const", "int Item::hashCode() const");
    assertMethodPresent(type,
            "void read(zserio::BitStreamReader&", "void Item::read(zserio::BitStreamReader&");
}

TEST_F(WithoutWriterCode, checkItemChoiceMethods)
{
    const char* type = "ItemChoice";

    assertMethodNotPresent(type,
            "ItemChoice()", "ItemChoice::ItemChoice()");
    assertMethodNotPresent(type,
            "size_t initializeOffsets(size_t", "size_t ItemChoice::initializeOffsets(size_t");
    assertMethodNotPresent(type,
            "void write(zserio::BitStreamWriter&", "void ItemChoice::write(zserio::BitStreamWriter&");

    assertMethodPresent(type,
            "ItemChoice(zserio::BitStreamReader&", "ItemChoice::ItemChoice(zserio::BitStreamReader&");
    assertMethodPresent(type,
            "ItemChoice(const ItemChoice&", "ItemChoice::ItemChoice(const ItemChoice&");
    assertMethodPresent(type,
            "ItemChoice& operator=(const ItemChoice&", "ItemChoice& ItemChoice::operator=(const ItemChoice&");
    assertMethodPresent(type,
            "void initialize(", "void ItemChoice::initialize(");
    assertMethodPresent(type,
            "void initializeChildren()", "void ItemChoice::initializeChildren()");
    assertMethodPresent(type,
            "bool getHasItem() const", "bool ItemChoice::getHasItem() const");
    assertMethodPresent(type,
            "Item& getItem() const", "Item& ItemChoice::getItem() const");
    assertMethodPresent(type,
            "uint16_t getParam() const", "uint16_t ItemChoice::getParam() const");
    assertMethodPresent(type,
            "size_t bitSizeOf(size_t", "size_t ItemChoice::bitSizeOf(size_t");
    assertMethodPresent(type,
            "bool operator==(const ItemChoice&", "bool ItemChoice::operator==(const ItemChoice&");
    assertMethodPresent(type,
            "int hashCode() const", "int ItemChoice::hashCode() const");
    assertMethodPresent(type,
            "void read(zserio::BitStreamReader&", "void ItemChoice::read(zserio::BitStreamReader&");
}

TEST_F(WithoutWriterCode, checkItemChoiceHolderMethods)
{
    const char* type = "ItemChoiceHolder";

    assertMethodNotPresent(type,
            "ItemChoiceHolder()", "ItemChoiceHolder::ItemChoiceHolder()");
    assertMethodNotPresent(type,
            "size_t initializeOffsets(size_t", "size_t ItemChoiceHolder::initializeOffsets(size_t");
    assertMethodNotPresent(type,
            "void write(zserio::BitStreamWriter&", "void ItemChoiceHolder::write(zserio::BitStreamWriter&");

    assertMethodPresent(type,
            "ItemChoiceHolder(zserio::BitStreamReader&",
            "ItemChoiceHolder::ItemChoiceHolder(zserio::BitStreamReader&");
    assertMethodPresent(type,
            "ItemChoiceHolder(const ItemChoiceHolder&",
            "ItemChoiceHolder::ItemChoiceHolder(const ItemChoiceHolder&");
    assertMethodPresent(type,
            "ItemChoiceHolder& operator=(const ItemChoiceHolder&",
            "ItemChoiceHolder& ItemChoiceHolder::operator=(const ItemChoiceHolder&");
    assertMethodPresent(type,
            "void initializeChildren()", "void ItemChoiceHolder::initializeChildren()");
    assertMethodPresent(type,
            "bool getHasItem() const", "bool ItemChoiceHolder::getHasItem() const");
    assertMethodPresent(type,
            "ItemChoice& getItemChoice() const", "ItemChoice& ItemChoiceHolder::getItemChoice() const");
    assertMethodPresent(type,
            "size_t bitSizeOf(size_t", "size_t ItemChoiceHolder::bitSizeOf(size_t");
    assertMethodPresent(type,
            "bool operator==(const ItemChoiceHolder&",
            "bool ItemChoiceHolder::operator==(const ItemChoiceHolder&");
    assertMethodPresent(type,
            "int hashCode() const", "int ItemChoiceHolder::hashCode() const");
    assertMethodPresent(type,
            "void read(zserio::BitStreamReader&", "void ItemChoiceHolder::read(zserio::BitStreamReader&");
}

TEST_F(WithoutWriterCode, checkTileMethods)
{
    const char* type = "Tile";

    assertMethodNotPresent(type,
            "Tile()", "Tile::Tile()");
    assertMethodNotPresent(type,
            "size_t initializeOffsets(size_t", "size_t Tile::initializeOffsets(size_t");
    assertMethodNotPresent(type,
            "void write(zserio::BitStreamWriter&", "void Tile::write(zserio::BitStreamWriter&");

    assertMethodPresent(type,
            "Tile(zserio::BitStreamReader&", "Tile::Tile(zserio::BitStreamReader&");
    assertMethodPresent(type,
            "void initializeChildren()", "void Tile::initializeChildren()");
    assertMethodPresent(type,
            "uint8_t getVersion() const", "uint8_t Tile::getVersion() const");
    assertMethodPresent(type,
            "uint32_t getNumElementsOffset() const", "uint32_t Tile::getNumElementsOffset() const");
    assertMethodPresent(type,
            "uint32_t getNumElements() const", "uint32_t Tile::getNumElements() const");
    assertMethodPresent(type,
            "& getData() const", "& Tile::getData() const");
    assertMethodPresent(type,
            "size_t bitSizeOf(size_t", "size_t Tile::bitSizeOf(size_t");
    assertMethodPresent(type,
            "bool operator==(const Tile&", "bool Tile::operator==(const Tile&");
    assertMethodPresent(type,
            "int hashCode() const", "int Tile::hashCode() const");
    assertMethodPresent(type,
            "void read(zserio::BitStreamReader&", "void Tile::read(zserio::BitStreamReader&");
}

TEST_F(WithoutWriterCode, checkGeoMapTableMethods)
{
    const char* type = "GeoMapTable";

    assertMethodNotPresent(type,
            "void createTable()", "void GeoMapTable::createTable()");
    assertMethodNotPresent(type,
            "void createOrdinaryRowIdTable()", "void GeoMapTable::createOrdinaryRowIdTable()");
    assertMethodNotPresent(type,
            "void deleteTable()", "void GeoMapTable::deleteTable()");
    assertMethodNotPresent(type,
            "void write(", "void GeoMapTable::write(");
    assertMethodNotPresent(type,
            "void update(", "void GeoMapTable::update(");
    assertMethodNotPresent(type,
            "void writeRow(", "void GeoMapTable::writeRow(");
    assertMethodNotPresent(type,
            "void appendCreateTableToQuery(", "void GeoMapTable::appendCreateTableToQuery(");

    assertMethodPresent(type,
            "GeoMapTable(zserio::SqliteConnection&", "GeoMapTable::GeoMapTable(zserio::SqliteConnection&");
    assertMethodPresent(type,
            "void read(", "void GeoMapTable::read(");
    assertMethodPresent(type,
            "void readRow(", "void GeoMapTable::readRow(");
}

TEST_F(WithoutWriterCode, checkGeoMapTableRowMethods)
{
    const char* type = "GeoMapTableRow";

    // no restricted methods

    assertMethodPresent(type,
            "GeoMapTableRow()", "GeoMapTableRow::GeoMapTableRow()");
    assertMethodPresent(type,
            "int32_t getTileId() const", "int32_t GeoMapTableRow::getTileId() const");
    assertMethodPresent(type,
            "void setTileId(int32_t", "void GeoMapTableRow::setTileId(int32_t");
    assertMethodPresent(type,
            "bool isNullTileId() const", "bool GeoMapTableRow::isNullTileId() const");
    assertMethodPresent(type,
            "void setNullTileId()", "void GeoMapTableRow::setNullTileId()");
    assertMethodPresent(type,
            "Tile& getTile() const", "Tile& GeoMapTableRow::getTile() const");
    assertMethodPresent(type,
            "void setTile(", "void GeoMapTableRow::setTile(");
    assertMethodPresent(type,
            "bool isNullTile() const", "bool GeoMapTableRow::isNullTile() const");
    assertMethodPresent(type,
            "void setNullTile()", "void GeoMapTableRow::setNullTile()");
}

TEST_F(WithoutWriterCode, checkWorldDbMethods)
{
    const char* type = "WorldDb";

    assertMethodNotPresent(type,
            "void createSchema(", "void WorldDb::createSchema(");
    assertMethodNotPresent(type,
            "void deleteSchema(", "void WorldDb::deleteSchema(");

    assertMethodPresent(type,
            "WorldDb(sqlite3*", "WorldDb::WorldDb(sqlite3*");
    assertMethodPresent(type,
            "WorldDb(const std::string&", "WorldDb::WorldDb(const std::string&");
    assertMethodPresent(type,
            "GeoMapTable& getEurope()", "GeoMapTable& WorldDb::getEurope()");
    assertMethodPresent(type,
            "GeoMapTable& getAmerica()", "GeoMapTable& WorldDb::getAmerica()");
    assertMethodPresent(type,
            "const char* databaseName()", "const char* WorldDb::databaseName()");
    assertMethodPresent(type,
            "void fillTableNames(", "void WorldDb::fillTableNames(");
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
    std::vector<GeoMapTableRow> europeRows;
    europe.read(europeRows);

    const GeoMapTable& america = worldDb.getAmerica();
    std::vector<GeoMapTableRow> americaRows;
    america.read(americaRows);

    ASSERT_EQ(1, europeRows.size());
    ASSERT_EQ(TILE_ID_EUROPE, europeRows[0].getTileId());
    checkTile(europeRows[0].getTile());

    ASSERT_EQ(1, americaRows.size());
    ASSERT_EQ(TILE_ID_AMERICA, americaRows[0].getTileId());
    checkTile(americaRows[0].getTile());
}

} // namespace without_writer_code
