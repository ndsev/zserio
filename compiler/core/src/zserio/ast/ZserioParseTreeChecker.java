package zserio.ast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import zserio.antlr.ZserioParser;
import zserio.antlr.ZserioParserBaseVisitor;
import zserio.tools.ZserioToolPrinter;

/**
 * Visitor which checks grammar parse tree semantics.
 */
public class ZserioParseTreeChecker extends ZserioParserBaseVisitor<Void>
{
    public ZserioParseTreeChecker(boolean allowImplicitArrays)
    {
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
            throw new ParserException(ctx.fieldOptionalClause().getStart(), "Auto optional field '" +
                    ctx.fieldTypeId().id().getText() + "' cannot contain if clause!");

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
        if (isInArrayField && !isInImplicitArrayField)
            isIndexAllowed = true;
        visitChildren(ctx);
        isIndexAllowed = false;

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
                final ParserStackedException stackedException = new ParserStackedException(location,
                        "Implicit arrays are deprecated and will be removed from the language!");
                stackedException.pushMessage(location,  "    For strong compatibility reason, please " +
                        "consider to use command line option '-allowImplicitArrays'.");
                throw stackedException;
            }

            final ZserioParser.FieldArrayRangeContext fieldArrayRangeCtx = ctx.fieldArrayRange();
            if (fieldArrayRangeCtx == null)
            {
                throw new ParserException(ctx.IMPLICIT().getSymbol(),
                        "Implicit keyword can be used only for arrays!");
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
                throw new ParserException(ctx.PACKED().getSymbol(),
                        "Packed keyword can be used only for arrays!");
            }

            if (ctx.IMPLICIT() != null)
            {
                throw new ParserException(ctx.IMPLICIT().getSymbol(),
                        "Implicit arrays cannot be packed!");
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
            throw new ParserException(ctx.sqlWithoutRowId().getStart(),
                    "Virtual table cannot be without rowid!");
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
    public Void visitIndexExpression(ZserioParser.IndexExpressionContext ctx)
    {
        // this check allows index expressions only for arrays in field offsets or parameters
        if (!isIndexAllowed)
        {
            if (isInImplicitArrayField)
            {
                throw new ParserException(ctx.INDEX().getSymbol(),
                        "Implicit arrays cannot have indexed offsets!");
            }
            else
            {
                throw new ParserException(ctx.INDEX().getSymbol(),
                        "Index operator is not allowed in this context!");
            }
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitId(ZserioParser.IdContext ctx)
    {
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
            throw new ParserException(ctx.EXPLICIT().getSymbol(),
                    "Explicit keyword is allowed only in SQL tables!");

        // index expression in type instantiation is allowed if we are in array
        if (isInArrayField)
            isIndexAllowed = true;
        visitChildren(ctx);
        isIndexAllowed = false;

        return null;
    }

    private void checkUtf8Encoding(AstLocation location)
    {
        final byte[] fileContent = readFile(location);
        try
        {
            final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            decoder.decode(ByteBuffer.wrap(fileContent));
        }
        catch (CharacterCodingException exception)
        {
            ZserioToolPrinter.printWarning(location, "Found non-UTF8 encoded characters.");
        }
    }

    private void checkNonPrintableCharacters(AstLocation location)
    {
        final byte[] fileContent = readFile(location);
        final String content = new String(fileContent, Charset.forName("UTF-8"));

        if (content.indexOf('\t') >= 0)
            ZserioToolPrinter.printWarning(location, "Found tab characters.");

        for (int i = 0; i < content.length(); ++i)
        {
            final char character = content.charAt(i);
            if (character < '\u0020' && character != '\r' && character != '\n' && character != '\t')
            {
                ZserioToolPrinter.printWarning(location, "Found non-printable ASCII characters.");
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

    private final boolean allowImplicitArrays;

    /** Flags used to allow index operator only in offsets or arguments in array fields. */
    private boolean isInArrayField = false;
    private boolean isInImplicitArrayField = false;
    private boolean isIndexAllowed = false;

    /** Flag used to allow explicit keyword only for SQL tables. */
    private boolean isInSqlTableField = false;
}
