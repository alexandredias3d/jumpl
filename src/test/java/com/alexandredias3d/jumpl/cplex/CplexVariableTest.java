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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;

import com.alexandredias3d.jumpl.api.Variable;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for the CplexVariable class. It uses the underlying type IloCplex to guarantee that any
 * eventual problems will be related only to the IloNumVar wrapper.
 */
public class CplexVariableTest {

  // Initial values for lower and upper bounds, and variable name
  private final double lb = 0.0;
  private final double ub = 100.0;
  private final String name = "var";
  // Updated values for lower and upper bounds, and variable name
  private final double lbPrime = 50.0;
  private final double ubPrime = 75.0;
  private final String namePrime = "varPrime";
  // Underlying types
  private IloNumVar var;
  private IloCplex model;
  // Variable wrapper
  private Variable varWrapper;

  @BeforeMethod
  public void setUp() {
    try {
      this.model = new IloCplex();
      this.var = this.model.numVar(this.lb, this.ub, this.name);
      this.varWrapper = new CplexVariable(this.var);
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
  public void testGetName() {
    assertEquals(this.varWrapper.getName(), this.name);
  }

  @Test
  public void testSetName() {
    this.varWrapper.setName(this.namePrime);
    assertEquals(this.varWrapper.getName(), this.namePrime);
  }

  @Test
  public void testGetLowerBound() {
    assertEquals(this.varWrapper.getLowerBound(), this.lb);
  }

  @Test
  public void testSetLowerBound() {
    this.varWrapper.setLowerBound(this.lbPrime);
    assertEquals(this.varWrapper.getLowerBound(), this.lbPrime);
  }

  @Test
  public void testGetUpperBound() {
    assertEquals(this.varWrapper.getUpperBound(), this.ub);
  }

  @Test
  public void testSetUpperBound() {
    this.varWrapper.setUpperBound(this.ubPrime);
    assertEquals(this.varWrapper.getUpperBound(), this.ubPrime);
  }

  @Test
  public void testGetWrappee() {
    assertNotNull(this.varWrapper.getWrappee());
  }

}