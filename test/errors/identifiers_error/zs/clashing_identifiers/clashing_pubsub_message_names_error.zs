package clashing_identifiers.clashing_pubsub_message_names_error;

struct Message
{
    string message;
};

pubsub Pubsub
{
    topic("test/x") Message x_message;
    topic("test/X") Message X_message;
};
