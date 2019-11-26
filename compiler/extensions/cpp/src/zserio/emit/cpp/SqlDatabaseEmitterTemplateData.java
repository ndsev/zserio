package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.ast.Field;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.CppNativeType;

public class SqlDatabaseEmitterTemplateData extends UserTypeTemplateData
{
    public SqlDatabaseEmitterTemplateData(TemplateDataContext context, SqlDatabaseType databaseType)
            throws ZserioEmitException
    {
        super(context, databaseType);

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        final List<Field> dbFields = databaseType.getFields();
        fields = new ArrayList<DatabaseField>(dbFields.size());
        for (Field dbField : dbFields)
            fields.add(new DatabaseField(cppNativeMapper, dbField, this));
    }

    public Iterable<DatabaseField> getFields()
    {
        return fields;
    }

    public static class DatabaseField
    {
        public DatabaseField(CppNativeMapper cppNativeMapper, Field field,
                IncludeCollector includeCollector) throws ZserioEmitException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();

            final CppNativeType nativeFieldType = cppNativeMapper.getCppType(fieldTypeInstantiation);
            includeCollector.addHeaderIncludesForType(nativeFieldType);

            name = field.getName();
            cppTypeName = nativeFieldType.getFullName();
            getterName = AccessorNameFormatter.getGetterName(field);
            isWithoutRowIdTable = (fieldBaseType instanceof SqlTableType) ?
                    ((SqlTableType)fieldBaseType).isWithoutRowId() : false;
        }

        public String getName()
        {
            return name;
        }

        public String getCppTypeName()
        {
            return cppTypeName;
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
        private final String cppTypeName;
        private final String getterName;
        private final boolean isWithoutRowIdTable;
    }

    private final List<DatabaseField> fields;
}
