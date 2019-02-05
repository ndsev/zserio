<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#include "GeneratePkgPrefix.inc.ftl">
<#include "RangeCheck.inc.ftl">
<@standard_header generatorDescription, packageName, javaMajorVersion, [
        "java.sql.SQLException",
        "java.sql.PreparedStatement",
        "java.sql.ResultSet",
        "java.io.IOException",
        "java.util.List",
        "java.util.ArrayList",
        "zserio.runtime.SqlDatabase"
]/>
<#assign hasBlobField=sql_table_has_blob_field(fields)/>
<#assign needsParameterProvider=explicitParameters?has_content/>
<#if withWriterCode>
    <#assign hasNonVirtualField=sql_table_has_non_virtual_field(fields)/>
</#if>
<#if withValidationCode>
    <#assign hasValidateableField=sql_table_has_validatable_field(fields)/>
</#if>
<#if hasBlobField>
    <#if withWriterCode>
<@imports ["zserio.runtime.io.ZserioIO"]/>
    </#if>
<@imports ["zserio.runtime.io.ByteArrayBitStreamReader"]/>
</#if>
<#if withValidationCode>
<@imports [
        "java.util.Map",
        "zserio.runtime.validation.ValidationError",
        "zserio.runtime.validation.ValidationReport",
        "zserio.runtime.validation.ValidationSqlUtil",
        "zserio.runtime.validation.ValidationTimer"
]/>
    <#if hasBlobField>
<@imports [
        "zserio.runtime.validation.ValidationBitStreamReader",
        "zserio.runtime.io.ByteArrayBitStreamWriter",
        "zserio.runtime.io.BitStreamCloseable"
]/>
    </#if>
</#if>

<@class_header generatorDescription/>
public class ${name}
{
<#if needsParameterProvider>
    public static interface IParameterProvider
    {
    <#list explicitParameters as parameter>
        ${parameter.javaTypeName} <@sql_parameter_provider_getter_name parameter/>(ResultSet resultSet);
    </#list>
    };

</#if>
    public ${name}(SqlDatabase db, String tableName)
    {
        __db = db;
        __attachedDbName = null;
        __tableName = tableName;
    }

    public ${name}(SqlDatabase db, String attachedDbName, String tableName)
    {
        __db = db;
        __attachedDbName = attachedDbName;
        __tableName = tableName;
    }
<#if withWriterCode>

    public void createTable() throws SQLException
    {
        final StringBuilder sqlQuery = getCreateTableQuery();
    <#if hasNonVirtualField && isWithoutRowId>
        sqlQuery.append(" WITHOUT ROWID");
    </#if>
        __db.executeUpdate(sqlQuery.toString());
    }

    <#if hasNonVirtualField && isWithoutRowId>
    public void createOrdinaryRowIdTable() throws SQLException
    {
        final StringBuilder sqlQuery = getCreateTableQuery();
        __db.executeUpdate(sqlQuery.toString());
    }

    </#if>
    public void deleteTable() throws SQLException
    {
        final StringBuilder sqlQuery = new StringBuilder("DROP TABLE ");
        appendTableNameToQuery(sqlQuery);
        __db.executeUpdate(sqlQuery.toString());
    }
</#if>

