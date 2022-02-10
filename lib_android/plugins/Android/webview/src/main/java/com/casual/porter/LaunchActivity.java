package com.casual.porter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.unity3d.player.UnityPlayer;

public class LaunchActivity extends Activity {
    @Override
    protected void onCreate(Bundle bundle) {
        Window _window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtils.HideStatusBar(_window);
        }
        _window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        StatusBarUtils.HideStatusBar(getWindow());
                    }
                }
            }
        });
        super.onCreate(bundle);
        if(UnityPlayer.currentActivity == null)  {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else {
            finish();
        }
    }
}
