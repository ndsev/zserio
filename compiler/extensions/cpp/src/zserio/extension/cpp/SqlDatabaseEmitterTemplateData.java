package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.Field;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

public class SqlDatabaseEmitterTemplateData extends UserTypeTemplateData
{
    public SqlDatabaseEmitterTemplateData(TemplateDataContext context, SqlDatabaseType databaseType)
            throws ZserioExtensionException
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
                IncludeCollector includeCollector) throws ZserioExtensionException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();

            final CppNativeType nativeFieldType = cppNativeMapper.getCppType(fieldTypeInstantiation);
            includeCollector.addHeaderIncludesForType(nativeFieldType);

            name = field.getName();
            typeInfo = new NativeTypeInfoTemplateData(nativeFieldType, fieldTypeInstantiation);
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

        public NativeTypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
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
        private final NativeTypeInfoTemplateData typeInfo;
        private final String getterName;
        private final boolean isWithoutRowIdTable;
        private final boolean hasExplicitParameters;
    }

    private final List<DatabaseField> fields;
}
