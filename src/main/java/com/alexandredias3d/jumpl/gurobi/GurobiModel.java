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

import com.alexandredias3d.jumpl.api.BaseModel;
import com.alexandredias3d.jumpl.api.DoubleParameter;
import com.alexandredias3d.jumpl.api.Guardable;
import com.alexandredias3d.jumpl.api.LinearExpression;
import com.alexandredias3d.jumpl.api.Variable;
import gurobi.GRB;
import gurobi.GRB.DoubleAttr;
import gurobi.GRB.DoubleParam;
import gurobi.GRB.IntParam;
import gurobi.GRB.StringParam;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;

/**
 * Concrete implementation of a wrapper for the Gurobi Model, called GRBModel.
 *
 * @author Alexandre H. T. Dias
 */
public class GurobiModel extends BaseModel<GRBModel> implements Guardable {

  private final GRBEnv env;

  public GurobiModel() {
    this("", "jumpl-gurobi.log", "jumpl-gurobi.lp", true);
  }

  public GurobiModel(boolean logToConsole) {
    this("", "jumpl-gurobi.log", "jumpl-gurobi.lp", logToConsole);
  }

  public GurobiModel(String logFile, String outputFile) {
    this("", logFile, outputFile, true);
  }

  public GurobiModel(String logFile, String outputFile, boolean logToConsole) {
    this("", logFile, outputFile, logToConsole);
  }

  public GurobiModel(String inputFile, String logFile, String outputFile, boolean logToConsole) {
    env = guard(() -> {
      GRBEnv e = new GRBEnv(true);
      e.set(StringParam.LogFile, logFile);
      e.set(IntParam.LogToConsole, (logToConsole) ? 1 : 0);
      e.start();
      return e;
    });

    model = guard(() -> (inputFile.isEmpty()) ? new GRBModel(env) : new GRBModel(env, inputFile));
    this.outputFile = outputFile;
  }

  @Override
  public void setObjectiveFunctionMinimize(LinearExpression expr) {
    guard(() -> {
      model.setObjective(((GurobiLinearExpression) expr).getWrappee(), GRB.MINIMIZE);
      return null;
    });
  }

  @Override
  public void setObjectiveFunctionMaximize(LinearExpression expr) {
    guard(() -> {
      model.setObjective(((GurobiLinearExpression) expr).getWrappee(), GRB.MAXIMIZE);
      return null;
    });
  }

  @Override
  public Variable addRealVariable(double lowerBound, double upperBound, String name) {
    return new GurobiVariable(
        guard(() -> model.addVar(lowerBound, upperBound, 0.0, GRB.CONTINUOUS, name)));
  }

  @Override
  public Variable addRealVariable(double lowerBound, double upperBound) {
    return addRealVariable(lowerBound, upperBound, getVariableName());
  }

  @Override
  public Variable addIntegerVariable(double lowerBound, double upperBound, String name) {
    return new GurobiVariable(
        guard(() -> model.addVar(lowerBound, upperBound, 0.0, GRB.INTEGER, name)));
  }

  @Override
  public Variable addIntegerVariable(double lowerBound, double upperBound) {
    return addIntegerVariable(lowerBound, upperBound, getVariableName());
  }

  @Override
  public Variable addBinaryVariable(String name) {
    return new GurobiVariable(guard(() -> model.addVar(0.0, 1.0, 0.0, GRB.BINARY, name)));
  }

  @Override
  public Variable addBinaryVariable() {
    return addBinaryVariable(getVariableName());
  }

  @Override
  public void addEqualConstraint(double value, LinearExpression expr, String name) {
    guard(() -> model
        .addConstr(value, GRB.EQUAL, ((GurobiLinearExpression) expr).getWrappee(), name));
  }

  @Override
  public void addEqualConstraint(LinearExpression expr, double value, String name) {
    guard(() -> model
        .addConstr(((GurobiLinearExpression) expr).getWrappee(), GRB.EQUAL, value, name));
  }

  @Override
  public void addEqualConstraint(LinearExpression expr, Variable var, String name) {
    guard(() -> model.addConstr(((GurobiLinearExpression) expr).getWrappee(), GRB.EQUAL,
        ((GurobiVariable) var).getWrappee(), name));
  }

  @Override
  public void addEqualConstraint(Variable var, LinearExpression expr, String name) {
    guard(() -> model.addConstr(((GurobiVariable) var).getWrappee(), GRB.EQUAL,
        ((GurobiLinearExpression) expr).getWrappee(), name));
  }

  @Override
  public void addEqualConstraint(LinearExpression expr1, LinearExpression expr2, String name) {
    guard(() -> model.addConstr(((GurobiLinearExpression) expr1).getWrappee(), GRB.EQUAL,
        ((GurobiLinearExpression) expr2).getWrappee(), name));
  }

