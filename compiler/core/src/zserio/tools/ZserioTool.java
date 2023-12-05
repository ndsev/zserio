package zserio.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.cli.ParseException;

import zserio.antlr.ZserioLexer;
import zserio.antlr.ZserioParser;
import zserio.antlr.util.ParseErrorListener;
import zserio.ast.Import;
import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.ParserException;
import zserio.ast.ParserStackedException;
import zserio.ast.Root;
import zserio.ast.ZserioAstBuilder;
import zserio.ast.ZserioAstChecker;
import zserio.ast.ZserioAstEvaluator;
import zserio.ast.ZserioAstSymbolResolver;
import zserio.ast.ZserioAstScopeSetter;
import zserio.ast.ZserioAstTemplator;
import zserio.ast.ZserioAstImporter;
import zserio.ast.ZserioAstTypeResolver;
import zserio.ast.ZserioParseTreeChecker;
import zserio.extension.common.ZserioExtensionException;

/**
 * The main class for Zserio tool.
 */
public final class ZserioTool
{
    /**
     * Zserio tool executor.
     */
    public enum Executor
    {
        /**
         * Zserio tool has been executed from Java.
         */
        JAVA_MAIN,

        /**
         * Zserio tool has been executed from Ant task.
         */
        ANT_TASK_MAIN,

        /**
         * Zserio tool has been executed from Python command line.
         */
        PYTHON_MAIN
    }

    /**
     * The entry point of Zserio tool for Java.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        if (!runTool(args, Executor.JAVA_MAIN))
            System.exit(1);
    }

    /**
     * The entry point of Zserio tool common for all executors.
     *
     * @param args Command line arguments.
     * @param executor Specifies in which way the Zserio tool has been executed.
     *
     * @return Returns true in case of success, otherwise returns false.
     */
    public static boolean runTool(String[] args, Executor executor)
    {
        try
        {
            final ZserioTool zserioTool = new ZserioTool(executor);
            zserioTool.execute(args);
        }
        catch (org.apache.commons.cli.ParseException exception)
        {
            ZserioToolPrinter.printError(exception.getMessage());
            return false;
        }
        catch (ZserioExtensionException exception)
        {
            ZserioToolPrinter.printError(exception.getMessage());
            return false;
        }
        catch (ParserStackedException exception)
        {
            for (ParserStackedException.Message message : exception.getMessageStack())
                ZserioToolPrinter.printError(message.getLocation(), message.getMessage());
            ZserioToolPrinter.printError(exception.getLocation(), exception.getMessage());
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

    private ZserioTool(Executor executor)
    {
        commandLineArguments = new CommandLineArguments(executor);
        // TODO[Mi-L@]: Note that the managers get command line arguments in constructor, but can use it only
        //              after parse is called!
        resourceManager = new ResourceManager(commandLineArguments);
        inputFileManager = new InputFileManager(commandLineArguments);
        extensionManager = new ExtensionManager(commandLineArguments);
    }

    private void execute(String[] args) throws Exception
    {
        commandLineArguments.parse(args);
        if (commandLineArguments.hasHelpOption())
        {
            commandLineArguments.printHelp(extensionManager.getExtensions());
        }
        else if (commandLineArguments.hasVersionOption())
        {
            commandLineArguments.printVersion(extensionManager.getExtensions());
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
        final ExtensionParameters parameters = new ZserioExtensionParameters(commandLineArguments,
                inputFileManager.getLastModifiedTime(), resourceManager.getLastModifiedTime());
        extensionManager.callExtensions(rootNode, parameters);
        ZserioToolPrinter.printMessage("Done");
    }

    private Root parse() throws Exception
    {
        final String inputFileName = commandLineArguments.getInputFileName();
        final WarningsConfig warningsConfig = commandLineArguments.getWarningsConfig();
        final ZserioAstBuilder astBuilder = new ZserioAstBuilder(
                commandLineArguments.getTopLevelPackageNameIds(), inputFileName, inputFileManager,
                warningsConfig);

        final String inputFileFullName = inputFileManager.getFileFullName(inputFileName);
        final Package parsedPackage = parsePackage(astBuilder, inputFileFullName);
        parseImportedPackages(astBuilder, parsedPackage);

        final Root rootNode = astBuilder.getAst();

        final ZserioAstImporter importer = new ZserioAstImporter(warningsConfig);
        rootNode.accept(importer);

        final ZserioAstTypeResolver typeResolver = new ZserioAstTypeResolver();
        rootNode.accept(typeResolver);

        final ZserioAstTemplator templator = new ZserioAstTemplator(typeResolver, warningsConfig);
        rootNode.accept(templator);

        final ZserioAstScopeSetter scopeSetter = new ZserioAstScopeSetter();
        rootNode.accept(scopeSetter);

        final ZserioAstSymbolResolver symbolResolver = new ZserioAstSymbolResolver(warningsConfig);
        rootNode.accept(symbolResolver);

        final ZserioAstEvaluator evaluator = new ZserioAstEvaluator();
        rootNode.accept(evaluator);

        final ZserioAstChecker checker = new ZserioAstChecker(
                warningsConfig, commandLineArguments.getWithGlobalRuleIdCheck());
        rootNode.accept(checker);

        return rootNode;
    }

    private Package parsePackage(ZserioAstBuilder astBuilder, String inputFileFullName) throws Exception
    {
        ZserioToolPrinter.printMessage("Parsing " + inputFileFullName);

        final CharStream inputStream = CharStreams.fromFileName(inputFileFullName, StandardCharsets.UTF_8);
        final ParseErrorListener parseErrorListener = new ParseErrorListener();
        final ZserioLexer lexer = new ZserioLexer(inputStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(parseErrorListener);
        final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        final ZserioParser parser = new ZserioParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(parseErrorListener);

        final ParseTree tree = parser.packageDeclaration();

        final ZserioParseTreeChecker parseTreeChecker = new ZserioParseTreeChecker(
                commandLineArguments.getWarningsConfig(), commandLineArguments.getAllowImplicitArrays());
        parseTreeChecker.visit(tree);

        final Package parsedPackage = (Package)astBuilder.visit(tree, tokenStream);

        // register after successful parsing to prevent timestamp warning to be fired before parsing errors
        inputFileManager.registerFile(inputFileFullName);

        return parsedPackage;
    }

    private void parseImportedPackages(ZserioAstBuilder astBuilder, Package parentPackage) throws Exception
    {
        final Iterable<Import> imports = parentPackage.getImports();
        for (Import importNode : imports)
        {
            final PackageName importedPackageName = importNode.getImportedPackageName();
            final String inputFileFullName = inputFileManager.getFileFullName(importedPackageName);
            if (!inputFileManager.isFileRegistered(inputFileFullName))
            {
                final Package parsedPackage = parsePackage(astBuilder, inputFileFullName);
                parseImportedPackages(astBuilder, parsedPackage);
            }
        }
    }

    private final CommandLineArguments commandLineArguments;
    private final ResourceManager resourceManager;
    private final InputFileManager inputFileManager;
    private final ExtensionManager extensionManager;
}
