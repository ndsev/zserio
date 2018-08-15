/*
 * Lexer part of Zserio grammar.
 */
header
{
package zserio.antlr;
import zserio.antlr.util.*;
}

class ZserioLexer extends Lexer;

options
{
    k=3;                   // 3 characters of lookahead
    charVocabulary='\u0003'..'\uFFFE';
    importVocab=ZserioParser;
    testLiterals=false;
    defaultErrorHandler=false;
}

// Identifiers (solves bool literals as well).
//
// Note that testLiterals is set to true! This means that after we match the
// rule, we look in the literals table to see if it's a literal or really an
// identifier.
ID options {testLiterals=true;}
    :   ( 't' "rue" ) => "true" { $setType(BOOL_LITERAL); }
        | ( 'f' "alse" ) => "false" { $setType(BOOL_LITERAL); }
        | ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*
    ;

// operators
QUESTIONMARK  :    '?';
LPAREN        :    '(';
RPAREN        :    ')';
LBRACKET      :    '[';
RBRACKET      :    ']';
LCURLY        :    '{';
RCURLY        :    '}';
COLON         :    ':';
DOUBLECOLON   :    "::";
COMMA         :    ',';
DOT           :    '.';
ASSIGN        :    '=';
BANG          :    '!';
DIVIDE        :    '/';
DIVASSIGN     :    "/=";
PLUS          :    '+';
PLUSASSIGN    :    "+=";
MINUS         :    '-';
MINUSASSIGN   :    "-=";
MULTIPLY      :    '*';
MULTASSIGN    :    "*=";
MODULO        :    '%';
MODASSIGN     :    "%=";
RSHIFT        :    ">>";
RSHIFTASSIGN  :    ">>=";
LSHIFT        :    "<<";
LSHIFTASSIGN  :    "<<=";
XORASSIGN     :    "^=";
ORASSIGN      :    "|=";
LOGICALOR     :    "||";
TILDE         :    '~';
AND           :    '&';
OR            :    '|';
XOR           :    '^';
ANDASSIGN     :    "&=";
LOGICALAND    :    "&&";
SEMICOLON     :    ';';
EQ            :    "==";
NE            :    "!=";
LT            :    "<";
LE            :    "<=";
GT            :    ">";
GE            :    ">=";

// others
DOLLAR        :    '$';
ATSIGN        :    '@';

// whitespaces (to be ignored)
WS
    :   (   ' '
            |    '\t'
            |    '\f'
            |    { LA(2) == '\n' }? '\r'! '\n' { newline(); }
            |    '\r' { newline(); }
            |    '\n' { newline(); }
        )+
        { $setType(Token.SKIP); }
    ;

// single-line comments
SL_COMMENT
    :    "//"
        (~('\n'|'\r'))* ('\n'|'\r'('\n')?)?
        { newline(); $setType(Token.SKIP); }
    ;

// multi-line comments (solves documentation comments as well)
COMMENT
    :   "/*"
        (
            {LA(2) != '/'}? "*" PLAIN_COMMENT_CONTENT "*/" { $setType(DOC_COMMENT); }
            | PLAIN_COMMENT_CONTENT "*/" { $setType(Token.SKIP); }
        )
    ;

// standard comments (to be skipped)
protected
PLAIN_COMMENT_CONTENT
    :   (
            { LA(2) != '/' }? '*'
        |   { LA(2) == '\n' }? '\r'! '\n' { newline(); }
        |   '\r'            { newline(); }
        |   '\n'            { newline(); }
        |   ~('*'|'\n'|'\r')
        )*
    ;

// string literals
STRING_LITERAL
    :   '"' (ESC | ~('"'|'\\'|'\n'|'\r') )* '"'
    ;

// Escape sequence.
//
// Note that this is protected. It can only be called from another lexer rule.
// It will not ever directly return a token to the parser.
// There are various ambiguities hushed in this rule. The optional
// '0'...'9' digit matches should be matched here rather than letting
// them go back to STRING_LITERAL to be matched. ANTLR does the
// right thing by matching immediately; hence, it's ok to shut off
// the FOLLOW ambig warnings.
protected
ESC
    :   '\\'
        (    'b'    // backspace
        |    'f'    // formfeed
        |    'n'    // new line
        |    'r'    // carriage return
        |    't'    // horizontal tab
        |    'v'    // vertical tab
        |    '\\'   // backslash
        |    '\''   // single quotation mark
        |    '"'    // double quotation mark
        |    '?'    // question mark
        |    'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT    // universal character name
        |    'x' HEX_DIGIT (options { warnWhenFollowAmbig = false; } : HEX_DIGIT)?   // hexadecimal number
        |    '0'..'3'
             (
                 options { warnWhenFollowAmbig = false; } : '0'..'7'
                 (options { warnWhenFollowAmbig = false; } : '0'..'7')?
             )?     // octal number
        |    '4'..'7' (options { warnWhenFollowAmbig = false; } : '0'..'7')?    // octal number
        )
    ;

// hexadecimal digit (again, note it's protected!)
protected
HEX_DIGIT
    :   ('0'..'9'|'A'..'F'|'a'..'f')
    ;

// numeric literals (solves others literals as well)
DECIMAL_LITERAL
    :   ( ('0'..'1')+ ('b'|'B') ) => ('0'..'1')+ ('b'|'B') { $setType(BINARY_LITERAL); } // binary
        | ( ('0'..'9')+ '.' ('0'..'9')* ('f'|'F')? ) => ('0'..'9')+ '.' ('0'..'9')* // float or double
            ('f' { $setType(FLOAT_LITERAL); } | 'F'{ $setType(FLOAT_LITERAL); } | { $setType(DOUBLE_LITERAL); })
        | (
              '0'
              (
                  ('x'|'X') (HEX_DIGIT)+ { $setType(HEXADECIMAL_LITERAL); } // hex
                  | ('0'..'7')+ { $setType(OCTAL_LITERAL); }                // octal
              )?
              |
              ('1'..'9') ('0'..'9')* // decimal
          )
    ;
