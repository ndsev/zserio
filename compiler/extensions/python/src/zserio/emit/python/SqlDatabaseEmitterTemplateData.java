package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ZserioType;
import zserio.ast.Field;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.TypeReference;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.PythonNativeType;

public final  class SqlDatabaseEmitterTemplateData extends UserTypeTemplateData
{
    public SqlDatabaseEmitterTemplateData(TemplateDataContext context, SqlDatabaseType databaseType)
            throws ZserioEmitException
    {
        super(context, databaseType);

        final PythonNativeTypeMapper pythonNativeTypeMapper = context.getPythonNativeTypeMapper();
        fields = new ArrayList<DatabaseFieldData>();
        for (Field field: databaseType.getFields())
            fields.add(new DatabaseFieldData(pythonNativeTypeMapper, field, this));

        importPackage("apsw");
    }

    public Iterable<DatabaseFieldData> getFields()
    {
        return fields;
    }

    public static class DatabaseFieldData
    {
        public DatabaseFieldData(PythonNativeTypeMapper pythonNativeTypeMapper, Field field,
                ImportCollector importCollector) throws ZserioEmitException
        {
            name = field.getName();

            final PythonNativeType nativeType = pythonNativeTypeMapper.getPythonType(field.getFieldType());
            importCollector.importType(nativeType);
            pythonTypeName = nativeType.getFullName();

            getterName = AccessorNameFormatter.getGetterName(field);
            final ZserioType fieldBaseType = TypeReference.resolveBaseType(field.getFieldReferencedType());
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
