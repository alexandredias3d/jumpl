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

import com.alexandredias3d.jumpl.api.BaseVariable;
import com.alexandredias3d.jumpl.api.Guardable;
import com.alexandredias3d.jumpl.api.Model;
import ilog.concert.IloNumVar;

/**
 * Concrete implementation of a wrapper for the CPLEX Variable, called IloNumVar.
 *
 * @author Alexandre H. T. Dias
 */
public class CplexVariable extends BaseVariable<IloNumVar> implements Guardable {

  /**
   * Package-private constructor using an already created IloNumVar.
   *
   * @param variable reference to a variable returned by {@link Model}
   */
  CplexVariable(IloNumVar variable) {
    this.variable = variable;
  }

  @Override
  public String getName() {
    return variable.getName();
  }

  @Override
  public void setName(String name) {
    variable.setName(name);
  }

  @Override
  public double getLowerBound() {
    return guard(() -> this.variable.getLB());
  }

  @Override
  public void setLowerBound(double lowerBound) {
    guard(() -> {
      variable.setLB(lowerBound);
      return null;
    });
  }

  @Override
  public double getUpperBound() {
    return guard(() -> this.variable.getUB());
  }

  @Override
  public void setUpperBound(double upperBound) {
    guard(() -> {
      variable.setUB(upperBound);
      return null;
    });
  }

}
