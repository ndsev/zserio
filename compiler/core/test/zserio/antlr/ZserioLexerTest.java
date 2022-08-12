package zserio.antlr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

public class ZserioLexerTest
{
    @Test
    public void operators()
    {
        final CharStream input = CharStreams.fromString(
                "& " +
                "= " +
                "! " +
                ": " +
                ", " +
                "/ " +
                ". " +
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
                "< " +
                "- " +
                "% " +
                "* " +
                "!= " +
                "| " +
                "+ " +
                "? " +
                "} " +
                "] " +
                ") " +
                "; " +
                "~ " +
                "^\n"
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.AND);
        checkToken(lexer, ZserioLexer.ASSIGN);
        checkToken(lexer, ZserioLexer.BANG);
        checkToken(lexer, ZserioLexer.COLON);
        checkToken(lexer, ZserioLexer.COMMA);
        checkToken(lexer, ZserioLexer.DIVIDE);
        checkToken(lexer, ZserioLexer.DOT);
        checkToken(lexer, ZserioLexer.EQ);
        checkToken(lexer, ZserioLexer.GE);
        checkToken(lexer, ZserioLexer.GT);
        checkToken(lexer, ZserioLexer.LBRACE);
        checkToken(lexer, ZserioLexer.LBRACKET);
        checkToken(lexer, ZserioLexer.LE);
        checkToken(lexer, ZserioLexer.LOGICAL_AND);
        checkToken(lexer, ZserioLexer.LOGICAL_OR);
        checkToken(lexer, ZserioLexer.LPAREN);
        checkToken(lexer, ZserioLexer.LSHIFT);
        checkToken(lexer, ZserioLexer.LT);
        checkToken(lexer, ZserioLexer.MINUS);
        checkToken(lexer, ZserioLexer.MODULO);
        checkToken(lexer, ZserioLexer.MULTIPLY);
        checkToken(lexer, ZserioLexer.NE);
        checkToken(lexer, ZserioLexer.OR);
        checkToken(lexer, ZserioLexer.PLUS);
        checkToken(lexer, ZserioLexer.QUESTIONMARK);
        checkToken(lexer, ZserioLexer.RBRACE);
        checkToken(lexer, ZserioLexer.RBRACKET);
        checkToken(lexer, ZserioLexer.RPAREN);
        checkToken(lexer, ZserioLexer.SEMICOLON);
        checkToken(lexer, ZserioLexer.TILDE);
        checkToken(lexer, ZserioLexer.XOR);

        checkEOF(lexer);
    }

