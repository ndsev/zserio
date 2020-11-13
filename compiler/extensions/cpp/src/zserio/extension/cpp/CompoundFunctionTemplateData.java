package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.TypeReference;
import zserio.ast.Function;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

public class CompoundFunctionTemplateData
{
    public CompoundFunctionTemplateData(CppNativeMapper cppNativeMapper, CompoundType compoundType,
            ExpressionFormatter cppExpressionFormatter, IncludeCollector includeCollector)
                    throws ZserioExtensionException
    {
        compoundFunctionList = new ArrayList<CompoundFunction>();
        final Iterable<Function> functionList = compoundType.getFunctions();
        for (Function function : functionList)
        {
            compoundFunctionList.add(new CompoundFunction(function, cppNativeMapper,
                    cppExpressionFormatter, includeCollector));
        }
    }

    public Iterable<CompoundFunction> getList()
    {
        return compoundFunctionList;
    }

    public static class CompoundFunction
    {
        public CompoundFunction(Function function, CppNativeMapper cppNativeMapper,
                ExpressionFormatter cppExpressionFormatter, IncludeCollector includeCollector)
                        throws ZserioExtensionException
        {
            final TypeReference returnTypeReference = function.getReturnTypeReference();
            final CppNativeType returnNativeType = cppNativeMapper.getCppType(returnTypeReference);
            returnTypeName = returnNativeType.getFullName();
            name = AccessorNameFormatter.getFunctionName(function);
            resultExpression = cppExpressionFormatter.formatGetter(function.getResultExpression());
            addIncludes(includeCollector, returnNativeType);
        }

        public String getReturnTypeName()
        {
            return returnTypeName;
        }

        public String getName()
        {
            return name;
        }

        public String getResultExpression()
        {
            return resultExpression;
        }

        /**
         * Adds includes for a type returned from function that is defined by a parent.
         * <p>
         * An example where this is needed:</p>
         * <pre>
         * {@code
         * enum bit:3 Color
         * {
         *     RED,
         *     GREEN,
         *     BLUE
         * };
         *
         * Parent
         * {
         *     Color color;
         *     Child child;
         * };
         *
         * Child
         * {
         *     uint8 someData;
         *
         *     function Color foo()
         *     {
         *         // Parent is only forward-declared in Child.h as it can't be #included directly.
         *         // This makes Color undefined unless it's explicitly included.
         *         return Parent.color;
         *     }
         * }
         * </pre>
         */
        private void addIncludes(IncludeCollector includeCollector, CppNativeType nativeType)
        {
            includeCollector.addHeaderIncludesForType(nativeType);
        }

        private final String returnTypeName;
        private final String name;
        private final String resultExpression;
    }

    private final List<CompoundFunction> compoundFunctionList;
}
