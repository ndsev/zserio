import os
import apsw

import SqlConstraints


class FieldConstraintsTest(SqlConstraints.TestCaseWithDb):
    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._database = self.api.TestDb.from_file(self.dbFileName)
        self._database.create_schema()
        self._constraintsTable = self._database.field_constraints_table

    def tearDown(self):
        self._database.close()

    def testWithoutSql(self):
        rowDict = self._createRowDict()
        rowDict["withoutSql"] = None
        self._writeRowDict(rowDict)

    def testSqlNotNull(self):
        rowDict = self._createRowDict()
        rowDict["sqlNotNull"] = None
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
        self.assertTrue(
            "NOT NULL constraint failed: fieldConstraintsTable.sqlNotNull" in str(context.exception),
            str(context.exception),
        )

    def testSqlDefaultNull(self):
        rowDict = self._createRowDict()
        rowDict["sqlDefaultNull"] = None
        self._writeRowDict(rowDict)

    def testSqlCheckConstant(self):
        rowDict = self._createRowDict()
        rowDict["sqlCheckConstant"] = self.WRONG_CONSTRAINTS_CONSTANT
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
        self.assertTrue(
            "CHECK constraint failed: sqlCheckConstant" in str(context.exception), str(context.exception)
        )

    def testSqlCheckImportedConstant(self):
        rowDict = self._createRowDict()
        rowDict["sqlCheckImportedConstant"] = self.WRONG_IMPORTED_CONSTRAINTS_CONSTANT
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
        self.assertTrue(
            "CHECK constraint failed: sqlCheckImportedConstant" in str(context.exception),
            str(context.exception),
        )

    def testSqlCheckUnicodeEscape(self):
        rowDict = self._createRowDict()
        rowDict["sqlCheckUnicodeEscape"] = self.WRONG_UNICODE_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
        self.assertTrue(
            "CHECK constraint failed: sqlCheckUnicodeEscape" in str(context.exception), str(context.exception)
        )

    def testSqlCheckHexEscape(self):
        rowDict = self._createRowDict()
        rowDict["sqlCheckHexEscape"] = self.WRONG_HEX_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
        self.assertTrue(
            "CHECK constraint failed: sqlCheckHexEscape" in str(context.exception), str(context.exception)
        )

    def testSqlCheckOctalEscape(self):
        rowDict = self._createRowDict()
        rowDict["sqlCheckOctalEscape"] = self.WRONG_OCTAL_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
        self.assertTrue(
            "CHECK constraint failed: sqlCheckOctalEscape" in str(context.exception), str(context.exception)
        )

    def _createRowDict(self):
        return {
            "primaryKey": 1,
            "withoutSql": 1,
            "sqlNotNull": 1,
            "sqlDefaultNull": 1,
            "sqlCheckConstant": 1,
            "sqlCheckImportedConstant": 1,
            "sqlCheckUnicodeEscape": self.UNICODE_ESCAPE_CONST,
            "sqlCheckHexEscape": self.HEX_ESCAPE_CONST,
            "sqlCheckOctalEscape": self.OCTAL_ESCAPE_CONST,
        }

    def _writeRowDict(self, rowDict):
        row = [
            (
                rowDict["primaryKey"],
                rowDict["withoutSql"],
                rowDict["sqlNotNull"],
                rowDict["sqlDefaultNull"],
                rowDict["sqlCheckConstant"],
                rowDict["sqlCheckImportedConstant"],
                rowDict["sqlCheckUnicodeEscape"],
                rowDict["sqlCheckHexEscape"],
                rowDict["sqlCheckOctalEscape"],
            )
        ]
        self._constraintsTable.write(row)

    UNICODE_ESCAPE_CONST = 1
    HEX_ESCAPE_CONST = 2
    OCTAL_ESCAPE_CONST = 3

    WRONG_CONSTRAINTS_CONSTANT = 124
    WRONG_IMPORTED_CONSTRAINTS_CONSTANT = 322

    WRONG_UNICODE_ESCAPE_CONST = 0
    WRONG_HEX_ESCAPE_CONST = 0
    WRONG_OCTAL_ESCAPE_CONST = 0
