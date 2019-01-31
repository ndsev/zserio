import unittest
import os

from testutils import getZserioApi, getApiDir

class SqlTypesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "sql_types.zs")
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)), "sql_types_test.sqlite")

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)
        self._database = self.api.SqlTypesDb.fromFile(self._fileName)
        self._database.createSchema()
        self._sqlColumnTypes = self._getSqlColumnTypes()

    def tearDown(self):
        self._database.close()

    def testUnsignedIntegerTypes(self):
        self.assertEqual("INTEGER", self._sqlColumnTypes["uint8Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["uint16Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["uint32Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["uint64Type"])

    def testSignedIntegerTypes(self):
        self.assertEqual("INTEGER", self._sqlColumnTypes["int8Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["int16Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["int32Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["int64Type"])

    def testUnsignedBitfieldTypes(self):
        self.assertEqual("INTEGER", self._sqlColumnTypes["bitfield8Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["variableBitfieldType"])

    def testSignedBitfieldTypes(self):
        self.assertEqual("INTEGER", self._sqlColumnTypes["intfield8Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["variableIntfieldType"])

    def testFloat16Type(self):
        self.assertEqual("REAL", self._sqlColumnTypes["float16Type"])

    def testFloat32Type(self):
        self.assertEqual("REAL", self._sqlColumnTypes["float32Type"])

    def testFloat64Type(self):
        self.assertEqual("REAL", self._sqlColumnTypes["float64Type"])

    def testVariableUnsignedIntegerTypes(self):
        self.assertEqual("INTEGER", self._sqlColumnTypes["varuint16Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["varuint32Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["varuint64Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["varuintType"])

    def testVariableSignedIntegerTypes(self):
        self.assertEqual("INTEGER", self._sqlColumnTypes["varint16Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["varint32Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["varint64Type"])
        self.assertEqual("INTEGER", self._sqlColumnTypes["varintType"])

    def testBoolType(self):
        self.assertEqual("INTEGER", self._sqlColumnTypes["boolType"])

    def testStringTypes(self):
        self.assertEqual("TEXT", self._sqlColumnTypes["stringType"])

    def testEnumType(self):
        self.assertEqual("INTEGER", self._sqlColumnTypes["enumType"])

    def testStructureType(self):
        self.assertEqual("BLOB", self._sqlColumnTypes["structureType"])

    def testChoiceType(self):
        self.assertEqual("BLOB", self._sqlColumnTypes["choiceType"])

    def testUnionType(self):
        self.assertEqual("BLOB", self._sqlColumnTypes["unionType"])

    def _getSqlColumnTypes(self):
        columnTypes = {}

        # prepare SQL query
        sqlQuery = "PRAGMA table_info(" + self.TABLE_NAME + ")"

        # get table info
        for row in self._database.executeQuery(sqlQuery):
            columnName = row[1]
            columnType = row[2]
            columnTypes[columnName] = columnType

        return columnTypes

    TABLE_NAME = "sqlTypesTable"
