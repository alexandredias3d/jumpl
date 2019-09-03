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
 * Wraps a concrete model type from solvers. Provides common functionality of models seen in
 * different solvers.
 *
 * @author Alexandre H. T. Dias
 */
public interface Model extends Wrappable {

  /**
   * Sets the objective function to minimize the given expression.
   *
   * @param expr linear expression which the goal is to minimize it
   */
  void setObjectiveFunctionMinimize(LinearExpression expr);

  /**
   * Sets the objective function to maximize the given expression.
   *
   * @param expr linear expression which the goal is to maximize it
   */
  void setObjectiveFunctionMaximize(LinearExpression expr);

  /**
   * Adds a real (continuous) variable.
   *
   * @param lowerBound minimum real value that can be assigned to a variable
   * @param upperBound maximum real value that can be assigned to a variable
   * @param name       variable name
   * @return a wrapper of the variable with type U
   */
  Variable addRealVariable(double lowerBound, double upperBound, String name);

  /**
   * {@code name} defaults to "x" + an auto-incremented integer.
   *
   * @see Model#addRealVariable(double, double, String)
   */
  Variable addRealVariable(double lowerBound, double upperBound);

  /**
   * Adds an integer (discrete) variable.
   *
   * @param lowerBound minimum integer value that can be assigned to a variable
   * @param upperBound maximum integer value that can be assigned to a variable
   * @param name       variable name
   * @return a wrapper of the variable with type U
   */
  Variable addIntegerVariable(double lowerBound, double upperBound, String name);

  /**
   * {@code name} defaults to "x" + an auto-incremented integer.
   *
   * @see Model#addIntegerVariable(double, double, String)
   */
  Variable addIntegerVariable(double lowerBound, double upperBound);

  /**
   * Adds a binary (0-1) variable.
   *
   * @param name variable name
   * @return a wrapper of the variable with type U
   */
  Variable addBinaryVariable(String name);

  /**
   * {@code name} defaults to "x" + an auto-incremented integer.
   *
   * @see Model#addBinaryVariable(String)
   */
  Variable addBinaryVariable();

  /**
   * Adds a constraint of type value = expr.
   *
   * @param value constant double value
   * @param expr  linear expression
   * @param name  constraint name
   */
  void addEqualConstraint(double value, LinearExpression expr, String name);

  /**
   * Adds a constraint of type expr = value.
   *
   * @param expr  linear expression
   * @param value constant double value
   * @param name  constraint name
   */
  void addEqualConstraint(LinearExpression expr, double value, String name);

  /**
   * Adds a constraint of type expr = var.
   *
   * @param expr linear expression
   * @param var  variable
   * @param name constraint name
   */
  void addEqualConstraint(LinearExpression expr, Variable var, String name);

  /**
   * Adds a constraint of type var = expr.
   *
   * @param var  variable
   * @param expr linear expression
   * @param name constraint name
   */
  void addEqualConstraint(Variable var, LinearExpression expr, String name);

  /**
   * Adds a constraint of type expr1 = expr2.
   *
   * @param expr1 first linear expression
   * @param expr2 second linear expression
   * @param name  constraint name
   */
  void addEqualConstraint(LinearExpression expr1, LinearExpression expr2, String name);

  /**
   * Adds a constraint of type value = var.
   *
   * @param value constant double value
   * @param var   variable
   * @param name  constraint name
   */
  void addEqualConstraint(double value, Variable var, String name);

  /**
   * Adds a constraint of type var = value.
   *
   * @param var   variable
   * @param value constant double value
   * @param name  constraint name
   */
  void addEqualConstraint(Variable var, double value, String name);

  /**
   * Adds a constraint of type var1 = var2.
   *
   * @param var1 first variable
   * @param var2 second variable
   * @param name constraint name
   */
  void addEqualConstraint(Variable var1, Variable var2, String name);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addEqualConstraint(double, LinearExpression, String)
   */
  void addEqualConstraint(double value, LinearExpression expr);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addEqualConstraint(LinearExpression, double, String)
   */
  void addEqualConstraint(LinearExpression expr, double value);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addEqualConstraint(LinearExpression, Variable, String)
   */
  void addEqualConstraint(LinearExpression expr, Variable var);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addEqualConstraint(Variable, LinearExpression, String)
   */
  void addEqualConstraint(Variable var, LinearExpression expr);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addEqualConstraint(LinearExpression, LinearExpression, String)
   */
  void addEqualConstraint(LinearExpression expr1, LinearExpression expr2);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addEqualConstraint(double, Variable, String)
   */
  void addEqualConstraint(double value, Variable var);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addEqualConstraint(Variable, double, String)
   */
  void addEqualConstraint(Variable var, double value);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addEqualConstraint(Variable, Variable, String)
   */
  void addEqualConstraint(Variable var1, Variable var2);

  /**
   * Adds a constraint of type value >= expr.
   *
   * @param value constant double value
   * @param expr  linear expression
   * @param name  constraint name
   */
  void addGreaterEqualConstraint(double value, LinearExpression expr, String name);

  /**
   * Adds a constraint of type expr >= value.
   *
   * @param expr  linear expression
   * @param value constant double value
   * @param name  constraint name
   */
  void addGreaterEqualConstraint(LinearExpression expr, double value, String name);

  /**
   * Adds a constraint of type expr >= var.
   *
   * @param expr linear expression
   * @param var  variable
   * @param name constraint name
   */
  void addGreaterEqualConstraint(LinearExpression expr, Variable var, String name);

  /**
   * Adds a constraint of type var >= expr.
   *
   * @param var  variable
   * @param expr linear expression
   * @param name constraint name
   */
  void addGreaterEqualConstraint(Variable var, LinearExpression expr, String name);

