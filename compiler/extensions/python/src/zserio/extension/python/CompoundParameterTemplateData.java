package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Parameter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

public final class CompoundParameterTemplateData
{
    public CompoundParameterTemplateData(TemplateDataContext context, CompoundType compoundType,
            ImportCollector importCollector) throws ZserioExtensionException
    {
        final List<Parameter> compoundParameterTypeList = compoundType.getTypeParameters();
        compoundParameterList = new ArrayList<CompoundParameter>(compoundParameterTypeList.size());
        for (Parameter compoundParameterType : compoundParameterTypeList)
        {
            compoundParameterList.add(new CompoundParameter(
                    context, compoundParameterType, importCollector));
        }
    }

    public Iterable<CompoundParameter> getList()
    {
        return compoundParameterList;
    }

    public static class CompoundParameter
    {
        public CompoundParameter(TemplateDataContext context, Parameter parameter,
                ImportCollector importCollector) throws ZserioExtensionException
        {
            name = parameter.getName();
            final PythonNativeType nativeType = context.getPythonNativeMapper().getPythonType(
                    parameter.getTypeReference());
            importCollector.importType(nativeType);
            pythonTypeName = nativeType.getFullName();
            getterName = AccessorNameFormatter.getGetterName(parameter);
            propertyName = AccessorNameFormatter.getPropertyName(parameter);
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

        public String getPropertyName()
        {
            return propertyName;
        }

        private final String name;
        private final String pythonTypeName;
        private final String getterName;
        private final String propertyName;
    }

    private final List<CompoundParameter> compoundParameterList;
}
