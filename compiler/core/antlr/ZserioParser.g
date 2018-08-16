/*
 * Parser part of Zserio grammar.
 */
header
{
package zserio.antlr;

import zserio.antlr.util.FileNameLexerToken;
import zserio.ast.*;
}

class ZserioParser extends Parser;

options
{
    k=2;
    buildAST=true;
    defaultErrorHandler=false;
}

// Keep the token list sorted alphabetically.
tokens
{
    ALIGN="align";
    AND<AST=zserio.ast.Expression>;
    ARRAY<AST=zserio.ast.ArrayType>;
    ARRAYELEM<AST=zserio.ast.Expression>;
    BANG<AST=zserio.ast.Expression>;
    BINARY_LITERAL<AST=zserio.ast.Expression>;
    BIT="bit"<AST=zserio.ast.UnsignedBitFieldType>;
    BOOL="bool"<AST=zserio.ast.BooleanType>;
    BOOL_LITERAL<AST=zserio.ast.Expression>;
    CASE="case"<AST=zserio.ast.ChoiceCase>;
    CHOICE="choice"<AST=zserio.ast.ChoiceType>;
    CONST="const"<AST=zserio.ast.ConstType>;
    DECIMAL_LITERAL<AST=zserio.ast.Expression>;
    DEFAULT="default"<AST=zserio.ast.ChoiceDefault>;
    DIVIDE<AST=zserio.ast.Expression>;
    DOC_COMMENT;
    DOT<AST=zserio.ast.Expression>;
    DOUBLE_LITERAL<AST=zserio.ast.Expression>;
    ENUM="enum"<AST=zserio.ast.EnumType>;
    EQ<AST=zserio.ast.Expression>;
    EXPLICIT="explicit"<AST=zserio.ast.Expression>;
    FIELD<AST=zserio.ast.Field>;
    FLOAT_LITERAL<AST=zserio.ast.Expression>;
    FLOAT16="float16"<AST=zserio.ast.FloatType>;
    FLOAT32="float32"<AST=zserio.ast.FloatType>;
    FLOAT64="float64"<AST=zserio.ast.FloatType>;
    FUNCTION="function"<AST=zserio.ast.FunctionType>;
    FUNCTIONCALL<AST=zserio.ast.Expression>;
    GE<AST=zserio.ast.Expression>;
    GT<AST=zserio.ast.Expression>;
    HEXADECIMAL_LITERAL<AST=zserio.ast.Expression>;
    ID<AST=zserio.ast.IdToken>;
    IF="if";
    IMPLICIT="implicit";
    IMPORT="import"<AST=zserio.ast.Import>;
    INDEX="index"<AST=zserio.ast.Expression>;
    INST<AST=zserio.ast.TypeInstantiation>;
    INT="int"<AST=zserio.ast.SignedBitFieldType>;
    INT16="int16"<AST=zserio.ast.StdIntegerType>;
    INT32="int32"<AST=zserio.ast.StdIntegerType>;
    INT64="int64"<AST=zserio.ast.StdIntegerType>;
    INT8="int8"<AST=zserio.ast.StdIntegerType>;
    INTEGER_LITERAL<AST=zserio.ast.Expression>;
    ITEM<AST=zserio.ast.EnumItem>;
    LE<AST=zserio.ast.Expression>;
    LENGTHOF="lengthof"<AST=zserio.ast.Expression>;
    LOGICALAND<AST=zserio.ast.Expression>;
    LOGICALOR<AST=zserio.ast.Expression>;
    LPAREN<AST=zserio.ast.Expression>;
    LSHIFT<AST=zserio.ast.Expression>;
    LT<AST=zserio.ast.Expression>;
    MINUS<AST=zserio.ast.Expression>;
    MODULO<AST=zserio.ast.Expression>;
    MULTIPLY<AST=zserio.ast.Expression>;
    NE<AST=zserio.ast.Expression>;
    NOT_HANDLED="not_handled";
    NUMBITS="numbits"<AST=zserio.ast.Expression>;
    OCTAL_LITERAL<AST=zserio.ast.Expression>;
    OFFSET;
    ON="on";
    OPTIONAL="optional";
    OR<AST=zserio.ast.Expression>;
    PACKAGE="package"<AST=zserio.ast.PackageToken>;
    PARAM<AST=zserio.ast.Parameter>;
    PLUS<AST=zserio.ast.Expression>;
    QUESTIONMARK<AST=zserio.ast.Expression>;
    RETURN="return";
    ROOT;
    RPC="rpc"<AST=zserio.ast.Rpc>;
    RSHIFT<AST=zserio.ast.Expression>;
    SERVICE="service"<AST=zserio.ast.ServiceType>;
    SQL="sql"<AST=zserio.ast.SqlConstraint>;
    SQL_DATABASE="sql_database"<AST=zserio.ast.SqlDatabaseType>;
    SQL_TABLE="sql_table"<AST=zserio.ast.SqlTableType>;
    SQL_VIRTUAL="sql_virtual";
    SQL_USING="using";
    SQL_WITHOUT_ROWID="sql_without_rowid";
    STRING="string"<AST=zserio.ast.StringType>;
    STRING_LITERAL<AST=zserio.ast.Expression>;
    STRUCTURE="struct"<AST=zserio.ast.StructureType>;
    SUBTYPE="subtype"<AST=zserio.ast.Subtype>;
    SUM="sum"<AST=zserio.ast.Expression>;
    TILDE<AST=zserio.ast.Expression>;
    TRANSLATION_UNIT;
    TYPEREF<AST=zserio.ast.TypeReference>;
    UINT16="uint16"<AST=zserio.ast.StdIntegerType>;
    UINT32="uint32"<AST=zserio.ast.StdIntegerType>;
    UINT64="uint64"<AST=zserio.ast.StdIntegerType>;
    UINT8="uint8"<AST=zserio.ast.StdIntegerType>;
    UMINUS<AST=zserio.ast.Expression>;
    UNION="union"<AST=zserio.ast.UnionType>;
    UNUSED="unused";
    UPLUS<AST=zserio.ast.Expression>;
    VARINT="varint"<AST=zserio.ast.VarIntegerType>;
    VARINT16="varint16"<AST=zserio.ast.VarIntegerType>;
    VARINT32="varint32"<AST=zserio.ast.VarIntegerType>;
    VARINT64="varint64"<AST=zserio.ast.VarIntegerType>;
    VARUINT="varuint"<AST=zserio.ast.VarIntegerType>;
    VARUINT16="varuint16"<AST=zserio.ast.VarIntegerType>;
    VARUINT32="varuint32"<AST=zserio.ast.VarIntegerType>;
    VARUINT64="varuint64"<AST=zserio.ast.VarIntegerType>;
    VFIELD<AST=zserio.ast.Field>;
    XOR<AST=zserio.ast.Expression>;
}

