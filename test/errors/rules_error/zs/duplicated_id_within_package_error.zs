package duplicated_id_within_package_error;

rule_group RuleGroupOne
{
    /*! Rule one. */
    rule "rule-one";

    /*! Rule two. */
    rule "rule-two";
};

rule_group RuleGroupTwo
{
    /*! Conflicting id with rule-one in RuleGroupOne. Must be unique using case insensitive comparison! */
    rule "rule-ONE";
};
