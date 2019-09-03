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

import com.alexandredias3d.jumpl.api.BaseVariable;
import com.alexandredias3d.jumpl.api.Guardable;
import com.alexandredias3d.jumpl.api.Model;
import gurobi.GRB;
import gurobi.GRBVar;

/**
 * Concrete implementation of a wrapper for the Gurobi Variable, called GRBVar.
 *
 * @author Alexandre H. T. Dias
 */
public class GurobiVariable extends BaseVariable<GRBVar> implements Guardable {

  /**
   * Package-private constructor using an already created GRBVar.
   *
   * @param variable reference to a variable returned by {@link Model}
   */
  GurobiVariable(final GRBVar variable) {
    this.variable = variable;
  }

  @Override
  public String getName() {
    return guard(() -> variable.get(GRB.StringAttr.VarName));
  }

  @Override
  public void setName(String name) {
    guard(() -> {
      variable.set(GRB.StringAttr.VarName, name);
      return null;
    });
  }

  @Override
  public double getLowerBound() {
    return guard(() -> variable.get(GRB.DoubleAttr.LB));
  }

  @Override
  public void setLowerBound(double lowerBound) {
    guard(() -> {
      variable.set(GRB.DoubleAttr.LB, lowerBound);
      return null;
    });
  }

  @Override
  public double getUpperBound() {
    return guard(() -> variable.get(GRB.DoubleAttr.UB));
  }

  @Override
  public void setUpperBound(double upperBound) {
    guard(() -> {
      variable.set(GRB.DoubleAttr.UB, upperBound);
      return null;
    });
  }

}
