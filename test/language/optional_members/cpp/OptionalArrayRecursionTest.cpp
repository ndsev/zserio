#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#
#include "optional_members/optional_array_recursion/Employee.h"
#include "optional_members/optional_array_recursion/Title.h"

namespace optional_members
{
namespace optional_array_recursion
{

class OptionalArrayRecursionTest : public ::testing::Test
{
protected:
    void fillEmployee(Employee& employee, const char name[], uint16_t salary, Title title)
    {
        employee.setName(name);
        employee.setSalary(salary);
        employee.setTitle(title);
    }

    void fillTeamLead(Employee& teamLead)
    {
        fillEmployee(teamLead, EMPLOYEE_TEAM_LEAD_NAME, EMPLOYEE_TEAM_LEAD_SALARY, Title::TEAM_LEAD);

        zserio::ObjectArray<Employee> teamMembers;
        Employee teamMember1;
        fillEmployee(teamMember1, EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title::DEVELOPER);
        teamMembers.push_back(teamMember1);

        Employee teamMember2;
        fillEmployee(teamMember2, EMPLOYEE_DEVELOPER2_NAME, EMPLOYEE_DEVELOPER2_SALARY, Title::DEVELOPER);
        teamMembers.push_back(teamMember2);

        teamLead.setTeamMembers(teamMembers);
    }

    void checkEmployeeInBitStream(zserio::BitStreamReader& reader, const char name[], uint16_t salary,
            Title title)
    {
        ASSERT_EQ(name, reader.readString());
        ASSERT_EQ(salary, reader.readBits(16));
        ASSERT_EQ(title, reader.readBits(8));
    }

    void checkTeamLeadInBitStream(zserio::BitStreamReader& reader)
    {
        checkEmployeeInBitStream(reader, EMPLOYEE_TEAM_LEAD_NAME, EMPLOYEE_TEAM_LEAD_SALARY, Title::TEAM_LEAD);
        ASSERT_EQ(NUM_DEVELOPERS, reader.readVarUInt64());
        checkEmployeeInBitStream(reader, EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY,
                Title::DEVELOPER);
        checkEmployeeInBitStream(reader, EMPLOYEE_DEVELOPER2_NAME, EMPLOYEE_DEVELOPER2_SALARY,
                Title::DEVELOPER);
    }

    static const char   EMPLOYEE_TEAM_LEAD_NAME[];
    static uint16_t     EMPLOYEE_TEAM_LEAD_SALARY;

    static const char   EMPLOYEE_DEVELOPER1_NAME[];
    static uint16_t     EMPLOYEE_DEVELOPER1_SALARY;

    static const char   EMPLOYEE_DEVELOPER2_NAME[];
    static uint16_t     EMPLOYEE_DEVELOPER2_SALARY;

