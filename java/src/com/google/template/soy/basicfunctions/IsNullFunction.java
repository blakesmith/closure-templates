/*
 * Copyright 2016 Google Inc.
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

package com.google.template.soy.basicfunctions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.UndefinedData;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.jbcsrc.restricted.BytecodeUtils;
import com.google.template.soy.jbcsrc.restricted.CodeBuilder;
import com.google.template.soy.jbcsrc.restricted.Expression;
import com.google.template.soy.jbcsrc.restricted.JbcSrcPluginContext;
import com.google.template.soy.jbcsrc.restricted.SoyExpression;
import com.google.template.soy.jbcsrc.restricted.SoyJbcSrcFunction;
import com.google.template.soy.jssrc.dsl.SoyJsPluginUtils;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.pysrc.restricted.PyExpr;
import com.google.template.soy.pysrc.restricted.PyExprUtils;
import com.google.template.soy.pysrc.restricted.SoyPySrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.shared.restricted.SoyPureFunction;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

/** Soy function that checks whether its argument is null. */
@Singleton
@SoyPureFunction
final class IsNullFunction
    implements SoyJavaFunction, SoyJsSrcFunction, SoyPySrcFunction, SoyJbcSrcFunction {

  @Inject
  IsNullFunction() {}

  @Override
  public SoyValue computeForJava(List<SoyValue> args) {
    SoyValue arg = args.get(0);
    return BooleanData.forValue(arg instanceof UndefinedData || arg instanceof NullData);
  }

  @Override
  public JsExpr computeForJsSrc(List<JsExpr> args) {
    JsExpr arg = args.get(0);
    JsExpr nullJsExpr = new JsExpr("null", Integer.MAX_VALUE);
    return SoyJsPluginUtils.genJsExprUsingSoySyntax(
        Operator.EQUAL, ImmutableList.of(arg, nullJsExpr));
  }

  @Override
  public PyExpr computeForPySrc(List<PyExpr> args) {
    return PyExprUtils.genPyNullCheck(args.get(0));
  }

  @Override
  public String getName() {
    return "isNull";
  }

  @Override
  public Set<Integer> getValidArgsSizes() {
    return ImmutableSet.of(1);
  }

  @Override
  public SoyExpression computeForJbcSrc(JbcSrcPluginContext context, List<SoyExpression> args) {
    final SoyExpression arg = args.get(0);
    if (BytecodeUtils.isPrimitive(arg.resultType())) {
      return SoyExpression.FALSE;
    }
    // This is what javac generates for 'someObject == null'
    return SoyExpression.forBool(
        new Expression(Type.BOOLEAN_TYPE, arg.features()) {
          @Override
          protected void doGen(CodeBuilder adapter) {
            arg.gen(adapter);
            Label isNull = new Label();
            adapter.ifNull(isNull);
            // non-null
            adapter.pushBoolean(false);
            Label end = new Label();
            adapter.goTo(end);
            adapter.mark(isNull);
            adapter.pushBoolean(true);
            adapter.mark(end);
          }
        });
  }
}
