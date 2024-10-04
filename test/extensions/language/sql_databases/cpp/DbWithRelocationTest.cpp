#include <cstdio>
#include <memory>
#include <string>
#include <vector>

#include "gtest/gtest.h"
#include "sql_databases/db_with_relocation/AmericaDb.h"
#include "sql_databases/db_with_relocation/EuropeDb.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SqliteFinalizer.h"
#include "zserio/StringConvertUtil.h"

namespace sql_databases
{
namespace db_with_relocation
{

using allocator_type = EuropeDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class DbWithRelocationTest : public ::testing::Test
{
public:
    DbWithRelocationTest() :
            m_europeDbFileName("language/sql_databases/db_with_relocation_test_europe.sqlite"),
            m_americaDbFileName("language/sql_databases/db_with_relocation_test_america.sqlite"),
            m_relocatedSlovakiaTableName("slovakia"),
            m_relocatedCzechiaTableName("czechia")
    {
        std::remove(m_europeDbFileName.c_str());
        std::remove(m_americaDbFileName.c_str());

        m_europeDb.reset(new EuropeDb(m_europeDbFileName));
        m_europeDb->createSchema();

        AmericaDb::TRelocationMap tableToDbFileNameRelocationMap;
        tableToDbFileNameRelocationMap.insert(std::make_pair(m_relocatedSlovakiaTableName, m_europeDbFileName));
        tableToDbFileNameRelocationMap.insert(std::make_pair(m_relocatedCzechiaTableName, m_europeDbFileName));
        m_americaDb.reset(new AmericaDb(m_americaDbFileName, tableToDbFileNameRelocationMap));
        m_americaDb->createSchema();

        m_attachedDatabasesNames.insert("main");
        m_attachedDatabasesNames.insert("AmericaDb_" + m_relocatedSlovakiaTableName);
        m_attachedDatabasesNames.insert("AmericaDb_" + m_relocatedCzechiaTableName);
    }

protected:
    bool isTableInDb(zserio::ISqliteDatabase& database, const string_type& checkTableName)
    {
        string_type sqlQuery =
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" + checkTableName + "'";
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(
                database.connection().prepareStatement(sqlQuery));

        int result = sqlite3_step(statement.get());
        if (result == SQLITE_DONE || result != SQLITE_ROW)
        {
            return false;
        }

        const unsigned char* readTableName = sqlite3_column_text(statement.get(), 0);
        return (readTableName != nullptr && checkTableName == reinterpret_cast<const char*>(readTableName));
    }

    const string_type m_europeDbFileName;
    const string_type m_americaDbFileName;
    const string_type m_relocatedSlovakiaTableName;
    const string_type m_relocatedCzechiaTableName;

    std::unique_ptr<EuropeDb> m_europeDb;
    std::unique_ptr<AmericaDb> m_americaDb;

    std::set<string_type> m_attachedDatabasesNames;
};

TEST_F(DbWithRelocationTest, tableGetters)
{
    CountryMapTable& germanyTable = m_europeDb->getGermany();
    CountryMapTable& usaTable = m_americaDb->getUsa();
    CountryMapTable& canadaTable = m_americaDb->getCanada();
    CountryMapTable& slovakiaTable = m_americaDb->getSlovakia();
    CountryMapTable& czechiaTable = m_americaDb->getCzechia();

    ASSERT_TRUE(&germanyTable != &usaTable);
    ASSERT_TRUE(&usaTable != &canadaTable);
    ASSERT_TRUE(&canadaTable != &slovakiaTable);
    ASSERT_TRUE(&slovakiaTable != &czechiaTable);
}

TEST_F(DbWithRelocationTest, relocatedSlovakiaTable)
{
    ASSERT_FALSE(isTableInDb(*m_americaDb, m_relocatedSlovakiaTableName));
    ASSERT_TRUE(isTableInDb(*m_europeDb, m_relocatedSlovakiaTableName));

    // write to relocated table
    int32_t updateTileId = 1;
    vector_type<CountryMapTable::Row> writtenRows(1);
    CountryMapTable::Row& row = writtenRows.back();
    row.setTileId(updateTileId);
    Tile writtenTile;
    writtenTile.setVersion('a');
    writtenTile.setData('A');
    row.setTile(writtenTile);
    CountryMapTable& relocatedTable = m_americaDb->getSlovakia();
    relocatedTable.write(writtenRows);

    // update it
    CountryMapTable::Row updateRow;
    updateRow.setTileId(updateTileId);
    Tile updatedTile;
    updatedTile.setVersion('b');
    updatedTile.setData('B');
    updateRow.setTile(updatedTile);
    const string_type updateCondition = string_type("tileId=") + zserio::toString<allocator_type>(updateTileId);
    relocatedTable.update(updateRow, updateCondition);

    // read it back
    vector_type<CountryMapTable::Row> readRows;
    CountryMapTable::Reader reader = relocatedTable.createReader();
    while (reader.hasNext())
    {
        readRows.push_back(reader.next());
    }

    ASSERT_EQ(1, readRows.size());
    ASSERT_EQ(updateRow.getTileId(), readRows.front().getTileId());
    ASSERT_EQ(updateRow.getTile(), readRows.front().getTile());
}

TEST_F(DbWithRelocationTest, relocatedCzechiaTable)
{
    ASSERT_FALSE(isTableInDb(*m_americaDb, m_relocatedCzechiaTableName));
    ASSERT_TRUE(isTableInDb(*m_europeDb, m_relocatedCzechiaTableName));

    // write to relocated table
    int32_t updateTileId = 1;
    vector_type<CountryMapTable::Row> writtenRows(1);
    CountryMapTable::Row& row = writtenRows.back();
    row.setTileId(updateTileId);
    Tile writtenTile;
    writtenTile.setVersion('c');
    writtenTile.setData('C');
    row.setTile(writtenTile);
    CountryMapTable& relocatedTable = m_americaDb->getCzechia();
    relocatedTable.write(writtenRows);

    // update it
    CountryMapTable::Row updateRow;
    updateRow.setTileId(updateTileId);
    Tile updatedTile;
    updatedTile.setVersion('d');
    updatedTile.setData('D');
    updateRow.setTile(updatedTile);
    const string_type updateCondition = string_type("tileId=") + zserio::toString<allocator_type>(updateTileId);
    relocatedTable.update(updateRow, updateCondition);

    // read it back
    vector_type<CountryMapTable::Row> readRows;
    CountryMapTable::Reader reader = relocatedTable.createReader();
    while (reader.hasNext())
    {
        readRows.push_back(reader.next());
    }

    ASSERT_EQ(1, readRows.size());
    ASSERT_EQ(updateRow.getTileId(), readRows.front().getTileId());
    ASSERT_EQ(updateRow.getTile(), readRows.front().getTile());
}

TEST_F(DbWithRelocationTest, attachedDatabases)
{
    std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(
            m_americaDb->connection().prepareStatement("PRAGMA database_list"));

    while (sqlite3_step(statement.get()) == SQLITE_ROW)
    {
        const char* databaseName = reinterpret_cast<const char*>(sqlite3_column_text(statement.get(), 1));
        ASSERT_EQ(1, m_attachedDatabasesNames.count(databaseName));
        m_attachedDatabasesNames.erase(databaseName);
    }

    ASSERT_EQ(1, m_attachedDatabasesNames.size());
    ASSERT_EQ(0, m_attachedDatabasesNames.count("main"));
}

} // namespace db_with_relocation
} // namespace sql_databases
