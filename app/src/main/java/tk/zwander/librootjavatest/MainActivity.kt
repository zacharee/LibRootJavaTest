package tk.zwander.librootjavatest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.topjohnwu.superuser.Shell
import eu.chainfire.librootjava.RootIPCReceiver
import eu.chainfire.librootjava.RootJava
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val shell by lazy { Shell.newInstance("su") }

    private var aidl: RootAidl? = null

    private val receiver = object : RootIPCReceiver<RootAidl>(null, 0) {
        override fun onConnect(ipc: RootAidl?) {
            aidl = ipc
        }

        override fun onDisconnect(ipc: RootAidl?) {
            aidl = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Shell.rootAccess()) {
            shell.newJob().add(
                *RootJava.getLaunchScript(
                    this,
                    RootTest::class.java,
                    null,
                    null,
                    null,
                    BuildConfig.APPLICATION_ID + ":root"
                ).toTypedArray()
            ).submit()
        }

        receiver.setContext(this)

        test_power.setOnClickListener {
            aidl?.turnOffScreen()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        receiver.release()
    }
}
