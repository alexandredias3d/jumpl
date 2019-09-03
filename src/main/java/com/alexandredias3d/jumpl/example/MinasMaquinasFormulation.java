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
 * Minas Máquinas problem formulation agnostic of the underlying solver. In this com.alexandredias3d.jumpl.example, {@link
 * #preOptimization()} is being used to instantiate MinasMaquinasInput.
 *
 * @author Alexandre H. T. Dias
 * @see <a href="https://mayronmoreira.github.io/prog-mat/01/2/subsection/5/lot-sizing">Minas
 * Máquinas - Mayron Moreira</a>
 */
public class MinasMaquinasFormulation extends BaseFormulation {

  private Variable[][] produced;
  private Variable[][] stored;

  private MinasMaquinasInput input;

  public MinasMaquinasFormulation(Model model) {
    super(model);
  }

  public static void main(String[] args) {
    ModelFactory.solveIn(MinasMaquinasFormulation.class, Solver.ALL);
  }

  @Override
  protected void preOptimization() {
    input = new MinasMaquinasInput();
  }

  @Override
  protected void putVariables() {
    produced = new Variable[input.numberOfItems][this.input.numberOfPeriods];
    stored = new Variable[input.numberOfItems][this.input.numberOfPeriods];

    for (int i = 0; i < input.numberOfItems; i++) {
      for (int j = 0; j < input.numberOfPeriods; j++) {
        produced[i][j] = model.addRealVariable(0, Double.POSITIVE_INFINITY);
        stored[i][j] = model.addRealVariable(0, Double.POSITIVE_INFINITY);
      }
    }
  }

  private void putStockBalanceConstraints() {
    for (int i = 0; i < input.numberOfItems; i++) {
      LinearExpression expr = model.createEmptyLinearExpression();
      expr.addTerm(1, produced[i][0]);
      expr.addTerm(-1, stored[i][0]);
      model.addEqualConstraint(expr, input.itemDemandPerPeriod[i][0]);
    }

    for (int i = 0; i < input.numberOfItems; i++) {
      for (int j = 1; j < input.numberOfPeriods; j++) {
        LinearExpression expr = model.createEmptyLinearExpression();
        expr.addTerm(1, stored[i][j - 1]);
        expr.addTerm(1, produced[i][j]);
        expr.addTerm(-1, stored[i][j]);
        model.addEqualConstraint(expr, input.itemDemandPerPeriod[i][j]);
      }
    }

  }

  private void putResourceCapacityConstraints() {
    for (int j = 0; j < input.numberOfPeriods; j++) {
      LinearExpression expr = model.createEmptyLinearExpression();
      for (int i = 0; i < input.numberOfItems; i++) {
        expr.addTerm(input.resourcesPerItem[i], produced[i][j]);
      }
      model.addLessEqualConstraint(expr, input.resourcesPerPeriod[j]);
    }
  }

  @Override
  protected void putConstraints() {
    putStockBalanceConstraints();
    putResourceCapacityConstraints();
  }

  @Override
  protected void putObjectiveFunction() {
    LinearExpression objectiveFunction = model.createEmptyLinearExpression();
    for (int i = 0; i < input.numberOfItems; i++) {
      for (int j = 0; j < input.numberOfPeriods; j++) {
        objectiveFunction.addTerm(input.productionCostPerPeriod[i][j], produced[i][j]);
        objectiveFunction.addTerm(input.storageCostPerPeriod[i][j], stored[i][j]);
      }
    }

    model.setObjectiveFunctionMinimize(objectiveFunction);
  }

}
