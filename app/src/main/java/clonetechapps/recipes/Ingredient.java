package clonetechapps.recipes;

import java.util.ArrayList;

public class Ingredient {

    //Ingredient item contains only name and amount currently.
    //Will eventually contain cost to help total recipe costs.
    //Only includes simple getter and setters for now.

    private String mName;
    private String mAmount;

    public Ingredient(String name, String amount){
        this.mName = name;
        this.mAmount = amount;
    }

    public String getName(){
        return mName;
    }

    public String getAmount(){
        return mAmount;
    }

    public void setName(String name){
        this.mName = name;
    }

    public void setAmount(String amount){
        this.mAmount = amount;
    }

    @Override
    public String toString() {
        return "Ingredient name: " + mName + ". Amount is: " + mAmount;
    }
}
