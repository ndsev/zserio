import unittest
import zserio

from testutils import getZserioApi

class OptionalArrayRecursionTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "optional_members.zs").optional_array_recursion

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

    def testHasTeamMembers(self):
        employee = self._createEmployee(self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY,
                                        self.api.Title.DEVELOPER)
        self.assertFalse(employee.hasTeamMembers())

        teamLead = self._createTeamLead()
        self.assertTrue(teamLead.hasTeamMembers())

    def testBitSizeOf(self):
        employee = self._createEmployee(self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY,
                                        self.api.Title.DEVELOPER)
        self.assertEqual(self.DEVELOPER1_BIT_SIZE, employee.bitSizeOf())

        teamLead = self._createTeamLead()
        self.assertEqual(self.TEAM_LEAD_BIT_SIZE, teamLead.bitSizeOf())

    def testInitializeOffsets(self):
        employee = self._createEmployee(self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY,
                                        self.api.Title.DEVELOPER)
        bitPosition = 1
        self.assertEqual(bitPosition + self.DEVELOPER1_BIT_SIZE, employee.initializeOffsets(bitPosition))

        teamLead = self._createTeamLead()
        self.assertEqual(bitPosition + self.TEAM_LEAD_BIT_SIZE, teamLead.initializeOffsets(bitPosition))

    def testWriteEmployee(self):
        employee = self._createEmployee(self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY,
                                        self.api.Title.DEVELOPER)

        writer = zserio.BitStreamWriter()
        employee.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        self._checkEmployeeInStream(reader, self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY,
                                    self.api.Title.DEVELOPER)

        reader.setBitPosition(0)
        readEmployee = self.api.Employee.fromReader(reader)
        self.assertEqual(self.EMPLOYEE_DEVELOPER1_NAME, readEmployee.getName())
        self.assertEqual(self.EMPLOYEE_DEVELOPER1_SALARY, readEmployee.getSalary())
        self.assertEqual(self.api.Title.DEVELOPER, readEmployee.getTitle())

    def testWriteTeamLead(self):
        teamLead = self._createTeamLead()
        writer = zserio.BitStreamWriter()
        teamLead.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        self._checkTeamLeadInStream(reader)

        reader.setBitPosition(0)
        readTeamLead = self.api.Employee.fromReader(reader)
        self.assertEqual(self.EMPLOYEE_TEAM_LEAD_NAME, readTeamLead.getName())
        self.assertEqual(self.EMPLOYEE_TEAM_LEAD_SALARY, readTeamLead.getSalary())
        self.assertEqual(self.api.Title.TEAM_LEAD, readTeamLead.getTitle())
        self.assertEqual(self.NUM_DEVELOPERS, len(readTeamLead.getTeamMembers()))

    def _createEmployee(self, name, salary, title):
        employee = self.api.Employee()
        employee.setName(name)
        employee.setSalary(salary)
        employee.setTitle(title)

        return employee

    def _createTeamLead(self):
        teamLead = self._createEmployee(self.EMPLOYEE_TEAM_LEAD_NAME, self.EMPLOYEE_TEAM_LEAD_SALARY,
                                        self.api.Title.TEAM_LEAD)

        teamMember1 = self._createEmployee(self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY,
                                           self.api.Title.DEVELOPER)
        teamMember2 = self._createEmployee(self.EMPLOYEE_DEVELOPER2_NAME, self.EMPLOYEE_DEVELOPER2_SALARY,
                                           self.api.Title.DEVELOPER)
        teamLead.setTeamMembers([teamMember1, teamMember2])

        return teamLead

    def _checkEmployeeInStream(self, reader, name, salary, title):
        self.assertEqual(name, reader.readString())
        self.assertEqual(salary, reader.readBits(16))
        self.assertEqual(title.value, reader.readBits(8))

    def _checkTeamLeadInStream(self, reader):
        self._checkEmployeeInStream(reader, self.EMPLOYEE_TEAM_LEAD_NAME, self.EMPLOYEE_TEAM_LEAD_SALARY,
                                    self.api.Title.TEAM_LEAD)
        self.assertEqual(self.NUM_DEVELOPERS, reader.readVarUInt64())
        self._checkEmployeeInStream(reader, self.EMPLOYEE_DEVELOPER1_NAME, self.EMPLOYEE_DEVELOPER1_SALARY,
                                    self.api.Title.DEVELOPER)
        self._checkEmployeeInStream(reader, self.EMPLOYEE_DEVELOPER2_NAME, self.EMPLOYEE_DEVELOPER2_SALARY,
                                    self.api.Title.DEVELOPER)

    EMPLOYEE_TEAM_LEAD_NAME = "Nico"
    EMPLOYEE_TEAM_LEAD_SALARY = 2000

    EMPLOYEE_DEVELOPER1_NAME = "Mike"
    EMPLOYEE_DEVELOPER1_SALARY = 1000

    EMPLOYEE_DEVELOPER2_NAME = "Luke"
    EMPLOYEE_DEVELOPER2_SALARY = 1800

    NUM_DEVELOPERS = 2
    DEVELOPER1_BIT_SIZE = 4 * 8 + 32
    TEAM_LEAD_BIT_SIZE = 4 * 8 + 32 + 8 + 4 * 8 + 32 + 4 * 8 + 32
