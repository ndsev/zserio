parser grammar ZserioParser;

options
{
    tokenVocab=ZserioLexer;
}


// PACKAGE (main rule)

packageDeclaration
    :   packageNameDefinition?
        importDeclaration*
        typeDeclaration*
        EOF
    ;

packageNameDefinition
    :   PACKAGE qualifiedName SEMICOLON
    ;

importDeclaration
    :   IMPORT id DOT (id DOT)* (id | MULTIPLY) SEMICOLON
    ;

typeDeclaration
    :   constDeclaration
    |   subtypeDeclaration
    |   structureDeclaration
    |   choiceDeclaration
    |   unionDeclaration
    |   enumDeclaration
    |   sqlTableDeclaration
    |   sqlDatabaseDefinition
    |   serviceDefinition
    ;


// CONST

constDeclaration
    :   CONST typeName id ASSIGN expression SEMICOLON
    ;


// SUBTYPE

subtypeDeclaration
    :   SUBTYPE typeName id SEMICOLON
    ;


// STRUCTURE

structureDeclaration
    :   STRUCTURE id parameterList?
        LBRACE
        structureFieldDefinition*
        functionDefinition*
        RBRACE
        SEMICOLON
    ;

structureFieldDefinition
    :   fieldAlignment?
        fieldOffset?
        OPTIONAL?
        fieldTypeId
        fieldInitializer?
        fieldOptionalClause?
        fieldConstraint?
        SEMICOLON
    ;

fieldAlignment
    :   ALIGN LPAREN DECIMAL_LITERAL RPAREN COLON
    ;

fieldOffset
    :   expression COLON
    ;

fieldTypeId
    :   IMPLICIT? typeReference id fieldArrayRange?
    ;

fieldArrayRange
    :   LBRACKET expression? RBRACKET
    ;

fieldInitializer
    :   ASSIGN expression
    ;

fieldOptionalClause
    :   IF expression
    ;

fieldConstraint
    :   COLON expression
    ;


// CHOICE

choiceDeclaration
    :   CHOICE id parameterList ON expression
        LBRACE
        choiceCases*
        choiceDefault?
        functionDefinition*
        RBRACE
        SEMICOLON
    ;

choiceCases
    :   choiceCase+ choiceFieldDefinition? SEMICOLON
    ;

choiceCase
    : CASE expression COLON
    ;

choiceDefault
    :   DEFAULT COLON choiceFieldDefinition? SEMICOLON
    ;

choiceFieldDefinition
    :   fieldTypeId fieldConstraint?
    ;


// UNION

unionDeclaration
    :   UNION id parameterList?
        LBRACE
        unionFieldDefinition*
        functionDefinition*
        RBRACE
        SEMICOLON
    ;

unionFieldDefinition
    :   choiceFieldDefinition SEMICOLON
    ;


// ENUM

enumDeclaration
    :   ENUM typeName id
        LBRACE
        enumItem (COMMA enumItem)* COMMA?
        RBRACE
        SEMICOLON
    ;

enumItem
    :   id (ASSIGN expression)?
    ;


// SQL TABLE

sqlTableDeclaration
    :   SQL_TABLE id (USING id)?
        LBRACE
        sqlTableFieldDefinition*
        sqlConstraintDefinition?
        sqlWithoutRowId?
        RBRACE
        SEMICOLON
    ;

sqlTableFieldDefinition
    :   SQL_VIRTUAL? typeReference id sqlConstraint? SEMICOLON
    ;

sqlConstraintDefinition
    :   sqlConstraint SEMICOLON
    ;

sqlConstraint
    :   SQL STRING_LITERAL
    ;

sqlWithoutRowId
    :   SQL_WITHOUT_ROWID SEMICOLON
    ;


// SQL DATABASE

sqlDatabaseDefinition
    :   SQL_DATABASE id
        LBRACE
        sqlDatabaseFieldDefinition+
        RBRACE
        SEMICOLON
    ;

sqlDatabaseFieldDefinition
    :   sqlTableReference id SEMICOLON
    ;

sqlTableReference
    :   qualifiedName
    ;


// RPC SERVICE

serviceDefinition
    :   SERVICE id
        LBRACE
        rpcDeclaration*
        RBRACE
        SEMICOLON
    ;

