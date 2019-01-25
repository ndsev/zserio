import unittest
import os
import apsw

from testutils import getZserioApi, getApiDir

class SqlConstraintsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "sql_constraints.zs")
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)), "sql_constraints_test.sqlite")

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)
        self._database = self.api.TestDb.fromFile(self._fileName)
        self._database.createSchema()
        self._constraintsTable = self._database.getConstraintsTable()

    def testWithoutSql(self):
        primaryKey = 1
        withoutSql = None
        sqlNotNull = 1
        sqlDefaultNull = 1
        sqlNull = 1
        sqlCheckConstant = 1
        sqlCheckEnum = self.api.ConstraintsEnum.VALUE1
        sqlCheckUnicodeEscape = self.UNICODE_ESCAPE_CONST
        sqlCheckHexEscape = self.HEX_ESCAPE_CONST
        sqlCheckOctalEscape = self.OCTAL_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._constraintsTable.write([(primaryKey, withoutSql, sqlNotNull, sqlDefaultNull, sqlNull,
                                           sqlCheckConstant, sqlCheckEnum, sqlCheckUnicodeEscape,
                                           sqlCheckHexEscape, sqlCheckOctalEscape)])
            self.assertTrue("NOT NULL constraint failed: constraintsTable.withoutSql" in str(context.exception))

    def testSqlNotNull(self):
        primaryKey = 1
        withoutSql = 1
        sqlNotNull = None
        sqlDefaultNull = 1
        sqlNull = 1
        sqlCheckConstant = 1
        sqlCheckEnum = self.api.ConstraintsEnum.VALUE1
        sqlCheckUnicodeEscape = self.UNICODE_ESCAPE_CONST
        sqlCheckHexEscape = self.HEX_ESCAPE_CONST
        sqlCheckOctalEscape = self.OCTAL_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._constraintsTable.write([(primaryKey, withoutSql, sqlNotNull, sqlDefaultNull, sqlNull,
                                           sqlCheckConstant, sqlCheckEnum, sqlCheckUnicodeEscape,
                                           sqlCheckHexEscape, sqlCheckOctalEscape)])
            self.assertTrue("NOT NULL constraint failed: constraintsTable.sqlNotNull" in str(context.exception))

    def testSqlDefaultNull(self):
        primaryKey = 1
        withoutSql = 1
        sqlNotNull = 1
        sqlDefaultNull = None
        sqlNull = 1
        sqlCheckConstant = 1
        sqlCheckEnum = self.api.ConstraintsEnum.VALUE1
        sqlCheckUnicodeEscape = self.UNICODE_ESCAPE_CONST
        sqlCheckHexEscape = self.HEX_ESCAPE_CONST
        sqlCheckOctalEscape = self.OCTAL_ESCAPE_CONST
        self._constraintsTable.write([(primaryKey, withoutSql, sqlNotNull, sqlDefaultNull, sqlNull,
                                       sqlCheckConstant, sqlCheckEnum, sqlCheckUnicodeEscape,
                                       sqlCheckHexEscape, sqlCheckOctalEscape)])

    def testSqlNull(self):
        primaryKey = 1
        withoutSql = 1
        sqlNotNull = 1
        sqlDefaultNull = 1
        sqlNull = None
        sqlCheckConstant = 1
        sqlCheckEnum = self.api.ConstraintsEnum.VALUE1
        sqlCheckUnicodeEscape = self.UNICODE_ESCAPE_CONST
        sqlCheckHexEscape = self.HEX_ESCAPE_CONST
        sqlCheckOctalEscape = self.OCTAL_ESCAPE_CONST
        self._constraintsTable.write([(primaryKey, withoutSql, sqlNotNull, sqlDefaultNull, sqlNull,
                                       sqlCheckConstant, sqlCheckEnum, sqlCheckUnicodeEscape,
                                       sqlCheckHexEscape, sqlCheckOctalEscape)])

    def testSqlCheckConstant(self):
        primaryKey = 1
        withoutSql = 1
        sqlNotNull = 1
        sqlDefaultNull = 1
        sqlNull = 1
        sqlCheckConstant = self.api.ConstraintsConstant
        sqlCheckEnum = self.api.ConstraintsEnum.VALUE1
        sqlCheckUnicodeEscape = self.UNICODE_ESCAPE_CONST
        sqlCheckHexEscape = self.HEX_ESCAPE_CONST
        sqlCheckOctalEscape = self.OCTAL_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._constraintsTable.write([(primaryKey, withoutSql, sqlNotNull, sqlDefaultNull, sqlNull,
                                           sqlCheckConstant, sqlCheckEnum, sqlCheckUnicodeEscape,
                                           sqlCheckHexEscape, sqlCheckOctalEscape)])
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def testSqlCheckEnum(self):
        primaryKey = 1
        withoutSql = 1
        sqlNotNull = 1
        sqlDefaultNull = 1
        sqlNull = 1
        sqlCheckConstant = 1
        sqlCheckEnum = self.api.ConstraintsEnum.VALUE2
        sqlCheckUnicodeEscape = self.UNICODE_ESCAPE_CONST
        sqlCheckHexEscape = self.HEX_ESCAPE_CONST
        sqlCheckOctalEscape = self.OCTAL_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._constraintsTable.write([(primaryKey, withoutSql, sqlNotNull, sqlDefaultNull, sqlNull,
                                           sqlCheckConstant, sqlCheckEnum, sqlCheckUnicodeEscape,
                                           sqlCheckHexEscape, sqlCheckOctalEscape)])
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def testSqlCheckUnicodeEscape(self):
        primaryKey = 1
        withoutSql = 1
        sqlNotNull = 1
        sqlDefaultNull = 1
        sqlNull = 1
        sqlCheckConstant = 1
        sqlCheckEnum = self.api.ConstraintsEnum.VALUE1
        sqlCheckUnicodeEscape = self.WRONG_UNICODE_ESCAPE_CONST
        sqlCheckHexEscape = self.HEX_ESCAPE_CONST
        sqlCheckOctalEscape = self.OCTAL_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._constraintsTable.write([(primaryKey, withoutSql, sqlNotNull, sqlDefaultNull, sqlNull,
                                           sqlCheckConstant, sqlCheckEnum, sqlCheckUnicodeEscape,
                                           sqlCheckHexEscape, sqlCheckOctalEscape)])
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def testSqlCheckHexEscape(self):
        primaryKey = 1
        withoutSql = 1
        sqlNotNull = 1
        sqlDefaultNull = 1
        sqlNull = 1
        sqlCheckConstant = 1
        sqlCheckEnum = self.api.ConstraintsEnum.VALUE1
        sqlCheckUnicodeEscape = self.UNICODE_ESCAPE_CONST
        sqlCheckHexEscape = self.WRONG_HEX_ESCAPE_CONST
        sqlCheckOctalEscape = self.OCTAL_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._constraintsTable.write([(primaryKey, withoutSql, sqlNotNull, sqlDefaultNull, sqlNull,
                                           sqlCheckConstant, sqlCheckEnum, sqlCheckUnicodeEscape,
                                           sqlCheckHexEscape, sqlCheckOctalEscape)])
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def testSqlCheckOctalEscape(self):
        primaryKey = 1
        withoutSql = 1
        sqlNotNull = 1
        sqlDefaultNull = 1
        sqlNull = 1
        sqlCheckConstant = 1
        sqlCheckEnum = self.api.ConstraintsEnum.VALUE1
        sqlCheckUnicodeEscape = self.UNICODE_ESCAPE_CONST
        sqlCheckHexEscape = self.HEX_ESCAPE_CONST
        sqlCheckOctalEscape = self.WRONG_OCTAL_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._constraintsTable.write([(primaryKey, withoutSql, sqlNotNull, sqlDefaultNull, sqlNull,
                                           sqlCheckConstant, sqlCheckEnum, sqlCheckUnicodeEscape,
                                           sqlCheckHexEscape, sqlCheckOctalEscape)])
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    UNICODE_ESCAPE_CONST = 1
    HEX_ESCAPE_CONST = 2
    OCTAL_ESCAPE_CONST = 3

    WRONG_UNICODE_ESCAPE_CONST = 0
    WRONG_HEX_ESCAPE_CONST = 0
    WRONG_OCTAL_ESCAPE_CONST = 0
