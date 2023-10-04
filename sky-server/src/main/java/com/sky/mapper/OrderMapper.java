package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单
     *
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 分页条件查询并按下单时间排序
     *
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     *
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    /**
     * 更新订单信息
     *
     * @param orders
     */
    //TODO 补全sql语句
    void update(Orders orders);

    /**
     * 根据状态统计订单数量
     *
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 根据订单状态和下单时间查询订单
     *
     * @param status
     * @param time
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLt(Integer status, LocalDateTime time);

    /**
     * 获取当天营业额
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    Double sumByTimeAndStatus(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 根据订单创建时间和状态获取订单数目
     *
     * @param map
     * @return
     */
    Integer getOrdersCountByMap(Map map);

    /**
     * 根据起止时间查询销量前十菜品的名称和销量
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("select od.name,sum(od.number) number from order_detail od,orders o where od.order_id = o.id and o.status = 5 and order_time > #{beginTime} and order_time < #{endTime} group by name limit 0,10")
    List<GoodsSalesDTO> getOrdersTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
