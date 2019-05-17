grammar DocComment;

/** Parser */

docComment
    :   COMMENT_BEGIN whitespace? docContent whitespace? COMMENT_END
    ;

docContent
    :   docElement (whitespaceInLine? NEWLINE whitespaceInLine? docElement)*?
    ;

docElement
    :   docTag
    |   docLine
    |   // empty line
    ;

docLine
    :   docLineElement (whitespaceInLine? docLineElement)*
    ;

docTag
    :   seeTag
    |   todoTag
    |   paramTag
    |   deprecatedTag
    ;

docLineElement
    :   seeTag
    |   docText
    ;

docText
    :   SEE
    |   TODO
    |   PARAM
    |   DEPRECATED
    |   (ID | TEXT | BACKSLASH | ESC | DOUBLE_QUOTE_ESC | DOUBLE_QUOTE | DOT | STAR | AT)
        (whitespaceInLine? (ID | TEXT | BACKSLASH | ESC | DOUBLE_QUOTE_ESC | DOUBLE_QUOTE | DOT | STAR | AT))*
    ;

seeTag
    :   SEE whitespaceInParagraph (seeTagAlias whitespaceInParagraph)? seeTagId
    ;

seeTagAlias
    :   DOUBLE_QUOTE seeTagAliasText DOUBLE_QUOTE
    ;

seeTagAliasText
    :   (
            SEE | TODO | PARAM | DEPRECATED |
            ID | TEXT | BACKSLASH | ESC | DOUBLE_QUOTE_ESC | DOT | STAR | AT |
            whitespaceInLine
        )+
    ;

seeTagId
    :   ID (DOT ID)*
    ;

todoTag
    :   TODO (whitespaceInParagraph docLine)?
    ;

paramTag
    :   PARAM whitespaceInParagraph paramName (whitespaceInParagraph docLine)?
    ;

paramName
    :   ID
    ;

deprecatedTag
    :   DEPRECATED
    ;

whitespace
    :   (
            SPACE
        |   NEWLINE
        )+
    ;

whitespaceInParagraph
    :   SPACE* NEWLINE SPACE*
    |   SPACE+
    ;

whitespaceInLine
    :   SPACE+
    ;

/** Lexer */

fragment
NEWLINE_COMMENT : SPACE* (STAR { _input.LA(1) != '/' }?)+ ;

COMMENT_BEGIN : '/**' (STAR { _input.LA(1) != '/' }?)*;

COMMENT_END : STAR* '*/' ;

NEWLINE : ('\r'? '\n' | '\r') NEWLINE_COMMENT? SPACE? ;

SPACE : ' ' | '\t' ;

DOT : '.' ;

STAR : '*' ;

BACKSLASH: '\\';

ESC : '\\\\';

DOUBLE_QUOTE_ESC : '\\"' ;

DOUBLE_QUOTE : '"' ;

AT : '@' ;

SEE : AT 'see' ;

TODO : AT 'todo' ;

PARAM : AT 'param' ;

DEPRECATED : AT 'deprecated' ;

ID : [a-zA-Z_][a-zA-Z_0-9]* ;

TEXT : ~[ .\t\r\n"*\\@a-zA-Z_]+;
