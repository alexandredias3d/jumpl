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
 * Defines aliases for the supported solvers. Currently supports:
 *
 * <li>{@link #CPLEX}</li>
 * <li>{@link #GUROBI}</li>
 *
 * @author Alexandre H. T. Dias
 */
public enum Solver {
  /**
   * /** CPLEX solver entry.
   */
  CPLEX,
  /**
   * GUROBI solver entry.
   */
  GUROBI,
  /**
   * Defines an entry to run all available solvers.
   */
  ALL
}
