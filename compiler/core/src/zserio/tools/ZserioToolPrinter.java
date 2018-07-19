package zserio.tools;

import zserio.antlr.util.BaseTokenAST;

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
     * Prints warning which is localized by base AST token.
     *
     * @param orignToken AST token to which the warning belongs to.
     * @param text       Text of the warning to print.
     */
    public static void printWarning(BaseTokenAST originToken, String text)
    {
        if (originToken == null)
            printWarning(null, 0, 0, text);
        else
            printWarning(originToken.getFileName(), originToken.getLine(), originToken.getColumn(), text);
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
     * Prints error which is localized in the Zserio source file.
     *
     * @param fileName Zserio source file to which the error belongs to.
     * @param line     Line number of the Zserio source file.
     * @param column   Column number of the Zserio source file.
     * @param text     Text of the error to print.
     */
    public static void printError(String fileName, int line, int column, String text)
    {
        printWarningOrError("[ERROR] ", fileName, line, column, text);
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
     * Prints general message.
     *
     * @param message Message to print.
     */
    public static void printMessage(String message)
    {
        System.out.println(message);
    }

    private static void printWarningOrError(String prefix, String fileName, int line, int column, String text)
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

        System.err.println(textBuffer);
    }
}
