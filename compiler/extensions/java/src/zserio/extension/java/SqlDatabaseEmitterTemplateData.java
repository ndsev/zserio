package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.ast.Field;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.extension.common.ZserioExtensionException;

public final  class SqlDatabaseEmitterTemplateData extends UserTypeTemplateData
{
    public SqlDatabaseEmitterTemplateData(TemplateDataContext context, SqlDatabaseType databaseType)
            throws ZserioExtensionException
    {
        super(context, databaseType);

        rootPackageName = context.getJavaRootPackageName();
        this.withValidationCode = context.getWithValidationCode();

        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        fields = new ArrayList<DatabaseFieldData>();
        for (Field field: databaseType.getFields())
            fields.add(new DatabaseFieldData(javaNativeMapper, field));
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
        public DatabaseFieldData(JavaNativeMapper javaNativeMapper, Field field)
                throws ZserioExtensionException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
            javaTypeName = javaNativeMapper.getJavaType(fieldTypeInstantiation).getFullName();

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
                final TypeInstantiation fieldInstantiation = tableField.getTypeInstantiation();
                if (fieldInstantiation instanceof ParameterizedTypeInstantiation)
                {
                    final ParameterizedTypeInstantiation parameterizedInstantiation =
                            (ParameterizedTypeInstantiation)fieldInstantiation;
                    final List<InstantiatedParameter> instantiatedParameters =
                            parameterizedInstantiation.getInstantiatedParameters();
                    for (InstantiatedParameter instantiatedParam : instantiatedParameters)
                    {
                        if (instantiatedParam.getArgumentExpression().isExplicitVariable())
                            return true;
                    }
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
