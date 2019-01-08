package com.ourcitydeals.ctrl.model;

public class VendorsProductsListData {
	private String product_id;
	private String product_name;
	private String price;
	private String regular_price;
	private String imageUrl;
	private String rating;
	/*private String vendor_name;
	private String location;
	private String telephone;
	private String store_email;
	private String vendor_image;*/

	public VendorsProductsListData(String product_id, String product_name, String price, String regular_price, String imageUrl,
								   String rating){
		this.product_id = product_id;
		this.product_name = product_name;
		this.price = price;
		this.regular_price = regular_price;
		this.imageUrl = imageUrl;
		this.rating = rating;
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

	public String get_rating() {
		return rating;
	}
	public void set_rating(String rating) {
		this.rating = rating;
	}
}