parser grammar ZserioParser;

options
{
    tokenVocab=ZserioLexer;
}

tokens { RSHIFT }


// PACKAGE (main rule)

packageDeclaration
    :   compatibilityVersionDirective?
        packageNameDefinition?
        importDeclaration*
        languageDirective*
        EOF
    ;

compatibilityVersionDirective
    :   COMPAT_VERSION LPAREN STRING_LITERAL RPAREN SEMICOLON
    ;

packageNameDefinition
    :   PACKAGE id (DOT id)* SEMICOLON
    ;

importDeclaration
    :   IMPORT id DOT (id DOT)* (id | MULTIPLY) SEMICOLON
    ;

languageDirective
    :   symbolDefinition
    |   typeDeclaration
    ;

typeDeclaration
    :   subtypeDeclaration
    |   structureDeclaration
    |   choiceDeclaration
    |   unionDeclaration
    |   enumDeclaration
    |   bitmaskDeclaration
    |   sqlTableDeclaration
    |   sqlDatabaseDefinition
    |   serviceDefinition
    |   pubsubDefinition
    |   instantiateDeclaration
    ;

symbolDefinition
    :   constDefinition
    |   ruleGroupDefinition
    ;

// CONST

constDefinition
    :   CONST typeInstantiation id ASSIGN expression SEMICOLON
    ;


// RULES

ruleGroupDefinition
    :   RULE_GROUP id
        LBRACE
        ruleDefinition*
        RBRACE
        SEMICOLON
    ;

ruleDefinition
    :   RULE expression SEMICOLON // string expression
    ;


// SUBTYPE

subtypeDeclaration
    :   SUBTYPE typeReference id SEMICOLON
    ;


// STRUCTURE

structureDeclaration
    :   STRUCTURE id templateParameters? typeParameters?
        LBRACE
        structureFieldDefinition*
        functionDefinition*
        RBRACE
        SEMICOLON
    ;

structureFieldDefinition
    :   fieldAlignment?
        fieldOffset?
        EXTEND?
        OPTIONAL?
        fieldTypeId
        fieldInitializer?
        fieldOptionalClause?
        fieldConstraint?
        SEMICOLON
    ;

fieldAlignment
    :   ALIGN LPAREN expression RPAREN COLON // integer expression
    ;

fieldOffset
    :   expression COLON
    ;

fieldTypeId
    :   PACKED? IMPLICIT? typeInstantiation id fieldArrayRange?
    ;

fieldArrayRange
    :   LBRACKET expression? RBRACKET
    ;

fieldInitializer
    :   ASSIGN expression // constant expression
    ;

fieldOptionalClause
    :   IF expression
    ;

fieldConstraint
    :   COLON expression
    ;


// CHOICE

choiceDeclaration
    :   CHOICE id templateParameters? typeParameters ON expression
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
    :   CASE expression COLON // constant expression
    ;

choiceDefault
    :   DEFAULT COLON choiceFieldDefinition? SEMICOLON
    ;

choiceFieldDefinition
    :   fieldTypeId fieldConstraint?
    ;


// UNION

unionDeclaration
    :   UNION id templateParameters? typeParameters?
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
    :   ENUM typeInstantiation id
        LBRACE
        enumItem (COMMA enumItem)* COMMA?
        RBRACE
        SEMICOLON
    ;

enumItem
    :   (DEPRECATED | REMOVED)? id (ASSIGN expression)?
    ;


// BITMASK

bitmaskDeclaration
    :   BITMASK typeInstantiation id
        LBRACE
        bitmaskValue (COMMA bitmaskValue)* COMMA?
        RBRACE
        SEMICOLON
    ;

bitmaskValue
    :    id (ASSIGN expression)?
    ;


// SQL TABLE

sqlTableDeclaration
    :   SQL_TABLE id templateParameters? (USING id)?
        LBRACE
        sqlTableFieldDefinition*
        sqlConstraintDefinition?
        sqlWithoutRowId?
        RBRACE
        SEMICOLON
    ;

