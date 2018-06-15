package clonetechapps.recipes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS;


public class ViewRecipesFragment extends Fragment {

    private ArrayList<Recipe> mRecipes;
    private PreferencesHandler handler = new PreferencesHandler();
    private DialogSettings dialogSettings = new DialogSettings();

    public ViewRecipesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_recipes, container, false);

        //Load in the recipes from shared preferences.
        mRecipes = handler.loadDataRecipes(getContext());

        //Display the recipes in an expandable list.
        //Using customer adapter to display. This allows us to drill into the recipe to see
        //the related ingredients, see class for further details.
        //Set the list adapter and the view then set adapter and display.
        ExpandableListAdapter e = new ExpandableListAdapter(getActivity(), mRecipes);
        ExpandableListView expandableListView = (ExpandableListView) rootView.findViewById(R.id.list);
        expandableListView.setAdapter(e);

        //If the list view exists, set onItemLongClick to enable editing the recipe.
        if(expandableListView != null){
            expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id) {
                    onGroupLongClick(position);
                    return true;
                }
            });
        }

        return rootView;
    }

    //Initial step when loading the the dialog.
    //This will first load the recipes into the mRecipes variable using the PreferencesHandler.
    //This is in case there has been a change since in the list since we loaded.
    //This should not happen but is a pre-cautionary reload.
    //Then finds the clicked recipe by taking the list int and removing 1.
    //We remove 1 here because the first item displayed is always "Add New Recipe".
    //See the ExpandableListAdapter class for more info.
    //Then launch the dialog box.
    public void onGroupLongClick(int id){
        mRecipes = handler.loadDataRecipes(getContext());
        Recipe recipe = mRecipes.get(id-1);
        editRecipeDialog(recipe);
    }

    private void editRecipeDialog(Recipe toEdit){
        final Recipe recipe = toEdit;
        final Context context = getContext();
        AlertDialog dialog =  new AlertDialog.Builder(context).create();

        //Set title of the dialog.
        //This uses the DialogSettings class as there are dialogs used from multiple
        //classes so set in 1 place to reduce repeat code.
        TextView title =  new TextView(context);
        dialogSettings.setDialogTitle(title,context.getString(R.string.edit_recipe));
        dialog.setCustomTitle(title);

        //ll (LinearLayout) is the container for the dialog main section.
        //Child items will be added.
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        //This is the first child item.
        //Create a text view, set text and layout parameters.
        //Then added to the parent layout item.
        TextView tvName = new TextView(context);
        tvName.setText(R.string.name_label);
        tvName.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams paramsName = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsName.leftMargin = 20;
        paramsName.topMargin = 20;
        tvName.setLayoutParams(paramsName);
        ll.addView(tvName);

        //Edit text for user entry
        //Only notable items are setting the select all focus so user does not have
        //to select themselves if they are totally renaming the recipe.
        final EditText editName = new EditText(context);
        editName.setText(recipe.getName());
        editName.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_CAP_WORDS);
        editName.setGravity(Gravity.CENTER_HORIZONTAL);
        editName.setLayoutParams(paramsName);
        editName.setSelectAllOnFocus(true);
        editName.requestFocus();
        ll.addView(editName);

        //The dialog has the view set to the parent layout.
        dialog.setView(ll);

        //Set the positive OK button, this will save the info
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Handles the entry of an edited recipe.
                //Takes the new name, and gets the ingredients and id of the original recipe.
                //The recipe is then created and passed to the PreferencesHandler to save.
                String newRecipeName = editName.getText().toString();
                if(!newRecipeName.equals("") && !newRecipeName.equals(" ")){
                    ArrayList<Ingredient> newRecipeIngredients = recipe.getIngredients();
                    int newRecipeId = recipe.getId();
                    Recipe newRecipe = new Recipe(newRecipeName, newRecipeIngredients, newRecipeId);
                    handler.replaceRecipe(recipe.getName(), recipe.getId(), newRecipe, context);

                    //View refreshed so to show the updated recipe name.
                    //Finally a toast is displayed to let hte user know the recipe is being updated.
                    refreshView();
                    Toast.makeText(context, context.getString(R.string.updating, newRecipeName), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.name_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Deletes the recipe if it is not in a shopping list. Simply passes to the PreferencesHandler and refreshed the view
        //to show that the recipe is gone to the user. Also sets a toast as confirmation to the user.
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<Recipe> shoppingRecipesList = handler.loadShoppingRecipeList(context);
                Boolean used = false;
                for(int p = 0; p < shoppingRecipesList.size(); p++) {
                    if (shoppingRecipesList.get(p) == recipe) {
                        used = true;
                    }
                }
                if(!used){
                    handler.deleteRecipe(recipe.getName(), recipe.getId(), context);
                    refreshView();
                    Toast.makeText(context, context.getString(R.string.deleting, recipe.getName()), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.cannot_remove_from_list), Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Does nothing, cancels the dialog.
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        new Dialog(context);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        //Set properties for the buttons. Uses the DialogSettings class.
        //These are repeated across classes hence being put in their own class.
        Button okBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        dialogSettings.setOkBtn(okBtn);

        Button delBtn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        dialogSettings.setDeleteBtn(delBtn);

        Button cancelBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        dialogSettings.setCancelBtn(cancelBtn);
    }

    //View is refreshed. This is simply re-creating and setting the expandableListView adapter.
    private void refreshView(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
