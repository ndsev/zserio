package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayInstantiation;
import zserio.ast.CompoundType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Parameter;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.TypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.SqlConstraint;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class CompoundTypeTemplateData extends DocTemplateData
{
    public CompoundTypeTemplateData(TemplateDataContext context, CompoundType compoundType)
            throws ZserioEmitException
    {
        super(context, compoundType, compoundType.getName());

        final SymbolTemplateDataMapper symbolTemplateDataMapper = context.getSymbolTemplateDataMapper();
        for (Parameter parameter : compoundType.getTypeParameters())
            parameters.add(new ParameterTemplateData(parameter, symbolTemplateDataMapper));

        final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();
        for (Field field : compoundType.getFields())
            fields.add(new FieldTemplateData(compoundType, field, docExpressionFormatter,
                    symbolTemplateDataMapper));

        for (Function function : compoundType.getFunctions())
            functions.add(new FunctionTemplateData(compoundType, function, docExpressionFormatter,
                    symbolTemplateDataMapper));
    }

    public Iterable<ParameterTemplateData> getParameters()
    {
        return parameters;
    }

    public Iterable<FieldTemplateData> getFields()
    {
        return fields;
    }

    public Iterable<FunctionTemplateData> getFunctions()
    {
        return functions;
    }

    public static class ParameterTemplateData
    {
        public ParameterTemplateData(Parameter parameter, SymbolTemplateDataMapper symbolTemplateDataMapper)
                throws ZserioEmitException
        {
            symbol = symbolTemplateDataMapper.getSymbol(parameter.getTypeReference().getType());
            name = parameter.getName();
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public String getName()
        {
            return name;
        }

        private final SymbolTemplateData symbol;
        private final String name;
    }

    public static class FieldTemplateData
    {
        public FieldTemplateData(CompoundType compoundType, Field field,
                ExpressionFormatter docExpressionFormatter, SymbolTemplateDataMapper symbolTemplateDataMapper)
                throws ZserioEmitException
        {
            name = field.getName();
            anchorName = DocEmitterTools.getAnchorName(compoundType, field.getName());
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            symbol = symbolTemplateDataMapper.getSymbol(fieldTypeInstantiation);
            initArguments(fieldTypeInstantiation, docExpressionFormatter);
            docComments = new DocCommentsTemplateData(field.getDocComments());
            isVirtual = field.getIsVirtual();
            isAutoOptional = field.isOptional() && field.getOptionalClauseExpr() == null;
            alignmentExpression = formatExpression(field.getAlignmentExpr(), docExpressionFormatter);
            constraintExpression = formatExpression(field.getConstraintExpr(), docExpressionFormatter);
            if (fieldTypeInstantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)fieldTypeInstantiation;
                isArrayImplicit = arrayInstantiation.isImplicit();
                arrayRange = "[" +
                        formatExpression(arrayInstantiation.getLengthExpression(), docExpressionFormatter) +
                        "]";
            }
            else
            {
                isArrayImplicit = false;
                arrayRange = "";
            }
            initializerExpression = formatExpression(field.getInitializerExpr(), docExpressionFormatter);
            optionalClauseExpression = formatExpression(field.getOptionalClauseExpr(), docExpressionFormatter);
            offsetExpression = formatExpression(field.getOffsetExpr(), docExpressionFormatter);
            final SqlConstraint sqlConstraint = field.getSqlConstraint();
            sqlConstraintExpression = (sqlConstraint == null) ? "" :
                formatExpression(sqlConstraint.getConstraintExpr(), docExpressionFormatter);
        }

        public String getName()
        {
            return name;
        }

        public String getAnchorName()
        {
            return anchorName;
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public Iterable<String> getArguments()
        {
            return arguments;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        public boolean getIsVirtual()
        {
            return isVirtual;
        }

        public boolean getIsAutoOptional()
        {
            return isAutoOptional;
        }

        public String getAlignmentExpression()
        {
            return alignmentExpression;
        }

        public String getConstraintExpression()
        {
            return constraintExpression;
        }

        public boolean getIsArrayImplicit()
        {
            return isArrayImplicit;
        }

        public String getArrayRange()
        {
            return arrayRange;
        }

        public String getInitializerExpression()
        {
            return initializerExpression;
        }

        public String getOptionalClauseExpression()
        {
            return optionalClauseExpression;
        }

        public String getOffsetExpression()
        {
            return offsetExpression;
        }

        public String getSqlConstraintExpression()
        {
            return sqlConstraintExpression;
        }

        private void initArguments(TypeInstantiation fieldTypeInstantiation,
                ExpressionFormatter docExpressionFormatter) throws ZserioEmitException
        {
            final TypeInstantiation typeInstantiation = (fieldTypeInstantiation instanceof ArrayInstantiation)
                    ? ((ArrayInstantiation)fieldTypeInstantiation).getElementTypeInstantiation()
                    : fieldTypeInstantiation;
            if (typeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                final ParameterizedTypeInstantiation parameterizedTypeInstantiation =
                        (ParameterizedTypeInstantiation)typeInstantiation;
                for (InstantiatedParameter instantiatedParameter :
                    parameterizedTypeInstantiation.getInstantiatedParameters())
                {
                    arguments.add(docExpressionFormatter.formatGetter(
                            instantiatedParameter.getArgumentExpression()));
                }
            }
        }

        private String formatExpression(Expression expression, ExpressionFormatter docExpressionFormatter)
                throws ZserioEmitException
        {
            return (expression == null) ? "" : docExpressionFormatter.formatGetter(expression);
        }

        private final String name;
        private final String anchorName;
        private final SymbolTemplateData symbol;
        private final List<String> arguments = new ArrayList<String>();
        private final DocCommentsTemplateData docComments;
        private final boolean isVirtual;
        private final boolean isAutoOptional;
        private final String alignmentExpression;
        private final String constraintExpression;
        private final boolean isArrayImplicit;
        private final String arrayRange;
        private final String initializerExpression;
        private final String optionalClauseExpression;
        private final String offsetExpression;
        private final String sqlConstraintExpression;
    }

    public static class FunctionTemplateData
    {
        public FunctionTemplateData(CompoundType compoundType, Function function,
                ExpressionFormatter docExpressionFormatter, SymbolTemplateDataMapper symbolTemplateDataMapper)
                        throws ZserioEmitException
        {
            name = function.getName();
            anchorName = DocEmitterTools.getAnchorName(compoundType, function.getName());
            returnSymbol = symbolTemplateDataMapper.getSymbol(function.getReturnTypeReference().getType());
            resultExpression = docExpressionFormatter.formatGetter(function.getResultExpression());
            docComments = new DocCommentsTemplateData(function.getDocComments());
        }

        public String getName()
        {
            return name;
        }

        public String getAnchorName()
        {
            return anchorName;
        }

        public SymbolTemplateData getReturnSymbol()
        {
            return returnSymbol;
        }

        public String getResultExpression()
        {
            return resultExpression;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String name;
        private final String anchorName;
        private final SymbolTemplateData returnSymbol;
        private final String resultExpression;
        private final DocCommentsTemplateData docComments;
    }

    private final List<ParameterTemplateData> parameters = new ArrayList<ParameterTemplateData>();
    private final List<FieldTemplateData> fields = new ArrayList<FieldTemplateData>();
    private final List<FunctionTemplateData> functions = new ArrayList<FunctionTemplateData>();
};
