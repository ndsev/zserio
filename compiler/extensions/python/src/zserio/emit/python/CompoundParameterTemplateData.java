package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Parameter;
import zserio.emit.common.ZserioEmitException;

public final class CompoundParameterTemplateData
{
    public CompoundParameterTemplateData(CompoundType compoundType) throws ZserioEmitException
    {
        final List<Parameter> compoundParameterTypeList = compoundType.getParameters();
        compoundParameterList = new ArrayList<CompoundParameter>(compoundParameterTypeList.size());
        for (Parameter compoundParameterType : compoundParameterTypeList)
            compoundParameterList.add(new CompoundParameter(compoundParameterType));
    }

    public Iterable<CompoundParameter> getList()
    {
        return compoundParameterList;
    }

    public static class CompoundParameter
    {
        public CompoundParameter(Parameter parameter) throws ZserioEmitException
        {
            name = parameter.getName();
            getterName = AccessorNameFormatter.getGetterName(parameter);
        }

        public String getName()
        {
            return name;
        }

        public String getGetterName()
        {
            return getterName;
        }

        private final String name;
        private final String getterName;
    }

    private final List<CompoundParameter> compoundParameterList;
}
