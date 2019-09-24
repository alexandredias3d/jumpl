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

import com.alexandredias3d.jumpl.cplex.CplexModel;
import com.alexandredias3d.jumpl.gurobi.GurobiModel;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstracts model creation details and provides two ways to use the API. {@link #createModel}
 * returns an instance of a concrete model while {@link #solveIn} handles creation and execution of
 * a formulation from start to finish.
 *
 * @author Alexandre H. T. Dias
 */
public final class ModelFactory {

  /**
   * Flag to control verbosity of solvers.
   */
  private static boolean VERBOSE = false;

  /**
   * Class name used as prefix in error messages.
   */
  private static final String errorPrefix = ModelFactory.class.getName();

  /**
   * Avoids class instantiation.
   */
  private ModelFactory() {
    throw new AssertionError();
  }

  /**
   * Creates a {@link Model} according to the given solver {@link Solver}.
   *
   * @param solver the solver that will optimize the model
   * @return an Model instance for the given solver
   */
  public static Model createModel(Solver solver) {
    switch (solver) {
      case CPLEX:
        return new CplexModel(VERBOSE);

      case GUROBI:
        return new GurobiModel(VERBOSE);

      default:
        throw new IllegalArgumentException(String.format(
            "%s: solver must be a valid entry (see the Solver enum for supported entries).",
            errorPrefix));
    }
  }

  /**
   * Queries the constructor of a formulation that receives a Model as the parameter. Returns the
   * constructor if it exists, otherwise finishes execution with an error.
   *
   * @return the constructor of a formulation
   */
  private static <T extends BaseFormulation> Constructor<T> getFormulationConstructor(
      Class<T> formulationClass) {
    Constructor<T> constructor;
    try {
      constructor = formulationClass.getConstructor(Model.class);
    } catch (NoSuchMethodException | SecurityException ex) {
      System.err.printf(
          "%s: the constructor has not been found in subclass or a security problem has happened.%n",
          errorPrefix);
      System.exit(1);
      constructor = null;
    }
    return constructor;
  }

  /**
   * Creates a model that is modified by the formulation and consequently, solved by the given
   * solver.
   *
   * @param solver the solver that will optimize the model
   * @return the instance of the formulation
   * @see BaseFormulation
   */
  private static <T extends BaseFormulation> T instantiateFormulation(Class<T> formulationClass,
      Solver solver) {
    Model model = createModel(solver);
    T formulation = null;
    Constructor<T> constructor = getFormulationConstructor(formulationClass);
    try {
      formulation = constructor.newInstance(model);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      ex.printStackTrace();
      System.err.printf("%n%s: one of the following errors has happened:"
              + "%n· Constructor of the concrete formulation is inaccessible;"
              + "%n· Number and types of the concrete formulation differ from the ones needed;"
              + "%n· Trying to instantiate an abstract formulation;"
              + "%n· Concrete formulation has thrown an exception.%n",
          errorPrefix);
      System.exit(1);
    }
    return formulation;
  }

  /**
   * Creates the model described in formulation and solves it using the given solver.
   *
   * @param formulationClass the subclass of BaseFormulation that describes the model
   * @param solver           the solver that will optimize the model
   * @return the instance of the formulation
   */
  public static <T extends BaseFormulation> T solveIn(Class<T> formulationClass, Solver solver) {
    return instantiateFormulation(formulationClass, solver);
  }

  /**
   * Creates the model described in formulation and solves it using all solvers.
   *
   * @param formulationClass the subclass of BaseFormulation that describes the model
   * @return the list of instances of the formulation
   */
  public static <T extends BaseFormulation> List<T> solveInAllSolvers(Class<T> formulationClass) {
    var formulationList = new ArrayList<T>();
    for (var solver : Solver.values()) {
      formulationList.add(instantiateFormulation(formulationClass, solver));
    }
    return formulationList;
  }

  /**
   * Enables the verbose output in console.
   */
  public void enableVerboseOutput() {
    ModelFactory.VERBOSE = true;
  }

  /**
   * Disables the verbose output in console.
   */
  public void disableVerboseOutput() {
    ModelFactory.VERBOSE = false;
  }

}
