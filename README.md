# nashorn-test case

nashorn-performance-test

Using the nashorn Engine in connection with `SimpleScriptContext` slows down over a longer period of time.
e.g.:

```java
// .. //
SimpleScriptContext scriptContext = new SimpleScriptContext();
scriptContext.setBindings(scriptEngine.createBindings(), ScriptContext.ENGINE_SCOPE);
compiledScript.eval(scriptContext);

Object global = scriptContext.getAttribute("window");
// Execute method
return (ScriptObjectMirror) ((Invocable) compiledScript.getEngine()).invokeMethod(global, "testPerformance");
```

Full Test-Case is here: https://github.com/BowlingX/nashorn-test/blob/master/src/com/bowlingx/Main.java
The example is rendering a react application. 

Execution starts slowly and stabilizes around 60-70ms on my machine.
It climbs up every hundreds iterations after a few minutes to over 120 - 150ms and growing (until manual stop).

Without creating the context on every iteration (but without beeing thread safe), there is no leak and performance is steady around 4ms.
The example is extracted from my library: https://github.com/BowlingX/play-webpack

## EDIT:
I changed my implementation of the referenced project to reuse the context trough multiple rendering threads.
