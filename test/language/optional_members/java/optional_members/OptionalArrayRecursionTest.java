package optional_members;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import optional_members.optional_array_recursion.Employee;
import optional_members.optional_array_recursion.Title;

import zserio.runtime.array.ObjectArray;
import zserio.runtime.io.FileBitStreamReader;

public class OptionalArrayRecursionTest
{
    @Test
    public void bitSizeOf()
    {
        final Employee employee = createEmployee(EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY,
                Title.DEVELOPER);
        assertEquals(DEVELOPER1_BIT_SIZE, employee.bitSizeOf());

        final Employee teamLead = createTeamLead();
        assertEquals(TEAM_LEAD_BIT_SIZE, teamLead.bitSizeOf());
    }

    @Test
    public void hasTeamMembers()
    {
        final Employee employee = createEmployee(EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY,
                Title.DEVELOPER);
        assertFalse(employee.hasTeamMembers());

        final Employee teamLead = createTeamLead();
        assertTrue(teamLead.hasTeamMembers());
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
    }

    @Test
    public void initializeOffsets()
    {
        final Employee employee = createEmployee(EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY,
                Title.DEVELOPER);
        final int bitPosition = 1;
        assertEquals(bitPosition + DEVELOPER1_BIT_SIZE, employee.initializeOffsets(bitPosition));

        final Employee teamLead = createTeamLead();
        assertEquals(bitPosition + TEAM_LEAD_BIT_SIZE, teamLead.initializeOffsets(bitPosition));
    }

    @Test
    public void fileWriteEmployee() throws IOException
    {
        final Employee employee = createEmployee(EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY,
                Title.DEVELOPER);

        final File employeeFile = new File("employee.bin");
        employee.write(employeeFile);
        final FileBitStreamReader reader = new FileBitStreamReader(employeeFile);
        checkEmployeeInStream(reader, EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title.DEVELOPER);
        reader.close();

        Employee readEmployee = new Employee(employeeFile);
        assertEquals(EMPLOYEE_DEVELOPER1_NAME, readEmployee.getName());
        assertEquals(EMPLOYEE_DEVELOPER1_SALARY, readEmployee.getSalary());
        assertEquals(Title.DEVELOPER, readEmployee.getTitle());
    }

    @Test
    public void fileWriteTeamLead() throws IOException
    {
        final Employee teamLead = createTeamLead();

        final File teamLeadFile = new File("team_lead.bin");
        teamLead.write(teamLeadFile);
        final FileBitStreamReader reader = new FileBitStreamReader(teamLeadFile);
        checkTeamLeadInStream(reader);
        reader.close();

        Employee readTeamLead = new Employee(teamLeadFile);
        assertEquals(EMPLOYEE_TEAM_LEAD_NAME, readTeamLead.getName());
        assertEquals(EMPLOYEE_TEAM_LEAD_SALARY, readTeamLead.getSalary());
        assertEquals(Title.TEAM_LEAD, readTeamLead.getTitle());
        assertEquals(NUM_DEVELOPERS, readTeamLead.getTeamMembers().length());
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
        final Employee teamLead = createEmployee(EMPLOYEE_TEAM_LEAD_NAME, EMPLOYEE_TEAM_LEAD_SALARY,
                Title.TEAM_LEAD);

        final List<Employee> teamMembers = new ArrayList<Employee>();
        final Employee teamMember1 = createEmployee(EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY,
                Title.DEVELOPER);
        teamMembers.add(teamMember1);
        final Employee teamMember2 = createEmployee(EMPLOYEE_DEVELOPER2_NAME, EMPLOYEE_DEVELOPER2_SALARY,
                Title.DEVELOPER);
        teamMembers.add(teamMember2);
        teamLead.setTeamMembers(new ObjectArray<Employee>(teamMembers));

        return teamLead;
    }

    private static void checkEmployeeInStream(FileBitStreamReader reader, String name, int salary,
            Title title) throws IOException
    {
        assertEquals(name, reader.readString());
        assertEquals(salary, reader.readBits(16));
        assertEquals(title.getValue(), reader.readBits(8));
    }

    private static void checkTeamLeadInStream(FileBitStreamReader reader) throws IOException
    {
        checkEmployeeInStream(reader, EMPLOYEE_TEAM_LEAD_NAME, EMPLOYEE_TEAM_LEAD_SALARY, Title.TEAM_LEAD);
        assertEquals(NUM_DEVELOPERS, reader.readVarUInt64());
        checkEmployeeInStream(reader, EMPLOYEE_DEVELOPER1_NAME, EMPLOYEE_DEVELOPER1_SALARY, Title.DEVELOPER);
        checkEmployeeInStream(reader, EMPLOYEE_DEVELOPER2_NAME, EMPLOYEE_DEVELOPER2_SALARY, Title.DEVELOPER);
    }

    private static String   EMPLOYEE_TEAM_LEAD_NAME = "Nico";
    private static int      EMPLOYEE_TEAM_LEAD_SALARY = 2000;

    private static String   EMPLOYEE_DEVELOPER1_NAME = "Mike";
    private static int      EMPLOYEE_DEVELOPER1_SALARY = 1000;

    private static String   EMPLOYEE_DEVELOPER2_NAME = "Luke";
    private static int      EMPLOYEE_DEVELOPER2_SALARY = 1800;

    private static int      NUM_DEVELOPERS = 2;
    private static int      DEVELOPER1_BIT_SIZE = EMPLOYEE_DEVELOPER1_NAME.length() * 8 + 32;
    private static int      TEAM_LEAD_BIT_SIZE = EMPLOYEE_TEAM_LEAD_NAME.length() * 8 + 32 + 8 +
            DEVELOPER1_BIT_SIZE + EMPLOYEE_DEVELOPER2_NAME.length() * 8 + 32;
}
