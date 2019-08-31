package net.tiny.resume;

import java.util.List;
import java.util.function.BiFunction;

import net.tiny.resume.Extraction.Education;

/**
 * Digitalization :  系统AI策略对简历文档提取文本进行数值化处理
 * 数值化结果Points传给Appraiser再进行评估处理
 */
public class Digitalization implements BiFunction<Policies, Extraction, Points> {

    @Override
    public Points apply(Policies policies, Extraction extraction) {
        Points points = new Points();
        points.id = extraction.id;
        // TODO
        //extraction.educations;
        List<Education> educations;
//        Policies.Operator operator = policies.getOperator("Education");
//        double score = operator.apply(value);
//        policies.setScore("Education", score);
        return points;
    }
}
