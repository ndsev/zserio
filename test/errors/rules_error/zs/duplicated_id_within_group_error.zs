package duplicated_id_within_group_error;

rule_group RuleGroup
{
    /*! Rule one. */
    rule "rule-one";

    /*! Rule two. */
    rule "rule-two";

    /*! Duplicated rule one. */
    rule "rule-one";
};

