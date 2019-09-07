package net.tiny.resume;

public class Resume {

    //文档类别
    public static enum Type {
        Text,
        Excel,
        Word,
        PDF
    }

    public static enum Title {
        Name,          //姓名
        Sex,           //性别
        Brith,         //生日
        Nationality,   //国籍
        Religion,      //宗教
        Civil,         //婚姻
        Visa,          //签证
        Education,     //最终学历
        Graduation,    //毕业学校
        Specialty,     //专业
        Income,        //收入
        Certification, //认证资格
        Abroad,        //海外年数
        Changes,       //跳槽
        Duties,        //勤务
        Careers,       //职业经历
        Experience,    //开发经验
        Fields,        //工作领域
        Language;      //外语水平
    }

    //extraction 抽出物 extract
    //resolver 分解器
    //acceptance 验收
    //Recognition rate 识别率
}
