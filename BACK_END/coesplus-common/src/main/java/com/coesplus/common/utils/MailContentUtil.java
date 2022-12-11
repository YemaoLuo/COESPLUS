package com.coesplus.common.utils;

public class MailContentUtil {

   public static String build(String subject, String content, String destination) {
      StringBuilder sb = new StringBuilder();
      sb.append("<head><meta charset=\"utf-8\"><meta http-equiv=\"X-UA-Compatible\" ><meta name=\"viewport\" " +
              "content=\"width=device-width, initial-scale=0\"><title>mail</title><link " +
              "href=\"https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css\" " +
              "rel=\"stylesheet\"></head><body style=\"color: #666; font-size: 14px; font-family: " +
              "'Open Sans',Helvetica,Arial,sans-serif;\"><div class=\"box-content\" style=\"width: 80%; " +
              "margin: 20px auto; max-width: 800px; min-width: 600px;\"><div class=\"header-tip\" " +
              "style=\"font-size: 12px;color: #000000;text-align: right;padding-right: 25px;padding-bottom: 10px;\">" +
              "</div><div class=\"info-top\" style=\"padding: 15px 25px;border-top-left-radius: 10px;" +
              "border-top-right-radius: 10px;background: #6495ED;overflow: hidden;line-height: 32px;\">" +
              "<span class=\"glyphicon glyphicon-envelope\"></span><span style=\"font-size: 18px\">\t");
      sb.append(subject);
      sb.append("</span></div><div class=\"info-wrap\" style=\"border-bottom-left-radius: 10px;" +
              "border-bottom-right-radius: 10px;border:1px solid #ddd;overflow: hidden;padding: 15px 15px 20px;\">" +
              "<div class=\"tips\" style=\"padding:15px;\"><p style=\" list-style: 160%; margin: 10px 0;" +
              "font-size: 20px\"><strong>");
      sb.append(destination);
      sb.append(":</strong></p><p style=\" list-style: 160%; margin: 10px 0;\"><br>");
      sb.append(content);
      sb.append("</p></div><div class=\"time\" style=\"text-align: right; color: #999; padding: 0 15px 15px;\">" +
              "</div><div style=\"background-color: #6495ED; border-bottom-left-radius: 10px;" +
              "border-bottom-right-radius: 10px;text-align: center;border-top-left-radius: 10px;" +
              "border-top-right-radius: 10px;\"><img src=\"https://www.must.edu.mo/images/logo_new.png\" " +
              "style=\"margin: 5px; width: 35%\"><img src=\"https://www.must.edu.mo/images/newui/new-white-logo.png\" " +
              "style=\"margin: 5px; width: 35%\"></div></div></div><script " +
              "src=\"https://cdn.jsdelivr.net/npm/jquery@1.12.4/dist/jquery.min.js\"></script><script " +
              "src=\"https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/js/bootstrap.min.js\"></script></body></html>");
      return sb.toString();
   }
}