    /** Reads all rows from the table. */
    public List<${rowName}> read(<#if needsParameterProvider>IParameterProvider parameterProvider</#if>)
            throws SQLException, IOException
    {
        return read(<#if needsParameterProvider>parameterProvider, </#if>"");
    }

    /** Reads all rows from the table which fulfill the given condition. */
    public List<${rowName}> read(<#if needsParameterProvider>IParameterProvider parameterProvider, </#if><#rt>
            <#lt>String condition) throws SQLException, IOException
    {
        // assemble sql query
        final StringBuilder sqlQuery = new StringBuilder("SELECT " +
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
        final List<${rowName}> rows = new ArrayList<${rowName}>();
        final PreparedStatement statement = __db.prepareStatement(sqlQuery.toString());
        try
        {
            final ResultSet resultSet = statement.executeQuery();
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
    public void write(List<${rowName}> rows) throws SQLException, IOException
    {
        // assemble sql query
        final StringBuilder sqlQuery = new StringBuilder("INSERT INTO ");
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
        final boolean wasTransactionStarted = __db.startTransaction();
        final PreparedStatement statement = __db.prepareStatement(sqlQuery.toString());
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

        __db.endTransaction(wasTransactionStarted);
    }

    /** Updates given row in the table. */
    public void update(${rowName} row, String whereCondition) throws SQLException, IOException
    {
        // assemble sql query
        final StringBuilder sqlQuery = new StringBuilder("UPDATE ");
        appendTableNameToQuery(sqlQuery);
        sqlQuery.append(" SET" +
    <#list fields as field>
                " ${field.name}=?<#if field_has_next>,</#if>" +
    </#list>
                " WHERE ");
        sqlQuery.append(whereCondition);

        // update row
        final PreparedStatement statement = __db.prepareStatement(sqlQuery.toString());
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
    public ValidationReport validate(<#if needsParameterProvider>IParameterProvider parameterProvider</#if>)
            throws SQLException
    {
        final ValidationTimer totalValidationTimer = new ValidationTimer();
        totalValidationTimer.start();
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        int numberOfValidatedRows = 0;
        final ValidationTimer totalParameterProviderTimer = new ValidationTimer();

    <#if hasValidateableField>
        if (validateSchema(errors))
        {
            <#-- don't use rowid because WITHOUT ROWID tables can be used even if Zserio does not support them -->
            final StringBuilder sqlQuery = new StringBuilder("SELECT " +
        <#list fields as field>
                    "${field.name}<#if field_has_next>, </#if>" +
        </#list>
                    " FROM ");
            appendTableNameToQuery(sqlQuery);
            final PreparedStatement statement = __db.prepareStatement(sqlQuery.toString());

            try
            {
                final ResultSet resultSet = statement.executeQuery();

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

        return new ValidationReport(1, numberOfValidatedRows, totalValidationTimer.getDuration(),
                totalParameterProviderTimer.getDuration(), errors);
    }
</#if>

    private void appendTableNameToQuery(StringBuilder sqlQuery)
    {
        if (__attachedDbName != null)
        {
            sqlQuery.append(__attachedDbName);
            sqlQuery.append('.');
        }
        sqlQuery.append(__tableName);
    }
<#if withWriterCode>

    private StringBuilder getCreateTableQuery() throws SQLException
    {
        final StringBuilder sqlQuery = new StringBuilder("CREATE <#if virtualTableUsing??>VIRTUAL </#if>TABLE ");
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
                "${field.name}<#if needsTypesInSchema> ${field.sqlTypeData.name}</#if><#rt>
                    <#lt><#if field.sqlConstraint??> ${sql_strip_quotes(field.sqlConstraint)}</#if><#if field_has_next>, </#if>" +
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
${I}final ${parameter.javaTypeName} param${parameter.definitionName?cap_first} =
${I}        parameterProvider.<@sql_parameter_provider_getter_name parameter/>(resultSet);
        </#if>
    </#list>
    <#if called_from_validation>
${I}totalParameterProviderTimer.stop();
    </#if>
${I}final ${field.javaTypeName} blob = new ${field.javaTypeName}(reader<#rt>
    <#list field.typeParameters as parameter>
<#lt>,
        <#if parameter.isExplicit>
${I}    param${parameter.definitionName?cap_first}<#rt>
        <#else>
${I}    (${parameter.javaTypeName})(${parameter.expression})<#rt>
        </#if>
    </#list>
<#lt>);
</#macro>
    private static ${rowName} readRow(<#if needsParameterProvider>IParameterProvider parameterProvider, </#if><#rt>
            <#lt>ResultSet resultSet) throws SQLException, IOException
    {
        final ${rowName} row = new ${rowName}();

<#list fields as field>
        // field ${field.name}
    <#assign valueVarName="value${field.name?cap_first}"/>
    <#if field.sqlTypeData.isBlob>
        final byte[] ${valueVarName} = resultSet.getBytes(${field_index + 1});
    <#elseif field.enumData??>
        final ${field.enumData.baseJavaTypeName} ${valueVarName} = <#rt>
            resultSet.get${field.enumData.baseJavaTypeName?cap_first}(${field_index + 1})<#lt>;
    <#elseif field.requiresBigInt>
        final long ${valueVarName} = resultSet.getLong(${field_index + 1});
    <#else>
        final ${field.javaTypeName} ${valueVarName} = resultSet.get${field.javaTypeName?cap_first}(${field_index + 1});
    </#if>
        if (!resultSet.wasNull())
        {
    <#if field.sqlTypeData.isBlob>
            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(${valueVarName});
            <@read_blob field, false, 3/>
            row.set${field.name?cap_first}(blob);
    <#elseif field.enumData??>
            row.set${field.name?cap_first}(${field.javaTypeName}.toEnum(${valueVarName}));
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

    private static void writeRow(${rowName} row, PreparedStatement statement) throws SQLException
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
            final byte[] blobData = ZserioIO.write(row.get${field.name?cap_first}());
            statement.setBytes(${field_index + 1}, blobData);
        <#elseif field.enumData??>
            final ${field.enumData.baseJavaTypeName} enumValue = row.get${field.name?cap_first}().getValue();
            statement.set${field.enumData.baseJavaTypeName?cap_first}(${field_index + 1}, enumValue);
        <#elseif field.requiresBigInt>
            final long bigIntValue = row.get${field.name?cap_first}().longValue();
            statement.setLong(${field_index + 1}, bigIntValue);
        <#else>
            final ${field.javaTypeName} value = row.get${field.name?cap_first}();
            statement.set${field.javaTypeName?cap_first}(${field_index + 1}, value);
        </#if>
        }
        <#if field_has_next>

        </#if>
    </#list>
    }
</#if>
<#if withValidationCode>

    private boolean validateSchema(List<ValidationError> errors) throws SQLException
    {
        final Map<String, ValidationSqlUtil.ColumnDescription> schema =
                ValidationSqlUtil.getTableSchema(__db, __attachedDbName, __tableName);
        boolean result = true;

    <#list fields as field>
        if (!validateColumn${field.name?cap_first}(errors, schema))
            result = false;
    </#list>

        if (!schema.isEmpty())
        {
            // report superfluous columns
            for (Map.Entry<String, ValidationSqlUtil.ColumnDescription> entry : schema.entrySet())
            {
                final String columnName = entry.getKey();
                final String columnType = entry.getValue().getType();
                errors.add(new ValidationError(__tableName, columnName, ValidationError.Type.COLUMN_SUPERFLUOUS,
                        "superfluous column " + __tableName + "." + columnName + " of type " + columnType +
                        " encountered"));
                result = false;
            }
        }

        return result;
    }
    <#list fields as field>

    private boolean validateColumn${field.name?cap_first}(List<ValidationError> errors,
            Map<String, ValidationSqlUtil.ColumnDescription> schema)
    {
        final ValidationSqlUtil.ColumnDescription column = schema.remove("${field.name}");
        <#-- if column is virtual, it can be hidden -->
        if (column == null<#if field.isVirtual> &&
                !ValidationSqlUtil.isHiddenColumnInTable(__db, __attachedDbName, __tableName, "${field.name}")</#if>)
        {
            errors.add(new ValidationError(__tableName, "${field.name}", ValidationError.Type.COLUMN_MISSING,
                    "column ${name}.${field.name} is missing"));
            return false;
        }
        <#-- SQLite does not maintain column properties for virtual tables columns or for virtual columns -->
        <#if !virtualTableUsing?? && !field.isVirtual>

        if (!column.getType().equals("${field.sqlTypeData.name}"))
        {
            errors.add(new ValidationError(__tableName, "${field.name}",
                    ValidationError.Type.INVALID_COLUMN_TYPE,
                    "column ${name}.${field.name} has type '" + column.getType() +
                    "' but '${field.sqlTypeData.name}' is expected"));
            return false;
        }

        if (<#if field.isNotNull>!</#if>column.isNotNull())
        {
            errors.add(new ValidationError(__tableName, "${field.name}",
                    ValidationError.Type.INVALID_COLUMN_CONSTRAINT,
                    "column ${name}.${field.name} is <#if !field.isNotNull>NOT </#if>NULL-able, " +
                    "but the column is expected to be <#if field.isNotNull>NOT </#if>NULL-able"));
            return false;
        }

        if (<#if field.isPrimaryKey>!</#if>column.isPrimaryKey())
        {
            errors.add(new ValidationError(__tableName, "${field.name}",
                    ValidationError.Type.INVALID_COLUMN_CONSTRAINT,
                    "column ${name}.${field.name} is <#if field.isPrimaryKey>not </#if>primary key, " +
                    "but the column is expected <#if !field.isPrimaryKey>not </#if>to be primary key"));
            return false;
        }
        </#if>

        return true;
    }
    </#list>
    <#if hasValidateableField>
        <#list fields as field>
            <#if field.sqlTypeData.isBlob>

    private boolean validateBlob${field.name?cap_first}(List<ValidationError> errors,
            ResultSet resultSet, ${rowName} row, <#if needsParameterProvider>IParameterProvider parameterProvider, </#if>
            ValidationTimer totalParameterProviderTimer) throws SQLException
    {
        final byte[] blobData = resultSet.getBytes(${field_index + 1});
        if (blobData != null)
        {
            final ValidationBitStreamReader reader = new ValidationBitStreamReader(blobData);
            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            try
            {
                <@read_blob field, true, 4/>

                row.set${field.name?cap_first}(blob);
                blob.write(writer);

                <#-- compare using original blob is not possible because unused bits do't have to be zero -->
                final byte[] maskedOriginalBlob = reader.toMaskedByteArray();
                <#-- writer sets unused bits (e.g. if align is used) to zero implicitly -->
                final byte[] writtenBlob = writer.toByteArray();
                if (maskedOriginalBlob.length != writtenBlob.length)
                {
                    errors.add(new ValidationError(__tableName, "${field.name}", getRowKeyValues(resultSet),
                            ValidationError.Type.BLOB_COMPARE_FAILED, "Blob binary compare failed because of " +
                            "length (" + maskedOriginalBlob.length + " != " + writtenBlob.length + ")"));
                    return false;
                }
                else
                {
                    boolean hasError = false;
                    for (int i = 0; i < maskedOriginalBlob.length; ++i)
                    {
                        if (maskedOriginalBlob[i] != writtenBlob[i])
                        {
                            errors.add(new ValidationError(__tableName, "${field.name}",
                                    getRowKeyValues(resultSet), ValidationError.Type.BLOB_COMPARE_FAILED,
                                    "Blob binary compare failed at byte position " + i));
                            hasError = true;
                        }
                    }
                    if (hasError)
                        return false;
                }
            }
            catch (IOException exception)
            {
                errors.add(new ValidationError(__tableName, "${field.name}", getRowKeyValues(resultSet),
                        ValidationError.Type.BLOB_PARSE_FAILED, exception));
                return false;
            }
            catch (RuntimeException exception)
            {
                errors.add(new ValidationError(__tableName, "${field.name}", getRowKeyValues(resultSet),
                        ValidationError.Type.BLOB_PARSE_FAILED, exception));
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

    private boolean validateField${field.name?cap_first}(List<ValidationError> errors, ResultSet resultSet,
            ${rowName} row) throws SQLException
    {
                <#if field.enumData?? || field.rangeCheckData.sqlRangeData?? || field.requiresBigInt>
        final long value = resultSet.getLong(${field_index + 1});
                <#else>
        final ${field.javaTypeName} value = resultSet.get${field.javaTypeName?cap_first}(${field_index + 1});
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
                ${field.javaTypeName}.toEnum((${field.enumData.baseJavaTypeName})value);
            }
            catch (IllegalArgumentException exception)
            {
                errors.add(new ValidationError(__tableName, "${field.name}", getRowKeyValues(resultSet),
                        ValidationError.Type.INVALID_ENUM_VALUE, "Enumeration value " + value +
                        " of ${name}.${field.name} is not valid!"));
                return false;
            }
                    </#if>
                </#if>
                <#if field.enumData??>
            row.set${field.name?cap_first}(${field.javaTypeName}.toEnum((${field.enumData.baseJavaTypeName})value));
                <#elseif field.requiresBigInt>
            row.set${field.name?cap_first}(java.math.BigInteger.valueOf(value));
                <#elseif field.rangeCheckData.sqlRangeData??>
            row.set${field.name?cap_first}((${field.javaTypeName})value);
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
    private static List<String> getRowKeyValues(ResultSet resultSet) throws SQLException
    {
        final List<String> rowKeyValues = new ArrayList<String>();
        <#list fields as field>
            <#if !hasPrimaryKeyField || field.isPrimaryKey>
                <#if field.sqlTypeData.isBlob>
        rowKeyValues.add("BLOB");
                <#else>
        final String value${field.name?cap_first} = resultSet.getString(${field_index + 1});
        rowKeyValues.add((value${field.name?cap_first} != null) ? value${field.name?cap_first} : "NULL");
                </#if>
            </#if>
        </#list>

        return rowKeyValues;
    }
        <#if hasBlobField>

    private static void closeStream(BitStreamCloseable stream)
    {
        try
        {
            stream.close();
        }
        catch (IOException exception)
        {
            // this cannot happen for byte array streams
        }
    }
        </#if>
    </#if>
</#if>

    private final SqlDatabase __db;
    private final String __attachedDbName;
    private final String __tableName;
}
