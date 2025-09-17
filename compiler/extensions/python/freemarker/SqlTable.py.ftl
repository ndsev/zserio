<#include "FileHeader.inc.ftl"/>
<#include "DocComment.inc.ftl">
<#if withTypeInfoCode>
    <#include "TypeInfo.inc.ftl"/>
</#if>
<@file_header generatorDescription/>
<@future_annotations/>
from enum import Enum, IntEnum
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

        **Columns**
    <#list fields as field>

        .. _${fullName}.${rowsClassName}.${field.name}:

        ${field.name}
        <#if field.docComments??>
        <@doc_comments_inner field.docComments, 3/>
        </#if>
    </#list>
        """

</#if>
        def __init__(self, <#rt>
                <#lt>rows: typing.Iterator[<#if needsRowConversion>typing.Tuple<#else>'${name}.${rowAnnotationName}'</#if>]<#rt>
                <#lt><#if needsParameterProvider>, parameter_provider: '${name}.IParameterProvider'</#if>,
                column_map: typing.List[bool]
                ) -> None:
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
            self._column_map = column_map

        def __iter__(self) -> typing.Iterator['${name}.${rowAnnotationName}']:
            for row in self._rows:
                yield <#if needsRowConversion>self._read_row(row)<#else>row</#if>

<#if needsRowConversion>
        def _read_row(self, row: typing.Tuple) -> '${name}.${rowAnnotationName}':
            i = 0
    <#list fields as field>
            if self._column_map[${field?index}]:
                ${field.snakeCaseName}_ = row[i]
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
                i += 1
            else:
                ${field.snakeCaseName}_ = None

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
        :param attached_db_name: Name of the attached database where table has been relocated.
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
        return self.read_columns(<#if needsParameterProvider>parameter_provider, </#if>None, condition)

    def read_columns(self, <#if needsParameterProvider>parameter_provider: '${name}.IParameterProvider', </#if>columns: typing.List[str], condition: str = None) -> <#rt>
            <#lt>'${name}.${rowsClassName}':
<#if withCodeComments>
        """
        Reads all rows from the table.

    <#if needsParameterProvider>
        :param parameter_provider: Explicit parameter provider to be used during reading.
    </#if>
        :param columns: column names to retrieve
        :param condition: SQL condition to use.

        :returns: Read rows.
        """

</#if>
        column_map = self._create_column_mapping(columns)
        sql_query = "SELECT "
        sql_query += self._get_columns_in_query(column_map, self._ColumnFormat.NAME)
        sql_query += " FROM "
        sql_query += self._get_table_name_in_query()
        if condition:
            sql_query += " WHERE " + condition

        cursor = self._connection.cursor()
        read_rows = cursor.execute(sql_query)

        return ${name}.${rowsClassName}(read_rows<#if needsParameterProvider>, parameter_provider</#if>, column_map)
<#if withWriterCode>

    def write(self, rows: typing.Sequence['${name}.${rowAnnotationName}'], columns: typing.List[str] = None) -> None:
    <#if withCodeComments>
        """
        Writes rows to the table.

        :param rows: Table rows to write.
        :param columns: Table columns to write.
        """

    </#if>
        column_map = self._create_column_mapping(columns)
        sql_query = "INSERT INTO "
        sql_query += self._get_table_name_in_query()
        sql_query += "("
        sql_query += self._get_columns_in_query(column_map, self._ColumnFormat.NAME)
        sql_query += ") VALUES ("
        sql_query += self._get_columns_in_query(column_map, self._ColumnFormat.SQL_PARAMETER)
        sql_query += ")"

        cursor = self._connection.cursor()
        has_autocommit = self._connection.getautocommit()
        if has_autocommit:
            cursor.execute("BEGIN")

        for row in rows:
            cursor.execute(sql_query, <#if needsRowConversion>self._write_row(row, column_map)<#else>row</#if>)

        if has_autocommit:
            cursor.execute("COMMIT")

    def update(self, row: '${name}.${rowAnnotationName}', where_condition: str) -> None:
        """
        Updates rows of the table.

        :param row: Updated values
        :param where_condition: SQL where condition to use.
        """
        return self.update_columns(row, None, where_condition)

    def update_columns(self, row: '${name}.${rowAnnotationName}', columns: typing.List[str], where_condition: str) -> None:
    <#if withCodeComments>
        """
        Updates rows of the table.

        :param row: Updated values
        :param columns: List of column names to update
        :param where_condition: SQL where condition to use.
        """

    </#if>
        column_map = self._create_column_mapping(columns)
        sql_query = "UPDATE "
        sql_query += self._get_table_name_in_query()
        sql_query += " SET "
        sql_query += self._get_columns_in_query(column_map, self._ColumnFormat.SQL_UPDATE)
        sql_query += " WHERE "
        sql_query += where_condition

        cursor = self._connection.cursor()
        cursor.execute(sql_query, <#if needsRowConversion>self._write_row(row, column_map)<#else>row</#if>)
</#if>

    def _create_column_mapping(self, columns: typing.List[str]) -> typing.List[bool]:
        if columns is None:
            column_map = [True] * ${fields?size}
        else:
            try:
                column_map = [False] * ${fields?size}
                for col in columns:
                    idx = self.COLUMN_NAMES.index(col)
                    column_map[idx] = True
            except ValueError as exc:
                raise ValueError("Column name '" + col + "' doesn't exist in '${name}'!") from exc

        return column_map

    def _get_table_name_in_query(self) -> str:
        return (self._attached_db_name + "." + self._table_name) if self._attached_db_name else self._table_name

    def _get_columns_in_query(self, column_map: typing.List[bool], fmt: _ColumnFormat) -> str:
        columns = []

    <#list fields as field>
        if column_map[${field?index}]:
            columns.append("${field.name}" if fmt == self._ColumnFormat.NAME else "${field.name}=?" if fmt == self._ColumnFormat.SQL_UPDATE else "?")
    </#list>

        return ",".join(columns)

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
    def _write_row(row: '${name}.${rowAnnotationName}', column_map: typing.List[bool]) -> typing.List:
        <#--
        type annotation is needed to denote that row_in_list is a list of Any values, which is not clear
        to mypy in case when all the columns are of the same type (either enum or bitmask)
        -->
        row_in_list : typing.List = []

        <#list fields as field>
        if column_map[${field?index}]:
            <#if field.sqlTypeData.isBlob>
            ${field.snakeCaseName}_ = row[${field?index}]
            if isinstance(${field.snakeCaseName}_, ${field.typeInfo.typeFullName}):
                writer = zserio.BitStreamWriter()
                ${field.snakeCaseName}_.write(writer)
                row_in_list.append(writer.byte_array)
            else:
                row_in_list.append(${field.snakeCaseName}_)
            <#elseif field.typeInfo.isEnum || field.typeInfo.isBitmask>
            ${field.snakeCaseName}_ = row[${field?index}]
            if isinstance(${field.snakeCaseName}_, ${field.typeInfo.typeFullName}):
                row_in_list.append(${field.snakeCaseName}_.value)
            else:
                row_in_list.append(${field.snakeCaseName}_)
            <#else>
            row_in_list.append(row[${field?index}])
            </#if>

        </#list>
        return row_in_list
    </#if>
</#if>

    class _ColumnFormat(Enum):
        NAME = 1
        SQL_PARAMETER = 2
        SQL_UPDATE = 3

    COLUMN_NAMES = (
    <#list fields as field>
        "${field.name}"<#if field?has_next>,<#else>)</#if>
    </#list>

    class Column(IntEnum):
    <#list fields as field>
        ${field.upperCaseName} = ${field?index}
    </#list>

    <#-- keep the annotation at the end to prevent mypy errors when SQL table is named ROW_ANNOTATION -->
    ${rowAnnotationName} = typing.Tuple[
    <#list fields as field>
        ${field.typeInfo.typeFullName}<#if field?has_next>,<#else>]</#if>
    </#list>
