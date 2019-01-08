package zserio.ast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
import zserio.tools.InputFileManager;
import zserio.tools.ZserioToolPrinter;

/**
 * The representation of AST TRANSLATION_UNIT node.
 *
 * This class represents a root AST node of one source file which is compiled by zserio.
 */
public class TranslationUnit extends TokenAST
{
    /**
     * Sets input file manager for this translation unit.
     *
     * @param inputFileManager Input file manager from the main class to set.
     */
    public void setInputFileManager(InputFileManager inputFileManager)
    {
        this.inputFileManager = inputFileManager;
    }

    /**
     * Gets the package which is defined in this translation unit.
     *
     * @return Package defined in this translation unit or null for empty input file.
     */
    public Package getPackage()
    {
        return unitPackage;
    }

    /**
     * Gets imports which is defined in this translation unit.
     *
     * @return List of all imports defined in this translation unit.
     */
    public List<Import> getImports()
    {
        return unitImports;
    }

    /**
     * Gets all types which is defined in this translation unit.
     *
     * @return List of all zserio types defined in this translation unit.
     */
    public List<ZserioType> getTypes()
    {
        return types;
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case ZserioParserTokenTypes.PACKAGE:
            if (!(child instanceof Package))
                return false;
            unitPackage = (Package)child;
            break;

        case ZserioParserTokenTypes.IMPORT:
            if (!(child instanceof Import))
                return false;
            final Import unitImport = (Import)child;
            unitImports.add(unitImport);
            unitPackage.addImport(unitImport);
            break;

        default:
            if (!(child instanceof ZserioType))
                return false;
            types.add((ZserioType)child);
            break;
        }

        return true;
    }

    @Override
    protected void evaluate() throws ParserException
    {
        // unitPackage can be null for empty files
        if (unitPackage != null)
        {
            // this must be checked now to avoid obscure errors if package is not stored in the same file name
            final PackageName unitPackageName = unitPackage.getPackageName();
            if (!unitPackageName.isEmpty())
            {
                // check it only if the package is not default
                final String expectedFileFullName = inputFileManager.getFileFullName(unitPackageName);
                if (!expectedFileFullName.equals(getFileName()))
                    throw new ParserException(unitPackage, "Package '" + unitPackageName.toString() +
                                "' does not match to the source file name!");
            }
        }
    }

    @Override
    protected void check() throws ParserException
    {
        final String unitFileName = getFileName();
        if (unitFileName != null)
        {
            // unit file name can be null for empty files
            final byte fileContent[] = readFile(unitFileName);

            checkUtf8Encoding(fileContent);
            checkNonPrintableCharacters(fileContent);
        }
    }

    private byte[] readFile(String fileName) throws ParserException
    {
        final File file = new File(fileName);
        FileInputStream inputStream = null;
        byte fileContent[];
        try
        {
            inputStream = new FileInputStream(file);
            fileContent = new byte[(int)file.length()];
            if (inputStream.read(fileContent) == -1)
                throw new ParserException(this, "Error during reading of source file " + fileName + "!");
        }
        catch (FileNotFoundException exception)
        {
            throw new ParserException(this, "Source file '" + fileName + "' cannot be found again!");
        }
        catch (IOException exception)
        {
            throw new ParserException(this, "Source file '" + fileName + "' cannot be read again!");
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

    private void checkUtf8Encoding(byte[] fileContent)
    {
        try
        {
            final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            decoder.decode(ByteBuffer.wrap(fileContent));
        }
        catch (CharacterCodingException exception)
        {
            ZserioToolPrinter.printWarning(this, "Found non-UTF8 encoded characters.");
        }
    }

    private void checkNonPrintableCharacters(byte[] fileContent)
    {
        final String content = new String(fileContent, Charset.forName("UTF-8"));

        if (content.indexOf('\t') >= 0)
            ZserioToolPrinter.printWarning(this, "Found tab characters.");

        for (int i = 0; i < content.length(); ++i)
        {
            final char character = content.charAt(i);
            if (character < '\u0020' && character != '\r' && character != '\n' && character != '\t')
            {
                ZserioToolPrinter.printWarning(this, "Found non-printable ASCII characters.");
                break;
            }
        }
    }

    private static final long serialVersionUID = -1L;

    private InputFileManager inputFileManager;
    private Package unitPackage = null;
    private final List<Import> unitImports = new ArrayList<Import>();
    private final List<ZserioType> types = new ArrayList<ZserioType>();
}
