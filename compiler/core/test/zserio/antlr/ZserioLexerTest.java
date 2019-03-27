package zserio.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import static org.junit.Assert.*;
import org.junit.Test;

public class ZserioLexerTest
{
    @Test
    public void operators()
    {
        final CharStream input = CharStreams.fromString(
                "& " +
                "&= " +
                "= " +
                "! " +
                ": " +
                ", " +
                "/ " +
                "/= " +
                ". " +
                ":: " +
                "== " +
                ">= " +
                "> " +
                "{ " +
                "[ " +
                "<= " +
                "&& " +
                "|| " +
                "( " +
                "<< " +
                "<<= " +
                "< " +
                "- " +
                "-= " +
                "% " +
                "%= " +
                "* " +
                "*= " +
                "!= " +
                "| " +
                "|= " +
                "+ " +
                "+= " +
                "? " +
                "} " +
                "] " +
                ") " +
                ">> " +
                ">>= " +
                "; " +
                "~ " +
                "^ " +
                "^=\n"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.AND);
        checkToken(lexer, Zserio4Lexer.AND_ASSIGN);
        checkToken(lexer, Zserio4Lexer.ASSIGN);
        checkToken(lexer, Zserio4Lexer.BANG);
        checkToken(lexer, Zserio4Lexer.COLON);
        checkToken(lexer, Zserio4Lexer.COMMA);
        checkToken(lexer, Zserio4Lexer.DIVIDE);
        checkToken(lexer, Zserio4Lexer.DIVIDE_ASSIGN);
        checkToken(lexer, Zserio4Lexer.DOT);
        checkToken(lexer, Zserio4Lexer.DOUBLE_COLON);
        checkToken(lexer, Zserio4Lexer.EQ);
        checkToken(lexer, Zserio4Lexer.GE);
        checkToken(lexer, Zserio4Lexer.GT);
        checkToken(lexer, Zserio4Lexer.LBRACE);
        checkToken(lexer, Zserio4Lexer.LBRACKET);
        checkToken(lexer, Zserio4Lexer.LE);
        checkToken(lexer, Zserio4Lexer.LOGICAL_AND);
        checkToken(lexer, Zserio4Lexer.LOGICAL_OR);
        checkToken(lexer, Zserio4Lexer.LPAREN);
        checkToken(lexer, Zserio4Lexer.LSHIFT);
        checkToken(lexer, Zserio4Lexer.LSHIFT_ASSIGN);
        checkToken(lexer, Zserio4Lexer.LT);
        checkToken(lexer, Zserio4Lexer.MINUS);
        checkToken(lexer, Zserio4Lexer.MINUS_ASSIGN);
        checkToken(lexer, Zserio4Lexer.MODULO);
        checkToken(lexer, Zserio4Lexer.MODULO_ASSIGN);
        checkToken(lexer, Zserio4Lexer.MULTIPLY);
        checkToken(lexer, Zserio4Lexer.MULTIPLY_ASSIGN);
        checkToken(lexer, Zserio4Lexer.NE);
        checkToken(lexer, Zserio4Lexer.OR);
        checkToken(lexer, Zserio4Lexer.OR_ASSIGN);
        checkToken(lexer, Zserio4Lexer.PLUS);
        checkToken(lexer, Zserio4Lexer.PLUS_ASSIGN);
        checkToken(lexer, Zserio4Lexer.QUESTIONMARK);
        checkToken(lexer, Zserio4Lexer.RBRACE);
        checkToken(lexer, Zserio4Lexer.RBRACKET);
        checkToken(lexer, Zserio4Lexer.RPAREN);
        checkToken(lexer, Zserio4Lexer.RSHIFT);
        checkToken(lexer, Zserio4Lexer.RSHIFT_ASSIGN);
        checkToken(lexer, Zserio4Lexer.SEMICOLON);
        checkToken(lexer, Zserio4Lexer.TILDE);
        checkToken(lexer, Zserio4Lexer.XOR);
        checkToken(lexer, Zserio4Lexer.XOR_ASSIGN);

        checkEOF(lexer);
    }

