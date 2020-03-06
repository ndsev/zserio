package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceCaseExpression;
import zserio.ast.ChoiceDefault;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.DocComment;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.PubsubType;
import zserio.ast.ServiceType;
import zserio.ast.ZserioType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.StructureType;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.ast.UnionType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class CompoundEmitter extends DefaultHtmlEmitter
{
    private CompoundType compound;
    private DocCommentTemplateData docCommentTemplateData;
    private String docPath;
    private boolean withSvgDiagrams;
    private UsedByCollector usedByCollector;

    private final List<FieldEmitter> fields = new ArrayList<FieldEmitter>();

    private final List<FunctionEmitter> functions = new ArrayList<FunctionEmitter>();

    public CompoundEmitter(String outputPath, boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        super(outputPath);
        docPath = outputPath;
        directory = new File(directory, CONTENT_FOLDER);
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
    }

    public CompoundEmitter(CompoundType cc)
    {
        this.compound = cc;
    }

    public String getName()
    {
        return compound == null ? "" : compound.getName();
    }

    public LinkedType getLinkedType() throws ZserioEmitException
    {
        if (compound == null)
            return null;

        ZserioType type = compound;
        LinkedType linkedType = new LinkedType(type);
        return linkedType;
    }

    public String getSqlConstraint() throws ZserioEmitException
    {
        String result = "";
        if (compound instanceof SqlTableType)
        {
            final SqlConstraint sqlConstraint = ((SqlTableType) compound).getSqlConstraint();
            if (sqlConstraint != null)
                result = getExpressionFormatter().formatGetter(sqlConstraint.getConstraintExpr());
        }

        return result;
    }

    public static class FunctionEmitter
    {
        public FunctionEmitter(Function fctn, ExpressionFormatter expressionFormatter)
        {
            function = fctn;
            this.expressionFormatter = expressionFormatter;
        }

        public Function getFuntionType()
        {
            return function;
        }

        public String getReturnTypeName() throws ZserioEmitException
        {
            String returnTypeName = TypeNameEmitter.getTypeName(function.getReturnTypeReference().getType());
            return returnTypeName;
        }

        public String getResult() throws ZserioEmitException
        {
            return expressionFormatter.formatGetter(function.getResultExpression());
        }

        private final ExpressionFormatter expressionFormatter;
        private final Function function;
    }

    public FieldEmitter getFieldEmitter( Field f ) throws ZserioEmitException
    {
        return new FieldEmitter(f, getExpressionFormatter());
    }

    public static class FieldEmitter
    {
        private final TypeNameEmitter tne;
        private final Field field;
        private final DocCommentTemplateData docCommentTemplateData;

        public FieldEmitter(Field f, ExpressionFormatter expressionFormatter) throws ZserioEmitException
        {
            this.field = f;
            tne = new TypeNameEmitter(expressionFormatter);
            docCommentTemplateData = new DocCommentTemplateData(field.getDocComment());
        }

        public String getName()
        {
            return field.getName();
        }

        public boolean getIsVirtual()
        {
            return field.getIsVirtual();
        }

        public boolean getIsAutoOptional()
        {
            return field.isOptional() && field.getOptionalClauseExpr() == null;
        }

        public boolean getIsArrayImplicit()
        {
            final TypeInstantiation instantiation = field.getTypeInstantiation();

            return (instantiation instanceof ArrayInstantiation) ?
                    ((ArrayInstantiation)instantiation).isImplicit() : false;
        }

        public LinkedType getType() throws ZserioEmitException
        {
            LinkedType linkedType = new LinkedType(field.getTypeInstantiation());
            return linkedType;
        }

        public String getConstraint() throws ZserioEmitException
        {
            return tne.getConstraint(field);
        }

        public String getArrayRange() throws ZserioEmitException
        {
            return tne.getArrayRange(field);
        }

        public String getInitializer() throws ZserioEmitException
        {
            return tne.getInitializer(field);
        }

        public String getOptionalClause() throws ZserioEmitException
        {
            return tne.getOptionalClause(field);
        }

        public String getOffset() throws ZserioEmitException
        {
            return tne.getOffset(field);
        }

        public String getSqlConstraint() throws ZserioEmitException
        {
          return tne.getSqlConstraint(field);
        }

        public DocCommentTemplateData getDocComment()
        {
            return docCommentTemplateData;
        }

        public boolean getIsDeprecated()
        {
            return docCommentTemplateData.getIsDeprecated();
        }

        public List<Expression> getArguments()
        {
            TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            if (typeInstantiation instanceof ArrayInstantiation)
                typeInstantiation = ((ArrayInstantiation)typeInstantiation).getElementTypeInstantiation();

            final List<Expression> arguments = new ArrayList<Expression>();
            if (typeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                for (InstantiatedParameter instantiatedParameter :
                        ((ParameterizedTypeInstantiation)typeInstantiation).getInstantiatedParameters())
                {
                    arguments.add(instantiatedParameter.getArgumentExpression());
                }
            }

            return arguments;
        }

        public boolean getHasAlignment()
        {
            return field.getAlignmentExpr() != null;
        }

        public int getAlignmentValue()
        {
            return field.getAlignmentExpr().getIntegerValue().intValue();
        }


    } // class FielEmitter




    public static class FieldLinkedType
    {
        private final Field field;
        private final LinkedType linkedType;

        public FieldLinkedType( Field field ) throws ZserioEmitException
        {
            this.field = field;
            this.linkedType = new LinkedType(field.getTypeInstantiation());
        }

        public Field getField()
        {
            return field;
        }

        public LinkedType getLinkedType()
        {
            return linkedType;
        }

    }; // class FieldLinkedType

    public void emit(CompoundType compnd) throws ZserioEmitException
    {
        this.compound = compnd;
        docCommentTemplateData = new DocCommentTemplateData(compnd.getDocComment());
        functions.clear();
        for (Function fctn : compnd.getFunctions())
        {
            FunctionEmitter fe = new FunctionEmitter(fctn, getExpressionFormatter());
            functions.add(fe);
        }
        containers.clear();
        for (CompoundType compound : usedByCollector.getUsedByTypes(compnd, CompoundType.class))
        {
            CompoundEmitter ce = new CompoundEmitter(compound);
            containers.add(ce);
        }
        protocols.clear();
        for (ServiceType service : usedByCollector.getUsedByTypes(compnd, ServiceType.class))
        {
            protocols.add(new LinkedType(service));
        }
        for (PubsubType pubsub : usedByCollector.getUsedByTypes(compnd, PubsubType.class))
        {
            protocols.add(new LinkedType(pubsub));
        }

        if (compnd instanceof ChoiceType)
            emitChoiceType();
        else
            emitCompoundType();
    }

    private void emitChoiceType() throws ZserioEmitException
    {
        try
        {
            Template tpl = cfg.getTemplate("doc/choice.html.ftl");
            setCurrentFolder(CONTENT_FOLDER);
            openOutputFileFromType(compound);
            tpl.process(this, writer);
        }
        catch (IOException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        catch (TemplateException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }

    private void emitCompoundType() throws ZserioEmitException
    {
        fields.clear();
        for (Field field : compound.getFields())
        {
            FieldEmitter fe = new FieldEmitter(field, getExpressionFormatter());
            fields.add(fe);
        }

        try
        {
            Template tpl = cfg.getTemplate("doc/compound.html.ftl");
            setCurrentFolder(CONTENT_FOLDER);
            openOutputFileFromType(compound);
            tpl.process(this, writer);
            writer.close();
        }
        catch (IOException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        catch (TemplateException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }

    public String getVirtualTableUsing()
    {
        return ( compound instanceof SqlTableType) ? ((SqlTableType) compound).getVirtualTableUsingString() : null;
    }

    public String getCategoryPlainText()
    {
        if (compound instanceof StructureType)
        {
            return "Structure";
        }
        else if (compound instanceof ChoiceType)
        {
            return "Choice";
        }
        else if (compound instanceof UnionType)
        {
            return "Union";
        }
        else if (compound instanceof SqlDatabaseType)
        {
            return "SQL Database";
        }
        else if (compound instanceof SqlTableType)
        {
            return "SQL Table";
        }

        throw new RuntimeException("unknown category "
                + compound.getClass().getName());
    }

    public String getCategoryKeyword()
    {
        if (compound instanceof StructureType)
        {
            return "struct ";
        }
        else if (compound instanceof ChoiceType)
        {
            return "choice ";
        }
        else if (compound instanceof UnionType)
        {
            return "union ";
        }
        else if (compound instanceof SqlDatabaseType)
        {
            return "sql_database ";
        }
        else if (compound instanceof SqlTableType)
        {
            return "sql_table ";
        }

        throw new RuntimeException("unknown category "
                + compound.getClass().getName());
    }

    @Override
    public String getPackageName()
    {
        return compound.getPackage().getPackageName().toString();
    }

    public CompoundType getType()
    {
        return compound;
    }




    /***************************************************************************
     * Section for choice-types
     * since there is no separate choice-emitter
     *--------------------------------------------------------------------------
     * @throws ZserioEmitException
     */

    public ChoiceData getChoiceData() throws ZserioEmitException
    {
        return new ChoiceData((ChoiceType)compound);
    }

    public DocCommentTemplateData getDocComment()
    {
        return docCommentTemplateData;
    }

    public boolean getIsDeprecated()
    {
        if (docCommentTemplateData == null)
            throw new RuntimeException("getIsDeprecated() called before emit()");

        return docCommentTemplateData.getIsDeprecated();
    }

    public List<FieldEmitter> getFields()
    {
        return fields;
    }

    public List<FunctionEmitter> getFunctions()
    {
        return functions;
    }

    public LinkedType toLinkedType(AstNode node) throws ZserioEmitException
    {
        LinkedType linkedType = new LinkedType(node);
        return linkedType;
    }

    public DocCommentTemplateData getFieldDocComment(Field f) throws ZserioEmitException
    {
        return new DocCommentTemplateData(f.getDocComment());
    }

    public DocCommentTemplateData getCaseDocComment(DocComment docComment) throws ZserioEmitException
    {
        return new DocCommentTemplateData(docComment);
    }

    public String emitExpression(Expression expr) throws ZserioEmitException
    {
        return getExpressionFormatter().formatGetter(expr);
    }

    public String getCollaborationDiagramSvgFileName() throws ZserioEmitException
    {
        return (withSvgDiagrams) ? DocEmitterTools.getTypeCollaborationSvgUrl(docPath, compound) : null;
    }

    public class ChoiceData
    {
        public ChoiceData(ChoiceType choiceType) throws ZserioEmitException
        {
            final Expression selectorExpression = choiceType.getSelectorExpression();
            selector = getExpressionFormatter().formatGetter(selectorExpression);

            caseMemberList = new ArrayList<CaseMember>();
            final Iterable<ChoiceCase> choiceCases = choiceType.getChoiceCases();
            for (ChoiceCase choiceCase : choiceCases)
                caseMemberList.add(new CaseMember(choiceCase, selectorExpression));

            final ChoiceDefault choiceDefault = choiceType.getChoiceDefault();
            defaultMember = (choiceDefault != null) ? new DefaultMember(choiceDefault) : null;
        }

        public String getSelector()
        {
            return selector;
        }

        public Iterable<CaseMember> getCaseMemberList()
        {
            return caseMemberList;
        }

        public DefaultMember getDefaultMember()
        {
            return defaultMember;
        }

        public class CaseMember
        {
            public CaseMember(ChoiceCase choiceCase, Expression selectorExpression) throws ZserioEmitException
            {
                caseList = new ArrayList<Case>();
                final Iterable<ChoiceCaseExpression> caseExpressions = choiceCase.getExpressions();
                for (ChoiceCaseExpression caseExpression : caseExpressions)
                    caseList.add(new Case(caseExpression.getExpression(), caseExpression.getDocComment(),
                            selectorExpression));

                compoundField = choiceCase.getField();
            }

            public Iterable<Case> getCaseList()
            {
                return caseList;
            }

            public Field getCompoundField()
            {
                return compoundField;
            }

            private final List<Case> caseList;
            private final Field compoundField;
        }

        public class Case
        {
            public Case(Expression caseExpression, DocComment docComment, Expression selectorExpression)
                    throws ZserioEmitException
            {
                expression = getExpressionFormatter().formatGetter(caseExpression);
                docCommentData = new DocCommentTemplateData(docComment);

                final Object caseExpressionObject = caseExpression.getExprSymbolObject();
                final ZserioType selectorExpressionType = selectorExpression.getExprZserioType();
                if (caseExpressionObject instanceof EnumItem  && selectorExpressionType instanceof EnumType)
                {
                    seeLink = new CaseSeeLink((EnumItem)caseExpressionObject, (EnumType)selectorExpressionType);
                }
                else if (caseExpressionObject instanceof BitmaskValue &&
                        selectorExpressionType instanceof BitmaskType)
                {
                    seeLink = new CaseSeeLink((BitmaskValue)caseExpressionObject,
                            (BitmaskType)selectorExpressionType);
                }
                else
                {
                    seeLink = null;
                }
            }

            public String getExpression()
            {
                return expression;
            }

            public DocCommentTemplateData getDocComment()
            {
                return docCommentData;
            }

            public CaseSeeLink getSeeLink()
            {
                return seeLink;
            }

            private final String                    expression;
            private final DocCommentTemplateData    docCommentData;
            private final CaseSeeLink               seeLink;
        }

        public class CaseSeeLink
        {
            public CaseSeeLink(EnumItem caseType, EnumType caseTypeOwner) throws ZserioEmitException
            {
                text = caseTypeOwner.getName() + "." + caseType.getName();
                link = DocEmitterTools.getUrlNameFromType(caseTypeOwner) + "#casedef_" + caseType.getName();
            }

            public CaseSeeLink(BitmaskValue caseType, BitmaskType caseTypeOwner) throws ZserioEmitException
            {
                text = caseTypeOwner.getName() + "." + caseType.getName();
                link = DocEmitterTools.getUrlNameFromType(caseTypeOwner) + "#casedef_" + caseType.getName();
            }

            public String getText()
            {
                return text;
            }

            public String getLink()
            {
                return link;
            }

            private final String text;
            private final String link;
        }

        public class DefaultMember
        {
            public DefaultMember(ChoiceDefault choiceDefault) throws ZserioEmitException
            {
                compoundField = choiceDefault.getField();
                docComment = new DocCommentTemplateData(choiceDefault.getDocComment());
            }

            public Field getCompoundField()
            {
                return compoundField;
            }

            public DocCommentTemplateData getDocComment()
            {
                return docComment;
            }

            private final Field compoundField;
            private final DocCommentTemplateData docComment;
        }

        private final String            selector;
        private final List<CaseMember>  caseMemberList;
        private final DefaultMember     defaultMember;
    }
}
