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

import com.alexandredias3d.jumpl.api.BaseModel;
import com.alexandredias3d.jumpl.api.DoubleParameter;
import com.alexandredias3d.jumpl.api.Guardable;
import com.alexandredias3d.jumpl.api.LinearExpression;
import com.alexandredias3d.jumpl.api.Variable;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Param;
import ilog.cplex.IloCplex.Param.MIP.Tolerances;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Concrete implementation of a wrapper for the CPLEX Model, called IloCplex.
 *
 * @author Alexandre H. T. Dias
 */
public class CplexModel extends BaseModel<IloCplex> implements Guardable {

  public CplexModel() {
    this("", "jumpl-cplex.log", "jumpl-cplex.lp", true);
  }

  public CplexModel(boolean logToConsole) {
    this("", "jumpl-cplex.log", "jumpl-cplex.lp", logToConsole);
  }

  public CplexModel(String logFile, String outputFile) {
    this("", logFile, outputFile, true);
  }

  public CplexModel(String logFile, String outputFile, boolean logToConsole) {
    this("", logFile, outputFile, logToConsole);
  }

  public CplexModel(String inputFile, String logFile, String outputFile, boolean logToConsole) {
    try {
      model = this.guard(IloCplex::new);
      if (!logToConsole) {
        model.setOut(null);
      }
      model.setOut(new FileOutputStream(logFile));
      if (!inputFile.isEmpty()) {
        model.importModel(inputFile);
      }
    } catch (FileNotFoundException | IloException e) {
      e.printStackTrace();
      System.err.println(getClass().getName() + ": error while creating the model.");
    }
    this.outputFile = outputFile;
  }

  @Override
  public void setObjectiveFunctionMinimize(LinearExpression expr) {
    guard(() -> this.model.addMinimize(((CplexLinearExpression) expr).getWrappee()));
  }

  @Override
  public void setObjectiveFunctionMaximize(LinearExpression expr) {
    guard(() -> this.model.addMaximize(((CplexLinearExpression) expr).getWrappee()));
  }

  @Override
  public Variable addRealVariable(double lowerBound, double upperBound, String name) {
    return new CplexVariable(guard(() -> this.model.numVar(lowerBound, upperBound, name)));
  }

  @Override
  public Variable addRealVariable(double lowerBound, double upperBound) {
    return addRealVariable(lowerBound, upperBound, this.getVariableName());
  }

  @Override
  public Variable addIntegerVariable(double lowerBound, double upperBound, String name) {
    return new CplexVariable(
        guard(() -> this.model.intVar((int) lowerBound, (int) upperBound, name)));
  }

  @Override
  public Variable addIntegerVariable(double lowerBound, double upperBound) {
    return addIntegerVariable(lowerBound, upperBound, this.getVariableName());
  }

  @Override
  public Variable addBinaryVariable(String name) {
    return new CplexVariable(guard(() -> this.model.boolVar(name)));
  }

  @Override
  public Variable addBinaryVariable() {
    return addBinaryVariable(this.getVariableName());
  }

  @Override
  public void addEqualConstraint(double value, LinearExpression expr, String name) {
    guard(() -> this.model.addEq(value, expr.getWrappee(), name));
  }

  @Override
  public void addEqualConstraint(LinearExpression expr, double value, String name) {
    guard(() -> this.model.addEq(expr.getWrappee(), value, name));
  }

  @Override
  public void addEqualConstraint(LinearExpression expr, Variable var, String name) {
    guard(() -> this.model.addEq(expr.getWrappee(), var.getWrappee(), name));
  }

  @Override
  public void addEqualConstraint(Variable var, LinearExpression expr, String name) {
    guard(() -> this.model.addEq(var.getWrappee(), expr.getWrappee(), name));
  }

  @Override
  public void addEqualConstraint(LinearExpression expr1, LinearExpression expr2, String name) {
    guard(() -> this.model.addEq(expr1.getWrappee(), expr2.getWrappee(), name));
  }

