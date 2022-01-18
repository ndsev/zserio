package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Parameter;
import zserio.ast.TypeReference;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * FreeMarker template data for compound parameters.
 */
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
            final TypeReference referencedType = parameter.getTypeReference();
            final JavaNativeType nativeType = javaNativeMapper.getJavaType(referencedType);
            typeInfo = new NativeTypeInfoTemplateData(nativeType, referencedType);
            getterName = AccessorNameFormatter.getGetterName(parameter);
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

        private final String name;
        private final NativeTypeInfoTemplateData typeInfo;
        private final String getterName;
    }

    private final String                    compoundName;
    private final List<CompoundParameter>   compoundParameterList;
}
