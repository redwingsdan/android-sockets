package com.vantageclient;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vantageclient.Interfaces.KillFragmentListener;
import com.vantageclient.activity.CameraListDialog;
import com.vantageclient.activity.DeviceAddFragment;
import com.vantageclient.activity.DeviceEditDialogFragment;
import com.vantageclient.activity.DeviceListDialog;
import com.vantageclient.activity.DeviceListDialogFragment;
import com.vantageclient.activity.DeviceListFragment;
import com.vantageclient.activity.LiveViewFragment;
import com.vantageclient.data.DeviceItem;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements FragmentDrawer.FragmentDrawerListener, KillFragmentListener,
        DeviceAddFragment.AddDeviceListener, DeviceEditDialogFragment.EditDeviceListener,DeviceListDialogFragment.DeleteDeviceListener,
        DeviceListDialog.DialogDismissListener, CameraListDialog.CameraListListener {

    DeviceListFragment deviceListFragment;
    LiveViewFragment liveViewFragment;

    Toolbar mToolbar;
    FragmentDrawer drawerFragment;

    private static final int liveView = 0;
    private static final int deviceList = 1;
    private static final int fragmentCount = deviceList + 1;
    private Fragment[] fragments = new Fragment[fragmentCount];
    private String[] fragmentTAGS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        fragments[liveView] = getSupportFragmentManager().findFragmentById(R.id.liveViewFragment);
        fragments[deviceList] = getSupportFragmentManager().findFragmentById(R.id.devicelist_fragment);
        fragments[liveView] = new LiveViewFragment();
        fragments[deviceList] = new DeviceListFragment();
        fragmentTAGS = new String[]{"liveViewFragment", "deviceListFragment"};
        liveViewFragment = (LiveViewFragment) fragments[liveView];

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch

        displayView(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        String title = getString(R.string.app_name);

        switch (position)
        {
            case 0:
                showFragment(liveView);
                break;
            case 1:
                showFragment(deviceList);
                break;
            case 2:
                showApp("aventura.calc.harddrivecalculator");
                break;
            case 3:
                showApp("com.aventuracctv.aventurafibercalculator");
                break;
            case 4:
                showApp("com.aventuracctv.aventurarangecalculator");
                break;
            case 5:
                showApp("com.aventuracctv.aventuravoltagedropcalculator");
                break;
        }
            getSupportActionBar().setTitle(title);
    }

    private void showFragment(int fragmentIndex) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (fm.findFragmentByTag(fragmentTAGS[fragmentIndex]) == null) {
            transaction.add(R.id.container_body, fragments[fragmentIndex], fragmentTAGS[fragmentIndex]);
        }

        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else if (fm.findFragmentByTag(fragmentTAGS[i]) != null) {
                transaction.addToBackStack(null);
                transaction.hide(fragments[i]);
            }
        }

        transaction.commit();
        }

    private void showApp(String packageName) {
        if (isAppInstalled(packageName)) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            startActivity(launchIntent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        }
    }

    public boolean isAppInstalled(String packageName){
        List<ApplicationInfo> packages;
        PackageManager pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(packageName)) return true;
        }
        return false;
    }

    @Override
    public void onAddDeviceClick(DeviceItem item) {
        deviceListFragment = (DeviceListFragment) fragments[deviceList];
        deviceListFragment.addDevice(item);
    }

    @Override
    public void onEditDeviceClick(DeviceItem item, String oldDevice) {
        deviceListFragment = (DeviceListFragment) fragments[deviceList];
        deviceListFragment.editDevice(item, oldDevice);
    }

    @Override
    public void onDeleteDeviceClick(String devicename) {
        deviceListFragment = (DeviceListFragment) fragments[deviceList];
        deviceListFragment.deleteDevice(devicename);
    }

    @Override
     public void onKillFragment(String tag) {
        // Check tag if you do this with more than one fragment, then:
        getFragmentManager().popBackStack();
    }

    @Override
    public void onDismissDialog(String devicename) {

        liveViewFragment.onDismiss(devicename);
    }

    @Override
    public void onCameraListClick(int position) {
        liveViewFragment.cameraView(position);
    }

}
