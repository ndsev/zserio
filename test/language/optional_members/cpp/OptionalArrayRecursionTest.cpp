#include <cstring>

#include "gtest/gtest.h"

#include "optional_members/optional_array_recursion/Employee.h"
#include "optional_members/optional_array_recursion/Title.h"

#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace optional_members
{
namespace optional_array_recursion
{

using allocator_type = Employee::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class OptionalArrayRecursionTest : public ::testing::Test
{
protected:
    void fillEmployee(Employee& employee, const char* name, uint16_t salary, Title title)
    {
        employee.setName(name);
        employee.setSalary(salary);
        employee.setTitle(title);
    }

    void fillTeamLead(Employee& teamLead)
    {
        fillEmployee(teamLead, EMPLOYEE_TEAM_LEAD_NAME, EMPLOYEE_TEAM_LEAD_SALARY, Title::TEAM_LEAD);

        vector_type<Employee> teamMembers;
        Employee teamMember1;
        fillEmployee(teamMember1, EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title::DEVELOPER);
        teamMembers.push_back(teamMember1);

        Employee teamMember2;
        fillEmployee(teamMember2, EMPLOYEE_DEVELOPER2_NAME, EMPLOYEE_DEVELOPER2_SALARY, Title::DEVELOPER);
        teamMembers.push_back(teamMember2);

        teamLead.setTeamMembers(teamMembers);
    }

    void checkEmployeeInBitStream(zserio::BitStreamReader& reader, const char* name, uint16_t salary,
            Title title)
    {
        ASSERT_EQ(name, reader.readString<allocator_type>());
        ASSERT_EQ(salary, reader.readBits(16));
        ASSERT_EQ(zserio::enumToValue(title), reader.readBits(8));
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

    static const std::string BLOB_NAME_BASE;

    static const char*const EMPTY_EMPLOYEE_NAME;
    static const uint16_t EMPTY_EMPLOYEE_SALARY;

    static const char* const EMPLOYEE_TEAM_LEAD_NAME;
    static const uint16_t EMPLOYEE_TEAM_LEAD_SALARY;

    static const char* const EMPLOYEE_DEVELOPER1_NAME;
    static const uint16_t EMPLOYEE_DEVELOPER1_SALARY;

    static const char* const EMPLOYEE_DEVELOPER2_NAME;
    static const uint16_t EMPLOYEE_DEVELOPER2_SALARY;

    static const size_t NUM_DEVELOPERS;
    static const size_t EMPTY_EMPLOYEE_BIT_SIZE;
    static const size_t TEAM_LEAD_BIT_SIZE;

    static const size_t TEAM_LEAD_BIT_SIZE_UNUSED_TEAM_MEMBERS;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string OptionalArrayRecursionTest::BLOB_NAME_BASE =
        "language/optional_members/optional_array_recursion_";

const char* const OptionalArrayRecursionTest::EMPTY_EMPLOYEE_NAME = "";
const uint16_t OptionalArrayRecursionTest::EMPTY_EMPLOYEE_SALARY = 0;

const char* const OptionalArrayRecursionTest::EMPLOYEE_TEAM_LEAD_NAME = "Nico";
const uint16_t OptionalArrayRecursionTest::EMPLOYEE_TEAM_LEAD_SALARY = 2000;

const char* const OptionalArrayRecursionTest::EMPLOYEE_DEVELOPER1_NAME = "Mike";
const uint16_t OptionalArrayRecursionTest::EMPLOYEE_DEVELOPER1_SALARY = 1000;

const char* const OptionalArrayRecursionTest::EMPLOYEE_DEVELOPER2_NAME = "Luke";
const uint16_t OptionalArrayRecursionTest::EMPLOYEE_DEVELOPER2_SALARY = 1800;

const size_t OptionalArrayRecursionTest::NUM_DEVELOPERS = 2;
const size_t OptionalArrayRecursionTest::EMPTY_EMPLOYEE_BIT_SIZE = 32;
const size_t OptionalArrayRecursionTest::TEAM_LEAD_BIT_SIZE = EMPTY_EMPLOYEE_BIT_SIZE * 3 + 8 +
        strlen(OptionalArrayRecursionTest::EMPLOYEE_TEAM_LEAD_NAME) * 8 +
        strlen(OptionalArrayRecursionTest::EMPLOYEE_DEVELOPER1_NAME) * 8 +
        strlen(OptionalArrayRecursionTest::EMPLOYEE_DEVELOPER2_NAME) * 8;

const size_t OptionalArrayRecursionTest::TEAM_LEAD_BIT_SIZE_UNUSED_TEAM_MEMBERS = EMPTY_EMPLOYEE_BIT_SIZE +
        strlen(OptionalArrayRecursionTest::EMPLOYEE_TEAM_LEAD_NAME) * 8;

TEST_F(OptionalArrayRecursionTest, isTeamMembersSetAndUsed)
{
    Employee employee(EMPTY_EMPLOYEE_NAME, EMPTY_EMPLOYEE_SALARY, Title::DEVELOPER, zserio::NullOpt);
    ASSERT_FALSE(employee.isTeamMembersSet());
    ASSERT_FALSE(employee.isTeamMembersUsed());

    fillTeamLead(employee);
    ASSERT_TRUE(employee.isTeamMembersSet());
    ASSERT_TRUE(employee.isTeamMembersUsed());

    employee.setTitle(Title::DEVELOPER); // set but not used
    ASSERT_TRUE(employee.isTeamMembersSet());
    ASSERT_FALSE(employee.isTeamMembersUsed());

    employee.setTitle(Title::TEAM_LEAD);
    employee.resetTeamMembers(); // used but not set
    ASSERT_FALSE(employee.isTeamMembersSet());
    ASSERT_TRUE(employee.isTeamMembersUsed());
}

TEST_F(OptionalArrayRecursionTest, resetTeamMembers)
{
    Employee employee;
    fillTeamLead(employee);
    ASSERT_TRUE(employee.isTeamMembersSet());
    ASSERT_TRUE(employee.isTeamMembersUsed());

    employee.resetTeamMembers(); // used but not set
    ASSERT_FALSE(employee.isTeamMembersSet());
    ASSERT_TRUE(employee.isTeamMembersUsed());
    ASSERT_THROW(employee.getTeamMembers(), zserio::CppRuntimeException);
}

TEST_F(OptionalArrayRecursionTest, bitSizeOf)
{
    Employee employee(EMPTY_EMPLOYEE_NAME, EMPTY_EMPLOYEE_SALARY, Title::DEVELOPER, zserio::NullOpt);
    ASSERT_EQ(EMPTY_EMPLOYEE_BIT_SIZE, employee.bitSizeOf());

    fillTeamLead(employee);
    ASSERT_EQ(TEAM_LEAD_BIT_SIZE, employee.bitSizeOf());

    employee.setTitle(Title::DEVELOPER); // set but not used
    ASSERT_EQ(TEAM_LEAD_BIT_SIZE_UNUSED_TEAM_MEMBERS, employee.bitSizeOf());
}

TEST_F(OptionalArrayRecursionTest, initializeOffsets)
{
    Employee employee(EMPTY_EMPLOYEE_NAME, EMPTY_EMPLOYEE_SALARY, Title::DEVELOPER, zserio::NullOpt);
    const size_t bitPosition = 1;
    ASSERT_EQ(bitPosition + EMPTY_EMPLOYEE_BIT_SIZE, employee.initializeOffsets(bitPosition));

    fillTeamLead(employee);
    ASSERT_EQ(bitPosition + TEAM_LEAD_BIT_SIZE, employee.initializeOffsets(bitPosition));

    employee.setTitle(Title::DEVELOPER); // set but not used
    ASSERT_EQ(bitPosition + TEAM_LEAD_BIT_SIZE_UNUSED_TEAM_MEMBERS, employee.initializeOffsets(bitPosition));
}

TEST_F(OptionalArrayRecursionTest, operatorEquality)
{
    Employee employee(EMPTY_EMPLOYEE_NAME, EMPTY_EMPLOYEE_SALARY, Title::DEVELOPER, zserio::NullOpt);

    Employee teamLead1;
    fillTeamLead(teamLead1);

    Employee teamLead2;
    fillTeamLead(teamLead2);

    ASSERT_FALSE(employee == teamLead2);
    ASSERT_TRUE(teamLead1 == teamLead2);

    teamLead1.setTitle(Title::DEVELOPER); // set but not used
    ASSERT_FALSE(teamLead1 == teamLead2);
    teamLead2.setTitle(Title::DEVELOPER); // set but not used
    ASSERT_TRUE(teamLead1 == teamLead2);
}

TEST_F(OptionalArrayRecursionTest, hashCode)
{
    Employee teamLead1;
    Employee teamLead2;

    fillTeamLead(teamLead1);
    fillTeamLead(teamLead2);
    ASSERT_EQ(teamLead1.hashCode(), teamLead2.hashCode());

    teamLead1.setTitle(Title::DEVELOPER); // set but not used
    ASSERT_NE(teamLead1.hashCode(), teamLead2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(198054975, teamLead1.hashCode());
    ASSERT_EQ(3595797558, teamLead2.hashCode());

    teamLead2.setTitle(Title::DEVELOPER); // set but not used
    ASSERT_EQ(teamLead1.hashCode(), teamLead2.hashCode());
}

TEST_F(OptionalArrayRecursionTest, writeReadEmployee)
{
    Employee employee;
    fillEmployee(employee, EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title::DEVELOPER);

    zserio::BitStreamWriter writer(bitBuffer);
    employee.write(writer);
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkEmployeeInBitStream(reader, EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title::DEVELOPER);
    reader.setBitPosition(0);

    Employee readEmployee(reader);
    ASSERT_EQ(EMPLOYEE_DEVELOPER1_NAME, readEmployee.getName());
    ASSERT_EQ(EMPLOYEE_DEVELOPER1_SALARY, readEmployee.getSalary());
    ASSERT_EQ(Title::DEVELOPER, readEmployee.getTitle());
}

TEST_F(OptionalArrayRecursionTest, writeReadTeamLead)
{
    Employee teamLead;
    fillTeamLead(teamLead);

    zserio::BitStreamWriter writer(bitBuffer);
    teamLead.write(writer);
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkTeamLeadInBitStream(reader);
    reader.setBitPosition(0);

    Employee readTeamLead(reader);
    ASSERT_EQ(EMPLOYEE_TEAM_LEAD_NAME, readTeamLead.getName());
    ASSERT_EQ(EMPLOYEE_TEAM_LEAD_SALARY, readTeamLead.getSalary());
    ASSERT_EQ(Title::TEAM_LEAD, readTeamLead.getTitle());
    ASSERT_TRUE(readTeamLead.isTeamMembersSet());
    ASSERT_TRUE(readTeamLead.isTeamMembersUsed());
    vector_type<Employee> teamMembers = readTeamLead.getTeamMembers();
    ASSERT_EQ(NUM_DEVELOPERS, teamMembers.size());

    teamLead.setTitle(Title::DEVELOPER); // set but not used
    zserio::BitStreamWriter writer2(bitBuffer);
    teamLead.write(writer2);
    zserio::BitStreamReader reader2(writer2.getWriteBuffer(), writer2.getBitPosition(), zserio::BitsTag());
    Employee readTeamLead2(reader2);
    ASSERT_EQ(EMPLOYEE_TEAM_LEAD_NAME, readTeamLead2.getName());
    ASSERT_EQ(EMPLOYEE_TEAM_LEAD_SALARY, readTeamLead2.getSalary());
    ASSERT_EQ(Title::DEVELOPER, readTeamLead2.getTitle());
    ASSERT_FALSE(readTeamLead2.isTeamMembersSet());
    ASSERT_FALSE(readTeamLead2.isTeamMembersUsed());
    ASSERT_THROW(readTeamLead2.getTeamMembers(), zserio::CppRuntimeException);
}

TEST_F(OptionalArrayRecursionTest, writeReadFileEmployee)
{
    Employee employee;
    fillEmployee(employee, EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title::DEVELOPER);
    const std::string fileName = BLOB_NAME_BASE + "employee.blob";
    zserio::serializeToFile(employee, fileName);

    Employee readEmployee = zserio::deserializeFromFile<Employee>(fileName);
    ASSERT_EQ(employee, readEmployee);
}

TEST_F(OptionalArrayRecursionTest, writeReadFileTeamLead)
{
    Employee teamLead;
    fillTeamLead(teamLead);
    const std::string fileName = BLOB_NAME_BASE + "team_lead.blob";
    zserio::serializeToFile(teamLead, fileName);

    const auto readTeamLead = zserio::deserializeFromFile<Employee>(fileName);
    ASSERT_EQ(teamLead, readTeamLead);
}

} // namespace optional_array_recursion
} // namespace optional_members