  @Override
  public void addEqualConstraint(double value, Variable var, String name) {
    guard(
        () -> model.addConstr(value, GRB.EQUAL, ((GurobiVariable) var).getWrappee(), name));
  }

  @Override
  public void addEqualConstraint(Variable var, double value, String name) {
    guard(
        () -> model.addConstr(((GurobiVariable) var).getWrappee(), GRB.EQUAL, value, name));
  }

  @Override
  public void addEqualConstraint(Variable var1, Variable var2, String name) {
    guard(() -> model.addConstr(((GurobiVariable) var1).getWrappee(), GRB.EQUAL,
        ((GurobiVariable) var2).getWrappee(), name));
  }

  @Override
  public void addEqualConstraint(double value, LinearExpression expr) {
    addEqualConstraint(value, expr, getConstraintName());
  }

  @Override
  public void addEqualConstraint(LinearExpression expr, double value) {
    addEqualConstraint(expr, value, getConstraintName());
  }

  @Override
  public void addEqualConstraint(LinearExpression expr, Variable var) {
    addEqualConstraint(expr, var, getConstraintName());
  }

  @Override
  public void addEqualConstraint(Variable var, LinearExpression expr) {
    addEqualConstraint(var, expr, getConstraintName());
  }

  @Override
  public void addEqualConstraint(LinearExpression expr1, LinearExpression expr2) {
    addEqualConstraint(expr1, expr2, getConstraintName());
  }

  @Override
  public void addEqualConstraint(double value, Variable var) {
    addEqualConstraint(value, var, getConstraintName());
  }

  @Override
  public void addEqualConstraint(Variable var, double value) {
    addEqualConstraint(var, value, getConstraintName());
  }

