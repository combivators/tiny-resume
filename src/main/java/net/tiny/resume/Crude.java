package net.tiny.resume;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * 简历文档原始扫描文本
 * 输出JSON格式的位置和文本信息
 * <code>
 * {
 *   "contents" : [
 *    {
 *     "x" : 1,
 *     "y" : 0,
 *     "s" : "氏名"
 *    },
 *   ]
 * }
 * </code>
 */
public class Crude {

    public static class Content implements Comparable<Content>, Comparator<Content> {
        public int x;
        public int y;
        public String s;
        public Content() {}

        Content(final int c, final int r, final String value) {
            x = c;
            y = r;
            s = value;
        }
        @Override
        public int compareTo(Content o) {
            if (y > o.y) return 1;
            if (y < o.y) return -1;
            if (x > o.x) return 1;
            if (x < o.x) return -1;
            return 0;
        }

        @Override
        public int compare(Content o1, Content o2) {
            return o1.compareTo(o2);
        }
        @Override
        public String toString() {
            return String.format("%d:%d %s", x, y, s);
        }
    }

    public Set<Content> contents = new TreeSet<>();

    public void append(final int x, final int y, final String value) {
        contents.add(new Content(x, y, toLongText(value)));
    }

    String toLongText(final String value) {
        return value.replaceAll("[\\t\\n\\r]", " ").replaceAll("\\s+", " ");
    }
}
