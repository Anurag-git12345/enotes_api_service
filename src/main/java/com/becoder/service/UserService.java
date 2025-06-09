package com.becoder.service;

import com.becoder.dto.PasswordChangeRequest;

public interface UserService {

	public void ChangePassword(PasswordChangeRequest passwordRequest);
}
