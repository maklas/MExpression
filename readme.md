Little expression evaluation framework.

Example of usage:
```java
String myExp = "2 + sin(5x^10 * 0.21)";
Expression expression = Compiler.compile(Test.expression);
ObjectMap<String, Double> parameters = new ObjectMap<String, Double>();
parameters.put("x", 5d);
double result = expression.evaluate(parameters);
System.out.println(result);
```

Full documentation is coming soon.