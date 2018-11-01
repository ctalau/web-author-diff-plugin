package com.oxygenxml.webapp.diff;

import java.util.List;

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.AuthorOperationWithResult;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;

/**
 * Show diff highlights in the right editor.
 * 
 * @author ctalau
 */
@WebappRestSafe
public class ShowDiffHighlightsOperation extends AuthorOperationWithResult {

  public String getDescription() {
    return "";
  }

  public String doOperation(AuthorDocumentModel rightDocumentModel, ArgumentsMap arg1)
      throws IllegalArgumentException, AuthorOperationException {
    AuthorAccess authorAccess = rightDocumentModel.getAuthorAccess();
    AuthorEditorAccess rightEditorAccess = authorAccess.getEditorAccess();
    
    @SuppressWarnings("unchecked")
    List<IntervalDescriptor> intervals = (List<IntervalDescriptor>) rightEditorAccess
        .getEditingContext().getAttribute("intervals");
    Util.renderHighlights(intervals, rightDocumentModel);
    
    return "";
  }

  public ArgumentDescriptor[] getArguments() {
    return new ArgumentDescriptor[0];
  }

}
