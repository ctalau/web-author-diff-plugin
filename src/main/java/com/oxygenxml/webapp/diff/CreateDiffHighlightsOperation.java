package com.oxygenxml.webapp.diff;

import java.util.ArrayList;
import java.util.List;

import com.oxygenxml.webapp.diff.IntervalDescriptor.Type;

import ro.sync.diff.api.DiffContentTypes;
import ro.sync.diff.api.DiffException;
import ro.sync.diff.api.DiffOptions;
import ro.sync.diff.api.Difference;
import ro.sync.diff.api.DifferencePerformer;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.AuthorOperationWithResult;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Creates the diff highlights for the left editor and adds passes them
 * as a session attribute on the right editor.
 * 
 * @author ctalau
 */
@WebappRestSafe
public class CreateDiffHighlightsOperation extends AuthorOperationWithResult {
  
  public String getDescription() {
    return "";
  }
  @Override
  public String doOperation(AuthorDocumentModel leftModel, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    AuthorAccess leftAuthorAccess = leftModel.getAuthorAccess();
    DifferencePerformer diffPerformer;
    try {
      diffPerformer = PluginWorkspaceProvider.getPluginWorkspace().getCompareUtilAccess().createDiffPerformer();
    } catch (DiffException e) {
      throw new AuthorOperationException("Cannot perform diff", e);
    }
    
    AuthorEditorAccess leftEditorAccess = leftAuthorAccess.getEditorAccess();
    AuthorDocumentModel rightDocumentModel = (AuthorDocumentModel) leftEditorAccess
        .getEditingContext().getAttribute(DiffEditorLinker.DIFF_PAIR_EDITOR_ATTR);
    AuthorEditorAccess rightEditorAccess = rightDocumentModel.getAuthorAccess().getEditorAccess();; 
    
    List<Difference> diffs;
    try {
      diffs = diffPerformer.performDiff(
          leftEditorAccess.createContentReader(), 
          rightEditorAccess.createContentReader(),
          leftEditorAccess.getEditorLocation().toExternalForm(), 
          rightEditorAccess.getEditorLocation().toExternalForm(),
          DiffContentTypes.XML_CONTENT_TYPE, 
          new DiffOptions(), 
          null);
      
      List<IntervalDescriptor> left = new ArrayList<IntervalDescriptor>();
      List<IntervalDescriptor> right = new ArrayList<IntervalDescriptor>();
      
      for (int i = 0; i < diffs.size(); i++) {
        Difference difference = diffs.get(i);
        int leftStartOffset = difference.getLeftIntervalStart();
        int leftEndOffset = difference.getLeftIntervalEnd();
        
        int rightStartOffset = difference.getRightIntervalStart();
        int rightEndOffset = difference.getRightIntervalEnd();
        
        IntervalDescriptor.Type leftType = Type.CHANGED;
        IntervalDescriptor.Type rightType = Type.CHANGED;
        if (leftStartOffset == leftEndOffset) {
          leftType = Type.REMOVED;
          rightType = Type.ADDED;
        } else if (rightStartOffset == rightEndOffset) {
          leftType = Type.ADDED;
          rightType = Type.REMOVED;
        }
        
        left.add(new IntervalDescriptor(i, leftStartOffset, leftEndOffset, leftType));
        right.add(new IntervalDescriptor(i, rightStartOffset, rightEndOffset, rightType));
      }
      
      Util.renderHighlights(left, leftModel);
      rightEditorAccess.getEditingContext().setAttribute("intervals", right);
    } catch (DiffException e) {
      new AuthorOperationException("Cannot perform diff", e);
    }
    
    return "";
  }

  public ArgumentDescriptor[] getArguments() {
    return new ArgumentDescriptor[0];
  }


}
