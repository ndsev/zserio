#include <cstdio>
#include <vector>

#include "gtest/gtest.h"

#include "zserio/SqliteException.h"

#include "sql_constraints/TestDb.h"
#include "sql_constraints/ConstraintsTable.h"
#include "sql_constraints/ConstraintsTableRow.h"
#include "sql_constraints/ConstraintsConstant.h"

namespace sql_constraints
{

class SqlConstraintsTest : public ::testing::Test
{
public:
    SqlConstraintsTest() : m_database(DB_FILE_NAME)
    {
        m_database.createSchema();
    }

    ~SqlConstraintsTest()
    {
        m_database.close();
        std::remove(DB_FILE_NAME);
    }

protected:
    static const char DB_FILE_NAME[];

    static const uint8_t UNICODE_ESCAPE_CONST;
    static const uint8_t HEX_ESCAPE_CONST;
    static const uint8_t OCTAL_ESCAPE_CONST;

    static const uint8_t WRONG_UNICODE_ESCAPE_CONST;
    static const uint8_t WRONG_HEX_ESCAPE_CONST;
    static const uint8_t WRONG_OCTAL_ESCAPE_CONST;

    TestDb m_database;
};

const char SqlConstraintsTest::DB_FILE_NAME[] = "sql_constraints_test.sqlite";

const uint8_t SqlConstraintsTest::UNICODE_ESCAPE_CONST = 1;
const uint8_t SqlConstraintsTest::HEX_ESCAPE_CONST = 2;
const uint8_t SqlConstraintsTest::OCTAL_ESCAPE_CONST = 3;

const uint8_t SqlConstraintsTest::WRONG_UNICODE_ESCAPE_CONST = 0;
const uint8_t SqlConstraintsTest::WRONG_HEX_ESCAPE_CONST = 0;
const uint8_t SqlConstraintsTest::WRONG_OCTAL_ESCAPE_CONST = 0;

TEST_F(SqlConstraintsTest, withoutSql)
{
    ConstraintsTable& constraintsTable = m_database.getConstraintsTable();
    ConstraintsTableRow row;
    row.setNullWithoutSql();
    row.setSqlNotNull(1);
    row.setSqlDefaultNull(1);
    row.setSqlNull(1);
    row.setSqlCheckConstant(1);
    row.setSqlCheckEnum(ConstraintsEnum::VALUE1);
    row.setSqlCheckUnicodeEscape(UNICODE_ESCAPE_CONST);
    row.setSqlCheckHexEscape(HEX_ESCAPE_CONST);
    row.setSqlCheckOctalEscape(OCTAL_ESCAPE_CONST);
    std::vector<ConstraintsTableRow> rows;
    rows.push_back(row);
    ASSERT_THROW(constraintsTable.write(rows), zserio::SqliteException);
}

TEST_F(SqlConstraintsTest, sqlNotNull)
{
    ConstraintsTable& constraintsTable = m_database.getConstraintsTable();
    ConstraintsTableRow row;
    row.setWithoutSql(1);
    row.setNullSqlNotNull();
    row.setSqlDefaultNull(1);
    row.setSqlNull(1);
    row.setSqlCheckConstant(1);
    row.setSqlCheckEnum(ConstraintsEnum::VALUE1);
    row.setSqlCheckUnicodeEscape(UNICODE_ESCAPE_CONST);
    row.setSqlCheckHexEscape(HEX_ESCAPE_CONST);
    row.setSqlCheckOctalEscape(OCTAL_ESCAPE_CONST);
    std::vector<ConstraintsTableRow> rows;
    rows.push_back(row);
    ASSERT_THROW(constraintsTable.write(rows), zserio::SqliteException);
}

TEST_F(SqlConstraintsTest, sqlDefaultNull)
{
    ConstraintsTable& constraintsTable = m_database.getConstraintsTable();
    ConstraintsTableRow row;
    row.setWithoutSql(1);
    row.setSqlNotNull(1);
    row.setNullSqlDefaultNull();
    row.setSqlNull(1);
    row.setSqlCheckConstant(1);
    row.setSqlCheckEnum(ConstraintsEnum::VALUE1);
    row.setSqlCheckUnicodeEscape(UNICODE_ESCAPE_CONST);
    row.setSqlCheckHexEscape(HEX_ESCAPE_CONST);
    row.setSqlCheckOctalEscape(OCTAL_ESCAPE_CONST);
    std::vector<ConstraintsTableRow> rows;
    rows.push_back(row);
    ASSERT_NO_THROW(constraintsTable.write(rows));
}

TEST_F(SqlConstraintsTest, sqlNull)
{
    ConstraintsTable& constraintsTable = m_database.getConstraintsTable();
    ConstraintsTableRow row;
    row.setWithoutSql(1);
    row.setSqlNotNull(1);
    row.setSqlDefaultNull(1);
    row.setNullSqlNull();
    row.setSqlCheckConstant(1);
    row.setSqlCheckEnum(ConstraintsEnum::VALUE1);
    row.setSqlCheckUnicodeEscape(UNICODE_ESCAPE_CONST);
    row.setSqlCheckHexEscape(HEX_ESCAPE_CONST);
    row.setSqlCheckOctalEscape(OCTAL_ESCAPE_CONST);
    std::vector<ConstraintsTableRow> rows;
    rows.push_back(row);
    ASSERT_NO_THROW(constraintsTable.write(rows));
}

TEST_F(SqlConstraintsTest, sqlCheckConstant)
{
    ConstraintsTable& constraintsTable = m_database.getConstraintsTable();
    ConstraintsTableRow row;
    row.setWithoutSql(1);
    row.setSqlNotNull(1);
    row.setSqlDefaultNull(1);
    row.setSqlNull(1);
    row.setSqlCheckConstant(ConstraintsConstant);
    row.setSqlCheckEnum(ConstraintsEnum::VALUE1);
    row.setSqlCheckUnicodeEscape(UNICODE_ESCAPE_CONST);
    row.setSqlCheckHexEscape(HEX_ESCAPE_CONST);
    row.setSqlCheckOctalEscape(OCTAL_ESCAPE_CONST);
    std::vector<ConstraintsTableRow> rows;
    rows.push_back(row);
    ASSERT_THROW(constraintsTable.write(rows), zserio::SqliteException);
}

TEST_F(SqlConstraintsTest, sqlCheckEnum)
{
    ConstraintsTable& constraintsTable = m_database.getConstraintsTable();
    ConstraintsTableRow row;
    row.setWithoutSql(1);
    row.setSqlNotNull(1);
    row.setSqlDefaultNull(1);
    row.setSqlNull(1);
    row.setSqlCheckConstant(1);
    row.setSqlCheckEnum(ConstraintsEnum::VALUE2);
    row.setSqlCheckUnicodeEscape(UNICODE_ESCAPE_CONST);
    row.setSqlCheckHexEscape(HEX_ESCAPE_CONST);
    row.setSqlCheckOctalEscape(OCTAL_ESCAPE_CONST);
    std::vector<ConstraintsTableRow> rows;
    rows.push_back(row);
    ASSERT_THROW(constraintsTable.write(rows), zserio::SqliteException);
}

TEST_F(SqlConstraintsTest, sqlCheckUnicodeEscape)
{
    ConstraintsTable& constraintsTable = m_database.getConstraintsTable();
    ConstraintsTableRow row;
    row.setWithoutSql(1);
    row.setSqlNotNull(1);
    row.setSqlDefaultNull(1);
    row.setSqlNull(1);
    row.setSqlCheckConstant(1);
    row.setSqlCheckEnum(ConstraintsEnum::VALUE1);
    row.setSqlCheckUnicodeEscape(WRONG_UNICODE_ESCAPE_CONST);
    row.setSqlCheckHexEscape(HEX_ESCAPE_CONST);
    row.setSqlCheckOctalEscape(OCTAL_ESCAPE_CONST);
    std::vector<ConstraintsTableRow> rows;
    rows.push_back(row);
    ASSERT_THROW(constraintsTable.write(rows), zserio::SqliteException);
}

TEST_F(SqlConstraintsTest, sqlCheckHexEscape)
{
    ConstraintsTable& constraintsTable = m_database.getConstraintsTable();
    ConstraintsTableRow row;
    row.setWithoutSql(1);
    row.setSqlNotNull(1);
    row.setSqlDefaultNull(1);
    row.setSqlNull(1);
    row.setSqlCheckConstant(1);
    row.setSqlCheckEnum(ConstraintsEnum::VALUE1);
    row.setSqlCheckUnicodeEscape(UNICODE_ESCAPE_CONST);
    row.setSqlCheckHexEscape(WRONG_HEX_ESCAPE_CONST);
    row.setSqlCheckOctalEscape(OCTAL_ESCAPE_CONST);
    std::vector<ConstraintsTableRow> rows;
    rows.push_back(row);
    ASSERT_THROW(constraintsTable.write(rows), zserio::SqliteException);
}

TEST_F(SqlConstraintsTest, sqlCheckOctalEscape)
{
    ConstraintsTable& constraintsTable = m_database.getConstraintsTable();
    ConstraintsTableRow row;
    row.setWithoutSql(1);
    row.setSqlNotNull(1);
    row.setSqlDefaultNull(1);
    row.setSqlNull(1);
    row.setSqlCheckConstant(1);
    row.setSqlCheckEnum(ConstraintsEnum::VALUE1);
    row.setSqlCheckUnicodeEscape(UNICODE_ESCAPE_CONST);
    row.setSqlCheckHexEscape(HEX_ESCAPE_CONST);
    row.setSqlCheckOctalEscape(WRONG_OCTAL_ESCAPE_CONST);
    std::vector<ConstraintsTableRow> rows;
    rows.push_back(row);
    ASSERT_THROW(constraintsTable.write(rows), zserio::SqliteException);
}

} // namespace sql_constraints
