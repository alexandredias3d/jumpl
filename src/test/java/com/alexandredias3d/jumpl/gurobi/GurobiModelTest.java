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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.alexandredias3d.jumpl.api.LinearExpression;
import com.alexandredias3d.jumpl.api.Model;
import com.alexandredias3d.jumpl.api.Variable;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for the GurobiModel. It uses all of the wrappers and test if all functions are functional
 * in some way. Only a thin layer is being tested here. Other tests can be implemented by iterating
 * in each constraint that was added and checking if they are the same in the wrapper and wrappee.
 */
public class GurobiModelTest {

  private static final int VAR_NUM = 2;
  private static final int EXPR_NUM = 2;
  private static final double EPSILON = 0.00000001;
  // Attributes
  private final Map<Character, Double> lbs = Map.ofEntries(
      Map.entry(GRB.CONTINUOUS, 0.0),
      Map.entry(GRB.INTEGER, 0.0),
      Map.entry(GRB.BINARY, 0.0)
  );

  private final Map<Character, Double> ubs = Map.ofEntries(
      Map.entry(GRB.CONTINUOUS, GRB.INFINITY),
      Map.entry(GRB.INTEGER, GRB.INFINITY),
      Map.entry(GRB.BINARY, 1.0)
  );

  private final double[] objFunCoeffs = {1.0, 1.0};

  private final double[][] exprCoeffs = {{1.0, 2.0},
      {2.0, 1.0}};

  private final double constantValue = 10.0;

  private final String[] varNames = {"var1", "var2"};
  private final String constrBaseNameFormat = "constr%d";
  // Underlying types
  private List<GRBVar> vars;
  private List<GRBLinExpr> exprs;
  private GRBLinExpr objExpr;
  private int currentConstraintIndex;
  // Wrappers
  private List<Variable> varWrappers;
  private List<LinearExpression> exprWrappers;
  private LinearExpression objExprWrapper;
  private Model modelWrapper;
  // Wrappee reference
  private GRBModel modelWrappee;

  @BeforeMethod(alwaysRun = true)
  public void setUpGurobiModel() {
    this.modelWrapper = new GurobiModel();
    this.modelWrappee = ((GurobiModel) this.modelWrapper).getWrappee();
  }

  @BeforeMethod(onlyForGroups = {"setObjectiveFunctionGroup", "addConstraintsGroup"})
  public void setUpVariableAndExpressionWrappeesList() {
    this.vars = new ArrayList<>();
    this.exprs = new ArrayList<>();
  }

  @BeforeMethod(onlyForGroups = {"setObjectiveFunctionGroup", "addVariablesGroup",
      "addConstraintsGroup", "solveProblemGroup"})
  public void setUpVariableAndExpressionWrappersList() {
    this.varWrappers = new ArrayList<>();
    this.exprWrappers = new ArrayList<>();
  }

  @BeforeMethod(onlyForGroups = {"setObjectiveFunctionGroup", "addConstraintsGroup"})
  public void setUpVariablesAndVariableWrappers() {
    try {
      // Creates 2 real variables
      for (int i = 0; i < GurobiModelTest.VAR_NUM; i++) {
        this.vars.add(this.modelWrappee.addVar(this.lbs.get(GRB.CONTINUOUS),
            this.ubs.get(GRB.CONTINUOUS), 0, GRB.CONTINUOUS,
            this.varNames[i]));
        this.varWrappers.add(new GurobiVariable(this.vars.get(i)));
      }
    } catch (GRBException e) {
      e.printStackTrace();
      System.err
          .println(this.getClass().getName() + ": error in setUpVariablesAndVariableWrappers.");
    }
  }

  @BeforeMethod(onlyForGroups = {"setObjectiveFunctionGroup"},
      dependsOnMethods = {"setUpVariableAndExpressionWrappeesList",
          "setUpVariablesAndVariableWrappers"})
  public void setUpObjectiveExpressionAndWrapper() {
    try {
      // Creates the objective expression 1x1 + 1x2
      this.objExpr = new GRBLinExpr();
      for (int j = 0; j < GurobiModelTest.VAR_NUM; j++) {
        this.objExpr.addTerm(this.objFunCoeffs[j], this.vars.get(j));
      }
      this.objExprWrapper = new GurobiLinearExpression(this.objExpr);
      this.modelWrappee.update();
    } catch (GRBException e) {
      e.printStackTrace();
      System.err.println(
          this.getClass().getName() + ": error in setUpObjectiveExpressionAndWrapper method.");
    }
  }

