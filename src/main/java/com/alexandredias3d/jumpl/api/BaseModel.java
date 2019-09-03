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
 * Base model wrapper class. Provides implementation for the method {@link #getWrappee()}, which
 * returns the wrappee (underlying) object that represents a model. Also provides the default naming
 * of variables and constraints.
 *
 * @param <T> type of wrappee model
 * @author Alexandre H. T. Dias
 */
public abstract class BaseModel<T> implements Model {

  /**
   * Wrappee (underlying) model of type T.
   */
  protected T model;

  /**
   * Stores the current index to be used while naming variables.
   */
  private int variableIndex;

  /**
   * Stores the current index to be used while naming constraints.
   */
  private int constraintIndex;

  /**
   * Defines a format for consistent default naming of variables.
   */
  private String variableNameFormat;

  /**
   * Defines a format for consistent default naming of variables.
   */
  private String constraintNameFormat;

  protected BaseModel() {
    variableIndex = -1;
    constraintIndex = -1;
    variableNameFormat = "x%d";
    constraintNameFormat = "c%d";
  }

  /**
   * Increments and gets the current variable index.
   *
   * @return current variable index
   */
  private int getVariableIndex() {
    return ++variableIndex;
  }

  /**
   * Increments and gets the current constraint index.
   *
   * @return current constraint index
   */
  private int getConstraintIndex() {
    return ++constraintIndex;
  }

  /**
   * Gets the current variable name. Default naming format is "x%d" where d is an auto-incremented
   * integer.
   *
   * @return variable name using current format and index
   */
  protected String getVariableName() {
    return String.format(variableNameFormat, getVariableIndex());
  }

  /**
   * Gets the current constraint name. Default naming format is "c%d" where d is an auto-incremented
   * integer.
   *
   * @return constraint name using current format and index
   */
  protected String getConstraintName() {
    return String.format(constraintNameFormat, getConstraintIndex());
  }

  /**
   * Sets the variable name format to the given one.
   *
   * @param format String containing the format of the variable name
   */
  public void setVariableNameFormat(String format) {
    variableNameFormat = format;
  }

  /**
   * Sets the constraint name format to the given one.
   *
   * @param format String containing the format of the constraint name
   */
  public void setConstraintNameFormat(String format) {
    constraintNameFormat = format;
  }

  @Override
  public T getWrappee() {
    return model;
  }

}
