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

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * LegoPlus problem solved directly in CPLEX.
 *
 * @author Alexandre H. T. Dias
 * @see <a href="https://mayronmoreira.github.io/prog-mat/01/2/subsection/1/lego">LegoPlus - Mayron
 * Moreira</a>
 */
public class CplexLegoPlus {

  public static void main(String[] args) {

    try {

      IloCplex model = new IloCplex();

      IloNumVar x = model.numVar(0, Double.POSITIVE_INFINITY, "x");
      IloNumVar y = model.numVar(0, Double.POSITIVE_INFINITY, "y");

      IloLinearNumExpr expr1 = model.linearNumExpr(0);
      expr1.addTerm(2, x);
      expr1.addTerm(2, y);
      model.addLe(expr1, 8, "R1");

      IloLinearNumExpr expr2 = model.linearNumExpr(0);
      expr2.addTerm(2, x);
      expr2.addTerm(1, y);
      model.addLe(expr2, 6, "R2");

      IloLinearNumExpr obj = model.linearNumExpr(0);
      obj.addTerm(16, x);
      obj.addTerm(10, y);
      model.addMaximize(obj);

      model.solve();

      System.out.printf("(x = %f, y = %f) = %f\n",
          model.getValue(x),
          model.getValue(y),
          model.getObjValue());

    } catch (IloException ex) {
      System.err.println(ex.getMessage());
    }

  }

}
