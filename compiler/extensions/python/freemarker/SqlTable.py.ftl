<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>
<#assign hasBlobField = false/>
<#list fields as field>
    <#if field.sqlTypeData.isBlob>
        <#assign hasBlobField = true/>
        <#break>
    </#if>
</#list>
<#assign hasEnumField = false/>
<#assign hasBitmaskField = false/>
<#list fields as field>
    <#if field.enumData??>
        <#assign hasEnumField = true/>
    </#if>
    <#if field.bitmaskData??>
        <#assign hasBitmaskField = true/>
    </#if>
</#list>
<#assign needsRowConversion = hasBlobField || hasEnumField || hasBitmaskField/>
<#assign needsParameterProvider = explicitParameters?has_content/>
<#if withWriterCode>
    <#assign hasNonVirtualField=false/>
    <#list fields as field>
        <#if !field.isVirtual>
            <#assign hasNonVirtualField=true/>
            <#break>
        </#if>
    </#list>
</#if>
<#if hasBlobField>
    <@package_imports ["zserio"]/>
</#if>

class ${name}:
<#assign rowAnnotationName = "RowAnnotation"/>
    ${rowAnnotationName} = typing.Tuple[
    <#list fields as field>
        ${field.pythonTypeName}<#if field?has_next>,<#else>]</#if>
    </#list>

<#assign rowsClassName = "Rows"/>
    class ${rowsClassName}:
        def __init__(self, <#rt>
                <#lt>rows: typing.Iterator[<#if needsRowConversion>typing.Tuple<#else>'${name}.${rowAnnotationName}'</#if>]<#rt>
                <#lt><#if needsParameterProvider>, parameterProvider: '${name}.IParameterProvider'</#if>) -> None:
            self._rows = rows
<#if needsParameterProvider>
            self._parameterProvider = parameterProvider
</#if>

        def __iter__(self) -> typing.Iterator['${name}.${rowAnnotationName}']:
            for row in self._rows:
                yield <#if needsRowConversion>self._readRow(row)<#else>row</#if>

<#if needsRowConversion>
    <#if !needsParameterProvider>
        @staticmethod
    </#if>
        def _readRow(<#if needsParameterProvider>self, </#if>row: typing.Tuple) -> '${name}.${rowAnnotationName}':
    <#list fields as field>
            ${field.name}_ = row[${field?index}]
        <#if field.sqlTypeData.isBlob>
            if ${field.name}_ is not None:
                reader = zserio.BitStreamReader(${field.name}_)
                ${field.name}_ = ${field.pythonTypeName}.fromReader(reader<#rt>
            <#list field.parameters as parameter>
                <#if parameter.isExplicit>
                    , self._parameterProvider.<@parameter_provider_method_name parameter/>(row)<#t>
                <#else>
                    , ${parameter.expression}<#t>
                </#if>
            </#list>
                    <#lt>)
        <#elseif field.enumData??>
            if ${field.name}_ is not None:
                ${field.name}_ = ${field.enumData.pythonTypeName}(${field.name}_)
        <#elseif field.bitmaskData??>
            if ${field.name}_ is not None:
                ${field.name}_ = ${field.bitmaskData.pythonTypeName}.fromValue(${field.name}_)
        </#if>
    </#list>

            return (<#list fields as field>${field.name}_<#if field?index == 0 || field?has_next>, </#if></#list>)

</#if>
<#if needsParameterProvider>
    <#macro parameter_provider_method_name parameter>
        get${parameter.expression?cap_first}<#t>
    </#macro>
    class IParameterProvider:
    <#list explicitParameters as parameter>
        def <@parameter_provider_method_name parameter/>(self, row: typing.Tuple) -> ${parameter.pythonTypeName}:
            raise NotImplementedError()
    </#list>

</#if>
    def __init__(self, connection: apsw.Connection, tableName: str, attachedDbName: str = None) -> None:
        self._connection: apsw.Connection = connection
        self._tableName: str = tableName
        self._attachedDbName: str = attachedDbName
<#if withWriterCode>

    def createTable(self) -> None:
        sqlQuery = self._getCreateTableQuery()
    <#if hasNonVirtualField && isWithoutRowId>
        sqlQuery += " WITHOUT ROWID"
    </#if>
        cursor = self._connection.cursor()
        cursor.execute(sqlQuery)
    <#if hasNonVirtualField && isWithoutRowId>

    def createOrdinaryRowIdTable(self) -> None:
        sqlQuery = self._getCreateTableQuery()
        cursor = self._connection.cursor()
        cursor.execute(sqlQuery)
    </#if>

    def deleteTable(self) -> None:
        sqlQuery = "DROP TABLE "
        sqlQuery += self._getTableNameInQuery()
        cursor = self._connection.cursor()
        cursor.execute(sqlQuery)
</#if>

    def read(self, <#if needsParameterProvider>parameterProvider: '${name}.IParameterProvider', </#if>condition: str = None) -> <#rt>
            <#lt>'${name}.${rowsClassName}':
        sqlQuery = ("SELECT "
<#list fields as field>
                    "${field.name}<#if field?has_next>, </#if>"
</#list>
                    " FROM ")
        sqlQuery += self._getTableNameInQuery()
        if condition:
            sqlQuery += " WHERE " + condition

        cursor = self._connection.cursor()
        readRows = cursor.execute(sqlQuery)

        return ${name}.${rowsClassName}(readRows<#if needsParameterProvider>, parameterProvider</#if>)
<#if withWriterCode>

    def write(self, rows: typing.Sequence['${name}.${rowAnnotationName}']) -> None:
        sqlQuery = "INSERT INTO "
        sqlQuery += self._getTableNameInQuery()
        sqlQuery += ("("
    <#list fields as field>
                     "${field.name}<#if field?has_next>, </#if>"
    </#list>
                     ") VALUES (<#list fields as field>?<#if field?has_next>, </#if></#list>)")

        cursor = self._connection.cursor()
        hasAutoCommit = self._connection.getautocommit()
        if hasAutoCommit:
            cursor.execute("BEGIN")

        for row in rows:
            cursor.execute(sqlQuery, <#if needsRowConversion>self._writeRow(row)<#else>row</#if>)

        if hasAutoCommit:
            cursor.execute("COMMIT")

    def update(self, row: '${name}.${rowAnnotationName}', whereCondition: str) -> None:
        sqlQuery = "UPDATE "
        sqlQuery += self._getTableNameInQuery()
        sqlQuery += (" SET"
    <#list fields as field>
                     " ${field.name}=?<#if field?has_next>,</#if>"
    </#list>
                     " WHERE ") + whereCondition

        cursor = self._connection.cursor()
        cursor.execute(sqlQuery, <#if needsRowConversion>self._writeRow(row)<#else>row</#if>)
</#if>

    def _getTableNameInQuery(self) -> str:
        return (self._attachedDbName + "." + self._tableName) if self._attachedDbName else self._tableName
<#if withWriterCode>

    def _getCreateTableQuery(self) -> str:
        sqlQuery = "CREATE <#if virtualTableUsing??>VIRTUAL </#if>TABLE "
        sqlQuery += self._getTableNameInQuery()
    <#if virtualTableUsing??>
        sqlQuery += " USING ${virtualTableUsing}"
    </#if>
    <#if hasNonVirtualField || sqlConstraint??>
        sqlQuery += ("(" +
        <#list fields as field>
            <#if !field.isVirtual>
                     "${field.name}<#if needsTypesInSchema> ${field.sqlTypeData.name}</#if>"<#rt>
                     <#lt><#if field.sqlConstraint??> + " " + ${field.sqlConstraint}</#if><#if field_has_next> + ","</#if> +
            </#if>
        </#list>
        <#if hasNonVirtualField && sqlConstraint??>
                     ", " +
        </#if>
        <#if sqlConstraint??>
                     ${sqlConstraint} +
        </#if>
                     ")")
    </#if>

        return sqlQuery
    <#if needsRowConversion>

    @staticmethod
    def _writeRow(row: '${name}.${rowAnnotationName}') -> typing.List:
        rowInList = list(row)

        <#list fields as field>
            <#if field.sqlTypeData.isBlob>
        ${field.name}_ = rowInList[${field?index}]
        if isinstance(${field.name}_, ${field.pythonTypeName}):
            writer = zserio.BitStreamWriter()
            ${field.name}_.write(writer)
            rowInList[${field?index}] = writer.getByteArray()

            <#elseif field.enumData??>
        ${field.name}_ = rowInList[${field?index}]
        if isinstance(${field.name}_, ${field.pythonTypeName}):
            rowInList[${field?index}] = ${field.name}_.value

            <#elseif field.bitmaskData??>
        ${field.name}_ = rowInList[${field?index}]
        if isinstance(${field.name}_, ${field.pythonTypeName}):
            rowInList[${field?index}] = ${field.name}_.getValue()

            </#if>
        </#list>
        return rowInList
    </#if>
</#if>
