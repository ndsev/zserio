package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Parameter;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;
import zserio.extension.java.types.NativeBooleanType;
import zserio.extension.java.types.NativeDoubleType;
import zserio.extension.java.types.NativeEnumType;
import zserio.extension.java.types.NativeFloatType;
import zserio.extension.java.types.NativeLongType;

public final class CompoundParameterTemplateData
{
    public CompoundParameterTemplateData(JavaNativeMapper javaNativeMapper,
            boolean withRangeCheckCode, CompoundType compoundType,
            ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
    {
        compoundName = compoundType.getName();

        final List<Parameter> compoundParameterTypeList = compoundType.getTypeParameters();
        compoundParameterList = new ArrayList<CompoundParameter>(compoundParameterTypeList.size());
        for (Parameter compoundParameterType : compoundParameterTypeList)
            compoundParameterList.add(new CompoundParameter(javaNativeMapper, compoundParameterType));
    }

    public Iterable<CompoundParameter> getList()
    {
        return compoundParameterList;
    }

    public String getCompoundName()
    {
        return compoundName;
    }

    public static class CompoundParameter
    {
        public CompoundParameter(JavaNativeMapper javaNativeMapper, Parameter parameter)
                throws ZserioExtensionException
        {
            name = parameter.getName();
            JavaNativeType nativeType = javaNativeMapper.getJavaType(parameter.getTypeReference());
            javaTypeName = nativeType.getFullName();
            typeInfo = new TypeInfoTemplateData(parameter.getTypeReference(), nativeType);
            getterName = AccessorNameFormatter.getGetterName(parameter);
            isBool = nativeType instanceof NativeBooleanType;
            isLong = nativeType instanceof NativeLongType;
            isFloat = nativeType instanceof NativeFloatType;
            isDouble = nativeType instanceof NativeDoubleType;
            isEnum = nativeType instanceof NativeEnumType;
            isSimpleType = nativeType.isSimple();
        }

        public String getName()
        {
            return name;
        }

        public String getJavaTypeName()
        {
            return javaTypeName;
        }

        public TypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
        }

        public String getGetterName()
        {
            return getterName;
        }

        public boolean getIsBool()
        {
            return isBool;
        }

        public boolean getIsLong()
        {
            return isLong;
        }

        public boolean getIsFloat()
        {
            return isFloat;
        }

        public boolean getIsDouble()
        {
            return isDouble;
        }

        public boolean getIsEnum()
        {
            return isEnum;
        }

        public boolean getIsSimpleType()
        {
            return isSimpleType;
        }

        private final String name;
        private final String javaTypeName;
        private final TypeInfoTemplateData typeInfo;
        private final String getterName;
        private final boolean isBool;
        private final boolean isLong;
        private final boolean isFloat;
        private final boolean isDouble;
        private final boolean isEnum;
        private final boolean isSimpleType;
    }

    private final String                    compoundName;
    private final List<CompoundParameter>   compoundParameterList;
}
