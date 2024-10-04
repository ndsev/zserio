package optional_members;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

import optional_members.optional_array_recursion.Employee;
import optional_members.optional_array_recursion.Title;

public class OptionalArrayRecursionTest
{
    @Test
    public void bitSizeOf()
    {
        final Employee employee =
                createEmployee(EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title.DEVELOPER);
        assertEquals(DEVELOPER1_BIT_SIZE, employee.bitSizeOf());

        final Employee teamLead = createTeamLead();
        assertEquals(TEAM_LEAD_BIT_SIZE, teamLead.bitSizeOf());
    }

    @Test
    public void isTeamMembersSetAndUsed()
    {
        final Employee employee =
                createEmployee(EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title.DEVELOPER);
        assertFalse(employee.isTeamMembersSet());
        assertFalse(employee.isTeamMembersUsed());

        employee.setTitle(Title.TEAM_LEAD);
        employee.resetTeamMembers(); // used but not set
        assertFalse(employee.isTeamMembersSet());
        assertTrue(employee.isTeamMembersUsed());

        final Employee teamLead = createTeamLead();
        assertTrue(teamLead.isTeamMembersSet());
        assertTrue(teamLead.isTeamMembersUsed());

        teamLead.setTitle(Title.DEVELOPER); // set but not used
        assertTrue(teamLead.isTeamMembersSet());
        assertFalse(teamLead.isTeamMembersUsed());
    }

    @Test
    public void resetTeamMembers()
    {
        final Employee employee = createTeamLead();
        assertTrue(employee.isTeamMembersSet());
        assertTrue(employee.isTeamMembersUsed());

        employee.resetTeamMembers(); // used but not set
        assertFalse(employee.isTeamMembersSet());
        assertTrue(employee.isTeamMembersUsed());
        assertEquals(null, employee.getTeamMembers());
    }

    @Test
    public void equals()
    {
        final Employee emptyEmployee1 = new Employee();
        final Employee emptyEmployee2 = new Employee();
        assertTrue(emptyEmployee1.equals(emptyEmployee2));

        final Employee teamLead1 = createTeamLead();
        assertFalse(teamLead1.equals(emptyEmployee1));

        final Employee teamLead2 = createTeamLead();
        assertTrue(teamLead1.equals(teamLead2));
    }

