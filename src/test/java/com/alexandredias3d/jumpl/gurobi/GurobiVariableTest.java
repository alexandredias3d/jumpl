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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;

import com.alexandredias3d.jumpl.api.Variable;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBModel;
import gurobi.GRBVar;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for the GurobiVariable class. It uses the underlying types of GRBEnv and GRBModel to
 * guarantee that any eventual problems will be related only to the GRBVar wrapper.
 */
public class GurobiVariableTest {

  // Variable type and its value in objective function are constants
  private final char varType = GRB.CONTINUOUS;
  private final double objFunctionCoeff = 0;
  // Initial values for lower and upper bounds, and variable name
  private final double lb = 0.0;
  private final double ub = 100.0;
  private final String name = "var";
  // Updated values for lower and upper bounds, and variable name
  private final double lbPrime = 50.0;
  private final double ubPrime = 75.0;
  private final String namePrime = "varPrime";
  // Underlying types
  private GRBVar var;
  private GRBEnv env;
  private GRBModel model;
  // Variable wrapper
  private Variable varWrapper;

  @BeforeMethod
  public void setUp() {
    try {
      this.env = new GRBEnv(true);
      this.model = new GRBModel(this.env);
      this.var = this.model
          .addVar(this.lb, this.ub, this.objFunctionCoeff, this.varType, this.name);
      this.varWrapper = new GurobiVariable(this.var);
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
  public void testGetName() {
    assertEquals(this.varWrapper.getName(), this.name);
  }

  @Test
  public void testSetName() {
    try {
      this.varWrapper.setName(this.namePrime);
      this.model.update();
      assertEquals(this.varWrapper.getName(), this.namePrime);
    } catch (GRBException e) {
      System.err.println(this.getClass().getName() + ": error in testSetName method.");
      e.printStackTrace();
    }
  }

  @Test
  public void testGetLowerBound() {
    assertEquals(this.varWrapper.getLowerBound(), this.lb);
  }

  @Test
  public void testSetLowerBound() {
    try {
      this.varWrapper.setLowerBound(this.lbPrime);
      this.model.update();
      assertEquals(this.varWrapper.getLowerBound(), this.lbPrime);
    } catch (GRBException e) {
      System.err.println(this.getClass().getName() + ": error in testSetLowerBound.");
      e.printStackTrace();
    }
  }

  @Test
  public void testGetUpperBound() {
    assertEquals(this.varWrapper.getUpperBound(), this.ub);
  }

  @Test
  public void testSetUpperBound() {
    try {
      this.varWrapper.setUpperBound(this.ubPrime);
      this.model.update();
      assertEquals(this.varWrapper.getUpperBound(), this.ubPrime);
    } catch (GRBException e) {
      System.err.println(this.getClass().getName() + ": error in testSetUpperBound.");
      e.printStackTrace();
    }
  }

  @Test
  public void testGetWrappee() {
    assertNotNull(this.varWrapper.getWrappee());
  }

}