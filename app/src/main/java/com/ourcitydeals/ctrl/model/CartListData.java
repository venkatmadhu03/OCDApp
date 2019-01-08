package com.ourcitydeals.ctrl.model;

public class CartListData {
	private String product_id;
	private String product_name;
	private String price;
	private String imageUrl;
	private String vendor_id;
	private String quantity;
	private String vendroName;
	private String totlal;

	public CartListData(String product_id, String product_name, String price, String imageUrl, String vendor_id, String quantity, String vendroName, String totlal){
		this.product_id = product_id;
		this.product_name = product_name;
		this.price = price;
		this.imageUrl = imageUrl;
		this.vendor_id = vendor_id;
		this.quantity = quantity;
		this.vendroName = vendroName;
		this.totlal = totlal;
	}

	public String getproduct_id() {
		return product_id;
	}
	public void setproduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getproduct_name() {
		return product_name;
	}
	public void setproduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getprice() {
		return price;
	}
	public void setprice(String price) {
		this.price = price;
	}

	public String getimageUrl() {
		return imageUrl;
	}
	public void setimageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getvendor_id() {
		return vendor_id;
	}
	public void setvendor_id(String vendor_id) {
		this.vendor_id = vendor_id;
	}

	public String getquantity() {
		return quantity;
	}
	public void setquantity(String quantity) {
		this.quantity = quantity;
	}

	public String getvendroName() {
		return vendroName;
	}
	public void setvendroName(String vendroName) {
		this.vendroName = vendroName;
	}

	public String getTotlal() {
		return totlal;
	}
	public void setTotlal(String totlal) {
		this.totlal = totlal;
	}
}