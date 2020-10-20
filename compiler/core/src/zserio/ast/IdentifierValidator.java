package zserio.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import zserio.antlr.ZserioLexer;

/**
 * The class implements validation of zserio language identifiers.
 */
public class IdentifierValidator
{
    /**
     * Checks that the language identifier satisfies the requirements for safe generation.
     *
     * @param id Zserio language identifier to validate.
     */
    public static void validate(String id)
    {
        if (reservedKeywordsList.contains(id))
            throw new RuntimeException("'" + id +
                    "' is a reserved keyword and may not be used in identifiers!");

        if (id.toLowerCase(Locale.ENGLISH).startsWith("zserio"))
            throw new RuntimeException(
                    "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!");
    }

    /**
     * Checks that the top level package identifier satisfies the requirements for safe generation.
     *
     * @param id Top level package identifier given by command line to validate.
     */
    public static void validateTopLevelPackageId(String id)
    {
        final CharStream input = CharStreams.fromString(id);
        final ZserioLexer lexer = new ZserioLexer(input);
        final Token token = lexer.nextToken();
        if (token == null || token.getType() != ZserioLexer.ID)
            throw new RuntimeException("'" + id +
                    "' cannot begin with number and can contain only letters, underscore or numbers!");

        validate(id);
    }

    private static final String[] reservedKeywords = new String[]
    {
        // C++ reserved keywords
        "alignas",   "alignof",          "and",          "and_eq",   "asm",       "auto",
        "bitand",    "bitor",            "bool",         "break",    "case",      "catch",
        "char",      "char16_t",         "char32_t",     "class",    "compl",     "const",
        "constexpr", "const_cast",       "continue",     "decltype", "default",   "delete",
        "do",        "double",           "dynamic_cast", "else",     "enum",      "explicit",
        "export",    "extern",           "false",        "float",    "for",       "friend",
        "goto",      "if",               "inline",       "int",      "long",      "mutable",
        "namespace", "new",              "noexcept",     "not",      "not_eq",    "nullptr",
        "operator",  "or",               "or_eq",        "private",  "protected", "public",
        "register",  "reinterpret_cast", "return",       "short",    "signed",    "sizeof",
        "static",    "static_assert",    "static_cast",  "struct",   "switch",    "template",
        "this",      "thread_local",     "throw",        "true",     "try",       "typedef",
        "typeid",    "typename",         "union",        "unsigned", "using",     "virtual",
        "void",      "volatile",         "wchar_t",      "while",    "xor",       "xor_eq",

        // Java reserved keywords
        "abstract",   "assert",       "boolean",    "break",    "byte",      "case",
        "catch",      "char",         "class",      "const",    "continue",  "default",
        "double",     "do",           "else",       "enum",     "extends",   "false",
        "final",      "finally",      "float",      "for",      "goto",      "if",
        "implements", "import",       "instanceof", "int",      "interface", "long",
        "native",     "new",          "null",       "package",  "private",   "protected",
        "public",     "return",       "short",      "static",   "strictfp",  "super",
        "switch",     "synchronized", "this",       "throw",    "throws",    "transient",

        // Python reserved keywords
        "and",    "as",      "assert",   "break", "class", "continue", "def",   "del",    "elif", "else",
        "except", "finally", "False",    "for",   "from",  "global",   "if",    "import", "in",   "is",
        "lambda", "None",    "nonlocal", "not",   "or",    "pass",     "raise", "return", "True", "try",
        "while",  "with",    "yield"
    };

    private static final Set<String> reservedKeywordsList =
            new HashSet<String>(Arrays.asList(reservedKeywords));
};
