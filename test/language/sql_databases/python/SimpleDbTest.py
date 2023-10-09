import os
import apsw

import SqlDatabases

class SimpleDbTest(SqlDatabases.TestCaseWithDb):
    def setUp(self):
        if os.path.exists(self.dbFileName):
            os.remove(self.dbFileName)
        self._tableNames = (self.EUROPE_TABLE_NAME, self.AMERICA_TABLE_NAME)

    def testConnectionConstructor(self):
        connection = apsw.Connection(self.dbFileName, apsw.SQLITE_OPEN_READWRITE | apsw.SQLITE_OPEN_CREATE)

        database = self.api.WorldDb(connection)
        database.create_schema()
        for tableName in self._tableNames:
            self.assertTrue(self._isTableInDb(database, tableName))
        database.close()
        connection.close()

    def testConnectionConstructorTableRelocationMap(self):
        connection = apsw.Connection(self.dbFileName, apsw.SQLITE_OPEN_READWRITE | apsw.SQLITE_OPEN_CREATE)

        database = self.api.WorldDb(connection, [])
        database.create_schema()
        for tableName in self._tableNames:
            self.assertTrue(self._isTableInDb(database, tableName))
        database.close()
        connection.close()

    def testFromFile(self):
        database = self.api.WorldDb.from_file(self.dbFileName)
        database.create_schema()
        for tableName in self._tableNames:
            self.assertTrue(self._isTableInDb(database, tableName))
        database.close()

    def testFromFileTableRelocationMap(self):
        database = self.api.WorldDb.from_file(self.dbFileName, [])
        database.create_schema()
        for tableName in self._tableNames:
            self.assertTrue(self._isTableInDb(database, tableName))
        database.close()

    def testClose(self):
        database = self.api.WorldDb.from_file(self.dbFileName)
        database.close()
        self.assertEqual(None, database.connection)

    def testTableGetters(self):
        database = self.api.WorldDb.from_file(self.dbFileName)
        database.create_schema()

        self.assertTrue(self._isTableInDb(database, self.EUROPE_TABLE_NAME))
        europeTable = database.europe
        europeTable.delete_table()
        self.assertFalse(self._isTableInDb(database, self.EUROPE_TABLE_NAME))

        self.assertTrue(self._isTableInDb(database, self.AMERICA_TABLE_NAME))
        americaTable = database.america
        americaTable.delete_table()
        self.assertFalse(self._isTableInDb(database, self.AMERICA_TABLE_NAME))

        database.close()

    def testConnection(self):
        database = self.api.WorldDb.from_file(self.dbFileName)
        database.create_schema()

        connection = database.connection
        cursor = connection.cursor()
        for tableName in self._tableNames:
            sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'"
            for row in cursor.execute(sqlQuery):
                self.assertEqual(tableName, row[0])

        database.close()

    def testExecuteQuery(self):
        database = self.api.WorldDb.from_file(self.dbFileName)
        database.create_schema()

        for tableName in self._tableNames:
            sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'"
            for row in database.connection.cursor().execute(sqlQuery):
                self.assertEqual(tableName, row[0])

        database.close()

    def testCreateSchema(self):
        database = self.api.WorldDb.from_file(self.dbFileName)
        for tableName in self._tableNames:
            self.assertFalse(self._isTableInDb(database, tableName))

        database.create_schema()
        for tableName in self._tableNames:
            self.assertTrue(self._isTableInDb(database, tableName))

        database.close()

    def testDeleteSchema(self):
        database = self.api.WorldDb.from_file(self.dbFileName)
        database.create_schema()
        for tableName in self._tableNames:
            self.assertTrue(self._isTableInDb(database, tableName))

        database.delete_schema()
        for tableName in self._tableNames:
            self.assertFalse(self._isTableInDb(database, tableName))

        database.close()

    def testGetDatabaseName(self):
        self.assertEqual(self.WORLD_DB_NAME, self.api.WorldDb.DATABASE_NAME)

    def testGetTableNames(self):
        self.assertEqual(self.EUROPE_TABLE_NAME, self.api.WorldDb.TABLE_NAME_EUROPE)
        self.assertEqual(self.AMERICA_TABLE_NAME, self.api.WorldDb.TABLE_NAME_AMERICA)

    @staticmethod
    def _isTableInDb(database, tableName):
        # check if database does contain table
        sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'"
        for row in database.connection.cursor().execute(sqlQuery):
            if len(row) == 1 and row[0] == tableName:
                return True

        return False

    WORLD_DB_NAME = "WorldDb"
    EUROPE_TABLE_NAME = "europe"
    AMERICA_TABLE_NAME = "america"
