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

package com.alexandredias3d.jumpl.cplex;

import com.alexandredias3d.jumpl.api.BaseLinearExpression;
import com.alexandredias3d.jumpl.api.Guardable;
import com.alexandredias3d.jumpl.api.LinearExpression;
import com.alexandredias3d.jumpl.api.Model;
import com.alexandredias3d.jumpl.api.Variable;
import ilog.concert.IloLinearNumExpr;

/**
 * Concrete implementation of a wrapper for the CPLEX LinearExpression, called IloLinearNumExpr. In
 * CPLEX, a linear expression cannot be created without a model.
 *
 * @author Alexandre H. T. Dias
 */
public class CplexLinearExpression extends BaseLinearExpression<IloLinearNumExpr> implements
    Guardable {

  /**
   * Package-private constructor using an already created IloLinearNumExpr.
   *
   * @param expression reference to an expression created by {@link Model}
   */
  CplexLinearExpression(final IloLinearNumExpr expression) {
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
    guard(() -> {
      expression.setConstant(expression.getConstant() + constant);
      return null;
    });
  }

  @Override
  public void addTerm(double coefficient, Variable variable) {
    guard(() -> {
      expression.addTerm(coefficient, variable.getWrappee());
      return null;
    });
  }

  @Override
  public double getConstant() {
    return guard(() -> expression.getConstant());
  }

}
