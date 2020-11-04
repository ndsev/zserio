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

#left_panel {
  top: 4rem;
  height: calc(100vh - 4rem);
  overflow: hidden;
  position: sticky;
  border-right: 1px solid rgba(0,0,0,0.1);
  padding-right: 0px;
}

#search_form {
  padding: 1rem 15px;
  margin-left: -15px;
  /* bottom border across the whole column thanks to the padding-left/rigth and margin-left */
  border-bottom: 1px solid rgba(0,0,0,0.1);
}

#search {
  height: calc(1.5rem + 0.75rem);
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5rem;
}

#search:focus {
  border-color: rgba(0, 0, 0, 0.2);
  box-shadow: 0 0 0 3px rgba(0,0,0,0.1);
}

#symbol_overview {
  padding-top: 1rem;
  overflow-y: auto;
  /* 2rem = search_form paddings, 1.5rem + 0.75rem = search height, 0.5rem = padding bottom */
  height: calc(100% - 2rem - 1.5rem - 0.75rem - 0.5rem);
  flex-wrap: nowrap;
}

#symbol_overview .nav-link {
  padding-top: 0.125rem;
  padding-right: 1.5rem;
  padding-bottom: 0.125rem;
  padding-left: 0.5rem;
}

#toc {
  top: 4rem;
  height: calc(100vh - 4rem);
  position: sticky;
  font-size: 0.875rem;
  overflow-x: hidden;
  overflow-y: hidden; /* scrolls automatically using javascript */
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
