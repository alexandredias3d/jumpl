<p align="center">
  <h3 align="center">JUMPL</h3>

  <p align="center">
    Java Uncomplicated Mathematical Programming Language
    <br />
  </p>
</p>

## About

JUMPL was developed to provide an easy way to solve Mixed Integer Programming (MIP) problems during my master's degree. Since I have not found a library for solving MIPs in Java that could be used with CPLEX and Gurobi, I have created this simple wrapper.

The wrapper was implemented in Java to simplify the integration with the simulator used. Although some unit tests are provided, this code is not production-ready and should be used taking this into account.

JUMPL can be divided in three parts: the classes and interfaces that wrap the underlying details of the solvers, the formulation class that provides overridable methods that are common to several formulations, and the model factory that deals with the instantiation of formulations and their optimization.

## Prerequisites

You need to manually install the solvers in order to build or use JUMPL. Currently it supports CPLEX and Gurobi. The bootstrap.sh expects that the user have set two environment variables that points to the JAR of each solver. It basically adds the JARs to the local Maven so that they can be used as Maven dependecies in Maven projects

There are no plans of supporting more solvers.

## Usage

You can add the JUMPL API as a Maven dependency in the pom.xml of your Maven project.
```xml
<dependency>
  <groupId>com.alexandredias3d</groupId>
  <artifactId>jumpl</artifactId>
  <!-- It is possible to specify the version or use the latest one -->
  <version>LATEST</version>
</dependency>
```


Following is an example of the API usage which solves a model using all available solvers in JUMPL (CPLEX and Gurobi).


```java
package com.alexandredias3d.jumpl.example;

import com.alexandredias3d.jumpl.api.BaseFormulation;
import com.alexandredias3d.jumpl.api.LinearExpression;
import com.alexandredias3d.jumpl.api.Model;
import com.alexandredias3d.jumpl.api.ModelFactory;
import com.alexandredias3d.jumpl.api.Variable;

/**
 * Lego Plus problem formulation agnostic of the underlying solver.
 *
 * @author Alexandre H. T. Dias
 * @see <a href="https://mayronmoreira.github.io/prog-mat/01/2/subsection/1/lego">LegoPlus - Mayron Moreira</a>
 */
public class LegoPlusFormulation extends BaseFormulation {

  private Variable x;
  private Variable y;

  public LegoPlusFormulation(final Model model) {
    super(model);
  }

  public static void main(String[] args) {
    var formulationList = ModelFactory.solveInAllSolvers(LegoPlusFormulation.class);

    for (var formulation : formulationList) {
      System.out.printf("(x = %f, y = %f) = %f\n",
          formulation.model.getVariableValue(formulation.x),
          formulation.model.getVariableValue(formulation.y),
          formulation.model.getObjectiveFunctionValue());
    }

  }

  @Override
  protected void putVariables() {
    x = this.model.addRealVariable(0, Double.POSITIVE_INFINITY, "x");
    y = this.model.addRealVariable(0, Double.POSITIVE_INFINITY, "y");
  }

  @Override
  protected void putConstraints() {
    LinearExpression expression1 = model.createEmptyLinearExpression();
    expression1.addTerm(2, x);
    expression1.addTerm(2, y);
    model.addLessEqualConstraint(expression1, 8);

    LinearExpression expression2 = model.createEmptyLinearExpression();
    expression2.addTerm(2, x);
    expression2.addTerm(1, y);
    model.addLessEqualConstraint(expression1, 6);
  }

  @Override
  protected void putObjectiveFunction() {
    LinearExpression objectiveFunction = model.createEmptyLinearExpression();
    objectiveFunction.addTerm(16, x);
    objectiveFunction.addTerm(10, y);
    model.setObjectiveFunctionMaximize(objectiveFunction);
  }

}
```


<!-- LICENSE -->
## License

Distributed under the Apache-2.0 License. See `LICENSE` for more information.
