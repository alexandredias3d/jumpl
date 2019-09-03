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

import com.alexandredias3d.jumpl.api.LinearExpression;
import com.alexandredias3d.jumpl.api.Variable;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for the GurobiLinearExpression class. It uses the underlying types of GRBEnv and GRBModel.
 * It assumes that GurobiVariable is functional. Since GurobiVariable is only used for getting and
 * setting data, any error in these tests will only be related to the GRBLinExpr wrapper.
 */
public class GurobiLinearExpressionTest {

  // Variable parameters
  private final char varType = GRB.CONTINUOUS;
  private final double objFunctionCoeff = 0;
  private final double lb = 0.0;
  private final double ub = 100.0;
  private final String baseName = "var";
  private final double coeffExpr1 = 3;
  private final double[] coeffsExpr2 = {2, 5};
  // Constant values
  private final double constant1 = 2.0;
  private final double constant2 = 0.3333;
  // Underlying types
  private GRBVar var1;
  private GRBVar var2;
  private GRBLinExpr expr1;
  private GRBLinExpr expr2;
  private GRBEnv env;
  private GRBModel model;
  // Wrappers
  private Variable var1Wrapper;
  private LinearExpression expr1Wrapper;
  private LinearExpression expr2Wrapper;

  @BeforeMethod
  public void setUp() {
    try {
      this.env = new GRBEnv(true);
      this.model = new GRBModel(this.env);

      this.expr1 = new GRBLinExpr();
      this.expr1Wrapper = new GurobiLinearExpression(this.expr1);

      this.var1 = this.model
          .addVar(this.lb, this.ub, this.objFunctionCoeff, this.varType, this.baseName + 1);
      this.var2 = this.model
          .addVar(this.lb, this.ub, this.objFunctionCoeff, this.varType, this.baseName + 2);
      this.var1Wrapper = new GurobiVariable(this.var1);

      this.expr2 = new GRBLinExpr();
      this.expr2.addTerm(this.coeffsExpr2[0], this.var1);
      this.expr2.addTerm(this.coeffsExpr2[1], this.var2);
      this.expr2Wrapper = new GurobiLinearExpression(this.expr2);

      this.model.update();
    } catch (GRBException e) {
      System.err.println(this.getClass().getName() + ": error in setUp method.");
      e.printStackTrace();
    }
  }

  @AfterMethod
  public void tearDown() {
    try {
      this.model.dispose();
      this.env.dispose();
    } catch (GRBException e) {
      System.err.println(this.getClass().getName() + ": error in tearDown method.");
      e.printStackTrace();
    }
  }

  @Test
  public void testAdd() {
    this.expr1Wrapper.add(this.expr2Wrapper);
    try {
      assertEquals(this.expr1Wrapper.getConstant(), 0.0);
      GRBLinExpr expr = ((GurobiLinearExpression) this.expr1Wrapper).getWrappee();
      for (int i = 0; i < expr.size(); i++) {
        assertEquals(expr.getCoeff(i), this.coeffsExpr2[i]);
      }
    } catch (GRBException e) {
      System.err.println(this.getClass().getName() + ": error in testAdd method.");
      e.printStackTrace();
    }
  }

  @Test
  public void testAddConstant() {
    this.expr1Wrapper.addConstant(this.constant1);
    assertEquals(this.expr1Wrapper.getConstant(), this.constant1);
    this.expr1Wrapper.addConstant(this.constant2);
    assertEquals(this.expr1Wrapper.getConstant(), this.constant1 + this.constant2);
  }

  @Test
  public void testAddTerm() {
    this.expr1Wrapper.addTerm(this.coeffExpr1, this.var1Wrapper);
    try {
      GRBLinExpr expr = ((GurobiLinearExpression) this.expr1Wrapper).getWrappee();
      assertEquals(expr.getCoeff(0), this.coeffExpr1);
      assertEquals(expr.getVar(0), this.var1);
    } catch (GRBException e) {
      System.err.println(this.getClass().getName() + ": error in testAddTerm method.");
      e.printStackTrace();
    }
  }

  @Test
  public void testDefaultConstant() {
    assertEquals(this.expr1Wrapper.getConstant(), 0.0);
  }

  @Test
  public void testGetWrappee() {
    assertNotNull(this.expr1Wrapper.getWrappee());
  }

}