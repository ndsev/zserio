package rules;

import rules.pkg_rules.*;

rule_group Rules
{
    /*!
    Some description.

    ![Logo](../data/logo.png)
    !*/
    rule "rules-01";

    /*!
    Other rule description
    !*/
    rule "rules-02";

    /**
     * @see Test
     */
    rule "rules-03";
};

rule_group OtherRules
{
    rule "other-01";
};

rule_group EmptyGroup
{};
