<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#include "RangeCheck.inc.ftl">
<#include "TypeInfo.inc.ftl">
<#include "DocComment.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#assign hasBlobField=sql_table_has_blob_field(fields)/>
<#assign needsParameterProvider=explicitParameters?has_content/>
<#if withWriterCode>
    <#assign hasNonVirtualField=sql_table_has_non_virtual_field(fields)/>
</#if>
<#if withValidationCode>
    <#assign hasValidatableField=sql_table_has_validatable_field(fields)/>
</#if>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
public class ${name}
{
<#if needsParameterProvider>
    <#if withCodeComments>
    /** Interface for class which provides all explicit parameters of the table. */
    </#if>
    public static interface ParameterProvider
    {
    <#list explicitParameters as parameter>
        <#if withCodeComments>
            <#if !parameter?is_first>

            </#if>
        /**
         * Gets the value of the explicit parameter ${parameter.expression}.
         *
         * @param resultSet Current row of the table which can be used during parameter calculation.
         *
         * @return The value of the explicit parameter ${parameter.expression}.
         */
        </#if>
        ${parameter.typeInfo.typeFullName} <@sql_parameter_provider_getter_name parameter/>(java.sql.ResultSet resultSet);
    </#list>
    };

</#if>
<#if withCodeComments>
    /**
     * Constructor from database connection and table name.
     *
     * @param connection Database connection where the table is located.
     * @param tableName Table name.
     */
</#if>
    public ${name}(java.sql.Connection connection, java.lang.String tableName)
    {
        this.connection = connection;
        this.attachedDbName = null;
        this.tableName = tableName;
    }

<#if withCodeComments>
    /**
     * Constructor from database connection, table name and attached database name.
     *
     * @param connection Database connection where the table is located.
     * @param attachedDbName Name of the attached database where table has been relocated.
     * @param tableName Table name.
     */
</#if>
    public ${name}(java.sql.Connection connection, java.lang.String attachedDbName,
            java.lang.String tableName)
    {
        this.connection = connection;
        this.attachedDbName = attachedDbName;
        this.tableName = tableName;
    }
<#if withTypeInfoCode>

    <#if withCodeComments>
    /**
     * Gets static information about the table type useful for generic introspection.
     *
     * @return Zserio type information.
     */
    </#if>
    public static zserio.runtime.typeinfo.TypeInfo typeInfo()
    {
        final java.lang.String templateName = <@template_info_template_name templateInstantiation!/>;
        final java.util.List<zserio.runtime.typeinfo.TypeInfo> templateArguments =
                <@template_info_template_arguments templateInstantiation!/>
        final java.util.List<zserio.runtime.typeinfo.ColumnInfo> columnList =
                <@columns_info fields/>

        return new zserio.runtime.typeinfo.TypeInfo.SqlTableTypeInfo(
                "${schemaTypeName}", ${name}.class, templateName, templateArguments,
                columnList, ${sqlConstraint!"\"\""}, "${virtualTableUsing!""}", ${isWithoutRowId?c}
        );
    }
</#if>
<#if withWriterCode>

    <#if withCodeComments>
    /**
     * Creates the table using database connection given by constructor.
     *
     * @throws SQLException If table creation fails.
     */
    </#if>
    public void createTable() throws java.sql.SQLException
    {
        final java.lang.StringBuilder sqlQuery = getCreateTableQuery();
    <#if hasNonVirtualField && isWithoutRowId>
        sqlQuery.append(" WITHOUT ROWID");
    </#if>
        executeUpdate(sqlQuery.toString());
    }

    <#if hasNonVirtualField && isWithoutRowId>
        <#if withCodeComments>
    /**
     * Creates the table as ordinary row id using database connection given by constructor.
     *
     * The method creates the table as ordinary row id even if it is specified as without row id in schema.
     *
     * @throws SQLException If table creation fails.
     */
        </#if>
    public void createOrdinaryRowIdTable() throws java.sql.SQLException
    {
        final java.lang.StringBuilder sqlQuery = getCreateTableQuery();
        executeUpdate(sqlQuery.toString());
    }

