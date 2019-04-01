parser grammar Zserio4Parser;

options
{
    tokenVocab=Zserio4Lexer;
}

// TRANSLATION UNIN (main rule)
translationUnit
    :   packageDeclaration?
        importDeclaration*
        typeDeclaration*
        EOF
    ;

packageDeclaration
    :   PACKAGE qualifiedName SEMICOLON
    ;

importDeclaration
    :   IMPORT importName SEMICOLON
    ;

importName
    :   ID DOT (ID DOT)* (ID | MULTIPLY)
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
    :   CONST typeName ID ASSIGN constantExpression SEMICOLON
    ;


// SUBTYPE

subtypeDeclaration
    :   SUBTYPE typeName ID SEMICOLON
    ;


// STRUCTURE

structureDeclaration
    :   STRUCTURE ID parameterList?
        LBRACE
        structureFieldDefinition*
        functionDefinition*
        RBRACE
        SEMICOLON
    ;

structureFieldDefinition
    :   fieldAlignment?
        fieldOffset?
        (
            OPTIONAL
            fieldTypeId
            fieldInitializer?
            fieldConstraint?
        |
            fieldTypeId
            fieldInitializer?
            fieldOptionalClause?
            fieldConstraint?
        )
        SEMICOLON
    ;

fieldAlignment
    :   ALIGN LPAREN DECIMAL_LITERAL RPAREN COLON
    ;

fieldOffset
    :   expression COLON // TODO: try to prune in the grammar!
    ;

fieldTypeId
    :   IMPLICIT typeReference ID LBRACKET RBRACKET
    |   typeReference ID fieldArrayRange?
    ;

fieldArrayRange
    :   LBRACKET expression? RBRACKET
    ;

fieldInitializer
    :   ASSIGN constantExpression
    ;

fieldOptionalClause
    :   IF expression
    ;

fieldConstraint
    :   COLON expression
    ;

// CHOICE

choiceDeclaration
    :   CHOICE ID parameterList ON choiceTag
        LBRACE
        choiceCases*
        choiceDefault?
        functionDefinition*
        RBRACE
    ;

choiceTag
    :   expression
    ;

choiceCases
    :   choiceCase+ choiceFieldDefinition?
    ;

choiceCase
    : CASE expression COLON
    ;

choiceDefault
    :   DEFAULT COLON choiceFieldDefinition?
    ;

choiceFieldDefinition
    :   fieldTypeId fieldConstraint? SEMICOLON
    ;


// UNION

unionDeclaration
    :   UNION ID parameterList?
        LBRACE
        unionFieldDefinition*
        functionDefinition*
        RBRACE
    ;

unionFieldDefinition
    :   choiceFieldDefinition
    ;


// ENUM

enumDeclaration
    :   ENUM typeName ID
        LBRACE
        enumItem (COMMA enumItem)* COMMA?
        RBRACE
    ;

enumItem
    :   ID (ASSIGN constantExpression)?
    ;


// SQL TABLE

sqlTableDeclaration
    :   SQL_TABLE ID (USING ID)?
        LBRACE
        sqlTableFieldDefinition*
        sqlConstraintDefinition?
        sqlWithoutRowId?
        RBRACE
    ;

sqlTableFieldDefinition
    :   SQL_VIRTUAL? typeReference ID sqlConstraint? SEMICOLON
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
    :   SQL_DATABASE ID
        LBRACE
        sqlDatabaseFieldDefinition+
        RBRACE
    ;

sqlDatabaseFieldDefinition
    :   sqlTableReference ID SEMICOLON
    ;

sqlTableReference
    :   qualifiedName
    ;


// RPC SERVICE

serviceDefinition
    :   SERVICE ID
        LBRACE
        rpcDeclaration*
        RBRACE
    ;

rpcDeclaration
    :   RPC STREAM? qualifiedName ID LPAREN STREAM? qualifiedName RPAREN SEMICOLON
    ;


// FUNCTION

functionDefinition
    :   FUNCTION functionType
        functionName LPAREN RPAREN // zserio funciton cannot have any arguments
        functionBody
    ;

functionType
    :   typeName // function doesn't need to specify parameters of parameterized types
    ;

functionName
    :   ID
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
    :   typeName ID
    ;


// EXPRESSION

constantExpression // TODO: do we need this?
    :   expression
    ;

expression
    :   primary                                             // primary atom / parenthesised expression
    |   expression DOT ID                                   // postfix dot
    |   expression LBRACKET expression RBRACKET             // postfix array
    |   expression LPAREN RPAREN                            // postfix function call
    |   (PLUS | MINUS) expression                           // unary +/-
    |   (TILDE | BANG) expression                           // unary ~/!
    |   expression (MULTIPLY | DIVIDE | MODULO) expression  // multiplicative
    |   expression (PLUS | MINUS) expression                // additive
    |   expression (LSHIFT | RSHIFT) expression             // shift
    |   expression (LT | LE | GT | GE) expression           // relational
    |   expression (EQ | NE) expression                     // equality
    |   expression AND expression                           // bitwise and
    |   expression XOR expression                           // bitwise xor
    |   expression OR expression                            // bitwise or
    |   expression LOGICAL_AND expression                   // logical and
    |   expression LOGICAL_OR expression                    // logical or
    |   expression QUESTIONMARK expression COLON expression // ternary operator
    ;

primary
    :   LPAREN expression RPAREN
    |   builtinFunciton
    |   literal
    |   INDEX
    |   ID
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


// BUILTIN FUNCTIONS

builtinFunciton
    :   lengthof
    |   numbits
    |   sum
    |   valueof
    ;

lengthof
    :   LENGTHOF LPAREN expression RPAREN
    ;

sum
    :   SUM LPAREN expression RPAREN
    ;

valueof
    :   VALUEOF LPAREN expression RPAREN
    ;

numbits
    :   NUMBITS LPAREN expression RPAREN
    ;


// TYPES

typeName
    :   builtinType
    |   qualifiedName
    ;

builtinType
    :   intType
    |   varintType
    |   unsignedBitfieldType
    |   signedBitfieldType
    |   boolType
    |   stringType
    |   floatType
    ;

qualifiedName
    :   ID (DOT ID)*
    ;

typeReference
    :   builtinType
    |   qualifiedName typeArgumentList?
    ;

typeArgumentList
    :   LPAREN typeArgument (COMMA typeArgument)* RPAREN
    ;

typeArgument
    :   EXPLICIT ID // TODO: allow only within a SQL table!
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

unsignedBitfieldType
    :   BIT_FIELD bitfieldLength
    ;

signedBitfieldType
    :   INT_FIELD bitfieldLength
    ;

bitfieldLength
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
