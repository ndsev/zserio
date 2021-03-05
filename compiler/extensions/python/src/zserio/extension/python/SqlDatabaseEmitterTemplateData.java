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
            snakeCaseName = PythonSymbolConverter.camelCaseToSnakeCase(name);
            pythonTypeName = nativeType.getFullName();
            propertyName = AccessorNameFormatter.getPropertyName(field);
            isWithoutRowIdTable = (fieldBaseType instanceof SqlTableType) ?
                    ((SqlTableType)fieldBaseType).isWithoutRowId() : false;
        }

        public String getName()
        {
            return name;
        }

        public String getSnakeCaseName()
        {
            return snakeCaseName;
        }

        public String getPythonTypeName()
        {
            return pythonTypeName;
        }

        public String getPropertyName()
        {
            return propertyName;
        }

        public boolean getIsWithoutRowIdTable()
        {
            return isWithoutRowIdTable;
        }

        private final String name;
        private final String snakeCaseName;
        private final String pythonTypeName;
        private final String propertyName;
        private final boolean isWithoutRowIdTable;
    }

    private final List<DatabaseFieldData> fields;
}
