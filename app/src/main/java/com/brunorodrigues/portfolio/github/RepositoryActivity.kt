package com.brunorodrigues.portfolio.github

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.brunorodrigues.portfolio.github.ui.repository.RepositoryFragment

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
                .replace(R.id.container, RepositoryFragment.newInstance())
                .commitNow()
        }
    }
}
