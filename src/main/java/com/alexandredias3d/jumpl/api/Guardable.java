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

import java.util.concurrent.Callable;

/**
 * Provides safe access to resources provided by solvers. Wraps "unsafe" calls (calls that can raise
 * exceptions) and provides a consistent way to deal with exceptions. Also, avoids the try-catch
 * boilerplate code in every one of these calls.
 *
 * @author Alexandre H. T. Dias
 */
public interface Guardable {

  /**
   * Surrounds a callable execution with a try-catch. Returns whatever was returned from the
   * Callable, or null if an exception was raised during execution.
   *
   * @param <T> type of Callable
   * @param c   callable
   * @return whatever the Callable returns or null if exception was raised
   * @see #exceptionHandler(Exception)
   */
  default <T> T guard(Callable<T> c) {
    try {
      return c.call();
    } catch (Exception e) {
      exceptionHandler(e);
      return null;
    }
  }

  /**
   * Handles the given exception by printing information and exiting the program with an error.
   *
   * @param e raised exception
   */
  default void exceptionHandler(Exception e) {
    System.err.printf("Exception in class %s\n" + "\n%s",
        getClass().getSimpleName(),
        e.getMessage());
    e.printStackTrace(System.err);
    System.exit(1);
  }

}
