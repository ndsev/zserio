package zserio.antlr.util;

import java.io.Serializable;

import antlr.CommonHiddenStreamToken;

/**
 * Lexer token which supports source file name.
 */
public class FileNameLexerToken extends CommonHiddenStreamToken implements Serializable
{
    /**
     * Default constructor.
     */
    public FileNameLexerToken()
    {
    }

    /**
     * Constructor from lexer token type and text.
     *
     * @param type Lexer token type.
     * @param text Lexer token text.
     */
    public FileNameLexerToken(int type, String text)
    {
        super(type, text);
    }

    /**
     * Constructor from lexer token type, text and hidden lexer token before.
     *
     * @param type              Lexer token type.
     * @param text              Lexer token text.
     * @param hiddenTokenBefore Hidden lexer token to set before this token.
     */
    public FileNameLexerToken(int type, String text, CommonHiddenStreamToken hiddenTokenBefore)
    {
        super(type, text);
        setHiddenBefore(hiddenTokenBefore);
    }

    /**
     * Gets name of the file associated with the lexer token.
     *
     * @return File name of the lexer token.
     */
    @Override
    public String getFilename()
    {
        return fileName;
    }

    /**
     * Sets name of the file associated with the lexer token.
     *
     * @param fileName File name of the lexer token to set.
     */
    @Override
    public void setFilename(String fileName)
    {
        this.fileName = fileName;
    }

    private static final long serialVersionUID = -4938112157897962838L;

    private String fileName;
}
