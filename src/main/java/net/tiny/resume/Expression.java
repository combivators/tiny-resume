package net.tiny.resume;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

/**
 * 运算表达式
 * <pre>
 * {@code
 * Expression exp = new Expression.Builder()
 *     .exp("value / 65.0 * rate")
 *     .build();
 * double v = exp.evaluate(40.0, 0.05);
 * }
 * </pre>
 *
 * 函数表达式
 * <pre>
 * {@code
 * Expression exp = new Expression.Builder()
 *     .fun("var exp = function(value, rate) {return value / 65.0 * rate;};")
 *     .call("exp(var1, var2)")
 *     .build();
 * double v = exp.evaluate(40.0, 0.05);
 * }
 * </pre>
 */
public class Expression {

    private static Logger LOGGER = Logger.getLogger(Expression.class.getName());

    private final Builder builder;
    // Use ThreadLocal context to avoid cross-talk
    private ThreadLocal<ScriptContext> context;
    private CompiledScript lib;
    private CompiledScript script;

    private Expression(Builder b) {
        this.builder = b;
        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            lib = ((Compilable) engine)
                    .compile(builder.function);
            script = ((Compilable) engine).compile(builder.call);
            context = ThreadLocal.withInitial(() -> initContext(lib));
        } catch (ScriptException e) {
             LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    // A function to initialize a ScriptContext with a base library
    private ScriptContext initContext(CompiledScript lib) {
        ScriptContext sc = new SimpleScriptContext();
        try {
            lib.eval(sc);
        } catch (ScriptException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return sc;
    }

    // A function to set the variable binding, evaluate the script, and catch
    // the exception inside a lambda
    public double evaluate(Object... args) {
        ScriptContext sc = context.get();
        Bindings bindings = sc.getBindings(ScriptContext.ENGINE_SCOPE);
        for (int n=0; n<args.length; n++) {
            bindings.put("var"+(n+1), args[n]);
        }
        try {
            Object ret = script.eval(sc);
            if (ret instanceof Double) {
                return ((Double)ret).doubleValue();
            } else {
                return Double.parseDouble(ret.toString());
            }
        } catch (ScriptException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
        return 0.0d;
    }

    public static class Builder {
        String function;
        String call = "exp(var1)";

        /** 可以单独设置运算表达式 */
        public Builder exp(String e) {
            String[] vars = e.replaceAll("\\d+(\\.\\d+)?", " ") //去除数字
                    .replaceAll("[+()/^|*|-]", " ")    //去除运算操作符
                    .trim() // 去掉前后的空格
                    .split("[ ]+"); // 空格分割
            StringBuffer args = new StringBuffer();
            for (int i=0; i<vars.length; i++) {
                if (args.length()>0) {
                    args.append(",");
                }
                args.append(vars[i]);
            }
            function = String.format("var exp = function(%s) {return %s;};", args.toString(), e);
            args.setLength(0);
            for (int i=0; i<vars.length; i++) {
                if (args.length()>0) {
                    args.append(",");
                }
                args.append("var" + (i+1));
            }
            call = String.format("exp(%s);", args.toString());
            return this;
        }

        /** 运算函数和调用函数需要分别设置 */
        public Builder fun(String f) {
            function = f;
            return this;
        }

        /** 运算函数和调用函数需要分别设置 */
        public Builder call(String c) {
            call = c;
            return this;
        }
        public Expression build() {
            if (function ==null || call == null)
                throw new IllegalArgumentException("function or call is null.");
            return new Expression(this);
        }
    }
}
