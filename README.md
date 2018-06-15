Recipes is an Android application that allows users to store recipes and create shopping lists.
Please note this is NOT a finished product. Below are next release notes. There are many improvements that need to be made to this, this is more of a proof of concept and functionality. 

Latest Updates:
Bug fixes:
Can no longer enter empty strings.
Must use a numerical value in amount fields.
Layout changes to shopping list fragment. This also includes support for landscape view.

New features:
Keyboard appears in all dialogs on open.
Name field is highlighted in all ingredient/recipe dialogs.
Able to add a new item to the shopping list without it being from a recipe.


Recipes.
Recipes are built of a name, id and an array list of ingredients. Ingredients are built of a name and amount (volume e.g. 100ml, 150g etc).

Shopping lists.
Shopping lists are 3 different things combined:
List of ingredients - this will be an array list of items as above, name and amount.
List of recipes - this will be an array list of recipes.
Multiplier - this is the amount of people the user is purchasing items for i.e. you want to cook for 2 people.

Currently we only use the recipe name from the stored shopping list however there will be other functionality in the future that will require the entire recipe object. Rather than take the ingredients from the recipes, we store them separately for a couple of reasons:
1 - There may be duplicates so they are taken out and the amounts increased accordingly.
2 - There are situations where users may already have items and therefore want to remove them from the list.

