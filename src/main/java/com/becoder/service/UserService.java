package com.becoder.service;

import com.becoder.dto.PasswordChangeRequest;
import com.becoder.dto.PswdResetRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

	public void ChangePassword(PasswordChangeRequest passwordRequest);

	public void sendEmailPasswordReset(String email, HttpServletRequest request) throws Exception;

	public void verifyPswdResetLink(Integer uid, String code) throws Exception;

	public void resetPassword(PswdResetRequest pswdResetRequest) throws Exception;
}
