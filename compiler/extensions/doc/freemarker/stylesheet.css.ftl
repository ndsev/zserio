<#-- constant values -->
<#assign headerPaddingTop=0.5/>
<#assign headerPaddingBottom=0.5/>
<#assign headerHeight=3.5/>
<#assign headerLogoHeight=1/>
<#assign anchorPaddingTop=0.25/>
<#assign headingAnchorPaddingTop=0.75/>
<#assign leftPanelPaddingTop=0.25/>
<#assign leftPanelPaddingBottom=leftPanelPaddingTop/>
<#assign leftPanelPaddingLeft=1/>
<#assign leftPanelPaddingRight=0/>
<#assign searchFormPaddingTop=1/>
<#assign searchFormPaddingBottom=searchFormPaddingTop/>
<#assign searchFormPaddingLeft=0.75/>
<#assign searchFormPaddingRight=searchFormPaddingLeft/>
<#assign searchHeight=2.25/>
<#assign searchPaddingTop=0.375/>
<#assign searchPaddingBottom=searchPaddingTop/>
<#assign searchPaddingLeft=0.75/>
<#assign searchPaddingRight=searchPaddingLeft/>
<#assign symbolOverviewPaddingTop=0.75/>
<#assign tocPaddingTop=0.25/>
<#assign tocPaddingBottom=tocPaddingTop/>
<#assign codeIndent=2/>
<#-- colors -->
<#assign borderColorLight="rgb(0,0,0,0.1)"/>
<#assign borderColorDark="rgb(0,0,0,0.2)"/>
<#assign codeBackgroundColor="rgb(0,0,0,0.05)"/>
<#assign linkColor="rgb(0,0,0,0.65)"/>
<#assign linkHoverColor="rgb(0,0,0,0.85)"/>
<#assign tokenBlueColor="rgb(0,0,128,0.65)"/>
<#assign tokenHoverBlueColor="rgb(0,0,128,0.85)"/>
<#assign tokenVioletColor="rgb(128,0,128,0.65)"/>
<#assign tokenHoverVioletColor="rgb(128,0,128,0.85)"/>
<#assign tokenBrownColor="rgb(128,64,0,0.65)"/>
<#assign tokenHoverBrownColor="rgb(128,64,0,0.85)"/>
<#assign tokenGreenColor="rgb(0,128,0,0.65)"/>
<#assign tokenHoverGreenColor="rgb(0,128,0,0.85)"/>
<#assign deprecatedColor="gray"/>
<#assign headerNavColor="white"/>
<#assign headerNavHoverColor="rgb(255,255,255,0.65)"/>
<#assign codeDocColor="gray"/>
<#-- stylesheet -->
.anchor:not(h1):not(h2), /* anchors for zserio fields, functions, etc. */
.anchor-md:not(h1):not(h2) { /* normal anchors from markdown */
  scroll-margin-top: ${headerHeight}rem;
  padding-top: ${anchorPaddingTop}rem;
}

main > h1:first-child, /* package heading */
main > .doc:first-child > h1:first-child, /* floating comment before the package declaration  */
main > .doc:first-child > h2:first-child {
  border-top: none;
  padding-top: 0; /* prevent scrolling above the anchor scroll */
  scroll-margin-top: ${headerHeight}rem;
}

h1 { /* package name or headings from documentation */
  border-top: 4px solid ${borderColorLight};
  /* set-up scroll to hide the border-top (horizontal line) */
  scroll-margin-top: calc(${headerHeight}rem - 4px);
  padding-top: ${headingAnchorPaddingTop}rem;
}

h2 { /* symbols in package or doc headings */
  border-top: 3px solid ${borderColorLight};
  /* set-up scroll to hide the border-top (horizontal line) */
  scroll-margin-top: calc(${headerHeight}rem - 3px);
  padding-top: ${headingAnchorPaddingTop}rem;
}

.code h1,
.code h2 {
  border-top: none;
  padding-top: revert;
  scroll-margin-top: ${headerHeight}rem;
}

.anchor-group {
  scroll-margin-top: ${headerHeight}rem;
}

#header {
  height: ${headerHeight}rem;
  padding-top: ${headerPaddingTop}rem;
  padding-bottom: ${headerPaddingBottom}rem;
}

#header .navbar-nav {
  color: ${headerNavColor};
}

#header #zserio-logo {
  height: ${headerLogoHeight}rem;
  color: ${headerNavColor};
}

#header #zserio-logo:hover #zserio-logo-serio {
  fill: ${headerNavHoverColor};
}

#header #zserio-logo:hover #zserio-logo-z {
  fill: rgba(255, 0, 0, 0.85);
}

.navbar-nav.navbar-center { /* custom bootstrap extension */
  position: absolute;
  left: 50%;
  transform: translatex(-50%);
}

#toc_button {
  padding: 0;
  margin: 0;
}

#toc_button:hover {
  color: ${headerNavHoverColor};
}

#left_panel {
  top: ${headerHeight}rem;
  height: calc(100vh - ${headerHeight}rem);
  overflow: hidden;
  position: sticky;
  border-right: 1px solid ${borderColorLight};
  padding-top: ${leftPanelPaddingTop}rem;
  padding-bottom: ${leftPanelPaddingBottom}rem;
  padding-left: ${leftPanelPaddingLeft}rem;
  padding-right: ${leftPanelPaddingRight}rem;
}

