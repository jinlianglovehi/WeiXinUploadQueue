package cn.ihealthbaby.weitaixinpro;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

/**
 * @author by kang on 2015/9/9.
 */
public class BaseTabFragment extends Fragment {

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container_framelayout, fragment);
        transaction.commit();
        getChildFragmentManager().executePendingTransactions();
    }

    public boolean popFragment() {
        boolean isPop = false;
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            isPop = true;
            getChildFragmentManager().popBackStack();
        }
        return isPop;
    }
}
