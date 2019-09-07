package net.tiny.resume;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.tiny.config.JsonParser;

public class PoliciesTest {

    @Test // 枚举计算方式
    public void testEnumeration() throws Exception {
        Policies.Enumeration enumeration = new Policies.Enumeration();
        enumeration.name = "Education"; //计算学历的分值
        List<Policies.Value> values = new ArrayList<>();
        values.add(new Policies.Value("Junior", 10.0d)); //高中
        values.add(new Policies.Value("Vocational", 15.0d));//职高
        values.add(new Policies.Value("University", 30.0d));//大学
        values.add(new Policies.Value("Postgraduate", 40.0d));//研究生
        values.add(new Policies.Value("Doctor", 50.0d)); //博士
        values.add(new Policies.Value("Postdoctoral", 55.0d)); //博士后
        enumeration.values = values;

        String json = JsonParser.marshal(enumeration);
        System.out.print(json);

        assertEquals(10.0d, enumeration.apply("Junior"));
        assertEquals(30.0d, enumeration.apply("University"));
        assertEquals(55.0d, enumeration.apply("Postdoctoral"));

        //导入外部数据包
        Reader reader = new FileReader(new File("src/test/resources/json/enum-education.json"));
        Policies.Enumeration pe = JsonParser.unmarshal(reader, Policies.Enumeration .class);
        assertNotNull(pe);
        reader.close();

        assertEquals(10.0d, pe.apply("Junior"));
        assertEquals(30.0d, pe.apply("University"));
        assertEquals(55.0d, pe.apply("Postdoctoral"));
    }


    @Test // 线性函数计算方式
    public void testLinear() throws Exception {
        //导入外部数据包
        Reader reader = new FileReader(new File("src/test/resources/json/linear-age.json"));
        Policies.Linear linear = JsonParser.unmarshal(reader, Policies.Linear .class);
        assertNotNull(linear);
        reader.close();

        assertEquals("Age", linear.name);
        assertEquals("value / 65.0 * 0.05", linear.expression);
        //计算工龄的分值
        assertEquals(0.03076923076923077d, linear.apply("40"));
        assertEquals(0.03461538461538462d, linear.apply("45.0"));
        assertEquals(0.038461538461538464d, linear.apply("50.0"));

        String json = JsonParser.marshal(linear);
        System.out.print(json);
    }
}
