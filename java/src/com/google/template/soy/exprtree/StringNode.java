/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.template.soy.exprtree;

import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.StringType;


/**
 * Node representing a string value.
 *
 * <p> Important: Do not use outside of Soy code (treat as superpackage-private).
 *
 */
public final class StringNode extends AbstractPrimitiveNode {


  /** The string value. */
  private final String value;


  /**
   * @param value The string value.
   */
  public StringNode(String value) {
    this.value = value;
  }


  /**
   * Copy constructor.
   * @param orig The node to copy.
   */
  private StringNode(StringNode orig) {
    super(orig);
    this.value = orig.value;
  }


  @Override public Kind getKind() {
    return Kind.STRING_NODE;
  }


  @Override public SoyType getType() {
    return StringType.getInstance();
  }


  /** Returns the string value. */
  public String getValue() {
    return value;
  }


  /**
   * Equivalent to {@code toSourceString(false)}.
   *
   * {@inheritDoc}
   */
  @Override public String toSourceString() {
    return toSourceString(false);
  }


  /**
   * Builds a Soy string literal for this string value (including the surrounding single quotes).
   *
   * @param escapeToAscii Whether to escape non-ASCII characters as Unicode hex escapes
   *     (backslash + 'u' + 4 hex digits).
   * @return A Soy string literal for this string value (including the surrounding single quotes).
   */
  public String toSourceString(boolean escapeToAscii) {
    return BaseUtils.escapeToSoyString(value, escapeToAscii);
  }


  @Override public StringNode clone() {
    return new StringNode(this);
  }

}