  @BeforeMethod(onlyForGroups = {"addConstraintsGroup"}, dependsOnMethods = {
      "setUpVariableAndExpressionWrappeesList",
      "setUpVariableAndExpressionWrappersList", "setUpVariablesAndVariableWrappers"})
  public void setUpExpressionsAndExpressionWrappers() {
    this.currentConstraintIndex = 0;
    try {
            /*
             Creates 2 linear expressions:
              (1) 1x1 + 2x2
              (2) 2x1 + 1x2
            */
      for (int i = 0; i < GurobiModelTest.EXPR_NUM; i++) {
        this.exprs.add(new GRBLinExpr());
        for (int j = 0; j < GurobiModelTest.VAR_NUM; j++) {
          this.exprs.get(i).addTerm(this.exprCoeffs[i][j], this.vars.get(j));
        }
        this.exprWrappers.add(new GurobiLinearExpression(this.exprs.get(i)));
      }
      this.modelWrappee.update();
    } catch (GRBException e) {
      e.printStackTrace();
      System.err.println(
          this.getClass().getName() + ": error in setUpExpressionsAndExpressionWrappers method.");
    }
  }

  @AfterMethod(alwaysRun = true)
  public void tearDown() {
    this.vars = null;
    this.exprs = null;
    this.varWrappers = null;
    this.exprWrappers = null;
    this.modelWrapper.dispose();
  }

  @Test(groups = {"setObjectiveFunctionGroup"})
  public void testSetObjectiveFunctionMinimize() {
    try {
      this.modelWrapper.setObjectiveFunctionMinimize(this.objExprWrapper);
      this.modelWrappee.update();
      assertEquals(this.modelWrappee.get(GRB.IntAttr.ModelSense), GRB.MINIMIZE);
    } catch (GRBException e) {
      e.printStackTrace();
      System.err.println(
          this.getClass().getName() + ": error in testSetObjectiveFunctionMinimize method.");
    }
  }

  @Test(groups = {"setObjectiveFunctionGroup"})
  public void testSetObjectiveFunctionMaximize() {
    try {
      this.modelWrapper.setObjectiveFunctionMaximize(this.objExprWrapper);
      this.modelWrappee.update();
      assertEquals(this.modelWrappee.get(GRB.IntAttr.ModelSense), GRB.MAXIMIZE);
    } catch (GRBException e) {
      e.printStackTrace();
      System.err.println(
          this.getClass().getName() + ": error in testSetObjectiveFunctionMaximize method.");
    }
  }

  private void testAddVariablesUtil(char type, String[] names) {

    for (int i = 0; i < GurobiModelTest.VAR_NUM; i++) {
      if (names != null) {
        switch (type) {
          case GRB.CONTINUOUS:
            this.varWrappers.add(this.modelWrapper
                .addRealVariable(this.lbs.get(type), this.ubs.get(type), names[i]));
            break;

          case GRB.INTEGER:
            this.varWrappers.add(this.modelWrapper
                .addIntegerVariable(this.lbs.get(type), this.ubs.get(type), names[i]));
            break;

          case GRB.BINARY:
            this.varWrappers.add(this.modelWrapper.addBinaryVariable(names[i]));
            break;
        }
      } else {
        switch (type) {
          case GRB.CONTINUOUS:
            this.varWrappers
                .add(this.modelWrapper.addRealVariable(this.lbs.get(type), this.ubs.get(type)));
            break;

          case GRB.INTEGER:
            this.varWrappers
                .add(this.modelWrapper.addIntegerVariable(this.lbs.get(type), this.ubs.get(type)));
            break;

          case GRB.BINARY:
            this.varWrappers.add(this.modelWrapper.addBinaryVariable());
            break;
        }
      }
    }

    try {
      // Updates the model
      this.modelWrappee.update();

      int i = 0;
      for (GRBVar var : this.modelWrappee.getVars()) {

        // Asserts the lower and upper bound values
        assertEquals(var.get(GRB.DoubleAttr.LB), this.lbs.get(type), GurobiModelTest.EPSILON);
        assertEquals(var.get(GRB.DoubleAttr.UB), this.ubs.get(type), GurobiModelTest.EPSILON);

        // Asserts if varNames match the given or the default ones
        if (names != null) {
          assertEquals(var.get(GRB.StringAttr.VarName), names[i]);
        } else {
          assertEquals(var.get(GRB.StringAttr.VarName), String.format("x%d", i));
        }

        // Asserts if variable is of the correct type
        assertEquals(var.get(GRB.CharAttr.VType), type);

        i += 1;
      }

      // Asserts the amount of variables added to the model
      assertEquals(this.modelWrappee.get(GRB.IntAttr.NumVars), GurobiModelTest.VAR_NUM);
    } catch (GRBException e) {
      e.printStackTrace();
      System.err.println(
          this.getClass().getName() + " : error in testAddVariablesUtil method for type ." + type);
    }
  }

