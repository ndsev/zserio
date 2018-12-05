#include <fstream>
#include <string>

#include "gtest/gtest.h"

namespace without_writer_code
{

class WithoutWriterCode : public ::testing::Test
{
protected:
    bool isMethodInTypePresent(const char* typeName, const char* methodName)
    {
        const std::string filePath = std::string(PATH) + typeName;
        return (isMethodInFilePresent((filePath + ".cpp").c_str(), methodName) ||
                isMethodInFilePresent((filePath + ".h").c_str(), methodName));
    }

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

    bool isWriteMethodInUserTypePresent(const char* typeName)
    {
        return (isMethodInTypePresent(typeName, "initializeOffsets(") ||
                isMethodInTypePresent(typeName, "write("));
    }

    bool isWriteMethodInSqlTablePresent(const char* typeName)
    {
        return (isMethodInTypePresent(typeName, "createTable(") ||
                isMethodInTypePresent(typeName, "createOrdinaryRowIdTable(") ||
                isMethodInTypePresent(typeName, "deleteTable(") ||
                isMethodInTypePresent(typeName, "write(") ||
                isMethodInTypePresent(typeName, "update(") ||
                isMethodInTypePresent(typeName, "writeRow(") ||
                isMethodInTypePresent(typeName, "appendCreateTableToQuery("));
    }

    bool isWriteMethodInSqlDatabasePresent(const char* typeName)
    {
        return (isMethodInTypePresent(typeName, "createSchema(") ||
                isMethodInTypePresent(typeName, "deleteSchema("));
    }

    static const char* PATH;
};

const char* WithoutWriterCode::PATH = "arguments/without_writer_code/gen/without_writer_code/";

TEST_F(WithoutWriterCode, checkUserTypes)
{
    ASSERT_FALSE(isWriteMethodInUserTypePresent("Item"));
    ASSERT_FALSE(isWriteMethodInUserTypePresent("ItemChoice"));
    ASSERT_FALSE(isWriteMethodInUserTypePresent("ItemChoiceHolder"));
    ASSERT_FALSE(isWriteMethodInUserTypePresent("Tile"));
    ASSERT_FALSE(isWriteMethodInUserTypePresent("ElementsUnion"));
    ASSERT_FALSE(isWriteMethodInUserTypePresent("TypeEnum"));
}

TEST_F(WithoutWriterCode, checkSqlTableTypes)
{
    ASSERT_FALSE(isWriteMethodInSqlTablePresent("GeoMapTable"));
}

TEST_F(WithoutWriterCode, checkSqlDatabaseTypes)
{
    ASSERT_FALSE(isWriteMethodInSqlDatabasePresent("WorldDb"));
    ASSERT_FALSE(isWriteMethodInSqlDatabasePresent("MasterDatabase"));
}

} // namespace without_writer_code
