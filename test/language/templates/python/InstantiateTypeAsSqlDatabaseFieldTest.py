import os

import Templates

class InstantiateTypeAsSqlDatabaseFieldTest(Templates.TestCaseWithDb):
    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)

    def testReadWrite(self):
        instantiateTypeAsSqlDatabaseFieldDb = self.api.InstantiateTypeAsSqlDatabaseFieldDb.from_file(
            self.dbFileName)
        instantiateTypeAsSqlDatabaseFieldDb.create_schema()

        stringTable = instantiateTypeAsSqlDatabaseFieldDb.string_table
        stringTableRows = [(0, "test")]
        stringTable.write(stringTableRows)

        otherStringTable = instantiateTypeAsSqlDatabaseFieldDb.other_string_table
        otherStringTableRows = [(13, "other test")]
        otherStringTable.write(otherStringTableRows)

        instantiateTypeAsSqlDatabaseFieldDb.close()

        readInstantiateTypeAsSqlDatabaseFieldDb = self.api.InstantiateTypeAsSqlDatabaseFieldDb.from_file(
            self.dbFileName)
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