  @Test(groups = {"addVariablesGroup"})
  public void testAddRealNamedVariable() {
    this.testAddVariablesUtil(GRB.CONTINUOUS, this.varNames);
  }

  @Test(groups = {"addVariablesGroup"})
  public void testAddRealUnnamedVariable() {
    this.testAddVariablesUtil(GRB.CONTINUOUS, null);
  }

  @Test(groups = {"addVariablesGroup"})
  public void testAddIntegerNamedVariable() {
    this.testAddVariablesUtil(GRB.INTEGER, this.varNames);
  }

  @Test(groups = {"addVariablesGroup"})
  public void testAddIntegerUnnamedVariable() {
    this.testAddVariablesUtil(GRB.INTEGER, null);
  }

  @Test(groups = {"addVariablesGroup"})
  public void testAddBinaryNamedVariable() {
    this.testAddVariablesUtil(GRB.BINARY, this.varNames);
  }

  @Test(groups = {"addVariablesGroup"})
  public void testAddBinaryUnnamedVariable() {
    this.testAddVariablesUtil(GRB.BINARY, null);
  }

  private void testConstraintsType(char[] constraintTypes, char expectedType) {
    for (char constraintType : constraintTypes) {
      assertEquals(constraintType, expectedType);
    }
  }

  private void testConstraintsName(String[] constraintNames) {
    // Check named constraints
    for (int i = 0; i < constraintNames.length / 2; i++) {
      assertEquals(constraintNames[i], String.format(this.constrBaseNameFormat, i));
      assertEquals(constraintNames[i + constraintNames.length / 2], String.format("c%d", i));
    }
  }

  private String getConstraintName() {
    return String.format(this.constrBaseNameFormat, this.currentConstraintIndex++);
  }

  @Test(groups = {"addConstraintsGroup"})
  public void testAddEqualConstraint() {
    // Asserts addition of named equal constraints
    // Variable and constant
    this.modelWrapper
        .addEqualConstraint(this.constantValue, this.varWrappers.get(0), this.getConstraintName());
    this.modelWrapper
        .addEqualConstraint(this.varWrappers.get(0), this.constantValue, this.getConstraintName());
    // Linear expression and constant
    this.modelWrapper
        .addEqualConstraint(this.constantValue, this.exprWrappers.get(0), this.getConstraintName());
    this.modelWrapper
        .addEqualConstraint(this.exprWrappers.get(0), this.constantValue, this.getConstraintName());
    // Variable and linear expression
    this.modelWrapper.addEqualConstraint(this.varWrappers.get(0), this.exprWrappers.get(0),
        this.getConstraintName());
    this.modelWrapper.addEqualConstraint(this.exprWrappers.get(0), this.varWrappers.get(0),
        this.getConstraintName());
    // Variable and variable
    this.modelWrapper.addEqualConstraint(this.varWrappers.get(0), this.varWrappers.get(1),
        this.getConstraintName());
    // Linear expression and linear expression
    this.modelWrapper.addEqualConstraint(this.exprWrappers.get(0), this.exprWrappers.get(1),
        this.getConstraintName());

    // Asserts addition of unnamed equal constraints
    // Variable and constant
    this.modelWrapper.addEqualConstraint(this.constantValue, this.varWrappers.get(0));
    this.modelWrapper.addEqualConstraint(this.varWrappers.get(0), this.constantValue);
    // Linear expression and constant
    this.modelWrapper.addEqualConstraint(this.constantValue, this.exprWrappers.get(0));
    this.modelWrapper.addEqualConstraint(this.exprWrappers.get(0), this.constantValue);
    // Variable and linear expression
    this.modelWrapper.addEqualConstraint(this.varWrappers.get(0), this.exprWrappers.get(0));
    this.modelWrapper.addEqualConstraint(this.exprWrappers.get(0), this.varWrappers.get(0));
    // Variable and variable
    this.modelWrapper.addEqualConstraint(this.varWrappers.get(0), this.varWrappers.get(1));
    // Linear expression and linear expression
    this.modelWrapper.addEqualConstraint(this.exprWrappers.get(0), this.exprWrappers.get(1));

    try {
      // Get model reference
      GRBModel model = this.modelWrappee;
      model.update();

      this.testConstraintsType(model.get(GRB.CharAttr.Sense, model.getConstrs()), GRB.EQUAL);
      this.testConstraintsName(model.get(GRB.StringAttr.ConstrName, model.getConstrs()));
    } catch (GRBException e) {
      e.printStackTrace();
      System.err.println(this.getClass().getName() + ": error in testAddEqualConstraint method.");
    }
  }

