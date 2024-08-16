package zserio.ast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import zserio.antlr.ZserioParser;
import zserio.antlr.ZserioParserBaseVisitor;
import zserio.tools.WarningsConfig;
import zserio.tools.ZserioToolPrinter;

/**
 * Visitor which checks grammar parse tree semantics.
 */
public final class ZserioParseTreeChecker extends ZserioParserBaseVisitor<Void>
{
    public ZserioParseTreeChecker(WarningsConfig warningsConfig, boolean allowImplicitArrays)
    {
        this.warningsConfig = warningsConfig;
        this.allowImplicitArrays = allowImplicitArrays;
    }

    @Override
    public Void visitPackageDeclaration(ZserioParser.PackageDeclarationContext ctx)
    {
        AstLocation location = new AstLocation(ctx.getStart());
        checkUtf8Encoding(location);
        checkNonPrintableCharacters(location);

        return visitChildren(ctx);
    }

    @Override
    public Void visitStructureFieldDefinition(ZserioParser.StructureFieldDefinitionContext ctx)
    {
        // this check avoids auto optional fields with optional clause
        if (ctx.OPTIONAL() != null && ctx.fieldOptionalClause() != null)
            throw new ParserException(ctx.fieldOptionalClause().getStart(),
                    "Auto optional field '" + ctx.fieldTypeId().id().getText() + "' cannot contain if clause!");

        // because of index expression check we must know if we are in array
        if (ctx.fieldTypeId().fieldArrayRange() != null)
        {
            isInArrayField = true;
            if (ctx.fieldTypeId().IMPLICIT() != null)
                isInImplicitArrayField = true;
        }
        visitChildren(ctx);
        isInArrayField = false;
        isInImplicitArrayField = false;

        return null;
    }

    @Override
    public Void visitFieldOffset(ZserioParser.FieldOffsetContext ctx)
    {
        // index expression in offsets is allowed if we are in array which is not implicit
        isInOffsetExpression = true;
        visitChildren(ctx);
        isInOffsetExpression = false;
        wasIndexUsedInOffsetExpression = false;

        return null;
    }

