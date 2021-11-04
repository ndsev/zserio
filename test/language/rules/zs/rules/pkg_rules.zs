package rules.pkg_rules;

struct Test
{
    // just a test struct to have something to reference to
};

/*!
Rules for this package.
!*/
rule_group PackageRules
{
    /*!
    Special rule.

    ![resource](../../data/resource.md)

    Referencing inner rule [rules-03](#rules-03).
    !*/
    rule "pkg-rules-01";

    /*!
    Referencing another rule [rules-02](../rules.zs#rules-02).
    !*/
    rule "pkg-rules-02";

    /*!
    Rule with the same id as the [rules-03](../rules.zs#rules-03) in different package.
    !*/
    rule "rules-03";
};
