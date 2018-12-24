import android.app.Application
import com.zeyad.rxredux.core.BuildConfig
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
//            Timber.plant(CrashReportingTree())
        }
    }
}
