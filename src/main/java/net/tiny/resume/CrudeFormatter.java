package net.tiny.resume;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 简历文档原始文本格式化
 */
public class CrudeFormatter {

    // 日语正则表达式示例 @see https://engineer-club.jp/java-pattern
    private static Pattern BLANK_PATTERN = Pattern.compile("[\\h]+");

    /**
     * 按输入源的格式，对分散项目合并整形
     * @param source
     * @return
     */
    public static Crude format(Crude source) {
        Crude formatted = new Crude();
        formatted.language = source.language;
        formatted.format = source.format;
        switch (formatted.format) {
        case "doc" :
        case "docx" :
            formatted.contents = mergeWord(source.contents);
            break;
        case "xls" :
            break;
        case "pdf" :
            break;
        }
        return formatted;
    }

    /**
     * 对非文本的记号根据同列上，同行左的项目名进行属性标注 "Symbol:{词义}"
     *
     * @param source
     * @return
     */
    public static Crude meanings(Crude source) {
        Iterator<Crude.Content> src = source.contents.iterator();
        while (src.hasNext()) {
            Crude.Content c = src.next();
            if (Crude.isSymbol(c.t)) {
                c.a = new String("Symbol:").concat(source.meaning(c));
            }
        }
        return source;
    }

    /**
     * 过滤掉空项目
     * @param source
     * @return
     */
    public static Crude clean(Crude source) {
        List<Crude.Content> list = new ArrayList<>();
        Iterator<Crude.Content> src = source.contents.iterator();
        while (src.hasNext()) {
            Crude.Content c = src.next();
            c.t = strip(c.t);
            if (c.t != null && !c.t.isEmpty()) {
                list.add(c);
            }
        }
        source.contents = list;
        return source;
    }

    /**
     * 过滤连续空格
     * @param source
     * @return
     */
    public static String strip(String base) {
        return BLANK_PATTERN.matcher(base).replaceAll("").trim();
    }


    //对Word文件的同一行连续项目进行合并
    static List<Crude.Content> mergeWord(List<Crude.Content> source) {
        List<Crude.Content> list = new ArrayList<>();
        int col = -1;

        Crude.Content last = null;
        Iterator<Crude.Content> src = source.iterator();
        while (src.hasNext()) {
            Crude.Content c = src.next();
            if (last != null) {
                if (c.p[1] == last.p[1] && c.p[0] == ++col) {
                    last.t = last.t.concat(c.t); //Merge
                } else {
                    list.add(last);
                    last = c;
                    col = last.p[0];
                }
            } else {
                last = c;
                col = last.p[0];
            }
        }
        if (null != last) {
            list.add(last);
        }
        return list;
    }
}
