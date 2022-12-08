package com.jas.takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jas.takeaway.common.R;
import com.jas.takeaway.entity.User;
import com.jas.takeaway.service.UserService;
import com.jas.takeaway.utils.SMSUtils;
import com.jas.takeaway.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 發送手機驗證碼短信
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //獲取手機號
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成隨機4位驗證碼 -- ValidateCodeUtils
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);  //直接攔截展示（開發需要）
            //調用阿里雲提供的短信服務API完成短信發送  --  SMSUtils
            //下面代碼沒必要真的發送，通過上面打印的code來驗證即可
//            SMSUtils.sendMessage("takeaway","",phone,code);
            //保存生成的驗證碼到Session
            session.setAttribute(phone,code);
            return R.success("手機驗證碼短信發送成功");
        }

        return R.error("短信發送失敗");
    }

    /**
     * 移動端用戶登陸
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //Map中獲取手機號和驗證碼
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //從Session中獲取保存的驗證碼
        Object codeInSession = session.getAttribute(phone);
        //驗證碼比對，若比對成功則登陸成功
        if(codeInSession!=null && codeInSession.equals(code)){
            //並且主動判斷當前手機號用戶是否為新用戶
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user==null){
                //若是新用戶則自動完成註冊
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }

        return R.error("登陸失敗");
    }
}
