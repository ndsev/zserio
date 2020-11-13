package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Function;
import zserio.ast.TypeReference;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

public final class CompoundFunctionTemplateData
{
    public CompoundFunctionTemplateData(CompoundType compoundType, PythonNativeMapper pythonNativeMapper,
            ExpressionFormatter pythonExpressionFormatter, ImportCollector importCollector)
                    throws ZserioExtensionException
    {
        compoundFunctionList = new ArrayList<CompoundFunction>();
        final Iterable<Function> functionList = compoundType.getFunctions();
        for (Function function : functionList)
            compoundFunctionList.add(new CompoundFunction(function, pythonNativeMapper,
                    pythonExpressionFormatter, importCollector));
    }

    public Iterable<CompoundFunction> getList()
    {
        return compoundFunctionList;
    }

    public static class CompoundFunction
    {
        public CompoundFunction(Function function, PythonNativeMapper pythonNativeMapper,
                ExpressionFormatter pythonExpressionFormatter, ImportCollector importCollector)
                        throws ZserioExtensionException
        {
            final TypeReference returnTypeReference = function.getReturnTypeReference();
            final PythonNativeType nativeType = pythonNativeMapper.getPythonType(returnTypeReference);
            importCollector.importType(nativeType);

            name = AccessorNameFormatter.getFunctionName(function);
            returnPythonTypeName = nativeType.getFullName();
            resultExpression = pythonExpressionFormatter.formatGetter(function.getResultExpression());
        }

        public String getReturnPythonTypeName()
        {
            return returnPythonTypeName;
        }

        public String getName()
        {
            return name;
        }

        public String getResultExpression()
        {
            return resultExpression;
        }

        private final String returnPythonTypeName;
        private final String name;
        private final String resultExpression;
    }

    private final List<CompoundFunction> compoundFunctionList;
}
