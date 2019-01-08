package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.ZserioType;
import zserio.ast.Parameter;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.types.JavaNativeType;
import zserio.emit.java.types.NativeBooleanType;
import zserio.emit.java.types.NativeDoubleType;
import zserio.emit.java.types.NativeEnumType;
import zserio.emit.java.types.NativeFloatType;
import zserio.emit.java.types.NativeLongType;

public final class CompoundParameterTemplateData
{
    public CompoundParameterTemplateData(JavaNativeTypeMapper javaNativeTypeMapper, boolean withRangeCheckCode,
            boolean withWriterCode, CompoundType compoundType, ExpressionFormatter javaExpressionFormatter)
                    throws ZserioEmitException
    {
        compoundName = compoundType.getName();
        this.withWriterCode = withWriterCode;

        final List<Parameter> compoundParameterTypeList = compoundType.getParameters();
        compoundParameterList = new ArrayList<CompoundParameter>(compoundParameterTypeList.size());
        for (Parameter compoundParameterType : compoundParameterTypeList)
            compoundParameterList.add(new CompoundParameter(javaNativeTypeMapper, compoundParameterType));
    }

    public Iterable<CompoundParameter> getList()
    {
        return compoundParameterList;
    }

    public String getCompoundName()
    {
        return compoundName;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public static class CompoundParameter
    {
        public CompoundParameter(JavaNativeTypeMapper javaNativeTypeMapper, Parameter parameter)
                throws ZserioEmitException
        {
            name = parameter.getName();
            final ZserioType type = parameter.getParameterType();
            JavaNativeType nativeType = javaNativeTypeMapper.getJavaType(type);
            javaTypeName = nativeType.getFullName();
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

        private final String    name;
        private final String    javaTypeName;
        private final String    getterName;
        private final boolean   isBool;
        private final boolean   isLong;
        private final boolean   isFloat;
        private final boolean   isDouble;
        private final boolean   isEnum;
        private final boolean   isSimpleType;
    }

    private final String                    compoundName;
    private final boolean                   withWriterCode;
    private final List<CompoundParameter>   compoundParameterList;
}
