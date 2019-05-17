package zserio.ast;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import zserio.antlr.DocCommentBaseVisitor;
import zserio.antlr.DocCommentParser;

/**
 * Implementation of DocCommentBaseVisitor which build the documentation comment AST.
 */
class DocCommentAstBuilder extends DocCommentBaseVisitor<Object>
{
    DocCommentAstBuilder(Token docCommentToken)
    {
        this.docCommentToken = docCommentToken;
    }

    @Override
    public Object visitDocComment(DocCommentParser.DocCommentContext ctx)
    {
        visitChildren(ctx);

        return new DocComment(docCommentToken, paragraphs);
    }

    @Override
    public Object visitDocElement(DocCommentParser.DocElementContext ctx)
    {
        if (ctx.docTag() != null)
        {
            currentMultilineNode = null;
            addDocElement(visitDocTag(ctx.docTag()));
        }
        else if (ctx.docLine() != null)
        {
            final DocLine docLine = visitDocLine(ctx.docLine());

            if (currentMultilineNode != null)
            {
                currentMultilineNode.addLine(docLine);
            }
            else
            {
                currentMultilineNode = new DocMultiline(docLine.getLocation(), docLine);
                addDocElement(new DocElement(docLine.getLocation(), currentMultilineNode));
            }
        }
        else // empty line
        {
            currentParagraph = null;
            currentMultilineNode = null;
        }

        return null;
    }

    @Override
    public DocElement visitDocTag(DocCommentParser.DocTagContext ctx)
    {
        if (ctx.seeTag() != null)
            return new DocElement(getLocation(ctx.getStart()), visitSeeTag(ctx.seeTag()));
        else if (ctx.todoTag() != null)
            return new DocElement(getLocation(ctx.getStart()), (visitTodoTag(ctx.todoTag())));
        else if (ctx.paramTag() != null)
            return new DocElement(getLocation(ctx.getStart()), (visitParamTag(ctx.paramTag())));
        else // deprecated tag
            return new DocElement(getLocation(ctx.getStart()), (visitDeprecatedTag(ctx.deprecatedTag())));
    }

    @Override
    public DocTagSee visitSeeTag(DocCommentParser.SeeTagContext ctx)
    {
        return new DocTagSee(getLocation(ctx.getStart()),
                (ctx.seeTagAlias() != null ? ctx.seeTagAlias().seeTagAliasText().getText() : null),
                ctx.seeTagId().getText());
    }

    @Override
    public DocTagTodo visitTodoTag(DocCommentParser.TodoTagContext ctx)
    {
        final DocLine docLine = ctx.docLine() != null
                ? visitDocLine(ctx.docLine())
                : new DocLine(getLocation(ctx.getStop()), new ArrayList<DocLineElement>());
        final DocTagTodo todoTag = new DocTagTodo(getLocation(ctx.getStart()), docLine);
        currentMultilineNode = todoTag;
        return todoTag;
    }

    @Override
    public DocTagParam visitParamTag(DocCommentParser.ParamTagContext ctx)
    {
        final DocLine docLine = ctx.docLine() != null
                ? visitDocLine(ctx.docLine())
                : new DocLine(getLocation(ctx.getStop()), new ArrayList<DocLineElement>());
        final DocTagParam paramTag = new DocTagParam(getLocation(ctx.getStart()), ctx.paramName().getText(),
                docLine);
        currentMultilineNode = paramTag;
        return paramTag;
    }

    @Override
    public DocTagDeprecated visitDeprecatedTag(DocCommentParser.DeprecatedTagContext ctx)
    {
        return new DocTagDeprecated(getLocation(ctx.getStart()));
    }

    @Override
    public DocLine visitDocLine(DocCommentParser.DocLineContext ctx)
    {
        List<DocLineElement> docLineElements = new ArrayList<DocLineElement>();

        for (DocCommentParser.DocLineElementContext docLineElementCtx : ctx.docLineElement())
            docLineElements.add(visitDocLineElement(docLineElementCtx));

        return new DocLine(getLocation(ctx.getStart()), docLineElements);
    }

    @Override
    public DocLineElement visitDocLineElement(DocCommentParser.DocLineElementContext ctx)
    {
        if (ctx.seeTag() != null)
            return new DocLineElement(getLocation(ctx.getStart()), visitSeeTag(ctx.seeTag()));
        else
            return new DocLineElement(getLocation(ctx.getStart()), visitDocText(ctx.docText()));
    }

    @Override
    public DocText visitDocText(DocCommentParser.DocTextContext ctx)
    {
        return new DocText(getLocation(ctx.getStart()), ctx.getText());
    }

    private void addDocElement(DocElement docElement)
    {
        if (currentParagraph == null)
        {
            currentParagraph = new DocParagraph(docElement.getLocation());
            paragraphs.add(currentParagraph);
        }

        currentParagraph.addDocElement(docElement);
    }

    private AstLocation getLocation(Token token)
    {
        final String fileName = docCommentToken.getInputStream().getSourceName();
        final int line = docCommentToken.getLine() + token.getLine() - 1; // lines are numbered from 1!
        final int charPositionInLine = token.getLine() == 1
                ? docCommentToken.getCharPositionInLine() + token.getCharPositionInLine()
                : token.getCharPositionInLine();

        return new AstLocation(fileName, line, charPositionInLine);
    }

    private final Token docCommentToken;
    private final List<DocParagraph> paragraphs = new ArrayList<DocParagraph>();

    private DocParagraph currentParagraph = null;
    private DocMultiline currentMultilineNode = null;
}
