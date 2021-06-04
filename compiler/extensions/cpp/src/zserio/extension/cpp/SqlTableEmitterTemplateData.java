package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.BitmaskType;
import zserio.ast.BooleanType;
import zserio.ast.CompoundType;
import zserio.ast.EnumType;
import zserio.ast.Parameter;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ZserioType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.common.sql.SqlNativeTypeMapper;
import zserio.extension.common.sql.types.NativeBlobType;
import zserio.extension.common.sql.types.NativeIntegerType;
import zserio.extension.common.sql.types.NativeRealType;
import zserio.extension.common.sql.types.SqlNativeType;
import zserio.extension.cpp.types.CppNativeType;
import zserio.tools.HashUtil;

public class SqlTableEmitterTemplateData extends UserTypeTemplateData
{
    public SqlTableEmitterTemplateData(TemplateDataContext context, SqlTableType tableType)
            throws ZserioExtensionException
    {
        super(context, tableType);

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);
        final SqlConstraint tableSqlConstraint = tableType.getSqlConstraint();
        sqlConstraint = (tableSqlConstraint == null) ? null :
            cppExpressionFormatter.formatGetter(tableSqlConstraint.getConstraintExpr());
        virtualTableUsing = tableType.getVirtualTableUsingString();
        needsTypesInSchema = tableType.needsTypesInSchema();
        isWithoutRowId = tableType.isWithoutRowId();

