package zserio.ast;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import zserio.antlr.DocCommentBaseVisitor;
import zserio.antlr.DocCommentParser;

/**
 * Implementation of DocComment4BaseVisitor which build the documentaion comment AST.
 */
public class DocCommentAstBuilder extends DocCommentBaseVisitor<Object>
{
    @Override
    public Object visitDocComment(DocCommentParser.DocCommentContext ctx)
    {
        token = ctx.getStart();
        visitChildren(ctx);

        return new DocComment(token, paragraphs, seeTags, todoTags, paramTags, isDeprecated);
    }

    @Override
    public Object visitDocLine(DocCommentParser.DocLineContext ctx)
    {
        if (ctx.docTag() != null)
        {
            prevMultilineNode = null;
            visitDocTag(ctx.docTag());
        }
        else if (ctx.docTextLine() != null)
        {
            final DocTextLine docTextLine = visitDocTextLine(ctx.docTextLine());
            if (prevMultilineNode == null)
            {
                final DocParagraph paragraph = new DocParagraph(ctx.getStart(), docTextLine);
                paragraphs.add(paragraph);
                prevMultilineNode = paragraph;
            }
            else
            {
                prevMultilineNode.addLine(docTextLine);
            }
        }
        else // empty line
        {
            prevMultilineNode = null;
        }

        return null;
    }

    @Override
    public Object visitDocTag(DocCommentParser.DocTagContext ctx)
    {
        if (ctx.seeTag() != null)
        {
            seeTags.add(visitSeeTag(ctx.seeTag()));
        }
        else if (ctx.todoTag() != null)
        {
            todoTags.add(visitTodoTag(ctx.todoTag()));
        }
        else if (ctx.paramTag() != null)
        {
            paramTags.add(visitParamTag(ctx.paramTag()));
        }
        else // deprecated tag
        {
            isDeprecated = true;
        }

        return null;
    }

    @Override
    public DocTagSee visitSeeTag(DocCommentParser.SeeTagContext ctx)
    {
        return new DocTagSee(ctx.getStart(),
                (ctx.seeTagAlias() != null ? ctx.seeTagAlias().seeTagAliasText().getText() : null),
                ctx.seeTagId().getText());
    }

    @Override
    public DocTagTodo visitTodoTag(DocCommentParser.TodoTagContext ctx)
    {
        final DocTagTodo todoTag = new DocTagTodo(ctx.getStart(), visitDocTextLine(ctx.docTextLine()));
        prevMultilineNode = todoTag;
        return todoTag;
    }

    @Override
    public DocTagParam visitParamTag(DocCommentParser.ParamTagContext ctx)
    {
        final DocTagParam paramTag = new DocTagParam(ctx.getStart(), ctx.paramName().getText(),
                visitDocTextLine(ctx.docTextLine()));
        prevMultilineNode = paramTag;
        return paramTag;
    }

    @Override
    public DocTextLine visitDocTextLine(DocCommentParser.DocTextLineContext ctx)
    {
        List<DocText> docTexts = new ArrayList<DocText>();

        for (DocCommentParser.DocTextContext docTextCtx : ctx.docText())
            docTexts.add(visitDocText(docTextCtx));

        return new DocTextLine(ctx.getStart(), docTexts);
    }

    @Override
    public DocText visitDocText(DocCommentParser.DocTextContext ctx)
    {
        if (ctx.seeTag() != null)
            return new DocText(ctx.getStart(), visitSeeTag(ctx.seeTag()));
        else
            return new DocText(ctx.getStart(), visitTextElement(ctx.textElement()));
    }

    @Override
    public DocTextElement visitTextElement(DocCommentParser.TextElementContext ctx)
    {
        return new DocTextElement(ctx.getStart(), ctx.getText());
    }

    private Token token = null;
    private final List<DocParagraph> paragraphs = new ArrayList<DocParagraph>();
    private final List<DocTagSee> seeTags = new ArrayList<DocTagSee>();
    private final List<DocTagTodo> todoTags = new ArrayList<DocTagTodo>();
    private final List<DocTagParam> paramTags = new ArrayList<DocTagParam>();
    private boolean isDeprecated = false;

    private DocMultilineNode prevMultilineNode = null;
}