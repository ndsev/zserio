package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.ast.Field;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.PythonNativeType;

public final  class SqlDatabaseEmitterTemplateData extends UserTypeTemplateData
{
    public SqlDatabaseEmitterTemplateData(TemplateDataContext context, SqlDatabaseType databaseType)
            throws ZserioEmitException
    {
        super(context, databaseType);

        importPackage("typing");
        importPackage("apsw");

        final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
        fields = new ArrayList<DatabaseFieldData>();
        for (Field field: databaseType.getFields())
            fields.add(new DatabaseFieldData(pythonNativeMapper, field, this));
    }

    public Iterable<DatabaseFieldData> getFields()
    {
        return fields;
    }

    public static class DatabaseFieldData
    {
        public DatabaseFieldData(PythonNativeMapper pythonNativeMapper, Field field,
                ImportCollector importCollector) throws ZserioEmitException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
            final PythonNativeType nativeType = pythonNativeMapper.getPythonType(fieldTypeInstantiation);
            importCollector.importType(nativeType);

            name = field.getName();
            pythonTypeName = nativeType.getFullName();
            getterName = AccessorNameFormatter.getGetterName(field);
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

        public String getGetterName()
        {
            return getterName;
        }

        public boolean getIsWithoutRowIdTable()
        {
            return isWithoutRowIdTable;
        }

        private final String name;
        private final String pythonTypeName;
        private final String getterName;
        private final boolean isWithoutRowIdTable;
    }

    private final List<DatabaseFieldData> fields;
}
