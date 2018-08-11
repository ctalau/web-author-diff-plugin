package com.oxygenxml.webapp.diff;

import java.util.List;

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;

/**
 * Show diff highlights in the right editor.
 * 
 * @author ctalau
 */
public class ShowDiffHighlights implements AuthorOperation {

  public String getDescription() {
    return "";
  }

  public void doOperation(AuthorAccess authorAccess, ArgumentsMap arg1)
      throws IllegalArgumentException, AuthorOperationException {
    AuthorEditorAccess rightEditorAccess = authorAccess.getEditorAccess();
    @SuppressWarnings("unchecked")
    List<IntervalDescriptor> intervals = (List<IntervalDescriptor>) rightEditorAccess
        .getEditingContext().getAttribute("intervals");
    Util.renderHighlights(intervals, rightEditorAccess);
  }

  public ArgumentDescriptor[] getArguments() {
    return new ArgumentDescriptor[0];
  }

}
