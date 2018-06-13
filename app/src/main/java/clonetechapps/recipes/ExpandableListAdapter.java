package clonetechapps.recipes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS;

//This adapter is used to populate ViewRecipesFragment.
//I have used an expandableListView and Adapter because we have a parent item (Recipes) and a
//child item(s) (Ingredients). This extends the BaseExpandableListAdapter.
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Recipe> mRecipes;
    private PreferencesHandler handler = new PreferencesHandler();
    private DialogSettings dialogSettings = new DialogSettings();

    public ExpandableListAdapter(Context context, ArrayList<Recipe> recipes){
        this.context = context;
        this.mRecipes = recipes;
    }

    //Provides the group count, which is the list of recipes + 1.
    //The + 1 is because at the beginning of the list we display "Add New Recipe".
    //Before we can calculate this we need to populate mRecipes via the PreferencesHandler class.
    @Override
    public int getGroupCount() {
        mRecipes = handler.loadDataRecipes(context);
        return mRecipes.size() + 1;
    }

    //Provides the children count. In this case we take the position in the Recipe list of the
    //parent. If in the first position (0) we return 0, this because we display "Add New Recipe"
    //instead of an item and want 0 children for that particular parent item.
    //Otherwise we return the recipe before i (the group position), get the ingredients, and then
    //add 1 to the size so as to allow for the "Add New Ingredient" that is in place 0 of the
    //children/sub list.

    //N.B. This may change in the future, I am debating adding a "publish recipe" function which
    //will likely load the recipe into the DB permanently, at which point there will be no option
    //to add new ingredients into it. This will then have to handle whether or not the recipe
    //has been published.
    @Override
    public int getChildrenCount(int i) {
        mRecipes = handler.loadDataRecipes(context);
        if(i == 0){
            return 0;
        } else {
            return mRecipes.get(i-1).getIngredients().size()+1;
        }
    }

    //Returns the recipe at the position. The position is negated by 1 because we have and extra
    //parent due to the "Add New Recipe" parent.
    @Override
    public Recipe getGroup(int i) {
        mRecipes = handler.loadDataRecipes(context);
        return mRecipes.get(i-1);
    }

    //Returns the ingredient at the position. We return take the group/parent - 1 (because of
    //"Add New Recipe" then get the ingredient at the actual position.
    //Because this is not used by us but only by the system to display, we do not need to avoid
    //the "Add New Ingredient".
    @Override
    public Ingredient getChild(int i, int i1) {
        return mRecipes.get(i-1).getIngredients().get(i1);
    }

    //Simple getters.
    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //This sets the view of the group item. In terms of the application - Recipes.
    @Override
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        //Inflate the view.
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_item, null);

        }
        //Either set the text view to have the text "Add New Recipe" or set it to the name of the
        //recipe.
        TextView recipeName = (TextView) view.findViewById(R.id.view_recipe_name);
        if(i == 0){
            recipeName.setText(R.string.add_new_recipe);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newDialog(context.getString(R.string.recipe), i);
                }
            });
        } else {
            mRecipes = handler.loadDataRecipes(context);
            Recipe recipe = getGroup(i);
            recipeName.setText(recipe.getName());
        }
        return view;
    }

    //Sets the view of the ingredients for each group item (aside from group item 0 - "Add New Recipe").
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final int recipeIdentifier = i;
        final int ingredientIdentifier = i1 -1;
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_sub_item, null);
        }
        TextView ingName = (TextView) view.findViewById(R.id.sub_name);
        TextView ingAmount = (TextView) view.findViewById(R.id.sub_amount);
        if(i == 0){
            return view;
        } else {
            //If we are looking at the first ingredient, set the text to "Add New Ingredient" and
            //set an onClick to launch the newDialog when clicked.
            if (i1 == 0) {
                ingName.setText(R.string.add_new_ingredient);
                ingAmount.setText("");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newDialog(context.getString(R.string.ingredient), recipeIdentifier);
                    }
                });
            }
            //Otherwise we load in the recipes, and set the ingredients of the recipe into the
            //TextViews. We then attach an onClick that starts an edit ingredient dialog.
            else {
                mRecipes = handler.loadDataRecipes(context);
                final Recipe recipe = mRecipes.get(i-1);
                final ArrayList<Ingredient> ingredient = recipe.getIngredients();
                ingName.setText(ingredient.get(i1-1).getName());
                ingAmount.setText(ingredient.get(i1-1).getAmount());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editIngredientDialog(recipeIdentifier-1, ingredientIdentifier);
                    }
                });
            }
        }

        return view;
    }

    //Child is selectable, as we want to edit ingredients/add new ones.
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    //Dialog when an ingredient item is clicked.
    private void editIngredientDialog(final int recipeId, final int ingredientId){
        final int ingredientIdentifier = ingredientId;
        final int recipeIdentifier = recipeId;
        final Ingredient ingredient = mRecipes.get(recipeIdentifier).getIngredients().get(ingredientIdentifier);
        AlertDialog dialog =  new AlertDialog.Builder(context).create();

        //Set title of dialog.
        TextView title =  new TextView(context);
        dialogSettings.setDialogTitle(title, context.getString(R.string.edit_ingredient));
        dialog.setCustomTitle(title);

        //This is a LinearLayout that will contain the name label and EditText at the top of the
        //dialog.
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        //Text view to label the user entry below.
        TextView tvName = new TextView(context);
        tvName.setText(R.string.name_label);
        tvName.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams paramsName = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsName.leftMargin = 20;
        paramsName.topMargin = 20;
        tvName.setLayoutParams(paramsName);
        ll.addView(tvName);

        //Edit text for user entry of the name.
        final EditText editName = new EditText(context);
        editName.setText(ingredient.getName());
        editName.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_CAP_WORDS);
        editName.setGravity(Gravity.CENTER_HORIZONTAL);
        editName.setLayoutParams(paramsName);
        ll.addView(editName);

        //LinearLayout2 will contain the amount label and EditText displayed below the name layout.
        LinearLayout ll2 = new LinearLayout(context);
        ll2.setOrientation(LinearLayout.HORIZONTAL);

        //Text view to show that this is entry for the recipe name.
        TextView tvAmount = new TextView(context);
        tvAmount.setText(R.string.amount_label);
        tvAmount.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams paramsName2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsName2.leftMargin = 20;
        paramsName2.topMargin = 20;
        tvAmount.setLayoutParams(paramsName2);
        ll2.addView(tvAmount);

        //Edit text for user entry of the amount.
        final EditText editAmount = new EditText(context);
        editAmount.setText(ingredient.getAmount());
        editAmount.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_CAP_WORDS);
        editAmount.setGravity(Gravity.CENTER_HORIZONTAL);
        editAmount.setLayoutParams(paramsName2);
        ll2.addView(editAmount);

        //ingredientLayout will contain both of LinearLayouts defined above to become the main
        //layout of the entire dialog body.
        LinearLayout ingredientLayout = new LinearLayout(context);
        ingredientLayout.setOrientation(LinearLayout.VERTICAL);

        ingredientLayout.addView(ll);
        ingredientLayout.addView(ll2);

        dialog.setView(ingredientLayout);

        //Set the positive OK button, this will save the updated recipe into the preferences.
        //We take the information the user has entered then set those into an ingredient object.
        //This is then added into the recipe in place of the old ingredient using setter methods.
        //The recipe is then replaced with the new updated recipe.
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String newIngredientName = editName.getText().toString();
                String newIngredientAmount = editAmount.getText().toString();

                Recipe updateRecipe = mRecipes.get(recipeId);
                String toUpdate = updateRecipe.getName();
                ArrayList<Ingredient> ingredients = updateRecipe.getIngredients();
                Ingredient toEdit = ingredients.get(ingredientId);
                toEdit.setName(newIngredientName);
                toEdit.setAmount(newIngredientAmount);

                handler.replaceRecipe(toUpdate, recipeId, updateRecipe, context);
                refreshEvents();

                Toast.makeText(context, context.getString(R.string.updating, toUpdate), Toast.LENGTH_SHORT).show();
            }
        });

        //Deletes the recipe, handled in the PReferencesHandler.
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newIngredientName = editName.getText().toString();
                handler.deleteIngredient(mRecipes.get(recipeId).getName(), mRecipes.get(recipeId).getId(), ingredientId, context);
                refreshEvents();
                Toast.makeText(context,context.getString(R.string.deleting_ingredient, newIngredientName, mRecipes.get(recipeId).getName()), Toast.LENGTH_SHORT).show();
            }
        });

        //Does nothing, cancels the dialog.
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        new Dialog(context);
        dialog.show();

        //Set properties for the OK button
        Button okBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        dialogSettings.setOkBtn(okBtn);

        Button delBtn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        dialogSettings.setDeleteBtn(delBtn);

        Button cancelBtn = dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE);
        dialogSettings.setCancelBtn(cancelBtn);

    }

    //This will create a dialog for user to enter a name for a new recipe or ingredient.
    //Determining which is by the string passed in "type".
    //ID will be auto assigned and ingredients will be an empty list if creating a new recipe.
    //Otherwise the new ingredient will take both values for name and amount and save them.
    //As most was the same, I decided to use 1 function for both and use some if statements.
    private void newDialog(final String type, int i){
        final int identifier = i;
        AlertDialog dialog = new AlertDialog.Builder(context).create();

        //Set title of the dialog box depending on the type passed in.
        TextView title = new TextView(context);
        if(type == context.getString(R.string.recipe)){
            dialogSettings.setDialogTitle(title, context.getString(R.string.new_recipe));
        } else {
            dialogSettings.setDialogTitle(title, context.getString(R.string.new_ingredient));
        }

        dialog.setCustomTitle(title);

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        //Text view to show that this is entry for the recipe name or ingredient name.
        //As both objects have a name value this works for both instances and therefore does not
        //depend on the type passed in.
        TextView tvName = new TextView(context);
        tvName.setText(R.string.name_label);
        tvName.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams paramsName = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsName.leftMargin = 20;
        paramsName.topMargin = 20;
        tvName.setLayoutParams(paramsName);
        ll.addView(tvName);

        //Edit text for user entry, in which the type on affects the hint for the EditText.
        final EditText entName = new EditText(context);
        if(type == context.getString(R.string.recipe)){
            entName.setHint(context.getString(R.string.recipe_name));
        } else {
            entName.setHint(context.getString(R.string.ingredient_name));
        }
        entName.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_CAP_WORDS);
        entName.setGravity(Gravity.CENTER_HORIZONTAL);
        entName.setLayoutParams(paramsName);
        ll.addView(entName);

        final EditText entAmount = new EditText(context);

        //If we are creating a new recipe we only need a name, as it is created empty (see below in
        //OK button functionality. Therefore we stop here and set the view.
        //If it is for an ingredient, we require the amount also so we set a TextView and EditText
        //to enter this data.
        if(type == context.getString(R.string.recipe)){
            dialog.setView(ll);
        } else {
            LinearLayout ll2 = new LinearLayout(context);
            ll2.setOrientation(LinearLayout.HORIZONTAL);

            //Text view to show that this is entry for the ingredient amount.
            TextView tvAmount = new TextView(context);
            tvAmount.setText(R.string.amount_label);
            tvAmount.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams paramsName2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsName2.leftMargin = 20;
            paramsName2.topMargin = 20;
            tvAmount.setLayoutParams(paramsName2);
            ll2.addView(tvAmount);

            //Edit text for user entry of ingredient amount.
            entAmount.setHint(R.string.ingredient_amount);
            entAmount.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_CAP_WORDS);
            entAmount.setGravity(Gravity.CENTER_HORIZONTAL);
            entAmount.setLayoutParams(paramsName2);
            ll2.addView(entAmount);

            LinearLayout ingredientLayout = new LinearLayout(context);
            ingredientLayout.setOrientation(LinearLayout.VERTICAL);

            ingredientLayout.addView(ll);
            ingredientLayout.addView(ll2);

            dialog.setView(ingredientLayout);
        }

        //Set the positive OK button, this will save the info depending on the type.
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //If we are saving a new recipe, we simply take the new recipe name, then create a
                //blank ingredient ArrayList, and a new ID (by adding 1 to the highest value).
                //We then save this into SharedPreferences.
                if(type == context.getString(R.string.recipe)){
                    String newRecipeName = entName.getText().toString();
                    ArrayList<Ingredient> newRecipeIngredients = new ArrayList<>();
                    int newRecipeId = mRecipes.get(mRecipes.size()).getId() + 1;
                    handler.saveDataRecipes(context, new Recipe(newRecipeName, newRecipeIngredients, newRecipeId));
                    refreshEvents();

                    Toast.makeText(context, context.getString(R.string.entering_recipe, newRecipeName), Toast.LENGTH_SHORT).show();
                }
                //If we are saving a new ingredient for a recipe, we get the name and amount from
                //the user entered information. The new ingredient is created by adding it to the
                //existing ingredient list for that Recipe. This is then updated into
                //SharedPreferences using the handler.
                else {
                    String newIngredientName = entName.getText().toString();
                    String newIngredientAmount = entAmount.getText().toString();
                    Recipe updatedRecipe = mRecipes.get(identifier-1);
                    ArrayList<Ingredient> ingredients = updatedRecipe.getIngredients();
                    ingredients.add(new Ingredient(newIngredientName, newIngredientAmount));
                    updatedRecipe.setIngredients(ingredients);

                    handler.replaceRecipe(updatedRecipe.getName(),updatedRecipe.getId(), updatedRecipe, context);
                    refreshEvents();
                    Toast.makeText(context, context.getString(R.string.entering_ingredient, newIngredientName, updatedRecipe.getName()), Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Does nothing, cancels the dialog.
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        new Dialog(context);
        dialog.show();

        //Set properties for the buttons
        Button okBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        dialogSettings.setOkBtn(okBtn);

        Button cancelBtn = dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE);
        dialogSettings.setCancelBtn(cancelBtn);
    }

    //This function refreshes the display in the view.
    private void refreshEvents(){
        this.notifyDataSetChanged();
    }
}
