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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.alexandredias3d.jumpl.api.LinearExpression;
import com.alexandredias3d.jumpl.api.Model;
import com.alexandredias3d.jumpl.api.Variable;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjectiveSense;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.Iterator;
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
public class CplexModelTest {

  private static final int VAR_NUM = 2;
  private static final int EXPR_NUM = 2;
  private static final double EPSILON = 0.00000001;
  // Attributes
  private final Map<IloNumVarType, Double> lbs = Map.ofEntries(
      Map.entry(IloNumVarType.Float, 0.0),
      Map.entry(IloNumVarType.Int, 0.0),
      Map.entry(IloNumVarType.Bool, 0.0)
  );

  private final Map<IloNumVarType, Double> ubs = Map.ofEntries(
      Map.entry(IloNumVarType.Float, Double.MAX_VALUE),
      Map.entry(IloNumVarType.Int, Double.MAX_VALUE),
      Map.entry(IloNumVarType.Bool, 1.0)
  );

  private final double[] objFunCoeffs = {1.0, 1.0};

  private final double[][] exprCoeffs = {{1.0, 2.0},
      {2.0, 1.0}};

  private final double constantValue = 10.0;

  private final String[] varNames = {"var1", "var2"};
  private final String constrBaseNameFormat = "constr%d";
  // Underlying types
  private List<IloNumVar> vars;
  private List<IloLinearNumExpr> exprs;
  private IloLinearNumExpr objExpr;
  private int currentConstraintIndex;
  // Wrappers
  private List<Variable> varWrappers;
  private List<LinearExpression> exprWrappers;
  private LinearExpression objExprWrapper;
  private Model modelWrapper;
  // Wrappee reference
  private IloCplex modelWrappee;

