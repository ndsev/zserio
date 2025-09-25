#include <fstream>
#include <string>

#include "gtest/gtest.h"
#include "with_sources_amalgamation/WorldDb.h"

namespace with_sources_amalgamation
{

using allocator_type = zserio::RebindAlloc<WorldDb::allocator_type, uint8_t>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class WithSourcesAmalgamation : public ::testing::Test
{
protected:
    bool isFilePresent(const char* fileName)
    {
        std::ifstream file(fileName);
        const bool isPresent = file.good();
        file.close();

        return isPresent;
    }
};

TEST_F(WithSourcesAmalgamation, checkWithSourcesAmalgamationPackage)
{
    ASSERT_TRUE(isFilePresent(
            "arguments/with_sources_amalgamation/gen/with_sources_amalgamation/WithSourcesAmalgamation.cpp"));
    ASSERT_TRUE(isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/Color.h"));
    ASSERT_TRUE(isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/Direction.h"));
    ASSERT_TRUE(
            isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/GeoMapTable.h"));
    ASSERT_TRUE(
            isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/SecondTable.h"));
    ASSERT_TRUE(isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/Group.h"));
    ASSERT_TRUE(
            isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/Permission.h"));
    ASSERT_TRUE(isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/WorldDb.h"));
    ASSERT_TRUE(isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/SecondDb.h"));

    ASSERT_FALSE(isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/Color.cpp"));
    ASSERT_FALSE(
            isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/Direction.cpp"));
    ASSERT_FALSE(
            isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/GeoMapTable.cpp"));
    ASSERT_FALSE(
            isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/SecondTable.cpp"));
    ASSERT_FALSE(isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/Group.cpp"));
    ASSERT_FALSE(
            isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/Permission.cpp"));
    ASSERT_FALSE(
            isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/WorldDb.cpp"));
    ASSERT_FALSE(
            isFilePresent("arguments/with_sources_amalgamation/gen/with_sources_amalgamation/SecondDb.cpp"));
}

TEST_F(WithSourcesAmalgamation, checkImportedTilePackage)
{
    ASSERT_TRUE(isFilePresent("arguments/with_sources_amalgamation/gen/_imported_tile_/ImportedTile.cpp"));
    ASSERT_FALSE(isFilePresent("arguments/with_sources_amalgamation/gen/_imported_tile_/Tile.cpp"));
}

TEST_F(WithSourcesAmalgamation, check___Package)
{
    ASSERT_TRUE(isFilePresent("arguments/with_sources_amalgamation/gen/___/Amalgamation.cpp"));
    ASSERT_FALSE(isFilePresent("arguments/with_sources_amalgamation/gen/__/Empty.cpp"));
}

TEST_F(WithSourcesAmalgamation, readWriteWorldDb)
{
    WorldDb worldDb(":memory:");
    worldDb.createSchema();

    GeoMapTable& europe = worldDb.getEurope();

    GeoMapTable::Row row;
    row.setTileId(13);
    row.setTile(_imported_tile_::Tile{42, 1, vector_type<uint8_t>{0xab}});
    row.setEmpty(___::Empty());
    vector_type<GeoMapTable::Row> rows;
    rows.push_back(row);
    europe.write(rows);

    GeoMapTable::Reader reader = europe.createReader();
    vector_type<GeoMapTable::Row> readRows;
    while (reader.hasNext())
    {
        readRows.push_back(reader.next());
    }

    ASSERT_EQ(1, readRows.size());
    const GeoMapTable::Row& readRow = readRows.at(0);
    ASSERT_EQ(row.getTileId(), readRow.getTileId());
    ASSERT_EQ(row.getTile(), readRow.getTile());
    // cannot read empty struct from the table since there are no bytes stored in it
    // ASSERT_EQ(row.getEmpty(), readRow.getEmpty());
}

} // namespace with_sources_amalgamation
