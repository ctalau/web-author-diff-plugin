Web Author Diff Plugin
======================

To open two documents in a side-by-side diff view, open `./plugin-resources/web-author-diff-plugin/diff.html` with the following params:

1. `base` - the common part of the URLs of the two editors
2. `left` - the URL suffix for the left editor
3. `right` - the URL suffix for the right editor
4. Other parameters will be passed to both editors

Dev notes
---------

Example diff URL:

```
http://localhost:8080/oxygen-xml-web-author/plugin-resources/web-author-diff-plugin/diff.html?base=webdav-http%3A%2F%2Flocalhost%3A8080%2Foxygen-xml-web-author%2Fplugins-dispatcher%2Fwebdav-server%2Fdita%2Fflowers%2Ftopics%2Fflowers%2F&left=gardenia.dita&right=lilac.dita&tags-mode=full-tags
```

Implementation notes:
- adding color highlights is an undo-able operation.
- the editor acquire the lock despite being read-only.
- text-based processing finds differences between attributes - we cannot add processing-instructions in attributes.