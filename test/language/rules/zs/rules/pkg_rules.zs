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
    !*/
    rule "pkg-rules-01";

    /*!
    Referencing another rule [rules-02](../rules.zs#rules-02).
    !*/
    rule "pkg-rules-02";
};
