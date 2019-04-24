grammar DocComment4;

/** Parser */

docComment
    :   COMMENT_BEGIN whitespace? docContent? whitespace? COMMENT_END
    ;

docContent
    :   docParagraph (NEWLINE whitespaceInLine? NEWLINE whitespace? docParagraph)*
    ;

docParagraph
    :   docTextLine (NEWLINE docTextLine)*
    ;

docTextLine
    :   docTag docText*
    |   docText+
    ;

docTag
    :   todoTag
    |   paramTag
    |   deprecatedTag
    ;

docText
    : seeTag
    | text
    ;

text
    :   SEE
    |   TODO
    |   PARAM
    |   DEPRECATED
    |   (
            ID
        |   WORD
        |   DOUBLE_QUOTE
        |   DOT
        |   STAR
        |   whitespaceInLine
        )+
    ;

seeTag
    :   SEE whitespaceInParagraph (seeTagAlias whitespaceInParagraph)? seeTagId
    ;

seeTagAlias
    :   DOUBLE_QUOTE text DOUBLE_QUOTE
    ;

seeTagId
    :   ID (DOT ID)*
    ;

todoTag
    :   TODO whitespaceInParagraph docTagText
    ;

paramTag
    :   PARAM whitespaceInParagraph paramId whitespaceInParagraph paramDescription
    ;

paramId
    :   ID
    ;

paramDescription
    :   docTagText
    ;

deprecatedTag
    :   DEPRECATED
    ;

docTagText
    :   docText+ (NEWLINE docText+)*
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
NEWLINE_COMMENT : SPACE? (STAR { _input.LA(1) != '/' }?)+ ;

COMMENT_BEGIN : '/**' (STAR { _input.LA(1) != '/' }?)*;

COMMENT_END : STAR* '*/' ;

NEWLINE : ('\r'? '\n' | '\r') NEWLINE_COMMENT? SPACE? ;

SPACE : ' ' | '\t' ;

DOT : '.' ;

STAR : '*' ;

DOUBLE_QUOTE : '"' ;

SEE : AT 'see' ;

TODO : AT 'todo' ;

PARAM : AT 'param' ;

DEPRECATED : AT 'deprecated' ;

ID : [a-zA-Z_][a-zA-Z_0-9]+ ;

WORD : ~[ .\t\r\n"*]+ ;
