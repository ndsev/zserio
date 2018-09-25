package zserio.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.ParseException;

import antlr.MismatchedTokenException;
import antlr.NoViableAltException;
import antlr.Token;
import antlr.TokenStreamException;
import antlr.TokenStreamHiddenTokenFilter;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;
import antlr.RecognitionException;

import zserio.antlr.ZserioEmitter;
import zserio.antlr.ZserioLexer;
import zserio.antlr.ZserioParser;
import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.ExpressionEvaluator;
import zserio.antlr.TypeEvaluator;
import zserio.antlr.util.FileNameLexerToken;
import zserio.antlr.util.ParserException;
import zserio.ast.PackageName;
import zserio.ast.Root;
import zserio.ast.TranslationUnit;
import zserio.ast.DefaultToken;
import zserio.ast.Import;

import zserio.ast.TokenAST;
import zserio.emit.common.ZserioEmitException;

/**
 * The main class for Zserio tool.
 */
public class ZserioTool
{
    /**
     * The entry point for this command line application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        if (!runTool(args))
            System.exit(1);
    }

    /**
     * The entry point for Ant task.
     *
     * Calling System.exit() method throws if it is called from Ant task. Therefore this special entry point
     * has been introduced.
     *
     * @param args Command line arguments.
     *
     * @return Returns true in case of success, otherwise returns false.
     */
    public static boolean runTool(String[] args)
    {
        try
        {
            final ZserioTool zserioTool = new ZserioTool();
            zserioTool.execute(args);
        }
        catch (org.apache.commons.cli.ParseException exception)
        {
            ZserioToolPrinter.printError(exception.getMessage());
            return false;
        }
        catch (ZserioEmitException exception)
        {
            ZserioToolPrinter.printError(exception.getMessage());
            return false;
        }
        catch (NoViableAltException exception)
        {
            ZserioToolPrinter.printError(exception.getFilename(), exception.getLine(),
                    exception.getColumn(), getErrorMessage(exception.getMessage(), exception.token));
            return false;
        }
        catch (MismatchedTokenException exception)
        {
            ZserioToolPrinter.printError(exception.getFilename(), exception.getLine(),
                    exception.getColumn(), getErrorMessage(exception.getMessage(), exception.token));
            return false;
        }
        catch (RecognitionException exception)
        {
            ZserioToolPrinter.printError(exception.getFilename(), exception.getLine(),
                                             exception.getColumn(), exception.getMessage());
            return false;
        }
        catch (TokenStreamException exception)
        {
            ZserioToolPrinter.printError(exception.toString());
        }
        catch (IOException exception)
        {
            ZserioToolPrinter.printError(exception.getMessage());
            return false;
        }
        catch (Throwable exception)
        {
            String message = exception.getMessage();
            if (message == null)
                message = "Internal error";
            ZserioToolPrinter.printError(message);
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    private ZserioTool()
    {
        commandLineArguments = new CommandLineArguments();
        inputFileManager = new InputFileManager(commandLineArguments);
        extensionManager = new ExtensionManager(commandLineArguments);
    }

    private void execute(String[] args) throws Exception
    {
        commandLineArguments.parse(args);
        if (commandLineArguments.hasHelpOption())
        {
            commandLineArguments.printHelp();
            extensionManager.printExtensions();
        }
        else if (commandLineArguments.hasVersionOption())
        {
            ZserioToolPrinter.printMessage("version " + ZserioVersion.VERSION_STRING);
        }
        else if (commandLineArguments.getDocCommentFileName() != null)
        {
            final TokenAST rootToken = parseComment(commandLineArguments.getDocCommentFileName(),
                    ZserioParserTokenTypes.DOC_COMMENT);
            rootToken.evaluateHiddenDocComment(null);
            showAstTree(rootToken.getHiddenDocComment());
        }
        else if (commandLineArguments.getInputFileName() == null)
        {
            throw new ParseException("Missing input file name!");
        }
        else
        {
            process();
        }
    }

    private void process() throws Exception
    {
        final Root rootNode = Root.create();

        parse(rootNode);

        if (commandLineArguments.hasShowAstOption())
            showAstTree(rootNode);

        check(rootNode);
        emit(rootNode);

        ZserioToolPrinter.printMessage("Done");
    }

    private void parse(Root rootNode) throws Exception
    {
        final String inputFileName = commandLineArguments.getInputFileName();
        final String inputFileFullName = inputFileManager.getFileFullName(inputFileName);
        final TranslationUnit translationUnit = parsePackage(rootNode, inputFileFullName);
        parseImportedPackages(rootNode, translationUnit);

        // create name scopes
        final TypeEvaluator typeEval = new TypeEvaluator();
        typeEval.root(rootNode);

        // resolve references
        rootNode.resolveReferences();

        // check expression types and evaluate constant expressions
        final ExpressionEvaluator exprEval = new ExpressionEvaluator();
        exprEval.root(rootNode);
    }

    private TranslationUnit parsePackage(Root rootNode, String inputFileFullName) throws Exception
    {
        ZserioToolPrinter.printMessage("Parsing " + inputFileFullName);
        inputFileManager.registerFile(inputFileFullName);

        // set up lexer, parser and token buffer
        FileInputStream stream = null;
        InputStreamReader reader = null;
        TranslationUnit translationUnit = null;
        try
        {
            stream = new FileInputStream(inputFileFullName);
            reader = new InputStreamReader(stream, "UTF-8");
            final ZserioLexer lexer = new ZserioLexerWithFileNameSupport(reader);
            lexer.setFilename(inputFileFullName);
            lexer.setTokenObjectClass(FileNameLexerToken.class.getCanonicalName());
            final TokenStreamHiddenTokenFilter filter = new TokenStreamHiddenTokenFilter(lexer);
            filter.hide(ZserioParserTokenTypes.DOC_COMMENT);
            final ZserioParser parser = new ZserioParser(filter);

            // must call this to see file name in error messages
            parser.setFilename(inputFileFullName);

            // use custom node class containing line information
            parser.setASTNodeClass(DefaultToken.class.getCanonicalName());

            // parse file and get root node of syntax tree
            parser.translationUnit(inputFileManager);
            translationUnit = (TranslationUnit)parser.getAST();
        }
        finally
        {
            try
            {
                if (reader != null)
                    reader.close();
            }
            finally
            {
                if (stream != null)
                    stream.close();
            }
        }

        // evaluates all children (we will need all import nodes immediately)
        translationUnit.evaluateAll();

        rootNode.addTranslationUnit(translationUnit);

        return translationUnit;
    }

    private void parseImportedPackages(Root rootNode, TranslationUnit parentTranslationUnit)
            throws Exception
    {
        final Iterable<Import> imports = parentTranslationUnit.getImports();
        for (Import importNode : imports)
        {
            final PackageName importedPackageName = importNode.getImportedPackageName();
            final String inputFileFullName = inputFileManager.getFileFullName(importedPackageName);
            if (!inputFileManager.isFileRegistered(inputFileFullName))
            {
                final TranslationUnit translationUnit = parsePackage(rootNode, inputFileFullName);
                parseImportedPackages(rootNode, translationUnit);
            }
        }
    }

    private void check(Root rootNode) throws Exception
    {
        ZserioToolPrinter.printMessage("Checking");

        // check all nodes
        rootNode.checkAll();
    }

    private void emit(Root rootNode) throws Exception
    {
        final ExtensionParameters parameters = new ExtensionParameters(commandLineArguments);
        final ZserioEmitter emitter = new ZserioEmitter();

        extensionManager.callExtensions(parameters, emitter, rootNode);
    }

    private static Root parseComment(String commentFileName, int commentTokenType)
            throws IOException, ParserException
    {
        ZserioToolPrinter.printMessage("Parsing " + commentFileName);

        final String docComment = readFileToString(commentFileName);
        final FileNameLexerToken commentLexerToken = new FileNameLexerToken(commentTokenType, docComment);
        final Root rootToken = Root.create(commentLexerToken);

        return rootToken;
    }

    private static String readFileToString(String fileName) throws IOException
    {
        final StringBuilder builder = new StringBuilder();
        FileInputStream stream = null;
        InputStreamReader reader = null;
        BufferedReader bufferReader = null;
        try
        {
            stream = new FileInputStream(fileName);
            reader = new InputStreamReader(stream, "UTF-8");
            bufferReader = new BufferedReader(reader);
            String line = bufferReader.readLine();
            while (line != null)
            {
                builder.append(line);
                builder.append("\n");
                line = bufferReader.readLine();
            }
        }
        finally
        {
            try
            {
                if (reader != null)
                    reader.close();
            }
            finally
            {
                try
                {
                    if (stream != null)
                        stream.close();
                }
                finally
                {
                    if (bufferReader != null)
                        bufferReader.close();
                }
            }
        }

        return builder.toString();
    }

    private static void showAstTree(AST rootNode)
    {
        final ASTFrame frame = new ASTFrame("AST", rootNode);
        frame.setVisible(true);
    }

    private static String getErrorMessage(String message, Token token)
    {
        if (token != null)
        {
            if (Root.isKeyword(token.getType()))
                return message + " (reserved keyword)";
            if (token.getType() == ZserioParserTokenTypes.EOF)
                return "Unexpected end of file: " + message;
        }
        return message;
    }

    private static class ZserioLexerWithFileNameSupport extends ZserioLexer
    {
        ZserioLexerWithFileNameSupport(InputStreamReader reader)
        {
            super(reader);
        }

        @Override
        protected Token makeToken(int t)
        {
            final Token token = super.makeToken(t);
            token.setFilename(getFilename());

            return token;
        }
    }

    private final InputFileManager inputFileManager;
    private final CommandLineArguments commandLineArguments;
    private final ExtensionManager extensionManager;
}