  @Test(groups = {"addConstraintsGroup"})
  public void testAddGreaterEqualConstraint() {
    // Asserts addition of named equal constraints
    // Variable and constant
    this.modelWrapper.addGreaterEqualConstraint(this.constantValue, this.varWrappers.get(0),
        this.getConstraintName());
    this.modelWrapper.addGreaterEqualConstraint(this.varWrappers.get(0), this.constantValue,
        this.getConstraintName());
    // Linear expression and constant
    this.modelWrapper.addGreaterEqualConstraint(this.constantValue, this.exprWrappers.get(0),
        this.getConstraintName());
    this.modelWrapper.addGreaterEqualConstraint(this.exprWrappers.get(0), this.constantValue,
        this.getConstraintName());
    // Variable and linear expression
    this.modelWrapper.addGreaterEqualConstraint(this.varWrappers.get(0), this.exprWrappers.get(0),
        this.getConstraintName());
    this.modelWrapper.addGreaterEqualConstraint(this.exprWrappers.get(0), this.varWrappers.get(0),
        this.getConstraintName());
    // Variable and variable
    this.modelWrapper.addGreaterEqualConstraint(this.varWrappers.get(0), this.varWrappers.get(1),
        this.getConstraintName());
    // Linear expression and linear expression
    this.modelWrapper.addGreaterEqualConstraint(this.exprWrappers.get(0), this.exprWrappers.get(1),
        this.getConstraintName());

    // Asserts addition of unnamed equal constraints
    // Variable and constant
    this.modelWrapper.addGreaterEqualConstraint(this.constantValue, this.varWrappers.get(0));
    this.modelWrapper.addGreaterEqualConstraint(this.varWrappers.get(0), this.constantValue);
    // Linear expression and constant
    this.modelWrapper.addGreaterEqualConstraint(this.constantValue, this.exprWrappers.get(0));
    this.modelWrapper.addGreaterEqualConstraint(this.exprWrappers.get(0), this.constantValue);
    // Variable and linear expression
    this.modelWrapper.addGreaterEqualConstraint(this.varWrappers.get(0), this.exprWrappers.get(0));
    this.modelWrapper.addGreaterEqualConstraint(this.exprWrappers.get(0), this.varWrappers.get(0));
    // Variable and variable
    this.modelWrapper.addGreaterEqualConstraint(this.varWrappers.get(0), this.varWrappers.get(1));
    // Linear expression and linear expression
    this.modelWrapper.addGreaterEqualConstraint(this.exprWrappers.get(0), this.exprWrappers.get(1));

    try {
      // Get model reference
      GRBModel model = this.modelWrappee;
      model.update();

      this.testConstraintsType(model.get(GRB.CharAttr.Sense, model.getConstrs()),
          GRB.GREATER_EQUAL);
      this.testConstraintsName(model.get(GRB.StringAttr.ConstrName, model.getConstrs()));
    } catch (GRBException e) {
      e.printStackTrace();
      System.err
          .println(this.getClass().getName() + ": error in testAddGreaterEqualConstraint method.");
    }
  }

