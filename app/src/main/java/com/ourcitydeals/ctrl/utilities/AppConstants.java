package com.ourcitydeals.ctrl.utilities;

public class AppConstants {
    // Beta Server root URL
    public static String BASE_URL = "https://www.mybuydeal.com/services/";

    //User login url
    public static String LOGIN_URL = BASE_URL+"login.php?";

    //User Registration url
    public static String REGISTER_URL = BASE_URL+"register.php?";

    //Home Products Live
    public static String HOME_PRODUCTS_URL = BASE_URL+"home_products.php";

    // Get all Categories
    public static String ALL_CATEGORIES_URL = BASE_URL+"categories.php?";

    //Get sub categories
    public static String ALL_SUB_CATEGORIES_URL = BASE_URL+"subcategories.php?cat_id=";

    //Get category wise products
    public static String CATEGORIES_WISE_PRODUCTS_LIST_URL = BASE_URL+"category_products1.php?";

    //Search Suggest
    public static String SEARCH_SUGGETIONS_URL = BASE_URL+"allkeywords.php?";

    //HOME PRODUCTS VIEW ALL --Live
    //public static String HOME_PRODUCTS_VIEW_ALL_URL = BASE_URL+"home_view_products.php?start=0&limit=10&keyword=";
    //HOME PRODUCTS VIEW ALL --Live 2
    public static String HOME_PRODUCTS_VIEW_ALL_URL = BASE_URL+"home_view_products1.php?keyword=";

    public static String VENDOR_WISE_PRODUCTS_URL = BASE_URL+"vendor_products.php?vendor=";

    //Search Results
    //public static String SEARCH_RESULTS_URL = BASE_URL+"search.php?start=0&limit=10&search=";

    public static String SEARCH_RESULTS_URL = BASE_URL+"search.php?search=";

    //Related Products
    public static String RELATED_PRODUCTS_URL = BASE_URL+"related_products.php?start=0&limit=10&product_id=";

    //Product Details
    public static String PRODUCT_FULL_DETAILS_URL = BASE_URL+"product_view.php?product_id=";

    // Add to cart
    public static String ADD_TO_CART_URL = BASE_URL+"add_cart.php?";

    //Show Cart
    public static String SHOW_CART_LIST_URL = BASE_URL+"show_cart.php?";

    // Add to wish list
    public static String ADD_TO_WISH_LIST_URL = BASE_URL+"wishlist_add.php?";

    //Show  Wish List
    public static String SHOW_WISH_LIST_URL = BASE_URL+"show_wishlist.php?userid=";

    //Remove Cart Item
    public static String REMOVE_CART_LIST_ITEM_URL = BASE_URL+"remove_cart.php?";

    //Order Creating
    public static String ORDER_CREATE_URL = BASE_URL+"create_order.php?";

    //Vendor Inquiry
    public static String VENDOR_INQUIRY_URL = BASE_URL+"mailsend.php?";

    //pin Code Check
    public static String PIN_CODE_CHECKING_URL = BASE_URL+"pincode_check.php?";

    //Update Profile
    public static String UPDATE_PROFILE_URL = BASE_URL+"update_profile.php?";

    //GET USER Profile
    public static String RETRIEVE_PROFILE_URL = BASE_URL+"retrieve_profile.php?";
}