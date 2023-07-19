package com.izumi.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.izumi.wiki.domain.User;
import com.izumi.wiki.domain.UserExample;
import com.izumi.wiki.exception.BusinessException;
import com.izumi.wiki.exception.BusinessExceptionCode;
import com.izumi.wiki.mapper.UserMapper;
import com.izumi.wiki.req.UserLoginReq;
import com.izumi.wiki.req.UserQueryReq;
import com.izumi.wiki.req.UserResetPassword;
import com.izumi.wiki.req.UserSaveReq;
import com.izumi.wiki.resp.PageResp;
import com.izumi.wiki.resp.UserLoginResp;
import com.izumi.wiki.resp.UserQueryResp;
import com.izumi.wiki.util.CopyUtil;
import com.izumi.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();

        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameLike("%" + req.getLoginName() + "%");
        }

        PageHelper.startPage(req.getPage(), req.getSize()); // 只对第一次遇到的sql查询有效
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());


        // 列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);
        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存
     *
     * @param req
     */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            if(ObjectUtils.isEmpty(selectByLoginName(req.getLoginName()))) {
                // 新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            } else {
                // 用户名已存在
                // 提示用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        } else {
            // 更新
            // 更新
            user.setLoginName(null); // 设置为空，不更新登录名，不让用户进行编辑
            user.setPassword(null); // 修改的时候也不修改密码
            userMapper.updateByPrimaryKeySelective(user); // Selective：表字段有值才会更新
        }
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改密码
     * @param req
     */
    public void resetPassword(UserResetPassword req) {
        // 只有 id 和 password 有值
        User user = CopyUtil.copy(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 登录
     * @param req
     * @return
     */
    public UserLoginResp login(UserLoginReq req) {
        // 校验 按用户名去数据库查
        User userDB = selectByLoginName(req.getLoginName());
        if (ObjectUtils.isEmpty(userDB)) {
            // 用户名不存在
            LOG.info("用户名不存在: {}", req.getLoginName()); // 日志记录一下
            // 提示注意 用户名不存在或密码不对，干扰攻击者的判断 一个模糊的提示
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        } else {
            // 比对密码
            if (userDB.getPassword().equals(req.getPassword())) {
                // 登录成功
                UserLoginResp userLoginResp = CopyUtil.copy(userDB, UserLoginResp.class);
                return userLoginResp;
            } else {
                // 密码不正确
                // 可以尝试加一个功能：输入错误几次以上就锁定用户
                LOG.info("密码不对, 输入密码: {}, 数据库密码: {}", req.getLoginName(), userDB.getPassword()); // 日志记录一下
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }


    public User selectByLoginName(String LoginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(LoginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if(CollectionUtils.isEmpty(userList)) {
            return null;
        } else {
            return userList.get(0);
        }
    }

}
