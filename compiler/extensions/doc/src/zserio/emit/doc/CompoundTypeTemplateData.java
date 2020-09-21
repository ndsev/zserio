package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayInstantiation;
import zserio.ast.CompoundType;
import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Parameter;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.TypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.emit.common.ZserioEmitException;

public class CompoundTypeTemplateData extends DocTemplateData
{
    public CompoundTypeTemplateData(TemplateDataContext context, CompoundType compoundType)
            throws ZserioEmitException
    {
        super(context, compoundType, compoundType.getName());

        for (Parameter parameter : compoundType.getTypeParameters())
            parameters.add(new LinkedType(parameter.getTypeReference().getType()));

        for (Field field : compoundType.getFields())
            fields.add(new FieldTemplateData(field, context.getDocExpressionFormatter()));

        for (Function function : compoundType.getFunctions())
            functions.add(new FunctionTemplateData(function, context.getDocExpressionFormatter()));
    }

    public Iterable<LinkedType> getParameters()
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

    public static class FieldTemplateData
    {
        public FieldTemplateData(Field field, DocExpressionFormatter docExpressionFormatter)
                throws ZserioEmitException
        {
            name = field.getName();
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            linkedType = new LinkedType(fieldTypeInstantiation);
            initArguments(fieldTypeInstantiation, docExpressionFormatter);
            docComment = new DocCommentTemplateData(field.getDocComment());
            isVirtual = field.getIsVirtual();
            isAutoOptional = field.isOptional() && field.getOptionalClauseExpr() == null;
            alignmentExpression = docExpressionFormatter.formatExpression(field.getAlignmentExpr());
            constraintExpression = docExpressionFormatter.formatExpression(field.getConstraintExpr());
            if (fieldTypeInstantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)fieldTypeInstantiation;
                isArrayImplicit = arrayInstantiation.isImplicit();

                arrayRange = "[" +
                            docExpressionFormatter.formatExpression(arrayInstantiation.getLengthExpression()) +
                            "]";
            }
            else
            {
                isArrayImplicit = false;
                arrayRange = "";
            }
            initializerExpression = docExpressionFormatter.formatExpression(field.getInitializerExpr());
            optionalClauseExpression = docExpressionFormatter.formatExpression(field.getOptionalClauseExpr());
            offsetExpression = docExpressionFormatter.formatExpression(field.getOffsetExpr());
            sqlConstraintExpression = docExpressionFormatter.formatExpression(
                    field.getSqlConstraint() != null ? field.getSqlConstraint().getConstraintExpr() : null);
        }

        public String getName()
        {
            return name;
        }

        public LinkedType getLinkedType()
        {
            return linkedType;
        }

        public Iterable<String> getArguments()
        {
            return arguments;
        }

        public DocCommentTemplateData getDocComment()
        {
            return docComment;
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

        public String getSqlConstraintException()
        {
            return sqlConstraintExpression;
        }

        private void initArguments(TypeInstantiation fieldTypeInstantiation,
                DocExpressionFormatter docExpressionFormatter) throws ZserioEmitException
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
                    arguments.add(docExpressionFormatter.formatExpression(
                            instantiatedParameter.getArgumentExpression()));
                }
            }
        }

        private final String name;
        private final LinkedType linkedType;
        private final List<String> arguments = new ArrayList<String>();
        private final DocCommentTemplateData docComment;
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
        public FunctionTemplateData(Function function, DocExpressionFormatter docExpressionFormatter)
                throws ZserioEmitException
        {
            name = function.getName();
            returnTypeName = function.getReturnTypeReference().getType().getName();
            resultExpression = docExpressionFormatter.formatExpression(function.getResultExpression());
        }

        public String getName()
        {
            return name;
        }

        public String getReturnTypeName() throws ZserioEmitException
        {
            return returnTypeName;
        }

        public String getResultExpression() throws ZserioEmitException
        {
            return resultExpression;
        }

        private final String name;
        private final String returnTypeName;
        private final String resultExpression;
    }

    private final List<LinkedType> parameters = new ArrayList<LinkedType>();
    private final List<FieldTemplateData> fields = new ArrayList<FieldTemplateData>();
    private final List<FunctionTemplateData> functions = new ArrayList<FunctionTemplateData>();
};