        final List<Field> tableFields = tableType.getFields();
        fields = new ArrayList<FieldTemplateData>(tableFields.size());
        final ExpressionFormatter cppSqlIndirectExpressionFormatter =
                context.getSqlIndirectExpressionFormatter(this);
        final SqlNativeTypeMapper sqlNativeTypeMapper = new SqlNativeTypeMapper();
        explicitParameters = new TreeSet<ExplicitParameterTemplateData>();
        boolean hasImplicitParameters = false;
        boolean requiresOwnerContext = false;
        for (Field tableField : tableFields)
        {
            final FieldTemplateData field = new FieldTemplateData(cppNativeMapper, cppExpressionFormatter,
                    cppSqlIndirectExpressionFormatter, sqlNativeTypeMapper, tableType, tableField, this);
            fields.add(field);

            if (field.getHasImplicitParameters())
                hasImplicitParameters = true;

            for (FieldTemplateData.ParameterTemplateData parameterTemplateData : field.getTypeParameters())
            {
                if (parameterTemplateData.getIsExplicit())
                {
                    final String expression = parameterTemplateData.getExpression();
                    final String cppTypeName = parameterTemplateData.getCppTypeName();
                    final boolean isSimpleType = parameterTemplateData.getIsSimpleType();
                    explicitParameters.add(new ExplicitParameterTemplateData(expression, cppTypeName,
                            isSimpleType));
                }
                else
                {
                    if (parameterTemplateData.getRequiresOwnerContext())
                        requiresOwnerContext = true;
                }
            }
        }
        this.hasImplicitParameters = hasImplicitParameters;
        this.requiresOwnerContext = requiresOwnerContext;
        this.needsChildrenInitialization = tableType.needsChildrenInitialization();
    }

    public Iterable<FieldTemplateData> getFields()
    {
        return fields;
    }

    public Iterable<ExplicitParameterTemplateData> getExplicitParameters()
    {
        return explicitParameters;
    }

    public boolean getHasImplicitParameters()
    {
        return hasImplicitParameters;
    }

    public boolean getRequiresOwnerContext()
    {
        return requiresOwnerContext;
    }

    public boolean getNeedsChildrenInitialization()
    {
        return needsChildrenInitialization;
    }

    public String getSqlConstraint()
    {
        return sqlConstraint;
    }

    public String getVirtualTableUsing()
    {
        return virtualTableUsing;
    }

    public boolean getNeedsTypesInSchema()
    {
        return needsTypesInSchema;
    }

    public boolean getIsWithoutRowId()
    {
        return isWithoutRowId;
    }

    public static class ExplicitParameterTemplateData implements Comparable<ExplicitParameterTemplateData>
    {
        public ExplicitParameterTemplateData(String expression, String cppTypeName, boolean isSimpleType)
        {
            this.expression = expression;
            this.cppTypeName = cppTypeName;
            this.isSimpleType = isSimpleType;
        }

        public String getExpression()
        {
            return expression;
        }

        public String getCppTypeName()
        {
            return cppTypeName;
        }

        public boolean getIsSimpleType()
        {
            return isSimpleType;
        }

        @Override
        public int compareTo(ExplicitParameterTemplateData other)
        {
            int result = expression.compareTo(other.expression);
            if (result != 0)
                return result;

            return cppTypeName.compareTo(other.cppTypeName);
        }

        @Override
        public boolean equals(Object other)
        {
            if (this == other)
                return true;

            if (other instanceof ExplicitParameterTemplateData)
            {
                return compareTo((ExplicitParameterTemplateData)other) == 0;
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, expression);
            hash = HashUtil.hash(hash, cppTypeName);
            return hash;
        }

        private final String expression;
        private final String cppTypeName;
        private final boolean isSimpleType;
    }

    public static class FieldTemplateData
    {
        public FieldTemplateData(CppNativeMapper cppNativeMapper,
                ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppSqlIndirectExpressionFormatter, SqlNativeTypeMapper sqlNativeTypeMapper,
                SqlTableType table, Field field, IncludeCollector includeCollector) throws ZserioExtensionException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
            final CppNativeType nativeFieldType = cppNativeMapper.getCppType(fieldTypeInstantiation);
            includeCollector.addHeaderIncludesForType(nativeFieldType);

            name = field.getName();
            cppTypeName = nativeFieldType.getFullName();
            cppArgumentTypeName = nativeFieldType.getArgumentTypeName();
            final SqlConstraint fieldSqlConstraint = field.getSqlConstraint();
            sqlConstraint = (fieldSqlConstraint == null) ? null :
                cppExpressionFormatter.formatGetter(fieldSqlConstraint.getConstraintExpr());
            isVirtual = field.isVirtual();

            getterName = AccessorNameFormatter.getGetterName(field);
            setterName = AccessorNameFormatter.getSetterName(field);
            resetterName = AccessorNameFormatter.getResetterName(field);
            indicatorName = AccessorNameFormatter.getIndicatorName(field);

            typeParameters = new ArrayList<ParameterTemplateData>();
            boolean hasImplicitParameters = false;
            if (fieldTypeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                final ParameterizedTypeInstantiation parameterizedInstantiation =
                        (ParameterizedTypeInstantiation)fieldTypeInstantiation;
                for (InstantiatedParameter parameter : parameterizedInstantiation.getInstantiatedParameters())
                {
                    final ParameterTemplateData parameterTemplateData = new ParameterTemplateData(
                            cppNativeMapper, cppSqlIndirectExpressionFormatter,
                            table, field, parameter, includeCollector);
                    typeParameters.add(parameterTemplateData);
                    if (!parameterTemplateData.getIsExplicit())
                        hasImplicitParameters = true;
                }
            }
            this.hasImplicitParameters = hasImplicitParameters;

            isSimpleType = nativeFieldType.isSimpleType();
            isBoolean = fieldBaseType instanceof BooleanType;
            enumData = createEnumTemplateData(cppNativeMapper, fieldBaseType, includeCollector);
            bitmaskData = createBitmaskTemplateData(cppNativeMapper, fieldBaseType, includeCollector);
            sqlTypeData = new SqlTypeTemplateData(sqlNativeTypeMapper, field);
            needsChildrenInitialization = (fieldBaseType instanceof CompoundType) &&
                    ((CompoundType)fieldBaseType).needsChildrenInitialization();
        }

        public String getName()
        {
            return name;
        }

        public String getCppTypeName()
        {
            return cppTypeName;
        }

        public String getCppArgumentTypeName()
        {
            return cppArgumentTypeName;
        }

        public String getSqlConstraint()
        {
            return sqlConstraint;
        }

        public boolean getIsVirtual()
        {
            return isVirtual;
        }

        public String getGetterName()
        {
            return getterName;
        }

        public String getSetterName()
        {
            return setterName;
        }

        public String getResetterName()
        {
            return resetterName;
        }

        public String getIndicatorName()
        {
            return indicatorName;
        }

        public Iterable<ParameterTemplateData> getTypeParameters()
        {
            return typeParameters;
        }

        public boolean getHasImplicitParameters()
        {
            return hasImplicitParameters;
        }

        public boolean getIsSimpleType()
        {
            return isSimpleType;
        }

        public boolean getIsBoolean()
        {
            return isBoolean;
        }

        public EnumTemplateData getEnumData()
        {
            return enumData;
        }

        public BitmaskTemplateData getBitmaskData()
        {
            return bitmaskData;
        }

        public SqlTypeTemplateData getSqlTypeData()
        {
            return sqlTypeData;
        }

        public boolean getNeedsChildrenInitialization()
        {
            return needsChildrenInitialization;
        }

        public static class ParameterTemplateData
        {
            public ParameterTemplateData(CppNativeMapper cppNativeMapper,
                    ExpressionFormatter cppSqlIndirectExpressionFormatter, SqlTableType tableType, Field field,
                    InstantiatedParameter instantiatedParameter,
                    IncludeCollector includeCollector) throws ZserioExtensionException
            {
                final Parameter parameter = instantiatedParameter.getParameter();
                final CppNativeType parameterNativeType =
                        cppNativeMapper.getCppType(parameter.getTypeReference());
                includeCollector.addHeaderIncludesForType(parameterNativeType);

                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                isExplicit = argumentExpression.isExplicitVariable();
                definitionName = parameter.getName();
                cppTypeName = parameterNativeType.getFullName();
                isSimpleType = parameterNativeType.isSimpleType();
                expression = cppSqlIndirectExpressionFormatter.formatGetter(argumentExpression);
                requiresOwnerContext = argumentExpression.requiresOwnerContext();
                getterName = AccessorNameFormatter.getGetterName(parameter);
            }

            public String getDefinitionName()
            {
                return definitionName;
            }

            public String getCppTypeName()
            {
                return cppTypeName;
            }

            public boolean getIsSimpleType()
            {
                return isSimpleType;
            }

            public boolean getIsExplicit()
            {
                return isExplicit;
            }

            public String getExpression()
            {
                return expression;
            }

            public boolean getRequiresOwnerContext()
            {
                return requiresOwnerContext;
            }

            public String getGetterName()
            {
                return getterName;
            }

            private final String definitionName;
            private final String cppTypeName;
            private final boolean isSimpleType;
            private final boolean isExplicit;
            private final String expression;
            private final boolean requiresOwnerContext;
            private final String getterName;
        }

        public static class EnumTemplateData
        {
            public EnumTemplateData(CppNativeMapper cppNativeMapper, EnumType enumType,
                    IncludeCollector includeCollector) throws ZserioExtensionException
            {
                final CppNativeType nativeBaseType =
                        cppNativeMapper.getCppType(enumType.getTypeInstantiation());
                includeCollector.addCppIncludesForType(nativeBaseType);
                baseCppTypeName = nativeBaseType.getFullName();
            }

            public String getBaseCppTypeName()
            {
                return baseCppTypeName;
            }

            private final String baseCppTypeName;
        }

        public static class BitmaskTemplateData
        {
            public BitmaskTemplateData(CppNativeMapper cppNativeMapper, BitmaskType bitmaskType,
                    IncludeCollector includeCollector) throws ZserioExtensionException
            {
                final CppNativeType nativeBaseType =
                        cppNativeMapper.getCppType(bitmaskType.getTypeInstantiation());
                includeCollector.addCppIncludesForType(nativeBaseType);
                baseCppTypeName = nativeBaseType.getFullName();
            }

            public String getBaseCppTypeName()
            {
                return baseCppTypeName;
            }

            private final String baseCppTypeName;
        }

        public static class SqlTypeTemplateData
        {
            public SqlTypeTemplateData(SqlNativeTypeMapper sqlNativeTypeMapper, Field field)
                    throws ZserioExtensionException
            {
                final SqlNativeType sqlNativeType = sqlNativeTypeMapper.getSqlType(
                        field.getTypeInstantiation());
                name = sqlNativeType.getFullName();
                isBlob = sqlNativeType instanceof NativeBlobType;
                isInteger = sqlNativeType instanceof NativeIntegerType;
                isReal = sqlNativeType instanceof NativeRealType;
            }

            public String getName()
            {
                return name;
            }

            public boolean getIsBlob()
            {
                return isBlob;
            }

            public boolean getIsInteger()
            {
                return isInteger;
            }

            public boolean getIsReal()
            {
                return isReal;
            }

            private final String name;
            private final boolean isBlob;
            private final boolean isInteger;
            private final boolean isReal;
        }

        private EnumTemplateData createEnumTemplateData(CppNativeMapper cppNativeMapper,
                ZserioType fieldBaseType, IncludeCollector includeCollector) throws ZserioExtensionException
        {
            if (!(fieldBaseType instanceof EnumType))
                return null;

            return new EnumTemplateData(cppNativeMapper, (EnumType)fieldBaseType, includeCollector);
        }

        private BitmaskTemplateData createBitmaskTemplateData(CppNativeMapper cppNativeMapper,
                ZserioType fieldBaseType, IncludeCollector includeCollector) throws ZserioExtensionException
        {
            if (!(fieldBaseType instanceof BitmaskType))
                return null;

            return new BitmaskTemplateData(cppNativeMapper, (BitmaskType)fieldBaseType, includeCollector);
        }

        private final String name;
        private final String cppTypeName;
        private final String cppArgumentTypeName;
        private final String sqlConstraint;
        private final boolean isVirtual;
        private final String getterName;
        private final String setterName;
        private final String resetterName;
        private final String indicatorName;
        private final List<ParameterTemplateData> typeParameters;
        private final boolean hasImplicitParameters;
        private final boolean isBoolean;
        private final EnumTemplateData enumData;
        private final BitmaskTemplateData bitmaskData;
        private final SqlTypeTemplateData sqlTypeData;
        private final boolean isSimpleType;
        private final boolean needsChildrenInitialization;
    }

    private final List<FieldTemplateData> fields;
    private final SortedSet<ExplicitParameterTemplateData> explicitParameters;
    private final boolean hasImplicitParameters;
    private final boolean requiresOwnerContext;
    private final boolean needsChildrenInitialization;
    private final String sqlConstraint;
    private final String virtualTableUsing;
    private final boolean needsTypesInSchema;
    private final boolean isWithoutRowId;
}