  @Override
  public void addEqualConstraint(double value, Variable var, String name) {
    guard(() -> this.model.addEq(value, var.getWrappee(), name));
  }

  @Override
  public void addEqualConstraint(Variable var, double value, String name) {
    guard(() -> this.model.addEq(var.getWrappee(), value, name));
  }

  @Override
  public void addEqualConstraint(Variable var1, Variable var2, String name) {
    guard(() -> this.model.addEq(var1.getWrappee(), var2.getWrappee(), name));
  }

  @Override
  public void addEqualConstraint(double value, LinearExpression expr) {
    addEqualConstraint(value, expr, this.getConstraintName());
  }

  @Override
  public void addEqualConstraint(LinearExpression expr, double value) {
    addEqualConstraint(expr, value, this.getConstraintName());
  }

  @Override
  public void addEqualConstraint(LinearExpression expr, Variable var) {
    addEqualConstraint(expr, var, this.getConstraintName());
  }

  @Override
  public void addEqualConstraint(Variable var, LinearExpression expr) {
    addEqualConstraint(var, expr, this.getConstraintName());
  }

  @Override
  public void addEqualConstraint(LinearExpression expr1, LinearExpression expr2) {
    addEqualConstraint(expr1, expr2, this.getConstraintName());
  }

  @Override
  public void addEqualConstraint(double value, Variable var) {
    addEqualConstraint(value, var, this.getConstraintName());
  }

  @Override
  public void addEqualConstraint(Variable var, double value) {
    addEqualConstraint(var, value, this.getConstraintName());
  }

