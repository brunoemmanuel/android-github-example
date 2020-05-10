package com.brunorodrigues.portfolio.github

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.brunorodrigues.portfolio.github.ui.pullrequest.PullRequestFragment

class PullRequestActivity : AppCompatActivity() {

    companion object {
        const val USER_NAME_KEY: String = "__user_name_key__"
        const val REPOSITORY_NAME_KEY = "__repository_name_key__"
        
        fun newIntent(context: Context?) : Intent {
            return Intent(context, PullRequestActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pull_request_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            intent.extras?.let { PullRequestFragment.newInstance(it) }?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, it)
                    .commitNow()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
