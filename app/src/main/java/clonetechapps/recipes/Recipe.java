package clonetechapps.recipes;

import java.util.ArrayList;

public class Recipe {

    //Recipe item contains name, an array list of ingredients and an ID number.
    //This class contains getters and setters (except for ID as this should never be changed once
    //created).

    private String mName;
    private ArrayList<Ingredient> mIngredients;
    private int mId;

    public Recipe(String name, ArrayList<Ingredient> ingredients, int id){
        this.mName = name;
        this.mIngredients = ingredients;
        this.mId = id;
    }

    public String getName(){
        return mName;
    }

    public ArrayList<Ingredient> getIngredients() {
        return mIngredients;
    }

    public int getId(){
        return mId;
    }

    public void setName(String name){
        this.mName = name;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients){
        this.mIngredients = ingredients;
    }

    @Override
    public String toString() {
        return "Recipe name: " + mName + ". ID is: " + mId + ". Ingredients are: " + mIngredients.toString();
    }
}
