package zserio.tools;

import zserio.ast.AstLocation;
import zserio.ast.AstNode;

/**
 * Class with static methods to handle all printing possibilities from Zserio Tool.
 */
public class ZserioToolPrinter
{
    /**
     * Prints warning which is localized in the Zserio source file.
     *
     * @param fileName Zserio source file to which the warning belongs to.
     * @param line     Line number of the Zserio source file.
     * @param column   Column number of the Zserio source file.
     * @param text     Text of the warning to print.
     */
    public static void printWarning(String fileName, int line, int column, String text)
    {
        printWarningOrError("[WARNING] ", fileName, line, column, text);
    }

    /**
     * Prints warning which is localized by base AST node.
     *
     * @param astNode AST node to which the warning belongs to.
     * @param text      Text of the warning to print.
     */
    public static void printWarning(AstNode astNode, String text)
    {
        if (astNode == null)
            printWarning(null, 0, 0, text);
        else
            printWarning(astNode.getLocation(), text);
    }

    /**
     * Prints warning which is localized by AST node location.
     *
     * @param astNodeLocation   AST node location.
     * @param text              Text of the warning to print.
     */
    public static void printWarning(AstLocation location, String text)
    {
        printWarning(location.getFileName(), location.getLine(), location.getColumn(), text);
    }

    /**
     * Prints warning which is localized in the Zserio source file.
     *
     * @param fileName Zserio source file to which the warning belongs to.
     * @param text     Text of the warning to print.
     */
    public static void printWarning(String fileName, String text)
    {
        printWarning(fileName, 0, 0, text);
    }

    /**
     * Prints error which is localized by AST node location.
     *
     * @param astNodeLocation AST node location.
     * @param text            Text of the error to print.
     */
    public static void printError(AstLocation location, String text)
    {
        printError(location.getFileName(), location.getLine(), location.getColumn(), text);
    }

    /**
     * Prints error which is localized in the Zserio source file.
     *
     * @param fileName Zserio source file to which the error belongs to.
     * @param line     Line number of the Zserio source file.
     * @param column   Column number of the Zserio source file.
     * @param text     Text of the error to print.
     */
    public static void printError(String fileName, int line, int column, String text)
    {
        printError(fileName, line, column, text, false);
    }

    /**
     * Prints error which is localized in the Zserio source file.
     *
     * @param fileName Zserio source file to which the error belongs to.
     * @param text     Text of the error to print.
     */
    public static void printError(String fileName, String text)
    {
        printError(fileName, 0, 0, text);
    }

    /**
     * Prints error.
     *
     * @param text Text of the error to print.
     */
    public static void printError(String text)
    {
        printError(null, 0, 0, text);
    }

    /**
     * Prints error prefix without new line character.
     *
     * @param text Text of the error prefix to print.
     */
    public static void printErrorPrefix(String text)
    {
        printError(null, 0, 0, text, true);
    }

    /**
     * Prints general message.
     *
     * @param message Message to print.
     */
    public static void printMessage(String message)
    {
        System.out.println(message);
    }

    private static void printError(String fileName, int line, int column, String text, boolean withoutNewLine)
    {
        printWarningOrError("[ERROR] ", fileName, line, column, text, withoutNewLine);
    }

    private static void printWarningOrError(String prefix, String fileName, int line, int column, String text)
    {
        printWarningOrError(prefix, fileName, line, column, text, false);
    }

    private static void printWarningOrError(String prefix, String fileName, int line, int column, String text,
            boolean withoutNewLine)
    {
        StringBuffer textBuffer = new StringBuffer();
        textBuffer.append(prefix);
        if (fileName != null)
        {
            textBuffer.append(fileName);
            if (line != 0 && column != 0)
            {
                textBuffer.append(":");
                textBuffer.append(line);
                textBuffer.append(":");
                textBuffer.append(column);
            }
            textBuffer.append(": ");
        }
        textBuffer.append(text);

        if (withoutNewLine)
            System.err.print(textBuffer);
        else
            System.err.println(textBuffer);
    }
}
