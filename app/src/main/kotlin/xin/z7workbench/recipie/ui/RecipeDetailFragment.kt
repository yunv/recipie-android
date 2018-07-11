package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.layout_recipe_detail.view.*
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare
import xin.z7workbench.recipie.entity.Comment

class RecipeDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_recipe_detail, container, false)

        val model = ViewModelProviders.of(requireActivity())[RecipeViewModel::class.java]
        model.recipe.observe(this, Observer {
            it ?: return@Observer
            view.apply {
                Glide.with(this).load(R.drawable.login_bg).into(bg)
                play.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_recipeDetailFragment_to_recipeDisplayFragment))
                comment.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_recipeDetailFragment_to_recipeCommentFragment))
                name.text = it.title
                description.text = it.description
                like.visibility = View.GONE
                favorite.visibility = View.GONE
                comments.adapter = CommentAdapter(it.comment_set ?: listOf())

                author.text = it.create_by?.nickname ?: "Unknown"
                likes.text = "${it.like_count}${context.getString(R.string.like_tail)} ${it.read_count}${context.getString(R.string.read_tail)}"

                like.setOnClickListener {
                    RecipieRetrofit.recipe.likeRecipe(it.id).prepare(context).subscribe { context.toast("已点赞") }
                    like.visibility = View.GONE
                    not_like.visibility = View.VISIBLE
                }
                not_like.setOnClickListener {
                    RecipieRetrofit.recipe.unlikeRecipe(it.id).prepare(context).subscribe { context.toast("已取消点赞") }
                    not_like.visibility = View.GONE
                    like.visibility = View.VISIBLE
                }
                favorite.setOnClickListener {
                    RecipieRetrofit.recipe.collectRecipe(it.id).prepare(context).subscribe { context.toast("已收藏") }
                    favorite.visibility = View.GONE
                    not_favorite.visibility = View.VISIBLE
                }
                not_favorite.setOnClickListener {
                    RecipieRetrofit.recipe.uncollectRecipe(it.id).prepare(context).subscribe { context.toast("已取消收藏") }
                    not_favorite.visibility = View.GONE
                    favorite.visibility = View.VISIBLE
                }
            }
        })
        return view
    }

    class CommentAdapter(var list: List<Comment> = listOf()) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                CommentViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_comment, parent, false))

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
            holder.v.apply {
                comment_author.text = list[position].user?.nickname ?: "Unknown"
                Glide.with(this).load(list[position].user?.avatar).apply(RequestOptions.circleCropTransform()).into(author_avatar)
                comment_content.text = list[position].content
                comment_like_count.text = list[position].like_count.toString()
                
                comment_like.setOnClickListener {
                    // TODO API
                    comment_like.visibility = View.GONE
                    comment_not_like.visibility = View.VISIBLE
                }
                comment_not_like.setOnClickListener {
                    // TODO API
                    comment_not_like.visibility = View.GONE
                    comment_like.visibility = View.VISIBLE
                }
            }
        }

        class CommentViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }
}