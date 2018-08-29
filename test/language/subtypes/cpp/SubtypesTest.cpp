#include <cstdio>
#include <string>
#include <fstream>

#include "gtest/gtest.h"

#include "subtypes/TestStructure.h"
#include "subtypes/SubtypeStructure.h"
#include "subtypes/Database.h"
#include "subtypes/ParameterizedSubtypeStruct.h"

namespace subtypes
{

TEST(SubtypesTest, Uint16Subtype)
{
    const Identifier identifier = 0xFFFF;
    TestStructure testStructure;
    testStructure.setIdentifier(identifier);
    const Identifier readIdentifier = testStructure.getIdentifier();
    ASSERT_EQ(identifier, readIdentifier);
}

TEST(SubtypesTest, TestStructureSubtype)
{
    const Identifier identifier = 0xFFFF;
    const std::string name = "Name";
    Student student;
    student.setIdentifier(identifier);
    student.setName(name);

    SubtypeStructure subtypeStructure;
    subtypeStructure.setStudent(student);
    const Student readStudent = subtypeStructure.getStudent();

    ASSERT_EQ(student, readStudent);
}

TEST(SubtypesTest, TestSubtypedTable)
{
    const std::string dbFileName = "subtypes_test.sqlite";
    std::remove(dbFileName.c_str());

    Database database(dbFileName);
    database.createSchema();
    ASSERT_TRUE(database.isOpen());

    // check if database does contain the table
    std::string tableName;
    sqlite3_stmt* statement;
    const std::string sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='students'";
    int result = sqlite3_prepare_v2(database.getConnection(), sqlQuery.c_str(), -1, &statement, NULL);
    ASSERT_EQ(result, SQLITE_OK);
    result = sqlite3_step(statement);
    if (result == SQLITE_ROW)
    {
        const unsigned char* readTableName = sqlite3_column_text(statement, 0);
        if (readTableName != NULL)
            tableName = reinterpret_cast<const char*>(readTableName);
    }
    sqlite3_finalize(statement);

    ASSERT_EQ("students", tableName);

    // check table getter
    TestTable& studentsAsTestTable = database.getStudents();
    SubtypedTable& studentsAsSubtypedTable = database.getStudents();
    ASSERT_EQ(&studentsAsTestTable, &studentsAsSubtypedTable);
}

namespace
{
    bool isCodeInFilePresent(const char* fileName, const char* code)
    {
        std::ifstream file(fileName);
        bool isPresent = false;
        std::string line;
        while (std::getline(file, line))
        {
            if (line.find(code) != std::string::npos)
            {
                isPresent = true;
                break;
            }
        }
        file.close();

        return isPresent;
    }
}

TEST(SubtypesTest, TestParamaterizedSubtype)
{
    // just check that ParameterizedSubtype is defined and that it's same as the ParameterizedStruct
    ParameterizedSubtypeStruct s;
    ParameterizedSubtype& parameterizedSubtype = s.getParameterizedSubtype();
    ParameterizedStruct& parameterizedStruct = s.getParameterizedSubtype();
    zserio::ObjectArray<ParameterizedSubtype>& parameterizedSubtypeArray = s.getParameterizedSubtypeArray();
    zserio::ObjectArray<ParameterizedStruct>& parameterizedStructArray = s.getParameterizedSubtypeArray();
    ASSERT_EQ(&parameterizedSubtype, &parameterizedStruct);
    ASSERT_EQ(&parameterizedSubtypeArray, &parameterizedStructArray);

    // ensure that include to the subtype is present and that the subtype is used in
    // ParameterizedSubtypeStruct's accessors
    ASSERT_TRUE(isCodeInFilePresent("language/subtypes/gen/subtypes/ParameterizedSubtypeStruct.h",
            "#include \"subtypes/ParameterizedSubtype.h\""));

    ASSERT_TRUE(isCodeInFilePresent("language/subtypes/gen/subtypes/ParameterizedSubtypeStruct.h",
            "subtypes::ParameterizedSubtype& getParameterizedSubtype()"));
    ASSERT_TRUE(isCodeInFilePresent("language/subtypes/gen/subtypes/ParameterizedSubtypeStruct.h",
            "const subtypes::ParameterizedSubtype& getParameterizedSubtype() const"));
    ASSERT_TRUE(isCodeInFilePresent("language/subtypes/gen/subtypes/ParameterizedSubtypeStruct.h",
            "void setParameterizedSubtype(const subtypes::ParameterizedSubtype& parameterizedSubtype)"));

    ASSERT_TRUE(isCodeInFilePresent("language/subtypes/gen/subtypes/ParameterizedSubtypeStruct.h",
            "zserio::ObjectArray<subtypes::ParameterizedSubtype>& getParameterizedSubtypeArray()"));
    ASSERT_TRUE(isCodeInFilePresent("language/subtypes/gen/subtypes/ParameterizedSubtypeStruct.h",
            "const zserio::ObjectArray<subtypes::ParameterizedSubtype>& getParameterizedSubtypeArray() const"));
    ASSERT_TRUE(isCodeInFilePresent("language/subtypes/gen/subtypes/ParameterizedSubtypeStruct.h",
            "void setParameterizedSubtypeArray"
                    "(const zserio::ObjectArray<subtypes::ParameterizedSubtype>& parameterizedSubtypeArray)"));
}

} // namespace subtypes
