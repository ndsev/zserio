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
        instantiateTypeAsSqlDatabaseFieldDb = self.api.InstantiateTypeAsSqlDatabaseFieldDb.from_file(
            self._fileName)
        instantiateTypeAsSqlDatabaseFieldDb.create_schema()

        stringTable = instantiateTypeAsSqlDatabaseFieldDb.string_table
        stringTableRows = [(0, "test")]
        stringTable.write(stringTableRows)

        otherStringTable = instantiateTypeAsSqlDatabaseFieldDb.other_string_table
        otherStringTableRows = [(13, "other test")]
        otherStringTable.write(otherStringTableRows)

        instantiateTypeAsSqlDatabaseFieldDb.close()

        readInstantiateTypeAsSqlDatabaseFieldDb = self.api.InstantiateTypeAsSqlDatabaseFieldDb.from_file(
            self._fileName)
        readStringTableIterator = readInstantiateTypeAsSqlDatabaseFieldDb.string_table.read()
        readStringTableRows = []
        for row in readStringTableIterator:
            readStringTableRows.append(row)
        readOtherStringTableIterator = readInstantiateTypeAsSqlDatabaseFieldDb.other_string_table.read()
        readOtherStringTableRows = []
        for row in readOtherStringTableIterator:
            readOtherStringTableRows.append(row)

        self.assertEqual(stringTableRows, readStringTableRows)
        self.assertEqual(otherStringTableRows, readOtherStringTableRows)
