package com.brunorodrigues.portfolio.github

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.brunorodrigues.portfolio.github.api.Client
import com.brunorodrigues.portfolio.github.ui.repository.RepositoryFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RepositoryActivity : AppCompatActivity() {



    companion object {
        fun newIntent(context: Context) : Intent {
            return Intent(context, RepositoryActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.repository_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RepositoryFragment.newInstance(), RepositoryFragment::class.toString())
                .commitNow()
        }
    }
}