rpcDeclaration
    :   RPC rpcTypeName id LPAREN rpcTypeName RPAREN SEMICOLON
    ;

rpcTypeName
    :   STREAM? qualifiedName
    ;


// FUNCTION

functionDefinition
    :   FUNCTION functionType
        functionName LPAREN RPAREN // zserio functions cannot have any arguments
        functionBody
    ;

functionType
    :   typeName // function doesn't need to specify parameters of parameterized types
    ;

functionName
    :   id
    ;

functionBody
    :   LBRACE
        RETURN expression SEMICOLON
        RBRACE
    ;


// PARAMETERS

parameterList
    :   LPAREN parameterDefinition (COMMA parameterDefinition)* RPAREN
    ;

parameterDefinition
    :   typeName id
    ;


// EXPRESSION

expression
    :   operator=LPAREN expression RPAREN                                           # parenthesizedExpression
    |   expression LPAREN operator=RPAREN                                           # functionCallExpression
    |   expression operator=LBRACKET expression RBRACKET                            # arrayExpression
    |   expression operator=DOT id                                                  # dotExpression
    |   operator=LENGTHOF LPAREN expression RPAREN                                  # lengthofExpression
    |   operator=SUM LPAREN expression RPAREN                                       # sumExpression
    |   operator=VALUEOF LPAREN expression RPAREN                                   # valueofExpression
    |   operator=NUMBITS LPAREN expression RPAREN                                   # numbitsExpression
    |   operator=(PLUS | MINUS | BANG | TILDE) expression                           # unaryExpression
    |   expression operator=(MULTIPLY | DIVIDE | MODULO) expression                 # multiplicativeExpression
    |   expression operator=(PLUS | MINUS) expression                               # additiveExpression
    |   expression operator=(LSHIFT | RSHIFT) expression                            # shiftExpression
    |   expression operator=(LT | LE | GT | GE) expression                          # relationalExpression
    |   expression operator=(EQ | NE) expression                                    # equalityExpression
    |   expression operator=AND expression                                          # bitwiseAndExpression
    |   expression operator=XOR expression                                          # bitwiseXorExpression
    |   expression operator=OR expression                                           # bitwiseOrExpression
    |   expression operator=LOGICAL_AND expression                                  # logicalAndExpression
    |   expression operator=LOGICAL_OR expression                                   # logicalOrExpression
    |   <assoc=right>expression operator=QUESTIONMARK expression COLON expression   # ternaryExpression
    |   literal                                                                     # literalExpression
    |   INDEX                                                                       # indexExpression
    |   id                                                                          # identifierExpression
    ;

literal
    :   BINARY_LITERAL
    |   OCTAL_LITERAL
    |   DECIMAL_LITERAL
    |   HEXADECIMAL_LITERAL
    |   BOOL_LITERAL
    |   STRING_LITERAL
    |   FLOAT_LITERAL
    |   DOUBLE_LITERAL
    ;

id
    :   ID
    ;


// TYPES

typeName
    :   builtinType
    |   qualifiedName
    ;

typeReference
    :   builtinType
    |   qualifiedName typeArgumentList?
    ;

builtinType
    :   intType
    |   varintType
    |   unsignedBitFieldType
    |   signedBitFieldType
    |   boolType
    |   stringType
    |   floatType
    ;

qualifiedName
    :   id (DOT id)*
    ;

typeArgumentList
    :   LPAREN typeArgument (COMMA typeArgument)* RPAREN
    ;

typeArgument
    :   EXPLICIT id
    |   expression
    ;

intType
    :   INT8
    |   INT16
    |   INT32
    |   INT64
    |   UINT8
    |   UINT16
    |   UINT32
    |   UINT64
    ;

varintType
    :   VARINT
    |   VARINT16
    |   VARINT32
    |   VARINT64
    |   VARUINT
    |   VARUINT16
    |   VARUINT32
    |   VARUINT64
    ;

unsignedBitFieldType
    :   BIT_FIELD bitFieldLength
    ;

signedBitFieldType
    :   INT_FIELD bitFieldLength
    ;

bitFieldLength
    :   COLON DECIMAL_LITERAL
    |   LT expression GT
    ;

boolType
    :   BOOL
    ;

stringType
    :   STRING
    ;

floatType
    :   FLOAT16
    |   FLOAT32
    |   FLOAT64
    ;
