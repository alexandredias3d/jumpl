/*
 * Copyright 2019 Alexandre H. T. Dias
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.alexandredias3d.jumpl.gurobi;

import com.alexandredias3d.jumpl.api.BaseLinearExpression;
import com.alexandredias3d.jumpl.api.Guardable;
import com.alexandredias3d.jumpl.api.LinearExpression;
import com.alexandredias3d.jumpl.api.Model;
import com.alexandredias3d.jumpl.api.Variable;
import gurobi.GRBLinExpr;

/**
 * Concrete implementation of a wrapper for the Gurobi Linear Expression, called GRBLinExpr.
 *
 * @author Alexandre H. T. Dias
 */
public class GurobiLinearExpression extends BaseLinearExpression<GRBLinExpr> implements Guardable {

  /**
   * Package-private constructor using an already created GRBLinExpr.
   *
   * @param expression reference to an expression created by {@link Model}
   */
  GurobiLinearExpression(final GRBLinExpr expression) {
    this.expression = expression;
  }

  @Override
  public void add(LinearExpression expression) {
    guard(() -> {
      this.expression.add(expression.getWrappee());
      return null;
    });
  }

  @Override
  public void addConstant(double constant) {
    expression.addConstant(constant);
  }

  @Override
  public void addTerm(double coefficient, Variable variable) {
    expression.addTerm(coefficient, variable.getWrappee());
  }

  @Override
  public double getConstant() {
    return expression.getConstant();
  }

}
