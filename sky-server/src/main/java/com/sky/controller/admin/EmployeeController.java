package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登陆")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 添加员工
     *
     * @param employeeDTO
     * @return
     */
    @ApiOperation("新增员工")
    @PostMapping()
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询，数据为{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.page(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用员工账号
     *
     * @return
     */
    @ApiOperation("启用禁用员工账号")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable("status") Integer status, Long id) {
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id回显员工
     * @return
     */
    @ApiOperation("根据id回显员工")
    @GetMapping("{id}")
    public Result<Employee> getById(@PathVariable("id") Integer id){
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 更新员工信息
     * @param employeeDTO
     * @return
     */
    @ApiOperation("更新员工信息")
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        employeeService.update(employeeDTO);
        return Result.success();
    }


}
