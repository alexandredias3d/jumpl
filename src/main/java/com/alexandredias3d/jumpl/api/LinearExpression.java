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

package com.alexandredias3d.jumpl.api;

/**
 * Wraps a concrete linear expression type from solvers. Provides common functionality of linear
 * expressions seen in different solvers.
 *
 * @author Alexandre H. T. Dias
 */
public interface LinearExpression extends Wrappable {

  /**
   * Adds a linear expression to the current linear expression.
   *
   * @param expr linear expression to add
   */
  void add(LinearExpression expr);

  /**
   * Adds a constant term to the current linear expression.
   *
   * @param constant constant term added (or subtracted) on the linear expression
   */
  void addConstant(double constant);

  /**
   * Adds a term (coefficient times variable) to the current linear expression.
   *
   * @param coefficient coefficient of the variable on the linear expression
   * @param variable    variable to be added to the linear expression
   */
  void addTerm(double coefficient, Variable variable);

  /**
   * Gets the constant term from the linear expression.
   *
   * @return value of constant
   */
  double getConstant();

}