  @Override
  public void addEqualConstraint(Variable var1, Variable var2) {
    addEqualConstraint(var1, var2, this.getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(double value, LinearExpression expr, String name) {
    guard(() -> this.model.addGe(value, expr.getWrappee(), name));
  }

  @Override
  public void addGreaterEqualConstraint(LinearExpression expr, double value, String name) {
    guard(() -> this.model.addGe(expr.getWrappee(), value, name));
  }

  @Override
  public void addGreaterEqualConstraint(LinearExpression expr, Variable var, String name) {
    guard(() -> this.model.addGe(expr.getWrappee(), var.getWrappee(), name));
  }

  @Override
  public void addGreaterEqualConstraint(Variable var, LinearExpression expr, String name) {
    guard(() -> this.model.addGe(var.getWrappee(), expr.getWrappee(), name));
  }

  @Override
  public void addGreaterEqualConstraint(LinearExpression expr1, LinearExpression expr2,
      String name) {
    guard(() -> this.model.addGe(expr1.getWrappee(), expr2.getWrappee(), name));
  }

  @Override
  public void addGreaterEqualConstraint(double value, Variable var, String name) {
    guard(() -> this.model.addGe(value, var.getWrappee(), name));
  }

  @Override
  public void addGreaterEqualConstraint(Variable var, double value, String name) {
    guard(() -> this.model.addGe(var.getWrappee(), value, name));
  }

  @Override
  public void addGreaterEqualConstraint(Variable var1, Variable var2, String name) {
    guard(() -> this.model.addGe(var1.getWrappee(), var2.getWrappee(), name));
  }

  @Override
  public void addGreaterEqualConstraint(double value, LinearExpression expr) {
    addGreaterEqualConstraint(value, expr, this.getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(LinearExpression expr, double value) {
    addGreaterEqualConstraint(expr, value, this.getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(LinearExpression expr, Variable var) {
    addGreaterEqualConstraint(expr, var, this.getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(Variable var, LinearExpression expr) {
    addGreaterEqualConstraint(var, expr, this.getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(LinearExpression expr1, LinearExpression expr2) {
    addGreaterEqualConstraint(expr1, expr2, this.getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(double value, Variable var) {
    addGreaterEqualConstraint(value, var, this.getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(Variable var, double value) {
    addGreaterEqualConstraint(var, value, this.getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(Variable var1, Variable var2) {
    addGreaterEqualConstraint(var1, var2, this.getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(double value, LinearExpression expr, String name) {
    guard(() -> this.model.addLe(value, expr.getWrappee(), name));
  }

  @Override
  public void addLessEqualConstraint(LinearExpression expr, double value, String name) {
    guard(() -> this.model.addLe(expr.getWrappee(), value, name));

  }

  @Override
  public void addLessEqualConstraint(LinearExpression expr, Variable var, String name) {
    guard(() -> this.model.addLe(expr.getWrappee(), var.getWrappee(), name));
  }

  @Override
  public void addLessEqualConstraint(Variable var, LinearExpression expr, String name) {
    guard(() -> this.model.addLe(var.getWrappee(), expr.getWrappee(), name));
  }

  @Override
  public void addLessEqualConstraint(LinearExpression expr1, LinearExpression expr2, String name) {
    guard(() -> this.model.addLe(expr1.getWrappee(), expr2.getWrappee(), name));
  }

  @Override
  public void addLessEqualConstraint(double value, Variable var, String name) {
    guard(() -> this.model.addLe(value, var.getWrappee(), name));
  }

  @Override
  public void addLessEqualConstraint(Variable var, double value, String name) {
    guard(() -> this.model.addLe(var.getWrappee(), value, name));
  }

  @Override
  public void addLessEqualConstraint(Variable var1, Variable var2, String name) {
    guard(() -> this.model.addLe(var1.getWrappee(), var2.getWrappee(), name));
  }

  @Override
  public void addLessEqualConstraint(double value, LinearExpression expr) {
    addLessEqualConstraint(value, expr, this.getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(LinearExpression expr, double value) {
    addLessEqualConstraint(expr, value, this.getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(LinearExpression expr, Variable var) {
    addLessEqualConstraint(expr, var, this.getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(Variable var, LinearExpression expr) {
    addLessEqualConstraint(var, expr, this.getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(LinearExpression expr1, LinearExpression expr2) {
    addLessEqualConstraint(expr1, expr2, this.getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(double value, Variable var) {
    addLessEqualConstraint(value, var, this.getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(Variable var, double value) {
    addLessEqualConstraint(var, value, this.getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(Variable var1, Variable var2) {
    addLessEqualConstraint(var1, var2, this.getConstraintName());
  }

  @Override
  public LinearExpression createEmptyLinearExpression() {
    return guard(() -> new CplexLinearExpression(this.model.linearNumExpr()));
  }

  @Override
  public void solve() {
    guard(() -> {
      model.exportModel("jumpl-cplex.lp");
      var startTime = System.currentTimeMillis();
      model.solve();
      solvingTime = (System.currentTimeMillis() - startTime) / 1000.0;
      return null;
    });
  }

  @Override
  public boolean dispose() {
    model.end();
    return true;
  }

  @Override
  public void setParameter(DoubleParameter parameter, double value) {
    guard(() -> {
      switch (parameter) {
        case ABSOLUTE_MIP_GAP:
          model.setParam(Tolerances.AbsMIPGap, value);
          break;

        case RELATIVE_MIP_GAP:
          model.setParam(Tolerances.MIPGap, value);
          break;

        case TIME_LIMIT:
          model.setParam(Param.TimeLimit, value);
          break;
      }
      return null;
    });
  }

  @Override
  public double getObjectiveFunctionValue() {
    return guard(() -> model.getObjValue());
  }

  @Override
  public double getVariableValue(Variable variable) {
    return guard(() -> model.getValue(((CplexVariable) variable).getWrappee()));
  }

  @Override
  public double[] getVariablesValues(Variable[] variables) {
    return Arrays.stream(variables).mapToDouble(this::getVariableValue).toArray();
  }

  @Override
  public int getStatus() {
    var status = -1;
    var statusCplex = guard(() -> model.getStatus());

    Field field;

    try {
     field = statusCplex.getClass().getDeclaredField("_status");
     field.setAccessible(true);
     status = (int) field.get(statusCplex);
     field.setAccessible(false);
    } catch (Exception ex) {

    }

    return status;
  }

}