    @Override
    public Void visitFieldTypeId(ZserioParser.FieldTypeIdContext ctx)
    {
        // this check avoids implicit for none-array fields and implicit arrays with length expression
        if (ctx.IMPLICIT() != null)
        {
            if (!allowImplicitArrays)
            {
                final AstLocation location = new AstLocation(ctx.IMPLICIT().getSymbol());
                final ParserStackedException stackedException = new ParserStackedException(
                        location, "Implicit arrays are deprecated and will be removed from the language!");
                stackedException.pushMessage(location,
                        "For strong compatibility reason, please "
                                + "consider to use command line option '-allowImplicitArrays'.");
                throw stackedException;
            }

            final ZserioParser.FieldArrayRangeContext fieldArrayRangeCtx = ctx.fieldArrayRange();
            if (fieldArrayRangeCtx == null)
            {
                throw new ParserException(
                        ctx.IMPLICIT().getSymbol(), "Implicit keyword can be used only for arrays!");
            }

            if (fieldArrayRangeCtx.expression() != null)
            {
                throw new ParserException(fieldArrayRangeCtx.expression().getStart(),
                        "Length expression is not allowed for implicit arrays!");
            }
        }

        if (ctx.PACKED() != null)
        {
            final ZserioParser.FieldArrayRangeContext fieldArrayRangeCtx = ctx.fieldArrayRange();
            if (fieldArrayRangeCtx == null)
            {
                throw new ParserException(
                        ctx.PACKED().getSymbol(), "Packed keyword can be used only for arrays!");
            }

            if (ctx.IMPLICIT() != null)
            {
                throw new ParserException(ctx.IMPLICIT().getSymbol(), "Implicit arrays cannot be packed!");
            }
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitSqlTableDeclaration(ZserioParser.SqlTableDeclarationContext ctx)
    {
        final boolean isVirtual = ctx.USING() != null;
        final boolean isWithoutRowId = ctx.sqlWithoutRowId() != null;

        if (isVirtual && isWithoutRowId)
        {
            throw new ParserException(
                    ctx.sqlWithoutRowId().getStart(), "Virtual table cannot be without rowid!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitSqlTableFieldDefinition(ZserioParser.SqlTableFieldDefinitionContext ctx)
    {
        isInSqlTableField = true;
        visitChildren(ctx);
        isInSqlTableField = false;

        return null;
    }

    @Override
    public Void visitParenthesizedExpression(ZserioParser.ParenthesizedExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(ctx, ctx.getStart(), "Parenthesis are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitFunctionCallExpression(ZserioParser.FunctionCallExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.LPAREN().getSymbol(), "Function call is not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitArrayExpression(ZserioParser.ArrayExpressionContext ctx)
    {
        if (isInOffsetArrayExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Array expression is not allowed in offset array expressions!");
        }

        visit(ctx.expression(0));

        if (isInOffsetExpression)
        {
            if (isInArrayField && !isInImplicitArrayField)
                isIndexAllowed = true;
            isInOffsetArrayExpression = true;
        }

        visit(ctx.expression(1));

        if (isInOffsetExpression)
        {
            isIndexAllowed = false;
            isInOffsetArrayExpression = false;
        }

        return null;
    }

    @Override
    public Void visitDotExpression(ZserioParser.DotExpressionContext ctx)
    {
        if (isInOffsetArrayExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Dot expression is not allowed in offset array expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitIsSetExpression(ZserioParser.IsSetExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.getStart(), "Operator isset is not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitLengthofExpression(ZserioParser.LengthofExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.getStart(), "Operator lengthof is not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitValueofExpression(ZserioParser.ValueofExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.getStart(), "Operator valueof is not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitNumbitsExpression(ZserioParser.NumbitsExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.getStart(), "Operator numbits is not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitUnaryExpression(ZserioParser.UnaryExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.getStart(), "Unary operators are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitMultiplicativeExpression(ZserioParser.MultiplicativeExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Arithmetic operators are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitAdditiveExpression(ZserioParser.AdditiveExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Arithmetic operators are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitShiftExpression(ZserioParser.ShiftExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Shift operators are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitRelationalExpression(ZserioParser.RelationalExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Relational operators are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitEqualityExpression(ZserioParser.EqualityExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Relational operators are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitBitwiseAndExpression(ZserioParser.BitwiseAndExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Bitwise operators are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitBitwiseXorExpression(ZserioParser.BitwiseXorExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Bitwise operators are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitBitwiseOrExpression(ZserioParser.BitwiseOrExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Bitwise operators are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitLogicalAndExpression(ZserioParser.LogicalAndExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Logical operators are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitLogicalOrExpression(ZserioParser.LogicalOrExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Logical operators are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitTernaryExpression(ZserioParser.TernaryExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.operator, "Ternary operator is not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitLiteralExpression(ZserioParser.LiteralExpressionContext ctx)
    {
        if (isInOffsetExpression)
        {
            throwForbiddenInOffsets(ctx, ctx.getStart(), "Literals are not allowed in offset expressions!");
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitIndexExpression(ZserioParser.IndexExpressionContext ctx)
    {
        // this check allows index expressions only for arrays in field offsets or parameters
        if (!isIndexAllowed)
        {
            if (isInImplicitArrayField)
            {
                throw new ParserException(
                        ctx.INDEX().getSymbol(), "Implicit arrays cannot have indexed offsets!");
            }
            else
            {
                throw new ParserException(
                        ctx.INDEX().getSymbol(), "Index operator is not allowed in this context!");
            }
        }

        if (isInOffsetExpression)
        {
            if (wasIndexUsedInOffsetExpression)
            {
                throw new ParserException(
                        ctx.getStart(), "Index operator can be used only once within an offset expression!");
            }
            wasIndexUsedInOffsetExpression = true;
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitId(ZserioParser.IdContext ctx)
    {
        if (isInOffsetArrayExpression)
        {
            throwForbiddenInOffsets(
                    ctx, ctx.getStart(), "Identifiers are not allowed in offset array expressions!");
        }

        try
        {
            IdentifierValidator.validate(ctx.getText());
        }
        catch (RuntimeException exception)
        {
            throw new ParserException(ctx.getStart(), exception.getMessage());
        }

        return null;
    }

    @Override
    public Void visitTypeArgument(ZserioParser.TypeArgumentContext ctx)
    {
        // explicit is allowed only for SQL table fields
        if (ctx.EXPLICIT() != null && !isInSqlTableField)
            throw new ParserException(
                    ctx.EXPLICIT().getSymbol(), "Explicit keyword is allowed only in SQL tables!");

        // index expression in type instantiation is allowed if we are in array
        if (isInArrayField)
            isIndexAllowed = true;
        visitChildren(ctx);
        isIndexAllowed = false;

        return null;
    }

    private void throwForbiddenInOffsets(ParserRuleContext ctx, Token locationToken, String message)
    {
        if (isInOffsetArrayExpression)
        {
            final ParserStackedException exception = new ParserStackedException(
                    new AstLocation(ctx.getStart()), "Only @index is allowed in offset array expressions!");
            exception.pushMessage(new AstLocation(locationToken), message);
            throw exception;
        }
        else
        {
            throw new ParserException(locationToken, message);
        }
    }

    private void checkUtf8Encoding(AstLocation location)
    {
        final byte[] fileContent = readFile(location);
        try
        {
            final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            decoder.decode(ByteBuffer.wrap(fileContent));
        }
        catch (CharacterCodingException exception)
        {
            ZserioToolPrinter.printWarning(
                    location, "Found non-UTF8 encoded characters.", warningsConfig, WarningsConfig.ENCODING);
        }
    }

    private void checkNonPrintableCharacters(AstLocation location)
    {
        final byte[] fileContent = readFile(location);
        final String content = new String(fileContent, StandardCharsets.UTF_8);

        if (content.indexOf('\t') >= 0)
        {
            ZserioToolPrinter.printWarning(
                    location, "Found tab characters.", warningsConfig, WarningsConfig.ENCODING);
        }

        for (int i = 0; i < content.length(); ++i)
        {
            final char character = content.charAt(i);
            if (character < '\u0020' && character != '\r' && character != '\n' && character != '\t')
            {
                ZserioToolPrinter.printWarning(location, "Found non-printable ASCII characters.",
                        warningsConfig, WarningsConfig.ENCODING);
                break;
            }
        }
    }

    private byte[] readFile(AstLocation location)
    {
        final String fileName = location.getFileName();
        final File file = new File(fileName);
        try (final FileInputStream inputStream = new FileInputStream(file))
        {
            final byte fileContent[] = new byte[(int)file.length()];
            if (inputStream.read(fileContent) == -1)
                throw new ParserException(location, "Error during reading of source file " + fileName + "!");
            return fileContent;
        }
        catch (FileNotFoundException exception)
        {
            throw new ParserException(location, "Source file '" + fileName + "' cannot be found again!");
        }
        catch (IOException exception)
        {
            throw new ParserException(location, "Source file '" + fileName + "' cannot be read again!");
        }
    }

    private final WarningsConfig warningsConfig;
    private final boolean allowImplicitArrays;

    /** Flags used to allow index operator only in offsets or arguments in array fields. */
    private boolean isInArrayField = false;
    private boolean isInImplicitArrayField = false;
    private boolean isIndexAllowed = false;
    private boolean isInOffsetExpression = false;
    private boolean isInOffsetArrayExpression = false;
    private boolean wasIndexUsedInOffsetExpression = false;

    /** Flag used to allow explicit keyword only for SQL tables. */
    private boolean isInSqlTableField = false;
}
