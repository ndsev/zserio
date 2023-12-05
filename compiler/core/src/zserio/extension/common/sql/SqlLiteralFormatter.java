package zserio.extension.common.sql;

import java.math.BigInteger;

import zserio.ast.Expression;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.common.sql.types.NativeIntegerType;
import zserio.extension.common.sql.types.NativeRealType;
import zserio.extension.common.sql.types.NativeTextType;

/**
 * Utility for expression formatting in SQLite format.
 */
public final class SqlLiteralFormatter
{
    /**
     * Formats expression value in SQLite format.
     *
     * SQLite value does not support any operands. Therefore the given expression value must be integer with
     * known result or it must not have any operands.
     *
     * @param valueExpression Expression value to format.
     *
     * @return The value in SQLite format.
     *
     * @throws ZserioExtensionException In case of any error in the extension.
     */
    public static String formatLiteral(Expression valueExpression) throws ZserioExtensionException
    {
        String sqlLiteral;
        switch (valueExpression.getExprType())
        {
            case INTEGER:
            case ENUM:
                final BigInteger integerValue = valueExpression.getIntegerValue();
                if (integerValue == null)
                    throw new ZserioExtensionException("Unexpected unresolved integer expression!");
                sqlLiteral = NativeIntegerType.formatLiteral(integerValue);
                break;

            case FLOAT:
                sqlLiteral = NativeRealType.formatLiteral(valueExpression.getText());
                break;

            case STRING:
                sqlLiteral = NativeTextType.formatLiteral(valueExpression.getText());
                break;

            case BOOLEAN:
                sqlLiteral = valueExpression.getText();
                break;

            default:
                throw new ZserioExtensionException("Unexpected expression type:" +
                        valueExpression.getExprType());
        }

        return sqlLiteral;
    }
}
