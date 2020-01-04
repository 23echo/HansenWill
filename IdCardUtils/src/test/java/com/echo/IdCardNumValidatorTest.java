package com.echo;

/**
 * @Author jianggx
 * @CreateDate 2020/1/4
 * @Description 身份证号码校验器 测试类
 */
public class IdCardNumValidatorTest {

    public static void main(String[] args) {

        IdCardNumValidator idCardNumValidator = new IdCardNumValidator();

//        String idCard = "110101940623123";
//        String idCard = "110101199406231238";
//        String idCard = "110101140623123";
        String idCard = "110101191406231238";

        //第四个条件不准
        System.out.println("是15位吗（1）"+idCardNumValidator.is15IdCard(idCard));
        System.out.println("是18位吗（1）"+idCardNumValidator.is18IdCard(idCard));
        System.out.println("15 -> 18 "+idCardNumValidator.idCardNum15TransTo18(idCard));
        System.out.println("是正确的吗 "+idCardNumValidator.isIdCard(idCard));
        System.out.println("是合法的吗 "+idCardNumValidator.isValidatedAllIdCard(idCard));
        System.out.println("是15位吗（2）"+idCardNumValidator.isIdNumBit15(idCard));
        //第四个条件不准
        System.out.println("是18位吗（2）"+idCardNumValidator.isIdNumBit18(idCard));


    }
}
