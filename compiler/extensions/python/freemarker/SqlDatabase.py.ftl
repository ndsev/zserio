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
    def __init__(self, connection, tableToDbFileNameRelocationDict=None):
        self._isExternal = True
        self._connection = connection
        self._cursor = connection.cursor()
        self._initTables(tableToDbFileNameRelocationDict)

    @classmethod
    def fromFile(cls, fileName, tableToDbFileNameRelocationDict=None):
        connection = apsw.Connection(fileName, <#rt>
            <#lt><#if withWriterCode>apsw.SQLITE_OPEN_READWRITE | apsw.SQLITE_OPEN_CREATE<#else>apsw.SQLITE_OPEN_READONLY</#if>)
        instance = cls(connection, tableToDbFileNameRelocationDict)
        instance._isExternal = False

        return instance

    def close(self):
        self._detachDatabases()
        if not self._isExternal:
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
        return self._cursor.execute(query)
<#if withWriterCode>

    def createSchema(self<#if hasWithoutRowIdTable>, withoutRowIdTableNamesBlackList=None</#if>):
    <#if fields?has_content>
        hasAutoCommit = self._connection.getautocommit()
        if hasAutoCommit:
            self._cursor.execute("BEGIN")

        <#list fields as field>
            <#if field.isWithoutRowIdTable>
        if self.<@field_table_name field/> in withoutRowIdTableNamesBlackList:
            self.<@field_member_name field/>.createOrdinaryRowIdTable()
        else
            self.<@field_member_name field/>.createTable()
            <#else>
        self.<@field_member_name field/>.createTable()
            </#if>
        </#list>

        if hasAutoCommit:
            self._cursor.execute("COMMIT")
    </#if>

    def deleteSchema(self):
    <#if fields?has_content>
        hasAutoCommit = self._connection.getautocommit()
        if hasAutoCommit:
            self._cursor.execute("BEGIN")

        <#list fields as field>
        self.<@field_member_name field/>.deleteTable()
        </#list>

        if hasAutoCommit:
            self._cursor.execute("COMMIT")
    </#if>
</#if>

    def _initTables(self, tableToDbFileNameRelocationDict):
        self._dbFileNameToAttachedDbNameDict = {}
        tableNameToAttachedDbNameDict = {}
        if tableToDbFileNameRelocationDict:
            for relocatedTableName, dbFileName in tableToDbFileNameRelocationDict.items():
                attachedDbName = self._dbFileNameToAttachedDbNameDict.get(dbFileName)
                if attachedDbName is None:
                    attachedDbName = self.DATABASE_NAME + "_" + relocatedTableName
                    self._attachDatabase(dbFileName, attachedDbName)
                    self._dbFileNameToAttachedDbNameDict[dbFileName] = attachedDbName

                tableNameToAttachedDbNameDict[relocatedTableName] = attachedDbName

<#list fields as field>
        self.<@field_member_name field/> = ${field.pythonTypeName}(
            self._cursor, self.<@field_table_name field/>, tableNameToAttachedDbNameDict.get(self.<@field_table_name field/>))
</#list>

    def _attachDatabase(self, dbFileName, dbName):
        sqlQuery = "ATTACH DATABASE '"
        sqlQuery += "file:" + dbFileName
        sqlQuery += "' AS "
        sqlQuery += dbName
        self._cursor.execute(sqlQuery)

    def _detachDatabases(self):
        for attachedDbName in self._dbFileNameToAttachedDbNameDict.values():
            sqlQuery = "DETACH DATABASE " + attachedDbName
            self._cursor.execute(sqlQuery)

    DATABASE_NAME = "${name}"
<#list fields as field>
    <@field_table_name field/> = "${field.name}"
</#list>
