import unittest
import os

from testutils import getZserioApi, getApiDir

class InstantiateTypeAsSqlDatabaseFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_type_as_sql_database_field
        cls._fileName = os.path.join(getApiDir(os.path.dirname(__file__)),
                                     "instantiate_type_as_sql_database_field.sqlite")

    def setUp(self):
        if os.path.exists(self._fileName):
            os.remove(self._fileName)

    def testReadWrite(self):
        instantiateTypeAsSqlDatabaseFieldDb = self.api.InstantiateTypeAsSqlDatabaseFieldDb.fromFile(
            self._fileName)
        instantiateTypeAsSqlDatabaseFieldDb.createSchema()

        stringTable = instantiateTypeAsSqlDatabaseFieldDb.getStringTable()
        stringTableRows = [(0, "test")]
        stringTable.write(stringTableRows)

        otherStringTable = instantiateTypeAsSqlDatabaseFieldDb.getOtherStringTable()
        otherStringTableRows = [(13, "other test")]
        otherStringTable.write(otherStringTableRows)

        instantiateTypeAsSqlDatabaseFieldDb.close()

        readInstantiateTypeAsSqlDatabaseFieldDb = self.api.InstantiateTypeAsSqlDatabaseFieldDb.fromFile(
            self._fileName)
        readStringTableIterator = readInstantiateTypeAsSqlDatabaseFieldDb.getStringTable().read()
        readStringTableRows = []
        for row in readStringTableIterator:
            readStringTableRows.append(row)
        readOtherStringTableIterator = readInstantiateTypeAsSqlDatabaseFieldDb.getOtherStringTable().read()
        readOtherStringTableRows = []
        for row in readOtherStringTableIterator:
            readOtherStringTableRows.append(row)

        self.assertEqual(stringTableRows, readStringTableRows)
        self.assertEqual(otherStringTableRows, readOtherStringTableRows)
