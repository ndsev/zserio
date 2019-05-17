package zserio.ast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import zserio.antlr.ZserioParser;
import zserio.antlr.ZserioParserBaseVisitor;
import zserio.antlr.util.ParserException;
import zserio.tools.InputFileManager;
import zserio.tools.ZserioToolPrinter;

/**
 * Visitor which checks ANTLR4 parse tree semantics.
 */
public class ZserioParseTreeChecker extends ZserioParserBaseVisitor<Void>
{
    /**
     * Constructor.
     *
     * @param inputFileManager Input file manager.
     */
    public ZserioParseTreeChecker(InputFileManager inputFileManager)
    {
        this.inputFileManager = inputFileManager;
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
    public Void visitPackageNameDefinition(ZserioParser.PackageNameDefinitionContext ctx)
    {
        if (ctx == null)
            return null;

        // this must be checked now to avoid obscure errors if package is not stored in the same file name
       final PackageName packageName = createPackageName(ctx.qualifiedName().id());
        final String expectedFileFullName = inputFileManager.getFileFullName(packageName);
        final String fileFullName = ctx.getStart().getInputStream().getSourceName();
        if (!expectedFileFullName.equals(fileFullName))
            throw new ParserException(ctx.qualifiedName().getStart(), "Package '" + packageName.toString() +
                    "' does not match to the source file name!");

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
            isInArrayField = true;
        visitChildren(ctx);
        isInArrayField = false;

        return null;
    }

    @Override
    public Void visitFieldOffset(ZserioParser.FieldOffsetContext ctx)
    {
        // index expression is allowed if we are in array
        if (isInArrayField)
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
            final ZserioParser.FieldArrayRangeContext fieldArrayRangeCtx = ctx.fieldArrayRange();
            if (fieldArrayRangeCtx == null)
                throw new ParserException(ctx.IMPLICIT().getSymbol(),
                        "Implicit keyword can be used only for arrays!");

            if (fieldArrayRangeCtx.expression() != null)
                throw new ParserException(fieldArrayRangeCtx.expression().getStart(),
                        "Length expression is not allowed for implicit arrays!");
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
            throw new ParserException(ctx.INDEX().getSymbol(),
                    "Index operator is not allowed in this context!");

        return visitChildren(ctx);
    }

    @Override
    public Void visitId(ZserioParser.IdContext ctx)
    {
        final String id = ctx.getText();
        if (reservedKeywordsList.contains(id))
            throw new ParserException(ctx.getStart(), "'" + id +
                    "' is a reserved keyword and may not be used here!");

        return null;
    }

    @Override
    public Void visitTypeArgument(ZserioParser.TypeArgumentContext ctx)
    {
        // explicit is allowed only for SQL table fields
        if (ctx.EXPLICIT() != null && !isInSqlTableField)
            throw new ParserException(ctx.EXPLICIT().getSymbol(),
                    "Explicit keyword is allowed only in SQL tables!");

        // index expression is allowed if we are in array
        if (isInArrayField)
            isIndexAllowed = true;
        visitChildren(ctx);
        isIndexAllowed = false;

        return null;
    }

    private PackageName createPackageName(List<ZserioParser.IdContext> ids)
    {
        final PackageName.Builder packageNameBuilder = new PackageName.Builder();
        for (ZserioParser.IdContext id : ids)
            packageNameBuilder.addId(id.getText());
        return packageNameBuilder.get();
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
        FileInputStream inputStream = null;
        byte fileContent[];
        try
        {
            inputStream = new FileInputStream(file);
            fileContent = new byte[(int)file.length()];
            if (inputStream.read(fileContent) == -1)
                throw new ParserException(location, "Error during reading of source file " + fileName + "!");
        }
        catch (FileNotFoundException exception)
        {
            throw new ParserException(location, "Source file '" + fileName + "' cannot be found again!");
        }
        catch (IOException exception)
        {
            throw new ParserException(location, "Source file '" + fileName + "' cannot be read again!");
        }
        finally
        {
            try
            {
                if (inputStream != null)
                    inputStream.close();
            }
            catch (IOException exception)
            {
                // just continue
            }
        }

        return fileContent;
    }

    /**
     * The array of all reserved keywords.
     */
    private static final String[] reservedKeywords = new String[]
    {
        // C++ reserved keywords
        "alignas",   "alignof",          "and",          "and_eq",   "asm",       "auto",
        "bitand",    "bitor",            "bool",         "break",    "case",      "catch",
        "char",      "char16_t",         "char32_t",     "class",    "compl",     "const",
        "constexpr", "const_cast",       "continue",     "decltype", "default",   "delete",
        "do",        "double",           "dynamic_cast", "else",     "enum",      "explicit",
        "export",    "extern",           "false",        "float",    "for",       "friend",
        "goto",      "if",               "inline",       "int",      "long",      "mutable",
        "namespace", "new",              "noexcept",     "not",      "not_eq",    "nullptr",
        "operator",  "or",               "or_eq",        "private",  "protected", "public",
        "register",  "reinterpret_cast", "return",       "short",    "signed",    "sizeof",
        "static",    "static_assert",    "static_cast",  "struct",   "switch",    "template",
        "this",      "thread_local",     "throw",        "true",     "try",       "typedef",
        "typeid",    "typename",         "union",        "unsigned", "using",     "virtual",
        "void",      "volatile",         "wchar_t",      "while",    "xor",       "xor_eq",

        // Java reserved keywords
        "abstract",   "assert",       "boolean",    "break",    "byte",      "case",
        "catch",      "char",         "class",      "const",    "continue",  "default",
        "double",     "do",           "else",       "enum",     "extends",   "false",
        "final",      "finally",      "float",      "for",      "goto",      "if",
        "implements", "import",       "instanceof", "int",      "interface", "long",
        "native",     "new",          "null",       "package",  "private",   "protected",
        "public",     "return",       "short",      "static",   "strictfp",  "super",
        "switch",     "synchronized", "this",       "throw",    "throws",    "transient",

        // Python reserved keywords
        "and",    "as",      "assert",   "break", "class", "continue", "def",   "del",    "elif", "else",
        "except", "finally", "False",    "for",   "from",  "global",   "if",    "import", "in",   "is",
        "lambda", "None",    "nonlocal", "not",   "or",    "pass",     "raise", "return", "True", "try",
        "while",  "with",    "yield"
    };

    private static final Set<String> reservedKeywordsList =
            new HashSet<String>(Arrays.asList(reservedKeywords));

    private final InputFileManager inputFileManager;

    /** Flags used to allow index operator only in offsets or arguments in array fields. */
    private boolean isInArrayField = false;
    private boolean isIndexAllowed = false;

    /** Flag used to allow explicit keyword only for SQL tables. */
    private boolean isInSqlTableField = false;
}
