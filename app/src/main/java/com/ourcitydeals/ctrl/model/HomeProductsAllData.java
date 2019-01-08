package com.ourcitydeals.ctrl.model;

import java.util.ArrayList;

public class HomeProductsAllData {
	private ArrayList<HomeProductsData> deals_products;
	private ArrayList<HomeProductsData> latest_products;
	private ArrayList<HomeProductsData> recommend_products;
	private ArrayList<HomeProductsData> mobiles_products;
	private ArrayList<HomeProductsData> food_products;

	public HomeProductsAllData(ArrayList<HomeProductsData> deals_products, ArrayList<HomeProductsData> latest_products,
							   ArrayList<HomeProductsData> recommend_products, ArrayList<HomeProductsData> mobiles_products,
							   ArrayList<HomeProductsData> food_products){
		this.deals_products = deals_products;
		this.latest_products = latest_products;
		this.recommend_products = recommend_products;
		this.mobiles_products = mobiles_products;
		this.food_products = food_products;
	}

	/*public HomeProductsAllData(ArrayList<HomeProductsData> deals_products, ArrayList<HomeProductsData> latest_products,
							   ArrayList<HomeProductsData> recommend_products, ArrayList<HomeProductsData> mobiles_products,
							   ArrayList<HomeProductsData> food_products, ArrayList<HomeProductsData> home_products){
		this.deals_products = deals_products;
		this.latest_products = latest_products;
		this.recommend_products = recommend_products;
		this.mobiles_products = mobiles_products;
		this.food_products = food_products;
		this.home_products =home_products;
	}*/

	public ArrayList<HomeProductsData> getdeals_products() {
		return deals_products;
	}
	public void setdeals_products(ArrayList<HomeProductsData> deals_products) {
		this.deals_products = deals_products;
	}

	public ArrayList<HomeProductsData> getdlatest_products() {
		return latest_products;
	}
	public void setlatest_products(ArrayList<HomeProductsData> latest_products) {
		this.latest_products = latest_products;
	}

	public ArrayList<HomeProductsData> getdrecommend_products() {
		return recommend_products;
	}
	public void setrecommend_products(ArrayList<HomeProductsData> recommend_products) {
		this.recommend_products = recommend_products;
	}

	public ArrayList<HomeProductsData> getdmobiles_products() {
		return mobiles_products;
	}
	public void setmobiles_products(ArrayList<HomeProductsData> mobiles_products) {
		this.mobiles_products = mobiles_products;
	}

	public ArrayList<HomeProductsData> getdfood_products() {
		return food_products;
	}
	public void setfood_products(ArrayList<HomeProductsData> food_products) {
		this.food_products = food_products;
	}
}