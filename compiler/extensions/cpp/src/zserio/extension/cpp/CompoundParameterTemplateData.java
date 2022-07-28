package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Parameter;
import zserio.ast.TypeReference;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

/**
 * FreeMarker template data for compound parameters.
 */
public class CompoundParameterTemplateData
{
    public CompoundParameterTemplateData(TemplateDataContext context, CompoundType compoundType,
            IncludeCollector includeCollector) throws ZserioExtensionException
    {
        final List<Parameter> compoundParameterTypeList = compoundType.getTypeParameters();
        compoundParameterList = new ArrayList<CompoundParameter>(compoundParameterTypeList.size());
        for (Parameter compoundParameterType : compoundParameterTypeList)
        {
            final CompoundParameter parameter = new CompoundParameter(context, compoundParameterType,
                    includeCollector);
            compoundParameterList.add(parameter);
        }
    }

    public Iterable<CompoundParameter> getList()
    {
        return compoundParameterList;
    }

    public static class CompoundParameter
    {
        public CompoundParameter(TemplateDataContext context, Parameter parameter,
                IncludeCollector includeCollector) throws ZserioExtensionException
        {
            final TypeReference parameterTypeReference = parameter.getTypeReference();
            final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
            final CppNativeType cppNativeType = cppNativeMapper.getCppType(parameterTypeReference);
            includeCollector.addHeaderIncludesForType(cppNativeType);

            name = parameter.getName();
            typeInfo = new NativeTypeInfoTemplateData(cppNativeType, parameterTypeReference);
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

    private final List<CompoundParameter> compoundParameterList;
}
