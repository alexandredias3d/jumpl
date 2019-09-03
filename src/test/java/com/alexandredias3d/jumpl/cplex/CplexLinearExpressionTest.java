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

import com.alexandredias3d.jumpl.api.LinearExpression;
import com.alexandredias3d.jumpl.api.Variable;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloLinearNumExprIterator;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for the CplexLinearExpression class. It uses the underlying type of IloCplex. It assumes
 * that CplexVariable is functional. Since CplexVariable is only used for getting and setting data,
 * any error in these tests will only be related to the GRBLinExpr wrapper.
 */
public class CplexLinearExpressionTest {

  // Variable parameters
  private final double lb = 0.0;
  private final double ub = 100.0;
  private final String baseName = "var";
  private final double coeffExpr1 = 3;
  private final double[] coeffsExpr2 = {2, 5};
  // Constant values
  private final double constant1 = 2.0;
  private final double constant2 = 0.3333;
  // Underlying types
  private IloNumVar var1;
  private IloNumVar var2;
  private IloLinearNumExpr expr1;
  private IloLinearNumExpr expr2;
  private IloCplex model;
  // Wrappers
  private Variable var1Wrapper;
  private LinearExpression expr1Wrapper;
  private LinearExpression expr2Wrapper;

  @BeforeMethod
  public void setUp() {
    try {
      this.model = new IloCplex();

      this.expr1 = this.model.linearNumExpr();
      this.expr1Wrapper = new CplexLinearExpression(this.expr1);

      this.var1 = this.model.numVar(this.lb, this.ub, this.baseName + 1);
      this.var2 = this.model.numVar(this.lb, this.ub, this.baseName + 2);
      this.var1Wrapper = new CplexVariable(this.var1);

      this.expr2 = this.model.linearNumExpr();
      this.expr2.addTerm(this.coeffsExpr2[0], this.var1);
      this.expr2.addTerm(this.coeffsExpr2[1], this.var2);
      this.expr2Wrapper = new CplexLinearExpression(this.expr2);
    } catch (IloException e) {
      e.printStackTrace();
      System.err.println(this.getClass().getName() + ": error in setUp method.");
    }
  }

  @AfterMethod
  public void tearDown() {
    this.model.end();
  }

  @Test
  public void testAdd() {
    this.expr1Wrapper.add(this.expr2Wrapper);
    assertEquals(this.expr1Wrapper.getConstant(), 0.0);

    IloLinearNumExprIterator iloLinearNumExprIterator = ((CplexLinearExpression) this.expr1Wrapper)
        .getWrappee().linearIterator();

    int i = 0;
    while (iloLinearNumExprIterator.hasNext()) {
      iloLinearNumExprIterator.nextNumVar();
      assertEquals(iloLinearNumExprIterator.getValue(), this.coeffsExpr2[i]);
      i++;
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
    IloLinearNumExprIterator iloLinearNumExprIterator = ((CplexLinearExpression) this.expr1Wrapper)
        .getWrappee().linearIterator();
    assertEquals(iloLinearNumExprIterator.nextNumVar(), this.var1);
    assertEquals(iloLinearNumExprIterator.getValue(), this.coeffExpr1);
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