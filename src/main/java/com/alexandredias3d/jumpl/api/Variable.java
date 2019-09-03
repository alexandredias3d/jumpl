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
 * Wraps a concrete variable type from a solver. Provides common functionality of variables seen in
 * different solvers.
 *
 * @author Alexandre H. T. Dias
 */
public interface Variable extends Wrappable {

  /**
   * @return variable name
   * @see #setName(String)
   */
  String getName();

  /**
   * Sets the variable name which can be used to retrieve information and for identification in
   * model files.
   *
   * @param name new variable name
   */
  void setName(String name);

  /**
   * @return variable lower bound
   * @see #setLowerBound(double)
   */
  double getLowerBound();

  /**
   * Sets the lower bound value of the variable which is the minimum value it can store.
   *
   * @param lowerBound new variable lower bound
   */
  void setLowerBound(double lowerBound);

  /**
   * @return variable upper bound
   * @see #setUpperBound(double)
   */
  double getUpperBound();

  /**
   * Sets the upper bound value of the variable which is the maximum value it can store.
   *
   * @param upperBound new variable upper bound
   */
  void setUpperBound(double upperBound);

}