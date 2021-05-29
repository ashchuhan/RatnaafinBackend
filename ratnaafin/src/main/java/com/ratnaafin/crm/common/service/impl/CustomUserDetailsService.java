package com.ratnaafin.crm.common.service.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ratnaafin.crm.admin.constant.TrueFalse;
import com.ratnaafin.crm.user.dao.UserDao;
import com.ratnaafin.crm.user.model.Role;
import com.ratnaafin.crm.user.model.User_master;

public class CustomUserDetailsService implements UserDetailsService{
    private static String ROLE_PREFIX = "ROLE_";
    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User_master user = userDao.findByUserName(username, TrueFalse.TRUE.getBvalue());
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        System.out.println("************************************** "+user.getUser_name());
        return new org.springframework.security.core.userdetails.User(user.getUser_name(), user.getPassword(),mapRolesToAuthorities(user.getRole()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(ROLE_PREFIX+role.getName())).collect(Collectors.toList());
    }
}