    </#if>
    <#if withCodeComments>
    /**
     * Deletes the table using database connection given by constructor.
     *
     * @throws SQLException If table delete fails.
     */
    </#if>
    public void deleteTable() throws java.sql.SQLException
    {
        final java.lang.StringBuilder sqlQuery = new java.lang.StringBuilder("DROP TABLE ");
        appendTableNameToQuery(sqlQuery);
        executeUpdate(sqlQuery.toString());
    }
</#if>

<#if withCodeComments>
    /**
     * Reads all rows from the table.
     *
    <#if needsParameterProvider>
     * @param parameterProvider Explicit parameter provider to be used during reading.
     *
    </#if>
     * @throws SQLException In case of any failure during reading from the database.
     * @throws IOException In case of any failure during reading from the database file.
     */
</#if>
    public java.util.List<${rowName}> read(<#if needsParameterProvider>ParameterProvider parameterProvider</#if>)
            throws java.sql.SQLException, java.io.IOException
    {
        return read(<#if needsParameterProvider>parameterProvider, </#if>"");
    }

<#if withCodeComments>
    /**
     * Reads all rows from the table which fulfill the given condition.
     *
        <#if needsParameterProvider>
     * @param parameterProvider Explicit parameter provider to be used during reading.
        </#if>
     * @param condition SQL condition to use.
     *
     * @return Read rows.
     *
     * @throws SQLException In case of any failure during reading from the database.
     * @throws IOException In case of any failure during reading from the database file.
     */
</#if>
    public java.util.List<${rowName}> read(<#if needsParameterProvider>ParameterProvider parameterProvider,</#if>
            java.lang.String condition) throws java.sql.SQLException, java.io.IOException
    {
        // assemble sql query
        final java.lang.StringBuilder sqlQuery = new java.lang.StringBuilder("SELECT " +
<#list fields as field>
                "${field.name}<#if field_has_next>, </#if>" +
</#list>
                " FROM ");
        appendTableNameToQuery(sqlQuery);
        if (!condition.isEmpty())
        {
            sqlQuery.append(" WHERE ");
            sqlQuery.append(condition);
        }

        // read rows
        final java.util.List<${rowName}> rows = new java.util.ArrayList<${rowName}>();
        try (
            final java.sql.PreparedStatement statement = connection.prepareStatement(sqlQuery.toString());
            final java.sql.ResultSet resultSet = statement.executeQuery();
        )
        {
            while (resultSet.next())
            {
                final ${rowName} row = readRow(<#if needsParameterProvider>parameterProvider, </#if>resultSet);
                rows.add(row);
            }
        }

        return rows;
    }
<#if withWriterCode>

    <#if withCodeComments>
    /**
     * Writes rows to the table.
     *
     * Assumes that no other rows with the same primary keys exist, otherwise an exception is thrown.
     *
     * @param rows Table rows to write.
     *
     * @throws SQLException In case of any failure during writing to the database.
     * @throws IOException In case of any failure during writing to the database file.
     */
    </#if>
    public void write(java.util.List<${rowName}> rows)
            throws java.sql.SQLException, java.io.IOException
    {
        // assemble sql query
        final java.lang.StringBuilder sqlQuery = new java.lang.StringBuilder("INSERT INTO ");
        appendTableNameToQuery(sqlQuery);
        sqlQuery.append(" (" +
    <#list fields as field>
                "${field.name}<#if field_has_next>, </#if>" +
    </#list>
                ") VALUES (<#rt>
    <#list fields as field>
                ?<#if field_has_next>, </#if><#t>
    </#list>
                )");<#lt>

        // write rows
        final boolean wasTransactionStarted = startTransaction();
        try (final java.sql.PreparedStatement statement = connection.prepareStatement(sqlQuery.toString()))
        {
            for (${rowName} row : rows)
            {
                writeRow(row, statement);
                statement.addBatch();
            }
            statement.executeBatch();
        }

        endTransaction(wasTransactionStarted);
    }

    <#if withCodeComments>
    /**
     * Updates row of the table.
     *
     * @param row Table row to update.
     * @param whereCondition SQL where condition to use.
     *
     * @throws SQLException In case of any failure during database update.
     * @throws IOException In case of any failure during updating of the database file.
     */
    </#if>
    public void update(${rowName} row, java.lang.String whereCondition)
            throws java.sql.SQLException, java.io.IOException
    {
        // assemble sql query
        final java.lang.StringBuilder sqlQuery = new java.lang.StringBuilder("UPDATE ");
        appendTableNameToQuery(sqlQuery);
        sqlQuery.append(" SET" +
    <#list fields as field>
                " ${field.name}=?<#if field_has_next>,</#if>" +
    </#list>
                " WHERE ");
        sqlQuery.append(whereCondition);

        // update row
        try (final java.sql.PreparedStatement statement = connection.prepareStatement(sqlQuery.toString()))
        {
            writeRow(row, statement);
            statement.executeUpdate();
        }
    }
</#if>
<#if withValidationCode>

    <#if withCodeComments>
    /**
     * Validates the table.
     *
     * Validation consists of the following validation tests:
     *
     * 1. Table schema validation.
     *
     *    Table schema is read from database and checked according to the schema. It's checked if number of
     *    columns are correct and if each column has expected type and expected 'isNotNull' and 'isPrimaryKey'
     *    flags.
     *
     * 2. Validation of column value types.
     *
     *    If column value is not null, it is checked if value type is the same as column type in the schema.
     *
     * 3. Validation of column values.
     *
     *    Each blob or integer value stored in table is read from database and checked. Blobs are read from
     *    the bit stream and written again. Then read bit stream is binary compared with the written stream.
     *    Integer values are checked according to their boundaries specified in schema.
     *
        <#if needsParameterProvider>
     * @param parameterProvider Explicit parameter provider to be used during reading.

        </#if>
     * @return Validation report which contains validation result.
     *
     * @throws SQLException In case of any failure during access to the database.
     */
    </#if>
    public zserio.runtime.validation.ValidationReport validate(<#if needsParameterProvider>ParameterProvider parameterProvider</#if>)
            throws java.sql.SQLException
    {
        final zserio.runtime.validation.ValidationTimer totalValidationTimer =
                new zserio.runtime.validation.ValidationTimer();
        totalValidationTimer.start();
        final java.util.List<zserio.runtime.validation.ValidationError> errors =
                new java.util.ArrayList<zserio.runtime.validation.ValidationError>();
        int numberOfValidatedRows = 0;
        final zserio.runtime.validation.ValidationTimer totalParameterProviderTimer =
                new zserio.runtime.validation.ValidationTimer();

    <#if hasNonVirtualField>
        if (validateSchema(errors))
        {
            <#-- don't use rowid because WITHOUT ROWID tables can be used even if Zserio does not support them -->
            final java.lang.StringBuilder sqlQuery = new java.lang.StringBuilder("SELECT " +
        <#list fields as field>
                    "${field.name}<#if field_has_next>, </#if>" +
        </#list>
                    " FROM ");
            appendTableNameToQuery(sqlQuery);

            try (
                final java.sql.PreparedStatement statement = connection.prepareStatement(sqlQuery.toString());
                final java.sql.ResultSet resultSet = statement.executeQuery();
            )
            {
                while (resultSet.next())
                {
                    numberOfValidatedRows++;

        <#list fields as field>
                    if (!validateType${field.name?cap_first}(errors, resultSet))
                        continue;
        </#list>
        <#if hasValidatableField>

                    final ${rowName} row = new ${rowName}();
            <#list fields as field>
                <#if field.sqlTypeData.isBlob>
                    if (!validateBlob${field.name?cap_first}(errors, resultSet, row,<#rt>
                            <#lt> <#if needsParameterProvider>parameterProvider,</#if>
                            totalParameterProviderTimer))
                        continue;
                <#else>
                    if (!validateField${field.name?cap_first}(errors, resultSet, row))
                        continue;
                </#if>
            </#list>
        </#if>
                }
            }
        }

    <#else>
        validateSchema(errors);
    </#if>
        totalValidationTimer.stop();

        return new zserio.runtime.validation.ValidationReport(1, numberOfValidatedRows,
                totalValidationTimer.getDuration(), totalParameterProviderTimer.getDuration(), errors);
    }
</#if>

    private void appendTableNameToQuery(java.lang.StringBuilder sqlQuery)
    {
        if (attachedDbName != null)
        {
            sqlQuery.append(attachedDbName);
            sqlQuery.append('.');
        }
        sqlQuery.append(tableName);
    }
<#if withWriterCode>

    private void executeUpdate(java.lang.String sql) throws java.sql.SQLException
    {
        try (final java.sql.Statement statement = connection.createStatement())
        {
            statement.executeUpdate(sql);
        }
    }

    private boolean startTransaction() throws java.sql.SQLException
    {
        boolean wasTransactionStarted = false;
        if (connection.getAutoCommit())
        {
            connection.setAutoCommit(false);
            wasTransactionStarted = true;
        }

        return wasTransactionStarted;
    }

    private void endTransaction(boolean wasTransactionStarted) throws java.sql.SQLException
    {
        if (wasTransactionStarted)
        {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    private java.lang.StringBuilder getCreateTableQuery() throws java.sql.SQLException
    {
        final java.lang.StringBuilder sqlQuery = new java.lang.StringBuilder("CREATE <#if virtualTableUsing??>VIRTUAL </#if>TABLE ");
        appendTableNameToQuery(sqlQuery);
        sqlQuery.append(
    <#if virtualTableUsing??>
                " USING ${virtualTableUsing}"<#rt>
                    <#lt><#if hasNonVirtualField || sqlConstraint??> +<#else>);</#if>
    </#if>
    <#if hasNonVirtualField || sqlConstraint??>
                "(" +
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
                ")");
    </#if>

        return sqlQuery;
    }

</#if>
<#macro read_blob field called_from_validation indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if called_from_validation>
${I}totalParameterProviderTimer.start();
    </#if>
    <#list field.typeParameters as parameter>
        <#if parameter.isExplicit>
${I}final ${parameter.typeInfo.typeFullName} param${parameter.name?cap_first} =
${I}        parameterProvider.<@sql_parameter_provider_getter_name parameter/>(resultSet);
        </#if>
    </#list>
    <#if called_from_validation>
${I}totalParameterProviderTimer.stop();
    </#if>
${I}final ${field.typeInfo.typeFullName} blob =
${I}        new ${field.typeInfo.typeFullName}(reader<#rt>
    <#list field.typeParameters as parameter>
                <#lt>,
        <#if parameter.isExplicit>
${I}                param${parameter.name?cap_first}<#rt>
        <#else>
${I}                (${parameter.typeInfo.typeFullName})(${parameter.expression})<#rt>
        </#if>
    </#list>
<#lt>);
</#macro>
    private static ${rowName} readRow(<#if needsParameterProvider>ParameterProvider parameterProvider, </#if>
            java.sql.ResultSet resultSet) throws java.sql.SQLException, java.io.IOException
    {
        final ${rowName} row = new ${rowName}();

<#list fields as field>
        // field ${field.name}
    <#assign valueVarName="value${field.name?cap_first}"/>
    <#if field.sqlTypeData.isBlob>
        final byte[] ${valueVarName} = resultSet.getBytes(${field?index + 1});
    <#elseif field.underlyingTypeInfo??>
        final ${field.underlyingTypeInfo.typeFullName} ${valueVarName} = <#rt>
                <#lt>resultSet.get${field.underlyingTypeInfo.typeName?cap_first}(${field?index + 1});
    <#elseif field.requiresBigInt>
        final long ${valueVarName} = resultSet.getLong(${field?index + 1});
    <#else>
        final ${field.typeInfo.typeFullName} ${valueVarName} = resultSet.get${field.typeInfo.typeName?cap_first}(${field?index + 1});
    </#if>
        if (!resultSet.wasNull())
        {
    <#if field.sqlTypeData.isBlob>
            final zserio.runtime.io.ByteArrayBitStreamReader reader =
                    new zserio.runtime.io.ByteArrayBitStreamReader(${valueVarName});
            <@read_blob field, false, 3/>
            row.set${field.name?cap_first}(blob);
    <#elseif field.typeInfo.isEnum>
            row.set${field.name?cap_first}(${field.typeInfo.typeFullName}.toEnum(${valueVarName}));
    <#elseif field.typeInfo.isBitmask>
            row.set${field.name?cap_first}(new ${field.typeInfo.typeFullName}(${valueVarName}));
    <#elseif field.requiresBigInt>
            row.set${field.name?cap_first}(java.math.BigInteger.valueOf(${valueVarName}));
    <#else>
            row.set${field.name?cap_first}(${valueVarName});
    </#if>
        }

</#list>
        return row;
    }
<#if withWriterCode>

    private static void writeRow(${rowName} row, java.sql.PreparedStatement statement)
            throws java.sql.SQLException
    {
    <#list fields as field>
        // field ${field.name}
        if (row.isNull${field.name?cap_first}())
        {
            statement.setNull(${field?index + 1}, java.sql.Types.${field.sqlTypeData.traditionalName});
        }
        else
        {
        <#if field.sqlTypeData.isBlob>
            final byte[] blobData = zserio.runtime.io.SerializeUtil.serializeToBytes(row.get${field.name?cap_first}());
            statement.setBytes(${field?index + 1}, blobData);
        <#elseif field.underlyingTypeInfo??>
            final ${field.underlyingTypeInfo.typeFullName} underlyingValue =
                    row.get${field.name?cap_first}().getValue();
            statement.set${field.underlyingTypeInfo.typeName?cap_first}(${field?index + 1}, underlyingValue);
        <#elseif field.requiresBigInt>
            final long bigIntValue = row.get${field.name?cap_first}().longValue();
            statement.setLong(${field?index + 1}, bigIntValue);
        <#else>
            final ${field.typeInfo.typeFullName} value = row.get${field.name?cap_first}();
            statement.set${field.typeInfo.typeName?cap_first}(${field?index + 1}, value);
        </#if>
        }
        <#if field_has_next>

        </#if>
    </#list>
    }
</#if>
<#if withValidationCode>

    private boolean validateSchema(java.util.List<zserio.runtime.validation.ValidationError> errors)
            throws java.sql.SQLException
    {
        final java.util.Map<java.lang.String, zserio.runtime.validation.ValidationSqliteUtil.ColumnDescription>
                schema = zserio.runtime.validation.ValidationSqliteUtil.getTableSchema(connection,
                        attachedDbName, tableName);
        boolean result = true;

    <#list fields as field>
        if (!validateColumn${field.name?cap_first}(errors, schema))
            result = false;
    </#list>

        if (!schema.isEmpty())
        {
            // report superfluous columns
            for (java.util.Map.Entry<java.lang.String,
                    zserio.runtime.validation.ValidationSqliteUtil.ColumnDescription> entry : schema.entrySet())
            {
                final java.lang.String columnName = entry.getKey();
                final java.lang.String columnType = entry.getValue().getType();
                errors.add(new zserio.runtime.validation.ValidationError(tableName, columnName,
                        zserio.runtime.validation.ValidationError.Type.COLUMN_SUPERFLUOUS,
                        "superfluous column " + tableName + "." + columnName + " of type " + columnType +
                        " encountered"));
                result = false;
            }
        }

        return result;
    }
    <#list fields as field>

    private boolean validateColumn${field.name?cap_first}(
            java.util.List<zserio.runtime.validation.ValidationError> errors,
            java.util.Map<java.lang.String,
                    zserio.runtime.validation.ValidationSqliteUtil.ColumnDescription> schema)
    {
        final zserio.runtime.validation.ValidationSqliteUtil.ColumnDescription column =
                schema.remove("${field.name}");
        <#-- if column is virtual, it can be hidden -->
        if (column == null<#if field.isVirtual> &&
                !zserio.runtime.validation.ValidationSqliteUtil.isHiddenColumnInTable(
                        connection, attachedDbName, tableName, "${field.name}")</#if>)
        {
            errors.add(new zserio.runtime.validation.ValidationError(tableName, "${field.name}",
                    zserio.runtime.validation.ValidationError.Type.COLUMN_MISSING,
                    "column ${name}.${field.name} is missing"));
            return false;
        }
        <#-- SQLite does not maintain column properties for virtual tables columns or for virtual columns -->
        <#if !virtualTableUsing?? && !field.isVirtual>

        if (!column.getType().equals("${field.sqlTypeData.name}"))
        {
            errors.add(new zserio.runtime.validation.ValidationError(tableName, "${field.name}",
                    zserio.runtime.validation.ValidationError.Type.INVALID_COLUMN_TYPE,
                    "column ${name}.${field.name} has type '" + column.getType() +
                    "' but '${field.sqlTypeData.name}' is expected"));
            return false;
        }

        if (<#if field.isNotNull>!</#if>column.isNotNull())
        {
            errors.add(new zserio.runtime.validation.ValidationError(tableName, "${field.name}",
                    zserio.runtime.validation.ValidationError.Type.INVALID_COLUMN_CONSTRAINT,
                    "column ${name}.${field.name} is <#if !field.isNotNull>NOT </#if>NULL-able, " +
                    "but the column is expected to be <#if field.isNotNull>NOT </#if>NULL-able"));
            return false;
        }

        if (<#if field.isPrimaryKey>!</#if>column.isPrimaryKey())
        {
            errors.add(new zserio.runtime.validation.ValidationError(tableName, "${field.name}",
                    zserio.runtime.validation.ValidationError.Type.INVALID_COLUMN_CONSTRAINT,
                    "column ${name}.${field.name} is <#if field.isPrimaryKey>not </#if>primary key, " +
                    "but the column is expected <#if !field.isPrimaryKey>not </#if>to be primary key"));
            return false;
        }
        </#if>

        return true;
    }
    </#list>
    <#if hasNonVirtualField>
        <#list fields as field>

    private boolean validateType${field.name?cap_first}(
            java.util.List<zserio.runtime.validation.ValidationError> errors, java.sql.ResultSet resultSet)
            throws java.sql.SQLException
    {
        // don't check null column values because new JDBC returns NUMERIC type for null in virtual tables
        // note that known Xerial JDBC drivers do not apply automatic type conversion in getObject method
        if (resultSet.getObject(${field?index + 1}) != null)
        {
            final java.sql.ResultSetMetaData metaData = resultSet.getMetaData();
            final int type = zserio.runtime.validation.ValidationSqliteUtil.sqlTypeToSqliteType(
                    metaData.getColumnType(${field?index + 1}));
            if (type != java.sql.Types.${field.sqlTypeData.traditionalName})
            {
                errors.add(new zserio.runtime.validation.ValidationError(tableName, "${field.name}",
                        getRowKeyValues(resultSet),
                        zserio.runtime.validation.ValidationError.Type.INVALID_COLUMN_TYPE,
                        "Column ${name}.${field.name} type check failed (" +
                        zserio.runtime.validation.ValidationSqliteUtil.sqliteColumnTypeName(type) +
                        " doesn't match to ${field.sqlTypeData.name})!"));
                return false;
            }
        }

        return true;
    }
        </#list>
    </#if>
    <#if hasValidatableField>
        <#list fields as field>

            <#if field.sqlTypeData.isBlob>
    private boolean validateBlob${field.name?cap_first}(
            java.util.List<zserio.runtime.validation.ValidationError> errors,
            java.sql.ResultSet resultSet, ${rowName} row, <#if needsParameterProvider>ParameterProvider parameterProvider, </#if>
            zserio.runtime.validation.ValidationTimer totalParameterProviderTimer) throws java.sql.SQLException
    {
        final byte[] blobData = resultSet.getBytes(${field?index + 1});
        if (blobData != null)
        {
            try (
                final zserio.runtime.validation.ValidationBitStreamReader reader =
                        new zserio.runtime.validation.ValidationBitStreamReader(blobData);
                final zserio.runtime.io.ByteArrayBitStreamWriter writer =
                        new zserio.runtime.io.ByteArrayBitStreamWriter();
            )
            {
                <@read_blob field, true, 4/>

                row.set${field.name?cap_first}(blob);
                blob.write(writer);

                <#-- compare using original blob is not possible because unused bits don't have to be zero -->
                final byte[] maskedOriginalBlob = reader.toMaskedByteArray();
                <#-- writer sets unused bits (e.g. if align is used) to zero implicitly -->
                final byte[] writtenBlob = writer.toByteArray();
                if (maskedOriginalBlob.length != writtenBlob.length)
                {
                    errors.add(new zserio.runtime.validation.ValidationError(tableName, "${field.name}",
                            getRowKeyValues(resultSet),
                            zserio.runtime.validation.ValidationError.Type.BLOB_COMPARE_FAILED,
                            "Blob binary compare failed because of " + "length (" + maskedOriginalBlob.length +
                            " != " + writtenBlob.length + ")"));
                    return false;
                }
                else
                {
                    boolean hasError = false;
                    for (int i = 0; i < maskedOriginalBlob.length; ++i)
                    {
                        if (maskedOriginalBlob[i] != writtenBlob[i])
                        {
                            errors.add(new zserio.runtime.validation.ValidationError(tableName,
                                    "${field.name}", getRowKeyValues(resultSet),
                                    zserio.runtime.validation.ValidationError.Type.BLOB_COMPARE_FAILED,
                                    "Blob binary compare failed at byte position " + i));
                            hasError = true;
                        }
                    }
                    if (hasError)
                        return false;
                }
            }
            catch (java.io.IOException exception)
            {
                errors.add(new zserio.runtime.validation.ValidationError(tableName, "${field.name}",
                        getRowKeyValues(resultSet),
                        zserio.runtime.validation.ValidationError.Type.BLOB_PARSE_FAILED, exception));
                return false;
            }
            catch (java.lang.RuntimeException exception)
            {
                errors.add(new zserio.runtime.validation.ValidationError(tableName, "${field.name}",
                        getRowKeyValues(resultSet),
                        zserio.runtime.validation.ValidationError.Type.BLOB_PARSE_FAILED, exception));
                return false;
            }
        }

        return true;
    }
            <#else>
    private boolean validateField${field.name?cap_first}(
            java.util.List<zserio.runtime.validation.ValidationError> errors, java.sql.ResultSet resultSet,
            ${rowName} row) throws java.sql.SQLException
    {
                <#if field.typeInfo.isEnum || field.rangeCheckData.sqlRangeData?? || field.requiresBigInt>
        final long value = resultSet.getLong(${field?index + 1});
                <#else>
        final ${field.typeInfo.typeFullName} value = resultSet.get${field.typeInfo.typeName?cap_first}(${field?index + 1});
                </#if>
        if (!resultSet.wasNull())
        {
                <#if !field.isVirtual>
                    <#if field.rangeCheckData.sqlRangeData??>
            <@sql_field_range_check field.rangeCheckData, name, field.name/>
                    </#if>
                    <#if field.typeInfo.isEnum>
            try
            {
                ${field.typeInfo.typeFullName}.toEnum((${field.underlyingTypeInfo.typeFullName})value);
            }
            catch (java.lang.IllegalArgumentException exception)
            {
                errors.add(new zserio.runtime.validation.ValidationError(tableName, "${field.name}",
                        getRowKeyValues(resultSet),
                        zserio.runtime.validation.ValidationError.Type.INVALID_ENUM_VALUE,
                        "Enumeration value " + value + " of ${name}.${field.name} is not valid!"));
                return false;
            }
                    </#if>
                </#if>
                <#if field.typeInfo.isEnum>
            row.set${field.name?cap_first}(
                    ${field.typeInfo.typeFullName}.toEnum((${field.underlyingTypeInfo.typeFullName})value));
                <#elseif field.typeInfo.isBitmask>
            row.set${field.name?cap_first}(
                    new ${field.typeInfo.typeFullName}((${field.underlyingTypeInfo.typeFullName})value));
                <#elseif field.requiresBigInt>
            row.set${field.name?cap_first}(java.math.BigInteger.valueOf(value));
                <#elseif field.rangeCheckData.sqlRangeData??>
            row.set${field.name?cap_first}((${field.typeInfo.typeFullName})value);
                <#else>
            row.set${field.name?cap_first}(value);
                </#if>
        }

        return true;
    }
            </#if>
        </#list>
    </#if>
    <#if hasNonVirtualField>

        <#assign hasPrimaryKeyField=false/>
        <#list fields as field>
            <#if field.isPrimaryKey>
                <#assign hasPrimaryKeyField=true/>
                <#break>
            </#if>
        </#list>
    private static java.util.List<java.lang.String> getRowKeyValues(java.sql.ResultSet resultSet)
            throws java.sql.SQLException
    {
        final java.util.List<java.lang.String> rowKeyValues = new java.util.ArrayList<java.lang.String>();
        <#list fields as field>
            <#if !hasPrimaryKeyField || field.isPrimaryKey>
                <#if field.sqlTypeData.isBlob>
        rowKeyValues.add("BLOB");
                <#else>
        final java.lang.String value${field.name?cap_first} = resultSet.getString(${field?index + 1});
        rowKeyValues.add((value${field.name?cap_first} != null) ? value${field.name?cap_first} : "NULL");
                </#if>
            </#if>
        </#list>

        return rowKeyValues;
    }
    </#if>
</#if>

    private final java.sql.Connection connection;
    private final java.lang.String attachedDbName;
    private final java.lang.String tableName;
}
