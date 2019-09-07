package net.tiny.resume;

import java.util.List;

/**
 * Points : 用于评价计算的资历点数
 * JSON格式大小约360字节
 */
public final class Points {

    public static final String VERSION = Version.CURRENT.ver();

    //积分
    public static class Point {
        public String name;
        public double score = 0.0d;
    }

    //积分表
    public static class Table {
        public String ver = VERSION;
        public String id;
        public int[] orders;
        public String[] titles;
        public Double[] scores;
        public long modified = System.currentTimeMillis();
        public String signature;
    }

    public String ver = VERSION;
    public String id;
    public List<Point> points;
    public long modified = System.currentTimeMillis();
    public String signature;

    /**
     * 评估项目数值
     * @param name
     * @return
     */
    public double score(String name) {
        for (Point as : points) {
            if (as.name.equals(name)) {
                return as.score;
            }
        }
        return 0.0d;
    }

    public String[] titles() {
        if (null == points || points.isEmpty()) {
            return null;
        }
        int size = points.size();
        String[] values = new String[size];
        for (int i=0; i<points.size(); i++) {
            values[i] = points.get(i).name;
        }
        return values;
    }

    /**
     * 评估数值矩阵
     * @return
     */
    public Double[] scores() {
        if (null == points || points.isEmpty()) {
            return null;
        }
        int size = points.size();
        Double[] values = new Double[size];
        for (int i=0; i<points.size(); i++) {
            values[i] = points.get(i).score;
        }
        return values;
    }

    public Table toTable() {
        Table table = new Table();
        table.id = id;
        table.signature = signature;
        table.titles = titles();
        table.scores = scores();
        return table;
    }

}
