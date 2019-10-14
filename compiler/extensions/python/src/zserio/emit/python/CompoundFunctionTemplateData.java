package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Function;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public final class CompoundFunctionTemplateData
{
    public CompoundFunctionTemplateData(CompoundType compoundType,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioEmitException
    {
        compoundFunctionList = new ArrayList<CompoundFunction>();
        final Iterable<Function> functionList = compoundType.getFunctions();
        for (Function function : functionList)
            compoundFunctionList.add(new CompoundFunction(function, pythonExpressionFormatter));
    }

    public Iterable<CompoundFunction> getList()
    {
        return compoundFunctionList;
    }

    public static class CompoundFunction
    {
        public CompoundFunction(Function function, ExpressionFormatter pythonExpressionFormatter)
                throws ZserioEmitException
        {
            name = AccessorNameFormatter.getFunctionName(function);
            resultExpression = pythonExpressionFormatter.formatGetter(function.getResultExpression());
        }

        public String getName()
        {
            return name;
        }

        public String getResultExpression()
        {
            return resultExpression;
        }

        private final String name;
        private final String resultExpression;
    }

    private final List<CompoundFunction> compoundFunctionList;
}