    @Test
    public void keywords()
    {
        final CharStream input = CharStreams.fromString(
                "align " +
                "bit " +
                "bool " +
                "case " +
                "choice " +
                "const " +
                "default " +
                "enum " +
                "explicit " +
                "float16 " +
                "float32 " +
                "float64 " +
                "function " +
                "if " +
                "implicit " +
                "import " +
                "@index " +
                "int " +
                "int16 " +
                "int32 " +
                "int64 " +
                "int8 " +
                "lengthof " +
                "numbits " +
                "on " +
                "optional " +
                "package " +
                "return " +
                "rpc " +
                "service " +
                "sql_database " +
                "sql_table " +
                "sql_virtual " +
                "sql_without_rowid " +
                "stream " +
                "string " +
                "struct " +
                "subtype " +
                "sum " +
                "uint16 " +
                "uint32 " +
                "uint64 " +
                "uint8 " +
                "union " +
                "valueof " +
                "varint " +
                "varint16 " +
                "varint32 " +
                "varint64 " +
                "varuint " +
                "varuint16 " +
                "varuint32 " +
                "varuint64\n"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.ALIGN);
        checkToken(lexer, Zserio4Lexer.BIT_FIELD);
        checkToken(lexer, Zserio4Lexer.BOOL);
        checkToken(lexer, Zserio4Lexer.CASE);
        checkToken(lexer, Zserio4Lexer.CHOICE);
        checkToken(lexer, Zserio4Lexer.CONST);
        checkToken(lexer, Zserio4Lexer.DEFAULT);
        checkToken(lexer, Zserio4Lexer.ENUM);
        checkToken(lexer, Zserio4Lexer.EXPLICIT);
        checkToken(lexer, Zserio4Lexer.FLOAT16);
        checkToken(lexer, Zserio4Lexer.FLOAT32);
        checkToken(lexer, Zserio4Lexer.FLOAT64);
        checkToken(lexer, Zserio4Lexer.FUNCTION);
        checkToken(lexer, Zserio4Lexer.IF);
        checkToken(lexer, Zserio4Lexer.IMPLICIT);
        checkToken(lexer, Zserio4Lexer.IMPORT);
        checkToken(lexer, Zserio4Lexer.INDEX);
        checkToken(lexer, Zserio4Lexer.INT_FIELD);
        checkToken(lexer, Zserio4Lexer.INT16);
        checkToken(lexer, Zserio4Lexer.INT32);
        checkToken(lexer, Zserio4Lexer.INT64);
        checkToken(lexer, Zserio4Lexer.INT8);
        checkToken(lexer, Zserio4Lexer.LENGTHOF);
        checkToken(lexer, Zserio4Lexer.NUMBITS);
        checkToken(lexer, Zserio4Lexer.ON);
        checkToken(lexer, Zserio4Lexer.OPTIONAL);
        checkToken(lexer, Zserio4Lexer.PACKAGE);
        checkToken(lexer, Zserio4Lexer.RETURN);
        checkToken(lexer, Zserio4Lexer.RPC);
        checkToken(lexer, Zserio4Lexer.SERVICE);
        checkToken(lexer, Zserio4Lexer.SQL_DATABASE);
        checkToken(lexer, Zserio4Lexer.SQL_TABLE);
        checkToken(lexer, Zserio4Lexer.SQL_VIRTUAL);
        checkToken(lexer, Zserio4Lexer.SQL_WITHOUT_ROWID);
        checkToken(lexer, Zserio4Lexer.STREAM);
        checkToken(lexer, Zserio4Lexer.STRING);
        checkToken(lexer, Zserio4Lexer.STRUCTURE);
        checkToken(lexer, Zserio4Lexer.SUBTYPE);
        checkToken(lexer, Zserio4Lexer.SUM);
        checkToken(lexer, Zserio4Lexer.UINT16);
        checkToken(lexer, Zserio4Lexer.UINT32);
        checkToken(lexer, Zserio4Lexer.UINT64);
        checkToken(lexer, Zserio4Lexer.UINT8);
        checkToken(lexer, Zserio4Lexer.UNION);
        checkToken(lexer, Zserio4Lexer.VALUEOF);
        checkToken(lexer, Zserio4Lexer.VARINT);
        checkToken(lexer, Zserio4Lexer.VARINT16);
        checkToken(lexer, Zserio4Lexer.VARINT32);
        checkToken(lexer, Zserio4Lexer.VARINT64);
        checkToken(lexer, Zserio4Lexer.VARUINT);
        checkToken(lexer, Zserio4Lexer.VARUINT16);
        checkToken(lexer, Zserio4Lexer.VARUINT32);
        checkToken(lexer, Zserio4Lexer.VARUINT64);

        checkEOF(lexer);
    }

