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

        for (Parameter parameter : compoundType.getTypeParameters())
            parameters.add(new ParameterTemplateData(context, parameter));

        for (Field field : compoundType.getFields())
            fields.add(new FieldTemplateData(context, compoundType, field));

        for (Function function : compoundType.getFunctions())
            functions.add(new FunctionTemplateData(context, compoundType, function));
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
        public ParameterTemplateData(TemplateDataContext context, Parameter parameter)
        {
            symbol = SymbolTemplateDataCreator.createData(context, parameter.getTypeReference().getType());
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
        public FieldTemplateData(TemplateDataContext context, CompoundType compoundType, Field field)
                throws ZserioEmitException
        {
            symbol = SymbolTemplateDataCreator.createData(context, compoundType, field);
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            typeSymbol = SymbolTemplateDataCreator.createData(context, fieldTypeInstantiation);
            final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();
            initArguments(fieldTypeInstantiation, docExpressionFormatter);
            docComments = new DocCommentsTemplateData(context, field.getDocComments());
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

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public SymbolTemplateData getTypeSymbol()
        {
            return typeSymbol;
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

        private final SymbolTemplateData symbol;
        private final SymbolTemplateData typeSymbol;
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
        public FunctionTemplateData(TemplateDataContext context, CompoundType compoundType, Function function)
                throws ZserioEmitException
        {
            symbol = SymbolTemplateDataCreator.createData(context, compoundType, function);
            returnSymbol = SymbolTemplateDataCreator.createData(context,
                    function.getReturnTypeReference().getType());
            final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();
            resultExpression = docExpressionFormatter.formatGetter(function.getResultExpression());
            docComments = new DocCommentsTemplateData(context, function.getDocComments());
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
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

        private final SymbolTemplateData symbol;
        private final SymbolTemplateData returnSymbol;
        private final String resultExpression;
        private final DocCommentsTemplateData docComments;
    }

    private final List<ParameterTemplateData> parameters = new ArrayList<ParameterTemplateData>();
    private final List<FieldTemplateData> fields = new ArrayList<FieldTemplateData>();
    private final List<FunctionTemplateData> functions = new ArrayList<FunctionTemplateData>();
};
