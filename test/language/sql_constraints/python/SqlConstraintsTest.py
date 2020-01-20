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

    def tearDown(self):
        self._database.close()

    def testWithoutSql(self):
        rowDict = self._createRowDict()
        rowDict['withoutSql'] = None
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
            self.assertTrue("NOT NULL constraint failed: constraintsTable.withoutSql" in str(context.exception))

    def testSqlNotNull(self):
        rowDict = self._createRowDict()
        rowDict['sqlNotNull'] = None
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
            self.assertTrue("NOT NULL constraint failed: constraintsTable.sqlNotNull" in str(context.exception))

    def testSqlDefaultNull(self):
        rowDict = self._createRowDict()
        rowDict['sqlDefaultNull'] = None
        self._writeRowDict(rowDict)

    def testSqlNull(self):
        rowDict = self._createRowDict()
        rowDict['sqlNull'] = None
        self._writeRowDict(rowDict)

    def testSqlCheckConstant(self):
        rowDict = self._createRowDict()
        rowDict['sqlCheckConstant'] = self.api.ConstraintsConstant
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def testSqlCheckImportedConstant(self):
        rowDict = self._createRowDict()
        rowDict['sqlCheckImportedConstant'] = self.api.constraints.ImportedConstant
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def testSqlCheckEnum(self):
        rowDict = self._createRowDict()
        rowDict['sqlCheckEnum'] = self.api.ConstraintsEnum.VALUE2
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def testSqlCheckImportedEnum(self):
        rowDict = self._createRowDict()
        rowDict['sqlCheckImportedEnum'] = self.api.constraints.ImportedEnum.TWO
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def testSqlCheckBitmask(self):
        rowDict = self._createRowDict()
        rowDict['sqlCheckBitmask'] = self.api.ConstraintsBitmask.Values.MASK2
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def testSqlCheckImportedBitmask(self):
        rowDict = self._createRowDict()
        rowDict['sqlCheckImportedBitmask'] = self.api.constraints.ImportedBitmask.Values.MASK2
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def testSqlCheckUnicodeEscape(self):
        rowDict = self._createRowDict()
        rowDict['sqlCheckUnicodeEscape'] = self.WRONG_UNICODE_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def testSqlCheckHexEscape(self):
        rowDict = self._createRowDict()
        rowDict['sqlCheckHexEscape'] = self.WRONG_HEX_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def testSqlCheckOctalEscape(self):
        rowDict = self._createRowDict()
        rowDict['sqlCheckOctalEscape'] = self.WRONG_OCTAL_ESCAPE_CONST
        with self.assertRaises(apsw.ConstraintError) as context:
            self._writeRowDict(rowDict)
            self.assertTrue("CHECK constraint failed: constraintsTable" in str(context.exception))

    def _createRowDict(self):
        return {
            'primaryKey' : 1,
            'withoutSql' : 1,
            'sqlNotNull' : 1,
            'sqlDefaultNull' : 1,
            'sqlNull' : 1,
            'sqlCheckConstant' : 1,
            'sqlCheckImportedConstant' : 1,
            'sqlCheckEnum' : self.api.ConstraintsEnum.VALUE1,
            'sqlCheckImportedEnum' : self.api.constraints.ImportedEnum.ONE,
            'sqlCheckBitmask' : self.api.ConstraintsBitmask.Values.MASK1,
            'sqlCheckImportedBitmask' : self.api.constraints.ImportedBitmask.Values.MASK1,
            'sqlCheckUnicodeEscape' : self.UNICODE_ESCAPE_CONST,
            'sqlCheckHexEscape' : self.HEX_ESCAPE_CONST,
            'sqlCheckOctalEscape' : self.OCTAL_ESCAPE_CONST
        }

    def _writeRowDict(self, rowDict):
        row = [(
            rowDict['primaryKey'],
            rowDict['withoutSql'],
            rowDict['sqlNotNull'],
            rowDict['sqlDefaultNull'],
            rowDict['sqlNull'],
            rowDict['sqlCheckConstant'],
            rowDict['sqlCheckImportedConstant'],
            rowDict['sqlCheckEnum'],
            rowDict['sqlCheckImportedEnum'],
            rowDict['sqlCheckBitmask'],
            rowDict['sqlCheckImportedBitmask'],
            rowDict['sqlCheckUnicodeEscape'],
            rowDict['sqlCheckHexEscape'],
            rowDict['sqlCheckOctalEscape']
        )]
        self._constraintsTable.write(row)

    UNICODE_ESCAPE_CONST = 1
    HEX_ESCAPE_CONST = 2
    OCTAL_ESCAPE_CONST = 3

    WRONG_UNICODE_ESCAPE_CONST = 0
    WRONG_HEX_ESCAPE_CONST = 0
    WRONG_OCTAL_ESCAPE_CONST = 0
