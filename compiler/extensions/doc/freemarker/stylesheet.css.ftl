<#-- constant values -->
<#assign headerPaddingTop=0.5/>
<#assign headerPaddingBottom=0.5/>
<#assign headerHeight=3.5/>
<#assign headerLogoHeight=1/>
<#assign anchorPaddingTop=0.25/>
<#assign symbolAnchorPaddingTop=0.75/>
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
<#assign deprecatedColor="gray"/>
<#assign headerNavColor="white"/>
<#assign headerNavHoverColor="rgb(255,255,255,0.65)"/>
<#-- stylesheet -->
/* Hack due to the sticky header. */
.anchor,
.anchor-md {
  scroll-margin-top: ${headerHeight}rem;
  padding-top: ${anchorPaddingTop}rem;
}
h1.anchor { /* package heading */
  padding-top: 0; /* prevent scrolling above the anchor scroll */
  scroll-margin-top: ${headerHeight}rem;
}
h2.anchor { /* symbols in package */
  border-top: 3px solid ${borderColorLight};
  /* set-up scroll to hide the border-top (horizontal line) */
  scroll-margin-top: ${headerHeight - symbolAnchorPaddingTop}rem;
  padding-top: ${symbolAnchorPaddingTop}rem;
}

h1,
h2,
h3 {
  margin-bottom: 1rem;
}

#header {
  height: ${headerHeight}rem;
  padding-top: ${headerPaddingTop}rem;
  padding-bottom: ${headerPaddingBottom}rem;
}

#header .logo {
  height: ${headerLogoHeight}rem;
}

#header .logo:hover {
  color: ${headerNavHoverColor};
}

#header .navbar-nav {
  color: ${headerNavColor};
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

.nav-link, a {
  color: ${linkColor};
}

.nav-link:hover, a:hover {
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
  width: max-content;
  margin-bottom: 0.5rem;
}

.code td {
  padding-right: 0.4rem;
  white-space: nowrap;
}

.code td:last-child {
  width: 100%; /* make last columns the only ones which are epanding to preserve whole table compact */
}

.code td.indent {
  padding-left: ${codeIndent}rem;
}

.code td.indent.empty {
  padding-left: 0;
  width: ${codeIndent}rem;
  min-width: ${codeIndent}rem;
}

#main dd {
  padding-left: 1em;
}

.deprecated {
  color: ${deprecatedColor};
}

#main del {
  color: ${deprecatedColor};
}

.svg {
  width: 100%;
  overflow-x: auto;
}
