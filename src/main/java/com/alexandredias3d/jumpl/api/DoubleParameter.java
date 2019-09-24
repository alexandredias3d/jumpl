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

public enum DoubleParameter {

  /**
   * Absolute MIP gap between lower and upper objective bounds.
   */
  ABSOLUTE_MIP_GAP,

  /**
   * Relative MIP gap between lower and upper objective bounds.
   */
  RELATIVE_MIP_GAP,

  /**
   * Maximum time (in seconds) that can be spent by the solver during optimization.
   */
  TIME_LIMIT

}
