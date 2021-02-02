package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.entity.Result;
import com.atguigu.service.MemberService;
import com.atguigu.service.ReportService;
import com.atguigu.service.SetmealService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    MemberService memberService;

    @Reference
    SetmealService setmealService;

    @Reference
    ReportService reportService;

    /**
     * 导出Excel报表
     * @return
     */
    @RequestMapping("/exportBusinessReport")
    public void exportBusinessReport(HttpServletRequest request, HttpServletResponse response){
        try {
            //1.拿数据
            //远程调用报表服务获取报表数据
            Map result = reportService.getBusinessReportData();
            //取出返回结果数据，准备将报表数据写入到Excel文件中
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            //2.获取模板文件
            //获得Excel模板文件绝对路径
            //file.separator这个代表系统目录中的间隔符，说白了就是斜线。
            String filePath = request.getSession().getServletContext().getRealPath("template")+ File.separator+"report_template.xlsx";
            //3.工作簿
            //读取模板文件创建Excel表格对象
            Workbook workbook = new XSSFWorkbook(new File(filePath));

            //4.写数据
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);//日期

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数（本日）
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日出游数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周出游数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月出游数

            int rowNum = 12;
            for(Map map : hotSetmeal){//热门套餐
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheet.getRow(rowNum ++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }

            //5.输出文件。以流形式文件下载，另存为操作
            //通过输出流进行文件下载
            ServletOutputStream out = response.getOutputStream();
            // 下载的数据类型（excel类型）
            response.setContentType("application/vnd.ms-excel");
            // 设置下载形式(通过附件的形式下载),设置头
            //content-Disposition  内容的位置
            //attachment;filename=report.xlsx  附录、附件;下载时的默认名字
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");

            workbook.write(out);//写给了浏览器，文件下载
            //6.关闭流
            out.flush();
            out.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            //跳转错误页面
            try {
                request.getRequestDispatcher("/pages/error/downloadError.html").forward(request, response);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        try {
            Map map = reportService.getBusinessReportData();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport(){
        try {
            List<String> setmealNames = new ArrayList<>();
            List<Map> setmealCount = setmealService.getSetmealReport();

            for (Map map : setmealCount) {
                String setmealName = (String) map.get("name");
                setmealNames.add(setmealName);
            }

            Map map = new HashMap();
            map.put("setmealNames", setmealNames);
            map.put("setmealCount", setmealCount);
            return new Result(true, MessageConstant.QUERY_SETMEALLIST_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, MessageConstant.QUERY_SETMEALLIST_FAIL);
        }
    }

    @RequestMapping("/getMemberReport")
    public Result getMemberReport(){

        try {
            List<String> months = new ArrayList<>();
            List<Integer> memberCount = null;
            // 获取日历对象
            Calendar calendar = Calendar.getInstance();
            //根据当前时间，获取前12个月的日历(当前日历2020-02，12个月前，日历时间2019-03)
            //第一个参数，日历字段
            //第二个参数，要添加到字段中的日期或时间
            calendar.add(Calendar.MONTH,-12);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            for (int i = 0; i < 12; i++) {
                //第一个参数是月份 2018-7
                //第二个参数是月份+1个月
                calendar.add(Calendar.MONTH, 1);
                Date time = calendar.getTime();
                String month = sdf.format(time);
                months.add(month);
            }
            memberCount = memberService.findMemberCountByMonth(months);

            Map map = new HashMap();
            map.put("months", months);
            map.put("memberCount", memberCount);

            return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL);

        }
    }
}
