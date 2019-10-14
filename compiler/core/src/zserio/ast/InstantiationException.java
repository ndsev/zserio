package zserio.ast;

/**
 * ParserStackedException used for reporting of exceptions during template instantiations.
 *
 * The exception reports whole trace of instantiation process.
 */
public class InstantiationException extends ParserStackedException
{
    /**
     * Constructor from parser exception and instantiation reference stack.
     *
     * @param exception                   The main parser exception containing the main error message.
     * @param instantiationReferenceStack Stack of instantiation references.
     */
    public InstantiationException(ParserException exception,
            Iterable<TypeReference> instantiationReferenceStack)
    {
        super(exception);

        pushInstantiationStack(instantiationReferenceStack);
    }

    /**
     * Constructor from the main error message and instantiation reference stack.
     *
     * @param location                    AST location where the error occurred.
     * @param message                     The main message for the error.
     * @param instantiationReferenceStack Stack of instantiation references.
     */
    public InstantiationException(AstLocation location, String message,
            Iterable<TypeReference> instantiationReferenceStack)
    {
        super(location, message);

        pushInstantiationStack(instantiationReferenceStack);
    }

    private void pushInstantiationStack(Iterable<TypeReference> instantiationReferenceStack)
    {
        for (TypeReference instantiationReference : instantiationReferenceStack)
        {
            pushMessage(instantiationReference.getLocation(), "In instantiation of '" +
                    instantiationReference.getReferencedTypeName() + "' required from here");
        }
    }

    private static final long serialVersionUID = -1326654366145118890L;
}
