/* Hack due to the sticky header. */
.anchor {
  scroll-margin-top: 3.75rem;
}
h2.anchor, h3.anchor {
  padding-top: 0.75rem;
  scroll-margin-top: 3rem;
  border-top: 3px solid rgba(0,0,0,0.1);
}

#header {
  line-height: 2rem;
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
}

#symbol_overview {
  top: 4rem;
  height: calc(100vh - 4rem);
  overflow-y: auto;
  position: sticky;
  border-right: 1px solid rgba(0,0,0,0.1);
}

#symbol_overview .nav-link {
  padding-top: 0.125rem;
  padding-right: 1.5rem;
  padding-bottom: 0.125rem;
  padding-left: 0.5rem;
}

#overview_nav {
  padding-top: 1rem;
}

#search_form {
  padding: 1rem 15px;
  margin-left: -15px;
  margin-right: -15px;
  /* bottom border across the whole column thanks to the padding-left/rigth and margin-left/right */
  border-bottom: 1px solid rgba(0,0,0,0.1);
}

#search:focus {
  border-color: rgba(0, 0, 0, 0.2);
  box-shadow: 0 0 0 3px rgba(0,0,0,0.1);
}

#toc {
  top: 4rem;
  height: 100%;
  position: sticky;
  font-size: 0.875rem;
  overflow-x: hidden;
  overflow-y: auto;
}

#toc .nav-link {
  padding-top: 0.125rem;
  padding-right: 1.5rem;
  padding-bottom: 0.125rem;
  padding-left: 0.5rem;
}

#toc .nav .nav-link.active {
  border-left: 1px solid rgba(0,0,0,0.2);
}

h1 {
  margin-top: 0;
  margin-bottom: 1rem;
}

h2, h3 {
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.nav .nav {
  margin-left: 1rem;
}

.nav .nav-link.active {
  font-weight: bold;
}

.nav-link, a {
  color: rgba(0,0,0,.65);
}

.nav-link:hover, a:hover {
  color: rgba(0,0,0,.85);
  text-decoration: none;
}

.code {
  font-family: monospace;
  border-radius: 0.5rem;
  background-color: rgba(0,0,0,0.05);
  padding: 0.5rem;
  width: max-content;
  margin-top: 1rem;
}

.code td {
  padding-right: 1ex;
}

td.indent {
  padding-left: 5.5ex;
}

td.indent.empty {
  padding-left: 0;
  width: 5.5ex;
}

.deprecated {
  color: gray;
}

del {
  color: gray;
}

.svg {
  max-width: 100%;
}
