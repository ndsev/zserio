#include <cstdio>
#include <vector>

#include "gtest/gtest.h"

#include "zserio/SqliteException.h"

#include "sql_constraints/TestDb.h"
#include "sql_constraints/field_constraints/FieldConstraintsTable.h"

namespace sql_constraints
{
namespace field_constraints
{

class FieldConstraintsTest : public ::testing::Test
{
public:
    FieldConstraintsTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_constraints::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~FieldConstraintsTest()
    {
        delete m_database;
    }

protected:
    void fillRow(FieldConstraintsTable::Row& row)
    {
        row.setWithoutSql(1);
        row.setSqlNotNull(1);
        row.setSqlDefaultNull(1);
        row.setSqlCheckConstant(1);
        row.setSqlCheckImportedConstant(1);
        row.setSqlCheckUnicodeEscape(UNICODE_ESCAPE_CONST);
        row.setSqlCheckHexEscape(HEX_ESCAPE_CONST);
        row.setSqlCheckOctalEscape(OCTAL_ESCAPE_CONST);
    }

    static const char DB_FILE_NAME[];

    static const uint8_t UNICODE_ESCAPE_CONST;
    static const uint8_t HEX_ESCAPE_CONST;
    static const uint8_t OCTAL_ESCAPE_CONST;

    static const uint16_t WRONG_CONSTRAINTS_CONSTANT;
    static const uint32_t WRONG_IMPORTED_CONSTRAINTS_CONSTANT;

    static const uint8_t WRONG_UNICODE_ESCAPE_CONST;
    static const uint8_t WRONG_HEX_ESCAPE_CONST;
    static const uint8_t WRONG_OCTAL_ESCAPE_CONST;

    sql_constraints::TestDb* m_database;
};

const char FieldConstraintsTest::DB_FILE_NAME[] = "field_constraints_test.sqlite";

const uint8_t FieldConstraintsTest::UNICODE_ESCAPE_CONST = 1;
const uint8_t FieldConstraintsTest::HEX_ESCAPE_CONST = 2;
const uint8_t FieldConstraintsTest::OCTAL_ESCAPE_CONST = 3;

const uint16_t FieldConstraintsTest::WRONG_CONSTRAINTS_CONSTANT = 124;
const uint32_t FieldConstraintsTest::WRONG_IMPORTED_CONSTRAINTS_CONSTANT = 322;

const uint8_t FieldConstraintsTest::WRONG_UNICODE_ESCAPE_CONST = 0;
const uint8_t FieldConstraintsTest::WRONG_HEX_ESCAPE_CONST = 0;
const uint8_t FieldConstraintsTest::WRONG_OCTAL_ESCAPE_CONST = 0;

TEST_F(FieldConstraintsTest, withoutSql)
{
    FieldConstraintsTable& fieldConstraintsTable = m_database->getFieldConstraintsTable();
    FieldConstraintsTable::Row row;
    fillRow(row);
    row.resetWithoutSql();
    std::vector<FieldConstraintsTable::Row> rows;
    rows.push_back(row);
    ASSERT_NO_THROW(fieldConstraintsTable.write(rows));
}

TEST_F(FieldConstraintsTest, sqlNotNull)
{
    FieldConstraintsTable& fieldConstraintsTable = m_database->getFieldConstraintsTable();
    FieldConstraintsTable::Row row;
    fillRow(row);
    row.resetSqlNotNull();
    std::vector<FieldConstraintsTable::Row> rows;
    rows.push_back(row);
    ASSERT_THROW(fieldConstraintsTable.write(rows), zserio::SqliteException);
}

TEST_F(FieldConstraintsTest, sqlDefaultNull)
{
    FieldConstraintsTable& fieldConstraintsTable = m_database->getFieldConstraintsTable();
    FieldConstraintsTable::Row row;
    fillRow(row);
    row.resetSqlDefaultNull();
    std::vector<FieldConstraintsTable::Row> rows;
    rows.push_back(row);
    ASSERT_NO_THROW(fieldConstraintsTable.write(rows));
}

TEST_F(FieldConstraintsTest, sqlCheckConstant)
{
    FieldConstraintsTable& fieldConstraintsTable = m_database->getFieldConstraintsTable();
    FieldConstraintsTable::Row row;
    fillRow(row);
    row.setSqlCheckConstant(WRONG_CONSTRAINTS_CONSTANT);
    std::vector<FieldConstraintsTable::Row> rows;
    rows.push_back(row);
    ASSERT_THROW(fieldConstraintsTable.write(rows), zserio::SqliteException);
}

TEST_F(FieldConstraintsTest, sqlCheckImportedConstant)
{
    FieldConstraintsTable& fieldConstraintsTable = m_database->getFieldConstraintsTable();
    FieldConstraintsTable::Row row;
    fillRow(row);
    row.setSqlCheckImportedConstant(WRONG_IMPORTED_CONSTRAINTS_CONSTANT);
    std::vector<FieldConstraintsTable::Row> rows;
    rows.push_back(row);
    ASSERT_THROW(fieldConstraintsTable.write(rows), zserio::SqliteException);
}

TEST_F(FieldConstraintsTest, sqlCheckUnicodeEscape)
{
    FieldConstraintsTable& fieldConstraintsTable = m_database->getFieldConstraintsTable();
    FieldConstraintsTable::Row row;
    fillRow(row);
    row.setSqlCheckUnicodeEscape(WRONG_UNICODE_ESCAPE_CONST);
    std::vector<FieldConstraintsTable::Row> rows;
    rows.push_back(row);
    ASSERT_THROW(fieldConstraintsTable.write(rows), zserio::SqliteException);
}

TEST_F(FieldConstraintsTest, sqlCheckHexEscape)
{
    FieldConstraintsTable& fieldConstraintsTable = m_database->getFieldConstraintsTable();
    FieldConstraintsTable::Row row;
    fillRow(row);
    row.setSqlCheckHexEscape(WRONG_HEX_ESCAPE_CONST);
    std::vector<FieldConstraintsTable::Row> rows;
    rows.push_back(row);
    ASSERT_THROW(fieldConstraintsTable.write(rows), zserio::SqliteException);
}

TEST_F(FieldConstraintsTest, sqlCheckOctalEscape)
{
    FieldConstraintsTable& fieldConstraintsTable = m_database->getFieldConstraintsTable();
    FieldConstraintsTable::Row row;
    fillRow(row);
    row.setSqlCheckOctalEscape(WRONG_OCTAL_ESCAPE_CONST);
    std::vector<FieldConstraintsTable::Row> rows;
    rows.push_back(row);
    ASSERT_THROW(fieldConstraintsTable.write(rows), zserio::SqliteException);
}

} // namespace field_constraints
} // namespace sql_constraints
