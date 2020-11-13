package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Parameter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

public class CompoundParameterTemplateData
{
    public CompoundParameterTemplateData(CppNativeMapper cppNativeMapper, CompoundType compoundType,
            IncludeCollector includeCollector, boolean withWriterCode) throws ZserioExtensionException
    {
        final List<Parameter> compoundParameterTypeList = compoundType.getTypeParameters();
        compoundParameterList = new ArrayList<CompoundParameter>(compoundParameterTypeList.size());
        for (Parameter compoundParameterType : compoundParameterTypeList)
        {
            final CompoundParameter parameter = new CompoundParameter(cppNativeMapper,
                    compoundParameterType, includeCollector);
            compoundParameterList.add(parameter);
        }

        this.withWriterCode = withWriterCode;
    }

    public Iterable<CompoundParameter> getList()
    {
        return compoundParameterList;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public static class CompoundParameter
    {
        public CompoundParameter(CppNativeMapper cppNativeMapper, Parameter parameter,
                IncludeCollector includeCollector) throws ZserioExtensionException
        {
            final CppNativeType cppNativeType = cppNativeMapper.getCppType(parameter.getTypeReference());
            includeCollector.addHeaderIncludesForType(cppNativeType);

            name = parameter.getName();
            cppTypeName = cppNativeType.getFullName();
            cppArgumentTypeName = cppNativeType.getArgumentTypeName();
            getterName = AccessorNameFormatter.getGetterName(parameter);
            isSimpleType = cppNativeType.isSimpleType();
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

        public String getGetterName()
        {
            return getterName;
        }

        public boolean getIsSimpleType()
        {
            return isSimpleType;
        }

        private final String name;
        private final String cppTypeName;
        private final String cppArgumentTypeName;
        private final String getterName;
        private final boolean isSimpleType;
    }

    private final List<CompoundParameter> compoundParameterList;
    private final boolean withWriterCode;
}
