package com.echo;

/**
 * @Author jianggx
 * @CreateDate 2020/1/4
 * @Description 身份证信息提取器 测试类
 */
public class IdCardInfoExtractorTest {

    public static void main(String[] args) {

        IdCardInfoExtractor idCardInfoExtractor = new IdCardInfoExtractor("110101199406231238");

        System.out.println("出生日期:" + idCardInfoExtractor.getYear() + "-" + idCardInfoExtractor.getMonth() + "-" + idCardInfoExtractor.getDay());
        System.out.println("性别:" + idCardInfoExtractor.getGender());
        System.out.println("年龄:" + idCardInfoExtractor.getAge());
        System.out.println("省份:" + idCardInfoExtractor.getProvince());

    }
}