    @Test
    public void hashCodeMethod()
    {
        final Employee emptyEmployee1 = new Employee();
        final Employee emptyEmployee2 = new Employee();
        assertEquals(emptyEmployee1.hashCode(), emptyEmployee2.hashCode());

        final Employee teamLead1 = createTeamLead();
        assertTrue(teamLead1.hashCode() != emptyEmployee1.hashCode());

        final Employee teamLead2 = createTeamLead();
        assertEquals(teamLead1.hashCode(), teamLead2.hashCode());

        teamLead1.setTitle(Title.DEVELOPER);
        // use hardcoded values to check that the hash code is stable
        assertEquals(198054975, teamLead1.hashCode());
        assertEquals((int)3595797558L, teamLead2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        final Employee employee =
                createEmployee(EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title.DEVELOPER);
        final int bitPosition = 1;
        assertEquals(bitPosition + DEVELOPER1_BIT_SIZE, employee.initializeOffsets(bitPosition));

        final Employee teamLead = createTeamLead();
        assertEquals(bitPosition + TEAM_LEAD_BIT_SIZE, teamLead.initializeOffsets(bitPosition));
    }

    @Test
    public void writeReadFileEmployee() throws IOException
    {
        final Employee employee =
                createEmployee(EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title.DEVELOPER);

        final File employeeFile = new File(BLOB_NAME_BASE + "employee.blob");
        SerializeUtil.serializeToFile(employee, employeeFile);

        Employee readEmployee = SerializeUtil.deserializeFromFile(Employee.class, employeeFile);
        assertEquals(EMPLOYEE_DEVELOPER1_NAME, readEmployee.getName());
        assertEquals(EMPLOYEE_DEVELOPER1_SALARY, readEmployee.getSalary());
        assertEquals(Title.DEVELOPER, readEmployee.getTitle());
    }

    @Test
    public void writeReadEmployee() throws IOException
    {
        final Employee employee =
                createEmployee(EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title.DEVELOPER);

        final BitBuffer bitBuffer = SerializeUtil.serialize(employee);
        checkEmployeeInBitBuffer(
                bitBuffer, EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title.DEVELOPER);

        Employee readEmployee = SerializeUtil.deserialize(Employee.class, bitBuffer);
        assertEquals(EMPLOYEE_DEVELOPER1_NAME, readEmployee.getName());
        assertEquals(EMPLOYEE_DEVELOPER1_SALARY, readEmployee.getSalary());
        assertEquals(Title.DEVELOPER, readEmployee.getTitle());
    }

    @Test
    public void writeReadFileTeamLead() throws IOException
    {
        final Employee teamLead = createTeamLead();

        final File teamLeadFile = new File(BLOB_NAME_BASE + "team_lead.blob");
        SerializeUtil.serializeToFile(teamLead, teamLeadFile);

        final Employee readTeamLead = SerializeUtil.deserializeFromFile(Employee.class, teamLeadFile);
        assertEquals(EMPLOYEE_TEAM_LEAD_NAME, readTeamLead.getName());
        assertEquals(EMPLOYEE_TEAM_LEAD_SALARY, readTeamLead.getSalary());
        assertEquals(Title.TEAM_LEAD, readTeamLead.getTitle());
        assertEquals(NUM_DEVELOPERS, readTeamLead.getTeamMembers().length);
    }

    @Test
    public void writeReadTeamLead() throws IOException
    {
        final Employee teamLead = createTeamLead();

        final BitBuffer bitBuffer = SerializeUtil.serialize(teamLead);
        checkTeamLeadInBitBuffer(bitBuffer);

        final Employee readTeamLead = SerializeUtil.deserialize(Employee.class, bitBuffer);
        assertEquals(EMPLOYEE_TEAM_LEAD_NAME, readTeamLead.getName());
        assertEquals(EMPLOYEE_TEAM_LEAD_SALARY, readTeamLead.getSalary());
        assertEquals(Title.TEAM_LEAD, readTeamLead.getTitle());
        assertEquals(NUM_DEVELOPERS, readTeamLead.getTeamMembers().length);
    }

    private static Employee createEmployee(String name, int salary, Title title)
    {
        final Employee employee = new Employee();
        employee.setName(name);
        employee.setSalary(salary);
        employee.setTitle(title);

        return employee;
    }

    private static Employee createTeamLead()
    {
        final Employee teamLead =
                createEmployee(EMPLOYEE_TEAM_LEAD_NAME, EMPLOYEE_TEAM_LEAD_SALARY, Title.TEAM_LEAD);
        final Employee[] teamMembers = new Employee[] {
                createEmployee(EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title.DEVELOPER),
                createEmployee(EMPLOYEE_DEVELOPER2_NAME, EMPLOYEE_DEVELOPER2_SALARY, Title.DEVELOPER)};
        teamLead.setTeamMembers(teamMembers);

        return teamLead;
    }

    private static void checkEmployeeInStream(BitStreamReader reader, String name, int salary, Title title)
            throws IOException
    {
        assertEquals(name, reader.readString());
        assertEquals(salary, reader.readBits(16));
        assertEquals(title.getValue(), reader.readBits(8));
    }

    private static void checkEmployeeInBitBuffer(BitBuffer bitBuffer, String name, int salary, Title title)
            throws IOException
    {
        try (final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer))
        {
            checkEmployeeInStream(reader, name, salary, title);
        }
    }

    private static void checkTeamLeadInBitBuffer(BitBuffer bitBuffer) throws IOException
    {
        try (final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer))
        {
            checkEmployeeInStream(reader, EMPLOYEE_TEAM_LEAD_NAME, EMPLOYEE_TEAM_LEAD_SALARY, Title.TEAM_LEAD);
            assertEquals(NUM_DEVELOPERS, reader.readVarUInt64());
            checkEmployeeInStream(
                    reader, EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title.DEVELOPER);
            checkEmployeeInStream(
                    reader, EMPLOYEE_DEVELOPER2_NAME, EMPLOYEE_DEVELOPER2_SALARY, Title.DEVELOPER);
        }
    }

    private static final String BLOB_NAME_BASE = "optional_array_recursion_";

    private static final String EMPLOYEE_TEAM_LEAD_NAME = "Nico";
    private static final int EMPLOYEE_TEAM_LEAD_SALARY = 2000;

    private static final String EMPLOYEE_DEVELOPER1_NAME = "Mike";
    private static final int EMPLOYEE_DEVELOPER1_SALARY = 1000;

    private static final String EMPLOYEE_DEVELOPER2_NAME = "Luke";
    private static final int EMPLOYEE_DEVELOPER2_SALARY = 1800;

    private static final int NUM_DEVELOPERS = 2;
    private static final int DEVELOPER1_BIT_SIZE = EMPLOYEE_DEVELOPER1_NAME.length() * 8 + 32;
    private static final int TEAM_LEAD_BIT_SIZE = EMPLOYEE_TEAM_LEAD_NAME.length() * 8 + 32 + 8 +
            DEVELOPER1_BIT_SIZE + EMPLOYEE_DEVELOPER2_NAME.length() * 8 + 32;
}
