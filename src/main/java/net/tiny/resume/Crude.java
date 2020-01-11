package net.tiny.resume;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 简历文档原始扫描文本
 * 输出JSON格式的位置和文本信息
 * <code>
 * {
 *   "language" : "ja",
 *   "contents" : [
 *    {
 *     "p" : [1, 2],
 *     "t" : "氏名",
 *     "a" : "Skip"
 *    },
 *    {
 *     "p" : [1, 3],
 *     "t" : "张三",
 *     "a" : "Name:0.99"
 *    }
 *   ]
 * }
 * </code>
 */
public class Crude {

    public static class Content implements Comparable<Content>, Comparator<Content> {
        public int[] p = new int[2]; //位置
        public String t;             //文本
        public String a;             //属性
        public String k;             //关键词
        public String s;             //摘要
        public Content() {}
        Content(final int x, final int y, final String value) {
            p[0] = x;
            p[1] = y;
            t = value;
        }
        @Override
        public int compareTo(Content o) {
            if (p[1] > o.p[1]) return 1;
            if (p[1] < o.p[1]) return -1;
            if (p[0] > o.p[0]) return 1;
            if (p[0] < o.p[0]) return -1;
            return 0;
        }

        @Override
        public int compare(Content o1, Content o2) {
            return o1.compareTo(o2);
        }
        @Override
        public String toString() {
            return String.format("%d:%d %s", p[0], p[1], s != null ? s : t);
        }
    }

    public String id;
    public String language;
    public String format;
    public List<Content> contents = new ArrayList<>();

    public void append(final int x, final int y, final String value) {
        contents.add(new Content(x, y, toLongText(value)));
    }

    /**
     * 文本为记号时，查找同列上边或，同行左边的项目非文本。
     * 非记号时，直接返回文本
     * @param source
     * @return
     */
    public String meaning(Content content) {
        if ( hasMeanings(content.t) )
            return content.t;
        int pos = contents.indexOf(content);
        int i = pos;
        while (i > 0) {
            Content c = contents.get(--i);
            if (c.p[1] < content.p[1] && c.p[0] == content.p[0]) { //同列上边的项目
                if (hasMeanings(c.t)) { //非记号
                    return c.t;
                }
            }
        }

        //TODO 同行左边的项目
        i = pos;
        while (i > 0) {
            Content c = contents.get(--i);
            if (c.p[1] == content.p[1] && c.p[0] < content.p[0]) { //同行左边的项目
                if (hasMeanings(c.t)) { //非记号
                    return c.t;
                }
            } else if (c.p[1] < content.p[1]) {
                break; //不同行时结束
            }
        }
        return null;
    }

    void print(PrintStream out) {
        out.println(language != null ? language : "");
        for (Content c : contents) {
            String t = Pattern.compile("[\\h]+").matcher(c.t).replaceAll("");
            out.print(t + " ");
        }
    }

    @Override
    public String toString() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        print(new PrintStream(out));
        return new String(out.toByteArray());
    }

    /**
     * 过滤空格换行等字符，返回变换后的长文本。
     * @param value
     * @return
     */
    static String toLongText(final String value) {
        return value.replaceAll("[\\t\\n\\r]", " ").replaceAll("\\s+", " ");
    }

    /**
     * 判断是否为记号
     * @param source
     * @return
     */
    public static boolean isSymbol(String term) {
        if (term == null || term.isEmpty())
            return false;
        if (term.toCharArray().length != 1) {
            return false;
        }
        return (UnicodeBlock.of( term.charAt(0) ) == UnicodeBlock.GEOMETRIC_SHAPES);
    }

    /**
     * 判断是否为记号
     * @param source
     * @return
     */
    public static boolean hasMeanings(String term) {
        if (term == null || term.isEmpty())
            return false;
        if (term.toCharArray().length == 1) {
            final UnicodeBlock ub = UnicodeBlock.of( term.charAt(0) );
            return (ub != UnicodeBlock.GEOMETRIC_SHAPES &&
                    ub != UnicodeBlock.BASIC_LATIN &&
                    ub != UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION &&
                    ub != UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS);
        }
        return true;

    }
}
