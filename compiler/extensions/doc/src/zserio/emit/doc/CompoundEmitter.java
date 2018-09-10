package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import zserio.ast.ArrayType;
import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceDefault;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ServiceType;
import zserio.ast.ZserioException;
import zserio.ast.ZserioType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FunctionType;
import zserio.ast.StructureType;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.doc.DocCommentToken;
import zserio.emit.common.ExpressionFormatter;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class CompoundEmitter extends DefaultHtmlEmitter
{
    private CompoundType compound;
    private DocCommentTemplateData docCommentTemplateData;
    private String docPath;
    private boolean withSvgDiagrams;

    private final List<FieldEmitter> fields = new ArrayList<FieldEmitter>();

    private final List<FunctionEmitter> functions = new ArrayList<FunctionEmitter>();

    public CompoundEmitter(String outputPath, boolean withSvgDiagrams)
    {
        super(outputPath);
        docPath = outputPath;
        directory = new File(directory, CONTENT_FOLDER);
        this.withSvgDiagrams = withSvgDiagrams;
    }

    public CompoundEmitter(CompoundType cc)
    {
        this.compound = cc;
    }

    public String getName()
    {
        return compound == null ? "" : compound.getName();
    }

    public LinkedType getLinkedType()
    {
        if (compound == null)
            return null;

        ZserioType type = compound;
        type = TypeReference.resolveType(type);
        LinkedType linkedType = new LinkedType(type);
        return linkedType;
    }

    public String getSqlConstraint()
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
        public FunctionEmitter(FunctionType fctn, ExpressionFormatter expressionFormatter)
        {
            function = fctn;
            this.expressionFormatter = expressionFormatter;
        }

        public FunctionType getFuntionType()
        {
            return function;
        }

        public String getReturnTypeName()
        {
            String returnTypeName = TypeNameEmitter.getTypeName(function.getReturnType());
            return returnTypeName;
        }

        public String getResult()
        {
            return expressionFormatter.formatGetter(function.getResultExpression());
        }

        private final ExpressionFormatter expressionFormatter;
        private final FunctionType function;
    }

    public FieldEmitter getFieldEmitter( Field f )
    {
        return new FieldEmitter(f, getExpressionFormatter());
    }

    public static class FieldEmitter
    {
        private TypeNameEmitter tne;
        private final Field field;
        private DocCommentTemplateData docCommentTemplateData;

        public FieldEmitter(Field f, ExpressionFormatter expressionFormatter)
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
            return field.getIsOptional() && field.getOptionalClauseExpr() == null;
        }

        public boolean getIsArrayImplicit()
        {
            ZserioType type = field.getFieldType();
            type = TypeReference.resolveType(type);

            return (type instanceof ArrayType) ? ((ArrayType)type).isImplicit() : false;
        }

        public LinkedType getType()
        {
            ZserioType type = field.getFieldType();
            type = TypeReference.resolveType(type);
            if (type instanceof ArrayType)
                type = TypeReference.resolveType(((ArrayType) type).getElementType());
            LinkedType linkedType = new LinkedType(type);
            return linkedType;
        }

        public String getConstraint()
        {
            return tne.getConstraint(field);
        }

        public String getArrayRange()
        {
            return tne.getArrayRange(field);
        }

        public String getOptionalClause()
        {
            return tne.getOptionalClause(field);
        }

        public String getOffset()
        {
            return tne.getOffset(field);
        }

        public String getSqlConstraint()
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
            ZserioType type = field.getFieldType();
            type = TypeReference.resolveType(type);
            while (type instanceof ArrayType)
                type = TypeReference.resolveType(((ArrayType) type).getElementType());
            if (type instanceof TypeInstantiation)
            {
                TypeInstantiation inst = (TypeInstantiation) type;
                final List<Expression> arguments = new ArrayList<Expression>();
                for (TypeInstantiation.InstantiatedParameter instantiatedParameter :
                        inst.getInstantiatedParameters())
                {
                    arguments.add(instantiatedParameter.getArgumentExpression());
                }

                return arguments;
            }
            else
            {
                return null;
            }
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

        public FieldLinkedType( Field field )
        {
            this.field = field;
            this.linkedType = new LinkedType( field.getFieldType() );
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

    public void emit(CompoundType compnd)
    {
        this.compound = compnd;
        docCommentTemplateData = new DocCommentTemplateData(compnd.getDocComment());
        functions.clear();
        for (FunctionType fctn : compnd.getFunctions())
        {
            FunctionEmitter fe = new FunctionEmitter(fctn, getExpressionFormatter());
            functions.add(fe);
        }
        containers.clear();
        for (CompoundType compound : compnd.getUsedByCompoundList())
        {
            CompoundEmitter ce = new CompoundEmitter(compound);
            containers.add(ce);
        }
        services.clear();
        for (ServiceType service : compnd.getUsedByServiceList())
        {
            services.add(new LinkedType(service));
        }

        if (compnd instanceof ChoiceType)
            emitChoiceType();
        else
            emitCompoundType();
    }

    private void emitChoiceType()
    {
        try
        {
            Template tpl = cfg.getTemplate("doc/choice.html.ftl");
            setCurrentFolder(CONTENT_FOLDER);
            openOutputFileFromType(compound);
            tpl.process(this, writer);
        }
        catch (IOException exc)
        {
            throw new ZserioException(exc);
        }
        catch (TemplateException exc)
        {
            throw new ZserioException(exc);
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }

    private void emitCompoundType()
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
        catch (IOException exc)
        {
            throw new ZserioException(exc);
        }
        catch (TemplateException exc)
        {
            throw new ZserioException(exc);
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
            return "";
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
        return compound.getScope().getPackage().getPackageName();
    }

    public CompoundType getType()
    {
        return compound;
    }




    /***************************************************************************
     * Section for choice-types
     * since there is no separate choice-emitter
     *--------------------------------------------------------------------------
     */

    public ChoiceData getChoiceData()
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

    public LinkedType toLinkedType(ZserioType type1)
    {
        ZserioType type2 = TypeReference.resolveType(type1);
        LinkedType linkedType = new LinkedType(type2);
        return linkedType;
    }

    public DocCommentTemplateData getFieldDocComment(Field f)
    {
        return new DocCommentTemplateData(f.getDocComment());
    }

    public DocCommentTemplateData getCaseDocComment(DocCommentToken token)
    {
        return new DocCommentTemplateData(token);
    }

    public String emitExpression(Expression expr)
    {
        return getExpressionFormatter().formatGetter(expr);
    }

    public String getCollaborationDiagramSvgFileName()
    {
        return (withSvgDiagrams) ? DocEmitterTools.getTypeCollaborationSvgUrl(docPath, compound) : null;
    }

    public class ChoiceData
    {
        public ChoiceData(ChoiceType choiceType)
        {
            final Expression selectorExpression = choiceType.getSelectorExpression();
            selector = getExpressionFormatter().formatGetter(selectorExpression);

            caseMemberList = new ArrayList<CaseMember>();
            final Iterable<ChoiceCase> choiceCases = choiceType.getChoiceCases();
            for (ChoiceCase choiceCase : choiceCases)
                caseMemberList.add(new CaseMember(choiceCase));

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
            public CaseMember(ChoiceCase choiceCase)
            {
                caseList = new ArrayList<Case>();
                final Iterable<ChoiceCase.CaseExpression> caseExpressions = choiceCase.getExpressions();
                for (ChoiceCase.CaseExpression caseExpression : caseExpressions)
                    caseList.add(new Case(caseExpression.getExpression(), caseExpression.getDocComment()));

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
            public Case(Expression caseExpression, DocCommentToken docCommentToken)
            {
                expression = getExpressionFormatter().formatGetter(caseExpression);
                docComment = new DocCommentTemplateData(docCommentToken);

                final Set<EnumItem> referencedEnumItems =
                        caseExpression.getReferencedSymbolObjects(EnumItem.class);
                if (!referencedEnumItems.isEmpty())
                    seeLink = new CaseSeeLink(referencedEnumItems.iterator().next());
                else
                    seeLink = null;
            }

            public String getExpression()
            {
                return expression;
            }

            public DocCommentTemplateData getDocComment()
            {
                return docComment;
            }

            public CaseSeeLink getSeeLink()
            {
                return seeLink;
            }

            private final String                    expression;
            private final DocCommentTemplateData    docComment;
            private final CaseSeeLink               seeLink;
        }

        public class CaseSeeLink
        {
            public CaseSeeLink(EnumItem caseType)
            {
                final EnumType caseTypeOwner = caseType.getEnumType();
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
            public DefaultMember(ChoiceDefault choiceDefault)
            {
                compoundField = choiceDefault.getField();
            }

            public Field getCompoundField()
            {
                return compoundField;
            }

            private final Field compoundField;
        }

        private final String            selector;
        private final List<CaseMember>  caseMemberList;
        private final DefaultMember     defaultMember;
    }
}
