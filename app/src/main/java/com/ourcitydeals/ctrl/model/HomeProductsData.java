package com.ourcitydeals.ctrl.model;

public class HomeProductsData {
	private String product_id;
	private String product_name;
	private String price;
	private String regular_price;
	private String imageUrl;
	private String vendor_name;
	private String vendor_description;
	private String rating;
	private String product_desc;

	public HomeProductsData(String product_id, String product_name, String price, String regular_price, String imageUrl, String vendor_name, String vendor_description, String rating, String product_desc){
		this.product_id = product_id;
		this.product_name = product_name;
		this.price = price;
		this.regular_price = regular_price;
		this.imageUrl = imageUrl;
		this.vendor_name = vendor_name;
		this.vendor_description = vendor_description;
		this.rating = rating;
		this.product_desc=product_desc;
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

	public String getregular_price() {
		return regular_price;
	}
	public void setregular_price(String regular_price) {
		this.regular_price = regular_price;
	}

	public String getimageUrl() {
		return imageUrl;
	}
	public void setimageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getvendor_name() {
		return vendor_name;
	}
	public void setvendor_name(String vendor_name) {
		this.vendor_name = vendor_name;
	}

	public String getvendor_description() {
		return vendor_description;
	}
	public void setvendor_description(String vendor_description) {
		this.vendor_description = vendor_description;
	}

	public String get_rating() {
		return rating;
	}
	public void set_rating(String rating) {
		this.rating = rating;
	}

	public String get_product_desc() {
		return product_desc;
	}
	public void set_product_desc(String product_desc) {
		this.product_desc = product_desc;
	}
}