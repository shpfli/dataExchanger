/**
 * 
 */
package com.hubery.data.bean;

/**
 * @author Hubery
 * @createDate 2016年11月25日
 */
public class TaskStatus {
	/**
	 * 任务状态 <br>
	 * true:成功; false:失败
	 */
	private boolean isSuccess = false;

	private String msg;

	public TaskStatus() {
		super();
	}

	/**
	 * @return the isSuccess
	 */
	public boolean isSuccess() {
		return isSuccess;
	}

	/**
	 * @param isSuccess
	 *            the isSuccess to set
	 */
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "{isSuccess:" + isSuccess + ", msg:" + msg + "}";
	}

}
