package com.brunorodrigues.portfolio.github.ui.pullrequest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brunorodrigues.portfolio.github.R
import com.brunorodrigues.portfolio.github.data.PullRequest
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class PullRequestRecyclerViewAdapter(context: Context?) : RecyclerView.Adapter<PullRequestRecyclerViewAdapter.ViewHolder>() {
    var items = ArrayList<PullRequest>()
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    lateinit var click: (Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.pull_request_card_item, parent, false)
        return ViewHolder(view).listen { pos, type ->
            if(click != null) click(pos)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pullRequest = items[position]

        if(pullRequest != null) {
            val user = pullRequest.user
            if(user != null) {
                Picasso.get().load(user.avatarUrl).into(holder.userImage)
                holder.userName.text = user.login
            }

            holder.pullRequestTitle.text = pullRequest.title

            if(pullRequest.body != "") {
                holder.pullRequestBody.text = pullRequest.body
                holder.pullRequestBody.visibility = View.VISIBLE
            } else {
                holder.pullRequestBody.text = ""
                holder.pullRequestBody.visibility = View.GONE
            }

            if(pullRequest.createdAt != null) {
                val df = SimpleDateFormat("dd/MM/yyyy")
                val releasedText = df.format(pullRequest.createdAt)
                holder.createdAt.text = releasedText
            } else {
                holder.createdAt.text = ""
            }
        }
    }

    fun setItemClick(click: (Int) -> Unit) {
        this.click = click
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pullRequestTitle: TextView = itemView.findViewById(R.id.pullRequestTitle)
        val pullRequestBody: TextView = itemView.findViewById(R.id.pullRequestBody)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)

        fun listen(event: (position: Int, type: Int) -> Unit): ViewHolder {
            itemView.setOnClickListener {
                event.invoke(adapterPosition, itemViewType)
            }

            return this
        }
    }
}