  @Test(groups = {"addConstraintsGroup"})
  public void testAddLessEqualConstraint() {
    // Asserts addition of named equal constraints
    // Variable and constant
    this.modelWrapper.addLessEqualConstraint(this.constantValue, this.varWrappers.get(0),
        this.getConstraintName());
    this.modelWrapper.addLessEqualConstraint(this.varWrappers.get(0), this.constantValue,
        this.getConstraintName());
    // Linear expression and constant
    this.modelWrapper.addLessEqualConstraint(this.constantValue, this.exprWrappers.get(0),
        this.getConstraintName());
    this.modelWrapper.addLessEqualConstraint(this.exprWrappers.get(0), this.constantValue,
        this.getConstraintName());
    // Variable and linear expression
    this.modelWrapper.addLessEqualConstraint(this.varWrappers.get(0), this.exprWrappers.get(0),
        this.getConstraintName());
    this.modelWrapper.addLessEqualConstraint(this.exprWrappers.get(0), this.varWrappers.get(0),
        this.getConstraintName());
    // Variable and variable
    this.modelWrapper.addLessEqualConstraint(this.varWrappers.get(0), this.varWrappers.get(1),
        this.getConstraintName());
    // Linear expression and linear expression
    this.modelWrapper.addLessEqualConstraint(this.exprWrappers.get(0), this.exprWrappers.get(1),
        this.getConstraintName());

    // Asserts addition of unnamed equal constraints
    // Variable and constant
    this.modelWrapper.addLessEqualConstraint(this.constantValue, this.varWrappers.get(0));
    this.modelWrapper.addLessEqualConstraint(this.varWrappers.get(0), this.constantValue);
    // Linear expression and constant
    this.modelWrapper.addLessEqualConstraint(this.constantValue, this.exprWrappers.get(0));
    this.modelWrapper.addLessEqualConstraint(this.exprWrappers.get(0), this.constantValue);
    // Variable and linear expression
    this.modelWrapper.addLessEqualConstraint(this.varWrappers.get(0), this.exprWrappers.get(0));
    this.modelWrapper.addLessEqualConstraint(this.exprWrappers.get(0), this.varWrappers.get(0));
    // Variable and variable
    this.modelWrapper.addLessEqualConstraint(this.varWrappers.get(0), this.varWrappers.get(1));
    // Linear expression and linear expression
    this.modelWrapper.addLessEqualConstraint(this.exprWrappers.get(0), this.exprWrappers.get(1));

    try {
      // Get model reference
      GRBModel model = this.modelWrappee;
      model.update();

      this.testConstraintsType(model.get(GRB.CharAttr.Sense, model.getConstrs()), GRB.LESS_EQUAL);
      this.testConstraintsName(model.get(GRB.StringAttr.ConstrName, model.getConstrs()));
    } catch (GRBException e) {
      e.printStackTrace();
      System.err
          .println(this.getClass().getName() + ": error in testAddLessEqualConstraint method.");
    }
  }

  @Test
  public void testCreateEmptyLinearExpression() {
    assertEquals(
        ((GurobiLinearExpression) this.modelWrapper.createEmptyLinearExpression()).getWrappee()
            .size(),
        new GRBLinExpr().size());
  }

  /**
   * Models the following problem using the MPL API (assumes the correctness of the API by tests):
   * min 1x1 + 1x2 s.t 1x1 + 2x2 <= 10 2x1 + 1x2 <= 10 x1 >= 0 x2 >= 0
   */
  private void modelProblem(int optimizationSense) {

    // Adds 2 real variables using
    for (int j = 0; j < GurobiModelTest.VAR_NUM; j++) {
      this.varWrappers.add(this.modelWrapper
          .addRealVariable(this.lbs.get(GRB.CONTINUOUS), this.ubs.get(GRB.CONTINUOUS)));
    }

    // Adds 2 constraints using
    for (int i = 0; i < GurobiModelTest.EXPR_NUM; i++) {
      LinearExpression expr = this.modelWrapper.createEmptyLinearExpression();
      for (int j = 0; j < GurobiModelTest.VAR_NUM; j++) {
        expr.addTerm(this.exprCoeffs[i][j], this.varWrappers.get(j));
      }
      this.modelWrapper.addLessEqualConstraint(expr, this.constantValue);
    }

    // Creates the objective function
    LinearExpression objExpr = this.modelWrapper.createEmptyLinearExpression();
    for (int j = 0; j < GurobiModelTest.VAR_NUM; j++) {
      objExpr.addTerm(this.objFunCoeffs[j], this.varWrappers.get(j));
    }

    // Add the objective function
    switch (optimizationSense) {
      case GRB.MINIMIZE:
        this.modelWrapper.setObjectiveFunctionMinimize(objExpr);
        break;

      case GRB.MAXIMIZE:
        this.modelWrapper.setObjectiveFunctionMaximize(objExpr);
        break;
    }
  }

  @Test(groups = {"solveProblemGroup"})
  public void testSolveMinimizationProblem() {
    this.modelProblem(GRB.MINIMIZE);
    this.modelWrapper.solve();
  }

  @Test(groups = {"solveProblemGroup"})
  public void testSolveMaximizationProblem() {
    this.modelProblem(GRB.MAXIMIZE);
    this.modelWrapper.solve();
  }

  @Test
  public void testGetWrappee() {
    assertNotNull(this.modelWrappee);
  }

  @Test(enabled = false)
  public void testDispose() {
    assertTrue(this.modelWrapper.dispose());
    try {
      this.modelWrappee.optimize();
    } catch (GRBException e) {
      e.printStackTrace(System.out);
      System.err.println(
          this.getClass().getName() + ": got expected exception trying to use a disposed model.");
    }
  }

}