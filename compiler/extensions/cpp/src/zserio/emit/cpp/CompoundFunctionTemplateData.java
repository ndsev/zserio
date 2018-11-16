package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.ast.FunctionType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.CppNativeType;

public class CompoundFunctionTemplateData
{
    public CompoundFunctionTemplateData(CppNativeTypeMapper cppNativeTypeMapper, CompoundType compoundType,
            ExpressionFormatter cppExpressionFormatter, IncludeCollector includeCollector)
                    throws ZserioEmitException
    {
        compoundFunctionList = new ArrayList<CompoundFunction>();
        final Iterable<FunctionType> compoundFunctionTypeList = compoundType.getFunctions();
        for (FunctionType compoundFunctionType : compoundFunctionTypeList)
            compoundFunctionList.add(new CompoundFunction(compoundFunctionType, cppNativeTypeMapper,
                                                          cppExpressionFormatter, includeCollector));
    }

    public Iterable<CompoundFunction> getList()
    {
        return compoundFunctionList;
    }

    public static class CompoundFunction
    {
        public CompoundFunction(FunctionType functionType, CppNativeTypeMapper cppNativeTypeMapper,
                ExpressionFormatter cppExpressionFormatter, IncludeCollector includeCollector)
                        throws ZserioEmitException
        {
            final ZserioType returnZserioType = functionType.getReturnType();
            final CppNativeType returnNativeType = cppNativeTypeMapper.getCppType(returnZserioType);
            returnTypeName = returnNativeType.getFullName();
            zserioReturnTypeName = ZserioTypeUtil.getFullName(returnZserioType);
            name = AccessorNameFormatter.getFunctionName(functionType);
            zserioName = functionType.getName();
            resultExpression = cppExpressionFormatter.formatGetter(functionType.getResultExpression());
            addIncludes(includeCollector, returnNativeType);
        }

        public String getReturnTypeName()
        {
            return returnTypeName;
        }

        public String getZserioReturnTypeName()
        {
            return zserioReturnTypeName;
        }

        public String getName()
        {
            return name;
        }

        public String getZserioName()
        {
            return zserioName;
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
        private final String zserioReturnTypeName;
        private final String name;
        private final String zserioName;
        private final String resultExpression;
    }

    private final List<CompoundFunction>    compoundFunctionList;
}
