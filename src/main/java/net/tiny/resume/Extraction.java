package net.tiny.resume;

import java.util.List;

/**
 * Extraction 简历文档提取物文本
 * JSON格式大小约2K字节
 */
public final class Extraction {
    public static final String VERSION = Version.CURRENT.ver();

    //类别
    public static enum Type {
        Personal,  //个人资料
        Education, //教育背景 (Training)
        Skill,     //技能 (Languages, Software, Specific, Licenses, Abilities)
        Experience,//工作经验 (Highlights)
        Hobbies,   //爱好 (Activities)
        Awards,    //受奖成就荣誉（Accomplishment, Honors)
        Objectives,//目标
        Contact,   //联系
        Reference  //参考
    }

    //信息描述 Description
    static abstract class Description {
        public String description;
    }
    //属性集合 Attribute
    static abstract class Attribute extends Description {
        public int order;
        public String type;
    }

    //个人基本属性 Personal
    public static class Personal {
        public String name;        //姓名
        public String sex;         //性别
        public String brith;       //生日
        public String nationality; //国籍
        public String civil;       //婚姻
        public String religion;    //宗教
        public String family;      //家族
    }

    //联系 Contact
    public static class Contact extends Attribute {
        public boolean encode = true;
    }

    public static enum EducationLevel {
        Junior,
        Vocational,
        University,
        Postgraduate,
        Doctor,
        Postdoctoral
    }

    public static enum Rank {
        normal,
        hight,
        top
    }

    //教育背景 Education
    public static class Education extends Attribute {
        public String begin;
        public String end;
        public EducationLevel level;
        public Rank rank;
    }

    //技能 Skill (Languages, Software, Specific, Licenses, Abilities)
    public static class Skill extends Attribute {
        public Rank rank;
    }

    //工程阶段 Phase
    public static class Phase extends Attribute {}
    //工作经历 Experience
    public static class Experience extends Attribute {
        public String begin;
        public String end;
        public String os;
        public String software;
        public String duty; //Like SE,TL
        public List<Phase> phases;
    }

    //受奖荣誉 Awards
    public static class Awards extends Attribute {}
    //爱好 Hobbies
    public static class Hobby extends Attribute {}
    //目标 Objectives
    public static class Objectives extends Description {}
    //参考 Reference
    public static class Reference extends Description {}

    public String ver = VERSION;
    public String id;
    /** 个人资料 */
    public Personal personal;
    /** 联系地址 */
    public List<Contact> contacts;
    /** 教育背景 */
    public List<Education> educations;
    /** 技能 */
    public List<Skill> skills;
    /** 工作经历 */
    public List<Experience> experiences;
    /** 受奖荣誉 */
    public List<Awards> awards;
    /** 爱好 */
    public List<Hobby> Hobbies;
    /** 目标 */
    public Objectives objectives;
    /** 参考 */
    public Reference reference;
}
