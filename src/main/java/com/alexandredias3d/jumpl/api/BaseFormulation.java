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
 * Provides the basic structure of a formulation including model population and execution. Calls a
 * sequence of methods for model creation automatically whenever a formulation is instantiated. User
 * should extend this class and implement at least the following methods: {@link #putVariables()},
 * {@link #putConstraints()} and {@link #putObjectiveFunction()}.
 *
 * @author Alexandre H. T. Dias
 */
public abstract class BaseFormulation {

  protected Model model;

  /**
   * Forces subclasses to call only the public constructors.
   *
   * @see #BaseFormulation(Model)
   */
  private BaseFormulation() {
  }

  /**
   * Creates a base formulation with the given model, calling {@link #execute()} afterward.
   *
   * @param model reference to a model wrapper
   * @see #execute()
   */
  public BaseFormulation(Model model) {
    this.model = model;
    execute();
  }

  /**
   * Executes all the steps needed to obtain a solution to the model. Firstly, calls {@link
   * #preOptimization()} to perform any pre-optimization procedure defined in subclasses.Secondly,
   * calls {@link #populateModel()} to populate the model with its decision variables, constraints
   * and objective function. Thirdly, calls {@link #solveModel()} to solve the model in the solver.
   * Lastly, calls {@link #postOptimization()} to perform any post-optimization procedure defined in
   * subclasses.
   */
  private void execute() {
    preOptimization();
    populateModel();
    solveModel();
    postOptimization();
  }

  /**
   * Populates an object to represent a mathematical model. Calls the following methods to
   * completely create a model {@link #putVariables()}, {@link #putConstraints()}, and {@link
   * #putObjectiveFunction()}.
   *
   * @see #execute()
   */
  private void populateModel() {
    putVariables();
    putConstraints();
    putObjectiveFunction();
  }

  /**
   * Calls the corresponding model solve method.
   *
   * @see #execute()
   */
  private void solveModel() {
    model.solve();
  }

  /**
   * Puts the decision variables in the model.
   *
   * @see #execute()
   */
  protected abstract void putVariables();

  /**
   * Puts the constraints in the model.
   *
   * @see #execute()
   */
  protected abstract void putConstraints();

  /**
   * Puts the objective function in the model.
   *
   * @see #execute()
   */
  protected abstract void putObjectiveFunction();

  /**
   * Puts cuts in the model to tighten its formulation.
   */
  protected void putCuts() {
    throw new UnsupportedOperationException(getClass().getName() + ": not implemented yet.");
  }

  /**
   * Performs pre-optimization procedures. Nothing is performed in the default implementation.
   *
   * @see #execute()
   */
  protected void preOptimization() {
  }

  /**
   * Performs post-optimization procedures. Nothing is performed in the default implementation.
   *
   * @see #execute()
   */
  protected void postOptimization() {
  }

  /**
   * Gets the model after being modified and solved by the formulation.
   * @return the model instance
   */
  public Model getModel() {
    return model;
  }

}
