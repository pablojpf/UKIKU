package knf.kuma.tv.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import knf.kuma.backup.BUUtils
import knf.kuma.commons.safeShow
import knf.kuma.directory.DirectoryService
import knf.kuma.jobscheduler.DirUpdateJob
import knf.kuma.jobscheduler.RecentsJob
import knf.kuma.recents.RecentsNotReceiver
import knf.kuma.tv.TVBaseActivity
import knf.kuma.tv.TVServersFactory
import knf.kuma.updater.UpdateActivity
import knf.kuma.updater.UpdateChecker

class TVMain : TVBaseActivity(), TVServersFactory.ServersInterface, UpdateChecker.CheckListener {

    private var fragment: TVMainFragment? = null
    private var serversFactory: TVServersFactory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragment = TVMainFragment.get()
        addFragment(fragment!!)
        DirectoryService.run(this)
        RecentsJob.schedule(this)
        DirUpdateJob.schedule(this)
        RecentsNotReceiver.removeAll(this)
        UpdateChecker.check(this, this)
        Answers.getInstance().logCustom(CustomEvent("TV UI"))
    }

    override fun onNeedUpdate(o_code: String, n_code: String) {
        runOnUiThread {
            MaterialDialog(this@TVMain).safeShow {
                title(text = "Actualización")
                message(text = "Parece que la versión $n_code está disponible, ¿Quieres actualizar?")
                positiveButton(text = "si") {
                    UpdateActivity.start(this@TVMain)
                }
                negativeButton(text = "despues")
            }
        }
    }

    override fun onReady(serversFactory: TVServersFactory) {
        this.serversFactory = serversFactory
    }

    override fun onFinish(started: Boolean, success: Boolean) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (data != null)
                if (requestCode == BUUtils.LOGIN_CODE) {
                    if (resultCode == Activity.RESULT_OK) {
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                        BUUtils.type = BUUtils.BUType.DRIVE
                        BUUtils.setDriveClient()
                    } else if (fragment != null) {
                        fragment!!.onLogin()
                    }
                } else if (resultCode == Activity.RESULT_OK) {
                    val bundle = data.extras
                    if (bundle != null)
                        if (bundle.getBoolean("is_video_server", false))
                            serversFactory!!.analyzeOption(bundle.getInt("position", 0))
                        else
                            serversFactory!!.analyzeServer(bundle.getInt("position", 0))
                } else if (resultCode == Activity.RESULT_CANCELED && data.extras!!.getBoolean("is_video_server", false))
                    serversFactory!!.showServerList()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
