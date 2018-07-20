#include <fstream>
#include <string>

#include "gtest/gtest.h"

namespace without_writer_code
{

class WithoutWriterCode : public ::testing::Test
{
protected:
    bool isMethodInFilePresent(const char* fileName, const char* methodName)
    {
        std::ifstream file(fileName);
        bool isPresent = false;
        std::string line;
        while (std::getline(file, line))
        {
            if (line.find(methodName) != std::string::npos)
            {
                isPresent = true;
                break;
            }
        }
        file.close();

        return isPresent;
    }

    bool isWriteMethodInCompoundPresent(const char* fileName)
    {
        return (isMethodInFilePresent(fileName, "initializeOffsets(") ||
                isMethodInFilePresent(fileName, "write("));
    }

    bool isWriteMethodInSqlTablePresent(const char* fileName)
    {
        return (isMethodInFilePresent(fileName, "createTable(") ||
                isMethodInFilePresent(fileName, "createOrdinaryRowIdTable(") ||
                isMethodInFilePresent(fileName, "deleteTable(") ||
                isMethodInFilePresent(fileName, "write(") ||
                isMethodInFilePresent(fileName, "update(") ||
                isMethodInFilePresent(fileName, "writeRow(") ||
                isMethodInFilePresent(fileName, "appendCreateTableToQuery("));
    }

    bool isWriteMethodInSqlDatabasePresent(const char* fileName)
    {
        return (isMethodInFilePresent(fileName, "createSchema(") ||
                isMethodInFilePresent(fileName, "deleteSchema("));
    }
};

TEST_F(WithoutWriterCode, checkCompoundTypes)
{
    ASSERT_FALSE(isWriteMethodInCompoundPresent(
            "arguments/without_writer_code/gen/without_writer_code/Item.cpp"));
    ASSERT_FALSE(isWriteMethodInCompoundPresent(
            "arguments/without_writer_code/gen/without_writer_code/Item.h"));
    ASSERT_FALSE(isWriteMethodInCompoundPresent(
            "arguments/without_writer_code/gen/without_writer_code/ItemChoice.cpp"));
    ASSERT_FALSE(isWriteMethodInCompoundPresent(
            "arguments/without_writer_code/gen/without_writer_code/ItemChoice.h"));
    ASSERT_FALSE(isWriteMethodInCompoundPresent(
            "arguments/without_writer_code/gen/without_writer_code/ItemChoiceHolder.cpp"));
    ASSERT_FALSE(isWriteMethodInCompoundPresent(
            "arguments/without_writer_code/gen/without_writer_code/ItemChoiceHolder.h"));
    ASSERT_FALSE(isWriteMethodInCompoundPresent(
            "arguments/without_writer_code/gen/without_writer_code/Tile.cpp"));
    ASSERT_FALSE(isWriteMethodInCompoundPresent(
            "arguments/without_writer_code/gen/without_writer_code/Tile.h"));
}

TEST_F(WithoutWriterCode, checkSqlTableTypes)
{
    ASSERT_FALSE(isWriteMethodInSqlTablePresent(
            "arguments/without_writer_code/gen/without_writer_code/GeoMapTable.cpp"));
    ASSERT_FALSE(isWriteMethodInSqlTablePresent(
            "arguments/without_writer_code/gen/without_writer_code/GeoMapTable.h"));
}

TEST_F(WithoutWriterCode, checkSqlDatabaseTypes)
{
    ASSERT_FALSE(isWriteMethodInSqlDatabasePresent(
            "arguments/without_writer_code/gen/without_writer_code/WorldDb.cpp"));
    ASSERT_FALSE(isWriteMethodInSqlDatabasePresent(
            "arguments/without_writer_code/gen/without_writer_code/WorldDb.h"));
    ASSERT_FALSE(isWriteMethodInSqlDatabasePresent(
            "arguments/without_writer_code/gen/without_writer_code/MasterDatabase.cpp"));
    ASSERT_FALSE(isWriteMethodInSqlDatabasePresent(
            "arguments/without_writer_code/gen/without_writer_code/MasterDatabase.h"));
}

} // namespace without_writer_code
