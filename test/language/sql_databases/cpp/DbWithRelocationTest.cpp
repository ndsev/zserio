#include <cstdio>
#include <vector>
#include <string>

#include <sqlite3.h>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "sql_databases/db_with_relocation/EuropeDb.h"
#include "sql_databases/db_with_relocation/AmericaDb.h"

namespace sql_databases
{
namespace db_with_relocation
{

class DbWithRelocationTest : public ::testing::Test
{
public:
    DbWithRelocationTest() :
            m_europeDbFileName("db_with_relocation_test_europe.sqlite"),
            m_americaDbFileName("db_with_relocation_test_america.sqlite"),
            m_relocatedSlovakiaTableName("slovakia"),
            m_relocatedCzechiaTableName("czechia"),
            m_europeDb(NULL), m_americaDb(NULL)
    {
        std::remove(m_europeDbFileName.c_str());
        std::remove(m_americaDbFileName.c_str());

        try
        {
            m_europeDb = new EuropeDb(m_europeDbFileName);
            m_europeDb->createSchema();

            AmericaDb::TRelocationMap tableToDbFileNameRelocationMap;
            tableToDbFileNameRelocationMap.insert(std::make_pair(m_relocatedSlovakiaTableName, m_europeDbFileName));
            tableToDbFileNameRelocationMap.insert(std::make_pair(m_relocatedCzechiaTableName, m_europeDbFileName));
            m_americaDb = new AmericaDb(m_americaDbFileName, tableToDbFileNameRelocationMap);
            m_americaDb->createSchema();

            m_attachedDatabasesNames.insert("main");
            m_attachedDatabasesNames.insert("AmericaDb_" + m_relocatedSlovakiaTableName);
            m_attachedDatabasesNames.insert("AmericaDb_" + m_relocatedCzechiaTableName);
        }
        catch (...)
        {
            delete m_europeDb;
            delete m_americaDb;
            throw;
        }
    }

    ~DbWithRelocationTest()
    {
        delete m_europeDb;
        delete m_americaDb;
    }

protected:
    bool isTableInDb(zserio::ISqliteDatabase& database, const std::string& checkTableName)
    {
        sqlite3_stmt* statement;
        std::string sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + checkTableName +
                "'";
        int result = sqlite3_prepare_v2(database.connection(), sqlQuery.c_str(), -1, &statement, NULL);
        if (result != SQLITE_OK)
            return false;

        result = sqlite3_step(statement);
        if (result == SQLITE_DONE || result != SQLITE_ROW)
        {
            sqlite3_finalize(statement);
            return false;
        }

        const unsigned char* readTableName = sqlite3_column_text(statement, 0);
        if (readTableName == NULL || checkTableName.compare(reinterpret_cast<const char*>(readTableName)) != 0)
        {
            sqlite3_finalize(statement);
            return false;
        }

        sqlite3_finalize(statement);

        return true;
    }

    const std::string m_europeDbFileName;
    const std::string m_americaDbFileName;
    const std::string m_relocatedSlovakiaTableName;
    const std::string m_relocatedCzechiaTableName;

    EuropeDb* m_europeDb;
    AmericaDb* m_americaDb;

    std::set<std::string> m_attachedDatabasesNames;
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
    std::vector<CountryMapTableRow> writtenRows(1);
    CountryMapTableRow& row = writtenRows.back();
    row.setTileId(updateTileId);
    Tile writtenTile;
    writtenTile.setVersion('a');
    writtenTile.setData('A');
    row.setTile(writtenTile);
    CountryMapTable& relocatedTable = m_americaDb->getSlovakia();
    relocatedTable.write(writtenRows);

    // update it
    CountryMapTableRow updateRow;
    updateRow.setTileId(updateTileId);
    Tile updatedTile;
    updatedTile.setVersion('b');
    updatedTile.setData('B');
    updateRow.setTile(updatedTile);
    const std::string updateCondition = std::string("tileId=") + zserio::convertToString(updateTileId);
    relocatedTable.update(updateRow, updateCondition);

    // read it back
    std::vector<CountryMapTableRow> readRows;
    relocatedTable.read(readRows);

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
    std::vector<CountryMapTableRow> writtenRows(1);
    CountryMapTableRow& row = writtenRows.back();
    row.setTileId(updateTileId);
    Tile writtenTile;
    writtenTile.setVersion('c');
    writtenTile.setData('C');
    row.setTile(writtenTile);
    CountryMapTable& relocatedTable = m_americaDb->getCzechia();
    relocatedTable.write(writtenRows);

    // update it
    CountryMapTableRow updateRow;
    updateRow.setTileId(updateTileId);
    Tile updatedTile;
    updatedTile.setVersion('d');
    updatedTile.setData('D');
    updateRow.setTile(updatedTile);
    const std::string updateCondition = std::string("tileId=") + zserio::convertToString(updateTileId);
    relocatedTable.update(updateRow, updateCondition);

    // read it back
    std::vector<CountryMapTableRow> readRows;
    relocatedTable.read(readRows);

    ASSERT_EQ(1, readRows.size());
    ASSERT_EQ(updateRow.getTileId(), readRows.front().getTileId());
    ASSERT_EQ(updateRow.getTile(), readRows.front().getTile());
}

TEST_F(DbWithRelocationTest, attachedDatabases)
{
    sqlite3_stmt* stmt = m_americaDb->prepareStatement("PRAGMA database_list");

    while (sqlite3_step(stmt) == SQLITE_ROW)
    {
        const char* databaseName = reinterpret_cast<const char*>(sqlite3_column_text(stmt, 1));
        ASSERT_EQ(1, m_attachedDatabasesNames.count(databaseName));
        m_attachedDatabasesNames.erase(databaseName);
    }

    sqlite3_finalize(stmt);

    ASSERT_EQ(1, m_attachedDatabasesNames.size());
    ASSERT_EQ(0, m_attachedDatabasesNames.count("main"));
}

} // namespace db_with_relocation
} // namespace sql_databases
