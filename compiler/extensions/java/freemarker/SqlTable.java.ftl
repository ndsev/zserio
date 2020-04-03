<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#include "RangeCheck.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#assign hasBlobField=sql_table_has_blob_field(fields)/>
<#assign needsParameterProvider=explicitParameters?has_content/>
<#if withWriterCode>
    <#assign hasNonVirtualField=sql_table_has_non_virtual_field(fields)/>
</#if>
<#if withValidationCode>
    <#assign hasValidatableField=sql_table_has_validatable_field(fields)/>
</#if>

public class ${name}
{
<#if needsParameterProvider>
    public static interface ParameterProvider
    {
    <#list explicitParameters as parameter>
        ${parameter.javaTypeFullName} <@sql_parameter_provider_getter_name parameter/>(java.sql.ResultSet resultSet);
    </#list>
    };

</#if>
    public ${name}(java.sql.Connection connection, java.lang.String tableName)
    {
        __connection = connection;
        __attachedDbName = null;
        __tableName = tableName;
    }

    public ${name}(java.sql.Connection connection, java.lang.String attachedDbName,
            java.lang.String tableName)
    {
        __connection = connection;
        __attachedDbName = attachedDbName;
        __tableName = tableName;
    }
<#if withWriterCode>

    public void createTable() throws java.sql.SQLException
    {
        final java.lang.StringBuilder sqlQuery = getCreateTableQuery();
    <#if hasNonVirtualField && isWithoutRowId>
        sqlQuery.append(" WITHOUT ROWID");
    </#if>
        executeUpdate(sqlQuery.toString());
    }

    <#if hasNonVirtualField && isWithoutRowId>
    public void createOrdinaryRowIdTable() throws java.sql.SQLException
    {
        final java.lang.StringBuilder sqlQuery = getCreateTableQuery();
        executeUpdate(sqlQuery.toString());
    }

    </#if>
    public void deleteTable() throws java.sql.SQLException
    {
        final java.lang.StringBuilder sqlQuery = new java.lang.StringBuilder("DROP TABLE ");
        appendTableNameToQuery(sqlQuery);
        executeUpdate(sqlQuery.toString());
    }
</#if>

    /** Reads all rows from the table. */
    public java.util.List<${rowName}> read(<#if needsParameterProvider>ParameterProvider parameterProvider</#if>)
            throws java.sql.SQLException, java.io.IOException
    {
        return read(<#if needsParameterProvider>parameterProvider, </#if>"");
    }

    /** Reads all rows from the table which fulfill the given condition. */
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
        final java.sql.PreparedStatement statement = __connection.prepareStatement(sqlQuery.toString());
        try
        {
            final java.sql.ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                final ${rowName} row = readRow(<#if needsParameterProvider>parameterProvider, </#if>resultSet);
                rows.add(row);
            }
        }
        finally
        {
            statement.close();
        }

        return rows;
    }
<#if withWriterCode>

    /**
     * Writes the given rows to the table.
     *
     * Assumes that no other rows with the same primary keys exist, otherwise an exception is thrown.
     */
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
        final java.sql.PreparedStatement statement = __connection.prepareStatement(sqlQuery.toString());
        try
        {
            for (${rowName} row : rows)
            {
                writeRow(row, statement);
                statement.addBatch();
            }
            statement.executeBatch();
        }
        finally
        {
            statement.close();
        }

        endTransaction(wasTransactionStarted);
    }

    /** Updates given row in the table. */
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
        final java.sql.PreparedStatement statement = __connection.prepareStatement(sqlQuery.toString());
        try
        {
            writeRow(row, statement);
            statement.executeUpdate();
        }
        finally
        {
            statement.close();
        }
    }
