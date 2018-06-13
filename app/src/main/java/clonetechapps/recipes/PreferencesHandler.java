package clonetechapps.recipes;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

//This is how the application currently handles data.
//It is stored locally in SharedPreferences.
//This will eventually be changed over to database as well, so to store locally and update a SQL
//DB, which will then have to handle offline situations.
//Will also have to manage preference to using Wi-Fi only so the DB is only updated via Wi-Fi.
//Meaning none of the below code will be removed, just added to.
//These are called in a few places and so use their own class.
public class PreferencesHandler {
    private ArrayList<Recipe> mRecipes;
    private ArrayList<Ingredient> mShoppingList;
    private ArrayList<Recipe> mShoppingRecipesList;
    String mShoppingMultiplier;

    //Returns the list of recipes stored into an ArrayList.
    //The preference is always called recipesList and the string is stored as recipe.
    //Uses a Gson implementation (see build.gradle).
    public ArrayList<Recipe> loadDataRecipes(Context context){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.recipes_list), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(context.getString(R.string.recipe), null);
        Type type = new TypeToken<ArrayList<Recipe>>() {}.getType();
        mRecipes = gson.fromJson(json,type);
        if(mRecipes == null){
            mRecipes = new ArrayList<>();
        }
        return mRecipes;
    }

    //Find the recipe that is being replaced and set to new values (either name or ingredients, ID
    //can never be changed).
    //This will then pass to the updateStoredRecipes function.
    public void replaceRecipe(String toRemove, int id, Recipe newRecipe, Context context){
        for(int i = 0; i < mRecipes.size(); i++){
            if(mRecipes.get(i).getName().equals(toRemove) && mRecipes.get(i).getId() == id){
                Recipe recipe = mRecipes.get(i);
                recipe.setName(newRecipe.getName());
                recipe.setIngredients(newRecipe.getIngredients());
            }
        }
        updateStoredRecipes(context);
    }

    //Removes the desired recipe from the list. Calls updateStoredRecipes function after.
    public void deleteRecipe(String toRemove, int id, Context context){
        for(int i = 0; i < mRecipes.size(); i++){
            if(mRecipes.get(i).getName().equals(toRemove) && mRecipes.get(i).getId() == id){
                mRecipes.remove(i);
            }
        }
        updateStoredRecipes(context);
    }

    //Saves the new recipe by loading the current, adding the new one onto the existing list,
    //then updating the stored recipes.
    public void saveDataRecipes(Context context, Recipe newRecipe){
        mRecipes = loadDataRecipes(context);
        mRecipes.add(newRecipe);
        updateStoredRecipes(context);
    }

    //Deletes an ingredient from the recipe by changing the ingredients for the recipe then
    //saving the recipe itself.
    public void deleteIngredient(String toRemove, int id, int ingredientId, Context context){
        mRecipes = loadDataRecipes(context);
        for(int i = 0; i < mRecipes.size(); i++){
            if(mRecipes.get(i).getName().equals(toRemove) && mRecipes.get(i).getId() == id){
                ArrayList<Ingredient> ingredients = mRecipes.get(i).getIngredients();
                ingredients.remove(ingredientId);
            }
        }
        updateStoredRecipes(context);
    }

    //This is the effective "save" of the recipe list. Again using Gson and Json will add this into
    //StoredPreferences under recipesList and recipe.
    private void updateStoredRecipes(Context context){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.recipes_list), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mRecipes);
        editor.putString(context.getString(R.string.recipe), json);
        editor.apply();
    }

    //This loads the list of ingredients in the shopping list and returns them as an ArrayList.
    //Always called "shoppingList" and string is set to "shopping".
    public ArrayList<Ingredient> loadShoppingList(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shopping_list), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(context.getString(R.string.shopping), null);
        Type type = new TypeToken<ArrayList<Ingredient>>() {
        }.getType();
        mShoppingList = gson.fromJson(json, type);
        if (mShoppingList == null) {
            mShoppingList = new ArrayList<>();
        }
        return mShoppingList;
    }

    //This loads the list of recipes in the shopping list and returns them as an ArrayList.
    //Always called "shoppingRecipesList" and string is set to "shoppingRecipes".
    public ArrayList<Recipe> loadShoppingRecipeList(Context context){
        SharedPreferences preferences1 = context.getSharedPreferences(context.getString(R.string.shopping_recipe_list), Context.MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json1 = preferences1.getString(context.getString(R.string.shopping_recipes), null);
        Type type1 = new TypeToken<ArrayList<Recipe>>() {
        }.getType();
        mShoppingRecipesList = gson1.fromJson(json1, type1);
        if (mShoppingRecipesList == null) {
            mShoppingRecipesList = new ArrayList<>();
        }
        return mShoppingRecipesList;
    }

    //This loads the multiplier used in the shopping list.
    //If it is not set, it defaults to 1.
    //Always called "shoppingListMultiplier" and string is set to "shoppingMultiplier".
    public String loadShoppingMultiplier(Context context){
        SharedPreferences preferences2 = context.getSharedPreferences(context.getString(R.string.shopping_list_multiplier), Context.MODE_PRIVATE);
        Gson gson2 = new Gson();
        String json2 = preferences2.getString(context.getString(R.string.shopping_multiplier), null);
        Type type2 = new TypeToken<Double>(){}.getType();
        mShoppingMultiplier = context.getString(R.string.one);
        if(gson2.fromJson(json2, type2) != null){
            mShoppingMultiplier = (gson2.fromJson(json2, type2).toString());
        }
        return mShoppingMultiplier;
    }

    //This removes the item from the shopping list that is required to be removed.
    //Then instantly saves the shopping list.
    //Always called "shoppingList" and string is set to "shopping".
    public void removeFromShoppingList(Context context, int toRemove){
        mShoppingList.remove(toRemove);
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shopping_list), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mShoppingList);
        editor.putString(context.getString(R.string.shopping), json);
        editor.apply();

    }

    //This function saves the shoppingList, the shoppingRecipesList and the shoppingMultiplier.
    //This block uses all 3 as a shopping list must contain all 3 pieces of information if new or
    //updated. Would be possible to break up into 3, however this ensures that all things are
    //correct to the user entry.
    public void saveShoppingList(Context context, ArrayList<Ingredient> list, ArrayList<Recipe> list1, String multiplier){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shopping_list), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(context.getString(R.string.shopping), json);
        editor.apply();

        SharedPreferences preferences1 = context.getSharedPreferences(context.getString(R.string.shopping_recipe_list), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = preferences1.edit();
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(list1);
        editor1.putString(context.getString(R.string.shopping_recipes), json1);
        editor1.apply();

        SharedPreferences preferences2 = context.getSharedPreferences(context.getString(R.string.shopping_list_multiplier), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferences2.edit();
        Gson gson2 = new Gson();
        String json2 = gson2.toJson(multiplier);
        editor2.putString(context.getString(R.string.shopping_multiplier), json2);
        editor2.apply();
    }
}
