package zserio.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import zserio.antlr.util.ParserException;

/**
 * The representation of AST node type ID.
 */
public class IdToken extends TokenAST
{
    /**
     * Checks if ID token differ from reserved keywords of C++ or Java.
     *
     * If ID token matches to some reserved keyword, it is considered as Zserio compilation error.
     *
     * @throws ParserException Throws if ID token matches to some reserved keyword.
     */
    @Override
    protected void check() throws ParserException
    {
        final String id = getText();
        if ( reservedKeywordsList.contains(id) )
            throw new ParserException(this, "'" + id +  "' is a reserved keyword and may not be used here!");
    }

    /**
     * The array of all reserved keywords.
     */
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

    private static final long serialVersionUID = -1L;

    private final Set<String> reservedKeywordsList = new HashSet<String>(Arrays.asList(reservedKeywords));
}
