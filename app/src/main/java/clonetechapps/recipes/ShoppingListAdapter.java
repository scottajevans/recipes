package clonetechapps.recipes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

    //The ShoppingListAdapter is used to display the shopping list items
    //which are a collection of ingredients from multiple recipes.
    //This is then displayed in a list view on the ViewShoppingListFragment.
    //Contains a simple getView method, that returns the name and amount into the text views
    //for the current ingredient.
public class ShoppingListAdapter extends ArrayAdapter {

    public ShoppingListAdapter(@NonNull Context context, int resource, ArrayList<Ingredient> ingredients) {
        super(context, resource, ingredients);
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        //Getting current ingredient by position.
        Ingredient currentIngredient = (Ingredient) getItem(position);

        //Initialise the ListItemView as the list_sub_item XML.
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_sub_item, parent,false);
        }

        //Sets the text views context to be the name/amount of the current ingredient.
        TextView tvName = (TextView) listItemView.findViewById(R.id.sub_name);
        tvName.setText(currentIngredient.getName());

        TextView tvAmount = (TextView)  listItemView.findViewById(R.id.sub_amount);
        tvAmount.setText(currentIngredient.getAmount());

        return listItemView;
    }
}
