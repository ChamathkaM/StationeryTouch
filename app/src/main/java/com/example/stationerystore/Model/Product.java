package com.example.stationerystore.Model;


public class Product {

    private String id;
    private String product;
    private String price;
    private String qty;
    private String description;
    private String supplierName;
    private String image;


    public Product() {
        //this constructor is required
    }

    public Product(String product, String price, String qty, String description, String supplierName) {
        this.product = product;
        this.price = price;
        this.qty = qty;
        this.description = description;
        this.supplierName = supplierName;
    }

    public Product(String product, String price, String qty, String description, String supplierName, String image) {
        this.product = product;
        this.price = price;
        this.qty = qty;
        this.description = description;
        this.supplierName = supplierName;
        this.image = image;
    }

    public Product(String id, String product, String price, String qty, String description, String supplierName, String image) {
        this.id = id;
        this.product = product;
        this.price = price;
        this.qty = qty;
        this.description = description;
        this.supplierName = supplierName;
        this.image = image;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}