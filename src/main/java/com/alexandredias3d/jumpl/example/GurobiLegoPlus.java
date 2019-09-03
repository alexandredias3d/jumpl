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

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

/**
 * LegoPlus problem solved directly in Gurobi.
 *
 * @author Alexandre H. T. Dias
 * @see <a href="https://mayronmoreira.github.io/prog-mat/01/2/subsection/1/lego">LegoPlus - Mayron
 * Moreira</a>
 */
public class GurobiLegoPlus {

  public static void main(String[] args) {

    try {

      GRBEnv env = new GRBEnv(true);
      env.start();

      GRBModel model = new GRBModel(env);

      GRBVar x = model.addVar(0, Double.POSITIVE_INFINITY, 0, GRB.CONTINUOUS, "x");
      GRBVar y = model.addVar(0, Double.POSITIVE_INFINITY, 0, GRB.CONTINUOUS, "y");

      GRBLinExpr expr1 = new GRBLinExpr();
      expr1.addTerm(2, x);
      expr1.addTerm(2, y);
      model.addConstr(expr1, GRB.LESS_EQUAL, 8, "");

      GRBLinExpr expr2 = new GRBLinExpr();
      expr2.addTerm(2, x);
      expr2.addTerm(1, y);
      model.addConstr(expr2, GRB.LESS_EQUAL, 6, "");

      GRBLinExpr obj = new GRBLinExpr();
      obj.addTerm(16, x);
      obj.addTerm(10, y);
      model.setObjective(obj, GRB.MAXIMIZE);

      model.optimize();

      System.out.printf("(x = %f, y = %f) = %f\n",
          model.getVarByName("x").get(GRB.DoubleAttr.X),
          model.getVarByName("y").get(GRB.DoubleAttr.X),
          model.get(GRB.DoubleAttr.ObjVal));

    } catch (GRBException ex) {
      System.out.println(ex.getErrorCode());
    }


  }

}
