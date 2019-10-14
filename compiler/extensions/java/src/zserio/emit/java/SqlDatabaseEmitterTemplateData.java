package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.TypeInstantiation.InstantiatedParameter;
import zserio.ast.ZserioType;
import zserio.ast.Field;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.TypeReference;
import zserio.emit.common.ZserioEmitException;

public final  class SqlDatabaseEmitterTemplateData extends UserTypeTemplateData
{
    public SqlDatabaseEmitterTemplateData(TemplateDataContext context, SqlDatabaseType databaseType)
            throws ZserioEmitException
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
                throws ZserioEmitException
        {
            final TypeReference fieldTypeReference = field.getTypeInstantiation().getTypeReference();
            final ZserioType fieldBaseType = fieldTypeReference.getBaseType();
            javaTypeName = javaNativeTypeMapper.getJavaType(fieldTypeReference).getFullName();

            name = field.getName();
            getterName = AccessorNameFormatter.getGetterName(field);
            if (fieldBaseType instanceof SqlTableType)
            {
                SqlTableType tableType = (SqlTableType)fieldBaseType;
                isWithoutRowIdTable = tableType.isWithoutRowId();
                hasExplicitParameters = hasTableExplicitParameters(tableType);
            }
            else
            {
                isWithoutRowIdTable = false;
                hasExplicitParameters = false;
            }
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

        public boolean getHasExplicitParameters()
        {
            return hasExplicitParameters;
        }

        private static boolean hasTableExplicitParameters(SqlTableType tableType)
        {
            for (Field tableField : tableType.getFields())
            {
                final List<InstantiatedParameter> instantiatedParameters =
                        tableField.getTypeInstantiation().getInstantiatedParameters();
                for (InstantiatedParameter instantiatedParam : instantiatedParameters)
                {
                    if (instantiatedParam.getArgumentExpression().isExplicitVariable())
                        return true;
                }
            }

            return false;
        }

        private final String name;
        private final String javaTypeName;
        private final String getterName;
        private final boolean isWithoutRowIdTable;
        private final boolean hasExplicitParameters;
    }

    private final String rootPackageName;
    private final boolean withValidationCode;
    private final List<DatabaseFieldData> fields;
}
