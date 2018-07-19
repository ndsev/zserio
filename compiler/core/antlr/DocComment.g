/*
 * Grammar to parse documentation comment.
 *
 * Documentation comment is comment beginning with "/**" supporting tags like "@see", "@param" and @todo.
 */
header
{
package zserio.antlr;

import zserio.ast.doc.DocTokenAST;
}

class DocCommentParser extends Parser;

options
{
    k=2;
    buildAST=true;
    defaultErrorHandler=false;
}

tokens
{
    DOC_COMMENT<AST=zserio.ast.doc.DocCommentToken>;
    DOC_PARAGRAPH<AST=zserio.ast.doc.DocParagraphToken>;
    DOC_TAG_PARAM<AST=zserio.ast.doc.DocTagParamToken>;
    DOC_TAG_SEE<AST=zserio.ast.doc.DocTagSeeToken>;
    DOC_TAG_TODO<AST=zserio.ast.doc.DocTagTodoToken>;
}

docCommentDeclaration
    :   nearest:BOC! (NEW_LINE_COMMENT!)* (docText)? EOC!
    {
        #docCommentDeclaration = #([DOC_COMMENT], docCommentDeclaration);
        DocTokenAST createdToken = (DocTokenAST)#docCommentDeclaration;
        createdToken.setImaginaryTokenPosition((DocTokenAST)#nearest);
    }
    ;

docText
    :   docParagraph
        (
            |
            ((NEW_LINE_COMMENT!)+ ((docParagraph) => docText)?)
        )
    ;

docParagraph
    :   nearest:docRows
    {
        #docParagraph = #([DOC_PARAGRAPH], #docParagraph);
        DocTokenAST createdToken = (DocTokenAST)#docParagraph;
        createdToken.setImaginaryTokenPosition((DocTokenAST)#nearest);
    }
    ;

docRows
    :   docRow ((NEW_LINE_COMMENT docRow) => NEW_LINE_COMMENT! docRows)?
    ;

docRow
    :   (DOC_TEXT | docTagSee)+ | docTagParam | docTagTodo | docTagDeprecated
    ;

docTagSee
    :   DOC_TAG_SEE^
    ;

docTagParamText
    :   DOC_TEXT ((NEW_LINE_COMMENT DOC_TEXT) => NEW_LINE_COMMENT! docTagParamText)?
    ;

docTagParam
    :   DOC_TAG_PARAM^ docTagParamText
    ;

docTagTodo
    :   DOC_TAG_TODO^ docTagParamText
    ;

docTagDeprecated
    :   DOC_TAG_DEPRECATED^
    ;

class DocCommentLexer extends Lexer;

options
{
    k=2;
    charVocabulary='\u0003'..'\uFFFE';
}

protected
NEW_LINE
    :   { LA(2) == '\n' }? '\r'! '\n' {newline();}
        | '\r' {newline();}
        | '\n' {newline();}
    ;

NEW_LINE_COMMENT
    :   NEW_LINE (WHITE_SPACES!)? ({LA(2) != '/'}? ('*')+ (WHITE_SPACES)?)?
    ;

WHITE_SPACES
    :   (' ' | '\t' | '\f')+
        { $setType(Token.SKIP); }
    ;

protected
BOC :   "/**";

EOC :   { LA(2) == '/' }? "*/";

DOC_TEXT
    :   (BOC) => BOC { $setType(BOC); } |
        (
            (
                ~('\r'|'\n'|'@'|' '|'\t'|'\f'|'*') |
                {LA(2) != '/'}? '*'
            )
            (
                ~('\r'|'\n'|'*'|'@') |
                {LA(2) != '/'}? '*' |
                {LA(2) != 's' || LA(3) != 'e' || LA(4) != 'e' || LA(5) != ' '}? '@'
            )*
        )
    ;

protected
DOC_TAG_SEE_ALLIAS
    :   '"' (~('"'))* '"'
    ;

protected
DOT :   '.';

protected
DOC_TAG_ID
    :   ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*
    ;

protected
DOC_TAG_SEE_LINK
    :   DOC_TAG_ID ((DOT DOC_TAG_ID) => DOT DOC_TAG_SEE_LINK)?
    ;

protected
AT  :   '@';

DOC_TAG_SEE
    :   AT! "see"! WHITE_SPACES! (NEW_LINE_COMMENT!)?
        (
            DOC_TAG_SEE_ALLIAS (WHITE_SPACES! | NEW_LINE_COMMENT!)
        )?
        DOC_TAG_SEE_LINK
    ;

DOC_TAG_PARAM
    :   AT! "param"! WHITE_SPACES! DOC_TAG_ID
    ;

DOC_TAG_TODO
    :   AT! "todo"!
    ;

DOC_TAG_DEPRECATED
    :   AT! "deprecated"!
    ;