/**
 * translationUnit.
 */
translationUnit
    :   (packageDeclaration)? (importDeclaration)* (commandDeclaration)* EOF!
        {
            #translationUnit = #([TRANSLATION_UNIT, getFilename()], translationUnit);
        }
    ;

packageDeclaration
    :   PACKAGE^ ID (DOT! ID)* SEMICOLON!
    ;

importDeclaration
    :   IMPORT^ ID DOT! (ID DOT!)* (MULTIPLY<AST=zserio.ast.DefaultToken> | ID) SEMICOLON!
    ;

commandDeclaration
    :   constDeclaration SEMICOLON! |
        subtypeDeclaration SEMICOLON! |
        structureDeclaration SEMICOLON! |
        choiceDeclaration SEMICOLON! |
        unionDeclaration SEMICOLON! |
        enumDeclaration SEMICOLON! |
        sqlTableDeclaration SEMICOLON! |
        sqlDatabaseDefinition SEMICOLON! |
        serviceDefinition SEMICOLON!
    ;

/**
 * constDeclaration.
 */
constDeclaration
    :   CONST^ definedType ID ASSIGN! constantExpression
    ;

/**
 * subtypeDeclaration.
 */
subtypeDeclaration
    :   SUBTYPE^ definedType ID
    ;

/**
 * structureDeclaration.
 */
