package com.coesplus.admin;

import com.coesplus.admin.service.MailSendLogService;
import com.coesplus.common.utils.MailContentUtil;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class MailTest {

    @Resource
    private MailSendLogService mailService;

    @Test
    void testSendSimpleMail() {
        Result result = mailService.sendSimpleMessage("1474964095@qq.com", "测试标题", "test");
        log.info(result.toString());
    }

    @Test
    void testSendComplexMail() {
        Result result = mailService.sendComplexMessage("1474964095@qq.com", "测试标题", "  <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "    <tr>\n" +
                "        <td>\n" +
                "          <table cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: collapse;margin: 0 auto;width: 600px;\">\n" +
                "            <tr>\n" +
                "              <td style=\"padding-top: 60px;padding-left: 20px;padding-right: 20px;\" colspan=\"2\">\n" +
                "                <b>入 职 通 知 书：</b><br />\n" +
                "                <br />\n" +
                "                您好!我们十分荣幸地通知您于×年×月×日到我公司××部门入职，担任××职，入职后试用期月薪为：￥×× 元/税前，转正后月薪为：￥××元/税前，补助￥××元/月，祝贺您即将成为我们公司的一员!关于您就职一事，特有以下事项提醒您做相应准备：\n" +
                "                <br />\n" +
                "                1. 请在上班当天携带身份证原件、复印件，户口簿复印件2份;学历证书、学位证书原件及复印件;6张一寸彩色照片;上一家就职公司离职证明等资料交给人力资源部 ，在部门助理协助下办理入职手续;；<br />\n" +
                "                2. 公司统一规定的上班时间为每周五天，作息时间8：30-17：30，其中12：00-13：30为午饭和休息时间；<br />\n" +
                "                &nbsp;&nbsp;&nbsp;&nbsp;学信网个人学历查询结果打印（在线验证报告）<br />\n" +
                "                3. 公司将为您提供必要的办公环境和办公设备，请自带饮用水杯；<br />\n" +
                "                4. 公司将会按照相关政策规定为您缴纳社会保险<br />\n" +
                "                5. 其它事项说明；<br />\n" +
                "                <div style=\"padding-top: 20px;\">备注：请遵守薪酬保密制度，不得向第三方透露自己的薪酬、福利等相关内容，一经发现公司将根据薪酬保密制度给予严重处分，严重者解除劳动合同.\n" +
                "                </div>\n" +
                "                <div style=\"padding-top: 20px;\">期待您成为我们的新同事！祝您在公司有一个美好的未来！</div>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "              <td colspan=\"2\">\n" +
                "                <div style=\"margin-top: 20px;margin-bottom: 20px;width: 100%;height: 1px;background-color: #acbdd4;\"></div>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "              <td style=\"padding-top:36px;padding-left: 20px;padding-right: 20px;\">\n" +
                "                <b>公司地址：</b><br />\n" +
                "                广州市xxxx<br /><br />\n" +
                "                <b>联系人及联系电话：</b><br />\n" +
                "                xxx<br />\n" +
                "                xxx-xxxxxxxx/xxxxxxxxxxxx<br /><br />\n" +
                "                \n" +
                "                <b>邮箱：</b><br />\n" +
                "                xxxxxxxx\n" +
                "              </td>\n" +
                "              <td style=\"padding-top:36px;\">\n" +
                "                <img src=\"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201901%2F11%2F20190111010348_tuyoj.jpg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1654659574&t=fc39d451f28f58cb346cfc988059ae8e\" alt=\"公司地图\" style=\"width: 280px;height: 186px;\">\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "              <td colspan=\"2\">\n" +
                "                <div style=\"margin-top: 20px;margin-bottom: 20px;border-top: 1px solid #acbdd4;width: 100%;height: 0;\"></div>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "              <td colspan=\"2\" style=\"padding-top:32px;padding-left: 20px;padding-bottom: 20px;\">\n" +
                "                <div style=\"width: 410px;display: inline-block;\">\n" +
                "                  <b>xxx</b>&nbsp;xxxx<br />\n" +
                "                  <b>xxx</b>&nbsp;xxxx<br /><br />\n" +
                "                </div>\n" +
                "                <img src=\"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201901%2F11%2F20190111010348_tuyoj.jpg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1654659574&t=fc39d451f28f58cb346cfc988059ae8e\" alt=\"二维码\" style=\"width:150px;height: 150px;vertical-align: top;\">\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "              <td colspan=\"2\">\n" +
                "                <a href=\"https://www.csdn.net/\" style=\"display: block;\">\n" +
                "                  <img src=\"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fwww.leixue.com%2Fuploads%2F2020%2F03%2FCSDN.png%21760&refer=http%3A%2F%2Fwww.leixue.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1654659829&t=ea09891eeb8d084056f7557929e80bed\" alt=\"底部图\" style=\"width: 598px;height: 75px;\">\n" +
                "                </a>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "  </table>");
        log.info(result.toString());
    }

    @Test
    void testMailContentUtil() {
        String content = MailContentUtil.build("COES-Plus登录预警", "您的账号于中国澳门登陆成功，登录IP发生变化，新登录IP为182.93.0.82，请核实是否为本人登录！", "Mike");
        mailService.sendComplexMessage("leafysn@qq.com", "COES-Plus登录预警", content);
    }
}
