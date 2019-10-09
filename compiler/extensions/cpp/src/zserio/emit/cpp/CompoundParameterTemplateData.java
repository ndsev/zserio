package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.ZserioType;
import zserio.ast.Parameter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.CppNativeType;

public class CompoundParameterTemplateData
{
    public CompoundParameterTemplateData(CppNativeTypeMapper cppNativeTypeMapper, CompoundType compoundType,
            IncludeCollector includeCollector, boolean withWriterCode) throws ZserioEmitException
    {
        final List<Parameter> compoundParameterTypeList = compoundType.getTypeParameters();
        compoundParameterList = new ArrayList<CompoundParameter>(compoundParameterTypeList.size());
        for (Parameter compoundParameterType : compoundParameterTypeList)
        {
            final CompoundParameter parameter = new CompoundParameter(cppNativeTypeMapper,
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
        public CompoundParameter(CppNativeTypeMapper cppNativeTypeMapper, Parameter parameter,
                IncludeCollector includeCollector) throws ZserioEmitException
        {
            final ZserioType type = parameter.getParameterType();
            final CppNativeType cppNativeType = cppNativeTypeMapper.getCppType(type);
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
