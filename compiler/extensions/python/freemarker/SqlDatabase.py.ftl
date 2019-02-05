<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports typeImports/>
<#if withWriterCode>
    <#assign hasWithoutRowIdTable=false/>
    <#list fields as field>
        <#if field.isWithoutRowIdTable>
            <#assign hasWithoutRowIdTable=true/>
            <#break>
        </#if>
    </#list>
</#if>

class ${name}():
    def __init__(self, connection, tableToAttachedDbNameRelocationMap=None):
        self._connection = connection
        self._initTables(tableToAttachedDbNameRelocationMap if tableToAttachedDbNameRelocationMap else {})
        self._attachedDbNameList = []
        self._isExternal = True

    @classmethod
    def fromFile(cls, fileName, tableToDbFileNameRelocationMap=None):
        connection = apsw.Connection(fileName, apsw.SQLITE_OPEN_URI | <#rt>
            <#lt><#if withWriterCode>apsw.SQLITE_OPEN_READWRITE | apsw.SQLITE_OPEN_CREATE<#else>apsw.SQLITE_OPEN_READONLY</#if>)

        tableNameToAttachedDbNameMap = {}
        dbFileNameToAttachedDbNameMap = {}
        if tableToDbFileNameRelocationMap:
            cursor = connection.cursor()
            for relocatedTableName, dbFileName in tableToDbFileNameRelocationMap.items():
                attachedDbName = dbFileNameToAttachedDbNameMap.get(dbFileName)
                if attachedDbName is None:
                    attachedDbName = cls.DATABASE_NAME + "_" + relocatedTableName
                    cls._attachDatabase(cursor, dbFileName, attachedDbName)
                    dbFileNameToAttachedDbNameMap[dbFileName] = attachedDbName

                tableNameToAttachedDbNameMap[relocatedTableName] = attachedDbName

        instance = cls(connection, tableNameToAttachedDbNameMap)
        instance._attachedDbNameList = dbFileNameToAttachedDbNameMap.values()
        instance._isExternal = False

        return instance

    def close(self):
        if not self._isExternal:
            self._detachDatabases()
            self._connection.close()
        self._connection = None
<#list fields as field>

    <#macro field_member_name field>
        _${field.name}_<#t>
    </#macro>
    <#macro field_table_name field>
        ${field.name?upper_case}_TABLE_NAME<#t>
    </#macro>
    def ${field.getterName}(self):
        return self.<@field_member_name field/>
</#list>

    def connection(self):
        return self._connection

    def executeQuery(self, query):
        cursor = self._connection.cursor()
        return cursor.execute(query)
<#if withWriterCode>

    def createSchema(self<#if hasWithoutRowIdTable>, withoutRowIdTableNamesBlackList=None</#if>):
        hasAutoCommit = self._connection.getautocommit()
        if hasAutoCommit:
            cursor = self._connection.cursor()
            cursor.execute("BEGIN")
    <#if hasWithoutRowIdTable>

        if withoutRowIdTableNamesBlackList is None:
            withoutRowIdTableNamesBlackList = []
    </#if>

    <#list fields as field>
        <#if field.isWithoutRowIdTable>
        if self.<@field_table_name field/> in withoutRowIdTableNamesBlackList:
            self.<@field_member_name field/>.createOrdinaryRowIdTable()
        else:
            self.<@field_member_name field/>.createTable()
        <#else>
        self.<@field_member_name field/>.createTable()
        </#if>
    </#list>

        if hasAutoCommit:
            cursor.execute("COMMIT")

    def deleteSchema(self):
        hasAutoCommit = self._connection.getautocommit()
        if hasAutoCommit:
            cursor = self._connection.cursor()
            cursor.execute("BEGIN")

    <#list fields as field>
        self.<@field_member_name field/>.deleteTable()
    </#list>

        if hasAutoCommit:
            cursor.execute("COMMIT")
</#if>

    def _initTables(self, tableNameToAttachedDbNameMap):
<#list fields as field>
        self.<@field_member_name field/> = ${field.pythonTypeName}(
            self._connection, self.<@field_table_name field/>, tableNameToAttachedDbNameMap.get(self.<@field_table_name field/>))
</#list>

    @staticmethod
    def _attachDatabase(cursor, dbFileName, dbName):
        sqlQuery = "ATTACH DATABASE '"
        sqlQuery += dbFileName
        sqlQuery += "' AS "
        sqlQuery += dbName
        cursor.execute(sqlQuery)

    def _detachDatabases(self):
        for attachedDbName in self._attachedDbNameList:
            sqlQuery = "DETACH DATABASE " + attachedDbName
            cursor = self._connection.cursor()
            cursor.execute(sqlQuery)

    DATABASE_NAME = "${name}"
<#list fields as field>
    <@field_table_name field/> = "${field.name}"
</#list>
