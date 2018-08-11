package com.oxygenxml.webapp.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.oxygenxml.webapp.diff.IntervalDescriptor.Type;

import ro.sync.diff.api.DiffContentTypes;
import ro.sync.diff.api.DiffException;
import ro.sync.diff.api.DiffOptions;
import ro.sync.diff.api.Difference;
import ro.sync.diff.api.DifferencePerformer;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Creates the diff highlights for the left editor and adds passes them
 * as a session attribute on the right editor.
 * 
 * @author ctalau
 */
public class CreateDiffHighlights implements AuthorOperation{
  
  public String getDescription() {
    return "";
  }

  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    DifferencePerformer diffPerformer;
    try {
      diffPerformer = PluginWorkspaceProvider.getPluginWorkspace().getCompareUtilAccess().createDiffPerformer();
    } catch (DiffException e) {
      throw new AuthorOperationException("Cannot perform diff", e);
    }
    
    AuthorEditorAccess leftEditorAccess = authorAccess.getEditorAccess();
    @SuppressWarnings("unchecked")
    AuthorEditorAccess rightEditorAccess = ((AtomicReference<AuthorEditorAccess>) leftEditorAccess
        .getEditingContext().getAttribute(DiffEditorLinker.DIFF_PAIR_EDITOR_ATTR)).get();
    
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
      
      for (Difference difference : diffs) {
        int leftStartOffset = this.toAuthorOffset(difference.getLeftIntervalStart());
        int leftEndOffset = this.toAuthorOffset(difference.getLeftIntervalStart());
        
        int rightStartOffset = this.toAuthorOffset(difference.getRightIntervalStart());
        int rightEndOffset = this.toAuthorOffset(difference.getRightIntervalStart());
        
        IntervalDescriptor.Type leftType = Type.CHANGED;
        IntervalDescriptor.Type rightType = Type.CHANGED;
        if (leftStartOffset == leftEndOffset) {
          leftType = Type.REMOVED;
          rightType = Type.ADDED;
        } else if (rightStartOffset == rightEndOffset) {
          leftType = Type.ADDED;
          rightType = Type.REMOVED;
        }
        
        left.add(new IntervalDescriptor(leftStartOffset, leftEndOffset, leftType));
        right.add(new IntervalDescriptor(rightStartOffset, rightEndOffset, rightType));
      }
      
      Util.renderHighlights(left, leftEditorAccess);
      rightEditorAccess.getEditingContext().setAttribute("intervals", right);
    } catch (DiffException e) {
      new AuthorOperationException("Cannot perform diff", e);
    }

  }

  private int toAuthorOffset(int leftIntervalStart) {
    // TODO: convert text offset to author offset.
    return 0;
  }

  public ArgumentDescriptor[] getArguments() {
    return new ArgumentDescriptor[0];
  }

}
