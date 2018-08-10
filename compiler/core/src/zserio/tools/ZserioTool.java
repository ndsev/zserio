package zserio.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.cli.ParseException;

import antlr.CommonHiddenStreamToken;
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
import zserio.ast.ZserioException;
import zserio.ast.ZserioTypeContainer;
import zserio.ast.DefaultToken;
import zserio.ast.Import;
import zserio.ast.Package;
import zserio.ast.TokenAST;

/**
 * The main class for Relational Zserio (Zserio) tool.
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
        catch (ZserioException exception)
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
        emitter = new ZserioEmitter();

        inputFileManager = new InputFileManager();
        commandLineArguments = new CommandLineArguments();
        extensionManager = new ExtensionManager(commandLineArguments.getOptions());

        final Token token = new FileNameLexerToken(ZserioParserTokenTypes.ROOT, "ROOT");
        rootNode = new DefaultToken(token);
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
            throw new ParseException("Missing input file name");
        }
        else
        {
            process();
        }
    }

    private void process() throws Exception
    {
        parse();

        if (commandLineArguments.hasShowAstOption())
            showAstTree(rootNode);

        check();
        emit();

        ZserioToolPrinter.printMessage("Done");
    }

    /** @todo redesign */
    private void parse() throws Exception
    {
        final String inputFileName = commandLineArguments.getInputFileName();
        detectInputFileExtension(inputFileName);
        final String inputFileFullName = getInputFileFullName(inputFileName);
        inputFileManager.registerFile(inputFileFullName);
        AST unitRoot = parsePackage(inputFileName, inputFileFullName, true);
        rootNode.addChild(unitRoot);
        parseImportedPackages(unitRoot);

        // create name scopes and resolve references
        TypeEvaluator typeEval = new TypeEvaluator();
        typeEval.root(rootNode);

        setRootPackage(unitRoot);
        PackageManager.get().linkAll();

        // check expression types and evaluate constant expressions
        ExpressionEvaluator exprEval = new ExpressionEvaluator();
        exprEval.root(rootNode);
    }

    private void check() throws Exception
    {
        ZserioToolPrinter.printMessage("Checking");

        rootNode.checkAll();

        ZserioTypeContainer.check();

        ZserioTypeCheckerVisitor zserioCheckerVisitor = new ZserioTypeCheckerVisitor();
        ZserioTypeContainer.walk(zserioCheckerVisitor);
        zserioCheckerVisitor.printWarnings();

        inputFileManager.checkFiles();
    }

    private void emit() throws Exception
    {
        ExtensionParameters parameters = new ExtensionParameters(commandLineArguments);
        extensionManager.callExtensions(parameters, emitter, rootNode);
    }

    private void setRootPackage(AST unitNode) throws ZserioException, IllegalArgumentException
    {
        AST node = unitNode.getFirstChild();
        if (node != null && node.getType() == ZserioParserTokenTypes.PACKAGE)
        {
            PackageManager packageManager = PackageManager.get();
            packageManager.setRoot(packageManager.lookup(node));
        }
    }

    private void parseImportedPackages(AST unitNode) throws Exception
    {
        AST node = unitNode.getFirstChild();

        // skip over optional "package"
        if (node != null && node.getType() == ZserioParserTokenTypes.PACKAGE)
            node = node.getNextSibling();

        while (node != null && node.getType() == ZserioParserTokenTypes.IMPORT)
        {
            Import importNode = (Import)node;
            final String inputFileName = getPackageFile(importNode);
            final String inputFileFullName = getInputFileFullName(inputFileName);
            if (!inputFileManager.isFileRegistered(inputFileFullName))
            {
                inputFileManager.registerFile(inputFileFullName);
                AST unitRoot = parsePackage(inputFileName, inputFileFullName, false);
                rootNode.addChild(unitRoot);
                parseImportedPackages(unitRoot);
            }
            node = node.getNextSibling();
        }
    }

    private String getPackageFile(Import importNode)
    {
        final String pkgFileName = StringJoinUtil.joinStrings(importNode.getPackagePath(), File.separator);
        return pkgFileName + inputFileExtension;
    }

    private AST parsePackage(String inputFileName, String inputFileFullName, boolean initialFile)
            throws Exception
    {
        ZserioToolPrinter.printMessage("Parsing " + inputFileFullName);

        // set up lexer, parser and token buffer
        FileInputStream stream = null;
        InputStreamReader reader = null;
        TokenAST retVal = null;
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
            parser.translationUnit();
            retVal = (TokenAST)parser.getAST();
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

        if (retVal == null)
            throw new ParserException(inputFileFullName, "ZserioParser: Parser errors.");

        checkPackageName(retVal, inputFileName, initialFile);
        checkImports(retVal);

        // call new AST nodes evaluator
        retVal.evaluateAll();

        return retVal;
    }

    private String getExpectedPackageName(String inputFileName)
    {
        String expectedPkgName = inputFileName;
        expectedPkgName = expectedPkgName.substring(0, expectedPkgName.lastIndexOf(inputFileExtension));
        expectedPkgName = expectedPkgName.replace(File.separatorChar, '.');

        return expectedPkgName;
    }

    // FIXME: This should be replaced by a smart Package AST class, but the current design is quite different.
    private String getPackageNameFromNode(AST importNode)
    {
        StringJoinUtil.Joiner sb = new StringJoinUtil.Joiner(Package.SEPARATOR);
        for (AST child = importNode.getFirstChild(); child != null; child = child.getNextSibling())
            sb.append(child.getText());

        return sb.toString();
    }

    // FIXME: This should be replaced by a smart Package AST class, but the current design is quite different.
    private void checkPackageName(AST packageRoot, String inputFileName, boolean initialFile)
            throws ParserException
    {
        final TokenAST node = (TokenAST)packageRoot.getFirstChild();
        // node is null only for empty input file
        if (node == null || node.getType() != ZserioParserTokenTypes.PACKAGE)
        {
            // no package name specified
            if (initialFile)
                return; // it's ok for the initial (first) zserio file

            // imported files must include "package ..."
            throw new ParserException(node, "ZserioParser: Package declaration missing in imported file.");
        }

        final String expectedPkgName = getExpectedPackageName(inputFileName);
        final String actualPkgName = getPackageNameFromNode(node);
        if (!actualPkgName.equals(expectedPkgName))
        {
            // file name and package name differ - this is a fatal error as it breaks imports badly
            throw new ParserException(node, "ZserioParser: File name and package name do not match!");
        }
    }

    private void checkImports(AST packageRoot) throws ParserException
    {
        final TokenAST node = (TokenAST)packageRoot.getFirstChild();

        // files that start with import are wrong (this implies they don't contain 'package' specification
        // but files without package can't use imports)
        // node is null only for empty input file
        if (node != null && node.getType() == ZserioParserTokenTypes.IMPORT)
        {
            throw new ParserException(node,
                    "ZserioParser: input file without 'package' specification can't use imports!");
        }
    }

    private void detectInputFileExtension(String inputFileName)
    {
        String fileName = (new File(inputFileName)).getName();
        final int i = fileName.lastIndexOf(".");
        if (i > 0)
            inputFileExtension = fileName.substring(i);
    }

    private String getInputFileFullName(String inputFileName)
    {
        final String srcPathName = commandLineArguments.getSrcPathName();

        return (srcPathName == null) ? inputFileName : new File(srcPathName, inputFileName).toString();
    }

    private static TokenAST createRootToken(CommonHiddenStreamToken hiddenTokenBefore)
    {
        final Token token = new FileNameLexerToken(ZserioParserTokenTypes.ROOT, "ROOT", hiddenTokenBefore);

        return new DefaultToken(token);
    }

    private static TokenAST parseComment(String commentFileName, int commentTokenType)
            throws IOException, ParserException
    {
        ZserioToolPrinter.printMessage("Parsing " + commentFileName);

        final String docComment = readFileToString(commentFileName);
        final FileNameLexerToken commentLexerToken = new FileNameLexerToken(commentTokenType, docComment);
        final TokenAST rootToken = createRootToken(commentLexerToken);

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
            if (TokenAST.isKeyword(token.getType()))
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

    private final ZserioEmitter emitter;

    private final InputFileManager inputFileManager;
    private String inputFileExtension = "";
    private final CommandLineArguments commandLineArguments;
    private final ExtensionManager extensionManager;

    private final TokenAST rootNode;
}
