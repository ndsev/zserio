package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ZserioType;
import zserio.ast.Field;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.TypeReference;

public final  class SqlDatabaseEmitterTemplateData extends UserTypeTemplateData
{
    public SqlDatabaseEmitterTemplateData(TemplateDataContext context, SqlDatabaseType databaseType)
    {
        super(context, databaseType);

        rootPackageName = context.getJavaRootPackageName();
        this.withValidationCode = context.getWithValidationCode();

        final JavaNativeTypeMapper javaNativeTypeMapper = context.getJavaNativeTypeMapper();
        fields = new ArrayList<DatabaseFieldData>();
        for (Field field: databaseType.getFields())
            fields.add(new DatabaseFieldData(javaNativeTypeMapper, field));
    }

    public String getRootPackageName()
    {
        return rootPackageName;
    }

    public boolean getWithValidationCode()
    {
        return withValidationCode;
    }

    public Iterable<DatabaseFieldData> getFields()
    {
        return fields;
    }

    public static class DatabaseFieldData
    {
        public DatabaseFieldData(JavaNativeTypeMapper javaNativeTypeMapper, Field field)
        {
            name = field.getName();
            javaTypeName = javaNativeTypeMapper.getJavaType(field.getFieldType()).getFullName();
            getterName = AccessorNameFormatter.getGetterName(field);
            final ZserioType fieldBaseType = TypeReference.resolveBaseType(field.getFieldReferencedType());
            isWithoutRowIdTable = (fieldBaseType instanceof SqlTableType) ?
                    ((SqlTableType)fieldBaseType).isWithoutRowId() : false;
        }

        public String getName()
        {
            return name;
        }

        public String getJavaTypeName()
        {
            return javaTypeName;
        }

        public String getGetterName()
        {
            return getterName;
        }

        public boolean getIsWithoutRowIdTable()
        {
            return isWithoutRowIdTable;
        }

        private final String  name;
        private final String  javaTypeName;
        private final String  getterName;
        private final boolean isWithoutRowIdTable;
    }

    private final String                    rootPackageName;
    private final boolean                   withValidationCode;
    private final List<DatabaseFieldData>   fields;
}
