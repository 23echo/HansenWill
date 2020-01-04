package com.echo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

/**
 * @Author jianggx
 * @CreateDate 2020/1/4
 * @Description 身份证号码校验器
 */
public class IdCardNumValidator {

    /*
    {"11", "北京"}, {"12", "天津"}, {"13", "河北"}, {"14", "山西"}, {"15", "内蒙古"},
    {"21", "辽宁"}, {"22", "吉林"}, {"23", "黑龙江"},
    {"31", "上海"}, {"32", "江苏"}, {"33", "浙江"}, {"34", "安徽"}, {"35", "福建"}, {"36", "江西"}, {"37", "山东"},
    {"41", "河南"}, {"42", "湖北"}, {"43", "湖南"}, {"44", "广东"}, {"45", "广西"}, {"46", "海南"},
    {"50", "重庆"}, {"51", "四川"}, {"52", "贵州"}, {"53", "云南"}, {"54", "西藏"},
    {"61", "陕西"}, {"62", "甘肃"}, {"63", "青海"}, {"64", "宁夏"}, {"65", "新疆"},
    {"71", "台湾"}, {"81", "香港"}, {"82", "澳门"}, {"91", "国外"}
    */

    private String[] cityCode = {
            "11", "12", "13", "14", "15",
            "21", "22", "23",
            "31", "32", "33", "34", "35", "36", "37",
            "41", "42", "43", "44", "45", "46",
            "50", "51", "52", "53", "54",
            "61", "62", "63", "64", "65",
            "71", "81", "82", "91"
    };

