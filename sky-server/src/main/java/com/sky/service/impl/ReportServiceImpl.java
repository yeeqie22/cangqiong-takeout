package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额统计
     *
     * @param begain
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnOverStatistics(LocalDate begain, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begain);
        while (!begain.equals(end)) {
            begain = begain.plusDays(1);
            dateList.add(begain);
        }
        //每日营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Double turnover = orderMapper.sumByTimeAndStatus(beginTime, endTime);
            turnover = turnover == null ? 0 : turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //每日新增商家
        List<Integer> newUserList = new ArrayList<>();
        //商家总数
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //一天的开始时刻和结束时刻
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map<String, LocalDateTime> map = new HashMap<>();
            map.put("endTime", endTime);
            //获取总用户数
            Integer totalUserNumber = userMapper.getUserNumberByMap(map);
            //totalUserNumber = totalUserNumber == null ? 0 : totalUserNumber;
            totalUserList.add(totalUserNumber);
            map.put("beginTime", beginTime);
            //获取新增用户数
            Integer newUserNumber = userMapper.getUserNumberByMap(map);
            //newUserNumber = newUserNumber == null ? 0 : newUserNumber;
            newUserList.add(newUserNumber);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //每天订单总数
        List<Integer> totalOrdersNumList = new ArrayList<>();
        //每天有效订单数（已完成的订单）
        List<Integer> validOrdersNumList = new ArrayList<>();
        //订单总数
        Integer ordersCount = 0;
        //有效订单总数
        Integer validOrdersCount = 0;

        for (LocalDate date : dateList) {
            //一天的开始和结束时刻
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();

            //获取每天订单数并累加到订单总数
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer totalOrderNum = orderMapper.getOrdersCountByMap(map);
            ordersCount += totalOrderNum;
            totalOrdersNumList.add(totalOrderNum);

            //获取每天有效订单数并且累加到有效订单总数
            map.put("status", Orders.COMPLETED);
            Integer validOrdersNum = orderMapper.getOrdersCountByMap(map);
            validOrdersCount += validOrdersNum;
            validOrdersNumList.add(validOrdersNum);
        }
        Double orderCompletionRate = 0.0;
        if (ordersCount != 0) {
            orderCompletionRate = validOrdersCount.doubleValue() / ordersCount;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(ordersCount)
                .validOrderCount(validOrdersCount)
                .orderCountList(StringUtils.join(totalOrdersNumList, ","))
                .validOrderCountList(StringUtils.join(validOrdersNumList, ","))
                .build();
    }
}