sqlTableFieldDefinition
    :   SQL_VIRTUAL? typeInstantiation id sqlConstraint? SEMICOLON
    ;

sqlConstraintDefinition
    :   sqlConstraint SEMICOLON
    ;

sqlConstraint
    :   SQL expression // string expression
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
    :   typeInstantiation id SEMICOLON
    ;


// SERVICE

serviceDefinition
    :   SERVICE id
        LBRACE
        serviceMethodDefinition*
        RBRACE
        SEMICOLON
    ;

serviceMethodDefinition
    :   typeReference id LPAREN typeReference RPAREN SEMICOLON
    ;


// PUBSUB

pubsubDefinition
    :   PUBSUB id
        LBRACE
        pubsubMessageDefinition*
        RBRACE
        SEMICOLON
    ;

pubsubMessageDefinition
    :   topicDefinition typeReference id SEMICOLON
    ;

topicDefinition
    :   (PUBLISH | SUBSCRIBE)? TOPIC
        LPAREN
        expression // string expression
        RPAREN
    ;


// FUNCTION

functionDefinition
    :   FUNCTION functionType
        functionName LPAREN RPAREN // zserio functions cannot have any arguments
        functionBody
    ;

functionType
    :   typeReference
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

typeParameters
    :   LPAREN parameterDefinition (COMMA parameterDefinition)* RPAREN
    ;

parameterDefinition
    :   typeReference id
    ;


// TEMPLATES

templateParameters
    :   LT id (COMMA id)* GT
    ;

templateArguments
    :   LT templateArgument (COMMA templateArgument)* GT
    ;

templateArgument
    :   typeReference
    ;

instantiateDeclaration
    :   INSTANTIATE typeReference id SEMICOLON
    ;


// EXPRESSION

expression
    :   operator=LPAREN expression RPAREN                                           # parenthesizedExpression
    |   expression LPAREN operator=RPAREN                                           # functionCallExpression
    |   expression operator=LBRACKET expression RBRACKET                            # arrayExpression
    |   expression operator=DOT id                                                  # dotExpression
    |   operator=ISSET LPAREN expression COMMA expression RPAREN                    # isSetExpression
    |   operator=LENGTHOF LPAREN expression RPAREN                                  # lengthofExpression
    |   operator=VALUEOF LPAREN expression RPAREN                                   # valueofExpression
    |   operator=NUMBITS LPAREN expression RPAREN                                   # numbitsExpression
    |   operator=(PLUS | MINUS | BANG | TILDE) expression                           # unaryExpression
    |   expression operator=(MULTIPLY | DIVIDE | MODULO) expression                 # multiplicativeExpression
    |   expression operator=(PLUS | MINUS) expression                               # additiveExpression
    |   expression (operator=LSHIFT | operator=GT GT) expression                    # shiftExpression
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

typeReference
    :   builtinType
    |   qualifiedName templateArguments?
    ;

typeInstantiation
    :   typeReference (typeArguments | dynamicLengthArgument)?
    ;

builtinType
    :   intType
    |   varintType
    |   fixedBitFieldType
    |   dynamicBitFieldType
    |   boolType
    |   stringType
    |   floatType
    |   externType
    |   bytesType
    ;

qualifiedName
    :   id (DOT id)*
    ;

typeArguments
    :   LPAREN typeArgument (COMMA typeArgument)* RPAREN
    ;

typeArgument
    :   EXPLICIT id
    |   expression
    ;

dynamicLengthArgument
    :   LT expression GT
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
    |   VARSIZE
    |   VARUINT
    |   VARUINT16
    |   VARUINT32
    |   VARUINT64
    ;

fixedBitFieldType
    :   (BIT_FIELD | INT_FIELD) COLON DECIMAL_LITERAL
    ;

dynamicBitFieldType
    :   (BIT_FIELD | INT_FIELD)
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

externType
    :   EXTERN
    ;

bytesType
    :   BYTES
    ;
