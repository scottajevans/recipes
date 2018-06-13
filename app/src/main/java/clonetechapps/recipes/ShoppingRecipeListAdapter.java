package clonetechapps.recipes;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

    //The ShoppingRecipeListAdapter is used to display the shopping list recipes which
    //are a collection of multiple recipes the user has selected as a shopping list.
    //This is then displayed in a list view on the ViewShoppingListFragment when the user
    //clicks a button to see the recipes that were selected.
    //Contains a simple getView method, that returns the name into a text view
    //for the current recipe.
public class ShoppingRecipeListAdapter extends ArrayAdapter {

    public ShoppingRecipeListAdapter(@NonNull Context context, int resource, ArrayList<Recipe> recipes) {
        super(context, resource, recipes);
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        //Getting current recipe by position.
        Recipe currentRecipe = (Recipe) getItem(position);

        //Initialise the ListItemView as the list_item XML.
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent,false);
        }

        //Setting the text view to have the correct recipe name, then setting some different
        //layout parameters.
        TextView tvName = (TextView) listItemView.findViewById(R.id.view_recipe_name);
        tvName.setTextColor(Color.BLACK);
        tvName.setTextSize(16);
        tvName.setText(currentRecipe.getName());

        return listItemView;
    }


}
