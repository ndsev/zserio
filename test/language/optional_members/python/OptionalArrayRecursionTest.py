import os
import zserio

import OptionalMembers
from testutils import getApiDir


class OptionalArrayRecursionTest(OptionalMembers.TestCase):
    def testConstructor(self):
        emptyEmployee = self.api.Employee()
        self.assertEqual("", emptyEmployee.name)
        self.assertEqual(0, emptyEmployee.salary)
        self.assertEqual(None, emptyEmployee.title)
        self.assertEqual(None, emptyEmployee.team_members)

        teamMember = self.api.Employee(
            self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY, self.api.Title.DEVELOPER
        )
        self.assertEqual(self.EMPLOYEE_DEVELOPER1_NAME, teamMember.name)
        self.assertEqual(self.EMPLOYEE_DEVELOPER1_SALARY, teamMember.salary)
        self.assertEqual(self.api.Title.DEVELOPER, teamMember.title)
        self.assertEqual(None, teamMember.team_members)

        teamMember = self.api.Employee(
            name_=self.EMPLOYEE_DEVELOPER1_NAME,
            salary_=self.EMPLOYEE_DEVELOPER1_SALARY,
            title_=self.api.Title.DEVELOPER,
        )
        self.assertEqual(self.EMPLOYEE_DEVELOPER1_NAME, teamMember.name)
        self.assertEqual(self.EMPLOYEE_DEVELOPER1_SALARY, teamMember.salary)
        self.assertEqual(self.api.Title.DEVELOPER, teamMember.title)
        self.assertEqual(None, teamMember.team_members)

    def testEq(self):
        emptyEmployee1 = self.api.Employee()
        emptyEmployee2 = self.api.Employee()
        self.assertTrue(emptyEmployee1 == emptyEmployee2)

        teamLead1 = self._createTeamLead()
        self.assertFalse(teamLead1 == emptyEmployee1)

        teamLead2 = self._createTeamLead()
        self.assertTrue(teamLead1 == teamLead2)

    def testHash(self):
        emptyEmployee1 = self.api.Employee()
        emptyEmployee2 = self.api.Employee()
        self.assertEqual(hash(emptyEmployee1), hash(emptyEmployee2))

        teamLead1 = self._createTeamLead()
        self.assertTrue(hash(teamLead1) != hash(emptyEmployee1))

        teamLead2 = self._createTeamLead()
        self.assertEqual(hash(teamLead1), hash(teamLead2))

        teamLead1.title = self.api.Title.DEVELOPER
        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(198054975, teamLead1.__hash__())
        self.assertEqual(3595797558, teamLead2.__hash__())

    def testIsTeamMembersSetAndUsed(self):
        employee = self._createEmployee(
            self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY, self.api.Title.DEVELOPER
        )
        self.assertFalse(employee.is_team_members_set())
        self.assertFalse(employee.is_team_members_used())

        employee.title = self.api.Title.TEAM_LEAD
        employee.reset_team_members()  # used but not set
        self.assertFalse(employee.is_team_members_set())
        self.assertTrue(employee.is_team_members_used())

        teamLead = self._createTeamLead()
        self.assertTrue(teamLead.is_team_members_set())
        self.assertTrue(teamLead.is_team_members_used())

        teamLead.title = self.api.Title.DEVELOPER  # set but not used
        self.assertTrue(teamLead.is_team_members_set())
        self.assertFalse(teamLead.is_team_members_used())

    def testResetTeamMembers(self):
        employee = self._createTeamLead()
        self.assertTrue(employee.is_team_members_set())
        self.assertTrue(employee.is_team_members_used())

        employee.reset_team_members()  # used but not set
        self.assertFalse(employee.is_team_members_set())
        self.assertTrue(employee.is_team_members_used())
        self.assertEqual(None, employee.team_members)

    def testBitSizeOf(self):
        employee = self._createEmployee(
            self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY, self.api.Title.DEVELOPER
        )
        self.assertEqual(self.DEVELOPER1_BIT_SIZE, employee.bitsizeof())

        teamLead = self._createTeamLead()
        self.assertEqual(self.TEAM_LEAD_BIT_SIZE, teamLead.bitsizeof())

    def testInitializeOffsets(self):
        employee = self._createEmployee(
            self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY, self.api.Title.DEVELOPER
        )
        bitPosition = 1
        self.assertEqual(bitPosition + self.DEVELOPER1_BIT_SIZE, employee.initialize_offsets(bitPosition))

        teamLead = self._createTeamLead()
        self.assertEqual(bitPosition + self.TEAM_LEAD_BIT_SIZE, teamLead.initialize_offsets(bitPosition))

    def testWriteReadEmployee(self):
        employee = self._createEmployee(
            self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY, self.api.Title.DEVELOPER
        )

        writer = zserio.BitStreamWriter()
        employee.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkEmployeeInStream(
            reader, self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY, self.api.Title.DEVELOPER
        )

        reader.bitposition = 0
        readEmployee = self.api.Employee.from_reader(reader)
        self.assertEqual(self.EMPLOYEE_DEVELOPER1_NAME, readEmployee.name)
        self.assertEqual(self.EMPLOYEE_DEVELOPER1_SALARY, readEmployee.salary)
        self.assertEqual(self.api.Title.DEVELOPER, readEmployee.title)

    def testWriteReadTeamLead(self):
        teamLead = self._createTeamLead()
        writer = zserio.BitStreamWriter()
        teamLead.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkTeamLeadInStream(reader)

        reader.bitposition = 0
        readTeamLead = self.api.Employee.from_reader(reader)
        self.assertEqual(self.EMPLOYEE_TEAM_LEAD_NAME, readTeamLead.name)
        self.assertEqual(self.EMPLOYEE_TEAM_LEAD_SALARY, readTeamLead.salary)
        self.assertEqual(self.api.Title.TEAM_LEAD, readTeamLead.title)
        self.assertEqual(self.NUM_DEVELOPERS, len(readTeamLead.team_members))

    def testWriteReadFileEmployee(self):
        employee = self._createEmployee(
            self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY, self.api.Title.DEVELOPER
        )
        filename = self.BLOB_NAME_BASE + "employee.blob"
        zserio.serialize_to_file(employee, filename)

        readEmployee = zserio.deserialize_from_file(self.api.Employee, filename)
        self.assertEqual(employee, readEmployee)

    def testWriteReadFileTeamLead(self):
        teamLead = self._createTeamLead()
        filename = self.BLOB_NAME_BASE + "team_lead.blob"
        zserio.serialize_to_file(teamLead, filename)

        readTeamLead = zserio.deserialize_from_file(self.api.Employee, filename)
        self.assertEqual(teamLead, readTeamLead)

    def _createEmployee(self, name, salary, title):
        employee = self.api.Employee()
        employee.name = name
        employee.salary = salary
        employee.title = title

        return employee

    def _createTeamLead(self):
        teamLead = self._createEmployee(
            self.EMPLOYEE_TEAM_LEAD_NAME, self.EMPLOYEE_TEAM_LEAD_SALARY, self.api.Title.TEAM_LEAD
        )

        teamMember1 = self._createEmployee(
            self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY, self.api.Title.DEVELOPER
        )
        teamMember2 = self._createEmployee(
            self.EMPLOYEE_DEVELOPER2_NAME, self.EMPLOYEE_DEVELOPER2_SALARY, self.api.Title.DEVELOPER
        )
        teamLead.team_members = [teamMember1, teamMember2]

        return teamLead

    def _checkEmployeeInStream(self, reader, name, salary, title):
        self.assertEqual(name, reader.read_string())
        self.assertEqual(salary, reader.read_bits(16))
        self.assertEqual(title.value, reader.read_bits(8))

    def _checkTeamLeadInStream(self, reader):
        self._checkEmployeeInStream(
            reader, self.EMPLOYEE_TEAM_LEAD_NAME, self.EMPLOYEE_TEAM_LEAD_SALARY, self.api.Title.TEAM_LEAD
        )
        self.assertEqual(self.NUM_DEVELOPERS, reader.read_varuint64())
        self._checkEmployeeInStream(
            reader, self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY, self.api.Title.DEVELOPER
        )
        self._checkEmployeeInStream(
            reader, self.EMPLOYEE_DEVELOPER2_NAME, self.EMPLOYEE_DEVELOPER2_SALARY, self.api.Title.DEVELOPER
        )

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "optional_array_recursion_")

    EMPLOYEE_TEAM_LEAD_NAME = "Nico"
    EMPLOYEE_TEAM_LEAD_SALARY = 2000

    EMPLOYEE_DEVELOPER1_NAME = "Mike"
    EMPLOYEE_DEVELOPER1_SALARY = 1000

    EMPLOYEE_DEVELOPER2_NAME = "Luke"
    EMPLOYEE_DEVELOPER2_SALARY = 1800

    NUM_DEVELOPERS = 2
    DEVELOPER1_BIT_SIZE = 4 * 8 + 32
    TEAM_LEAD_BIT_SIZE = 4 * 8 + 32 + 8 + 4 * 8 + 32 + 4 * 8 + 32