  @Override
  public void addEqualConstraint(Variable var1, Variable var2) {
    addEqualConstraint(var1, var2, getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(double value, LinearExpression expr, String name) {
    guard(() -> model
        .addConstr(value, GRB.GREATER_EQUAL, ((GurobiLinearExpression) expr).getWrappee(), name));
  }

  @Override
  public void addGreaterEqualConstraint(LinearExpression expr, double value, String name) {
    guard(() -> model
        .addConstr(((GurobiLinearExpression) expr).getWrappee(), GRB.GREATER_EQUAL, value, name));
  }

  @Override
  public void addGreaterEqualConstraint(LinearExpression expr, Variable var, String name) {
    guard(
        () -> model.addConstr(((GurobiLinearExpression) expr).getWrappee(), GRB.GREATER_EQUAL,
            ((GurobiVariable) var).getWrappee(), name));
  }

  @Override
  public void addGreaterEqualConstraint(Variable var, LinearExpression expr, String name) {
    guard(
        () -> model.addConstr(((GurobiVariable) var).getWrappee(), GRB.GREATER_EQUAL,
            ((GurobiLinearExpression) expr).getWrappee(), name));
  }

  @Override
  public void addGreaterEqualConstraint(LinearExpression expr1, LinearExpression expr2,
      String name) {
    guard(() -> model
        .addConstr(((GurobiLinearExpression) expr1).getWrappee(), GRB.GREATER_EQUAL,
            ((GurobiLinearExpression) expr2).getWrappee(), name));
  }

  @Override
  public void addGreaterEqualConstraint(double value, Variable var, String name) {
    guard(() -> model
        .addConstr(value, GRB.GREATER_EQUAL, ((GurobiVariable) var).getWrappee(), name));
  }

  @Override
  public void addGreaterEqualConstraint(Variable var, double value, String name) {
    guard(() -> model
        .addConstr(((GurobiVariable) var).getWrappee(), GRB.GREATER_EQUAL, value, name));
  }

  @Override
  public void addGreaterEqualConstraint(Variable var1, Variable var2, String name) {
    guard(
        () -> model.addConstr(((GurobiVariable) var1).getWrappee(), GRB.GREATER_EQUAL,
            ((GurobiVariable) var2).getWrappee(), name));
  }

  @Override
  public void addGreaterEqualConstraint(double value, LinearExpression expr) {
    addGreaterEqualConstraint(value, expr, getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(LinearExpression expr, double value) {
    addGreaterEqualConstraint(expr, value, getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(LinearExpression expr, Variable var) {
    addGreaterEqualConstraint(expr, var, getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(Variable var, LinearExpression expr) {
    addGreaterEqualConstraint(var, expr, getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(LinearExpression expr1, LinearExpression expr2) {
    addGreaterEqualConstraint(expr1, expr2, getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(double value, Variable var) {
    addGreaterEqualConstraint(value, var, getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(Variable var, double value) {
    addGreaterEqualConstraint(var, value, getConstraintName());
  }

  @Override
  public void addGreaterEqualConstraint(Variable var1, Variable var2) {
    addGreaterEqualConstraint(var1, var2, getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(double value, LinearExpression expr, String name) {
    guard(() -> model
        .addConstr(value, GRB.LESS_EQUAL, ((GurobiLinearExpression) expr).getWrappee(), name));
  }

  @Override
  public void addLessEqualConstraint(LinearExpression expr, double value, String name) {
    guard(() -> model
        .addConstr(((GurobiLinearExpression) expr).getWrappee(), GRB.LESS_EQUAL, value, name));
  }

  @Override
  public void addLessEqualConstraint(LinearExpression expr, Variable var, String name) {
    guard(
        () -> model.addConstr(((GurobiLinearExpression) expr).getWrappee(), GRB.LESS_EQUAL,
            ((GurobiVariable) var).getWrappee(), name));
  }

  @Override
  public void addLessEqualConstraint(Variable var, LinearExpression expr, String name) {
    guard(
        () -> model.addConstr(((GurobiVariable) var).getWrappee(), GRB.LESS_EQUAL,
            ((GurobiLinearExpression) expr).getWrappee(), name));
  }

  @Override
  public void addLessEqualConstraint(LinearExpression expr1, LinearExpression expr2, String name) {
    guard(
        () -> model.addConstr(((GurobiLinearExpression) expr1).getWrappee(), GRB.LESS_EQUAL,
            ((GurobiLinearExpression) expr2).getWrappee(), name));
  }

  @Override
  public void addLessEqualConstraint(double value, Variable var, String name) {
    guard(() -> model
        .addConstr(value, GRB.LESS_EQUAL, ((GurobiVariable) var).getWrappee(), name));
  }

  @Override
  public void addLessEqualConstraint(Variable var, double value, String name) {
    guard(() -> model
        .addConstr(((GurobiVariable) var).getWrappee(), GRB.LESS_EQUAL, value, name));
  }

  @Override
  public void addLessEqualConstraint(Variable var1, Variable var2, String name) {
    guard(() -> model.addConstr(((GurobiVariable) var1).getWrappee(), GRB.LESS_EQUAL,
        ((GurobiVariable) var2).getWrappee(), name));
  }

  @Override
  public void addLessEqualConstraint(double value, LinearExpression expr) {
    addLessEqualConstraint(value, expr, getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(LinearExpression expr, double value) {
    addLessEqualConstraint(expr, value, getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(LinearExpression expr,
      Variable var) {
    addLessEqualConstraint(expr, var, getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(Variable var, LinearExpression expr) {
    addLessEqualConstraint(var, expr, getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(LinearExpression expr1,
      LinearExpression expr2) {
    addLessEqualConstraint(expr1, expr2, getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(double value, Variable var) {
    addLessEqualConstraint(value, var, getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(Variable var, double value) {
    addLessEqualConstraint(var, value, getConstraintName());
  }

  @Override
  public void addLessEqualConstraint(Variable var1, Variable var2) {
    addLessEqualConstraint(var1, var2, getConstraintName());
  }

  @Override
  public LinearExpression createEmptyLinearExpression() {
    return new GurobiLinearExpression(new GRBLinExpr());
  }

  @Override
  public void solve() {
    guard(() -> {
      model.write(outputFile);
      var startTime = System.currentTimeMillis();
      model.optimize();
      solvingTime = (System.currentTimeMillis() - startTime) / 1000.0;
      return null;
    });
  }

  @Override
  public boolean dispose() {
    try {
      model.dispose();
      env.dispose();
      return true;
    } catch (GRBException e) {
      e.printStackTrace();
      System.err.println(getClass().getName() + ": error while disposing the model.");
      return false;
    }
  }

  @Override
  public void setParameter(DoubleParameter parameter, double value) {
    guard(() -> {
      switch (parameter) {
        case ABSOLUTE_MIP_GAP:
          model.set(DoubleParam.MIPGapAbs, value);
          break;

        case RELATIVE_MIP_GAP:
          model.set(DoubleParam.MIPGap, value);
          break;

        case TIME_LIMIT:
          model.set(DoubleParam.TimeLimit, value);
          break;
      }
      return null;
    });
  }

  @Override
  public double getObjectiveFunctionValue() {
    return guard(() -> model.get(DoubleAttr.ObjVal));
  }

  @Override
  public double getVariableValue(Variable variable) {
    return guard(() -> ((GurobiVariable) variable).getWrappee().get(DoubleAttr.X));
  }

}
