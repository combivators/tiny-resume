package net.tiny.resume;

import java.util.List;

/**
 * 基于简历文本计算评价项目的策略
 *
 */
public final class Policies {

    public static final String VERSION = Version.CURRENT.ver();

    //计算方式
    public static enum Arithmetic {
        Enumeration,   // 枚举计算方式
        Linear,        // 线性计算方式
        WordsOperator, // 自然语言计算方式
    }

    public static class Value {
        public String name;
        public double value = 0.0d;
        public Value() {}
        public Value(String n, double v) {
            name = n;
            value= v;
        }
    }

    // 计算方式接口
    public static interface Operator {
        double apply(String name);
    }

    // 枚举计算方式
    public static class Enumeration implements Operator {
        public String name;
        public List<Value> values;

        @Override
        public double apply(String name) {
            return values.stream()
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElseGet(() -> {return new Value();})
                .value;
        }
    }

    // 线性函数计算方式
    public static class Linear implements Operator {
        public String name;
        public String expression;
        private transient Expression _exp;

        public void setExpression(String exp) {
            expression = exp;
            _exp = new Expression.Builder()
                    .exp(expression)
                    .build();
        }

        private Expression getExp() {
            if (null == _exp) {
                _exp = new Expression.Builder()
                        .exp(expression)
                        .build();
            }
            return _exp;
        }

        @Override
        public double apply(String value) {
            return getExp().evaluate(Double.parseDouble(value));
        }
    }

    // 自然语言计算方式
    public static class WordsOperator implements Operator {
        //TODO
        @Override
        public double apply(String text) {
            return 0.0d;
        }
    }

    public String ver = VERSION;
    public String id;
    public List<Enumeration> enumerations;
    public List<Linear> linears;
    public List<WordsOperator> words;
}
