package clonetechapps.recipes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS;

public class ViewShoppingListFragment extends Fragment {

    private ArrayList<Recipe> mRecipes;
    private ArrayList<Ingredient> mShoppingList;
    private ArrayList<Recipe> mShoppingRecipesList;
    private String mShoppingMultiplier;
    private PreferencesHandler handler = new PreferencesHandler();
    private DialogSettings dialogSettings = new DialogSettings();



    public ViewShoppingListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getContext();
        //Inflate the layout for this fragment then set variables for all the text views and
        //list view.
        View rootView = inflater.inflate(R.layout.fragment_view_shopping_list, container, false);
        TextView viewRecipes = (TextView) rootView.findViewById(R.id.button_view_recipes) ;
        TextView newList = (TextView) rootView.findViewById(R.id.button_new_shopping_list);
        TextView updateList = (TextView) rootView.findViewById(R.id.button_update_shopping_list);
        ListView listView = (ListView) rootView.findViewById(R.id.shopping_list);
        TextView listInfo = (TextView) rootView.findViewById(R.id.shopping_list_info);

        //Loads the values in the StoredPreferences using the PreferencesHandler class.
        mShoppingList = handler.loadShoppingList(context);
        mShoppingRecipesList = handler.loadShoppingRecipeList(context);
        mShoppingMultiplier = handler.loadShoppingMultiplier(context);

        //Sets the ListView in the middle of the layout to have the values of ingredients that
        //were previously selected in the shopping list.
        ShoppingListAdapter shoppingListAdapter =  new ShoppingListAdapter(context,0, mShoppingList);
        listView.setAdapter(shoppingListAdapter);

        //Sets the TextView at the top of the screen to have dynamic data.
        double people = Double.parseDouble(mShoppingMultiplier);
        int peopleInt = (int) people;
        int listSize = mShoppingRecipesList.size();
        String info;
        if(listSize > 0){
            info = context.getString(R.string.shopping_info, listSize, mShoppingList.size(), peopleInt);
        } else {
            info = context.getString(R.string.create_shopping);
        }
        listInfo.setText(info);

        //Set the TextViews to have the correct text value.
        viewRecipes.setText(context.getString(R.string.view_recipe_list));
        newList.setText(context.getString(R.string.new_shopping_list));
        updateList.setText(context.getString(R.string.update_list));

