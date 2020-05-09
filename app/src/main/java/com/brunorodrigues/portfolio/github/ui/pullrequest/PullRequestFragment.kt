package com.brunorodrigues.portfolio.github.ui.pullrequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.brunorodrigues.portfolio.github.R
import com.brunorodrigues.portfolio.github.ui.BaseFragment

class PullRequestFragment : BaseFragment(PullRequestFragment::class.toString()) {

    companion object {
        fun newInstance() = PullRequestFragment()
    }

    private lateinit var viewModel: PullRequestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.pull_request_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PullRequestViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