    @Test
    public void docComment()
    {
        final CharStream input = CharStreams.fromString(
                "/** doc comment */\n" +
                "/**\n" +
                " * doc comment\n" +
                " * multiline\n" +
                " */"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.DOC_COMMENT, Zserio4Lexer.DOC, "/** doc comment */");
        checkToken(lexer, Zserio4Lexer.DOC_COMMENT, Zserio4Lexer.DOC, "/**\n * doc comment\n * multiline\n */");

        checkEOF(lexer);
    }

    @Test
    public void blockComment()
    {
        final CharStream input = CharStreams.fromString(
                "/* block comment */\n" +
                "/* block comment\n" +
                "   multiline */"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.BLOCK_COMMENT, Lexer.HIDDEN, "/* block comment */");
        checkToken(lexer, Zserio4Lexer.BLOCK_COMMENT, Lexer.HIDDEN, "/* block comment\n   multiline */");

        checkEOF(lexer);
    }

    @Test
    public void lineComment()
    {
        final CharStream input = CharStreams.fromString(
                "// line comment\n" +
                "// line comment at the end"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.LINE_COMMENT, Lexer.HIDDEN, "// line comment");
        checkToken(lexer, Zserio4Lexer.LINE_COMMENT, Lexer.HIDDEN, "// line comment at the end");

        checkEOF(lexer);
    }

