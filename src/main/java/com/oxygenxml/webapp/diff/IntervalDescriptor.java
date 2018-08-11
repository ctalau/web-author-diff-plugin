package com.oxygenxml.webapp.diff;

/**
 * Descriptor for a diff interval.
 * @author ctalau
 *
 */
public class IntervalDescriptor {
  public enum Type {
    ADDED,
    REMOVED, 
    CHANGED
  }
  
  final int start;
  final int end;
  final Type type;

  public IntervalDescriptor(int start, int end, Type type) {
    this.start = start;
    this.end = end;
    this.type = type;
  }
  
}
