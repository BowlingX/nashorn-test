package com.bowlingx;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;


public class Main {

    public static void main(String[] args) throws ScriptException, NoSuchMethodException {

        ScriptEngine scriptEngine = new NashornScriptEngineFactory().getScriptEngine();
        ClassLoader loader = Main.class.getClassLoader();
        String pre = "var global = global || this, self = self || this, window = window || this;var console = {};console.debug = print;console.warn = print;console.error = print;console.log = print;console.trace = print;global.setTimeout = function(fn, delay) {\n" +
                "  return __play_webpack_setTimeout.apply(fn, delay || 0);\n" +
                "};";
        CompiledScript compiledScript = ((Compilable) scriptEngine).compile(new InputStreamReader(new SequenceInputStream(
                java.util.Collections.enumeration(
                        asList(
                                new ByteArrayInputStream(pre.getBytes(StandardCharsets.UTF_8)),
                                loader.getResourceAsStream("./resources/vendor.js"),
                                loader.getResourceAsStream("./resources/polyfills.js"),
                                loader.getResourceAsStream("./resources/server.js")

                        ))
        )));



        List<Integer> range = IntStream.rangeClosed(0, 10000000)
                .boxed().collect(Collectors.toList());

        range.forEach((Integer i) -> {
            Long start = System.nanoTime();


            ScriptObjectMirror result = runScript(scriptEngine, compiledScript);
            assert result != null;
            System.out.println((System.nanoTime() - start) / 1000000);
        });
    }

    private static ScriptObjectMirror runScript(ScriptEngine scriptEngine, CompiledScript compiledScript) {
        try {
            SimpleScriptContext scriptContext = new SimpleScriptContext();
            scriptContext.setBindings(scriptEngine.createBindings(), ScriptContext.ENGINE_SCOPE);
            compiledScript.eval(scriptContext);

            Object global = scriptContext.getAttribute("window");
            return (ScriptObjectMirror) ((Invocable) compiledScript.getEngine()).invokeMethod(global, "testPerformance");
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