    @Test
    public void boolLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "true false false true"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.BOOL_LITERAL, "true");
        checkToken(lexer, Zserio4Lexer.BOOL_LITERAL, "false");
        checkToken(lexer, Zserio4Lexer.BOOL_LITERAL, "false");
        checkToken(lexer, Zserio4Lexer.BOOL_LITERAL, "true");

        checkEOF(lexer);
    }

    @Test
    public void stringLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "\"true\" \"0\" \"text\" \"with \\\"escaped\\\" string\" " +
                "\"multiple escapes \\\\\" \"more \\\\\\\"escapes\\\""
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.STRING_LITERAL, "\"true\"");
        checkToken(lexer, Zserio4Lexer.STRING_LITERAL, "\"0\"");
        checkToken(lexer, Zserio4Lexer.STRING_LITERAL, "\"text\"");
        checkToken(lexer, Zserio4Lexer.STRING_LITERAL, "\"with \\\"escaped\\\" string\"");
        checkToken(lexer, Zserio4Lexer.STRING_LITERAL, "\"multiple escapes \\\\\"");
        checkToken(lexer, Zserio4Lexer.STRING_LITERAL, "\"more \\\\\\\"escapes\\\"");

        checkEOF(lexer);
    }

    @Test
    public void binaryLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "1001b 0101b 00b 0B 1B 101B"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.BINARY_LITERAL, "1001b");
        checkToken(lexer, Zserio4Lexer.BINARY_LITERAL, "0101b");
        checkToken(lexer, Zserio4Lexer.BINARY_LITERAL, "00b");
        checkToken(lexer, Zserio4Lexer.BINARY_LITERAL, "0B");
        checkToken(lexer, Zserio4Lexer.BINARY_LITERAL, "1B");
        checkToken(lexer, Zserio4Lexer.BINARY_LITERAL, "101B");

        checkEOF(lexer);
    }

    @Test
    public void octalLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "0101 0770 012 00 01234567"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.OCTAL_LITERAL, "0101");
        checkToken(lexer, Zserio4Lexer.OCTAL_LITERAL, "0770");
        checkToken(lexer, Zserio4Lexer.OCTAL_LITERAL, "012");
        checkToken(lexer, Zserio4Lexer.OCTAL_LITERAL, "00");
        checkToken(lexer, Zserio4Lexer.OCTAL_LITERAL, "01234567");

        checkEOF(lexer);
    }

    @Test
    public void hexadecimalLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "0x12 0x0 0xFEDCBA98 0xFF 0x1 0xabcdef00 0x1f"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.HEXADECIMAL_LITERAL, "0x12");
        checkToken(lexer, Zserio4Lexer.HEXADECIMAL_LITERAL, "0x0");
        checkToken(lexer, Zserio4Lexer.HEXADECIMAL_LITERAL, "0xFEDCBA98");
        checkToken(lexer, Zserio4Lexer.HEXADECIMAL_LITERAL, "0xFF");
        checkToken(lexer, Zserio4Lexer.HEXADECIMAL_LITERAL, "0x1");
        checkToken(lexer, Zserio4Lexer.HEXADECIMAL_LITERAL, "0xabcdef00");
        checkToken(lexer, Zserio4Lexer.HEXADECIMAL_LITERAL, "0x1f");

        checkEOF(lexer);
    }

    @Test
    public void floatLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "0e1 0e-1 1e2f 9. 9.12 9.f 9.1f 9.e2 9.e2f 9.e-2f .0 .0e-1 .0f .1e3f"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, "0e1");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, "0e-1");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, "1e2f");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, "9.");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, "9.12");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, "9.f");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, "9.1f");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, "9.e2");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, "9.e2f");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, "9.e-2f");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, ".0");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, ".0e-1");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, ".0f");
        checkToken(lexer, Zserio4Lexer.FLOAT_LITERAL, ".1e3f");

        checkEOF(lexer);
    }

    @Test
    public void decimalLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "0 19 99 987654321"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.DECIMAL_LITERAL, "0");
        checkToken(lexer, Zserio4Lexer.DECIMAL_LITERAL, "19");
        checkToken(lexer, Zserio4Lexer.DECIMAL_LITERAL, "99");
        checkToken(lexer, Zserio4Lexer.DECIMAL_LITERAL, "987654321");

        checkEOF(lexer);
    }

    @Test
    public void id()
    {
        final CharStream input = CharStreams.fromString(
                "id _id i1 _1 _ __ I _I s0mE_UgLy_id"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.ID, "id");
        checkToken(lexer, Zserio4Lexer.ID, "_id");
        checkToken(lexer, Zserio4Lexer.ID, "i1");
        checkToken(lexer, Zserio4Lexer.ID, "_1");
        checkToken(lexer, Zserio4Lexer.ID, "_");
        checkToken(lexer, Zserio4Lexer.ID, "__");
        checkToken(lexer, Zserio4Lexer.ID, "I");
        checkToken(lexer, Zserio4Lexer.ID, "_I");
        checkToken(lexer, Zserio4Lexer.ID, "s0mE_UgLy_id");

        checkEOF(lexer);
    }

    @Test
    public void invalidToken()
    {
        final CharStream input = CharStreams.fromString(
                "01abc 0x13fx 01278"
        );

        Zserio4Lexer lexer = new Zserio4Lexer(input);

        checkToken(lexer, Zserio4Lexer.INVALID_TOKEN, "01abc");
        checkToken(lexer, Zserio4Lexer.INVALID_TOKEN, "0x13fx");
        checkToken(lexer, Zserio4Lexer.INVALID_TOKEN, "01278");

        checkEOF(lexer);
    }

    private void checkToken(Lexer lexer, int type)
    {
        checkToken(lexer, type, null);
    }

    private void checkToken(Lexer lexer, int type, String text)
    {
        checkToken(lexer, type, Lexer.DEFAULT_TOKEN_CHANNEL, text);
    }

    private void checkToken(Lexer lexer, int type, int channel, String text)
    {
        final Token token = lexer.nextToken();
        assertNotNull(token);
        assertEquals("expecting token '" + type + ":" + lexer.getVocabulary().getSymbolicName(type) + "' - ",
                type, token.getType());
        assertEquals("expecting channel '" + channel + ":" + lexer.getChannelNames()[channel] + "' - ",
                channel, token.getChannel());
        if (text != null)
            assertEquals("expecting text \"" + text + "\" - ", text, token.getText());
    }

    private void checkEOF(Lexer lexer)
    {
        final Token token = lexer.nextToken();
        assertEquals(Lexer.EOF, token.getType());
    }
}
