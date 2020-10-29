/* Hack due to the sticky header. */
main .anchor::before {
  display: block;
  height: 3rem;
  margin-top: -3rem;
  content: "";
}

main h1.anchor::before, main h2.anchor::before, main h3.anchor::before {
  display: block;
  height: 3.5rem;
  margin-top: -3.5rem;
  content: "";
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
  border-right: 1px solid rgba(0,0,0,.1);
}

#toc {
  top: 4rem;
  height: 100%;
  position: sticky;
  font-size: 0.6em;
  overflow-x: hidden;
  overflow-y: auto;
}

#toc .nav .nav-link.active {
  border-left: 1px solid rgba(0,0,0,.2);
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
}

.docuCode {
  background-color: #FFFFFF;
  empty-cells: show;
  border-width: 1px;
  border-style: dotted;
  border-spacing: 0px;
  border-collapse: collapse;
  padding: 0.5em;
  font-family: monospace;
}

.docuCode tr td {
  border-width: 0px;
  border-style: none;
  border-collapse: collapse;
  padding-right: 1ex;
}

.docuCode table tr.codeMember td {
  padding-top: .25em;
  padding-bottom: .25em;
}

.tabIndent {
  padding-left: 5.5ex;
}

.deprecated {
  color: gray;
}
