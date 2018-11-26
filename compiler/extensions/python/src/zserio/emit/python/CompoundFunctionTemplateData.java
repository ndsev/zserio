package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.FunctionType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public final class CompoundFunctionTemplateData
{
    public CompoundFunctionTemplateData(CompoundType compoundType,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioEmitException
    {
        compoundFunctionList = new ArrayList<CompoundFunction>();
        final Iterable<FunctionType> compoundFunctionTypeList = compoundType.getFunctions();
        for (FunctionType compoundFunctionType : compoundFunctionTypeList)
            compoundFunctionList.add(new CompoundFunction(compoundFunctionType, pythonExpressionFormatter));
    }

    public Iterable<CompoundFunction> getList()
    {
        return compoundFunctionList;
    }

    public static class CompoundFunction
    {
        public CompoundFunction(FunctionType functionType, ExpressionFormatter pythonExpressionFormatter)
                throws ZserioEmitException
        {
            name = AccessorNameFormatter.getFunctionName(functionType);
            resultExpression = pythonExpressionFormatter.formatGetter(functionType.getResultExpression());
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
