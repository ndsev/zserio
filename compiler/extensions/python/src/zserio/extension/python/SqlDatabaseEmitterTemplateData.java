package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.ast.Field;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

public final  class SqlDatabaseEmitterTemplateData extends UserTypeTemplateData
{
    public SqlDatabaseEmitterTemplateData(TemplateDataContext context, SqlDatabaseType databaseType)
            throws ZserioExtensionException
    {
        super(context, databaseType);

        importPackage("typing");
        importPackage("apsw");

        fields = new ArrayList<DatabaseFieldData>();
        for (Field field: databaseType.getFields())
            fields.add(new DatabaseFieldData(context, field, this));
    }

    public Iterable<DatabaseFieldData> getFields()
    {
        return fields;
    }

    public String getDatabaseNameConstant()
    {
        return DATABASE_NAME_CONSTANT;
    }

    public static class DatabaseFieldData
    {
        public DatabaseFieldData(TemplateDataContext context, Field field,
                ImportCollector importCollector) throws ZserioExtensionException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
            final PythonNativeType nativeType =
                    context.getPythonNativeMapper().getPythonType(fieldTypeInstantiation);
            importCollector.importType(nativeType);

            name = field.getName();
            pythonTypeName = nativeType.getFullName();
            propertyName = AccessorNameFormatter.getPropertyName(field);
            tableNameConstant = TABLE_NAME_CONSTANT_PREFIX + field.getName();
            isWithoutRowIdTable = (fieldBaseType instanceof SqlTableType) ?
                    ((SqlTableType)fieldBaseType).isWithoutRowId() : false;
        }

        public String getName()
        {
            return name;
        }

        public String getPythonTypeName()
        {
            return pythonTypeName;
        }

        public String getPropertyName()
        {
            return propertyName;
        }

        public String getTableNameConstant()
        {
            return tableNameConstant;
        }

        public boolean getIsWithoutRowIdTable()
        {
            return isWithoutRowIdTable;
        }

        private final String name;
        private final String pythonTypeName;
        private final String propertyName;
        private final String tableNameConstant;
        private final boolean isWithoutRowIdTable;
    }

    private final List<DatabaseFieldData> fields;
    private static final String DATABASE_NAME_CONSTANT = "DATABASE_NAME";
    // note that we need some prefix to prevent clashing
    private static final String TABLE_NAME_CONSTANT_PREFIX = "TABLE_NAME_";
}
