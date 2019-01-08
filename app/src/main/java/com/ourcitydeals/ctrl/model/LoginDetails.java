package com.ourcitydeals.ctrl.model;

public class LoginDetails {
	private String UserID;
	private String userName;
	private String userMobile;
	private String userEmail;
	private String loginDate;

	public LoginDetails(String UserID, String userName, String userMobile, String userEmail, String loginDate){
		this.UserID = UserID;
		this.userName = userName;
		this.userMobile = userMobile;
		this.userEmail = userEmail;
		this.loginDate = loginDate;
	}

	public LoginDetails(){

	}

	public String getUserID() {
		return UserID;
	}
	public void setUserID(String UserID) {
		this.UserID = UserID;
	}
	public String getuserName() {
		return userName;
	}
	public void setuserName(String userName) {
		this.userName = userName;
	}
	public String getUserMobile() {
		return userMobile;
	}
	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}
	public String getuserEmail() {
		return userEmail;
	}
	public void setuserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(String loginDate) {
		this.loginDate = loginDate;
	}
	
}