structureDeclaration
    :   STRUCTURE^ ID (parameterList)?
        LCURLY!
        structureMemberList
        RCURLY!
    ;

structureMemberList
    :   (structureFieldDefinition SEMICOLON!)* (functionDefinition)*
    ;

structureFieldDefinition!
    :   (a:fieldAlignment)?
        l:fieldOffset
        (p:OPTIONAL)?
        t:fieldTypeId
        (i:fieldInitializer)?
        (o:fieldOptionalClause)?
        (c:fieldConstraint)?
        {
            #structureFieldDefinition = #([FIELD], t, p, i, o, c, l, a);
        }
    ;

fieldAlignment
    :   ALIGN^ LPAREN! v:DECIMAL_LITERAL RPAREN! COLON!
    ;

// prevent need of error handling in a parent rule
fieldOffset
    :   ((fieldOffsetRule) => fieldOffsetRule)?
    ;
    exception
    catch [NoViableAltException e]
    {} // let a following rule to fail with proper message

fieldOffsetRule!
    :   e:variableExpression COLON
        {
            #fieldOffsetRule = #([OFFSET], e);
        }
    ;

fieldTypeId!
    :   (m:IMPLICIT)?
        t:typeReference
        i:ID
        (
            {#m != null}? (n:fieldArrayRange { #t = #([ARRAY], t, n, m); }) |
            {#m == null}? (r:fieldArrayRange { #t = #([ARRAY], t, r); })?
        )
        {
            // create only 2 AST siblings t and i without any AST parent
            #fieldTypeId = #(null, t, i);
        }
    ;
    exception
    catch [NoViableAltException e]
    { 
        if (LA(2) != ZserioParserTokenTypes.EOF)
            throw e;
        // let a following rule to fail with proper message for unexpected EOF
    }

fieldArrayRange
    :   LBRACKET! (expression)? RBRACKET!
    ;

fieldInitializer
    :   ASSIGN^ constantExpression
    ;

fieldOptionalClause
    :   IF^ expression
    ;

fieldConstraint
    :   COLON^ expression
    ;

functionDefinition
    :   FUNCTION^
        definedType // function doesn't need to specify parameters of parameterized types
        ID functionParamList functionBody
    ;

functionParamList
    :   LPAREN! RPAREN!
    ;

functionBody
    :   LCURLY! RETURN^ expression SEMICOLON! RCURLY!
    ;

/**
 * choiceDeclaration.
 */
choiceDeclaration
    :   CHOICE^ ID parameterList ON! choiceTag
        LCURLY!
        (choiceCases)+
        (defaultChoice)?
        (functionDefinition)*
        RCURLY!
    ;

choiceTag
    :   variableExpression
    ;

choiceCases
    :   CASE^ expression COLON! (CASE expression COLON!)* (choiceFieldDefinition)? SEMICOLON!
    ;

choiceFieldDefinition!
    :   t:fieldTypeId
        (c:fieldConstraint)?
        {
            #choiceFieldDefinition = #([FIELD], t, c);
        }
    ;

defaultChoice
    :   DEFAULT^ COLON! (choiceFieldDefinition)? SEMICOLON!
    ;

/**
 * unionDeclaration.
 */
unionDeclaration
    :   UNION^ ID (parameterList)?
        LCURLY!
            (unionFieldDefinition)+
            (functionDefinition)*
        RCURLY!
    ;

unionFieldDefinition
    :   choiceFieldDefinition SEMICOLON!
    ;

/**
 * enumDeclaration.
 */
enumDeclaration
    :   ENUM^ definedType ID
        LCURLY!
        enumItem (COMMA! enumItem)* (COMMA!)?
        RCURLY!
    ;

enumItem
    :   i:ID (ASSIGN! constantExpression)?
        {
            #enumItem = #([ITEM], enumItem);
        }
    ;

/**
 * sqlTableDeclaration.
 */
sqlTableDeclaration
    :   SQL_TABLE^ ID
        (SQL_USING! ID)?
        LCURLY!
        (sqlTableFieldDefinition | sqlTableVirtualFieldDefinition)*
        (sqlConstraint SEMICOLON!)?
        (sqlWithoutRowId SEMICOLON!)?
        RCURLY!
    ;

sqlTableFieldDefinition!
    :   t:typeReference
        i:ID
        (s:sqlConstraint)?
        SEMICOLON!
        {
            #sqlTableFieldDefinition = #([FIELD], t, i, s);
        }
    ;

sqlConstraint
    :   SQL^ STRING_LITERAL
    ;

sqlTableVirtualFieldDefinition!
    :   v:SQL_VIRTUAL
        t:typeReference
        i:ID
        (s:sqlConstraint)?
        SEMICOLON!
        {
            #sqlTableVirtualFieldDefinition = #([VFIELD], t, i, s, v);
        }
    ;

sqlWithoutRowId
    :   SQL_WITHOUT_ROWID
    ;

/**
 * sqlDatabaseDefinition.
 */
sqlDatabaseDefinition
    :   SQL_DATABASE^ ID
        LCURLY!
        (sqlDatabaseFieldDefinition)+
        RCURLY!
    ;

sqlDatabaseFieldDefinition
    :   sqlTableReference i:ID SEMICOLON!
        {
            #sqlDatabaseFieldDefinition = #([FIELD], #sqlDatabaseFieldDefinition);
        }
    ;

sqlTableReference
    :   ID
        {
            #sqlTableReference = #([TYPEREF], #sqlTableReference);
        }
    ;

/**
 * serviceDefinition.
 */
serviceDefinition
    :   SERVICE^ ID
        LCURLY!
        (rpcDeclaration SEMICOLON!)*
        RCURLY!
    ;

rpcDeclaration
    :   RPC^
        typeSymbol // rpc doesn't need to specify parameters of parameterized types and forbids built-in types
        ID
        LPAREN!
        typeSymbol
        RPAREN!
    ;

/**
 * definedType.
 */
definedType
    :   typeSymbol |
        builtinType
    ;

typeSymbol
    :   ID (DOT! ID)*
        {
            #typeSymbol = #([TYPEREF], #typeSymbol);
        }
    ;

builtinType
    :   integerType |
        unsignedBitField |
        signedBitField |
        varintType |
        boolType |
        stringType |
        floatType
    ;

integerType
    :   UINT8 |
        UINT16 |
        UINT32 |
        UINT64 |
        INT8 |
        INT16 |
        INT32 |
        INT64
    ;

unsignedBitField
    :   BIT^ (COLON! DECIMAL_LITERAL | LT! shiftExpression GT!)
    ;

signedBitField
    :   INT^ (COLON! DECIMAL_LITERAL | LT! shiftExpression GT!)
    ;

varintType
    :   VARINT |
        VARINT16 |
        VARINT32 |
        VARINT64 |
        VARUINT |
        VARUINT16 |
        VARUINT32 |
        VARUINT64
    ;

boolType
    :   BOOL
    ;

stringType
    :    STRING
    ;

floatType
    :   FLOAT16 |
        FLOAT32 |
        FLOAT64
    ;

/**
 * parameterList.
 */
parameterList
    :   LPAREN! ( parameterDefinition ( COMMA! parameterDefinition)* )? RPAREN!
    ;

parameterDefinition
    :   definedType i:ID
        {
            #parameterDefinition = #([PARAM], #parameterDefinition);
        }
    ;

/**
 * typeReference.
 */
typeReference
    :   builtinType |
        (
            typeSymbol
            (
                typeArgumentList
                {
                    // instantiation
                    #typeReference = #([INST], typeReference);
                }
            )?
            {
                if (#typeReference instanceof TypeReference)
                {
                    // type reference
                    ((TypeReference)#typeReference).setNonParametrizedTypeCheck();
                }
            }
        )
    ;

typeArgumentList
    :   LPAREN! (typeArgument (COMMA! typeArgument)* )? RPAREN!
    ;

typeArgument
    :   EXPLICIT^ variableName |
        functionArgument
    ;

/**
 * expression.
 */
expression
    :   conditionalExpression
    ;

unaryExpression
    :   postfixExpression |
        unaryOperand |
        lengthOfOperand |
        sumFunction |
        numbitsFunction
    ;

postfixExpression
    :   primaryExpression
        (
            o:postfixOperand!
            {
                if (#o != null)
                {
                    AST rhs = #o.getFirstChild();
                    #postfixExpression= #(o, postfixExpression, rhs);
                }
            }
        )*
    ;

primaryExpression
    :   variableName |
        constant |
        index |
        parenthesizedExpression
    ;

variableName
    :   ID<AST=zserio.ast.Expression>
    ;

constant
    :   DECIMAL_LITERAL |
        BINARY_LITERAL |
        FLOAT_LITERAL |
        DOUBLE_LITERAL |
        HEXADECIMAL_LITERAL |
        OCTAL_LITERAL |
        BOOL_LITERAL |
        STRING_LITERAL
    ;

index
    :   ATSIGN! INDEX^
    ;

parenthesizedExpression
    :   LPAREN^ expression RPAREN!
    ;

postfixOperand
    :   arrayOperand |
        functionArgumentList |
        dotOperand
    ;

arrayOperand!
    :   LBRACKET e:expression RBRACKET
        {
            #arrayOperand = #([ARRAYELEM], e);
        }
    ;

functionArgumentList
    :   LPAREN! (functionArgument (COMMA! functionArgument)* )? RPAREN!
        {
            #functionArgumentList = #([FUNCTIONCALL], functionArgumentList);
        }
    ;

dotOperand
    :   DOT^ ID<AST=zserio.ast.Expression>
    ;

unaryOperand!
    :   o:unaryOperator e:unaryExpression
        {
            #unaryOperand = #(o, e);
        }
    ;

unaryOperator
    :   p:PLUS!
        {
            #unaryOperator = #[UPLUS, "+"];
            ((TokenAST)#unaryOperator).setImaginaryTokenPosition((TokenAST)#p);
        } |
        m:MINUS!
        {
            #unaryOperator = #[UMINUS, "-"];
            ((TokenAST)#unaryOperator).setImaginaryTokenPosition((TokenAST)#m);
        } |
        TILDE |
        BANG
    ;

lengthOfOperand
    :   LENGTHOF^ unaryExpression
    ;

sumFunction
    :   SUM^ LPAREN! functionArgument RPAREN!
    ;

functionArgument
    :   conditionalExpression
    ;

numbitsFunction
    :   NUMBITS^ LPAREN! functionArgument RPAREN!
    ;

conditionalExpression
    :   logicalOrExpression (QUESTIONMARK^ expression COLON! conditionalExpression)?
    ;

logicalOrExpression
    :   logicalAndExpression (LOGICALOR^ logicalOrExpression)?
    ;

logicalAndExpression
    :   inclusiveOrExpression  (LOGICALAND^ logicalAndExpression)?
    ;

inclusiveOrExpression
    :   exclusiveOrExpression (OR^ inclusiveOrExpression)?
    ;

exclusiveOrExpression
    :   andExpression (XOR^ exclusiveOrExpression)?
    ;

andExpression
    :   equalityExpression (AND^ andExpression)?
    ;

equalityExpression
    :   relationalExpression  (( EQ^ | NE^ ) equalityExpression)?
    ;

relationalExpression
    :   shiftExpression (( LT^ | LE^ | GT^ | GE^) shiftExpression)*
    ;

shiftExpression
    :   additiveExpression ((LSHIFT^ | RSHIFT^) additiveExpression)*
    ;

additiveExpression
    :   multiplicativeExpression  ((PLUS^ | MINUS^) multiplicativeExpression)*
    ;

multiplicativeExpression
    :   unaryExpression ((MULTIPLY^ | DIVIDE^ | MODULO^) unaryExpression)*
    ;

/**
 * constantExpression.
 */
constantExpression
    :   logicalOrExpression
    ;

/**
 * variableExpression.
 */
variableExpression
    :   variableName
        (
            o:postfixOperand!
            {
                if (#o != null)
                {
                    AST rhs = #o.getFirstChild();
                    #variableExpression= #(o, variableExpression, rhs);
                }
            }
        )*
    ;
