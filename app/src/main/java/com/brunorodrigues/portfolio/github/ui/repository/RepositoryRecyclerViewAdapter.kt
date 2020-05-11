package com.brunorodrigues.portfolio.github.ui.repository

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brunorodrigues.portfolio.github.R
import com.brunorodrigues.portfolio.github.data.Repository
import com.squareup.picasso.Picasso

class RepositoryRecyclerViewAdapter(context: Context?) : RecyclerView.Adapter<RepositoryRecyclerViewAdapter.ViewHolder>() {
    var items = ArrayList<Repository>()
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    lateinit var click: (Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.repository_card_item, parent, false)
        return ViewHolder(view).listen { pos, type ->
            if(click != null) click(pos)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = items[position]

        if(repository != null) {
            val user = repository.owner
            if(user != null && user.avatarUrl != "") {
                Picasso.get().load(user.avatarUrl).into(holder.userImage)
                holder.userName.text = user.login
            }

            holder.repositoryName.text = repository.name
            holder.repositoryDescription.text = repository.description
            holder.forkCount.text = repository.forksCount.toString()
            holder.starCount.text = repository.stargazersCount.toString()
        }
    }

    fun setItemClick(click: (Int) -> Unit) {
        this.click = click
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val repositoryName: TextView = itemView.findViewById(R.id.repositoryName)
        val repositoryDescription: TextView = itemView.findViewById(R.id.repositoryDescription)
        val forkCount: TextView = itemView.findViewById(R.id.forkCount)
        val starCount: TextView = itemView.findViewById(R.id.starCount)
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