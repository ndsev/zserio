grammar DocComment4;

/** Parser */

docComment
    :   COMMENT_BEGIN whitespace? docContent whitespace? COMMENT_END
    ;

docContent
    :   docLine (whitespaceInLine? NEWLINE whitespaceInLine? docLine)*?
    ;

docLine
    :   docTag
    |   docTextLine
    |   // empty line
    ;

docTextLine
    :   docText (whitespaceInLine? docText)*
    ;

docTag
    :   seeTag
    |   todoTag
    |   paramTag
    |   deprecatedTag
    ;

docText
    :   seeTag
    |   textElement
    ;

textElement
    :   SEE
    |   TODO
    |   PARAM
    |   DEPRECATED
    |   ((ID | WORD | ESC | DOUBLE_QOUTE_ESC | DOUBLE_QUOTE | DOT | STAR) whitespaceInLine?)+
    ;

seeTag
    :   SEE whitespaceInParagraph (seeTagAlias whitespaceInParagraph)? seeTagId
    ;

seeTagAlias
    :   DOUBLE_QUOTE seeTagAliasText DOUBLE_QUOTE
    ;

seeTagAliasText
    :   ((SEE | TODO | PARAM | DEPRECATED | ID | WORD | ESC | DOUBLE_QOUTE_ESC | DOT | STAR) whitespaceInLine?)+
    ;

seeTagId
    :   ID (DOT ID)*
    ;

todoTag
    :   TODO whitespaceInParagraph docTextLine
    ;

paramTag
    :   PARAM whitespaceInParagraph paramName whitespaceInParagraph docTextLine
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
SLASH : '/' ;

fragment
AT : '@' ;

fragment
NEWLINE_COMMENT : SPACE* (STAR { _input.LA(1) != '/' }?)+ ;

COMMENT_BEGIN : '/**' (STAR { _input.LA(1) != '/' }?)*;

COMMENT_END : STAR* '*/' ;

NEWLINE : ('\r'? '\n' | '\r') NEWLINE_COMMENT? SPACE? ;

SPACE : ' ' | '\t' ;

DOT : '.' ;

STAR : '*' ;

ESC : '\\\\';

DOUBLE_QOUTE_ESC : '\\"' ;

DOUBLE_QUOTE : '"' ;

SEE : AT 'see' ;

TODO : AT 'todo' ;

PARAM : AT 'param' ;

DEPRECATED : AT 'deprecated' ;

ID : [a-zA-Z_][a-zA-Z_0-9]* ;

WORD : ~[ .\t\r\n"*\\]+ ;
