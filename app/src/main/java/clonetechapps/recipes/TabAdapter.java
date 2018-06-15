package clonetechapps.recipes;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

//The tab adapter is used for setting the different tabs at the top of the app.
//This also handles changing between them and displays the correct fragment.
public class TabAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;

    //The tab adapter will use the fm (fragment manager) to keep each fragment's state in the
    //adapter across swipes.
    public TabAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, String>();
    }

    //Returns the fragment that needs displaying bases on the position integer.
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ViewRecipesFragment();
        } else {
            return new ViewShoppingListFragment();
        }
    }

    //Returns the total number of pages.
    @Override
    public int getCount() {
        return 2;
    }

    //Returns the titles of the pages.
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.recipes_title);
        } else {
            return mContext.getString(R.string.shopping_list_title);
        }
    }

    //Overrides to allow refreshing
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);

        if(obj instanceof Fragment){
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    public Fragment getFragment(int position){
        String tag = mFragmentTags.get(position);
        if(tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);
    }
}
