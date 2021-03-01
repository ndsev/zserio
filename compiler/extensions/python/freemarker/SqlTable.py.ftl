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
                <#lt><#if needsParameterProvider>, parameter_provider: '${name}.IParameterProvider'</#if>) -> None:
            self._rows = rows
<#if needsParameterProvider>
            self._parameter_provider = parameter_provider
</#if>

        def __iter__(self) -> typing.Iterator['${name}.${rowAnnotationName}']:
            for row in self._rows:
                yield <#if needsRowConversion>self._read_row(row)<#else>row</#if>

<#if needsRowConversion>
    <#if !needsParameterProvider>
        @staticmethod
    </#if>
        def _read_row(<#if needsParameterProvider>self, </#if>row: typing.Tuple) -> '${name}.${rowAnnotationName}':
    <#list fields as field>
            ${field.snakeCaseName}_ = row[${field?index}]
        <#if field.sqlTypeData.isBlob>
            if ${field.snakeCaseName}_ is not None:
                reader = zserio.BitStreamReader(${field.snakeCaseName}_)
                ${field.snakeCaseName}_ = ${field.pythonTypeName}.from_reader(reader<#rt>
            <#list field.parameters as parameter>
                <#if parameter.isExplicit>
                    , self._parameter_provider.<@parameter_provider_method_name parameter/>(row)<#t>
                <#else>
                    , ${parameter.expression}<#t>
                </#if>
            </#list>
                    <#lt>)
        <#elseif field.enumData??>
            if ${field.snakeCaseName}_ is not None:
                ${field.snakeCaseName}_ = ${field.enumData.pythonTypeName}(${field.snakeCaseName}_)
        <#elseif field.bitmaskData??>
            if ${field.snakeCaseName}_ is not None:
                ${field.snakeCaseName}_ = ${field.bitmaskData.pythonTypeName}.from_value(${field.snakeCaseName}_)
        </#if>
    </#list>

            return (<#list fields as field>${field.snakeCaseName}_<#if field?index == 0 || field?has_next>, </#if></#list>)

</#if>
<#if needsParameterProvider>
    <#macro parameter_provider_method_name parameter>
        ${parameter.expression}<#t>
    </#macro>
    class IParameterProvider:
    <#list explicitParameters as parameter>
        def <@parameter_provider_method_name parameter/>(self, row: typing.Tuple) -> ${parameter.pythonTypeName}:
            raise NotImplementedError()
    </#list>

</#if>
    def __init__(self, connection: apsw.Connection, table_name: str, attached_db_name: str = None) -> None:
        self._connection: apsw.Connection = connection
        self._table_name: str = table_name
        self._attached_db_name: str = attached_db_name
<#if withWriterCode>

    def create_table(self) -> None:
        sql_query = self._get_create_table_query()
    <#if hasNonVirtualField && isWithoutRowId>
        sql_query += " WITHOUT ROWID"
    </#if>
        cursor = self._connection.cursor()
        cursor.execute(sql_query)
    <#if hasNonVirtualField && isWithoutRowId>

    def create_ordinary_rowid_table(self) -> None:
        sql_query = self._get_create_table_query()
        cursor = self._connection.cursor()
        cursor.execute(sql_query)
    </#if>

    def delete_table(self) -> None:
        sql_query = "DROP TABLE "
        sql_query += self._get_table_name_in_query()
        cursor = self._connection.cursor()
        cursor.execute(sql_query)
</#if>

    def read(self, <#if needsParameterProvider>parameter_provider: '${name}.IParameterProvider', </#if>condition: str = None) -> <#rt>
            <#lt>'${name}.${rowsClassName}':
        sql_query = ("SELECT "
<#list fields as field>
                    "${field.name}<#if field?has_next>, </#if>"
</#list>
                    " FROM ")
        sql_query += self._get_table_name_in_query()
        if condition:
            sql_query += " WHERE " + condition

        cursor = self._connection.cursor()
        read_rows = cursor.execute(sql_query)

        return ${name}.${rowsClassName}(read_rows<#if needsParameterProvider>, parameter_provider</#if>)
<#if withWriterCode>

    def write(self, rows: typing.Sequence['${name}.${rowAnnotationName}']) -> None:
        sql_query = "INSERT INTO "
        sql_query += self._get_table_name_in_query()
        sql_query += ("("
    <#list fields as field>
                     "${field.name}<#if field?has_next>, </#if>"
    </#list>
                     ") VALUES (<#list fields as field>?<#if field?has_next>, </#if></#list>)")

        cursor = self._connection.cursor()
        has_autocommit = self._connection.getautocommit()
        if has_autocommit:
            cursor.execute("BEGIN")

        for row in rows:
            cursor.execute(sql_query, <#if needsRowConversion>self._write_row(row)<#else>row</#if>)

        if has_autocommit:
            cursor.execute("COMMIT")

    def update(self, row: '${name}.${rowAnnotationName}', whereCondition: str) -> None:
        sql_query = "UPDATE "
        sql_query += self._get_table_name_in_query()
        sql_query += (" SET"
    <#list fields as field>
                     " ${field.name}=?<#if field?has_next>,</#if>"
    </#list>
                     " WHERE ") + whereCondition

        cursor = self._connection.cursor()
        cursor.execute(sql_query, <#if needsRowConversion>self._write_row(row)<#else>row</#if>)
</#if>

    def _get_table_name_in_query(self) -> str:
        return (self._attached_db_name + "." + self._table_name) if self._attached_db_name else self._table_name
<#if withWriterCode>

    def _get_create_table_query(self) -> str:
        sql_query = "CREATE <#if virtualTableUsing??>VIRTUAL </#if>TABLE "
        sql_query += self._get_table_name_in_query()
    <#if virtualTableUsing??>
        sql_query += " USING ${virtualTableUsing}"
    </#if>
    <#if hasNonVirtualField || sqlConstraint??>
        sql_query += ("(" +
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

        return sql_query
    <#if needsRowConversion>

    @staticmethod
    def _write_row(row: '${name}.${rowAnnotationName}') -> typing.List:
        row_in_list = list(row)

        <#list fields as field>
            <#if field.sqlTypeData.isBlob>
        ${field.snakeCaseName}_ = row_in_list[${field?index}]
        if isinstance(${field.snakeCaseName}_, ${field.pythonTypeName}):
            writer = zserio.BitStreamWriter()
            ${field.snakeCaseName}_.write(writer)
            row_in_list[${field?index}] = writer.byte_array

            <#elseif field.enumData?? || field.bitmaskData??>
        ${field.snakeCaseName}_ = row_in_list[${field?index}]
        if isinstance(${field.snakeCaseName}_, ${field.pythonTypeName}):
            row_in_list[${field?index}] = ${field.snakeCaseName}_.value

            </#if>
        </#list>
        return row_in_list
    </#if>
</#if>