  @BeforeMethod(alwaysRun = true)
  public void setUpGurobiModel() {
    this.modelWrapper = new CplexModel();
    this.modelWrappee = ((CplexModel) this.modelWrapper).getWrappee();
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
      for (int i = 0; i < CplexModelTest.VAR_NUM; i++) {
        this.vars.add(this.modelWrappee.numVar(this.lbs.get(IloNumVarType.Float),
            this.ubs.get(IloNumVarType.Float), this.varNames[i]));
        this.varWrappers.add(new CplexVariable(this.vars.get(i)));
      }
    } catch (IloException e) {
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
      this.objExpr = this.modelWrappee.linearNumExpr();
      for (int j = 0; j < CplexModelTest.VAR_NUM; j++) {
        this.objExpr.addTerm(this.objFunCoeffs[j], this.vars.get(j));
      }
      this.objExprWrapper = new CplexLinearExpression(this.objExpr);
    } catch (IloException e) {
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
      for (int i = 0; i < CplexModelTest.EXPR_NUM; i++) {
        this.exprs.add(this.modelWrappee.linearNumExpr());
        for (int j = 0; j < CplexModelTest.VAR_NUM; j++) {
          this.exprs.get(i).addTerm(this.exprCoeffs[i][j], this.vars.get(j));
        }
        this.exprWrappers.add(new CplexLinearExpression(this.exprs.get(i)));
      }
    } catch (IloException e) {
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
      assertEquals(this.modelWrappee.getObjective().getSense(), IloObjectiveSense.Minimize);
    } catch (IloException e) {
      e.printStackTrace();
      System.err.println(
          this.getClass().getName() + ": error in testSetObjectiveFunctionMinimize method.");
    }
  }

  @Test(groups = {"setObjectiveFunctionGroup"})
  public void testSetObjectiveFunctionMaximize() {
    try {
      this.modelWrapper.setObjectiveFunctionMaximize(this.objExprWrapper);
      assertEquals(this.modelWrappee.getObjective().getSense(), IloObjectiveSense.Maximize);
    } catch (IloException e) {
      e.printStackTrace();
      System.err.println(
          this.getClass().getName() + ": error in testSetObjectiveFunctionMaximize method.");
    }
  }

  private void testAddVariablesUtil(IloNumVarType type, String[] names) {

    for (int i = 0; i < CplexModelTest.VAR_NUM; i++) {
      if (names != null) {
        if (IloNumVarType.Float.equals(type)) {
          this.varWrappers.add(
              this.modelWrapper.addRealVariable(this.lbs.get(type), this.ubs.get(type), names[i]));
        } else if (IloNumVarType.Int.equals(type)) {
          this.varWrappers.add(this.modelWrapper
              .addIntegerVariable(this.lbs.get(type), this.ubs.get(type), names[i]));
        } else if (IloNumVarType.Bool.equals(type)) {
          this.varWrappers.add(this.modelWrapper.addBinaryVariable(names[i]));
        }
      } else {
        if (IloNumVarType.Float.equals(type)) {
          this.varWrappers
              .add(this.modelWrapper.addRealVariable(this.lbs.get(type), this.ubs.get(type)));
        } else if (IloNumVarType.Int.equals(type)) {
          this.varWrappers
              .add(this.modelWrapper.addIntegerVariable(this.lbs.get(type), this.ubs.get(type)));
        } else if (IloNumVarType.Bool.equals(type)) {
          this.varWrappers.add(this.modelWrapper.addBinaryVariable());
        }
      }
    }

    try {

      // This test cannot be performed in a similar fashion to the one performed in Gurobi since the IloCplex
      // class does not provide a way for querying the variables alone.
      for (int i = 0; i < CplexModelTest.VAR_NUM; i++) {
        IloNumVar varWrappee = this.varWrappers.get(i).getWrappee();
        assertEquals(varWrappee.getLB(), this.lbs.get(type), CplexModelTest.EPSILON);
        assertEquals(varWrappee.getUB(), this.ubs.get(type), CplexModelTest.EPSILON);

        // Asserts if varNames match the given or the default ones
        if (names != null) {
          assertEquals(varWrappee.getName(), names[i]);
        } else {
          assertEquals(varWrappee.getName(), String.format("x%d", i));
        }

        // Asserts if variable is of the correct type
        assertEquals(varWrappee.getType(), type);
      }

    } catch (IloException e) {
      e.printStackTrace();
      System.err.println(
          this.getClass().getName() + " : error in testAddVariablesUtil method for type ." + type);
    }
  }

  @Test(groups = {"addVariablesGroup"})
  public void testAddRealNamedVariable() {
    this.testAddVariablesUtil(IloNumVarType.Float, this.varNames);
  }

  @Test(groups = {"addVariablesGroup"})
  public void testAddRealUnnamedVariable() {
    this.testAddVariablesUtil(IloNumVarType.Float, null);
  }

  @Test(groups = {"addVariablesGroup"})
  public void testAddIntegerNamedVariable() {
    this.testAddVariablesUtil(IloNumVarType.Int, this.varNames);
  }

  @Test(groups = {"addVariablesGroup"})
  public void testAddIntegerUnnamedVariable() {
    this.testAddVariablesUtil(IloNumVarType.Int, null);
  }

  @Test(groups = {"addVariablesGroup"})
  public void testAddBinaryNamedVariable() {
    this.testAddVariablesUtil(IloNumVarType.Bool, this.varNames);
  }

  @Test(groups = {"addVariablesGroup"})
  public void testAddBinaryUnnamedVariable() {
    this.testAddVariablesUtil(IloNumVarType.Bool, null);
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
      IloCplex model = this.modelWrappee;
      Iterator it = model.rangeIterator();

      // CPLEX does not provide easy querying for constraints either, thus this is a simplified version of the
      // test performed in GurobiModelTest. Constraints are being divided into groups of 4.
      int i = 0;
      while (it.hasNext()) {
        IloRange constr = (IloRange) it.next();
        double lb = (i < 4) ? this.constantValue : 0.0;
        double ub = (i < 4) ? this.constantValue : 0.0;

        assertEquals(constr.getLB(), lb);
        assertEquals(constr.getUB(), ub);
        i = (i == 7) ? 0 : i + 1;
      }

    } catch (IloException e) {
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
      IloCplex model = this.modelWrappee;
      Iterator it = model.rangeIterator();

      // CPLEX does not provide easy querying for constraints either, thus this is a simplified version of the
      // test performed in GurobiModelTest. Constraints are being divided into groups of 4.
      int i = 0;
      boolean flip = false;
      while (it.hasNext()) {
        IloRange constr = (IloRange) it.next();
        double lb, ub;

        if (flip) {
          lb = (i < 4) ? this.constantValue : 0.0;
          assertEquals(constr.getLB(), lb);
        } else {
          ub = (i < 4) ? this.constantValue : 0.0;
          assertEquals(constr.getUB(), ub);
        }

        i = (i == 7) ? 0 : i + 1;
        flip = (i < 4) != flip;
      }

    } catch (IloException e) {
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
      IloCplex model = this.modelWrappee;
      Iterator it = model.rangeIterator();

      // CPLEX does not provide easy querying for constraints either, thus this is a simplified version of the
      // test performed in GurobiModelTest. Constraints are being divided into groups of 4.
      int i = 0;
      boolean flip = true;
      while (it.hasNext()) {
        IloRange constr = (IloRange) it.next();
        double lb, ub;

        if (flip) {
          lb = (i < 4) ? this.constantValue : 0.0;
          assertEquals(constr.getLB(), lb);
        } else {
          ub = (i < 4) ? this.constantValue : 0.0;
          assertEquals(constr.getUB(), ub);
        }

        i = (i == 7) ? 0 : i + 1;
        flip = (i < 4) != flip;
      }

    } catch (IloException e) {
      e.printStackTrace();
      System.err
          .println(this.getClass().getName() + ": error in testAddLessEqualConstraint method.");
    }

  }

  @Test
  public void testCreateEmptyLinearExpression() {
    try {
      assertEquals(
          ((CplexLinearExpression) this.modelWrapper.createEmptyLinearExpression()).getWrappee()
              .getConstant(),
          this.modelWrappee.linearIntExpr().getConstant());
    } catch (IloException e) {
      e.printStackTrace();
      System.err.println(this.getClass().getName() + ": error in testCreateLinearExpression().");
    }
  }

  /**
   * Models the following problem using the MPL API (assumes the correctness of the API by tests):
   * min 1x1 + 1x2 s.t 1x1 + 2x2 <= 10 2x1 + 1x2 <= 10 x1 >= 0 x2 >= 0
   */
  private void modelProblem(IloObjectiveSense optimizationSense) {

    // Adds 2 real variables using
    for (int j = 0; j < CplexModelTest.VAR_NUM; j++) {
      this.varWrappers.add(this.modelWrapper.addRealVariable(this.lbs.get(IloNumVarType.Float),
          this.ubs.get(IloNumVarType.Float)));
    }

    // Adds 2 constraints using
    for (int i = 0; i < CplexModelTest.EXPR_NUM; i++) {
      LinearExpression expr = this.modelWrapper.createEmptyLinearExpression();
      for (int j = 0; j < CplexModelTest.VAR_NUM; j++) {
        expr.addTerm(this.exprCoeffs[i][j], this.varWrappers.get(j));
      }
      this.modelWrapper.addLessEqualConstraint(expr, this.constantValue);
    }

    // Creates the objective function
    LinearExpression objExpr = this.modelWrapper.createEmptyLinearExpression();
    for (int j = 0; j < CplexModelTest.VAR_NUM; j++) {
      objExpr.addTerm(this.objFunCoeffs[j], this.varWrappers.get(j));
    }

    // Add the objective function
    if (IloObjectiveSense.Minimize.equals(optimizationSense)) {
      this.modelWrapper.setObjectiveFunctionMinimize(objExpr);
    } else if (IloObjectiveSense.Maximize.equals(optimizationSense)) {
      this.modelWrapper.setObjectiveFunctionMaximize(objExpr);
    }
  }

  @Test(groups = {"solveProblemGroup"})
  public void testSolveMinimizationProblem() {
    this.modelProblem(IloObjectiveSense.Minimize);
    this.modelWrapper.solve();
  }

  @Test(groups = {"solveProblemGroup"})
  public void testSolveMaximizationProblem() {
    this.modelProblem(IloObjectiveSense.Maximize);
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
      this.modelWrappee.solve();
    } catch (IloException e) {
      e.printStackTrace(System.out);
      System.err.println(
          this.getClass().getName() + ": got expected exception trying to use a disposed model.");
    }
  }
}