<#include "FileHeader.inc.ftl"/>
<#include "DocComment.inc.ftl">
<#if withTypeInfoCode>
    <#include "TypeInfo.inc.ftl"/>
</#if>
<@file_header generatorDescription/>
<@future_annotations/>
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
    <#if field.typeInfo.isEnum>
        <#assign hasEnumField = true/>
    </#if>
    <#if field.typeInfo.isBitmask>
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
<#if hasBlobField || withTypeInfoCode>
    <@package_imports ["zserio"]/>
</#if>
<#assign rowAnnotationName = "ROW_ANNOTATION"/>
<#assign rowsClassName = "Rows"/>

class ${name}:
<#if withCodeComments && docComments??>
<@doc_comments docComments, 1/>

</#if>
    class ${rowsClassName}:
<#if withCodeComments>
        """
        Class which describes one row in the table.
        """

</#if>
        def __init__(self, <#rt>
                <#lt>rows: typing.Iterator[<#if needsRowConversion>typing.Tuple<#else>'${name}.${rowAnnotationName}'</#if>]<#rt>
                <#lt><#if needsParameterProvider>, parameter_provider: '${name}.IParameterProvider'</#if>) -> None:
<#if withCodeComments>
            """
            Constructor from rows.

            :param rows: Table row to construct from.
    <#if needsParameterProvider>
            :param parameter_provider: Explicit parameter provider to be used.
    </#if>
            """

</#if>
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
                ${field.snakeCaseName}_ = ${field.typeInfo.typeFullName}.from_reader(reader<#rt>
            <#list field.parameters as parameter>
                <#if parameter.isExplicit>
                    , self._parameter_provider.<@parameter_provider_method_name parameter/>(row)<#t>
                <#else>
                    , ${parameter.expression}<#t>
                </#if>
            </#list>
                    <#lt>)
        <#elseif field.typeInfo.isEnum>
            if ${field.snakeCaseName}_ is not None:
                ${field.snakeCaseName}_ = ${field.typeInfo.typeFullName}(${field.snakeCaseName}_)
        <#elseif field.typeInfo.isBitmask>
            if ${field.snakeCaseName}_ is not None:
                ${field.snakeCaseName}_ = ${field.typeInfo.typeFullName}.from_value(${field.snakeCaseName}_)
        </#if>
    </#list>

            return (<#list fields as field>${field.snakeCaseName}_<#if field?index == 0 || field?has_next>, </#if></#list>)

</#if>
<#if needsParameterProvider>
    <#macro parameter_provider_method_name parameter>
        ${parameter.expression}<#t>
    </#macro>
    class IParameterProvider:
    <#if withCodeComments>
        """
        Interface for class which provides all explicit parameters of the table.
        """

    </#if>
    <#list explicitParameters as parameter>
        def <@parameter_provider_method_name parameter/>(self, row: typing.Tuple) -> ${parameter.typeInfo.typeFullName}:
        <#if withCodeComments>
            """
            Gets the value of the explicit parameter ${parameter.expression}.

            :param row: Current row of the table which can be used during parameter calculation.

            :returns: The value of the explicit parameter ${parameter.expression}.
            """

        </#if>
            raise NotImplementedError()
    </#list>

</#if>
    def __init__(self, connection: apsw.Connection, table_name: str, attached_db_name: str = None) -> None:
<#if withCodeComments>
        """
        Constructor from database connection, table name and attached database name.

        :param connection: Database connection where the table is located.
        :param table_name: Table name.
        :param attached_db_name Name of the attached database where table has been relocated.
        """

</#if>
        self._connection: apsw.Connection = connection
        self._table_name: str = table_name
        self._attached_db_name: str = attached_db_name
<#if withTypeInfoCode>

    @staticmethod
    def type_info() -> zserio.typeinfo.TypeInfo:
    <#if withCodeComments>
        """
        Gets static information about the table type useful for generic introspection.

        :returns: Zserio type information.
        """

    </#if>
        column_list: typing.List[zserio.typeinfo.MemberInfo] = [
    <#list fields as field>
            <@member_info_table_field field field?has_next/>
    </#list>
        ]
        attribute_list = {
            zserio.typeinfo.TypeAttribute.COLUMNS : column_list<#rt>
    <#if sqlConstraint??>
            <#lt>,
            zserio.typeinfo.TypeAttribute.SQL_CONSTRAINT : ${sqlConstraint}<#rt>
    </#if>
    <#if virtualTableUsing??>
            <#lt>,
            zserio.typeinfo.TypeAttribute.VIRTUAL_TABLE_USING : '${virtualTableUsing}'<#rt>
    </#if>
    <#if isWithoutRowId>
            <#lt>,
            zserio.typeinfo.TypeAttribute.WITHOUT_ROWID : None<#rt>
    </#if>
    <#if templateInstantiation??>
            <#lt>,
            <@type_info_template_instantiation_attributes templateInstantiation/>
    </#if>

        }

        return zserio.typeinfo.TypeInfo('${schemaTypeFullName}', ${name}, attributes=attribute_list)
</#if>
<#if withWriterCode>

    def create_table(self) -> None:
    <#if withCodeComments>
        """
        Creates the table using database connection given by constructor.
        """

    </#if>
        sql_query = self._get_create_table_query()
    <#if hasNonVirtualField && isWithoutRowId>
        sql_query += " WITHOUT ROWID"
    </#if>
        cursor = self._connection.cursor()
        cursor.execute(sql_query)
    <#if hasNonVirtualField && isWithoutRowId>

    def create_ordinary_rowid_table(self) -> None:
        <#if withCodeComments>
        """
        Creates the table as ordinary row id using database connection given by constructor.

        The method creates the table as ordinary row id even if it is specified as without row id in schema.
        """

        </#if>
        sql_query = self._get_create_table_query()
        cursor = self._connection.cursor()
        cursor.execute(sql_query)
    </#if>

    def delete_table(self) -> None:
    <#if withCodeComments>
        """
        Deletes the table using database connection given by constructor.
        """

    </#if>
        sql_query = "DROP TABLE "
        sql_query += self._get_table_name_in_query()
        cursor = self._connection.cursor()
        cursor.execute(sql_query)
</#if>

    def read(self, <#if needsParameterProvider>parameter_provider: '${name}.IParameterProvider', </#if>condition: str = None) -> <#rt>
            <#lt>'${name}.${rowsClassName}':
<#if withCodeComments>
        """
        Reads all rows from the table.

    <#if needsParameterProvider>
        :param parameter_provider: Explicit parameter provider to be used during reading.
    </#if>
        :param condition: SQL condition to use.

        :returns: Read rows.
        """

</#if>
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
    <#if withCodeComments>
        """
        Writes rows to the table.

        :param rows: Table rows to write.
        """

    </#if>
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

    def update(self, row: '${name}.${rowAnnotationName}', where_condition: str) -> None:
    <#if withCodeComments>
        """
        Updates row of the table.

        :param row: Table row to update.
        :param where_condition: SQL where condition to use.
        """

    </#if>
        sql_query = "UPDATE "
        sql_query += self._get_table_name_in_query()
        sql_query += (" SET"
    <#list fields as field>
                     " ${field.name}=?<#if field?has_next>,</#if>"
    </#list>
                     " WHERE ") + where_condition

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
        <#--
        type annotation is needed to denote that row_in_list is a list of Any values, which is not clear
        to mypy in case when all the columns are of the same type (either enum or bitmask)
        -->
        row_in_list : typing.List = list(row)

        <#list fields as field>
            <#if field.sqlTypeData.isBlob>
        ${field.snakeCaseName}_ = row_in_list[${field?index}]
        if isinstance(${field.snakeCaseName}_, ${field.typeInfo.typeFullName}):
            writer = zserio.BitStreamWriter()
            ${field.snakeCaseName}_.write(writer)
            row_in_list[${field?index}] = writer.byte_array

            <#elseif field.typeInfo.isEnum || field.typeInfo.isBitmask>
        ${field.snakeCaseName}_ = row_in_list[${field?index}]
        if isinstance(${field.snakeCaseName}_, ${field.typeInfo.typeFullName}):
            row_in_list[${field?index}] = ${field.snakeCaseName}_.value

            </#if>
        </#list>
        return row_in_list
    </#if>
</#if>

    <#-- keep the annotation at the end to prevent mypy errors when SQL table is named ROW_ANNOTATION -->
    ${rowAnnotationName} = typing.Tuple[
    <#list fields as field>
        ${field.typeInfo.typeFullName}<#if field?has_next>,<#else>]</#if>
    </#list>
