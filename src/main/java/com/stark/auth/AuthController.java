package com.stark.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class AuthController {
    @Autowired
    HttpServletRequest request;

    @RequestMapping(value="/login", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity login (
            @RequestParam(value="userid", defaultValue = "")
                    String userid,
            @RequestParam(value="pwd", defaultValue = "")
                    String pwd)
            throws AuthFailException
    {
        if (userid == null || pwd == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            throw new AuthFailException();
        }
        if (userid.equals("admin") && pwd.equals("123456")) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", userid);
            UUID uuid = UUID.randomUUID();
            session.setAttribute("uuid", uuid.toString());
            Map<String, String> responseContent = new HashMap<String, String>();
            responseContent.put("sid", uuid.toString());
            responseContent.put("auth", "OAuth");
            responseContent.put("Method", "GET");
            return new ResponseEntity<Map>(responseContent, HttpStatus.OK);
        }
        else {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            throw new AuthFailException();
        }
    }

    @RequestMapping(value="/auth", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity auth(
            @RequestParam(value="sid", defaultValue = "") String sid
    ) throws AuthFailException
    {
        if (sid == null) {
            throw new AuthFailException();
        }
        HttpSession session = request.getSession(false);
        if (session != null && sid.equals(session.getAttribute("uuid"))) {
            return new ResponseEntity(HttpStatus.OK);
        }
        else{
            if (session != null)
                session.invalidate();
            throw new AuthFailException();
        }
    }
}
