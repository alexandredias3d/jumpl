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

package com.alexandredias3d.jumpl.example;

/**
 * Minas Máquinas input data to be used in the formulation.
 *
 * @author Alexandre H. T. Dias
 * @see <a href="https://mayronmoreira.github.io/prog-mat/01/2/subsection/5/lot-sizing">Minas
 * Máquinas - Mayron Moreira</a>
 */
final class MinasMaquinasInput {

  final int numberOfItems = 3;
  final int numberOfPeriods = 2;

  final double[][] productionCostPerPeriod = {
      {100, 80},
      {60, 80},
      {30, 200}
  };

  final double[][] storageCostPerPeriod = {
      {2, 2.5},
      {3, 3.5},
      {3.5, 3.5}
  };

  final double[] resourcesPerItem = {0.25, 0.3, 0.3};

  final double[] resourcesPerPeriod = {200, 250};

  final double[][] itemDemandPerPeriod = {
      {50, 20},
      {40, 60},
      {100, 80}
  };

}
