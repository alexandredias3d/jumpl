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
 * Encapsulates an object with an wrapper and allow getting its wrappee (underlying) object. Allows
 * the abstraction of several aspects concerning model creation using solvers by using wrappers
 * instead of underlying types.
 *
 * @author Alexandre H. T. Dias
 */
public interface Wrappable {

  /**
   * Gets the wrappee object of type T that can be used directly in the solvers.
   *
   * @param <T> the type of wrappee object
   * @return wrappee object of type T
   */
  <T> T getWrappee();

}
