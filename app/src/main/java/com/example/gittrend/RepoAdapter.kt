package com.example.gittrend

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.gittrend.database.Repository

/**
 * Created by Shivansh ON 03/09/20.
 */
class RepoAdapter(private val repositoryList: List<Repository>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var expandedItemPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        RepositoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_repo_item, parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val repoViewHolder = holder as RepositoryViewHolder
        val repository = repositoryList[position]

        repoViewHolder.avatar.load(repository.avatar) {
            placeholder(R.drawable.circle)
            transformations(CircleCropTransformation())
        }
        repoViewHolder.authorName.text = repository.author
        repoViewHolder.repoName.text = repository.name

        repoViewHolder.description.text = repository.description
        repoViewHolder.language.text = repository.language

        if (repository.languageColor != null) {
            try {
                ((repoViewHolder.langColorIndicator.background) as GradientDrawable).setColor(
                    Color.parseColor(
                        repository.languageColor
                    )
                )
            } catch (ex: Exception) {
                ((repoViewHolder.langColorIndicator.background) as GradientDrawable)
                    .setColor(Color.parseColor("#E8E8E8"))
            }
        }

        repoViewHolder.stars.text = repository.stars.toString()
        repoViewHolder.forks.text = repository.forks.toString()

        if (repository.isExpanded) {
            repoViewHolder.extraContent.visibility = View.VISIBLE
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                repoViewHolder.itemView.elevation = 5F
        } else {
            repoViewHolder.extraContent.visibility = View.GONE
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                repoViewHolder.itemView.elevation = 0F
        }
    }

    override fun getItemCount(): Int = repositoryList.size

    inner class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.image_avatar)
        val authorName: TextView = itemView.findViewById(R.id.text_author)
        val repoName: TextView = itemView.findViewById(R.id.text_repo_name)
        val extraContent: ConstraintLayout = itemView.findViewById(R.id.extra_contents)

        val description: TextView = itemView.findViewById(R.id.text_description)
        val langColorIndicator: View = itemView.findViewById(R.id.language_color)
        val language: TextView = itemView.findViewById(R.id.text_language)
        val stars: TextView = itemView.findViewById(R.id.text_stars)
        val forks: TextView = itemView.findViewById(R.id.text_forks)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != -1)
                    adapterPosition.let { currentPosition ->
                        repositoryList[currentPosition].isExpanded = !repositoryList[currentPosition].isExpanded
                        notifyItemChanged(currentPosition)
                        if (currentPosition != expandedItemPosition) {
                            expandedItemPosition?.let {
                                repositoryList[it].isExpanded = false
                                notifyItemChanged(it)
                            }
                            expandedItemPosition = currentPosition
                        } else expandedItemPosition = null
                    }
            }
        }
    }
}