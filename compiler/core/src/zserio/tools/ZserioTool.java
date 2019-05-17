package zserio.tools;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.NoSuchFileException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.cli.ParseException;

import zserio.antlr.ZserioLexer;
import zserio.antlr.ZserioParser;
import zserio.antlr.util.ParseErrorListener;
import zserio.antlr.util.ParserException;
import zserio.ast.Import;
import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.Root;
import zserio.ast.ZserioAstBuilder;
import zserio.ast.ZserioAstChecker;
import zserio.ast.ZserioAstEvaluator;
import zserio.ast.ZserioAstResolver;
import zserio.ast.ZserioAstScopeSetter;
import zserio.ast.ZserioParseTreeChecker;
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
        catch (ParserException exception)
        {
            ZserioToolPrinter.printError(exception.getLocation(), exception.getMessage());
            return false;
        }
        catch (NoSuchFileException exception)
        {
            ZserioToolPrinter.printError(exception.getMessage() + ": No such file!");
            return false;
        }
        catch (IOException exception)
        {
            ZserioToolPrinter.printError(exception.getMessage());
            return false;
        }
        catch (Throwable exception)
        {
            ZserioToolPrinter.printErrorPrefix("Internal error: ");
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
        final Root rootNode = parse();
        emit(rootNode);
        ZserioToolPrinter.printMessage("Done");
    }

    private Root parse() throws Exception
    {
        final ZserioAstBuilder astBuilderVisitor = new ZserioAstBuilder();

        final String inputFileName = commandLineArguments.getInputFileName();
        final String inputFileFullName = inputFileManager.getFileFullName(inputFileName);
        final Package parsedPackage = parsePackage(astBuilderVisitor, inputFileFullName);
        parseImportedPackages(astBuilderVisitor, parsedPackage);

        final Root rootNode = astBuilderVisitor.getAst();

        final ZserioAstScopeSetter scopeSetter = new ZserioAstScopeSetter();
        rootNode.accept(scopeSetter);

        final ZserioAstResolver resolver = new ZserioAstResolver();
        rootNode.accept(resolver);

        final ZserioAstEvaluator evaluator = new ZserioAstEvaluator();
        rootNode.accept(evaluator);

        final ZserioAstChecker checker = new ZserioAstChecker(commandLineArguments.getWithUnusedWarnings());
        rootNode.accept(checker);

        return rootNode;
    }

    private Package parsePackage(ZserioAstBuilder astBuilder,
            String inputFileFullName) throws Exception
    {
        ZserioToolPrinter.printMessage("Parsing " + inputFileFullName);

        inputFileManager.registerFile(inputFileFullName);

        final CharStream inputStream = CharStreams.fromFileName(inputFileFullName, Charset.forName("UTF-8"));
        final ParseErrorListener parseErrorListener = new ParseErrorListener();
        final ZserioLexer lexer = new ZserioLexer(inputStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(parseErrorListener);
        final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        final ZserioParser parser = new ZserioParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(parseErrorListener);

        final ParseTree tree = parser.packageDeclaration();

        final ZserioParseTreeChecker parseTreeChecker = new ZserioParseTreeChecker(inputFileManager);
        parseTreeChecker.visit(tree);

        final Package parsedPackage = (Package)astBuilder.visit(tree, tokenStream);

        return parsedPackage;
    }

    private void parseImportedPackages(ZserioAstBuilder astBuilderVisitor,
            Package parentPackage) throws Exception
    {
        final Iterable<Import> imports = parentPackage.getImports();
        for (Import importNode : imports)
        {
            final PackageName importedPackageName = importNode.getImportedPackageName();
            final String inputFileFullName = inputFileManager.getFileFullName(importedPackageName);
            if (!inputFileManager.isFileRegistered(inputFileFullName))
            {
                final Package parsedPackage = parsePackage(astBuilderVisitor, inputFileFullName);
                parseImportedPackages(astBuilderVisitor, parsedPackage);
            }
        }
    }

    private void emit(Root rootNode) throws Exception
    {
        final ExtensionParameters parameters = new ExtensionParameters(commandLineArguments);
        extensionManager.callExtensions(parameters, rootNode);
    }

    private final InputFileManager inputFileManager;
    private final CommandLineArguments commandLineArguments;
    private final ExtensionManager extensionManager;
}
