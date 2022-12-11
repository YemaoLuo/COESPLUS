package com.coesplus.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coesplus.admin.service.AdministratorService;
import com.coesplus.admin.vo.AdministratorVo;
import com.coesplus.common.entity.Administrator;
import com.coesplus.common.utils.BaseContext;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/administrator")
@Slf4j
public class AdministratorController {

    @Resource
    private AdministratorService administratorService;


    /**
     * 分页查询
     */
    @GetMapping()
    public Result page(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize) {
        try {
            Page page = new Page(currentPage, pageSize);
            LambdaQueryWrapper<Administrator> administratorQueryWrapper = new LambdaQueryWrapper();
            administratorQueryWrapper.eq(Administrator::getIsDeleted, 0)//选出非删除的
                    .orderBy(true, false, Administrator::getUpdateTime);
            page = administratorService.page(page, administratorQueryWrapper);//把非删除的放入page
            List<Administrator> administratorList = page.getRecords();
            List<AdministratorVo> administratorVoList = new ArrayList<>();

            administratorList.forEach(administrator -> {
                AdministratorVo vo = new AdministratorVo();
                BeanUtils.copyProperties(administrator, vo);

                administratorVoList.add(vo);
            });

            log.info(currentPage + "::" + pageSize + "::" + administratorVoList);
            page.setRecords(administratorVoList);
            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增Admin name，email，telephone均不可重复
     *
     * @author LuoYemao
     * @since 2022/10/18 0:05
     */
    @PostMapping
    public Result add(@RequestBody Administrator administratorVo) {
        try {
            return administratorService.add(administratorVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新
     *
     * @author LuoYemao
     * @since 2022/10/20 0:05
     */
    @PatchMapping()
    public Result update(@RequestBody Administrator administratorVo) {
        try {
            //校验是否更改的为自己
            Administrator currentAdmin = (Administrator) BaseContext.getValue("currentAdmin");
            if (!currentAdmin.getId().equals(administratorVo.getId())) {
                return Result.error("您没有权限修改其他管理员信息！");
            }
            //Vo -> Entity
            Administrator administrator = new Administrator();
            BeanUtils.copyProperties(administratorVo, administrator);
            //校验name，email，telephone均不可重复
            LambdaQueryWrapper<Administrator> administratorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            administratorLambdaQueryWrapper.or()
                    .eq(Administrator::getName, administrator.getName())
                    .eq(Administrator::getEmail, administrator.getEmail())
                    .eq(Administrator::getTelephone, administrator.getTelephone());
            long count = administratorService.count(administratorLambdaQueryWrapper);
            if (count > 0) {
                return Result.error("姓名，邮箱，电话不唯一！请重新设置！");
            } else {
                administratorService.updateById(administrator);
                return Result.ok();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}