#search_form {
  padding-top: ${searchFormPaddingTop}rem;
  padding-bottom: ${searchFormPaddingBottom}rem;
  padding-left: ${searchFormPaddingLeft}rem;
  padding-right: ${searchFormPaddingRight}rem;
  margin-left: -${leftPanelPaddingLeft}rem; /* bottom border across the whole left panel */
  border-bottom: 1px solid ${borderColorLight};
}

#search {
  height: ${searchHeight}rem;
  padding-top: ${searchPaddingTop}rem;
  padding-bottom: ${searchPaddingBottom}rem;
  padding-left: ${searchPaddingLeft}rem;
  padding-right: ${searchPaddingRight}rem;
  border-color: ${borderColorLight};
}

#search:focus {
  border-color: ${borderColorDark};
  box-shadow: 0 0 0 3px ${borderColorLight};
}

#symbol_overview {
  padding-top: ${symbolOverviewPaddingTop}rem;
  overflow-y: auto;
  height: calc(100% - ${searchFormPaddingTop + searchHeight + searchFormPaddingBottom}rem);
  flex-wrap: nowrap;
}

.nav-link {
  padding-top: 0.125rem;
  padding-right: 1.5rem;
  padding-bottom: 0.125rem;
  padding-left: 0.5rem;
}

#toc {
  top: ${headerHeight}rem;
  height: calc(100vh - ${headerHeight}rem);
  position: sticky;
  font-size: 0.875rem;
  overflow-x: hidden;
  overflow-y: hidden; /* scrolls automatically using javascript */
  padding-top: ${tocPaddingTop}rem;
  padding-bottom: ${tocPaddingBottom}rem;
}

#toc .nav .nav-link.active {
  border-left: 1px solid ${borderColorDark};
}

.nav-symbols {
  display: none; /* display only symbols for active package */
  margin-left: 1rem;
}

.nav-symbols.active {
  display: block;
}

.nav-symbols.active.collapsed {
  display: none;
}

.nav-link-package.active {
  font-weight: bold;
}

.nav-link,
a {
  color: ${linkColor};
}

.nav-link:hover,
a:hover {
  color: ${linkHoverColor};
  text-decoration: none;
}

.code {
  font-family: monospace;
  padding: 0.5rem;
  margin-top: 1rem;
}

.code-background {
  background-color: ${codeBackgroundColor};
  border-radius: 0.5rem;
  padding: 1rem;
  max-width: max-content;
  min-width: min-content;
  margin-bottom: 0.5rem;
}

.code td {
  padding-right: 0.4rem;
  white-space: nowrap;
}

.code td:last-child {
  width: 100%; /* make last columns the only ones which are expanding to preserve whole table compact */
}

.code td.indent {
  padding-left: ${codeIndent}rem;
}

.code td.indent.empty {
  padding-left: 0;
  width: ${codeIndent}rem;
  min-width: ${codeIndent}rem;
  max-width: ${codeIndent}rem;
}

.code td.value-expression  {
  padding-left: 1rem;
}

.code div.doc {
  white-space: normal;
  color: ${codeDocColor};
}

.code p:last-child {
  margin-bottom: 0;
}

.code .btn {
  margin: 0;
  padding: 0;
  margin-left: 0.2rem;
  vertical-align: baseline;
  color: ${linkColor};
}

.code .btn:hover {
  color: ${linkHoverColor};
}

.code .btn.comments-hidden {
    color: rgba(0,0,0,0.25);
}

.code .btn.comments-hidden:hover {
    color: rgba(0,0,0,0.45);
}

.code .constant-token,
.code .enumitem-token,
.code .bitmaskvalue-token {
  color: ${tokenBlueColor};
}

.code .constant-token:hover,
.code .enumitem-token:hover,
.code .bitmaskvalue-token:hover {
  color: ${tokenHoverBlueColor};
}

.code .subtype-token,
.code .instantiatetype-token {
  color: ${tokenVioletColor};
}

.code .subtype-token:hover,
.code .instantiatetype-token:hover {
  color: ${tokenHoverVioletColor};
}

.code .structure-token,
.code .choice-token,
.code .union-token,
.code .sqltable-token,
.code .sqldatabase-token,
.code .service-token,
.code .pubsub-token {
  color: ${tokenBrownColor};
}

.code .structure-token:hover,
.code .choice-token:hover,
.code .union-token:hover,
.code .sqltable-token:hover,
.code .sqldatabase-token:hover,
.code .service-token:hover,
.code .pubsub-token:hover {
  color: ${tokenHoverBrownColor};
}

.code .enum-token,
.code .bitmask-token {
  color: ${tokenGreenColor};
}

.code .enum-token:hover,
.code .bitmask-token:hover {
  color: ${tokenHoverGreenColor};
}

.rules .anchor,
.rules .anchor-md {
  border-top: none;
  padding-top: revert;
  scroll-margin-top: ${headerHeight}rem;
}

.rules p:last-child {
  margin-bottom: 0;
}

#main dd {
  padding-left: 1em;
}

#main code {
  color: inherit; /* don't use pink color which is default from Bootstrap */
}

.deprecated {
  color: ${deprecatedColor};
  text-decoration: line-through;
}

.svg {
  width: 100%;
  overflow-x: auto;
}