    @Test
    public void keywords()
    {
        final CharStream input = CharStreams.fromString(
                "align " +
                "bit " +
                "bool " +
                "bitmask " +
                "case " +
                "choice " +
                "const " +
                "default " +
                "enum " +
                "explicit " +
                "extern " +
                "float16 " +
                "float32 " +
                "float64 " +
                "function " +
                "if " +
                "implicit " +
                "import " +
                "instantiate " +
                "@index " +
                "int " +
                "int16 " +
                "int32 " +
                "int64 " +
                "int8 " +
                "isset " +
                "lengthof " +
                "numbits " +
                "on " +
                "optional " +
                "package " +
                "packed " +
                "publish " +
                "pubsub " +
                "return " +
                "rule " +
                "rule_group " +
                "service " +
                "sql " +
                "sql_database " +
                "sql_table " +
                "sql_virtual " +
                "sql_without_rowid " +
                "string " +
                "struct " +
                "subscribe " +
                "subtype " +
                "topic " +
                "uint16 " +
                "uint32 " +
                "uint64 " +
                "uint8 " +
                "union " +
                "using " +
                "valueof " +
                "varint " +
                "varint16 " +
                "varint32 " +
                "varint64 " +
                "varsize " +
                "varuint " +
                "varuint16 " +
                "varuint32 " +
                "varuint64 " +
                "zserio_compatibility_version\n"
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.ALIGN);
        checkToken(lexer, ZserioLexer.BIT_FIELD);
        checkToken(lexer, ZserioLexer.BOOL);
        checkToken(lexer, ZserioLexer.BITMASK);
        checkToken(lexer, ZserioLexer.CASE);
        checkToken(lexer, ZserioLexer.CHOICE);
        checkToken(lexer, ZserioLexer.CONST);
        checkToken(lexer, ZserioLexer.DEFAULT);
        checkToken(lexer, ZserioLexer.ENUM);
        checkToken(lexer, ZserioLexer.EXPLICIT);
        checkToken(lexer, ZserioLexer.EXTERN);
        checkToken(lexer, ZserioLexer.FLOAT16);
        checkToken(lexer, ZserioLexer.FLOAT32);
        checkToken(lexer, ZserioLexer.FLOAT64);
        checkToken(lexer, ZserioLexer.FUNCTION);
        checkToken(lexer, ZserioLexer.IF);
        checkToken(lexer, ZserioLexer.IMPLICIT);
        checkToken(lexer, ZserioLexer.IMPORT);
        checkToken(lexer, ZserioLexer.INSTANTIATE);
        checkToken(lexer, ZserioLexer.INDEX);
        checkToken(lexer, ZserioLexer.INT_FIELD);
        checkToken(lexer, ZserioLexer.INT16);
        checkToken(lexer, ZserioLexer.INT32);
        checkToken(lexer, ZserioLexer.INT64);
        checkToken(lexer, ZserioLexer.INT8);
        checkToken(lexer, ZserioLexer.ISSET);
        checkToken(lexer, ZserioLexer.LENGTHOF);
        checkToken(lexer, ZserioLexer.NUMBITS);
        checkToken(lexer, ZserioLexer.ON);
        checkToken(lexer, ZserioLexer.OPTIONAL);
        checkToken(lexer, ZserioLexer.PACKAGE);
        checkToken(lexer, ZserioLexer.PACKED);
        checkToken(lexer, ZserioLexer.PUBLISH);
        checkToken(lexer, ZserioLexer.PUBSUB);
        checkToken(lexer, ZserioLexer.RETURN);
        checkToken(lexer, ZserioLexer.RULE);
        checkToken(lexer, ZserioLexer.RULE_GROUP);
        checkToken(lexer, ZserioLexer.SERVICE);
        checkToken(lexer, ZserioLexer.SQL);
        checkToken(lexer, ZserioLexer.SQL_DATABASE);
        checkToken(lexer, ZserioLexer.SQL_TABLE);
        checkToken(lexer, ZserioLexer.SQL_VIRTUAL);
        checkToken(lexer, ZserioLexer.SQL_WITHOUT_ROWID);
        checkToken(lexer, ZserioLexer.STRING);
        checkToken(lexer, ZserioLexer.STRUCTURE);
        checkToken(lexer, ZserioLexer.SUBSCRIBE);
        checkToken(lexer, ZserioLexer.SUBTYPE);
        checkToken(lexer, ZserioLexer.TOPIC);
        checkToken(lexer, ZserioLexer.UINT16);
        checkToken(lexer, ZserioLexer.UINT32);
        checkToken(lexer, ZserioLexer.UINT64);
        checkToken(lexer, ZserioLexer.UINT8);
        checkToken(lexer, ZserioLexer.UNION);
        checkToken(lexer, ZserioLexer.USING);
        checkToken(lexer, ZserioLexer.VALUEOF);
        checkToken(lexer, ZserioLexer.VARINT);
        checkToken(lexer, ZserioLexer.VARINT16);
        checkToken(lexer, ZserioLexer.VARINT32);
        checkToken(lexer, ZserioLexer.VARINT64);
        checkToken(lexer, ZserioLexer.VARSIZE);
        checkToken(lexer, ZserioLexer.VARUINT);
        checkToken(lexer, ZserioLexer.VARUINT16);
        checkToken(lexer, ZserioLexer.VARUINT32);
        checkToken(lexer, ZserioLexer.VARUINT64);
        checkToken(lexer, ZserioLexer.COMPAT_VERSION);

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

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.DOC_COMMENT, ZserioLexer.DOC, "/** doc comment */");
        checkToken(lexer, ZserioLexer.DOC_COMMENT, ZserioLexer.DOC, "/**\n * doc comment\n * multiline\n */");

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

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.BLOCK_COMMENT, Lexer.HIDDEN, "/* block comment */");
        checkToken(lexer, ZserioLexer.BLOCK_COMMENT, Lexer.HIDDEN, "/* block comment\n   multiline */");

        checkEOF(lexer);
    }

    @Test
    public void lineComment()
    {
        final CharStream input = CharStreams.fromString(
                "// line comment\n" +
                "// line comment at the end"
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.LINE_COMMENT, Lexer.HIDDEN, "// line comment");
        checkToken(lexer, ZserioLexer.LINE_COMMENT, Lexer.HIDDEN, "// line comment at the end");

        checkEOF(lexer);
    }

    @Test
    public void boolLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "true false false true"
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.BOOL_LITERAL, "true");
        checkToken(lexer, ZserioLexer.BOOL_LITERAL, "false");
        checkToken(lexer, ZserioLexer.BOOL_LITERAL, "false");
        checkToken(lexer, ZserioLexer.BOOL_LITERAL, "true");

        checkEOF(lexer);
    }

    @Test
    public void stringLiteral()
    {
        final CharStream input = CharStreams.fromString(
                " \"true\" " +
                " \"0\" " +
                " \"text\" " +
                " \"with \\\"escaped\\\" string\" " +
                " \"multiple escapes \\\\\" " +
                " \"more \\\\\\\"escapes\\\"\" " +
                " \"with escaped unicode value \\u0031\" " +
                " \"with escaped hexadecimal value \\x32\" " +
                " \"with escaped octal value \\063\" "
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.STRING_LITERAL, "\"true\"");
        checkToken(lexer, ZserioLexer.STRING_LITERAL, "\"0\"");
        checkToken(lexer, ZserioLexer.STRING_LITERAL, "\"text\"");
        checkToken(lexer, ZserioLexer.STRING_LITERAL, "\"with \\\"escaped\\\" string\"");
        checkToken(lexer, ZserioLexer.STRING_LITERAL, "\"multiple escapes \\\\\"");
        checkToken(lexer, ZserioLexer.STRING_LITERAL, "\"more \\\\\\\"escapes\\\"\"");
        checkToken(lexer, ZserioLexer.STRING_LITERAL, "\"with escaped unicode value \\u0031\"");
        checkToken(lexer, ZserioLexer.STRING_LITERAL, "\"with escaped hexadecimal value \\x32\"");
        checkToken(lexer, ZserioLexer.STRING_LITERAL, "\"with escaped octal value \\063\"");

        checkEOF(lexer);
    }

    @Test
    public void invalidStringLiteral()
    {
        final CharStream input = CharStreams.fromString(
                " \"invalid unicode escape character \\uBAD\" " +
                " \"invalid octal escape character \\09\" " +
                " \"invalid hexadecimal escape character \\xA\" "
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.INVALID_STRING_LITERAL, "\"invalid unicode escape character \\uBAD\"");
        checkToken(lexer, ZserioLexer.INVALID_STRING_LITERAL, "\"invalid octal escape character \\09\"");
        checkToken(lexer, ZserioLexer.INVALID_STRING_LITERAL, "\"invalid hexadecimal escape character \\xA\"");

        checkEOF(lexer);
    }

    @Test
    public void notTerminatedStringLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "\"Not terminated string"
        );

        ZserioLexer lexer = new ZserioLexer(input);
        checkToken(lexer, ZserioLexer.INVALID_STRING_LITERAL, "\"Not terminated string");
        checkEOF(lexer);
    }

    @Test
    public void notTerminatedMultilineStringLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "\"Not terminated\nmultiline string\""
        );

        ZserioLexer lexer = new ZserioLexer(input);
        checkToken(lexer, ZserioLexer.INVALID_STRING_LITERAL, "\"Not terminated\nmultiline string\"");
        checkEOF(lexer);
    }

    @Test
    public void notTerminatedStringWithQuoteLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "\"Not terminated string with quote\\\""
        );

        ZserioLexer lexer = new ZserioLexer(input);
        checkToken(lexer, ZserioLexer.INVALID_STRING_LITERAL, "\"Not terminated string with quote\\\"");
        checkEOF(lexer);
    }

    @Test
    public void binaryLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "1001b 0101b 00b 0B 1B 101B"
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.BINARY_LITERAL, "1001b");
        checkToken(lexer, ZserioLexer.BINARY_LITERAL, "0101b");
        checkToken(lexer, ZserioLexer.BINARY_LITERAL, "00b");
        checkToken(lexer, ZserioLexer.BINARY_LITERAL, "0B");
        checkToken(lexer, ZserioLexer.BINARY_LITERAL, "1B");
        checkToken(lexer, ZserioLexer.BINARY_LITERAL, "101B");

        checkEOF(lexer);
    }

    @Test
    public void octalLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "0101 0770 012 00 01234567"
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.OCTAL_LITERAL, "0101");
        checkToken(lexer, ZserioLexer.OCTAL_LITERAL, "0770");
        checkToken(lexer, ZserioLexer.OCTAL_LITERAL, "012");
        checkToken(lexer, ZserioLexer.OCTAL_LITERAL, "00");
        checkToken(lexer, ZserioLexer.OCTAL_LITERAL, "01234567");

        checkEOF(lexer);
    }

    @Test
    public void invalidOctalLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "0109"
        );

        ZserioLexer lexer = new ZserioLexer(input);
        checkToken(lexer, ZserioLexer.INVALID_TOKEN, "0109");
        checkEOF(lexer);
    }

    @Test
    public void hexadecimalLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "0x12 0x0 0xFEDCBA98 0XFF 0x1 0xabcdef00 0x1f"
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.HEXADECIMAL_LITERAL, "0x12");
        checkToken(lexer, ZserioLexer.HEXADECIMAL_LITERAL, "0x0");
        checkToken(lexer, ZserioLexer.HEXADECIMAL_LITERAL, "0xFEDCBA98");
        checkToken(lexer, ZserioLexer.HEXADECIMAL_LITERAL, "0XFF");
        checkToken(lexer, ZserioLexer.HEXADECIMAL_LITERAL, "0x1");
        checkToken(lexer, ZserioLexer.HEXADECIMAL_LITERAL, "0xabcdef00");
        checkToken(lexer, ZserioLexer.HEXADECIMAL_LITERAL, "0x1f");

        checkEOF(lexer);
    }

    @Test
    public void invalidHexadecimalLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "0xFEEDWOLF"
        );

        ZserioLexer lexer = new ZserioLexer(input);
        checkToken(lexer, ZserioLexer.INVALID_TOKEN, "0xFEEDWOLF");
        checkEOF(lexer);
    }

    @Test
    public void floatOrDoubleLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "0e1 0e-1 1e2f 9. 9.12 9.f 9.1f 9.e2 9.e2f 9.e-2f .0 .0e-1 .0f .1e3f"
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.DOUBLE_LITERAL, "0e1");
        checkToken(lexer, ZserioLexer.DOUBLE_LITERAL, "0e-1");
        checkToken(lexer, ZserioLexer.FLOAT_LITERAL, "1e2f");
        checkToken(lexer, ZserioLexer.DOUBLE_LITERAL, "9.");
        checkToken(lexer, ZserioLexer.DOUBLE_LITERAL, "9.12");
        checkToken(lexer, ZserioLexer.FLOAT_LITERAL, "9.f");
        checkToken(lexer, ZserioLexer.FLOAT_LITERAL, "9.1f");
        checkToken(lexer, ZserioLexer.DOUBLE_LITERAL, "9.e2");
        checkToken(lexer, ZserioLexer.FLOAT_LITERAL, "9.e2f");
        checkToken(lexer, ZserioLexer.FLOAT_LITERAL, "9.e-2f");
        checkToken(lexer, ZserioLexer.DOUBLE_LITERAL, ".0");
        checkToken(lexer, ZserioLexer.DOUBLE_LITERAL, ".0e-1");
        checkToken(lexer, ZserioLexer.FLOAT_LITERAL, ".0f");
        checkToken(lexer, ZserioLexer.FLOAT_LITERAL, ".1e3f");

        checkEOF(lexer);
    }

    @Test
    public void decimalLiteral()
    {
        final CharStream input = CharStreams.fromString(
                "0 19 99 987654321"
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.DECIMAL_LITERAL, "0");
        checkToken(lexer, ZserioLexer.DECIMAL_LITERAL, "19");
        checkToken(lexer, ZserioLexer.DECIMAL_LITERAL, "99");
        checkToken(lexer, ZserioLexer.DECIMAL_LITERAL, "987654321");

        checkEOF(lexer);
    }

    @Test
    public void id()
    {
        final CharStream input = CharStreams.fromString(
                "id _id i1 _1 _ __ I _I s0mE_UgLy_id"
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.ID, "id");
        checkToken(lexer, ZserioLexer.ID, "_id");
        checkToken(lexer, ZserioLexer.ID, "i1");
        checkToken(lexer, ZserioLexer.ID, "_1");
        checkToken(lexer, ZserioLexer.ID, "_");
        checkToken(lexer, ZserioLexer.ID, "__");
        checkToken(lexer, ZserioLexer.ID, "I");
        checkToken(lexer, ZserioLexer.ID, "_I");
        checkToken(lexer, ZserioLexer.ID, "s0mE_UgLy_id");

        checkEOF(lexer);
    }

    @Test
    public void invalidToken()
    {
        final CharStream input = CharStreams.fromString(
                "01abc 0x13fx 01278"
        );

        ZserioLexer lexer = new ZserioLexer(input);

        checkToken(lexer, ZserioLexer.INVALID_TOKEN, "01abc");
        checkToken(lexer, ZserioLexer.INVALID_TOKEN, "0x13fx");
        checkToken(lexer, ZserioLexer.INVALID_TOKEN, "01278");

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
        assertEquals(type, token.getType(),
                () -> "expecting token '" + type + ":" + lexer.getVocabulary().getSymbolicName(type) + "' - ");
        assertEquals(channel, token.getChannel(),
                () -> "expecting channel '" + channel + ":" + lexer.getChannelNames()[channel] + "' - ");
        if (text != null)
            assertEquals(text, token.getText(), () -> "expecting text \"" + text + "\" - ");
    }

    private void checkEOF(Lexer lexer)
    {
        final Token token = lexer.nextToken();
        assertEquals(Lexer.EOF, token.getType());
    }
}
