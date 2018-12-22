package tk.zwander.librootjavatest;

import android.hardware.input.InputManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import eu.chainfire.librootjava.RootIPC;
import eu.chainfire.librootjava.RootJava;

import java.lang.reflect.InvocationTargetException;

public class RootTest {
    private static InputManager inputManager;

    public static void main(String[] args) {
        try {
            inputManager = (InputManager) InputManager.class.getMethod("getInstance").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        RootJava.restoreOriginalLdLibraryPath();

        IBinder ipc = new RootAidl.Stub() {
            @Override
            public void turnOffScreen() {
                sendKeyEvent(KeyEvent.KEYCODE_POWER, null);
            }
        };

        try {
            new RootIPC(BuildConfig.APPLICATION_ID, ipc, 0, 30 * 1000, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendKeyEvent(int code, String flag) {
        boolean longPress = flag != null && flag.contains("longpress");
        boolean repeat = flag != null && flag.contains("repeat");

        long now = SystemClock.uptimeMillis();

        try {
            if (longPress) {
                //TODO: find a way to make this work
            } else {
                injectKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, code, 0));
                injectKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_UP, code, 0));
                if (repeat) {
                    Thread.sleep(50);
                    injectKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, code, 0));
                    injectKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_UP, code, 0));
                }
            }
        } catch (Exception ignored) {}
    }

    private static void injectKeyEvent(KeyEvent event) {
        try {
            InputManager.class.getMethod("injectInputEvent", InputEvent.class, int.class)
                    .invoke(inputManager, event, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