  /**
   * Adds a constraint of type expr1 >= expr2.
   *
   * @param expr1 first linear expression
   * @param expr2 second linear expression
   * @param name  constraint name
   */
  void addGreaterEqualConstraint(LinearExpression expr1, LinearExpression expr2, String name);

  /**
   * Adds a constraint of type value >= var.
   *
   * @param value constant double value
   * @param var   variable
   * @param name  constraint name
   */
  void addGreaterEqualConstraint(double value, Variable var, String name);

  /**
   * Adds a constraint of type var >= value.
   *
   * @param var   variable
   * @param value constant double value
   * @param name  constraint name
   */
  void addGreaterEqualConstraint(Variable var, double value, String name);

  /**
   * Adds a constraint of type var1 >= var2.
   *
   * @param var1 first variable
   * @param var2 second variable
   * @param name constraint name
   */
  void addGreaterEqualConstraint(Variable var1, Variable var2, String name);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addGreaterEqualConstraint(double, LinearExpression, String)
   */
  void addGreaterEqualConstraint(double value, LinearExpression expr);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addGreaterEqualConstraint(LinearExpression, double, String)
   */
  void addGreaterEqualConstraint(LinearExpression expr, double value);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addGreaterEqualConstraint(LinearExpression, Variable, String)
   */
  void addGreaterEqualConstraint(LinearExpression expr, Variable var);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addGreaterEqualConstraint(Variable, LinearExpression, String)
   */
  void addGreaterEqualConstraint(Variable var, LinearExpression expr);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addGreaterEqualConstraint(LinearExpression, LinearExpression, String)
   */
  void addGreaterEqualConstraint(LinearExpression expr1, LinearExpression expr2);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addGreaterEqualConstraint(double, Variable, String)
   */
  void addGreaterEqualConstraint(double value, Variable var);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addGreaterEqualConstraint(Variable, double, String)
   */
  void addGreaterEqualConstraint(Variable var, double value);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addGreaterEqualConstraint(Variable, Variable, String)
   */
  void addGreaterEqualConstraint(Variable var1, Variable var2);

  /**
   * Adds a constraint of type value <= expr.
   *
   * @param value constant double value
   * @param expr  linear expression
   * @param name  constraint name
   */
  void addLessEqualConstraint(double value, LinearExpression expr, String name);

  /**
   * Adds a constraint of type expr <= value.
   *
   * @param expr  linear expression
   * @param value constant double value
   * @param name  constraint name
   */
  void addLessEqualConstraint(LinearExpression expr, double value, String name);

  /**
   * Adds a constraint of type expr <= var.
   *
   * @param expr linear expression
   * @param var  variable
   * @param name constraint name
   */
  void addLessEqualConstraint(LinearExpression expr, Variable var, String name);

  /**
   * Adds a constraint of type var <= expr.
   *
   * @param var  variable
   * @param expr linear expression
   * @param name constraint name
   */
  void addLessEqualConstraint(Variable var, LinearExpression expr, String name);

  /**
   * Adds a constraint of type expr1 <= expr2.
   *
   * @param expr1 first linear expression
   * @param expr2 second linear expression
   * @param name  constraint name
   */
  void addLessEqualConstraint(LinearExpression expr1, LinearExpression expr2, String name);

  /**
   * Adds a constraint of type value <= expr.
   *
   * @param value constant double value
   * @param var   variable
   * @param name  constraint name
   */
  void addLessEqualConstraint(double value, Variable var, String name);

  /**
   * Adds a constraint of type var <= value.
   *
   * @param var   variable
   * @param value constant double value
   * @param name  constraint name
   */
  void addLessEqualConstraint(Variable var, double value, String name);

  /**
   * Adds a constraint of type var1 <= var2.
   *
   * @param var1 first linear expression
   * @param var2 second linear expression
   * @param name constraint name
   */
  void addLessEqualConstraint(Variable var1, Variable var2, String name);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addLessEqualConstraint(double, LinearExpression, String)
   */
  void addLessEqualConstraint(double value, LinearExpression expr);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addLessEqualConstraint(LinearExpression, double, String)
   */
  void addLessEqualConstraint(LinearExpression expr, double value);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addLessEqualConstraint(LinearExpression, Variable, String)
   */
  void addLessEqualConstraint(LinearExpression expr, Variable var);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addLessEqualConstraint(Variable, LinearExpression, String)
   */
  void addLessEqualConstraint(Variable var, LinearExpression expr);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addLessEqualConstraint(LinearExpression, LinearExpression, String)
   */
  void addLessEqualConstraint(LinearExpression expr1, LinearExpression expr2);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addLessEqualConstraint(double, Variable, String)
   */
  void addLessEqualConstraint(double value, Variable var);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addLessEqualConstraint(Variable, double, String)
   */
  void addLessEqualConstraint(Variable var, double value);

  /**
   * {@code name} defaults to "c" + an auto-incremented integer.
   *
   * @see Model#addLessEqualConstraint(Variable, Variable, String)
   */
  void addLessEqualConstraint(Variable var1, Variable var2);

  /**
   * Creates an empty linear expression.
   *
   * @return empty linear expression
   */
  LinearExpression createEmptyLinearExpression();

  /**
   * Solves the model.
   */
  void solve();

  /**
   * Releases all resources used by the model.
   *
   * @return true if no errors occurred during disposal, false otherwise
   */
  boolean dispose();

  void setAbsoluteMIPGap(double gap);

  void setRelativeMIPGap(double gap);

  void setTimeLimit(double time);

}