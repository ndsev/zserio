package zserio.tools;

import java.io.IOException;
import java.nio.charset.Charset;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.cli.ParseException;

import zserio.antlr.Zserio4Lexer;
import zserio.antlr.Zserio4Parser;
import zserio.ast.PackageName;
import zserio.ast4.Import;
import zserio.ast4.ParseTreeCheckingVisitor;
import zserio.ast4.ParserException;
import zserio.ast4.Root;
import zserio.ast4.CheckingListener;
import zserio.ast4.PrintStringTreeListener;
import zserio.ast4.TranslationUnit;
import zserio.ast4.ZserioAstBuilderVisitor;
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
        /*catch (NoViableAltException exception)
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
        }*/
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

        showAstTree(rootNode);

        check(rootNode);

        ZserioToolPrinter.printMessage("Emitting not implemented with ATNLR4"); // TODO: emit
        // emit()

        ZserioToolPrinter.printMessage("Done");
    }

    private Root parse() throws Exception
    {
        ZserioAstBuilderVisitor astBuilderVisitor = new ZserioAstBuilderVisitor(
                commandLineArguments.getWithUnusedWarnings());

        final String inputFileName = commandLineArguments.getInputFileName();
        final String inputFileFullName = inputFileManager.getFileFullName(inputFileName);
        final TranslationUnit translationUnit = parsePackage(astBuilderVisitor, inputFileFullName);
        parseImportedPackages(astBuilderVisitor, translationUnit);

        final Root rootNode = astBuilderVisitor.getAst();
        //rootNode.resolveReferences();

        // TODO: scope evaluation
        // TODO: expression evaluation

        return rootNode;
    }

    private TranslationUnit parsePackage(ZserioAstBuilderVisitor astBuilderVisitor,
            String inputFileFullName) throws Exception
    {
        ZserioToolPrinter.printMessage("Parsing " + inputFileFullName);
        inputFileManager.registerFile(inputFileFullName);

        final CharStream inputStream = CharStreams.fromFileName(inputFileFullName, Charset.forName("UTF-8"));
        final Zserio4Lexer lexer = new Zserio4Lexer(inputStream);
        final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        final Zserio4Parser parser = new Zserio4Parser(tokenStream);
        final ParseTree tree = parser.translationUnit();

        ParseTreeCheckingVisitor parseTreeCheckingVisitor = new ParseTreeCheckingVisitor(inputFileManager);
        parseTreeCheckingVisitor.visit(tree);

        final TranslationUnit translationUnit = (TranslationUnit)astBuilderVisitor.visit(tree);

        return translationUnit;
    }

    private void parseImportedPackages(ZserioAstBuilderVisitor astBuilderVisitor,
            TranslationUnit parentTranslationUnit) throws Exception
    {
        final Iterable<Import> imports = parentTranslationUnit.getImports();
        for (Import importNode : imports)
        {
            final PackageName importedPackageName = importNode.getImportedPackageName();
            final String inputFileFullName = inputFileManager.getFileFullName(importedPackageName);
            if (!inputFileManager.isFileRegistered(inputFileFullName))
            {
                final TranslationUnit translationUnit = parsePackage(astBuilderVisitor,
                        inputFileFullName);
                parseImportedPackages(astBuilderVisitor, translationUnit);
            }
        }
    }

    private void check(Root rootNode)
    {
        ZserioToolPrinter.printMessage("Checking");

        CheckingListener checkingListener = new CheckingListener();
        rootNode.walk(checkingListener);
    }

    /*private void emit(Root rootNode) throws Exception
    {
        final ExtensionParameters parameters = new ExtensionParameters(commandLineArguments);
        extensionManager.callExtensions(parameters, rootNode);
    }*/

    private static void showAstTree(Root rootNode)
    {
        ZserioToolPrinter.printMessage("AST:");
        PrintStringTreeListener printStringTreeListener = new PrintStringTreeListener();
        rootNode.walk(printStringTreeListener);
    }

    private final InputFileManager inputFileManager;
    private final CommandLineArguments commandLineArguments;
    private final ExtensionManager extensionManager;
}