</#if>
<#if withValidationCode>

    /** Validates all fields in all rows of the table. */
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

    <#if hasValidatableField>
        if (validateSchema(errors))
        {
            <#-- don't use rowid because WITHOUT ROWID tables can be used even if Zserio does not support them -->
            final java.lang.StringBuilder sqlQuery = new java.lang.StringBuilder("SELECT " +
        <#list fields as field>
                    "${field.name}<#if field_has_next>, </#if>" +
        </#list>
                    " FROM ");
            appendTableNameToQuery(sqlQuery);
            final java.sql.PreparedStatement statement = __connection.prepareStatement(sqlQuery.toString());

            try
            {
                final java.sql.ResultSet resultSet = statement.executeQuery();

                while (resultSet.next())
                {
                    numberOfValidatedRows++;
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
                }
            }
            finally
            {
                statement.close();
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
        if (__attachedDbName != null)
        {
            sqlQuery.append(__attachedDbName);
            sqlQuery.append('.');
        }
        sqlQuery.append(__tableName);
    }
<#if withWriterCode>

    private void executeUpdate(java.lang.String sql) throws java.sql.SQLException
    {
        final java.sql.Statement statement = __connection.createStatement();
        try
        {
            statement.executeUpdate(sql);
        }
        finally
        {
            statement.close();
        }
    }

    private boolean startTransaction() throws java.sql.SQLException
    {
        boolean wasTransactionStarted = false;
        if (__connection.getAutoCommit())
        {
            __connection.setAutoCommit(false);
            wasTransactionStarted = true;
        }

        return wasTransactionStarted;
    }

    private void endTransaction(boolean wasTransactionStarted) throws java.sql.SQLException
    {
        if (wasTransactionStarted)
        {
            __connection.commit();
            __connection.setAutoCommit(true);
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
${I}final ${parameter.javaTypeFullName} param${parameter.definitionName?cap_first} =
${I}        parameterProvider.<@sql_parameter_provider_getter_name parameter/>(resultSet);
        </#if>
    </#list>
    <#if called_from_validation>
${I}totalParameterProviderTimer.stop();
    </#if>
${I}final ${field.javaTypeFullName} blob =
${I}        new ${field.javaTypeFullName}(reader<#rt>
    <#list field.typeParameters as parameter>
                <#lt>,
        <#if parameter.isExplicit>
${I}                param${parameter.definitionName?cap_first}<#rt>
        <#else>
${I}                (${parameter.javaTypeFullName})(${parameter.expression})<#rt>
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
        final byte[] ${valueVarName} = resultSet.getBytes(${field_index + 1});
    <#elseif field.enumData??>
        final ${field.enumData.baseJavaTypeFullName} ${valueVarName} = <#rt>
                <#lt>resultSet.get${field.enumData.baseJavaTypeName?cap_first}(${field_index + 1});
    <#elseif field.bitmaskData??>
        final ${field.bitmaskData.baseJavaTypeFullName} ${valueVarName} = <#rt>
                <#lt>resultSet.get${field.bitmaskData.baseJavaTypeName?cap_first}(${field_index + 1});
    <#elseif field.requiresBigInt>
        final long ${valueVarName} = resultSet.getLong(${field_index + 1});
    <#else>
        final ${field.javaTypeFullName} ${valueVarName} = resultSet.get${field.javaTypeName?cap_first}(${field_index + 1});
    </#if>
        if (!resultSet.wasNull())
        {
    <#if field.sqlTypeData.isBlob>
            final zserio.runtime.io.ByteArrayBitStreamReader reader =
                    new zserio.runtime.io.ByteArrayBitStreamReader(${valueVarName});
            <@read_blob field, false, 3/>
            row.set${field.name?cap_first}(blob);
    <#elseif field.enumData??>
            row.set${field.name?cap_first}(${field.javaTypeFullName}.toEnum(${valueVarName}));
    <#elseif field.bitmaskData??>
            row.set${field.name?cap_first}(new ${field.javaTypeFullName}(${valueVarName}));
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
            statement.setNull(${field_index + 1}, java.sql.Types.${field.sqlTypeData.traditionalName});
        }
        else
        {
        <#if field.sqlTypeData.isBlob>
            final byte[] blobData = zserio.runtime.io.ZserioIO.write(row.get${field.name?cap_first}());
            statement.setBytes(${field_index + 1}, blobData);
        <#elseif field.enumData??>
            final ${field.enumData.baseJavaTypeFullName} enumValue =
                    row.get${field.name?cap_first}().getValue();
            statement.set${field.enumData.baseJavaTypeName?cap_first}(${field_index + 1}, enumValue);
        <#elseif field.bitmaskData??>
            final ${field.bitmaskData.baseJavaTypeFullName} bitmaskValue =
                    row.get${field.name?cap_first}().getValue();
            statement.set${field.bitmaskData.baseJavaTypeName?cap_first}(${field_index + 1}, bitmaskValue);
        <#elseif field.requiresBigInt>
            final long bigIntValue = row.get${field.name?cap_first}().longValue();
            statement.setLong(${field_index + 1}, bigIntValue);
        <#else>
            final ${field.javaTypeFullName} value = row.get${field.name?cap_first}();
            statement.set${field.javaTypeName?cap_first}(${field_index + 1}, value);
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
                schema = zserio.runtime.validation.ValidationSqliteUtil.getTableSchema(__connection,
                        __attachedDbName, __tableName);
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
                errors.add(new zserio.runtime.validation.ValidationError(__tableName, columnName,
                        zserio.runtime.validation.ValidationError.Type.COLUMN_SUPERFLUOUS,
                        "superfluous column " + __tableName + "." + columnName + " of type " + columnType +
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
                        __connection, __attachedDbName, __tableName, "${field.name}")</#if>)
        {
            errors.add(new zserio.runtime.validation.ValidationError(__tableName, "${field.name}",
                    zserio.runtime.validation.ValidationError.Type.COLUMN_MISSING,
                    "column ${name}.${field.name} is missing"));
            return false;
        }
        <#-- SQLite does not maintain column properties for virtual tables columns or for virtual columns -->
        <#if !virtualTableUsing?? && !field.isVirtual>

        if (!column.getType().equals("${field.sqlTypeData.name}"))
        {
            errors.add(new zserio.runtime.validation.ValidationError(__tableName, "${field.name}",
                    zserio.runtime.validation.ValidationError.Type.INVALID_COLUMN_TYPE,
                    "column ${name}.${field.name} has type '" + column.getType() +
                    "' but '${field.sqlTypeData.name}' is expected"));
            return false;
        }

        if (<#if field.isNotNull>!</#if>column.isNotNull())
        {
            errors.add(new zserio.runtime.validation.ValidationError(__tableName, "${field.name}",
                    zserio.runtime.validation.ValidationError.Type.INVALID_COLUMN_CONSTRAINT,
                    "column ${name}.${field.name} is <#if !field.isNotNull>NOT </#if>NULL-able, " +
                    "but the column is expected to be <#if field.isNotNull>NOT </#if>NULL-able"));
            return false;
        }

        if (<#if field.isPrimaryKey>!</#if>column.isPrimaryKey())
        {
            errors.add(new zserio.runtime.validation.ValidationError(__tableName, "${field.name}",
                    zserio.runtime.validation.ValidationError.Type.INVALID_COLUMN_CONSTRAINT,
                    "column ${name}.${field.name} is <#if field.isPrimaryKey>not </#if>primary key, " +
                    "but the column is expected <#if !field.isPrimaryKey>not </#if>to be primary key"));
            return false;
        }
        </#if>

        return true;
    }
    </#list>
    <#if hasValidatableField>
        <#list fields as field>
            <#if field.sqlTypeData.isBlob>

    private boolean validateBlob${field.name?cap_first}(
            java.util.List<zserio.runtime.validation.ValidationError> errors,
            java.sql.ResultSet resultSet, ${rowName} row, <#if needsParameterProvider>ParameterProvider parameterProvider, </#if>
            zserio.runtime.validation.ValidationTimer totalParameterProviderTimer) throws java.sql.SQLException
    {
        final byte[] blobData = resultSet.getBytes(${field_index + 1});
        if (blobData != null)
        {
            final zserio.runtime.validation.ValidationBitStreamReader reader =
                    new zserio.runtime.validation.ValidationBitStreamReader(blobData);
            final zserio.runtime.io.ByteArrayBitStreamWriter writer =
                    new zserio.runtime.io.ByteArrayBitStreamWriter();
            try
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
                    errors.add(new zserio.runtime.validation.ValidationError(__tableName, "${field.name}",
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
                            errors.add(new zserio.runtime.validation.ValidationError(__tableName,
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
                errors.add(new zserio.runtime.validation.ValidationError(__tableName, "${field.name}",
                        getRowKeyValues(resultSet),
                        zserio.runtime.validation.ValidationError.Type.BLOB_PARSE_FAILED, exception));
                return false;
            }
            catch (java.lang.RuntimeException exception)
            {
                errors.add(new zserio.runtime.validation.ValidationError(__tableName, "${field.name}",
                        getRowKeyValues(resultSet),
                        zserio.runtime.validation.ValidationError.Type.BLOB_PARSE_FAILED, exception));
                return false;
            }
            finally
            {
                closeStream(writer);
                closeStream(reader);
            }
        }

        return true;
    }
            <#else>

    private boolean validateField${field.name?cap_first}(
            java.util.List<zserio.runtime.validation.ValidationError> errors, java.sql.ResultSet resultSet,
            ${rowName} row) throws java.sql.SQLException
    {
                <#if field.enumData?? || field.rangeCheckData.sqlRangeData?? || field.requiresBigInt>
        final long value = resultSet.getLong(${field_index + 1});
                <#else>
        final ${field.javaTypeFullName} value = resultSet.get${field.javaTypeName?cap_first}(${field_index + 1});
                </#if>
        if (!resultSet.wasNull())
        {
                <#if !field.isVirtual>
                    <#if field.rangeCheckData.sqlRangeData??>
            <@sql_field_range_check field.rangeCheckData, name, field.name/>
                    </#if>
                    <#if field.enumData??>
            try
            {
                ${field.javaTypeFullName}.toEnum((${field.enumData.baseJavaTypeFullName})value);
            }
            catch (java.lang.IllegalArgumentException exception)
            {
                errors.add(new zserio.runtime.validation.ValidationError(__tableName, "${field.name}",
                        getRowKeyValues(resultSet),
                        zserio.runtime.validation.ValidationError.Type.INVALID_ENUM_VALUE,
                        "Enumeration value " + value + " of ${name}.${field.name} is not valid!"));
                return false;
            }
                    </#if>
                </#if>
                <#if field.enumData??>
            row.set${field.name?cap_first}(${field.javaTypeFullName}.toEnum(
                    (${field.enumData.baseJavaTypeFullName})value));
                <#elseif field.requiresBigInt>
            row.set${field.name?cap_first}(java.math.BigInteger.valueOf(value));
                <#elseif field.rangeCheckData.sqlRangeData??>
            row.set${field.name?cap_first}((${field.javaTypeFullName})value);
                <#else>
            row.set${field.name?cap_first}(value);
                </#if>
        }

        return true;
    }
            </#if>
        </#list>

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
        final java.lang.String value${field.name?cap_first} = resultSet.getString(${field_index + 1});
        rowKeyValues.add((value${field.name?cap_first} != null) ? value${field.name?cap_first} : "NULL");
                </#if>
            </#if>
        </#list>

        return rowKeyValues;
    }
        <#if hasBlobField>

    private static void closeStream(zserio.runtime.io.BitStreamCloseable stream)
    {
        try
        {
            stream.close();
        }
        catch (java.io.IOException exception)
        {
            // this cannot happen for byte array streams
        }
    }
        </#if>
    </#if>
</#if>

    private final java.sql.Connection __connection;
    private final java.lang.String __attachedDbName;
    private final java.lang.String __tableName;
}
