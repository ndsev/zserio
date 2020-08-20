package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Parameter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.PythonNativeType;

public final class CompoundParameterTemplateData
{
    public CompoundParameterTemplateData(CompoundType compoundType, PythonNativeMapper pythonNativeMapper,
            ImportCollector importCollector) throws ZserioEmitException
    {
        final List<Parameter> compoundParameterTypeList = compoundType.getTypeParameters();
        compoundParameterList = new ArrayList<CompoundParameter>(compoundParameterTypeList.size());
        for (Parameter compoundParameterType : compoundParameterTypeList)
            compoundParameterList.add(new CompoundParameter(compoundParameterType, pythonNativeMapper,
                    importCollector));
    }

    public Iterable<CompoundParameter> getList()
    {
        return compoundParameterList;
    }

    public static class CompoundParameter
    {
        public CompoundParameter(Parameter parameter, PythonNativeMapper pythonNativeMapper,
                ImportCollector importCollector) throws ZserioEmitException
        {
            name = parameter.getName();
            final PythonNativeType nativeType = pythonNativeMapper.getPythonType(parameter.getTypeReference());
            importCollector.importType(nativeType);
            pythonTypeName = nativeType.getFullName();
            getterName = AccessorNameFormatter.getGetterName(parameter);
        }

        public String getName()
        {
            return name;
        }

        public String getPythonTypeName()
        {
            return pythonTypeName;
        }

        public String getGetterName()
        {
            return getterName;
        }

        private final String name;
        private final String pythonTypeName;
        private final String getterName;
    }

    private final List<CompoundParameter> compoundParameterList;
}
