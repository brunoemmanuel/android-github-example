package com.brunorodrigues.portfolio.github.ui.repository

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.brunorodrigues.portfolio.github.R
import com.brunorodrigues.portfolio.github.ui.BaseFragment

class RepositoryFragment : BaseFragment(RepositoryFragment::class.toString()) {

    companion object {
        fun newInstance() = RepositoryFragment()
    }

    private lateinit var viewModel: RepositoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.repository_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RepositoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
