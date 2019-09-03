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

package com.alexandredias3d.jumpl.example;

import com.alexandredias3d.jumpl.api.BaseFormulation;
import com.alexandredias3d.jumpl.api.LinearExpression;
import com.alexandredias3d.jumpl.api.Model;
import com.alexandredias3d.jumpl.api.ModelFactory;
import com.alexandredias3d.jumpl.api.Solver;
import com.alexandredias3d.jumpl.api.Variable;

/**
 * Lego Plus problem formulation agnostic of the underlying solver.
 *
 * @author Alexandre H. T. Dias
 * @see <a href="https://mayronmoreira.github.io/prog-mat/01/2/subsection/1/lego">LegoPlus - Mayron
 * Moreira</a>
 */
public class LegoPlusFormulation extends BaseFormulation {

  private Variable x;
  private Variable y;

  public LegoPlusFormulation(final Model model) {
    super(model);
  }

  public static void main(String[] args) {
    ModelFactory.solveIn(LegoPlusFormulation.class, Solver.ALL);
  }

  @Override
  protected void putVariables() {
    x = this.model.addRealVariable(0, Double.POSITIVE_INFINITY, "x");
    y = this.model.addRealVariable(0, Double.POSITIVE_INFINITY, "y");
  }

  @Override
  protected void putConstraints() {
    LinearExpression expression1 = model.createEmptyLinearExpression();
    expression1.addTerm(2, x);
    expression1.addTerm(2, y);
    model.addLessEqualConstraint(expression1, 8);

    LinearExpression expression2 = model.createEmptyLinearExpression();
    expression2.addTerm(2, x);
    expression2.addTerm(1, y);
    model.addLessEqualConstraint(expression1, 6);
  }

  @Override
  protected void putObjectiveFunction() {
    LinearExpression objectiveFunction = model.createEmptyLinearExpression();
    objectiveFunction.addTerm(16, x);
    objectiveFunction.addTerm(10, y);
    model.setObjectiveFunctionMaximize(objectiveFunction);
  }

}