    /**
     * 每位加权因子
     */
    private int[] power = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * 第18位校检码
     */
    private String[] verifyCode = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};

    /**
     * 第一代身份证位数
     */
    private int oldIdNumBit = 15;

    /**
     * 第二代身份证位数
     */
    private int newIdNumBit = 18;

    /**
     * 验证身份证合法性
     *
     * @param idCard
     * @return
     */
    public boolean isValidatedAllIdCard(String idCard) {
        if (idCard.length() == oldIdNumBit) {
            idCard = this.idCardNum15TransTo18(idCard);
        }
        return this.isIdNumBit18(idCard);
    }

    /**
     * 判断18位身份证的合法性
     * 根据〖中华人民共和国国家标准GB11643-1999〗中有关公民身份号码的规定，公民身份号码是特征组合码，由十七位数字本体码和一位数字校验码组成。
     * 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
     * <p>
     * 顺序码: 表示在同一地址码所标识的区域范围内，对同年、同月、同 日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配 给女性。
     * 1.前1、2位数字表示：所在省份的代码； 2.第3、4位数字表示：所在城市的代码； 3.第5、6位数字表示：所在区县的代码；
     * 4.第7~14位数字表示：出生年、月、日； 5.第15、16位数字表示：所在地的派出所的代码；
     * 6.第17位数字表示性别：奇数表示男性，偶数表示女性；
     * 7.第18位数字是校检码：也有的说是个人信息码，一般是随计算机的随机产生，用来检验身份证的正确性。校检码可以是0~9的数字，有时也用x表示。
     * <p>
     * 第十八位数字(校验码)的计算方法为：
     * 1.将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
     * 2.将这17位数字和系数相乘的结果相加。
     * 3.用加出来和除以11，看余数是多少？
     * 4.余数只可能有0 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3 2。
     * 5.通过上面得知如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ。如果余数是10，身份证的最后一位号码就是2。
     *
     * @param idCard
     * @return
     */
    public boolean isIdNumBit18(String idCard) {
        // 非18位为假
        if (idCard.length() != newIdNumBit) {
            return false;
        }
        // 获取前17位
        String idCard17 = idCard.substring(0, 17);
        // 获取第18位
        String idCard18Code = idCard.substring(17, 18);
        char[] c = null;
        String checkCode = "";
        // 是否都为数字
        if (isDigital(idCard17)) {
            c = idCard17.toCharArray();
        } else {
            return false;
        }

        int[] bit = convertCharToInt(c);
        int sum17 = getPowerSum(bit);

        // 将和值与11取模得到余数进行校验码判断
        checkCode = getCheckCodeBySum(sum17);
        if (null == checkCode) {
            return false;
        }
        // 将身份证的第18位与算出来的校码进行匹配，不相等就为假
        return idCard18Code.equalsIgnoreCase(checkCode);
    }

    /**
     * 验证15位身份证的合法性,该方法验证不准确，最好是将15转为18位后再判断，该类中已提供。
     *
     * @param idCard
     * @return
     */
    public boolean isIdNumBit15(String idCard) {

        // 非15位为假
        if (idCard.length() != oldIdNumBit) {
            return false;
        }

        // 是否全都为数字
        if (isDigital(idCard)) {
            String provinceId = idCard.substring(0, 2);
            String birthday = idCard.substring(6, 12);
            int year = Integer.parseInt(idCard.substring(6, 8));
            int month = Integer.parseInt(idCard.substring(8, 10));
            int day = Integer.parseInt(idCard.substring(10, 12));

            // 判断是否为合法的省份
            boolean flag = false;
            for (String id : cityCode) {
                if (id.equals(provinceId)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return false;
            }
            // 该身份证生出日期在当前日期之后时为假
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("yyMMdd").parse(birthday);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (birthDate == null || new Date().before(birthDate)) {
                return false;
            }

            // 判断是否为合法的年份
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            int currentYear = gregorianCalendar.get(Calendar.YEAR);
            int idCardYearTwoBit = Integer.parseInt(String.valueOf(currentYear).substring(2));

            int minYear = 50;
            // 判断该年份的两位表示法，小于50的和大于当前年份的，为假
            if ((year < minYear && year > idCardYearTwoBit)) {
                return false;
            }

            int minMonth = 1;
            int maxMonth = 12;
            // 判断是否为合法的月份
            if (month < minMonth || month > maxMonth) {
                return false;
            }

            // 判断是否为合法的日期
            boolean isLegalDate = false;
            // 将该身份证的出生日期赋于对象curDay
            gregorianCalendar.setTime(birthDate);
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    isLegalDate = (day >= 1 && day <= 31);
                    break;
                // 平年2月28天，闰年2月29天
                case 2:
                    if (gregorianCalendar.isLeapYear(gregorianCalendar.get(Calendar.YEAR))) {
                        isLegalDate = (day >= 1 && day <= 29);
                    } else {
                        isLegalDate = (day >= 1 && day <= 28);
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    isLegalDate = (day >= 1 && day <= 30);
                    break;
            }
            return isLegalDate;
        } else {
            return false;
        }
    }

    /**
     * 将15位的身份证转成18位身份证
     *
     * @param idCard
     * @return
     */
    public String idCardNum15TransTo18(String idCard) {
        String idCardNum17;
        // 非15位身份证
        if (idCard.length() != oldIdNumBit) {
            return null;
        }

        if (isDigital(idCard)) {
            // 获取出生年月日
            String birthday = idCard.substring(6, 12);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("yyMMdd").parse(birthday);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            assert birthDate != null;
            calendar.setTime(birthDate);
            String year = String.valueOf(calendar.get(Calendar.YEAR));

            idCardNum17 = idCard.substring(0, 6) + year + idCard.substring(8);

            char[] idCardNum17charArray = idCardNum17.toCharArray();

            String checkCode;
            int[] bit;
            int sum17;

            // 将字符数组转为整型数组
            bit = convertCharToInt(idCardNum17charArray);

            sum17 = getPowerSum(bit);

            // 获取和值与11取模得到余数进行校验码
            checkCode = getCheckCodeBySum(sum17);
            // 获取不到校验位
            if (checkCode == null) {
                return null;
            }

            // 将前17位与第18位校验码拼接
            idCardNum17 += checkCode;
        } else {
            // 身份证包含数字
            return null;
        }
        return idCardNum17;
    }

    /**
     * 15位和18位身份证号码的基本数字和位数验校
     *
     * @param idCard
     * @return
     */
    public boolean isIdCard(String idCard) {
        return idCard != null && !"".equals(idCard) && Pattern.matches(
                "(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)", idCard);
    }

    /**
     * 15位身份证号码的基本数字和位数验校
     *
     * @param idCard
     * @return
     */
    public boolean is15IdCard(String idCard) {
        return idCard != null && !"".equals(idCard) && Pattern.matches(
                "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$", idCard);
    }

    /**
     * 18位身份证号码的基本数字和位数验校
     *
     * @param idCard
     * @return
     */
    public boolean is18IdCard(String idCard) {
        return Pattern.matches(
                "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([\\d|x|X]{1})$", idCard);
    }

    /**
     * 数字验证
     *
     * @param str
     * @return
     */
    private boolean isDigital(String str) {
        return str != null && !"".equals(str) && str.matches("^[0-9]*$");
    }

    /**
     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     *
     * @param bit
     * @return
     */
    private int getPowerSum(int[] bit) {

        int sum = 0;

        if (power.length != bit.length) {
            return sum;
        }

        for (int i = 0; i < bit.length; i++) {
            for (int j = 0; j < power.length; j++) {
                if (i == j) {
                    sum = sum + bit[i] * power[j];
                }
            }
        }
        return sum;
    }

    /**
     * 将和值与11取模得到余数进行校验码判断
     *
     * @param sum17
     * @return 校验位
     */
    private String getCheckCodeBySum(int sum17) {
        String checkCode = null;
        switch (sum17 % 11) {
            case 10:
                checkCode = "2";
                break;
            case 9:
                checkCode = "3";
                break;
            case 8:
                checkCode = "4";
                break;
            case 7:
                checkCode = "5";
                break;
            case 6:
                checkCode = "6";
                break;
            case 5:
                checkCode = "7";
                break;
            case 4:
                checkCode = "8";
                break;
            case 3:
                checkCode = "9";
                break;
            case 2:
                checkCode = "x";
                break;
            case 1:
                checkCode = "0";
                break;
            case 0:
                checkCode = "1";
                break;
        }
        return checkCode;
    }

    /**
     * 将字符数组转为整型数组
     *
     * @param c
     * @return
     * @throws NumberFormatException
     */
    private int[] convertCharToInt(char[] c) throws NumberFormatException {
        int[] a = new int[c.length];
        int k = 0;
        for (char temp : c) {
            a[k++] = Integer.parseInt(String.valueOf(temp));
        }
        return a;
    }

}