    static const size_t NUM_DEVELOPERS;
    static const size_t EMPTY_EMPLOYEE_BIT_SIZE;
    static const size_t TEAM_LEAD_BIT_SIZE;
};

const char  OptionalArrayRecursionTest::EMPLOYEE_TEAM_LEAD_NAME[] = "Nico";
uint16_t    OptionalArrayRecursionTest::EMPLOYEE_TEAM_LEAD_SALARY = 2000;

const char  OptionalArrayRecursionTest::EMPLOYEE_DEVELOPER1_NAME[] = "Mike";
uint16_t    OptionalArrayRecursionTest::EMPLOYEE_DEVELOPER1_SALARY = 1000;

const char  OptionalArrayRecursionTest::EMPLOYEE_DEVELOPER2_NAME[] = "Luke";
uint16_t    OptionalArrayRecursionTest::EMPLOYEE_DEVELOPER2_SALARY = 1800;

const size_t OptionalArrayRecursionTest::NUM_DEVELOPERS = 2;
const size_t OptionalArrayRecursionTest::EMPTY_EMPLOYEE_BIT_SIZE = 32;
const size_t OptionalArrayRecursionTest::TEAM_LEAD_BIT_SIZE = EMPTY_EMPLOYEE_BIT_SIZE * 3 + 8 +
        (sizeof(OptionalArrayRecursionTest::EMPLOYEE_TEAM_LEAD_NAME) - 1) * 8 +
        (sizeof(OptionalArrayRecursionTest::EMPLOYEE_DEVELOPER1_NAME) - 1) * 8 +
        (sizeof(OptionalArrayRecursionTest::EMPLOYEE_DEVELOPER2_NAME) - 1) * 8;

TEST_F(OptionalArrayRecursionTest, hasTeamMembers)
{
    Employee teamLead;
    ASSERT_FALSE(teamLead.hasTeamMembers());

    fillTeamLead(teamLead);
    ASSERT_TRUE(teamLead.hasTeamMembers());
}

TEST_F(OptionalArrayRecursionTest, bitSizeOf)
{
    Employee teamLead;
    ASSERT_EQ(EMPTY_EMPLOYEE_BIT_SIZE, teamLead.bitSizeOf());

    fillTeamLead(teamLead);
    ASSERT_EQ(TEAM_LEAD_BIT_SIZE, teamLead.bitSizeOf());
}

TEST_F(OptionalArrayRecursionTest, initializeOffsets)
{
    Employee teamLead;
    const size_t bitPosition = 1;
    ASSERT_EQ(bitPosition + EMPTY_EMPLOYEE_BIT_SIZE, teamLead.initializeOffsets(bitPosition));

    fillTeamLead(teamLead);
    ASSERT_EQ(bitPosition + TEAM_LEAD_BIT_SIZE, teamLead.initializeOffsets(bitPosition));
}

TEST_F(OptionalArrayRecursionTest, operatorEquality)
{
    Employee teamLead1;
    Employee teamLead2;
    ASSERT_TRUE(teamLead1 == teamLead2);

    fillTeamLead(teamLead1);
    ASSERT_FALSE(teamLead1 == teamLead2);

    fillTeamLead(teamLead2);
    ASSERT_TRUE(teamLead1 == teamLead2);
}

TEST_F(OptionalArrayRecursionTest, hashCode)
{
    Employee teamLead1;
    Employee teamLead2;
    ASSERT_EQ(teamLead1.hashCode(), teamLead2.hashCode());

    fillTeamLead(teamLead1);
    ASSERT_NE(teamLead1.hashCode(), teamLead2.hashCode());

    fillTeamLead(teamLead2);
    ASSERT_EQ(teamLead1.hashCode(), teamLead2.hashCode());
}

TEST_F(OptionalArrayRecursionTest, writeEmployee)
{
    Employee employee;
    fillEmployee(employee, EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title::DEVELOPER);

    zserio::BitStreamWriter writer;
    employee.write(writer);
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    checkEmployeeInBitStream(reader, EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title::DEVELOPER);
    reader.setBitPosition(0);

    Employee readTeamLead(reader);
    ASSERT_EQ(EMPLOYEE_DEVELOPER1_NAME, readTeamLead.getName());
    ASSERT_EQ(EMPLOYEE_DEVELOPER1_SALARY, readTeamLead.getSalary());
    ASSERT_EQ(Title::DEVELOPER, readTeamLead.getTitle());
}

TEST_F(OptionalArrayRecursionTest, writeTeamLead)
{
    Employee teamLead;
    fillTeamLead(teamLead);

    zserio::BitStreamWriter writer;
    teamLead.write(writer);
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    checkTeamLeadInBitStream(reader);
    reader.setBitPosition(0);

    Employee readTeamLead(reader);
    ASSERT_EQ(EMPLOYEE_TEAM_LEAD_NAME, readTeamLead.getName());
    ASSERT_EQ(EMPLOYEE_TEAM_LEAD_SALARY, readTeamLead.getSalary());
    ASSERT_EQ(Title::TEAM_LEAD, readTeamLead.getTitle());
    ASSERT_TRUE(readTeamLead.hasTeamMembers());
    zserio::ObjectArray<Employee> teamMembers = readTeamLead.getTeamMembers();
    ASSERT_EQ(NUM_DEVELOPERS, teamMembers.size());
}

} // namespace optional_array_recursion
} // namespace optional_members