        //Set the onClickListener to launch the viewRecipesDialog when pressed.
        viewRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewRecipesDialog();
            }
        });

        //Set the onClickListener to create a dialog for a new shopping list.
        //Here you can see using the type value "new" in string format. This is to distinguish
        //between a new or update function, and the dialog handles accordingly.
        newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog(true);
            }
        });

        //Same as above but update.
        updateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog(false);
            }
        });

        //Set an onClickListener to create a dialog. This will allow users to delete from the list
        //Say they already own this item or they are deleting as they are shopping.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClickedDialog(i);
            }
        });
        return rootView;
    }

    //This dialog displays the recipes that were selected when previously creating a shopping list.
    //It is only for viewing so there is only a cancel button,
    private void viewRecipesDialog(){
        final Context context = getContext();
        AlertDialog dialog = new AlertDialog.Builder(context).create();

        TextView title = new TextView(context);
        dialogSettings.setDialogTitle(title, context.getString(R.string.recipes_to_make));
        dialog.setCustomTitle(title);

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        //Sets the adapter to the ListView for the shoppingRecipeList. See the customer adapter for
        //more info.
        ListView listView = new ListView(context);
        ShoppingRecipeListAdapter listAdapter = new ShoppingRecipeListAdapter(context, 0, mShoppingRecipesList);
        listView.setAdapter(listAdapter);

        ll.addView(listView);
        dialog.setView(ll);

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Does nothing to close the dialog.
                //This dialog is for viewing only.
            }
        });

        new Dialog(context);
        dialog.show();

        //Set properties for the OK button
        Button okBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        dialogSettings.setOkBtn(okBtn);
    }

    //This dialog handles an individual ingredient being clicked.
    //The only options here are to cancel or to remove the item from the shopping list.
    private void listItemClickedDialog(int id){
        final int identifier = id;
        final Context context = getContext();
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        String ingredientName = mShoppingList.get(identifier).getName();

        //Set the dialog title.
        TextView title = new TextView(context);
        dialogSettings.setDialogTitle(title, context.getString(R.string.ingredient_with_name, ingredientName));
        dialog.setCustomTitle(title);

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        //Single text view in this dialog to just double check the user would like to remove it.
        //The item is specified by name to reduce confusion or miss-click.
        TextView tvQuestion = new TextView(context);
        tvQuestion.setText(context.getString(R.string.remove_ingredient, ingredientName));
        tvQuestion.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams paramsQuestionHolder = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsQuestionHolder.leftMargin = 20;
        paramsQuestionHolder.topMargin = 20;
        tvQuestion.setLayoutParams(paramsQuestionHolder);

        ll.addView(tvQuestion);
        dialog.setView(ll);

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Does nothing to close the dialog.
            }
        });

        //This onClickListener is the delete button.
        //The identifier (position integer) is passed to the PreferencesHandler to remove from the
        //stored shopping list. See the PreferencesHandler class for more info.
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handler.removeFromShoppingList(context, identifier);
                refreshView();
            }
        });

        new Dialog(context);
        dialog.show();

        //Set properties for the buttons
        Button okBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        dialogSettings.setOkBtn(okBtn);

        Button delBtn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        dialogSettings.setDeleteBtn(delBtn);
    }

    private void listDialog(final Boolean newList){
        final Context context = getContext();
        AlertDialog dialog =  new AlertDialog.Builder(context).create();

        //Set title depending on whether the type is new or not.
        //So far I have only used this dialog for new or update situations so use it by checking
        //whether the type (String) was "new" or not. If not we assume update although not
        //tested explicitly.
        TextView title =  new TextView(context);
        if(newList){
            dialogSettings.setDialogTitle(title,context.getString(R.string.new_shopping_list));
        } else {
            dialogSettings.setDialogTitle(title,context.getString(R.string.update_shopping_list));
        }
        dialog.setCustomTitle(title);

        //This is the main layout for the entire dialog.
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        //This layout is to hold the below TextView and EditText to keep them aligned together.
        LinearLayout ll2 = new LinearLayout(context);
        ll2.setOrientation(LinearLayout.HORIZONTAL);

        TextView buyingFor = new TextView(context);
        buyingFor.setText(R.string.servings_label);
        buyingFor.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams paramsBuyingForHolder = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsBuyingForHolder.leftMargin = 20;
        paramsBuyingForHolder.topMargin = 20;
        buyingFor.setLayoutParams(paramsBuyingForHolder);

        //This is for the amount of people the shopping list is for.
        //This becomes the shoppingMultiplier for this particular existence of the list.
        final EditText noBuyingFor = new EditText(context);
        noBuyingFor.setInputType(TYPE_CLASS_NUMBER);
        noBuyingFor.setHint(R.string.enter_amount_hint);
        noBuyingFor.setLayoutParams(paramsBuyingForHolder);
        if(!newList){
            double people = Double.parseDouble(mShoppingMultiplier);
            int peopleInt = (int) people;
            noBuyingFor.setText("" + peopleInt);
        }

        //Adding the TextView and EditView into the LinearLayout, then adding these into the dialog
        //LinearLayout.
        ll2.addView(buyingFor);
        ll2.addView(noBuyingFor);
        ll.addView(ll2);

        //We are using a multi selection spinner. This is a custom spinner I have used from
        //com.extra. This was taken from the following URL:
        //http://androiddhina.blogspot.com/2016/02/android-multi-selection-spinner.html
        //There's no mention of licence with this. And it does work for my current use,
        //though may be changed in the future just to be a  bit safer.
        //Also there are mutli-selection spinners with more features such as search bars at the top.
        final com.extra.MultiSelectionSpinner spin = new com.extra.MultiSelectionSpinner(context);
        List<String> recipes = new ArrayList<>();
        mRecipes = handler.loadDataRecipes(context);
        for(int i = 0; i < mRecipes.size(); i++){
            recipes.add(i, mRecipes.get(i).getName());
        }
        //If there are recipes in the list, add those to the items in the options of the spinner.
        if(recipes != null){
            spin.setItems(recipes);
        }

        //Sets the hint of the spinner. This is a method I added myself, which places a new
        //item at position 0 of the spinner list so to display only when nothing is selected.
        spin.setHint(context.getString(R.string.select_recipes));
        LinearLayout.LayoutParams paramsSpinner = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsSpinner.leftMargin = 40;
        paramsSpinner.topMargin = 50;
        paramsSpinner.bottomMargin = 60;
        spin.setLayoutParams(paramsSpinner);

        //When new, no items are selected. However when updating we need to check back against the
        //list and check the boxes next to those in the spinner.
        if(!newList) {
            int[] selected = new int[mShoppingRecipesList.size()];
            int count = 0;
            for (int i = 0; i < mShoppingRecipesList.size(); i++) {
                for (int p = 0; p < mRecipes.size(); p++) {
                    if (mShoppingRecipesList.get(i).getId() == mRecipes.get(p).getId() && mShoppingRecipesList.get(i).getName().equals(mRecipes.get(p).getName())) {
                        selected[count] = p;
                        count++;
                    }
                }
            }
            spin.setSelection(selected);
        }

        //Add the spinner below the ll2 object, then add the entire LinearLayout to the dialog.
        ll.addView(spin);
        dialog.setView(ll);

        //Set the positive OK button, this will save the shopping list.
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Create new lists for the items that are selected from the spinner.
                ArrayList<Recipe> recipesList = new ArrayList<>();
                ArrayList<Ingredient> ingredientsList = new ArrayList<>();

                //Use a provided getter for the spinner to test against the entire available
                //recipes list. If checked, add these to the above selected items ArrayLists.
                //Adds both the recipe itself (for the recipe list button) and the ingredient
                //itself for the actual shopping list.
                String items = spin.getSelectedItemsAsString();
                for(String splitStr: items.split(", ")){
                    for(int p = 0; p < mRecipes.size(); p++){
                        if(splitStr.equals(mRecipes.get(p).getName())){
                            recipesList.add(mRecipes.get(p));
                            ingredientsList.addAll(mRecipes.get(p).getIngredients());
                        }
                    }
                }

                //Display a toast message (depending on what type originally passed into function).
                if(newList){
                    Toast.makeText(context, R.string.add_list_toast, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,R.string.update_list_toast, Toast.LENGTH_SHORT).show();
                }

                //Gets the multiplier from the edit text, then checks for duplicates in the list.
                //This is a function further down that will handle 2 recipes using the same
                //ingredient, see below for more info. Once done, we then save the lists and
                //multiplier into the SharedPreferences. Then the entire fragment is refreshed,
                //in order to display any new or updated shopping list and info texts.
                int multiplier = Integer.parseInt(noBuyingFor.getText().toString());
                ingredientsList = checkForDuplicates(ingredientsList, multiplier);
                handler.saveShoppingList(context, ingredientsList, recipesList, "" + multiplier);
                refreshView();
            }
        });

        //Does nothing, cancels the dialog.
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        //If we are adding a new shopping list, we do not need to clear the existing list.
        //Hence the check at the beginning, however if it is an update run we create a button
        //that removes all things from the shopping list. First we create a toast message for the
        //user. Then we create brand new ArrayLists for the recipes and ingredients and pass
        //a string of value 1 into the saveShoppingList function.  The view is then refreshed.
        if(!newList){
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.clear), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(context,R.string.clear_list_toast, Toast.LENGTH_SHORT).show();
                    ArrayList<Ingredient> ingredientsList = new ArrayList<>();
                    ArrayList<Recipe> recipeList = new ArrayList<>();
                    handler.saveShoppingList(context, ingredientsList, recipeList, context.getString(R.string.one));
                    refreshView();
                }
            });
        }

        new Dialog(context);
        dialog.show();

        //Set properties for the buttons.
        Button okBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        dialogSettings.setOkBtn(okBtn);

        Button cancelBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        dialogSettings.setCancelBtn(cancelBtn);

        Button clearBtn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        dialogSettings.setDeleteBtn(clearBtn);
    }

    //When the fragment is resumed, we refresh the view for the user.
    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    //Used to force a refresh of the view itself.
    private void refreshView(){
        //Loads the values in the StoredPreferences using the PreferencesHandler class.
        mShoppingList = handler.loadShoppingList(getContext());
        mShoppingRecipesList = handler.loadShoppingRecipeList(getContext());
        mShoppingMultiplier = handler.loadShoppingMultiplier(getContext());

        //Sets the ListView in the middle of the layout to have the values of ingredients that
        //were previously selected in the shopping list.
        ShoppingListAdapter shoppingListAdapter =  new ShoppingListAdapter(getContext(),0, mShoppingList);
        ListView listView = (ListView) getActivity().findViewById(R.id.shopping_list);
        listView.setAdapter(shoppingListAdapter);

        //Sets the TextView at the top of the screen to have dynamic data.
        TextView listInfo = (TextView) getActivity().findViewById(R.id.shopping_list_info);
        double people = Double.parseDouble(mShoppingMultiplier);
        int peopleInt = (int) people;
        int listSize = mShoppingRecipesList.size();
        String info = null;
        if(listSize != 0){
            info = getContext().getString(R.string.shopping_info, listSize, mShoppingList.size(), peopleInt);
        } else {
            info = getContext().getString(R.string.create_shopping);
        }
        listInfo.setText(info);
    }

    //This function checks through a list of ingredients for duplicates.
    //It then takes the multiplier into consideration and changes the amounts for each ingredient.
    //This list is then returned. The reason we use array lists and not hash maps is that the
    public ArrayList<Ingredient> checkForDuplicates(ArrayList<Ingredient> ingredientsList, int multiplier){
        for(int p = 0; p < ingredientsList.size(); p++){
            boolean updated = false;
            boolean containsDigits = false;
            String amountToAddTo = ingredientsList.get(p).getAmount();
            //Because the amount values are stored as strings, we must break them up into 2 strings.
            //Mass is numerical value, and type is the measurement i.e. ml, mg, teaspoons.
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder stringBuilder1 = new StringBuilder();
            String mass = null;
            String type = null;

            //Looks through the word, checks if it is a digit.
            //If so, the character is appended to stringBuilder, and the contains digits boolean
            //is set to true. This comes into play later in the function. If they are not a digit
            //the character is appended to the second string builder, and then added to the type
            //field.
            for(int r = 0; r < amountToAddTo.length(); r++){
                if(Character.isDigit(amountToAddTo.charAt(r))){
                    stringBuilder.append(amountToAddTo.charAt(r));
                    containsDigits = true;
                } else {
                    stringBuilder1.append(amountToAddTo.charAt(r));
                }
            }
            mass = stringBuilder.toString();
            type = stringBuilder1.toString();

            //For the first item we now have the mass and the measurement.
            //We now need to do this for the rest of the list, so we loop through the ingredientsList
            //where the index is not the same as the first item.
            for(int k = 0; k < ingredientsList.size(); k++){
                if(ingredientsList.get(p).getName().equals(ingredientsList.get(k).getName()) && p != k){
                    String amountToAdd = ingredientsList.get(k).getAmount();

                    StringBuilder sb3 = new StringBuilder();
                    StringBuilder sb4 = new StringBuilder();
                    String mass2 = null;
                    String type2 = null;
                    for(int r = 0; r < amountToAdd.length(); r++){
                        if(Character.isDigit(amountToAdd.charAt(r))){
                            sb3.append(amountToAdd.charAt(r));
                            containsDigits = true;
                        } else {
                            sb4.append(amountToAdd.charAt(r));
                        }
                    }
                    type2 = sb4.toString();
                    mass2 = sb3.toString();

                    //We now have both the first item (mass and type) and the second item (type2 and
                    //mass2. We now check whether either is contained in the other. If so we set
                    //a boolean to true (defined at top of function).
                    //TO-DO: fully test whether .equals() or .contains() is better.
                    boolean match = false;
                    if(type.length() >= type2.length()){
                        if(type.contains(type2)){
                            match = true;
                        }
                    } else if (type2.contains(type)){
                        match = true;
                    }

                    //If the match variable is true (as success of previous if else statement) then
                    //we add the too masses together as integers, and multiply them by the
                    //multiplier and set the first ingredient to have the new total mass. We then
                    //remove the second that we found from the ingredientsList which will be passed
                    //back out of the function.
                    //If it is not a match then we just multiple the mass by the multiplier and
                    //set this on the ingredient.
                    //At this point, regardless of match being true or not, we set updated boolean
                    //to true to miss a later else if/make sure each item has been multiplied.
                    if(match){
                        int totMass = (Integer.parseInt(mass) + Integer.parseInt(mass2)) * multiplier;
                        ingredientsList.get(p).setAmount(totMass + type);
                        ingredientsList.remove(k);
                        updated = true;
                    } else {
                        int totMass = (Integer.parseInt(mass)) * multiplier;
                        ingredientsList.get(p).setAmount(totMass + type);
                        updated = true;
                    }
                }
                //If the item never entered the if statement that could lead to a match, and it
                //does contain a digit, we just multiply the mass by the multiplier and set the value.
                else if(!updated && containsDigits){
                    int totMass = (Integer.parseInt(mass)) * multiplier;
                    ingredientsList.get(p).setAmount(totMass + type);
                }
            }
        }
        return ingredientsList;
    }
}
