package com.brunorodrigues.portfolio.github.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.brunorodrigues.portfolio.github.api.Client
import com.brunorodrigues.portfolio.github.ui.pullrequest.PullRequestViewModel
import com.brunorodrigues.portfolio.github.ui.repository.RepositoryViewModel

class DefaultViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if(modelClass == RepositoryViewModel::class.java) {
            RepositoryViewModel(Client()) as T
        } else {
            PullRequestViewModel(Client()) as T
        }
    }
}